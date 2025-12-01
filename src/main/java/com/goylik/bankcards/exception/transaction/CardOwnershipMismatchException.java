package com.goylik.bankcards.exception.transaction;

public class CardOwnershipMismatchException extends RuntimeException {
    public CardOwnershipMismatchException() {
    }

    public CardOwnershipMismatchException(String message) {
        super(message);
    }

    public CardOwnershipMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardOwnershipMismatchException(Throwable cause) {
        super(cause);
    }
}
