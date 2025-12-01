package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.ChangeUserRoleRequest;
import com.goylik.bankcards.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse updateRole(ChangeUserRoleRequest request);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long userId);
    void deleteUser(Long userId);
}
