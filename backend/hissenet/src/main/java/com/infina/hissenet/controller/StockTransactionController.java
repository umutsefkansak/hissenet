package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
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

    // Order emri üzerine stock transaction oluşturma
    @PostMapping("/order")
    public ApiResponse<StockTransactionResponse> createTransactionFromOrder(@RequestBody StockTransactionCreateRequest request) {
        return ApiResponse.created("Transaction created from order successfully", stockTransactionService.createTransactionFromOrder(request));
    }

    // Temettü işlemi oluşturma
    @PostMapping("/dividend")
    public ApiResponse<StockTransactionResponse> createDividendTransaction(@RequestBody StockTransactionCreateRequest request) {
        return ApiResponse.created("Dividend transaction created successfully", stockTransactionService.createDividendTransaction(request));
    }

    // Belirli bir portföye ait tüm işlemler
    @GetMapping("/portfolio/{portfolioId}")
    public ApiResponse<List<StockTransactionResponse>> getTransactionsByPortfolioId(@PathVariable Long portfolioId) {
        return ApiResponse.ok("Transactions for portfolio listed successfully", stockTransactionService.getTransactionsByPortfolioId(portfolioId));
    }

    // Belirli bir hisseye ait tüm işlemler
    @GetMapping("/stock/{stockId}")
    public ApiResponse<List<StockTransactionResponse>> getTransactionsByStockId(@PathVariable Long stockId) {
        return ApiResponse.ok("Transactions for stock listed successfully", stockTransactionService.getTransactionsByStockId(stockId));
    }

    // Belirli bir emire ait tüm işlemler
    @GetMapping("/order/{orderId}")
    public ApiResponse<List<StockTransactionResponse>> getTransactionsByOrderId(@PathVariable Long orderId) {
        return ApiResponse.ok("Transactions for order listed successfully", stockTransactionService.getTransactionsByOrderId(orderId));
    }

    // Belirtilen tarih aralığındaki işlemler
    @GetMapping("/date-range")
    public ApiResponse<List<StockTransactionResponse>> getTransactionsByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ApiResponse.ok("Transactions in date range listed successfully", stockTransactionService.getTransactionsByDateRange(start, end));
    }

    // Belirli bir işlem türüne göre işlemler
    @GetMapping("/type/{transactionType}")
    public ApiResponse<List<StockTransactionResponse>> getTransactionsByType(@PathVariable String transactionType) {
        return ApiResponse.ok("Transactions by type listed successfully", stockTransactionService.getTransactionsByType(transactionType));
    }
}
