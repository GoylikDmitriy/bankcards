package com.goylik.bankcards.service.impl;

import com.goylik.bankcards.dto.request.LoginRequest;
import com.goylik.bankcards.dto.request.SignupRequest;
import com.goylik.bankcards.dto.response.AuthResponse;
import com.goylik.bankcards.entity.enums.Role;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.exception.user.EmailAlreadyExistsException;
import com.goylik.bankcards.exception.user.UserNotFoundException;
import com.goylik.bankcards.repository.UserRepository;
import com.goylik.bankcards.security.jwt.JwtUtils;
import com.goylik.bankcards.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.email()));

        String jwt = jwtUtils.generateToken(request.email());

        return new AuthResponse(jwt, user.getId(), user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public void register(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("User already registered with email: " + request.email());
        }

        var user = createUser(request.email(), request.password());
        userRepository.save(user);
    }

    private User createUser(String email, String password) {
        var user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setEnabled(true);

        return user;
    }
}
