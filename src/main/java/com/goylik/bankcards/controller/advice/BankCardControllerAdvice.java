package com.goylik.bankcards.controller.advice;

import com.goylik.bankcards.dto.response.ErrorResponse;
import com.goylik.bankcards.exception.card.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BankCardControllerAdvice {
    @ExceptionHandler(CardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCardNotFoundException(CardNotFoundException e) {
        log.warn("Card not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ExpiredCardException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExpiredCardException(ExpiredCardException e) {
        log.warn("Can't operate with expired card: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BlockedCardException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBlockedCardException(BlockedCardException e) {
        log.warn("Can't operate with block card: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ActiveCardException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleActiveCardException(ActiveCardException e) {
        log.warn("Can't operate with active card: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CardCryptoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCardCryptoException(CardCryptoException e) {
        log.warn("Card crypto error: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CardNumberValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCardNumberValidationException(CardNumberValidationException e) {
        log.warn("Card number is not valid: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
