package com.goylik.bankcards.util.specification;

import com.goylik.bankcards.entity.BankCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CardSpecification {
    public Specification<BankCard> hasStatus(String status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public Specification<BankCard> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public Specification<BankCard> buildByStatusAndUserId(String status, Long userId) {
        return Specification
                .where(hasStatus(status))
                .and(hasUserId(userId));
    }
}