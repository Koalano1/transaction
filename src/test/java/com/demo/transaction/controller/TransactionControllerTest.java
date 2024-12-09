//package com.demo.transaction.controller;
//
//import com.demo.transaction.dto.RecordResponseTransactionDto;
//import com.demo.transaction.dto.TransactionRequestDto;
//import com.demo.transaction.dto.TransactionResponseDto;
//import com.demo.transaction.model.entities.Transaction;
//import com.demo.transaction.model.enums.TransactionType;
//import com.demo.transaction.service.TransactionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//public class TransactionControllerTest {
//    @InjectMocks
//    private TransactionController transactionController;
//
//    @Mock
//    private TransactionService transactionService;
//
//    private TransactionRequestDto transactionRequestDto;
//    private Transaction transaction;
//    private TransactionResponseDto transactionResponseDto;
//    private RecordResponseTransactionDto recordResponseTransactionDto;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Sample data for transaction request and response
//        transactionRequestDto = new TransactionRequestDto("sender123", "receiver123", BigDecimal.valueOf(1000), TransactionType.DEPOSIT);
////        transaction = new Transaction(1L, "sender123", "receiver123", BigDecimal.valueOf(1000), "TRANSFER");
////        transactionResponseDto = new TransactionResponseDto(1L, "sender123", "receiver123", BigDecimal.valueOf(1000), "TRANSFER");
////        recordResponseTransactionDto = new RecordResponseTransactionDto("sender123", "receiver123", BigDecimal.valueOf(1000), "TRANSFER");
//    }
//
//    @Test
//    public void testCreateTransaction() {
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(transaction);
//
//        when(transactionService.createTransaction(transactionRequestDto)).thenReturn(transactions);
//
//        List<TransactionResponseDto> result = transactionController.createTransaction(transactionRequestDto);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(transactionResponseDto.getId(), result.get(0).getId());
//        assertEquals(transactionResponseDto.getSenderAccountNumber(), result.get(0).getSenderAccountNumber());
//        assertEquals(transactionResponseDto.getReceiverAccountNumber(), result.get(0).getReceiverAccountNumber());
//        assertEquals(transactionResponseDto.getAmount(), result.get(0).getAmount());
//        assertEquals(transactionResponseDto.getTransactionType(), result.get(0).getTransactionType());
//
//        // Verify that the service method was called once
//        verify(transactionService, times(1)).createTransaction(transactionRequestDto);
//    }
//
//    @Test
//    public void testGetTransactionsByUserId() {
//        List<RecordResponseTransactionDto> transactionDtos = new ArrayList<>();
//        transactionDtos.add(recordResponseTransactionDto);
//
//        when(transactionService.getAllUsersByUserId("sender123")).thenReturn(transactionDtos);
//
//        List<RecordResponseTransactionDto> result = transactionController.getTransactionsByUserId("sender123");
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(recordResponseTransactionDto.getSenderNewBalance(), result.get(0));
//        assertEquals(recordResponseTransactionDto.getReceiverNewBalance(), result.get(0));
//        assertEquals(recordResponseTransactionDto.getAmount(), result.get(0).getAmount());
//        assertEquals(recordResponseTransactionDto.getTransactionId(), result.get(0).getTransactionId());
//
//        verify(transactionService, times(1)).getAllUsersByUserId("sender123");
//    }
//
//
//}
