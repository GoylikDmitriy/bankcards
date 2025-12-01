package com.goylik.bankcards.exception.card;

public class ExpiredCardException extends RuntimeException {
    public ExpiredCardException() {
    }

    public ExpiredCardException(String message) {
        super(message);
    }

    public ExpiredCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredCardException(Throwable cause) {
        super(cause);
    }
}
