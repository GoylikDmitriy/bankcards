package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.CardBlockRequestFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardBlockRequestService {
    CardBlockRequestResponse createRequest(Long cardId);
    void approveRequest(Long requestId);
    void rejectRequest(Long requestId);
    Page<CardBlockRequestResponse> getPendingRequests(CardBlockRequestFilter filter, Pageable pageable);
}
