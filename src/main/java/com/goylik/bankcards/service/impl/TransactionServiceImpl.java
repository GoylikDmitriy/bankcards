package com.goylik.bankcards.service.impl;

import com.goylik.bankcards.dto.request.InternalTransferRequest;
import com.goylik.bankcards.dto.response.TransactionResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.Transaction;
import com.goylik.bankcards.exception.card.BlockedCardException;
import com.goylik.bankcards.exception.card.ExpiredCardException;
import com.goylik.bankcards.exception.transaction.InsufficientBalanceException;
import com.goylik.bankcards.exception.transaction.CardOwnershipMismatchException;
import com.goylik.bankcards.exception.transaction.SameCardTransferException;
import com.goylik.bankcards.repository.CardRepository;
import com.goylik.bankcards.repository.TransactionRepository;
import com.goylik.bankcards.service.TransactionService;
import com.goylik.bankcards.util.CardHelper;
import com.goylik.bankcards.util.SecurityUtils;
import com.goylik.bankcards.util.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final TransactionMapper transactionMapper;
    private final CardHelper cardHelper;

    @Override
    @Transactional
    public TransactionResponse makeInternalTransfer(InternalTransferRequest request) {
        long firstId = Math.min(request.fromCardId(), request.toCardId());
        long secondId = Math.max(request.fromCardId(), request.toCardId());

        var firstLockedCard = cardHelper.fetchCardByIdWithLockOrElseThrow(firstId);
        var secondLockedCard = cardHelper.fetchCardByIdWithLockOrElseThrow(secondId);

        var fromCard = (request.fromCardId() == firstId) ? firstLockedCard : secondLockedCard;
        var toCard = (request.toCardId() == secondId) ? secondLockedCard : firstLockedCard;

        validateTransferOrThrow(fromCard, toCard, request.amount());

        applyTransfer(fromCard, toCard, request.amount());

        var transaction = new Transaction(fromCard, toCard, request.amount());
        transaction = transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    private void validateTransferOrThrow(BankCard fromCard, BankCard toCard, BigDecimal amount) {
        validateCardsOrThrow(fromCard, toCard);

        var userId = SecurityUtils.getCurrentUserId();
        cardHelper.validateOwnershipOrElseThrow(fromCard, userId);

        if (!hasSufficientBalance(fromCard, amount)) {
            throw new InsufficientBalanceException("Not enough balance to make transfer.");
        }
    }

    private void validateCardsOrThrow(BankCard fromCard, BankCard toCard) {
        if (fromCard.equals(toCard)) {
            throw new SameCardTransferException("Can't transfer to the same card.");
        }

        if (!Objects.equals(fromCard.getUser(), toCard.getUser())) {
            throw new CardOwnershipMismatchException("Cards must belong to the same user.");
        }

        if (fromCard.isBlocked() || toCard.isBlocked()) {
            throw new BlockedCardException("Can't make transfer cause one of the cards is blocked.");
        }

        if (fromCard.isExpired() || toCard.isExpired()) {
            throw new ExpiredCardException("Can't make transfer cause one of the cards is expired.");
        }
    }

    private boolean hasSufficientBalance(BankCard fromCard, BigDecimal amount) {
        return fromCard.getBalance().compareTo(amount) >= 0;
    }

    private void applyTransfer(BankCard from, BankCard to, BigDecimal amount) {
        from.withdraw(amount);
        to.deposit(amount);

        cardRepository.save(from);
        cardRepository.save(to);
    }
}
