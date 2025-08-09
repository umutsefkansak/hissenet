package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.StockHistoryCreateRequest;
import com.infina.hissenet.dto.request.StockHistoryUpdateRequest;
import com.infina.hissenet.dto.response.StockHistoryResponse;
import com.infina.hissenet.service.abstracts.IStockHistoryService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-histories")
public class StockHistoryController {

    private final IStockHistoryService stockHistoryService;

    public StockHistoryController(IStockHistoryService stockHistoryService) {
        this.stockHistoryService = stockHistoryService;
    }

    @PostMapping
    public ApiResponse<StockHistoryResponse> createHistory(
            @Valid @RequestBody StockHistoryCreateRequest request
    ) {
        StockHistoryResponse response = stockHistoryService.createStockHistory(request);
        return ApiResponse.created( MessageUtils.getMessage("stock.history.created.successfully"), response);
    }

    @GetMapping("/{id}")
    public ApiResponse<StockHistoryResponse> getHistoryById(@PathVariable Long id) {
        StockHistoryResponse response = stockHistoryService.getStockHistory(id);
        return ApiResponse.ok(MessageUtils.getMessage("stock.history.fetched.successfully"), response);
    }

    @GetMapping
    public ApiResponse<List<StockHistoryResponse>> listHistories() {
        List<StockHistoryResponse> list = stockHistoryService.getAllStockHistories();
        return ApiResponse.ok(MessageUtils.getMessage("stock.history.listed.successfully"), list);
    }

    @PutMapping("/{id}")
    public ApiResponse<StockHistoryResponse> updateHistory(
            @PathVariable Long id,
            @Valid @RequestBody StockHistoryUpdateRequest request
    ) {
        StockHistoryResponse response = stockHistoryService.updateStockHistory(id, request);
        return ApiResponse.ok(MessageUtils.getMessage("stock.history.updated.successfully"), response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteHistory(@PathVariable Long id) {
        stockHistoryService.deleteStockHistory(id);
        return ApiResponse.ok(MessageUtils.getMessage("stock.history.deleted.successfully"), null);
    }
}