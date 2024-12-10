package com.demo.transaction.transaction;

import com.demo.transaction.controller.query.TransactionQueryController;
import com.demo.transaction.dto.RecordResponseTransactionDto;
import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.TransactionType;
import com.demo.transaction.service.impl.TransactionQueryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TransactionQueryControllerTest {

    @Mock
    private TransactionQueryServiceImpl transactionQueryService;

    @InjectMocks
    private TransactionQueryController transactionQueryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionQueryController).build();
    }

    @Test
    void getTransactionsByUserId_ShouldReturnTransactions() throws Exception {
        String userId = "user123";
        List<RecordResponseTransactionDto> transactions = Collections.singletonList(new RecordResponseTransactionDto());

        when(transactionQueryService.getAllUsersByUserId(userId)).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(transactionQueryService).getAllUsersByUserId(userId);
    }

    @Test
    void getTransactions_ShouldReturnFilteredTransactions() throws Exception {
        String userId = "user123";
        BigDecimal amount = new BigDecimal("100.00");
        TransactionType type = TransactionType.WITHDRAWAL;
        int page = 1;
        int size = 10;

        Page<Transaction> pageOfTransactions = new PageImpl<>(Collections.singletonList(new Transaction()));

        when(transactionQueryService.getTransactions(userId, amount, type, page - 1, size)).thenReturn(pageOfTransactions);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("userId", userId)
                        .param("amount", amount.toString())
                        .param("type", type.toString())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").exists());

        verify(transactionQueryService).getTransactions(userId, amount, type, page - 1, size);
    }
}

