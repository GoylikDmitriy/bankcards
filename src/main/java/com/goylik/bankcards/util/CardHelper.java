package com.goylik.bankcards.util;

import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.exception.AccessDeniedException;
import com.goylik.bankcards.exception.card.CardNotFoundException;
import com.goylik.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CardHelper {
    private final CardRepository cardRepository;

    public BankCard fetchCardByIdWithLockOrElseThrow(Long id) {
        return cardRepository.findByIdWithLock(id)
                .orElseThrow(() -> new CardNotFoundException("Bank card not found with id = " + id));
    }

    public BankCard fetchCardByIdOrElseThrow(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Bank card not found with id = " + id));
    }

    public void validateOwnershipOrElseThrow(BankCard card, Long userId) {
        if (!Objects.equals(card.getUser().getId(), userId))
            throw new AccessDeniedException("Card does not belong to this user");
    }
}
