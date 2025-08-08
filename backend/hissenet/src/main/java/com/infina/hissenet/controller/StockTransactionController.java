package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.StockTransactionControllerDoc;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.service.StockTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-transactions")
public class StockTransactionController implements StockTransactionControllerDoc {
    private final StockTransactionService stockTransactionService;

    public StockTransactionController(StockTransactionService stockTransactionService) {
        this.stockTransactionService = stockTransactionService;
    }

    @Override
    @GetMapping("/buylist/{portfolioId}")
    public ApiResponse<List<StockTransactionResponse>> getStockTransactions(@PathVariable Long portfolioId) {
        return ApiResponse.ok("Satın Alınan Hisseler",stockTransactionService.getAllBuyTransactions(portfolioId));
    }
    
    @Override
    @PatchMapping("/{transactionId}/{portfolioId}")
    public ApiResponse<Void> updatePortfolio(@PathVariable Long transactionId,@PathVariable Long portfolioId) {
        stockTransactionService.updatePortfolioIdForStockTransactions(transactionId,portfolioId);
        return ApiResponse.ok("hisse yeni pörtföye taşındı");
    }
    @GetMapping("/quantity/{cursomerId}/{stockCode}")
    public ApiResponse<Integer> getQuantityForStockTransaction(@PathVariable Long cursomerId,@PathVariable String stockCode) {
        return ApiResponse.ok(stockCode+ "ait hisse sayısı",stockTransactionService.getQuantityForStockTransactionWithStream(cursomerId,stockCode));
    }
    @GetMapping("/list/{customerId}/{stockCode}/{quantity}")
    public List<StockTransactionResponse> list(@PathVariable Long customerId, @PathVariable String stockCode,@PathVariable int quantity){
        return stockTransactionService.list(customerId,stockCode,quantity);
    }
}
