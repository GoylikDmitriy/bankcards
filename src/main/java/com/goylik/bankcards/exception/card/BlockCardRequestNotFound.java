package com.goylik.bankcards.exception.card;

public class BlockCardRequestNotFound extends RuntimeException {
    public BlockCardRequestNotFound() {
    }

    public BlockCardRequestNotFound(String message) {
        super(message);
    }

    public BlockCardRequestNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockCardRequestNotFound(Throwable cause) {
        super(cause);
    }
}
