package com.goylik.bankcards.util.mapper;

import com.goylik.bankcards.dto.response.UserResponse;
import com.goylik.bankcards.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
