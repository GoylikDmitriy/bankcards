package com.goylik.bankcards.service.impl;

import com.goylik.bankcards.dto.request.ChangeUserRoleRequest;
import com.goylik.bankcards.dto.response.UserResponse;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.entity.enums.Role;
import com.goylik.bankcards.exception.user.UserNotFoundException;
import com.goylik.bankcards.repository.UserRepository;
import com.goylik.bankcards.service.UserService;
import com.goylik.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse updateRole(ChangeUserRoleRequest request) {
        var user = fetchUserByIdOrElseThrow(request.userId());
        user.setRole(Role.valueOf(request.role().toUpperCase()));
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        var user = fetchUserByIdOrElseThrow(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        var user = fetchUserByIdOrElseThrow(userId);
        userRepository.delete(user);
    }

    private User fetchUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id = " + id));
    }
}
