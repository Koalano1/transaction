package com.demo.transaction.repository;

import com.demo.transaction.model.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.id = :userId OR t.receiverAccount.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);

}
