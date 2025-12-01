package com.goylik.bankcards.util.mapper;

import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.entity.CardBlockRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardBlockRequestMapper {
    CardBlockRequestResponse toResponse(CardBlockRequest cardBlockRequest);
}
