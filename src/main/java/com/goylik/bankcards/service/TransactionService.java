package com.goylik.bankcards.service;

import com.goylik.bankcards.dto.request.InternalTransferRequest;
import com.goylik.bankcards.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse makeInternalTransfer(InternalTransferRequest request);
}
