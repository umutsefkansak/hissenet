package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IWalletTransactionService {
    /**
     * Creates a new wallet transaction.
     *
     * @param request wallet transaction creation data
     * @return created wallet transaction details
     */
    WalletTransactionResponse createWalletTransaction(CreateWalletTransactionRequest request);

    /**
     * Updates an existing wallet transaction.
     *
     * @param walletTransactionId transaction identifier
     * @param updateWalletTransactionRequest wallet transaction update data
     * @return updated wallet transaction details
     */
    WalletTransactionResponse updateWalletTransaction(Long walletTransactionId, UpdateWalletTransactionRequest updateWalletTransactionRequest);

    /**
     * Completes a wallet transaction.
     *
     * @param transactionId transaction identifier
     * @param finalBalance final balance after transaction
     */
    void completeTransaction(Long transactionId, BigDecimal finalBalance);

    /**
     * Cancels a wallet transaction.
     *
     * @param transactionId transaction identifier
     */
    void cancelTransaction(Long transactionId);

    /**
     * Gets transaction history for a specific wallet.
     *
     * @param walletId wallet identifier
     * @return list of wallet transactions
     */
    List<WalletTransactionResponse> getTransactionHistory(Long walletId);

    /**
     * Lists all wallet transactions with pagination.
     *
     * @param pageable pagination parameters
     * @return page of wallet transactions
     */
    Page<WalletTransactionResponse> getAllTransactions(Pageable pageable);

    /**
     * Retrieves a wallet transaction by ID.
     *
     * @param transactionId transaction identifier
     * @return wallet transaction details
     */
    WalletTransactionResponse getTransactionById(Long transactionId);
}
