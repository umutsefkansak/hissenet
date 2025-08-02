package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.enums.TransactionType;

import java.math.BigDecimal;


/**
 * Service interface for wallet operations.
 * Handles creation, update, retrieval, and wallet management operations.
 */
public interface IWalletService {
    /**
     * Creates a new wallet for a customer.
     *
     * @param request wallet creation data
     * @return created wallet details
     */
    WalletResponse createWallet(CreateWalletRequest request);

    /**
     * Retrieves wallet information by customer ID.
     *
     * @param customerId customer identifier
     * @return wallet details
     */
    WalletResponse getWalletByCustomerId(Long customerId);

    /**
     * Adds balance to a customer's wallet.
     *
     * @param customerId customer identifier
     * @param amount amount to add
     * @param transactionType type of transaction
     * @return updated wallet details
     */
    WalletResponse addBalance(Long customerId, BigDecimal amount, TransactionType transactionType);

    /**
     * Subtracts balance from a customer's wallet.
     *
     * @param customerId customer identifier
     * @param amount amount to subtract
     * @param transactionType type of transaction
     * @return updated wallet details
     */
    WalletResponse subtractBalance(Long customerId, BigDecimal amount, TransactionType transactionType);

    /**
     * Processes a stock purchase transaction.
     *
     * @param customerId customer identifier
     * @param totalAmount total stock amount
     * @param commission commission amount
     * @return updated wallet details
     */
    WalletResponse processStockPurchase(Long customerId, BigDecimal totalAmount, BigDecimal commission);

    /**
     * Processes a stock sale transaction.
     *
     * @param customerId customer identifier
     * @param totalAmount total stock amount
     * @param commission commission amount
     * @return updated wallet details
     */
    WalletResponse processStockSale(Long customerId, BigDecimal totalAmount, BigDecimal commission);


    /**
     * Processes a withdrawal from the wallet.
     *
     * @param customerId customer identifier
     * @param amount withdrawal amount
     * @return updated wallet details
     */
    WalletResponse processWithdrawal(Long customerId, BigDecimal amount);

    /**
     * Processes a deposit to the wallet.
     *
     * @param customerId customer identifier
     * @param amount deposit amount
     * @return updated wallet details
     */
    WalletResponse processDeposit(Long customerId, BigDecimal amount);

    /**
     * Locks a customer's wallet.
     *
     * @param customerId customer identifier
     * @return updated wallet details
     */
    WalletResponse lockWallet(Long customerId);

    /**
     * Unlocks a customer's wallet.
     *
     * @param customerId customer identifier
     * @return updated wallet details
     */
    WalletResponse unlockWallet(Long customerId);

    /**
     * Resets daily transaction limits for a wallet.
     *
     * @param customerId customer identifier
     * @return updated wallet details
     */
    WalletResponse resetDailyLimits(Long customerId);

    /**
     * Resets monthly transaction limits for a wallet.
     *
     * @param customerId customer identifier
     * @return updated wallet details
     */
    WalletResponse resetMonthlyLimits(Long customerId);

    /**
     * Checks if a transaction can be performed for a customer.
     *
     * @param customerId customer identifier
     * @param amount transaction amount
     * @return true if transaction can be performed, false otherwise
     */
    boolean canPerformTransaction(Long customerId, BigDecimal amount);

    /**
     * Gets the current balance of a customer's wallet.
     *
     * @param customerId customer identifier
     * @return wallet balance
     */
    BigDecimal getWalletBalance(Long customerId);

    /**
     * Updates wallet limits and settings.
     *
     * @param customerId customer identifier
     * @param request update request containing new limits
     * @return updated wallet details
     */
    WalletResponse updateWalletLimits(Long customerId, UpdateWalletRequest request);


}
