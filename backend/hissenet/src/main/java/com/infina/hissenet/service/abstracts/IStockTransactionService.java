package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface IStockTransactionService {
    StockTransactionResponse createTransactionFromOrder(StockTransactionCreateRequest request);
    StockTransactionResponse createDividendTransaction(StockTransactionCreateRequest request);
    List<StockTransactionResponse> getTransactionsByPortfolioId(Long portfolioId);
    List<StockTransactionResponse> getTransactionsByStockId(Long stockId);
    List<StockTransactionResponse> getTransactionsByOrderId(Long orderId);
    List<StockTransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<StockTransactionResponse> getTransactionsByType(String transactionType);
}
