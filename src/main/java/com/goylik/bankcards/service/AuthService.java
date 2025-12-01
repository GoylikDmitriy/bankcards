package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.LoginRequest;
import com.goylik.bankcards.dto.request.SignupRequest;
import com.goylik.bankcards.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse authenticate(LoginRequest request);
    void register(SignupRequest request);
}
