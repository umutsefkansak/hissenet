package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.StockTransaction;

import java.util.List;

/**
 * Defines common financial operations that are shared across portfolio and transaction services.
 * Provides read/aggregation utilities for stock transactions and portfolio calculations.
 *
 * @author Furkan Can
 */
public interface ICommonFinancialService {

    /**
     * Returns merged BUY transactions for a given portfolio.
     * The result merges transactions by stock code and contains only active quantities.
     *
     * @param portfolioId portfolio identifier
     * @return list of merged stock transactions
     */
    List<StockTransactionResponse> getAllBuyTransactions(Long portfolioId);

    /**
     * Merges a list of transactions for the same stock into a single response,
     * calculating total quantity, average price, and cost components.
     *
     * @param transactions transactions for the same stock code
     * @return merged transaction response
     */
    StockTransactionResponse mergeTransactions(List<StockTransaction> transactions);

    /**
     * Calculates the net quantity for a given customer and stock code across all portfolios.
     * SELL transactions are subtracted from BUY transactions.
     *
     * @param customerId customer identifier
     * @param stockCode  stock code (e.g., THYAO)
     * @return net quantity
     */
    Integer getQuantityForStockTransactionWithStream(Long customerId, String stockCode);
}


