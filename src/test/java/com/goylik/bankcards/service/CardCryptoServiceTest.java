package com.goylik.bankcards.service;

import com.goylik.bankcards.exception.card.CardCryptoException;
import com.goylik.bankcards.service.impl.CardCryptoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardCryptoServiceTest {

    @InjectMocks
    private CardCryptoServiceImpl cryptoService;

    private static final byte[] VALID_KEY = new byte[32];

    @BeforeEach
    void setUp() {
        for (int i = 0; i < VALID_KEY.length; i++) VALID_KEY[i] = (byte) i;

        String base64Key = Base64.getEncoder().encodeToString(VALID_KEY);
        ReflectionTestUtils.setField(cryptoService, "base64Key", base64Key);

        cryptoService.init();
        assertNotNull(ReflectionTestUtils.getField(cryptoService, "secretKey"));
    }

    @Test
    void encryptAndDecryptShouldReturnOriginalCardNumber() {
        String cardNumber = "1234567812345678";

        String encrypted = cryptoService.encrypt(cardNumber);
        assertNotNull(encrypted);
        assertNotEquals(cardNumber, encrypted);

        String decrypted = cryptoService.decrypt(encrypted);
        assertEquals(cardNumber, decrypted);
    }

    @Test
    void decryptWithInvalidDataShouldThrowException() {
        String invalidData = "shortdata";

        CardCryptoException ex = assertThrows(CardCryptoException.class,
                () -> cryptoService.decrypt(invalidData));
        assertTrue(ex.getMessage().contains("Failed to decrypt card number")
                || ex.getMessage().contains("Invalid encrypted data format"));
    }

    @Test
    void initWithInvalidKeyLengthShouldThrowException() {
        CardCryptoServiceImpl invalidService = new CardCryptoServiceImpl();
        ReflectionTestUtils.setField(invalidService, "base64Key", Base64.getEncoder().encodeToString(new byte[10]));

        IllegalStateException ex = assertThrows(IllegalStateException.class, invalidService::init);
        assertTrue(ex.getMessage().contains("Encryption key must be 16, 24 or 32 bytes"));
    }
}