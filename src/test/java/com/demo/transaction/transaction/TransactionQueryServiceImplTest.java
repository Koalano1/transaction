package com.demo.transaction.transaction;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.TransactionType;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.repository.filter.TransactionFilter;
import com.demo.transaction.repository.specification.TransactionSpecificationCreator;
import com.demo.transaction.service.impl.TransactionQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionQueryServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionQueryServiceImpl service;

    private Account sender;
    private Account receiver;
    private Transaction transaction;
    private RecordResponseTransactionDto responseDto;

    @BeforeEach
    void setUp() {
        // Setup mock data
        sender = new Account();
        sender.setId("1");
        sender.setUsername("senderUser");
        sender.setBalance(new BigDecimal("1000.00"));

        receiver = new Account();
        receiver.setId("2");
        receiver.setUsername("receiverUser");
        receiver.setBalance(new BigDecimal("500.00"));

        transaction = new Transaction();
        transaction.setId("tx123");
        transaction.setSenderId(sender.getId());
        transaction.setReceiverId(receiver.getId());
        transaction.setAmount(new BigDecimal("200.00"));

        responseDto = new RecordResponseTransactionDto();
    }

    @Test
    void getAllUsersByUserId_success() {
        // Arrange
        String username = "senderUser";
        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(sender));
        when(transactionRepository.findBySenderId(sender.getId())).thenReturn(List.of(transaction));
        when(transactionRepository.findByReceiverId(sender.getId())).thenReturn(new ArrayList<>());

        // Act
        List<RecordResponseTransactionDto> result = service.getAllUsersByUserId(username);

        // Assert
        assertEquals(1, result.size());
        assertEquals(transaction.getId(), result.get(0).getTransactionId());
    }

    @Test
    void getAllUsersByUserId_userNotFound_throwsException() {
        // Arrange
        String username = "nonExistingUser";
        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UnprocessableEntityException.class, () -> {
            service.getAllUsersByUserId(username);
        });
        assertTrue(exception.getMessage().contains("User with username " + username + " not found"));
    }

    @Test
    void getTransactions_success() {
        // Arrange
        String userId = sender.getId();
        TransactionFilter filter = TransactionFilter.builder().userId(userId).build();
        when(accountRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(transactionRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(Page.empty());

        // Act
        Page<Transaction> result = service.getTransactions(userId, new BigDecimal("100.00"), TransactionType.WITHDRAWAL, 0, 10);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void getTransactions_withAmountFilter() {
        // Arrange
        String userId = sender.getId();
        BigDecimal amount = new BigDecimal("100.00");
        TransactionFilter filter = TransactionFilter.builder().userId(userId).build();
        when(accountRepository.findById(userId)).thenReturn(Optional.of(sender));

        Specification<Transaction> specification = TransactionSpecificationCreator.from(filter);
        when(transactionRepository.findAll(specification, PageRequest.of(0, 10)))
                .thenReturn(Page.empty());

        // Act
        Page<Transaction> result = service.getTransactions(userId, amount, TransactionType.WITHDRAWAL, 0, 10);

        // Assert
        assertNotNull(result);
        verify(transactionRepository, times(1)).findAll(specification, PageRequest.of(0, 10));
    }

    @Test
    void getTransactions_noTransactionsFound() {
        // Arrange
        String userId = sender.getId();
        when(accountRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(transactionRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(Page.empty());

        // Act
        Page<Transaction> result = service.getTransactions(userId, null, null, 0, 10);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAccountById_accountNotFound_throwsException() {
        // Arrange
        when(accountRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getAllUsersByUserId("1");
        });

        assertTrue(exception.getMessage().contains("Account not found with id: 1"));
    }
}

