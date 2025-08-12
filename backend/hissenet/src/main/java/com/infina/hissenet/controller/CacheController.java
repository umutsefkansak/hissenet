package com.infina.hissenet.controller;

import com.infina.hissenet.controller.doc.CacheControllerDoc;
import com.infina.hissenet.dto.response.CombinedStockData;
import com.infina.hissenet.service.CacheManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/cache")
public class CacheController implements CacheControllerDoc {
    private final CacheManagerService cacheService;

    public CacheController(CacheManagerService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/combined/{code}")
    public ResponseEntity<CombinedStockData> getOne(@PathVariable String code) {
        if (!StringUtils.hasText(code)) {
            return ResponseEntity.notFound().build();
        }
        CombinedStockData data = cacheService.getCachedByCode(code);
        return data != null
                ? ResponseEntity.ok(data)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/combined")
    public ResponseEntity<List<CombinedStockData>> getAll() {
        List<CombinedStockData> all = cacheService.getAllCached();
        return ResponseEntity.ok(all);
    }
}

