package com.demo.transaction;

import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account sender;
    private Account receiver;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new Account();
        sender.setId(1L);
        sender.setBalance(BigDecimal.valueOf(1000));
        sender.setStatus(AccountStatus.ACTIVE);

        receiver = new Account();
        receiver.setId(2L);
        receiver.setBalance(BigDecimal.valueOf(500));
        receiver.setStatus(AccountStatus.ACTIVE);

        transaction = new Transaction();
        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);
        transaction.setAmount(BigDecimal.valueOf(200));
        transaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTransaction_Success() {
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transaction);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(800), sender.getBalance());
        assertEquals(BigDecimal.valueOf(700), receiver.getBalance());
        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
    }

    @Test
    void testCreateTransaction_SenderNotFound_ThrowException() {
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UnprocessableEntityException.class, () ->
                transactionService.createTransaction(transaction));

        assertEquals("Receiver account with ID 2 not found", exception.getMessage());
    }

    @Test
    void testCreateTransaction_AccountInactive_ThrowsException() {
        sender.setStatus(AccountStatus.INACTIVE);
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        Exception exception = assertThrows(UnprocessableEntityException.class, () ->
                transactionService.createTransaction(transaction));

        assertEquals("Both accounts must be active to perform the transaction.", exception.getMessage());
    }


}
