package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.WalletTransaction;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.exception.wallet.*;
import com.infina.hissenet.mapper.WalletMapper;
import com.infina.hissenet.repository.CustomerRepository;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.repository.WalletTransactionRepository;
import com.infina.hissenet.service.abstracts.IWalletService;
import com.infina.hissenet.utils.DateUtils;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.infina.hissenet.utils.DateUtils.calculateT2SettlementDate;


@Service
@Transactional
public class WalletService extends GenericServiceImpl<Wallet, Long> implements IWalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final CustomerRepository customerRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper, CustomerRepository customerRepository, WalletTransactionRepository walletTransactionRepository){
        super(walletRepository);
        this.walletRepository=walletRepository;
        this.walletMapper=walletMapper;
        this.walletTransactionRepository = walletTransactionRepository;
        this.customerRepository=customerRepository;
    }



    public WalletResponse createWallet(CreateWalletRequest request){
        Optional<Customer> optionalCustomer = customerRepository.findById(request.customerId());
        if (optionalCustomer.isEmpty()){
            throw new CustomerNotFoundException(request.customerId());
        }
        Optional<Wallet> existingWallet = walletRepository.findByCustomerId(request.customerId());

        if (existingWallet.isPresent()){
            throw new WalletAlreadyExistsException(request.customerId());
        }
        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCustomer(optionalCustomer.get());
        wallet.setLastResetDate(LocalDate.now());

        Wallet savedWallet = save(wallet);
        return walletMapper.toResponse(savedWallet);
    }
    public WalletResponse getWalletByCustomerId(Long customerId){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        return walletMapper.toResponse(wallet);
    }
    public WalletResponse addBalance(Long customerId, BigDecimal amount, TransactionType transactionType){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        validateWalletForTransaction(wallet);

        if (transactionType == TransactionType.STOCK_SALE) {
            wallet.addBalance(amount);
            wallet.blockBalance(amount);

            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.STOCK_SALE);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setSource("EXTERNAL");
            transaction.setDestination("WALLET");
            transaction.setSettlementDate(calculateT2SettlementDate(LocalDateTime.now())); // T+2 gün

            wallet.addTransaction(transaction);
        } else {
            // Normal işlem
            wallet.addBalance(amount);
            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(amount);
            transaction.setTransactionType(transactionType);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setSource("EXTERNAL");
            transaction.setDestination("WALLET");

            wallet.addTransaction(transaction);
        }

        updateTransactionTracking(wallet, amount, true);
        Wallet updateWallet = update(wallet);
        return walletMapper.toResponse(updateWallet);
    }
    public WalletResponse subtractBalance(Long customerId, BigDecimal amount, TransactionType transactionType){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        validateWalletForTransaction(wallet);
        validateSufficientBalance(wallet, amount);
        validateTransactionLimits(wallet, amount);
        if (transactionType == TransactionType.STOCK_PURCHASE) {
            wallet.blockBalance(amount);

            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.STOCK_PURCHASE);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setSource("WALLET");
            transaction.setDestination("EXTERNAL");
            transaction.setSettlementDate(calculateT2SettlementDate(LocalDateTime.now())); // T+2 gün

            wallet.addTransaction(transaction);
        } else {
            // Normal işlem
            wallet.subtractBalance(amount);
            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(amount);
            transaction.setTransactionType(transactionType);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setSource("WALLET");
            transaction.setDestination("EXTERNAL");

            wallet.addTransaction(transaction);
        }
        updateTransactionTracking(wallet, amount, false);
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    public WalletResponse processStockPurchase(Long customerId, BigDecimal totalAmount, BigDecimal commission){
        BigDecimal totalCost = totalAmount.add(commission);
        return subtractBalance(customerId, totalCost, TransactionType.STOCK_PURCHASE);
    }
    public WalletResponse processStockSale(Long customerId, BigDecimal totalAmount, BigDecimal commission){
        BigDecimal netAmount = totalAmount.subtract(commission);
        return addBalance(customerId, netAmount, TransactionType.STOCK_SALE);
    }
    @Transactional
    public void processT2Settlements() {
        List<WalletTransaction> transactionsReadyForSettlement = walletTransactionRepository.findTransactionsReadyForSettlement(
                LocalDateTime.now(), TransactionStatus.COMPLETED,
                TransactionType.STOCK_PURCHASE, TransactionType.STOCK_SALE
        );

        for (WalletTransaction transaction : transactionsReadyForSettlement) {
            processSettlement(transaction);
        }
    }

    private void processSettlement(WalletTransaction transaction) {
        Wallet wallet = transaction.getWallet();
        if (transaction.getTransactionType() == TransactionType.STOCK_PURCHASE) {

            wallet.transferBlockedToBalance(transaction.getAmount());
        } else if (transaction.getTransactionType() == TransactionType.STOCK_SALE) {
            wallet.unblockBalance(transaction.getAmount());
        }

        transaction.setTransactionStatus(TransactionStatus.SETTLED);
        transaction.setSettlementDate(calculateT2SettlementDate(LocalDateTime.now()));

        walletTransactionRepository.save(transaction);
        update(wallet);
    }
    public BigDecimal getAvailableBalance(Long customerId) {
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        return wallet.getAvailableBalance();
    }

    public BigDecimal getBlockedBalance(Long customerId) {
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        return wallet.getBlockedBalance();
    }
    public WalletResponse processWithdrawal(Long customerId, BigDecimal amount){
        return subtractBalance(customerId, amount, TransactionType.WITHDRAWAL);
    }
    public WalletResponse processDeposit(Long customerId, BigDecimal amount){
        return addBalance(customerId, amount, TransactionType.DEPOSIT);
    }
    public WalletResponse lockWallet(Long customerId){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        wallet.lockWallet();
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }
    public WalletResponse unlockWallet(Long customerId){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        wallet.unlockWallet();
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }
    public WalletResponse resetDailyLimits(Long customerId){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        wallet.resetDailyLimits();
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }
    public WalletResponse resetMonthlyLimits(Long customerId) {
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        wallet.resetMonthlyLimits();
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }
    public boolean canPerformTransaction(Long customerId, BigDecimal amount) {
        try {
            Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
            return wallet.isActive() && !wallet.isLocked()
                    && wallet.hasSufficientBalance(amount)
                    && !wallet.isDailyLimitExceeded(amount)
                    && !wallet.isMonthlyLimitExceeded(amount)
                    && !wallet.isTransactionCountExceeded();
        } catch (Exception e) {
            return false;
        }
    }
    public BigDecimal getWalletBalance(Long customerId){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        return wallet.getBalance();
    }
    public WalletResponse updateWalletLimits(Long customerId, UpdateWalletRequest request){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        if (request.dailyLimit() != null){
            wallet.setDailyLimit(request.dailyLimit());
        }
        if (request.monthlyLimit() != null){
            wallet.setMonthlyLimit(request.monthlyLimit());
        }
        if (request.maxTransactionAmount() != null) {
            wallet.setMaxTransactionAmount(request.maxTransactionAmount());
        }
        if (request.minTransactionAmount() != null) {
            wallet.setMinTransactionAmount(request.minTransactionAmount());
        }
        if (request.maxDailyTransactionCount() != null) {
            wallet.setMaxDailyTransactionCount(request.maxDailyTransactionCount());
        }
        if (request.isLocked() != null) {
            wallet.setLocked(request.isLocked());
        }
        if (request.walletStatus() != null) {
            wallet.setWalletStatus(request.walletStatus());
        }
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }

    private void validateTransactionLimits(Wallet wallet, BigDecimal amount) {
        if (wallet.isDailyLimitExceeded(amount)){
            throw new WalletLimitExceededException("Daily");
        }
        if (wallet.isMonthlyLimitExceeded(amount)){
            throw new WalletLimitExceededException("Monthly");
        }
        if (wallet.isTransactionCountExceeded()){
            throw new WalletLimitExceededException("Daily transaction count");
        }
    }

    private void validateSufficientBalance(Wallet wallet, BigDecimal amount) {
        if (!wallet.hasSufficientBalance(amount)){
            throw new InsufficientBalanceException(amount, wallet.getAvailableBalance());
        }
    }

    private Wallet getWalletByCustomerIdOrThrow(Long customerId) {
        Optional<Wallet> optionalWallet = walletRepository.findByCustomerId(customerId);
        if (optionalWallet.isEmpty()){
            throw new WalletNotFoundException(customerId);
        }
        return optionalWallet.get();
    }
    private void validateWalletForTransaction(Wallet wallet){
        if (!wallet.isActive()){
            throw new WalletNotActiveException();
        }
        if (wallet.isLocked()){
            throw new WalletLockedException();
        }
    }

    private void updateTransactionTracking(Wallet wallet, BigDecimal amount, boolean isAddition){
        if (!isAddition){
            wallet.incrementTransactionCount();
            wallet.addToDailyUsedAmount(amount);
            wallet.addToMonthlyUsedAmount(amount);
        }
    }


}
