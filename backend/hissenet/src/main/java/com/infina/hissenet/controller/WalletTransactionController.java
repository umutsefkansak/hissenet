package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.WalletTransactionControllerDoc;
import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import com.infina.hissenet.service.WalletTransactionService;
import com.infina.hissenet.utils.MessageUtils;
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
        ApiResponse<WalletTransactionResponse> response = ApiResponse.created(MessageUtils.getMessage("wallet.transaction.create.success"),
                walletTransactionService.createWalletTransaction(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{transactionId}")
    public ApiResponse<WalletTransactionResponse> getTransactionById(@PathVariable Long transactionId){
       return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.retrieve.success"), walletTransactionService.getTransactionById(transactionId));
    }
    @GetMapping("/wallet/{walletId}/history")
    public ApiResponse<List<WalletTransactionResponse>> getTransactionHistory(@PathVariable Long walletId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.history.success"), walletTransactionService.getTransactionHistory(walletId));
    }
    @GetMapping
    public ApiResponse<Page<WalletTransactionResponse>> getAllTransactions(Pageable pageable){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.all.retrieve.success"), walletTransactionService.getAllTransactions(pageable));
    }
    @PutMapping("/{transactionId}")
    public ApiResponse<WalletTransactionResponse> updateWalletTransaction(@PathVariable Long transactionId,
                                                                          @Valid @RequestBody UpdateWalletTransactionRequest request){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.update.success"), walletTransactionService.updateWalletTransaction(transactionId, request));
    }
    @PostMapping("/{transactionId}/complete")
    public ApiResponse<String> completeTransaction(@PathVariable Long transactionId){
        walletTransactionService.completeTransaction(transactionId);
        return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.complete.success"));
    }
    @PostMapping("/{transactionId}/cancel")
    public ApiResponse<String> cancelTransaction(@PathVariable Long transactionId){
        walletTransactionService.cancelTransaction(transactionId);
        return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.cancel.success"));
    }
    @DeleteMapping("{transactionId}")
    public ApiResponse<String> deleteTransactionById(@PathVariable Long transactionId){
        walletTransactionService.deleteById(transactionId);
        return ApiResponse.ok(MessageUtils.getMessage("wallet.transaction.delete.success"));
    }



}
