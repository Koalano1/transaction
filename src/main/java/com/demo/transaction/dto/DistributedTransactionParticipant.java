package com.demo.transaction.dto;

import com.demo.transaction.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Data
public class DistributedTransactionParticipant {

    private final String senderAccount;

        private final String receiverAccount;

    private TransactionStatus status;

}
