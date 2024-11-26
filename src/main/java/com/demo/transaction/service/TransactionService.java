package com.demo.transaction.service;

import com.demo.transaction.model.entities.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> listTransactionsByUserId(Long userId);

    Transaction createTransaction(Transaction tr);
}
