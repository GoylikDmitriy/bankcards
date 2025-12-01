package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.response.CardBlockRequestResponse;
import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.security.jwt.JwtAuthFilter;
import com.goylik.bankcards.service.CardBlockRequestService;
import com.goylik.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardBlockRequestService cardBlockRequestService;

    @MockitoBean
    protected JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long CARD_ID = 1L;

    @Test
    void getUserCardsShouldReturnPagedCards() throws Exception {
        CardResponse card = new CardResponse(1L, "**** 1234", "John Doe", "ACTIVE", YearMonth.now().plusYears(1), BigDecimal.valueOf(1000));
        Page<CardResponse> page = new PageImpl<>(List.of(card));

        when(cardService.getUserCards(any(CardFilter.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/user/cards")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(card.id()))
                .andExpect(jsonPath("$.content[0].maskedCardNumber").value(card.maskedCardNumber()))
                .andExpect(jsonPath("$.content[0].ownerName").value(card.ownerName()))
                .andExpect(jsonPath("$.content[0].status").value(card.status()));
    }

    @Test
    void getUserCardDetailsShouldReturnCard() throws Exception {
        CardResponse card = new CardResponse(CARD_ID, "**** 1234", "John Doe", "ACTIVE", YearMonth.now().plusYears(1), BigDecimal.valueOf(1000));

        when(cardService.getUserCardById(CARD_ID)).thenReturn(card);

        mockMvc.perform(get("/api/user/cards/{cardId}", CARD_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(card.id()))
                .andExpect(jsonPath("$.maskedCardNumber").value(card.maskedCardNumber()))
                .andExpect(jsonPath("$.ownerName").value(card.ownerName()))
                .andExpect(jsonPath("$.status").value(card.status()));
    }

    @Test
    void requestCardBlockShouldReturnCreatedResponse() throws Exception {
        CardBlockRequestResponse response = new CardBlockRequestResponse(10L, "PENDING", null);

        when(cardBlockRequestService.createRequest(CARD_ID)).thenReturn(response);

        mockMvc.perform(post("/api/user/cards/{cardId}/request-block", CARD_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.status").value(response.status()));
    }
}

