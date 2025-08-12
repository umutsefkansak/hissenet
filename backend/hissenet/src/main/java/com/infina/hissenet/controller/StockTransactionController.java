package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.StockTransactionControllerDoc;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.service.StockTransactionService;
import com.infina.hissenet.utils.MessageUtils;
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
        return ApiResponse.ok(MessageUtils.getMessage("stock.transaction.buylist.success"),stockTransactionService.getAllBuyTransactions(portfolioId));
    }
    
    @Override
    @PatchMapping("/{transactionId}/{portfolioId}")
    public ApiResponse<Void> updatePortfolio(@PathVariable Long transactionId,@PathVariable Long portfolioId) {
        stockTransactionService.updatePortfolioIdForStockTransactions(transactionId,portfolioId);
        return ApiResponse.ok(MessageUtils.getMessage("stock.transaction.moved.success"));
    }
    @GetMapping("/quantity/{cursomerId}/{stockCode}")
    public ApiResponse<Integer> getQuantityForStockTransaction(@PathVariable Long cursomerId,@PathVariable String stockCode) {
        return ApiResponse.ok(MessageUtils.getMessage("stock.transaction.quantity.success", stockCode),stockTransactionService.getQuantityForStockTransactionWithStream(cursomerId,stockCode));
    }
    @GetMapping("/stock-size/{cursomerId}")
    public ApiResponse<Integer> getStockSizeForStockTransaction(@PathVariable Long cursomerId) {
        return ApiResponse.ok("hisse sayısı",stockTransactionService.getTotalStock(cursomerId));
    }

}
