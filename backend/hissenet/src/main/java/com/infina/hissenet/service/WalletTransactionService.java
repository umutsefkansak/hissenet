package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.WalletTransaction;
import com.infina.hissenet.exception.TransactionNotFoundException;
import com.infina.hissenet.exception.WalletNotFoundException;
import com.infina.hissenet.mapper.WalletTransactionMapper;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.repository.WalletTransactionRepository;
import com.infina.hissenet.service.abstracts.IWalletTransactionService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WalletTransactionService extends GenericServiceImpl<WalletTransaction, Long> implements IWalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletTransactionMapper walletTransactionMapper;
    private final WalletRepository walletRepository;
    public WalletTransactionService(WalletTransactionRepository walletTransactionRepository,
                                    WalletRepository walletRepository,
                                    WalletTransactionMapper walletTransactionMapper){
        super(walletTransactionRepository);
        this.walletTransactionMapper=walletTransactionMapper;
        this.walletRepository=walletRepository;
        this.walletTransactionRepository=walletTransactionRepository;
    }

    public WalletTransactionResponse createWalletTransaction(CreateWalletTransactionRequest request){
        Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new WalletNotFoundException(request.walletId()));

        WalletTransaction walletTransaction = walletTransactionMapper.toEntity(request);
        walletTransaction.setWallet(wallet);
        walletTransaction.setBalanceBefore(wallet.getBalance());
        WalletTransaction savedTransaction = save(walletTransaction);
        return walletTransactionMapper.toResponse(savedTransaction);
    }
    public WalletTransactionResponse updateWalletTransaction(Long walletTransactionId, UpdateWalletTransactionRequest updateWalletTransactionRequest){
        WalletTransaction walletTransaction = findById(walletTransactionId)
                .orElseThrow(()-> new TransactionNotFoundException(walletTransactionId));
        walletTransactionMapper.updateEntityFromRequest(updateWalletTransactionRequest, walletTransaction);
        WalletTransaction updatedTransaction = update(walletTransaction);
        return walletTransactionMapper.toResponse(updatedTransaction);
    }
    public void completeTransaction(Long transactionId, BigDecimal finalBalance){
        WalletTransaction walletTransaction = findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        walletTransaction.completeTransaction(finalBalance);
        update(walletTransaction);
    }
    public void cancelTransaction(Long transactionId){
        WalletTransaction walletTransaction = findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        walletTransaction.cancelTransaction();
        update(walletTransaction);
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
    public WalletTransactionResponse getTransactionById(Long transactionId) {
        WalletTransaction walletTransaction = findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        return walletTransactionMapper.toResponse(walletTransaction);
    }
  }
