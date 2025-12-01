package com.goylik.bankcards.service;

public interface CardCryptoService {
    public String encrypt(String cardNumber);
    public String decrypt(String encryptedCardNumber);
}
