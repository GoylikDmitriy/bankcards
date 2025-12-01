package com.goylik.bankcards.util.mapper;

import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.entity.BankCard;
import com.goylik.bankcards.util.CardNumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(
            target = "maskedCardNumber",
            source = "last4",
            qualifiedByName = "maskCard"
    )
    CardResponse toResponse(BankCard card);

    @Named("maskCard")
    default String maskCard(String last4) {
        return CardNumberUtils.getMaskedCardNumber(last4);
    }
}