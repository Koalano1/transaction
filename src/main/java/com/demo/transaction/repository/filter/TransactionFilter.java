package com.demo.transaction.repository.filter;

import com.demo.transaction.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TransactionFilter {

    private String userId;

    private TransactionType type;

}
