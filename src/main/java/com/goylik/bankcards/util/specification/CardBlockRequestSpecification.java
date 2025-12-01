package com.goylik.bankcards.util.specification;

import com.goylik.bankcards.entity.CardBlockRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CardBlockRequestSpecification {
    public Specification<CardBlockRequest> hasStatus(String status) {
        return ((root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status));
    }

    public Specification<CardBlockRequest> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public Specification<CardBlockRequest> buildByStatusAndUserId(String status, Long userId) {
        return Specification
                .where(hasStatus(status))
                .and(hasUserId(userId));
    }
}
