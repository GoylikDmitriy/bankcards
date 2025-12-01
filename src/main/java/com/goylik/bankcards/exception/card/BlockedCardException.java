package com.goylik.bankcards.exception.card;

public class BlockedCardException extends RuntimeException {
    public BlockedCardException() {
    }

    public BlockedCardException(String message) {
        super(message);
    }

    public BlockedCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockedCardException(Throwable cause) {
        super(cause);
    }
}
