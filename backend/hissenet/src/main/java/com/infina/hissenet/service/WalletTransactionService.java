package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.WalletTransaction;
import com.infina.hissenet.mapper.WalletTransactionMapper;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.repository.WalletTransactionRepository;
import com.infina.hissenet.utils.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletTransactionService implements IGenericService<WalletTransaction, Long> {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletTransactionMapper walletTransactionMapper;
    private final WalletRepository walletRepository;
    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository, WalletRepository walletRepository, WalletTransactionMapper walletTransactionMapper){
        this.walletTransactionMapper=walletTransactionMapper;
        this.walletRepository=walletRepository;
        this.walletTransactionRepository=walletTransactionRepository;
    }

    public WalletTransactionResponse createWalletTransaction(CreateWalletTransactionRequest request){
        Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found by ID: "+ request.walletId()));

        WalletTransaction walletTransaction = walletTransactionMapper.toEntity(request);
        walletTransaction.setWallet(wallet);
        walletTransaction.setBalanceBefore(wallet.getBalance());
        WalletTransaction savedTransaction = save(walletTransaction);
        return walletTransactionMapper.toResponse(savedTransaction);
    }
    public WalletTransactionResponse updateWalletTransaction(Long walletTransactionId, UpdateWalletTransactionRequest updateWalletTransactionRequest){
        WalletTransaction walletTransaction = findById(walletTransactionId)
                .orElseThrow(()-> new RuntimeException("Transaction not found by id: " + walletTransactionId));
        walletTransactionMapper.updateEntityFromRequest(updateWalletTransactionRequest, walletTransaction);
        WalletTransaction updatedTransaction = update(walletTransaction);
        return walletTransactionMapper.toResponse(updatedTransaction);
    }
    public void completeTransaction(Long transactionId, BigDecimal finalBalance){
        WalletTransaction walletTransaction = findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        walletTransaction.completeTransaction(finalBalance);
    }
    public void cancelTransaction(Long transactionId){
        WalletTransaction walletTransaction = findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        walletTransaction.cancelTransaction();
    }
    public List<WalletTransactionResponse> getTransactionHistory(Long walletId){
        return walletTransactionRepository.findByWalletIdOrderByTransactionDateDesc(walletId)
                .stream()
                .map(walletTransactionMapper::toResponse)
                .collect(Collectors.toList());
    }
    public Page<WalletTransactionResponse> getAllTransactions(Pageable pageable){
        return walletTransactionRepository.findAll(pageable)
                .map(walletTransactionMapper::toResponse);
    }

    @Override
    public WalletTransaction save(WalletTransaction entity) {
        return walletTransactionRepository.save(entity);
    }

    @Override
    public WalletTransaction update(WalletTransaction entity) {
        return walletTransactionRepository.save(entity);
    }

    @Override
    public void delete(WalletTransaction entity) {
        entity.setDeleted(true);
        update(entity);
    }

    @Override
    public void deleteById(Long id) {
        walletTransactionRepository.deleteById(id);
    }

    @Override
    public Optional<WalletTransaction> findById(Long id) {
        return walletTransactionRepository.findById(id);
    }

    @Override
    public List<WalletTransaction> findAll() {
        return walletTransactionRepository.findAll();
    }

    @Override
    public Page<WalletTransaction> findAll(Pageable pageable) {
        return walletTransactionRepository.findAll(pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return walletTransactionRepository.existsById(id);
    }
}
