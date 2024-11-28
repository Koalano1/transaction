package com.demo.transaction.service.impl;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public List<Transaction> createTransaction(TransactionRequestDto request) {
        Account sender = accountRepository.findById(request.getSenderAccountId())
                .orElseThrow(() -> new UnprocessableEntityException("Sender account with ID " +
                        request.getSenderAccountId() + " not found"));

        Account receiver = accountRepository.findById(request.getReceiverAccountId())
                .orElseThrow(() -> new UnprocessableEntityException("Receiver account with ID " +
                        request.getReceiverAccountId() + " not found"));

        if (sender.getStatus() != AccountStatus.ACTIVE || receiver.getStatus() != AccountStatus.ACTIVE) {
            throw new UnprocessableEntityException("Both accounts must be active to perform the transaction.");
        }

        if (request.getAmount().compareTo(sender.getBalance()) > 0) {
            throw new UnprocessableEntityException("Insufficient funds in sender's account.");
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = Transaction.builder()
                .senderId(request.getSenderAccountId())
                .receiverId(request.getReceiverAccountId())
                .amount(request.getAmount())
                .type(request.getTransactionType())
                .createdAt(LocalDateTime.now())
                .build();
        return List.of(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public List<RecordResponseTransactionDto> getAllUsersByUserId(String username) {
        String userId = getUserIdByUserId(username);

        List<Transaction> transactions = getTransactionByUserId(userId);

        return mapTransactionToRecordResponse(transactions, userId);
    }

    private List<RecordResponseTransactionDto> mapTransactionToRecordResponse(List<Transaction> transactions, String userId) {
        return transactions.stream()
                .map(transaction -> {
                    RecordResponseTransactionDto response = new RecordResponseTransactionDto();
                    mapTransactionToResponse(transaction, response, userId);
                    return response;
                })
                .collect(Collectors.toList());
    }

    private void mapTransactionToResponse(Transaction transaction, RecordResponseTransactionDto response, String userId) {
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());

        setRoleAndUserId(transaction, response, userId);

        Account sender = getAccountById(transaction.getSenderId());
        Account receiver = getAccountById(transaction.getReceiverId());

        response.setSenderOldBalance(sender.getBalance());
        response.setSenderOldBalance(receiver.getBalance());

        response.setSenderNewBalance(sender.getBalance());
        response.setReceiverNewBalance(receiver.getBalance());
    }

    private void setRoleAndUserId(Transaction transaction, RecordResponseTransactionDto response, String userId) {
        if (transaction.getSenderId().equals(userId)) {
            response.setUserId(transaction.getSenderId());
            response.setRole("sender");
        } else {
            response.setUserId(transaction.getReceiverId());
            response.setRole("receiver");
        }
    }

    private Account getAccountById(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
    }

    private List<Transaction> getTransactionByUserId(String userId) {
        List<Transaction> sentTransactions = transactionRepository.findBySenderId(userId);
        List<Transaction> receivedTransactions = transactionRepository.findByReceiverId(userId);

        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);

        return allTransactions;
    }

    private String getUserIdByUserId(String username) {
        Account user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UnprocessableEntityException("User with username " + username + " not found"));
        return user.getId();
    }

}
