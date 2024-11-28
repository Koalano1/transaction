package com.demo.transaction.service;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.model.entities.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> createTransaction(TransactionRequestDto request);

    List<RecordResponseTransactionDto> getAllUsersByUserId(String userId);

}
