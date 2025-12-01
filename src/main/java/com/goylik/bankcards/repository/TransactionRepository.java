package com.goylik.bankcards.repository;

import com.goylik.bankcards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
