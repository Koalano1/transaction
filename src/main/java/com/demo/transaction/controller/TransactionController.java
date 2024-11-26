package com.demo.transaction.controller;

import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<List<Transaction>> createTransaction(@RequestBody List<Transaction> transaction) {
        List<Transaction> savedTransactions = new ArrayList<>();

        for (Transaction tr : transaction) {
            savedTransactions.add(transactionService.createTransaction(tr));
        }
        return ResponseEntity.ok(savedTransactions);
    }

    @GetMapping("users/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionById(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.listTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }
}
