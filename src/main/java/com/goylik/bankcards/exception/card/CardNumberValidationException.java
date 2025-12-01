package com.goylik.bankcards.exception.card;

public class CardNumberValidationException extends RuntimeException {
    public CardNumberValidationException() {
    }

    public CardNumberValidationException(String message) {
        super(message);
    }

    public CardNumberValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardNumberValidationException(Throwable cause) {
        super(cause);
    }
}
