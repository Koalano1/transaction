package com.demo.transaction.dto;

import com.demo.transaction.model.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionRequestDto {

    private String senderAccountId;

    private String receiverAccountId;

    private BigDecimal amount;

    private TransactionType transactionType;

}
