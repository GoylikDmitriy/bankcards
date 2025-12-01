package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.InternalTransferRequest;
import com.goylik.bankcards.dto.response.TransactionResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.Transaction;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.exception.transaction.*;
import com.goylik.bankcards.repository.CardRepository;
import com.goylik.bankcards.repository.TransactionRepository;
import com.goylik.bankcards.service.impl.TransactionServiceImpl;
import com.goylik.bankcards.util.CardHelper;
import com.goylik.bankcards.util.SecurityUtils;
import com.goylik.bankcards.util.mapper.TransactionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CardRepository cardRepository;
    @Mock private TransactionMapper transactionMapper;
    @Mock private CardHelper cardHelper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private static final Long USER_ID = 1L;
    private static final Long FROM_CARD_ID = 10L;
    private static final Long TO_CARD_ID = 20L;

    @Test
    void makeInternalTransferShouldTransferBalanceAndReturnResponse() {
        BankCard fromCard = new BankCard("ENC1", "1111", "John", null, null);
        fromCard.setId(FROM_CARD_ID);
        fromCard.setBalance(BigDecimal.valueOf(1000));

        BankCard toCard = new BankCard("ENC2", "2222", "John", null, null);
        toCard.setId(TO_CARD_ID);
        toCard.setBalance(BigDecimal.valueOf(500));

        InternalTransferRequest request = new InternalTransferRequest(FROM_CARD_ID, TO_CARD_ID, BigDecimal.valueOf(300));

        Transaction transaction = new Transaction(fromCard, toCard, request.amount());
        TransactionResponse response = new TransactionResponse(
                1L,
                "**** 1111",
                "**** 2222",
                request.amount(),
                LocalDateTime.now()
        );

        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

            when(cardHelper.fetchCardByIdWithLockOrElseThrow(FROM_CARD_ID)).thenReturn(fromCard);
            when(cardHelper.fetchCardByIdWithLockOrElseThrow(TO_CARD_ID)).thenReturn(toCard);
            doNothing().when(cardHelper).validateOwnershipOrElseThrow(fromCard, USER_ID);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
            when(transactionMapper.toResponse(transaction)).thenReturn(response);

            TransactionResponse result = transactionService.makeInternalTransfer(request);

            assertEquals(response, result);
            assertEquals(BigDecimal.valueOf(700), fromCard.getBalance());
            assertEquals(BigDecimal.valueOf(800), toCard.getBalance());

            verify(cardRepository).save(fromCard);
            verify(cardRepository).save(toCard);
        }
    }

    @Test
    void makeInternalTransferShouldThrowSameCardTransferException() {
        BankCard card = new BankCard("ENC", "1111", "John", null, null);
        card.setId(FROM_CARD_ID);

        InternalTransferRequest request = new InternalTransferRequest(FROM_CARD_ID, FROM_CARD_ID, BigDecimal.valueOf(100));

        when(cardHelper.fetchCardByIdWithLockOrElseThrow(FROM_CARD_ID)).thenReturn(card);

        SameCardTransferException ex = assertThrows(SameCardTransferException.class, () ->
                transactionService.makeInternalTransfer(request));

        assertEquals("Can't transfer to the same card.", ex.getMessage());
    }

    @Test
    void makeInternalTransferShouldThrowInsufficientBalanceException() {
        BankCard fromCard = new BankCard("ENC1", "1111", "John", null, null);
        fromCard.setId(FROM_CARD_ID);
        fromCard.setBalance(BigDecimal.valueOf(100));

        BankCard toCard = new BankCard("ENC2", "2222", "John", null, null);
        toCard.setId(TO_CARD_ID);

        InternalTransferRequest request = new InternalTransferRequest(FROM_CARD_ID, TO_CARD_ID, BigDecimal.valueOf(200));

        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

            when(cardHelper.fetchCardByIdWithLockOrElseThrow(FROM_CARD_ID)).thenReturn(fromCard);
            when(cardHelper.fetchCardByIdWithLockOrElseThrow(TO_CARD_ID)).thenReturn(toCard);
            doNothing().when(cardHelper).validateOwnershipOrElseThrow(fromCard, USER_ID);

            InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () ->
                    transactionService.makeInternalTransfer(request));

            assertEquals("Not enough balance to make transfer.", ex.getMessage());
        }
    }

    @Test
    void makeInternalTransferShouldThrowCardOwnershipMismatchException() {
        User user1 = new User();
        user1.setId(USER_ID);
        user1.setEmail("test@mail.com");
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@mail.com");

        BankCard fromCard = new BankCard("ENC1", "1111", "John", null, user1);
        fromCard.setId(FROM_CARD_ID);
        fromCard.setBalance(BigDecimal.valueOf(1000L));

        BankCard toCard = new BankCard("ENC2", "2222", "Jane", null, user2);
        toCard.setId(TO_CARD_ID);
        toCard.setBalance(BigDecimal.valueOf(230L));

        InternalTransferRequest request = new InternalTransferRequest(FROM_CARD_ID, TO_CARD_ID, BigDecimal.valueOf(100));

        when(cardHelper.fetchCardByIdWithLockOrElseThrow(FROM_CARD_ID)).thenReturn(fromCard);
        when(cardHelper.fetchCardByIdWithLockOrElseThrow(TO_CARD_ID)).thenReturn(toCard);

        try (var mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(user1.getId());

            CardOwnershipMismatchException ex = assertThrows(CardOwnershipMismatchException.class, () ->
                    transactionService.makeInternalTransfer(request));

            assertEquals("Cards must belong to the same user.", ex.getMessage());
        }
    }
}
