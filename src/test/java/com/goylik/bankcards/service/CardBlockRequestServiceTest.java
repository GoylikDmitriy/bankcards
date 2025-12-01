package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.CardBlockRequestFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.CardBlockRequest;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.entity.enums.CardBlockRequestStatus;
import com.goylik.bankcards.exception.card.DuplicateBlockRequestException;
import com.goylik.bankcards.exception.card.InvalidRequestStatusException;
import com.goylik.bankcards.repository.CardBlockRequestRepository;
import com.goylik.bankcards.service.impl.CardBlockRequestServiceImpl;
import com.goylik.bankcards.util.specification.CardBlockRequestSpecification;
import com.goylik.bankcards.util.CardHelper;
import com.goylik.bankcards.util.SecurityUtils;
import com.goylik.bankcards.util.mapper.CardBlockRequestMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBlockRequestServiceTest {

    @Mock private CardBlockRequestRepository requestRepository;
    @Mock private CardBlockRequestMapper requestMapper;
    @Mock private CardService cardService;
    @Mock private CardHelper cardHelper;
    @Mock private CardBlockRequestSpecification requestSpecification;

    @InjectMocks private CardBlockRequestServiceImpl requestService;

    private static final Long CARD_ID = 1L;
    private static final Long REQUEST_ID = 100L;
    private static final Long USER_ID = 10L;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(USER_ID);
    }

    @Test
    void createRequestShouldReturnMappedResponse() {
        BankCard card = new BankCard("ENC", "1234", "John", YearMonth.now().plusYears(1), currentUser);
        card.setId(CARD_ID);
        CardBlockRequest savedRequest = new CardBlockRequest(currentUser, card);
        CardBlockRequestResponse response = new CardBlockRequestResponse(REQUEST_ID, CardBlockRequestStatus.PENDING.name(), LocalDateTime.now());

        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(currentUser);

            when(cardHelper.fetchCardByIdOrElseThrow(CARD_ID)).thenReturn(card);
            when(requestRepository.existsByBankCardIdAndStatus(CARD_ID, CardBlockRequestStatus.PENDING)).thenReturn(false);
            when(requestRepository.save(any())).thenReturn(savedRequest);
            when(requestMapper.toResponse(savedRequest)).thenReturn(response);

            CardBlockRequestResponse result = requestService.createRequest(CARD_ID);

            assertEquals(response, result);
            verify(cardHelper).validateOwnershipOrElseThrow(card, USER_ID);
            verify(requestRepository).save(any(CardBlockRequest.class));
        }
    }

    @Test
    void createRequestWhenPendingRequestExistsShouldThrowException() {
        BankCard card = new BankCard("ENC", "1234", "John", YearMonth.now().plusYears(1), currentUser);
        card.setId(CARD_ID);
        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(currentUser);

            when(cardHelper.fetchCardByIdOrElseThrow(CARD_ID)).thenReturn(card);
            when(requestRepository.existsByBankCardIdAndStatus(CARD_ID, CardBlockRequestStatus.PENDING)).thenReturn(true);

            assertThrows(DuplicateBlockRequestException.class,
                    () -> requestService.createRequest(CARD_ID));
        }
    }

    @Test
    void approveRequestShouldBlockCard() {
        BankCard card = new BankCard("ENC", "1234", "John", YearMonth.now().plusYears(1), currentUser);
        CardBlockRequest request = new CardBlockRequest(currentUser, card);
        request.setStatus(CardBlockRequestStatus.PENDING);

        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);

        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(currentUser);

            requestService.approveRequest(REQUEST_ID);

            assertEquals(CardBlockRequestStatus.APPROVED, request.getStatus());
            verify(cardService).blockCard(card.getId());
            verify(requestRepository).save(request);
        }
    }

    @Test
    void approveRequestWhenNotPendingShouldThrowException() {
        BankCard card = new BankCard("ENC", "1234", "John", YearMonth.now().plusYears(1), currentUser);
        CardBlockRequest request = new CardBlockRequest(currentUser, card);
        request.setStatus(CardBlockRequestStatus.APPROVED);

        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));

        assertThrows(InvalidRequestStatusException.class,
                () -> requestService.approveRequest(REQUEST_ID));
    }

    @Test
    void rejectRequestShouldUpdateStatus() {
        BankCard card = new BankCard("ENC", "1234", "John", YearMonth.now().plusYears(1), currentUser);
        CardBlockRequest request = new CardBlockRequest(currentUser, card);
        request.setStatus(CardBlockRequestStatus.PENDING);

        when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);

        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(currentUser);

            requestService.rejectRequest(REQUEST_ID);

            assertEquals(CardBlockRequestStatus.REJECTED, request.getStatus());
            verify(requestRepository).save(request);
        }
    }

    @Test
    void getPendingRequestsShouldReturnMappedPage() {
        CardBlockRequestFilter filter = new CardBlockRequestFilter(CardBlockRequestStatus.PENDING.name(), USER_ID);
        Pageable pageable = PageRequest.of(0, 10);

        BankCard card = new BankCard("ENC", "1234", "John", YearMonth.now().plusYears(1), currentUser);
        CardBlockRequest request = new CardBlockRequest(currentUser, card);
        Page<CardBlockRequest> page = new PageImpl<>(List.of(request));

        CardBlockRequestResponse response = new CardBlockRequestResponse(REQUEST_ID, CardBlockRequestStatus.PENDING.name(), LocalDateTime.now());

        when(requestSpecification.buildByStatusAndUserId(CardBlockRequestStatus.PENDING.name(), USER_ID))
                .thenReturn((root, query, cb) -> null);
        when(requestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(requestMapper.toResponse(request)).thenReturn(response);

        Page<CardBlockRequestResponse> result = requestService.getPendingRequests(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().getFirst());
    }
}
