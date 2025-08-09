package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.entity.WalletTransaction;
import com.infina.hissenet.entity.enums.Status;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;
import com.infina.hissenet.exception.wallet.InsufficientBalanceException;
import com.infina.hissenet.mapper.WalletMapper;
import com.infina.hissenet.repository.CustomerRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private WalletTransactionRepository walletTransactionRepository;
    @InjectMocks
    private WalletService service;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(walletMapper.toResponse(any(Wallet.class))).thenAnswer(inv -> {
            Wallet w = inv.getArgument(0);
            if (w == null) return null;
            Long customerId = (w.getCustomer() != null) ? w.getCustomer().getId() : null;
            return new WalletResponse(
                    w.getId(),
                    customerId,
                    w.getBalance(),
                    w.getCurrency(),
                    w.getAvailableBalance(),
                    w.getBlockedBalance(),
                    w.getDailyLimit(),
                    w.getMonthlyLimit(),
                    w.getDailyUsedAmount(),
                    w.getMonthlyUsedAmount(),
                    w.getMaxTransactionAmount(),
                    w.getMinTransactionAmount(),
                    w.getDailyTransactionCount(),
                    w.getMaxDailyTransactionCount(),
                    w.getLastTransactionDate(),
                    w.getLastResetDate(),
                    w.getLocked(),
                    w.getWalletStatus(),
                    null,
                    null
            );
        });
    }

    private Wallet walletWith(BigDecimal balance, BigDecimal available, BigDecimal blocked) {
        Wallet w = new Wallet();
        w.setCustomer(customer);
        w.setWalletStatus(Status.ACTIVE);
        w.setLocked(false);
        w.setBalance(balance);
        w.setAvailableBalance(available);
        w.setBlockedBalance(blocked);
        return w;
    }

    @Test
    void createWalletSuccessReturnsResponse() {
        CreateWalletRequest request = mock(CreateWalletRequest.class);
        when(request.customerId()).thenReturn(1L);

        Wallet newWallet = new Wallet();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.empty());
        when(walletMapper.toEntity(request)).thenReturn(newWallet);

        WalletResponse response = service.createWallet(request);
        assertNotNull(response);
        assertEquals(1L, response.customerId());
        assertNotNull(newWallet.getLastResetDate());
        verify(walletRepository).save(any(Wallet.class));
    }
    @Test
    void addBalanceDepositSuccessWalletAndSaves() {
        Wallet wallet = walletWith(new BigDecimal("100"), new BigDecimal("100"), BigDecimal.ZERO);
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));

        WalletResponse response = service.addBalance(1L, new BigDecimal("50"), TransactionType.DEPOSIT);
        assertNotNull(response);
        assertEquals(new BigDecimal("150"), wallet.getBalance());
        assertEquals(new BigDecimal("150"), wallet.getAvailableBalance());
        assertEquals(BigDecimal.ZERO, wallet.getBlockedBalance());
        assertEquals(new BigDecimal("150"), response.balance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void subtractBalanceWithdrawalSuccess() {
        Wallet wallet = walletWith(new BigDecimal("190"), new BigDecimal("110"), BigDecimal.ZERO);
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));

        WalletResponse response = service.subtractBalance(1L, new BigDecimal("90"), TransactionType.WITHDRAWAL);
        assertNotNull(response);
        assertEquals(new BigDecimal("100"), wallet.getBalance());
        assertEquals(new BigDecimal("20"), wallet.getAvailableBalance());
        assertEquals(1, wallet.getDailyTransactionCount());
        assertEquals(new BigDecimal("90"), wallet.getDailyUsedAmount());
        assertEquals(new BigDecimal("90"), wallet.getMonthlyUsedAmount());
        assertEquals(new BigDecimal("100"), response.balance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void subtractBalanceInsufficientBalance() {
        Wallet wallet = walletWith(new BigDecimal("10"), new BigDecimal("60"), BigDecimal.ZERO);
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));
        assertThrows(InsufficientBalanceException.class,
                () -> service.subtractBalance(1L, new BigDecimal("90"), TransactionType.WITHDRAWAL));
        verify(walletRepository, never()).save(any());
    }

    @Test
    void processStockPurchaseBlocksUntilSettlement() {
        Wallet wallet = walletWith(new BigDecimal("1000"), new BigDecimal("1000"), BigDecimal.ZERO);
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));
        service.processStockPurchase(1L, new BigDecimal("500"), new BigDecimal("120"));
        assertEquals(new BigDecimal("1000"), wallet.getBalance());
        assertEquals(new BigDecimal("380"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("620"), wallet.getBlockedBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void processStockSaleIncreasesBalanceAndBlocksUntilSettlement() {
        Wallet wallet = walletWith(new BigDecimal("1000"), new BigDecimal("1000"), BigDecimal.ZERO);
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));
        service.processStockSale(1L, new BigDecimal("600"), new BigDecimal("10"));
        assertEquals(new BigDecimal("1590"), wallet.getBalance());
        assertEquals(new BigDecimal("1000"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("590"), wallet.getBlockedBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void processT2SettlementsProcessesTypes() {
        Wallet wallet = walletWith(new BigDecimal("1590"), new BigDecimal("490"), new BigDecimal("1100"));

        WalletTransaction buyTx = new WalletTransaction();
        buyTx.setWallet(wallet);
        buyTx.setAmount(new BigDecimal("510"));
        buyTx.setTransactionType(TransactionType.STOCK_PURCHASE);
        buyTx.setTransactionStatus(TransactionStatus.COMPLETED);
        buyTx.setSettlementDate(LocalDateTime.now().minusMinutes(1));

        WalletTransaction sellTx = new WalletTransaction();
        sellTx.setWallet(wallet);
        sellTx.setAmount(new BigDecimal("590"));
        sellTx.setTransactionType(TransactionType.STOCK_SALE);
        sellTx.setTransactionStatus(TransactionStatus.COMPLETED);
        sellTx.setSettlementDate(LocalDateTime.now().minusMinutes(1));
        when(walletTransactionRepository.findTransactionsReadyForSettlement(
                any(LocalDateTime.class),
                eq(TransactionStatus.COMPLETED),
                eq(TransactionType.STOCK_PURCHASE),
                eq(TransactionType.STOCK_SALE)
        )).thenReturn(List.of(buyTx, sellTx));
        service.processT2Settlements();
        assertEquals(new BigDecimal("0"), wallet.getBlockedBalance());
        assertEquals(new BigDecimal("1080"), wallet.getBalance());
        assertEquals(new BigDecimal("1080"), wallet.getAvailableBalance());
        assertEquals(TransactionStatus.SETTLED, buyTx.getTransactionStatus());
        assertEquals(TransactionStatus.SETTLED, sellTx.getTransactionStatus());
        verify(walletTransactionRepository, times(2)).save(any(WalletTransaction.class));
        verify(walletRepository, atLeast(2)).save(any(Wallet.class));
    }

    @Test
    void resetDailyAndMonthlyLimits() {
        Wallet wallet = walletWith(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        wallet.setDailyTransactionCount(3);
        wallet.setDailyUsedAmount(new BigDecimal("200"));
        wallet.setMonthlyUsedAmount(new BigDecimal("800"));
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));

        service.resetDailyLimits(1L);
        assertEquals(0, wallet.getDailyTransactionCount());
        assertEquals(BigDecimal.ZERO, wallet.getDailyUsedAmount());
        assertEquals(LocalDate.now(), wallet.getLastResetDate());
        verify(walletRepository).save(wallet);
        service.resetMonthlyLimits(1L);
        assertEquals(BigDecimal.ZERO, wallet.getMonthlyUsedAmount());
        assertEquals(LocalDate.now(), wallet.getLastResetDate());
        verify(walletRepository, times(2)).save(wallet);
    }

    @Test
    void getAvailableAndBlockedBalance() {
        Wallet wallet = walletWith(new BigDecimal("100"), new BigDecimal("70"), new BigDecimal("30"));
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));
        assertEquals(new BigDecimal("70"), service.getAvailableBalance(1L));
        assertEquals(new BigDecimal("30"), service.getBlockedBalance(1L));
    }

    @Test
    void updateWalletLimitsUpdatesProvided() {
        Wallet wallet = walletWith(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        when(walletRepository.findByCustomerId(1L)).thenReturn(Optional.of(wallet));

        UpdateWalletRequest request = mock(UpdateWalletRequest.class);
        when(request.dailyLimit()).thenReturn(new BigDecimal("1000"));
        when(request.monthlyLimit()).thenReturn(new BigDecimal("5000"));
        when(request.maxTransactionAmount()).thenReturn(new BigDecimal("200"));
        when(request.minTransactionAmount()).thenReturn(null);
        when(request.maxDailyTransactionCount()).thenReturn(25);
        when(request.isLocked()).thenReturn(Boolean.TRUE);
        when(request.walletStatus()).thenReturn(Status.ACTIVE);

        WalletResponse resp = service.updateWalletLimits(1L, request);
        assertNotNull(resp);
        assertEquals(new BigDecimal("1000"), wallet.getDailyLimit());
        assertEquals(new BigDecimal("5000"), wallet.getMonthlyLimit());
        assertEquals(new BigDecimal("200"), wallet.getMaxTransactionAmount());
        assertNull(wallet.getMinTransactionAmount());
        assertEquals(25, wallet.getMaxDailyTransactionCount());
        assertTrue(wallet.getLocked());
        assertEquals(Status.ACTIVE, wallet.getWalletStatus());
        verify(walletRepository).save(wallet);
        assertEquals(new BigDecimal("1000"), resp.dailyLimit());
        assertEquals(new BigDecimal("5000"), resp.monthlyLimit());
    }
}