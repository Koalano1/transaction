package com.demo.transaction.service;

import com.demo.transaction.dto.DistributedTransaction;
import com.demo.transaction.model.entities.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> listTransactionsByUserId(String userId);


    Transaction createTransaction(Transaction tr);
}
