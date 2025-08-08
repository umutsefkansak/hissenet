package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.StockPriceCreateRequest;
import com.infina.hissenet.dto.request.StockPriceUpdateRequest;
import com.infina.hissenet.dto.response.StockPriceResponse;
import com.infina.hissenet.service.abstracts.IStockPriceService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-prices")
public class StockPriceController {

    private final IStockPriceService stockPriceService;

    public StockPriceController(IStockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @PostMapping
    public ApiResponse<StockPriceResponse> createPrice(@Valid @RequestBody StockPriceCreateRequest request) {
        StockPriceResponse response = stockPriceService.createStockPrice(request);
        return ApiResponse.created(MessageUtils.getMessage("stock.price.created.successfully"), response);
    }

    @GetMapping("/{id}")
    public ApiResponse<StockPriceResponse> getPriceById(@PathVariable Long id) {
        StockPriceResponse response = stockPriceService.getStockPrice(id);
        return ApiResponse.ok(MessageUtils.getMessage("stock.price.fetched.successfully"),response);
    }

    @GetMapping
    public ApiResponse<List<StockPriceResponse>> listPrices() {
        List<StockPriceResponse> list = stockPriceService.getAllStockPrices();
        return ApiResponse.ok(MessageUtils.getMessage("stock.price.listed.successfully"), list);
    }

    @PutMapping("/{id}")
    public ApiResponse<StockPriceResponse> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody StockPriceUpdateRequest request
    ) {
        StockPriceResponse response = stockPriceService.updateStockPrice(id, request);
        return ApiResponse.ok(MessageUtils.getMessage("stock.price.updated.successfully"), response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePrice(@PathVariable Long id) {
        stockPriceService.deleteStockPrice(id);
        return ApiResponse.ok(MessageUtils.getMessage("stock.price.deleted.successfully"), null);
    }
}
