package com.demo.transaction.service;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        sender = Account.builder()
                .id("senderId")
                .username("sender")
                .email("sender@example.com")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        receiver = Account.builder()
                .id("receiverId")
                .username("receiver")
                .email("receiver@example.com")
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateTransaction_Success() {
        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .senderAccountId(sender.getId())
                .receiverAccountId(receiver.getId())
                .amount(BigDecimal.valueOf(100))
                .build();

        // Mocking repository methods
        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(
                Transaction.builder()
                        .id("transactionId")
                        .senderId(sender.getId())
                        .receiverId(receiver.getId())
                        .amount(requestDto.getAmount())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        List<Transaction> result = transactionServiceImpl.createTransaction(requestDto);

        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
        verify(transactionRepository).save(any(Transaction.class));

        assert(result.size() == 1);
        assert(result.get(0).getSenderId().equals(sender.getId()));
        assert(result.get(0).getReceiverId().equals(receiver.getId()));
    }

    @Test
    void testCreateTransaction_InsufficientFunds() {
        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .senderAccountId(sender.getId())
                .receiverAccountId(receiver.getId())
                .amount(BigDecimal.valueOf(2000))
                .build();

        when(accountRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        UnprocessableEntityException exception = org.junit.jupiter.api.Assertions.assertThrows(
                UnprocessableEntityException.class,
                () -> transactionServiceImpl.createTransaction(requestDto)
        );

        assert(exception.getMessage().contains("Insufficient funds in sender's account."));
    }

    @Test
    void testCreateTransaction_AccountNotFound() {
        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .senderAccountId("invalidSenderId")
                .receiverAccountId(receiver.getId())
                .amount(BigDecimal.valueOf(100))
                .build();

        // Mocking repository methods
        when(accountRepository.findById(any(String.class))).thenReturn(Optional.empty());

        // Execute the service method and expect an exception
        UnprocessableEntityException exception = org.junit.jupiter.api.Assertions.assertThrows(
                UnprocessableEntityException.class,
                () -> transactionServiceImpl.createTransaction(requestDto)
        );

        assert(exception.getMessage().contains("Sender account with ID invalidSenderId not found"));
    }

    @Test
    void testGetAllUsersByUserId_Success() {

        String username = "sender";
        String userId = sender.getId();

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(sender));
        when(transactionRepository.findBySenderId(userId)).thenReturn(List.of(
                Transaction.builder().senderId(sender.getId()).receiverId(receiver.getId()).amount(BigDecimal.valueOf(100)).build()
        ));
        when(transactionRepository.findByReceiverId(userId)).thenReturn(List.of());

        List<RecordResponseTransactionDto> transactions = transactionServiceImpl.getAllUsersByUserId(username);

        assert(transactions.size() == 1);
        assert(transactions.get(0).getAmount().equals(BigDecimal.valueOf(100)));
    }

    @Test
    void testGetAllUsersByUserId_UserNotFound() {
        String username = "nonExistingUser";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        UnprocessableEntityException exception = org.junit.jupiter.api.Assertions.assertThrows(
                UnprocessableEntityException.class,
                () -> transactionServiceImpl.getAllUsersByUserId(username)
        );

        assert(exception.getMessage().contains("User with username nonExistingUser not found"));
    }
}
