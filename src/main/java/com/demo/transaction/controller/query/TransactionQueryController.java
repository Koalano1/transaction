package com.demo.transaction.controller.query;

import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.TransactionType;
import com.demo.transaction.service.impl.TransactionQueryServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
//for READ controller
@ConditionalOnProperty(name = "app.write.enabled", havingValue = "false")
public class TransactionQueryController {

    private final TransactionQueryServiceImpl transactionQueryService;

    @GetMapping("users/{userId}")
    public List<RecordResponseTransactionDto> getTransactionsByUserId(@PathVariable String userId) {
        return transactionQueryService.getAllUsersByUserId(userId);
    }

    @GetMapping
    public Page<Transaction> getTransactions(@RequestParam String userId,

                                             @RequestParam(required = false)
                                             BigDecimal amount,

                                             @RequestParam(value = "type",required = false)
                                             @Parameter(description = "Search by type")
                                             TransactionType type,

                                             @RequestParam(value = "page", defaultValue = "1")
                                             int page,

                                             @RequestParam(value = "size", defaultValue = "10")
                                             int size) {
        return transactionQueryService.getTransactions(userId, amount, type, page, size);
    }

}
