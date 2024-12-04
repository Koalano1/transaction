package com.demo.transaction.repository.specification;

import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.repository.filter.TransactionFilter;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecificationCreator {
    public static Specification<Transaction> from(TransactionFilter filter) {
        Specification<Transaction> transactionSpecification = Specification.where(null);

        if (StringUtils.isEmpty(filter.getUserId())) {
            transactionSpecification = transactionSpecification
                    .and(TransactionSpecification.hasUserId(filter.getUserId())
                    .or(TransactionSpecification.hasTransactionType(filter.getType())));
        }

        return transactionSpecification;
    }
}
