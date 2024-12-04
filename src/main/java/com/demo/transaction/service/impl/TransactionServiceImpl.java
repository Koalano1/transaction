package com.demo.transaction.service.impl;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.model.enums.TransactionType;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.repository.filter.TransactionFilter;
import com.demo.transaction.repository.specification.TransactionSpecification;
import com.demo.transaction.repository.specification.TransactionSpecificationCreator;
import com.demo.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
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
    public List<RecordResponseTransactionDto> getAllUsersByUserId(String username) {
        String userId = getUserIdByUserId(username);

        List<Transaction> transactions = getTransactionByUserId(userId);

        return mapTransactionToRecordResponse(transactions, userId);
    }

    @Override
    public Page<Transaction> getTransactions(String userId,
                                             BigDecimal amount,
                                             TransactionType type,
                                             int page,
                                             int size) {
        TransactionFilter filter = TransactionFilter.builder()
                .userId(userId)
                .type(type)
                .build();

        Specification<Transaction> specification = TransactionSpecificationCreator.from(filter);

        if("sender".equalsIgnoreCase(filter.getUserId())) {
            specification = specification.and(TransactionSpecification.isSender(userId));
        } else if("receiver".equalsIgnoreCase(filter.getUserId())) {
            specification = specification.and(TransactionSpecification.isReceiver(userId));
        } else {
            specification = specification.and(TransactionSpecification.hasUserId(userId));
        }
        if(amount != null) {
            specification = specification.and(TransactionSpecification.hasAmount(amount));
        }

        return transactionRepository.findAll(specification, PageRequest.of(page,size));
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
