package com.goylik.bankcards.exception.card;

public class InvalidRequestStatusException extends RuntimeException {
    public InvalidRequestStatusException() {
    }

    public InvalidRequestStatusException(String message) {
        super(message);
    }

    public InvalidRequestStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestStatusException(Throwable cause) {
        super(cause);
    }
}
