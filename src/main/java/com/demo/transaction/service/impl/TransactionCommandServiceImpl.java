package com.demo.transaction.service.impl;

import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.exception.UnprocessableEntityException;
import com.demo.transaction.model.entities.Account;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.AccountStatus;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import com.demo.transaction.service.TransactionCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
   // private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createTransaction(TransactionRequestDto request) {
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
        transactionRepository.save(transaction);
    }

}
