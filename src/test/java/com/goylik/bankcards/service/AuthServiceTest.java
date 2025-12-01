package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.LoginRequest;
import com.goylik.bankcards.dto.request.SignupRequest;
import com.goylik.bankcards.dto.response.AuthResponse;
import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.entity.enums.Role;
import com.goylik.bankcards.exception.user.EmailAlreadyExistsException;
import com.goylik.bankcards.exception.user.UserNotFoundException;
import com.goylik.bankcards.repository.UserRepository;
import com.goylik.bankcards.security.jwt.JwtUtils;
import com.goylik.bankcards.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String EMAIL = "test@mail.com";
    private static final String PASSWORD = "pass";
    private static final String ENCODED_PASSWORD = "encoded-pass";
    private static final String JWT = "jwt-token";
    private static final Long USER_ID = 1L;

    @Test
    void authenticateShouldReturnAuthResponseWhenCredentialsValid() {
        var request = new LoginRequest(EMAIL, PASSWORD);

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setRole(Role.USER);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(jwtUtils.generateToken(EMAIL))
                .thenReturn(JWT);

        AuthResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals(EMAIL) &&
                                auth.getCredentials().equals(PASSWORD)
                )
        );

        assertThat(response.jwt()).isEqualTo(JWT);
        assertThat(response.id()).isEqualTo(USER_ID);
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void authenticateShouldThrowExceptionWhenUserNotFound() {
        var request = new LoginRequest(EMAIL, PASSWORD);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.authenticate(request));

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void registerShouldSaveUserWhenEmailNotExists() {
        var request = new SignupRequest(EMAIL, PASSWORD);

        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(false);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.register(request);

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void registerShouldThrowExceptionWhenEmailExists() {
        var request = new SignupRequest(EMAIL, PASSWORD);

        when(userRepository.existsByEmail(EMAIL))
                .thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }
}