package com.demo.transaction.service;

import com.demo.transaction.dto.TransactionRequestDto;

public interface TransactionCommandService {
    void createTransaction(TransactionRequestDto request);

}
