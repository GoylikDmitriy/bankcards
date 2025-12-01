package com.goylik.bankcards.exception.card;

public class DuplicateBlockRequestException extends RuntimeException {
    public DuplicateBlockRequestException() {
    }

    public DuplicateBlockRequestException(String message) {
        super(message);
    }

    public DuplicateBlockRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateBlockRequestException(Throwable cause) {
        super(cause);
    }
}
