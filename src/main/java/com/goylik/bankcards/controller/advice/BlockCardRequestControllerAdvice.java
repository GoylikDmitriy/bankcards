package com.goylik.bankcards.controller.advice;

import com.goylik.bankcards.dto.response.ErrorResponse;
import com.goylik.bankcards.exception.card.BlockCardRequestNotFound;
import com.goylik.bankcards.exception.card.DuplicateBlockRequestException;
import com.goylik.bankcards.exception.card.InvalidRequestStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BlockCardRequestControllerAdvice {
    @ExceptionHandler(BlockCardRequestNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBlockCardRequestNotFound(BlockCardRequestNotFound e) {
        log.warn("Block card request not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidRequestStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestStatusException(InvalidRequestStatusException e) {
        log.warn("The request status not valid for this request: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DuplicateBlockRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateBlockRequestException(DuplicateBlockRequestException e) {
        log.warn("Request to block a card is already exists: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
