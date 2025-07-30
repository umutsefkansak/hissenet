package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.enums.Status;
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
import java.time.LocalDateTime;
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
    /*
    public WalletResponse addBalance(Long customerId, BigDecimal amount, TransactionType transactionType){
        Wallet wallet = getWalletByCustomerIdOrThrow(customerId);

        validateWalletForTransaction(wallet);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastTransactionDate(LocalDateTime.now());
        updateTransactionTracking(wallet, amount, true);


    } */

    private Wallet getWalletByCustomerIdOrThrow(Long customerId) {
        Optional<Wallet> optionalWallet = walletRepository.findByCustomerId(customerId);
        if (optionalWallet.isEmpty()){
            throw new RuntimeException("Wallet not found for customer ID: "+ customerId);
        }
        return optionalWallet.get();
    }


    private void validateWalletForTransaction(Wallet wallet){
        if (!isWalletActive(wallet)){
            throw new RuntimeException("Wallet is not active");
        }
        if (isWalletLocked(wallet)){
            throw new RuntimeException("Wallet is locked");
        }
    }

    private boolean isWalletLocked(Wallet wallet) {
        return Boolean.TRUE.equals(wallet.getLocked());
    }

    private boolean isWalletActive(Wallet wallet) {
        return Status.ACTIVE.equals(wallet.getWalletStatus());
    }

    private void updateTransactionTracking(Wallet wallet, BigDecimal amount, boolean isAddition){
        if (!isAddition){
            //satis ve satin alim icin
            wallet.setDailyTransactionCount(wallet.getDailyTransactionCount()+1);
            wallet.setDailyUsedAmount(wallet.getDailyUsedAmount().add(amount));
            wallet.setMonthlyUsedAmount(wallet.getMonthlyUsedAmount().add(amount));
        }
    }


}
