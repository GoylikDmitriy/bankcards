package com.goylik.bankcards.controller.advice;

import com.goylik.bankcards.dto.response.ErrorResponse;
import com.goylik.bankcards.exception.transaction.CardOwnershipMismatchException;
import com.goylik.bankcards.exception.transaction.InsufficientBalanceException;
import com.goylik.bankcards.exception.transaction.SameCardTransferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TransactionControllerAdvice {
    @ExceptionHandler(CardOwnershipMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCardOwnershipMismatchException(CardOwnershipMismatchException e) {
        log.warn("Cards have different users: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientBalanceException(InsufficientBalanceException e) {
        log.warn("Card doesn't have enough balance to make operation: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(SameCardTransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSameCardTransferException(SameCardTransferException e) {
        log.warn("Can't make operation for the same cards: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
