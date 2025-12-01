package com.goylik.bankcards.exception.transaction;

public class SameCardTransferException extends RuntimeException {
    public SameCardTransferException() {
    }

    public SameCardTransferException(String message) {
        super(message);
    }

    public SameCardTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameCardTransferException(Throwable cause) {
        super(cause);
    }
}
