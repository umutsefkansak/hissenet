package com.infina.hissenet.controller;

import com.infina.hissenet.dto.request.StockData;
import com.infina.hissenet.service.abstracts.IStockCacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cache/stocks")
public class StockCacheController {

    private final IStockCacheService stockCacheService;

    public StockCacheController(IStockCacheService stockCacheService) {
        this.stockCacheService = stockCacheService;
    }

    @GetMapping
    public ResponseEntity<List<StockData>> getAllCachedStocks() {
        return ResponseEntity.ok(stockCacheService.getCachedStocks());
    }

    @GetMapping("/{code}/price")
    public ResponseEntity<BigDecimal> getPriceByCode(@PathVariable String code) {
        BigDecimal price = stockCacheService.getPriceByCodeOrNull(code);
        if (price != null) {
            return ResponseEntity.ok(price);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
