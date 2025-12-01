package com.goylik.bankcards.util.scheduler;

import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.entity.enums.CardStatus;
import com.goylik.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardExpiryService {

    private final CardRepository cardRepository;

    @Transactional
    public void expireCardsIfNeeded() {
        var cards = cardRepository.findAllByStatusNot(CardStatus.EXPIRED);
        for (BankCard card : cards) {
            if (card.isExpired()) {
                CardStatus oldStatus = card.getStatus();
                card.expire();
                cardRepository.save(card);
                log.info("Card {} status changed from {} to EXPIRED", card.getId(), oldStatus);
            }
        }
    }
}

