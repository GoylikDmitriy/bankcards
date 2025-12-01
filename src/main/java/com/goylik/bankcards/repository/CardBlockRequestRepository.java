package com.goylik.bankcards.repository;

import com.goylik.bankcards.entity.CardBlockRequest;
import com.goylik.bankcards.entity.enums.CardBlockRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long>,
        JpaSpecificationExecutor<CardBlockRequest> {

    boolean existsByBankCardIdAndStatus(Long cardId, CardBlockRequestStatus status);
}
