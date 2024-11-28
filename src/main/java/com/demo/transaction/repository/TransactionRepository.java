package com.demo.transaction.repository;

import com.demo.transaction.model.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findBySenderId(String userId);

    List<Transaction> findByReceiverId(String userId);
}
