package com.demo.transaction.controller.command;

import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.service.impl.TransactionCommandServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
@ConditionalOnProperty(name = "app.write.enabled", havingValue = "true")
public class TransactionCommandController {

    private final TransactionCommandServiceImpl transactionCommandService;

    @PostMapping
    public void createTransaction(@RequestBody TransactionRequestDto request) {
        transactionCommandService.createTransaction(request);
    }

}
