package com.goylik.bankcards.repository;

import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<BankCard, Long>,
        JpaSpecificationExecutor<BankCard> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM BankCard c WHERE c.id = :id")
    Optional<BankCard> findByIdWithLock(@Param("id") Long id);

    List<BankCard> findAllByStatusNot(CardStatus status);
}
