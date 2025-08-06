package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.WalletTransactionControllerDoc;
import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import com.infina.hissenet.service.WalletTransactionService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet-transactions")
public class WalletTransactionController implements WalletTransactionControllerDoc {

    private final WalletTransactionService walletTransactionService;


    public WalletTransactionController(WalletTransactionService walletTransactionService) {
        this.walletTransactionService = walletTransactionService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<WalletTransactionResponse>> createWalletTransaction(@Valid @RequestBody CreateWalletTransactionRequest request){
        ApiResponse<WalletTransactionResponse> response = ApiResponse.created("Wallet transaction created successfully",
                walletTransactionService.createWalletTransaction(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{transactionId}")
    public ApiResponse<WalletTransactionResponse> getTransactionById(@PathVariable Long transactionId){
       return ApiResponse.ok("Transaction retrieved succesfully", walletTransactionService.getTransactionById(transactionId));
    }
    @GetMapping("/wallet/{walletId}/history")
    public ApiResponse<List<WalletTransactionResponse>> getTransactionHistory(@PathVariable Long walletId){
        return ApiResponse.ok("Transaction history retrieved successfully", walletTransactionService.getTransactionHistory(walletId));
    }
    @GetMapping
    public ApiResponse<Page<WalletTransactionResponse>> getAllTransactions(Pageable pageable){
        return ApiResponse.ok("All transactions retrieved successfully", walletTransactionService.getAllTransactions(pageable));
    }
    @PutMapping("/{transactionId}")
    public ApiResponse<WalletTransactionResponse> updateWalletTransaction(@PathVariable Long transactionId,
                                                                          @Valid @RequestBody UpdateWalletTransactionRequest request){
        return ApiResponse.ok("Transaction updated successfully", walletTransactionService.updateWalletTransaction(transactionId, request));
    }
    @PostMapping("/{transactionId}/complete")
    public ApiResponse<String> completeTransaction(@PathVariable Long transactionId){
        walletTransactionService.completeTransaction(transactionId);
        return ApiResponse.ok("Transaction completed successfully");
    }
    @PostMapping("/{transactionId}/cancel")
    public ApiResponse<String> cancelTransaction(@PathVariable Long transactionId){
        walletTransactionService.cancelTransaction(transactionId);
        return ApiResponse.ok("Transaction canceled successfully");
    }
    @DeleteMapping("{transactionId}")
    public ApiResponse<String> deleteTransactionById(@PathVariable Long transactionId){
        walletTransactionService.deleteById(transactionId);
        return ApiResponse.ok("Transaction deleted successfully");
    }



}
