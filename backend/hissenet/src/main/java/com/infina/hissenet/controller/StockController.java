package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.StockCreateRequest;
import com.infina.hissenet.dto.request.StockUpdateRequest;
import com.infina.hissenet.dto.response.StockResponse;
import com.infina.hissenet.service.abstracts.IStockService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final IStockService stockService;

    public StockController(IStockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> createStock(@Valid @RequestBody StockCreateRequest request) {
        StockResponse response = stockService.createStock(request);
        ApiResponse<StockResponse> apiResponse = ApiResponse.created(MessageUtils.getMessage("stock.created.successfully"),  response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<StockResponse> getStockById(@PathVariable Long id) {
        StockResponse response = stockService.getStock(id);
        return ApiResponse.ok(MessageUtils.getMessage("stock.fetched.successfully"),  response);
    }

    @GetMapping
    public ApiResponse<List<StockResponse>> listStocks() {
        List<StockResponse> list = stockService.getAllStocks();
        return ApiResponse.ok(MessageUtils.getMessage("stock.listed.successfully"),  list);
    }

    @PutMapping("/{id}")
    public ApiResponse<StockResponse> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        StockResponse response = stockService.updateStock(id, request);
        return ApiResponse.ok( MessageUtils.getMessage("stock.updated.successfully"), response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ApiResponse.ok(MessageUtils.getMessage("stock.deleted.successfully"));
    }
}
