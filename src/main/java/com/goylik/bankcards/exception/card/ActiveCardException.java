package com.goylik.bankcards.exception.card;

public class ActiveCardException extends RuntimeException {
    public ActiveCardException() {
    }

    public ActiveCardException(String message) {
        super(message);
    }

    public ActiveCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActiveCardException(Throwable cause) {
        super(cause);
    }
}
