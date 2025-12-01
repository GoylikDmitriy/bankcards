package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.LoginRequest;
import com.goylik.bankcards.dto.request.SignupRequest;
import com.goylik.bankcards.dto.response.AuthResponse;
import com.goylik.bankcards.security.jwt.JwtAuthFilter;
import com.goylik.bankcards.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String EMAIL = "test@mail.com";
    private static final String PASSWORD = "password";
    private static final Long USER_ID = 1L;
    private static final String JWT = "jwt-token";

    @Test
    void signupShouldReturn201WhenRequestValid() throws Exception {
        SignupRequest request = new SignupRequest(EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(authService).register(request);
    }

    @Test
    void signupShouldReturn400WhenRequestInvalid() throws Exception {
        SignupRequest request = new SignupRequest("", "");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signinShouldReturnAuthResponseWhenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);

        AuthResponse response = new AuthResponse(JWT, USER_ID, EMAIL, "USER");

        when(authService.authenticate(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(JWT))
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).authenticate(request);
    }

    @Test
    void signinShouldReturn400WhenRequestInvalid() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

