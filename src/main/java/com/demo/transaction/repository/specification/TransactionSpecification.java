package com.demo.transaction.repository.specification;

import com.demo.transaction.model.entities.Transaction;
import com.demo.transaction.model.enums.TransactionType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public interface TransactionSpecification {

    static Specification<Transaction> hasTransactionType(TransactionType transactionType) {
        return (transaction, cq, cb) -> cb.equal(transaction.get("transactionType"), transactionType);
    }
    static Specification<Transaction> hasUserId(String userId) {
        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("senderId"), userId),
                        criteriaBuilder.equal(root.get("receiverId"), userId)
                );
    }

    static Specification<Transaction> isSender(String senderId) {
        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("senderId"), senderId);
    }

    static Specification<Transaction> isReceiver(String receiverId) {
        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("receiverId"), receiverId);
    }
    static Specification<Transaction> hasAmount(BigDecimal amount) {
        return (transaction, cq, cb) -> cb.equal(transaction.get("amount"), amount);
    }

}
