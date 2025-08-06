package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.StockTransactionControllerDoc;
import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.service.StockTransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-transactions")
public class StockTransactionController {
    private final StockTransactionService stockTransactionService;

    public StockTransactionController(StockTransactionService stockTransactionService) {
        this.stockTransactionService = stockTransactionService;
    }

    @GetMapping("/buylist/{portfolioId}")
    public ApiResponse<List<StockTransactionResponse>> getStockTransactions(@PathVariable Long portfolioId) {
        return ApiResponse.ok("Satın Alınan Hisseler",stockTransactionService.getAllBuyTransactions(portfolioId));
    }
    @PatchMapping("/{transactionId}/{portfolioId}")
    public ApiResponse<Void> updatePortfolio(@PathVariable Long transactionId,@PathVariable Long portfolioId) {
        stockTransactionService.updatePortfolioIdForStockTransactions(transactionId,portfolioId);
        return ApiResponse.ok("hisse yeni pörtföye taşındı");
    }


}
