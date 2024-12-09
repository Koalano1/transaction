package com.demo.transaction.service;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.TransactionType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionQueryService {

    List<RecordResponseTransactionDto> getAllUsersByUserId(String userId);

    Page<Transaction> getTransactions(String userId, BigDecimal amount, TransactionType type, int page, int size);

}
