package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.CardFilter;
import com.goylik.bankcards.dto.request.CreateCardRequest;
import com.goylik.bankcards.dto.response.CardResponse;
import com.goylik.bankcards.security.jwt.JwtAuthFilter;
import com.goylik.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    protected JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CardService cardService;

    private static final Long CARD_ID = 1L;

    @Test
    void createCardShouldReturnCreatedCard() throws Exception {
        CreateCardRequest request = new CreateCardRequest("John Doe", 2, 1L);
        CardResponse response = new CardResponse(1L, "**** 1234", "John Doe", "ACTIVE", YearMonth.now().plusYears(2), BigDecimal.valueOf(1000));

        Mockito.when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.maskedCardNumber").value(response.maskedCardNumber()))
                .andExpect(jsonPath("$.ownerName").value(response.ownerName()))
                .andExpect(jsonPath("$.status").value(response.status()));
    }

    @Test
    void getAllCardsShouldReturnPagedCards() throws Exception {
        CardFilter filter = new CardFilter("ACTIVE", null);
        Page<CardResponse> page = new PageImpl<>(List.of(
                new CardResponse(1L, "**** 1234", "John Doe", "ACTIVE", YearMonth.now().plusYears(1), BigDecimal.valueOf(1000))
        ), PageRequest.of(0, 10), 1);

        Mockito.when(cardService.getAllCards(any(CardFilter.class), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/cards")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].maskedCardNumber").value("**** 1234"))
                .andExpect(jsonPath("$.content[0].ownerName").value("John Doe"));
    }

    @Test
    void blockCardShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/admin/cards/{cardId}/block", CARD_ID))
                .andExpect(status().isOk());

        Mockito.verify(cardService).blockCard(CARD_ID);
    }

    @Test
    void activateCardShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/admin/cards/{cardId}/activate", CARD_ID))
                .andExpect(status().isOk());

        Mockito.verify(cardService).activateCard(CARD_ID);
    }

    @Test
    void deleteCardShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/cards/{cardId}", CARD_ID))
                .andExpect(status().isNoContent());

        Mockito.verify(cardService).deleteCard(CARD_ID);
    }
}