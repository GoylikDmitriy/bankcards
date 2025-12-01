package com.goylik.bankcards.config;

import com.goylik.bankcards.util.CardNumberUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardUtilsConfig {
    @Value("${app.bank-card.bank-bin}")
    private String bankBIN;

    @PostConstruct
    public void init() {
        CardNumberUtils.setBankBIN(bankBIN);
    }
}
