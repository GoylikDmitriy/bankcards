package com.goylik.bankcards.util;

import lombok.Setter;

import java.util.Random;

public final class CardNumberUtils {
    @Setter
    private static String bankBIN;

    private CardNumberUtils() {}

    public static boolean validate(String cardNumber) {
        return cardNumber != null
                && cardNumber.matches("\\d{16}")
                && calculateLuhnSum(cardNumber) % 10 == 0;
    }

    public static String getMaskedCardNumber(String last4) {
        return "**** **** **** " + last4;
    }

    public static String getLast4(String cardNumber) {
        return cardNumber.substring(cardNumber.length() - 4);
    }

    public static String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder(bankBIN);

        int cardNumberLength = 16;
        int remainingDigits = cardNumberLength - bankBIN.length() - 1;
        for (int i = 0; i < remainingDigits; i++) {
            cardNumber.append(random.nextInt(10));
        }

        int dummy = 0;
        cardNumber.append(dummy);
        int sum = calculateLuhnSum(cardNumber.toString());

        int lastDigit = (10 - (sum % 10)) % 10;
        cardNumber.deleteCharAt(cardNumber.length() - 1).append(lastDigit);
        return cardNumber.toString();
    }

    private static int calculateLuhnSum(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = cardNumber.charAt(i) - '0';
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum;
    }
}
