package com.demo.transaction.dto;

import com.demo.transaction.model.enums.TransactionStatus;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NonNull
@Setter
@Getter
public class DistributedTransaction {

    private String id;

    private TransactionStatus status;

    private List<DistributedTransactionParticipant> participants;

}