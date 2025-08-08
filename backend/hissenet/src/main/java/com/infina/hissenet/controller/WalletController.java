package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.WalletControllerDoc;
import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.enums.TransactionType;
import com.infina.hissenet.service.WalletService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController implements WalletControllerDoc{

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(@Valid @RequestBody CreateWalletRequest request){
        ApiResponse<WalletResponse> response = ApiResponse.created(MessageUtils.getMessage("wallet.create.success"),
                walletService.createWallet(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/customer/{customerId}")
    public ApiResponse<WalletResponse> getWalletByCustomerId(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.retrieve.success"), walletService.getWalletByCustomerId(customerId));
    }
    @GetMapping("/customer/{customerId}/balance")
    public ApiResponse<BigDecimal> getWalletBalance(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.balance.retrieve.success"), walletService.getWalletBalance(customerId));
    }
    @PutMapping("/customer/{customerId}/limits")
    public ApiResponse<WalletResponse> updateWalletLimits(@PathVariable Long customerId, @Valid @RequestBody UpdateWalletRequest request){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.limits.update.success"), walletService.updateWalletLimits(customerId, request));
    }
    //balance processes
    @PostMapping("/customer/{customerId}/add-balance")
    public ApiResponse<WalletResponse> addBalance(@PathVariable Long customerId, @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive") BigDecimal amount, @RequestParam TransactionType transactionType){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.balance.add.success"), walletService.addBalance(customerId, amount, transactionType));
    }
    @PostMapping("/customer/{customerId}/subtract-balance")
    public ApiResponse<WalletResponse> subtractBalance(@PathVariable Long customerId, @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive") BigDecimal amount, @RequestParam TransactionType transactionType){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.balance.subtract.success"), walletService.subtractBalance(customerId,amount, transactionType));
    }
    @PostMapping("/customer/{customerId}/stock/purchase")
    public ApiResponse<WalletResponse> processStockPurchase(@PathVariable Long customerId, @RequestParam BigDecimal totalAmount, @RequestParam BigDecimal commission){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.stock.purchase.success"),
                walletService.processStockPurchase(customerId, totalAmount, commission));
    }
    @PostMapping("/customer/{customerId}/stock/sale")
    public ApiResponse<WalletResponse> processStockSale(@PathVariable Long customerId, @RequestParam BigDecimal totalAmount, @RequestParam BigDecimal commission){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.stock.sale.success"), walletService.processStockSale(customerId, totalAmount, commission));
    }
    @PostMapping("/customer/{customerId}/deposit")
    public ApiResponse<WalletResponse> processDeposit(@PathVariable Long customerId, @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive") BigDecimal amount){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.deposit.success"), walletService.processDeposit(customerId, amount));
    }
    @PostMapping("/customer/{customerId}/withdrawal")
    public ApiResponse<WalletResponse> processWithdrawal(@PathVariable Long customerId, @RequestParam @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive") BigDecimal amount){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.withdrawal.success"), walletService.processWithdrawal(customerId, amount));
    }
    @PostMapping("/customer/{customerId}/lock")
    public ApiResponse<WalletResponse> lockWallet(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.lock.success"), walletService.lockWallet(customerId));
    }
    @PostMapping("/customer/{customerId}/unlock")
    public ApiResponse<WalletResponse> unlockWallet(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.unlock.success"), walletService.unlockWallet(customerId));
    }
    @PostMapping("/customer/{customerId}/reset-daily-limits")
    public ApiResponse<WalletResponse> resetDailyLimits(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.limits.daily.reset.success"), walletService.resetDailyLimits(customerId));
    }
    @PostMapping("/customer/{customerId}/reset-monthly-limits")
    public ApiResponse<WalletResponse> resetMonthlyLimits(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.limits.monthly.reset.success"), walletService.resetMonthlyLimits(customerId));
    }
    @DeleteMapping("/{walletId}")
    public ApiResponse<String> deleteWalletById(@PathVariable Long walletId) {
        walletService.deleteById(walletId);
        return ApiResponse.ok(MessageUtils.getMessage("wallet.delete.success"));
    }
    @GetMapping("/customer/{customerId}/available-balance")
    public ApiResponse<BigDecimal> getAvailableBalance(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.available.balance.success"), walletService.getAvailableBalance(customerId));
    }

    @GetMapping("/customer/{customerId}/blocked-balance")
    public ApiResponse<BigDecimal> getBlockedBalance(@PathVariable Long customerId){
        return ApiResponse.ok(MessageUtils.getMessage("wallet.blocked.balance.success"), walletService.getBlockedBalance(customerId));
    }
}
