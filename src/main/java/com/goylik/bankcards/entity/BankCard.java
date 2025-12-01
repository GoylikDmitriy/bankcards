package com.goylik.bankcards.entity;

import com.goylik.bankcards.entity.enums.CardStatus;
import com.goylik.bankcards.exception.card.ActiveCardException;
import com.goylik.bankcards.exception.card.BlockedCardException;
import com.goylik.bankcards.exception.card.ExpiredCardException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

@Entity
@Table(name = "bank_cards")
@Getter @Setter
@SQLDelete(sql = "UPDATE bank_cards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "encrypted_card_number", nullable = false, length = 256)
    private String encryptedCardNumber;

    @Column(name = "last4", nullable = false, length = 4)
    private String last4;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "expiry_date", nullable = false)
    private YearMonth expiryDate;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void block() {
        if (isExpired()) {
            throw new ExpiredCardException("Can't block expired card.");
        }

        if (isBlocked()) {
            throw new BlockedCardException("Card is already blocked.");
        }

        status = CardStatus.BLOCKED;
    }

    public void activate() {
        if (isExpired()) {
            throw new ExpiredCardException("Can't activate expired card.");
        }

        if (isActive()) {
            throw new ActiveCardException("Card is already active.");
        }

        status = CardStatus.ACTIVE;
    }

    public void expire() {
        if (!isExpired()) {
            throw new ExpiredCardException("Card is not expired yet.");
        }

        status = CardStatus.EXPIRED;
    }

    public boolean isActive() {
        return status == CardStatus.ACTIVE;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(YearMonth.now());
    }

    public boolean isBlocked() {
        return status == CardStatus.BLOCKED;
    }

    protected BankCard() {}

    public BankCard(String encryptedCardNumber, String last4,
                    String ownerName, YearMonth expiryDate,
                    User user) {
        this.encryptedCardNumber = encryptedCardNumber;
        this.last4 = last4;
        this.ownerName = ownerName;
        this.expiryDate = expiryDate;
        this.user = user;
        this.status = CardStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "BankCard{" +
                "id=" + id +
                ", ownerName='" + ownerName + '\'' +
                ", status=" + status +
                ", expiryDate=" + expiryDate +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankCard other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
