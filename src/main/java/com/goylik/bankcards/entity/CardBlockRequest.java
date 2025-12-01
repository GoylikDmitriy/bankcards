package com.goylik.bankcards.entity;

import com.goylik.bankcards.entity.enums.CardBlockRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "card_block_requests")
@Getter @Setter
public class CardBlockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private BankCard bankCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardBlockRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_admin_id")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    protected CardBlockRequest() {}

    public CardBlockRequest(User user, BankCard bankCard) {
        this.user = user;
        this.bankCard = bankCard;
        this.status = CardBlockRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "CardBlockRequest{" +
                "id=" + id +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardBlockRequest that = (CardBlockRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}