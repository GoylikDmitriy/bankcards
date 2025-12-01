package com.goylik.bankcards.service.impl;

import com.goylik.bankcards.dto.request.CardBlockRequestFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.CardBlockRequest;
import com.goylik.bankcards.entity.enums.CardBlockRequestStatus;
import com.goylik.bankcards.exception.card.*;
import com.goylik.bankcards.repository.CardBlockRequestRepository;
import com.goylik.bankcards.service.CardBlockRequestService;
import com.goylik.bankcards.service.CardService;
import com.goylik.bankcards.util.specification.CardBlockRequestSpecification;
import com.goylik.bankcards.util.CardHelper;
import com.goylik.bankcards.util.SecurityUtils;
import com.goylik.bankcards.util.mapper.CardBlockRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CardBlockRequestServiceImpl implements CardBlockRequestService {
    private final CardBlockRequestRepository requestRepository;
    private final CardBlockRequestMapper requestMapper;
    private final CardService cardService;
    private final CardHelper cardHelper;
    private final CardBlockRequestSpecification requestSpecification;

    @Override
    @Transactional
    public CardBlockRequestResponse createRequest(Long cardId) {
        var card = cardHelper.fetchCardByIdOrElseThrow(cardId);
        var user = SecurityUtils.getCurrentUser();

        cardHelper.validateOwnershipOrElseThrow(card, user.getId());

        validateCardForBlockRequest(card);

        var cardBlockRequest = new CardBlockRequest(user, card);
        cardBlockRequest = requestRepository.save(cardBlockRequest);
        return requestMapper.toResponse(cardBlockRequest);
    }

    private void validateCardForBlockRequest(BankCard card) {
        if (requestRepository.existsByBankCardIdAndStatus(card.getId(), CardBlockRequestStatus.PENDING)) {
            throw new DuplicateBlockRequestException("There is already a pending request for this card.");
        }

        if (card.isExpired()) {
            throw new ExpiredCardException("Cannot block expired card.");
        }

        if (card.isBlocked()) {
            throw new BlockedCardException("Card is already blocked.");
        }
    }

    @Override
    @Transactional
    public void approveRequest(Long requestId) {
        var request = processRequest(requestId, CardBlockRequestStatus.APPROVED);
        cardService.blockCard(request.getBankCard().getId());
    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId) {
        processRequest(requestId, CardBlockRequestStatus.REJECTED);
    }

    private CardBlockRequest processRequest(Long requestId, CardBlockRequestStatus status) {
        var request = fetchCardBlockRequestByIdOrElseThrow(requestId);

        if (request.getStatus() != CardBlockRequestStatus.PENDING) {
            throw new InvalidRequestStatusException("Only pending requests can be processed. Current status: " + request.getStatus());
        }

        var admin = SecurityUtils.getCurrentUser();
        request.setProcessedBy(admin);
        request.setProcessedAt(LocalDateTime.now());
        request.setStatus(status);

        return requestRepository.save(request);
    }

    private CardBlockRequest fetchCardBlockRequestByIdOrElseThrow(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() ->
                        new BlockCardRequestNotFound(
                                "Request to block the card is not found with id = " + id)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardBlockRequestResponse> getPendingRequests(CardBlockRequestFilter filter,
                                                             Pageable pageable) {
        var spec = requestSpecification
                .buildByStatusAndUserId(CardBlockRequestStatus.PENDING.name(), filter.userId());

        return requestRepository.findAll(spec, pageable)
                .map(requestMapper::toResponse);
    }
}
