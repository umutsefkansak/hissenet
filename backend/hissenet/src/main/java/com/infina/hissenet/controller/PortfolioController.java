package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService service;

    public PortfolioController(PortfolioService portfolioService) {
        this.service = portfolioService;
    }
    
    // yeni portfolio olustur
    @PostMapping("/{customerId}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(@Valid @RequestBody PortfolioCreateRequest request, @PathVariable Long customerId) {
        ApiResponse<PortfolioResponse> response = ApiResponse.
                created("Portfolio created successfully",
                        service.createPortfolio(request,customerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // portfolio güncelle
    @PutMapping("/{id}")
    public ApiResponse<PortfolioResponse> updatePortfolio(@PathVariable Long id, @Valid @RequestBody PortfolioUpdateRequest request) {
        return ApiResponse.ok("Portfolio updated successfully", service.updatePortfolio(id, request));
    }
    
    // müşterinin portfolio listesini getir
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<PortfolioSummaryResponse>> getPortfoliosByCustomer(@PathVariable Long customerId) {
        return ApiResponse.ok("Customer portfolios retrieved successfully", service.getPortfoliosByCustomer(customerId));
    }
    
    // portfolio sil
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePortfolio(@PathVariable Long id) {
        service.deletePortfolio(id);
        return ApiResponse.ok("Portfolio deleted successfully");
    }
    
    // portföy değeri güncelle
    @PatchMapping("/{id}/values")
    public ApiResponse<PortfolioResponse> updatePortfolioValues(@PathVariable Long id) {
        return ApiResponse.ok("Portfolio values updated successfully", service.updatePortfolioValues(id));
    }
    
    // Aktif portföyler
    @GetMapping("/active")
    public ApiResponse<List<PortfolioSummaryResponse>> getActivePortfolios() {
        return ApiResponse.ok("Active portfolios retrieved successfully", service.getActivePortfolios());
    }
    
    // Tek portföy getir
    @GetMapping("/{id}")
    public ApiResponse<PortfolioResponse> getPortfolio(@PathVariable Long id) {
        return ApiResponse.ok("Portfolio retrieved successfully", service.getPortfolioResponse(id));
    }
}
