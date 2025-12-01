package com.goylik.bankcards.service.impl;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.request.CreateCardRequest;
import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.exception.user.UserNotFoundException;
import com.goylik.bankcards.repository.CardRepository;
import com.goylik.bankcards.repository.UserRepository;
import com.goylik.bankcards.service.CardCryptoService;
import com.goylik.bankcards.service.CardService;
import com.goylik.bankcards.util.CardHelper;
import com.goylik.bankcards.util.CardNumberUtils;
import com.goylik.bankcards.util.specification.CardSpecification;
import com.goylik.bankcards.util.SecurityUtils;
import com.goylik.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardCryptoService cardCryptoService;
    private final CardHelper cardHelper;
    private final CardSpecification cardSpecification;

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponse> getUserCards(CardFilter filter, Pageable pageable) {
        var userId = SecurityUtils.getCurrentUserId();
        var spec = cardSpecification.buildByStatusAndUserId(filter.status(), userId);
        var cards = cardRepository.findAll(spec, pageable);
        return cards.map(cardMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getUserCardById(Long cardId) {
        var card = cardHelper.fetchCardByIdOrElseThrow(cardId);
        var userId = SecurityUtils.getCurrentUserId();
        cardHelper.validateOwnershipOrElseThrow(card, userId);
        return cardMapper.toResponse(card);
    }

    @Override
    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        var user = fetchUserByIdOrElseThrow(request.userId());
        var bankCard = buildNewCard(request, user);
        bankCard = cardRepository.save(bankCard);
        return cardMapper.toResponse(bankCard);
    }

    private User fetchUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id = " + id));
    }

    private BankCard buildNewCard(CreateCardRequest request, User user) {
        var cardNumber = CardNumberUtils.generateCardNumber();
        var last4 = CardNumberUtils.getLast4(cardNumber);
        var encryptedCardNumber = cardCryptoService.encrypt(cardNumber);
        var expiry = YearMonth.now().plusYears(request.validityYears());

        return new BankCard(
                encryptedCardNumber,
                last4,
                request.ownerName(),
                expiry,
                user
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(CardFilter filter, Pageable pageable) {
        var spec = cardSpecification.buildByStatusAndUserId(filter.status(), filter.userId());
        return cardRepository.findAll(spec, pageable)
                .map(cardMapper::toResponse);
    }

    @Override
    @Transactional
    public void blockCard(Long cardId) {
        var card = cardHelper.fetchCardByIdOrElseThrow(cardId);
        card.block();
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void activateCard(Long cardId) {
        var card = cardHelper.fetchCardByIdOrElseThrow(cardId);
        card.activate();
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        var card = cardHelper.fetchCardByIdOrElseThrow(cardId);
        cardRepository.delete(card);
    }
}