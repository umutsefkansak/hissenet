package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.WalletTransaction;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.transaction.TransactionNotFoundException;
import com.infina.hissenet.mapper.WalletTransactionMapper;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WalletTransactionServiceTest {

    @Mock
    private WalletTransactionRepository walletTransactionRepository;
    @Mock
    private WalletTransactionMapper walletTransactionMapper;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private WalletTransactionService service;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(10L);
        when(walletTransactionRepository.save(any(WalletTransaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(walletTransactionMapper.toResponse(any(WalletTransaction.class)))
                .thenReturn(mock(WalletTransactionResponse.class));
    }

    @Test
    void createWalletTransactionSuccess() {
        CreateWalletTransactionRequest request = mock(CreateWalletTransactionRequest.class);
        when(request.walletId()).thenReturn(10L);
        WalletTransaction tx = new WalletTransaction();
        when(walletRepository.findById(10L)).thenReturn(Optional.of(wallet));
        when(walletTransactionMapper.toEntity(request)).thenReturn(tx);

        WalletTransactionResponse resp = service.createWalletTransaction(request);
        assertNotNull(resp);
        assertEquals(wallet, tx.getWallet());
        verify(walletTransactionRepository).save(tx);
        verify(walletTransactionMapper).toResponse(tx);
    }

    @Test
    void updateWalletTransactionSuccess() {
        UpdateWalletTransactionRequest updateReq = mock(UpdateWalletTransactionRequest.class);
        WalletTransaction existing = new WalletTransaction();
        existing.setId(1L);
        when(walletTransactionRepository.findById(1L)).thenReturn(Optional.of(existing));

        WalletTransactionResponse resp = service.updateWalletTransaction(1L, updateReq);
        assertNotNull(resp);
        verify(walletTransactionMapper).updateEntityFromRequest(updateReq, existing);
        verify(walletTransactionRepository).save(existing);
        verify(walletTransactionMapper).toResponse(existing);
    }

    @Test
    void completeTransactionCompletedAndSaves() {
        WalletTransaction tx = new WalletTransaction();
        tx.setId(5L);
        when(walletTransactionRepository.findById(5L)).thenReturn(Optional.of(tx));
        service.completeTransaction(5L);
        assertEquals(TransactionStatus.COMPLETED, tx.getTransactionStatus());
        verify(walletTransactionRepository).save(tx);
    }


    @Test
    void getTransactionHistoryList() {
        WalletTransaction t1 = new WalletTransaction();
        WalletTransaction t2 = new WalletTransaction();
        when(walletTransactionRepository.findByWalletIdOrderByTransactionDateDesc(10L))
                .thenReturn(List.of(t1, t2));
        List<WalletTransactionResponse> list = service.getTransactionHistory(10L);
        assertEquals(2, list.size());
        verify(walletTransactionMapper, times(2)).toResponse(any(WalletTransaction.class));
    }

    @Test
    void getAllTransactionsPage() {
        WalletTransaction t1 = new WalletTransaction();
        Page<WalletTransaction> page = new PageImpl<>(List.of(t1));
        when(walletTransactionRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<WalletTransactionResponse> resp = service.getAllTransactions(PageRequest.of(0, 10));
        assertEquals(1, resp.getTotalElements());
        verify(walletTransactionMapper, times(1)).toResponse(t1);
    }

    @Test
    void getTransactionByIdSuccess() {
        WalletTransaction t1 = new WalletTransaction();
        t1.setId(7L);
        when(walletTransactionRepository.findById(7L)).thenReturn(Optional.of(t1));
        WalletTransactionResponse resp = service.getTransactionById(7L);
        assertNotNull(resp);
        verify(walletTransactionMapper).toResponse(t1);
    }

    @Test
    void getTransactionByIdNotFoundThrows() {
        when(walletTransactionRepository.findById(8L)).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class, () -> service.getTransactionById(8L));
    }
}