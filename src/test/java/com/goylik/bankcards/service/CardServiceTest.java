package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.request.CreateCardRequest;
import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.repository.CardRepository;
import com.goylik.bankcards.repository.UserRepository;
import com.goylik.bankcards.service.impl.CardServiceImpl;
import com.goylik.bankcards.util.CardHelper;
import com.goylik.bankcards.util.CardNumberUtils;
import com.goylik.bankcards.util.specification.CardSpecification;
import com.goylik.bankcards.util.SecurityUtils;
import com.goylik.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock private CardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private CardMapper cardMapper;
    @Mock private CardCryptoService cardCryptoService;
    @Mock private CardHelper cardHelper;
    @Mock private CardSpecification cardSpecification;

    @InjectMocks
    private CardServiceImpl cardService;

    private static final Long USER_ID = 1L;
    private static final Long CARD_ID = 10L;

    @Test
    void getUserCardsShouldReturnMappedPage() {
        CardFilter filter = new CardFilter("ACTIVE", null);
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(USER_ID);

        BankCard card = new BankCard(
                "NC123",
                "1234",
                "John",
                YearMonth.now().plusYears(1),
                user);

        card.setId(100L);

        Page<BankCard> page = new PageImpl<>(List.of(card));

        CardResponse mapped = new CardResponse(
                card.getId(),
                "**** " + card.getLast4(),
                card.getOwnerName(),
                card.getStatus().name(),
                card.getExpiryDate(),
                card.getBalance()
        );

        try (var securityMock = Mockito.mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

            Specification<BankCard> spec = mock(Specification.class);
            when(cardSpecification.buildByStatusAndUserId("ACTIVE", USER_ID)).thenReturn(spec);
            when(cardRepository.findAll(spec, pageable)).thenReturn(page);
            when(cardMapper.toResponse(card)).thenReturn(mapped);

            Page<CardResponse> result = cardService.getUserCards(filter, pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(mapped, result.getContent().getFirst());

            verify(cardRepository).findAll(spec, pageable);
            verify(cardMapper).toResponse(card);
        }
    }

    @Test
    void getUserCardByIdShouldValidateOwnershipAndReturnResponse() {
        User user = new User();
        user.setId(USER_ID);

        BankCard card = new BankCard(
                "ENC9999",
                "9999",
                "John",
                YearMonth.now().plusYears(1),
                user);

        card.setId(CARD_ID);

        CardResponse mapped = new CardResponse(
                card.getId(),
                "**** " + card.getLast4(),
                card.getOwnerName(),
                card.getStatus().name(),
                card.getExpiryDate(),
                card.getBalance()
        );

        try (var securityMock = Mockito.mockStatic(SecurityUtils.class)) {
            securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

            when(cardHelper.fetchCardByIdOrElseThrow(CARD_ID)).thenReturn(card);
            when(cardMapper.toResponse(card)).thenReturn(mapped);

            CardResponse result = cardService.getUserCardById(CARD_ID);

            assertEquals(mapped, result);
            verify(cardHelper).validateOwnershipOrElseThrow(card, USER_ID);
        }
    }

    @Test
    void createCardShouldEncryptSaveAndMap() {
        CreateCardRequest request = new CreateCardRequest("John Doe", 2, USER_ID);
        User user = new User();
        user.setId(USER_ID);

        String rawCard = "1111222233334444";
        String last4 = "4444";
        String encrypted = "ENC1111";

        BankCard savedCard = new BankCard(
                encrypted,
                last4,
                "John Doe",
                YearMonth.now().plusYears(2),
                user);

        savedCard.setId(1L);

        CardResponse mapped = new CardResponse(
                savedCard.getId(),
                "**** " + savedCard.getLast4(),
                savedCard.getOwnerName(),
                savedCard.getStatus().name(),
                savedCard.getExpiryDate(),
                savedCard.getBalance()
        );

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        try (var cardNumberMock = Mockito.mockStatic(CardNumberUtils.class)) {
            cardNumberMock.when(CardNumberUtils::generateCardNumber).thenReturn(rawCard);
            cardNumberMock.when(() -> CardNumberUtils.getLast4(rawCard)).thenReturn(last4);

            when(cardCryptoService.encrypt(rawCard)).thenReturn(encrypted);
            when(cardRepository.save(any(BankCard.class))).thenReturn(savedCard);
            when(cardMapper.toResponse(savedCard)).thenReturn(mapped);

            CardResponse result = cardService.createCard(request);

            assertEquals(mapped, result);
            verify(cardRepository).save(any(BankCard.class));
        }
    }

    @Test
    void blockCardShouldCallBlockAndSave() {
        BankCard card = mock(BankCard.class);
        when(cardHelper.fetchCardByIdOrElseThrow(CARD_ID)).thenReturn(card);

        cardService.blockCard(CARD_ID);

        verify(card).block();
        verify(cardRepository).save(card);
    }

    @Test
    void activateCardShouldCallActivateAndSave() {
        BankCard card = mock(BankCard.class);
        when(cardHelper.fetchCardByIdOrElseThrow(CARD_ID)).thenReturn(card);

        cardService.activateCard(CARD_ID);

        verify(card).activate();
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCardShouldDeleteCard() {
        BankCard card = new BankCard(
                "ENC5555",
                "5555",
                "John",
                YearMonth.now().plusYears(1),
                new User());

        card.setId(CARD_ID);

        when(cardHelper.fetchCardByIdOrElseThrow(CARD_ID)).thenReturn(card);

        cardService.deleteCard(CARD_ID);

        verify(cardRepository).delete(card);
    }
}
