package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.enums.TransactionType;
import com.infina.hissenet.mapper.WalletMapper;
import com.infina.hissenet.repository.CustomerRepository;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.utils.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WalletService implements IGenericService<Wallet, Long> {

    private WalletRepository walletRepository;
    private WalletMapper walletMapper;
    private CustomerRepository customerRepository;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper,CustomerRepository customerRepository){
        this.walletRepository=walletRepository;
        this.walletMapper=walletMapper;
        this.customerRepository=customerRepository;
    }


    @Override
    public Wallet save(Wallet entity) {
        return walletRepository.save(entity);
    }

    @Override
    public Wallet update(Wallet entity) {
        return walletRepository.save(entity);
    }

    @Override
    public void delete(Wallet entity) {
        entity.setDeleted(true);
        update(entity);
    }

    @Override
    public void deleteById(Long id) {
        walletRepository.deleteById(id);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return walletRepository.findById(id);
    }

    @Override
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    @Override
    public Page<Wallet> findAll(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return walletRepository.existsById(id);
    }

    public WalletResponse createWallet(CreateWalletRequest request){
        Optional<Customer> optionalCustomer = customerRepository.findById(request.customerId());
        if (optionalCustomer.isEmpty()){
            throw new RuntimeException("Customer not found with ID: " + request.customerId());
        }
        Optional<Wallet> existingWallet = walletRepository.findByCustomerId(request.customerId());

        if (existingWallet.isPresent()){
            throw new RuntimeException("This customer already has wallet");
        }
        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCustomer(optionalCustomer.get());
        wallet.setLastResetDate(LocalDate.now());

        Wallet savedWallet = save(wallet);
        return walletMapper.toResponse(savedWallet);
    }

    public WalletResponse getWalletByCustomerId(Long customerId){
        Optional<Wallet> optionalWallet = walletRepository.findByCustomerId(customerId);
        if (optionalWallet.isEmpty()){
            throw new RuntimeException("Wallet not found for this customer ID: " + customerId);
        }
        return walletMapper.toResponse(optionalWallet.get());
    }

    public WalletResponse addBalance(Long customerId, BigDecimal amount, TransactionType transactionType){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        validateWalletForTransaction(wallet);
        wallet.addBalance(amount);
        updateTransactionTracking(wallet, amount, true);
        Wallet updateWallet = update(wallet);
        return walletMapper.toResponse(updateWallet);
    }
    
    public WalletResponse subtractBalance(Long customerId, BigDecimal amount, TransactionType transactionType){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);
        validateWalletForTransaction(wallet);
        validateSufficientBalance(wallet, amount);
        validateTransactionLimits(wallet, amount);
        wallet.subtractBalance(amount);
        updateTransactionTracking(wallet, amount, false);
        Wallet updatedWallet = update(wallet);
        return walletMapper.toResponse(updatedWallet);
    }


    public WalletResponse processStockPurchase(Long customerId, BigDecimal totalAmount, BigDecimal commission, BigDecimal tax){
        BigDecimal totalCost = totalAmount.add(commission).add(tax);
        return subtractBalance(customerId, totalCost, TransactionType.STOCK_PURCHASE);
    }

    public WalletResponse processStockSale(Long customerId, BigDecimal totalAmount, BigDecimal commission, BigDecimal tax){
        BigDecimal netAmount = totalAmount.subtract(commission).subtract(tax);
        return addBalance(customerId, netAmount, TransactionType.STOCK_SALE);
    }
    public WalletResponse processDividendPayment(Long customerId, BigDecimal dividendAmount){
        return addBalance(customerId, dividendAmount, TransactionType.DIVIDEND);
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
            throw new RuntimeException("Daily limit exceeded");
        }
        if (wallet.isMonthlyLimitExceeded(amount)){
            throw new RuntimeException("Monthly limit exceeded");
        }
        if (wallet.isTransactionCountExceeded()){
            throw new RuntimeException("Daily transaction count exceeded");
        }
    }

    private void validateSufficientBalance(Wallet wallet, BigDecimal amount) {
        if (!wallet.hasSufficientBalance(amount)){
            throw new RuntimeException("Insufficient balance. Required balance: "+ amount+" Exist balance: "+ wallet.getBalance());
        }
    }


    private Wallet getWalletByCustomerIdOrThrow(Long customerId) {
        Optional<Wallet> optionalWallet = walletRepository.findByCustomerId(customerId);
        if (optionalWallet.isEmpty()){
            throw new RuntimeException("Wallet not found for customer ID: "+ customerId);
        }
        return optionalWallet.get();
    }


    private void validateWalletForTransaction(Wallet wallet){
        if (!wallet.isActive()){
            throw new RuntimeException("Wallet is not active");
        }
        if (wallet.isLocked()){
            throw new RuntimeException("Wallet is locked");
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
