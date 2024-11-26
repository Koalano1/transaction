package com.demo.transaction.service.impl;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;


    @Override
    @Transactional
    public List<Transaction> listTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Account sender = accountRepository.findById(transaction.getSenderAccount().getId())
                .orElseThrow(() -> new UnprocessableEntityException("Sender account with ID " + transaction.getSenderAccount().getId() + " not found"));

        Account receiver = accountRepository.findById(transaction.getReceiverAccount().getId())
                .orElseThrow(() -> new UnprocessableEntityException("Receiver account with ID " + transaction.getReceiverAccount().getId() + " not found"));

        if (sender.getStatus() != AccountStatus.ACTIVE || receiver.getStatus() != AccountStatus.ACTIVE) {
            throw new UnprocessableEntityException("Both accounts must be active to perform the transaction.");
        }

        if (transaction.getAmount().compareTo(sender.getBalance()) > 0) {
            throw new UnprocessableEntityException("Insufficient funds in sender's account.");
        }

        sender.setBalance(sender.getBalance().subtract(transaction.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transaction.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transactions = new Transaction();
        transactions.setSenderAccount(sender);
        transactions.setReceiverAccount(receiver);
        transactions.setAmount(transaction.getAmount());
        transactions.setCreatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionRepository.save(savedTransaction);
    }
}
