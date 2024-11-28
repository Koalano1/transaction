package com.demo.transaction.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecordResponseTransactionDto {

    private String transactionId;

    private String userId;

    private String role;

    private BigDecimal amount;

    private BigDecimal senderOldBalance;

    private BigDecimal senderNewBalance;

    private BigDecimal receiverNewBalance;
}
