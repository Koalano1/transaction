package com.demo.transaction.controller;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.dto.TransactionResponseDto;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.TransactionType;
import com.demo.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public List<TransactionResponseDto> createTransaction(@RequestBody TransactionRequestDto request) {
        List<Transaction> transactions = transactionService.createTransaction(request);
        List<TransactionResponseDto> transactionResponses = new ArrayList<>();
        transactions.forEach(transaction -> {
            TransactionResponseDto transactionResponse = TransactionResponseDto.builder()
                    .id(transaction.getId())
                    .senderAccountNumber(transaction.getSenderId())
                    .receiverAccountNumber(transaction.getReceiverId())
                    .amount(transaction.getAmount())
                    .transactionType(transaction.getType())
                    .build();
            transactionResponses.add(transactionResponse);
        });
        return transactionResponses;
    }

    @GetMapping("users/{userId}")
    public List<RecordResponseTransactionDto> getTransactionsByUserId(@PathVariable String userId) {
        return transactionService.getAllUsersByUserId(userId);
    }

    @GetMapping
    public Page<Transaction> getTransactions(@RequestParam String userId,
                                             @RequestParam(required = false) BigDecimal amount,
                                             @RequestParam(required = false) TransactionType type,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return transactionService.getTransactions(userId, amount, type, page, size);
    }

}
