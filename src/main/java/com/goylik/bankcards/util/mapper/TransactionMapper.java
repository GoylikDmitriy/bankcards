package com.goylik.bankcards.util.mapper;

import com.goylik.bankcards.dto.response.TransactionResponse;
import com.goylik.bankcards.entity.Transaction;
import com.goylik.bankcards.util.CardNumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(
            target = "maskedCardNumberFrom",
            source = "fromCard.last4",
            qualifiedByName = "maskCard"
    )
    @Mapping(
            target = "maskedCardNumberTo",
            source = "toCard.last4",
            qualifiedByName = "maskCard"
    )
    TransactionResponse toResponse(Transaction transaction);

    @Named("maskCard")
    default String maskCard(String last4) {
        return CardNumberUtils.getMaskedCardNumber(last4);
    }
}
