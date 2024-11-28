package com.demo.transaction.dto;

import com.demo.transaction.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionResponseDto {

    private String id;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private BigDecimal amount;

    private String transactionId;

    private TransactionType transactionType;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private BigDecimal senderOldBalance;

    private BigDecimal senderNewBalance;

    private BigDecimal receiverNewBalance;

}
