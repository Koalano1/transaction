package com.demo.transaction.transaction;

import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.service.impl.TransactionCommandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static com.demo.transaction.model.enums.TransactionType.DEPOSIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionCommandServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionCommandServiceImpl service;

    private TransactionRequestDto request;
    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        sender = new Account();
        sender.setId("1");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setStatus(AccountStatus.ACTIVE);

        receiver = new Account();
        receiver.setId("2");
        receiver.setBalance(new BigDecimal("500.00"));
        receiver.setStatus(AccountStatus.ACTIVE);

        request = new TransactionRequestDto();
        request.setSenderAccountId(sender.getId());
        request.setReceiverAccountId(receiver.getId());
        request.setAmount(new BigDecimal("200.00"));
        request.setTransactionType(DEPOSIT);
    }

    @Test
    void createTransaction_success() {
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        service.createTransaction(request);

        assertEquals(new BigDecimal("800.00"), sender.getBalance());
        assertEquals(new BigDecimal("700.00"), receiver.getBalance());
        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void createTransaction_senderNotFound_throwsException() {
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UnprocessableEntityException.class, () -> {
            service.createTransaction(request);
        });

        assertTrue(exception.getMessage().contains("Sender account with ID " + sender.getId() + " not found"));
    }

    @Test
    void createTransaction_receiverNotFound_throwsException() {
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UnprocessableEntityException.class, () -> {
            service.createTransaction(request);
        });

        assertTrue(exception.getMessage().contains("Receiver account with ID " + receiver.getId() + " not found"));
    }

    @Test
    void createTransaction_insufficientFunds_throwsException() {
        request.setAmount(new BigDecimal("1200.00"));

        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        Exception exception = assertThrows(UnprocessableEntityException.class, () -> {
            service.createTransaction(request);
        });

        assertTrue(exception.getMessage().contains("Insufficient funds in sender's account."));
    }

    @Test
    void createTransaction_inactiveAccount_throwsException() {
        sender.setStatus(AccountStatus.INACTIVE);

        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        Exception exception = assertThrows(UnprocessableEntityException.class, () -> {
            service.createTransaction(request);
        });

        assertTrue(exception.getMessage().contains("Both accounts must be active to perform the transaction."));
    }
}
