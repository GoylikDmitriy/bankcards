package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.request.CreateCardRequest;
import com.goylik.bankcards.dto.response.CardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    Page<CardResponse> getUserCards(CardFilter filter, Pageable pageable);
    CardResponse getUserCardById(Long cardId);

    CardResponse createCard(CreateCardRequest request);
    Page<CardResponse> getAllCards(CardFilter filter, Pageable pageable);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    void deleteCard(Long cardId);
}
