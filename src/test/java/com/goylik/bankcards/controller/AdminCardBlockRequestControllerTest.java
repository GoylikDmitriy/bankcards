package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.CardBlockRequestFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.security.jwt.JwtAuthFilter;
import com.goylik.bankcards.service.CardBlockRequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCardBlockRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCardBlockRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    protected JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CardBlockRequestService cardBlockRequestService;

    private static final Long REQUEST_ID = 1L;

    @Test
    void getPendingCardBlockRequestsShouldReturnPagedRequests() throws Exception {
        CardBlockRequestFilter filter = new CardBlockRequestFilter("PENDING", null);
        Page<CardBlockRequestResponse> page = new PageImpl<>(List.of(
                new CardBlockRequestResponse(1L, "PENDING", LocalDateTime.now())
        ), PageRequest.of(0, 10), 1);

        Mockito.when(cardBlockRequestService.getPendingRequests(any(CardBlockRequestFilter.class), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/block-requests/pending")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void approveCardBlockRequestShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/admin/block-requests/{requestId}/approve", REQUEST_ID))
                .andExpect(status().isOk());

        Mockito.verify(cardBlockRequestService).approveRequest(REQUEST_ID);
    }

    @Test
    void rejectCardBlockRequestShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/admin/block-requests/{requestId}/reject", REQUEST_ID))
                .andExpect(status().isOk());

        Mockito.verify(cardBlockRequestService).rejectRequest(REQUEST_ID);
    }
}
