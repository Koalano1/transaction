package com.demo.transaction.transaction;

import com.demo.transaction.controller.command.TransactionCommandController;
import com.demo.transaction.dto.TransactionRequestDto;
import com.demo.transaction.model.enums.TransactionType;
import com.demo.transaction.service.impl.TransactionCommandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransactionCommandControllerTest {

    @Mock
    private TransactionCommandServiceImpl transactionCommandService;

    @InjectMocks
    private TransactionCommandController transactionCommandController;

    private MockMvc mockMvc;

    private TransactionRequestDto transactionRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionCommandController).build();

        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setAmount(BigDecimal.valueOf(1000));
        transactionRequestDto.setTransactionType(TransactionType.WITHDRAWAL);
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        doNothing().when(transactionCommandService).createTransaction(transactionRequestDto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType("application/json")
                        .content("{\"amount\":1000, \"fromAccount\":\"123456789\", \"toAccount\":\"987654321\", \"transactionType\":\"TRANSFER\"}"))
                .andExpect(status().isOk());  // Should return 200 OK

        verify(transactionCommandService, times(1)).createTransaction(transactionRequestDto);
    }

    @Test
    void shouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        doThrow(new RuntimeException("Transaction failed")).when(transactionCommandService).createTransaction(transactionRequestDto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType("application/json")
                        .content("{\"amount\":1000, \"fromAccount\":\"123456789\", \"toAccount\":\"987654321\", \"transactionType\":\"TRANSFER\"}"))
                .andExpect(status().isInternalServerError());

        verify(transactionCommandService, times(1)).createTransaction(transactionRequestDto);
    }
}
