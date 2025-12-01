package com.goylik.bankcards.controller;

import com.goylik.bankcards.dto.request.InternalTransferRequest;
import com.goylik.bankcards.dto.response.TransactionResponse;
import com.goylik.bankcards.security.jwt.JwtAuthFilter;
import com.goylik.bankcards.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    protected JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private TransactionService transactionService;

    private static final Long FROM_CARD_ID = 1L;
    private static final Long TO_CARD_ID = 2L;

    @Test
    void makeInternalTransferShouldReturnTransactionResponse() throws Exception {
        InternalTransferRequest request = new InternalTransferRequest(FROM_CARD_ID, TO_CARD_ID, BigDecimal.valueOf(500));

        TransactionResponse response = new TransactionResponse(
                1L,
                "**** 1111",
                "**** 2222",
                BigDecimal.valueOf(500),
                LocalDateTime.now()
        );

        Mockito.when(transactionService.makeInternalTransfer(any(InternalTransferRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/user/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.maskedCardNumberFrom").value("**** 1111"))
                .andExpect(jsonPath("$.maskedCardNumberTo").value("**** 2222"))
                .andExpect(jsonPath("$.amount").value(500));
    }

    @Test
    void makeInternalTransferShouldReturnBadRequestWhenAmountIsNegative() throws Exception {
        InternalTransferRequest request = new InternalTransferRequest(FROM_CARD_ID, TO_CARD_ID, BigDecimal.valueOf(-100));

        mockMvc.perform(post("/api/user/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
