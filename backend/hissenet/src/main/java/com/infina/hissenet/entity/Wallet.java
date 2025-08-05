package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
@SQLRestriction("is_deleted = false")
public class Wallet extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @NotNull(message = "The balance cannot be empty")
    @DecimalMin(value = "0.0", message = "The balance cannot be negative")
    @Column(name = "balance", precision = 19, nullable = false, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull(message = "Currency cannot be empty")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "TRY";

    @Column(name = "daily_limit", scale = 4)
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit", scale = 4)
    private BigDecimal monthlyLimit;

    @Column(name = "daily_used_amount", scale = 4)
    private BigDecimal dailyUsedAmount = BigDecimal.ZERO;

    @Column(name = "monthly_used_amount", scale = 4)
    private BigDecimal monthlyUsedAmount = BigDecimal.ZERO;

    @Column(name = "max_transaction_amount", scale = 4)
    private BigDecimal maxTransactionAmount;

    @Column(name = "min_transaction_amount", scale = 4)
    private BigDecimal minTransactionAmount;

    @Column(name = "daily_transaction_count")
    private Integer dailyTransactionCount = 0;

    @Column(name = "max_daily_transaction_count")
    private Integer maxDailyTransactionCount;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    @Column(name = "last_reset_date")
    private java.time.LocalDate lastResetDate;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status walletStatus = Status.ACTIVE;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WalletTransaction> transactions = new ArrayList<>();

    @Column(name = "blocked_balance", precision = 19, nullable = false, scale = 2, columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal blockedBalance = BigDecimal.ZERO;

    @Column(name = "available_balance", precision = 19, nullable = false, scale = 2, columnDefinition = "DECIMAL(19,2) DEFAULT 0.00")
    private BigDecimal availableBalance = BigDecimal.ZERO;
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.availableBalance=this.availableBalance.add(amount);
        this.lastTransactionDate = LocalDateTime.now();
    }
    public void subtractBalance(BigDecimal amount) {
        if (!hasSufficientBalance(amount)) {
            throw new RuntimeException("Insufficient balance. Required: " + amount + ", Available: " + this.balance);
        }
        this.balance = this.balance.subtract(amount);
        this.availableBalance=this.availableBalance.subtract(amount);
        this.lastTransactionDate = LocalDateTime.now();
    }
    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
    public boolean isActive() {
        return Status.ACTIVE.equals(this.walletStatus);
    }

    public boolean isLocked() {
        return Boolean.TRUE.equals(this.isLocked);
    }
    public boolean isDailyLimitExceeded(BigDecimal amount) {
        if (this.dailyLimit == null) return false;
        return this.dailyUsedAmount.add(amount).compareTo(this.dailyLimit) > 0;
    }

    public boolean isMonthlyLimitExceeded(BigDecimal amount) {
        if (this.monthlyLimit == null) return false;
        return this.monthlyUsedAmount.add(amount).compareTo(this.monthlyLimit) > 0;
    }
    public boolean isTransactionCountExceeded() {
        if (this.maxDailyTransactionCount == null) return false;
        return this.dailyTransactionCount >= this.maxDailyTransactionCount;
    }

    public void incrementTransactionCount() {
        this.dailyTransactionCount++;
    }
    public void addToDailyUsedAmount(BigDecimal amount) {
        this.dailyUsedAmount = this.dailyUsedAmount.add(amount);
    }

    public void addToMonthlyUsedAmount(BigDecimal amount) {
        this.monthlyUsedAmount = this.monthlyUsedAmount.add(amount);
    }
    public void resetDailyLimits() {
        this.dailyUsedAmount = BigDecimal.ZERO;
        this.dailyTransactionCount = 0;
        this.lastResetDate = LocalDate.now();
    }

    public BigDecimal getBlockedBalance() {
        return blockedBalance;
    }

    public void setBlockedBalance(BigDecimal blockedBalance) {
        this.blockedBalance = blockedBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void resetMonthlyLimits() {
        this.monthlyUsedAmount = BigDecimal.ZERO;
        this.lastResetDate = LocalDate.now();
    }
    public void blockBalance(BigDecimal amount) {
        if (!hasSufficientAvailableBalance(amount)) {
            throw new RuntimeException("Insufficient available balance. Required: " + amount + ", Available: " + this.availableBalance);
        }
        this.blockedBalance = this.blockedBalance.add(amount);
        this.availableBalance = this.availableBalance.subtract(amount);
        this.lastTransactionDate = LocalDateTime.now();
    }

    public void unblockBalance(BigDecimal amount) {
        if (this.blockedBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient blocked balance to unblock. Required: " + amount + ", Blocked: " + this.blockedBalance);
        }
        this.blockedBalance = this.blockedBalance.subtract(amount);
        this.availableBalance = this.availableBalance.add(amount);
        this.lastTransactionDate = LocalDateTime.now();
    }
    public void transferBlockedToBalance(BigDecimal amount) {
        if (this.blockedBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient blocked balance to transfer. Required: " + amount + ", Blocked: " + this.blockedBalance);
        }
        this.blockedBalance = this.blockedBalance.subtract(amount);
        this.balance = this.balance.subtract(amount);
        this.lastTransactionDate = LocalDateTime.now();
    }

    public boolean hasSufficientAvailableBalance(BigDecimal amount) {
        return this.availableBalance.compareTo(amount) >= 0;
    }

    public void lockWallet() {
        this.isLocked = true;
    }

    public void unlockWallet() {
        this.isLocked = false;
    }
    public void addTransaction(WalletTransaction transaction){
        if (this.transactions == null){
            this.transactions = new ArrayList<>();
        }
        else transactions.add(transaction);
        transaction.setWallet(this);
    }

    public Wallet(){}

    public Wallet(Customer customer, BigDecimal balance, String currency, BigDecimal dailyLimit, BigDecimal monthlyLimit, BigDecimal dailyUsedAmount, BigDecimal monthlyUsedAmount, LocalDate lastResetDate) {
        this.customer = customer;
        this.balance = balance;
        this.currency = currency;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.dailyUsedAmount = dailyUsedAmount;
        this.monthlyUsedAmount = monthlyUsedAmount;
        this.lastResetDate = lastResetDate;
    }

    public BigDecimal getMaxTransactionAmount() {
        return maxTransactionAmount;
    }

    public void setMaxTransactionAmount(BigDecimal maxTransactionAmount) {
        this.maxTransactionAmount = maxTransactionAmount;
    }

    public List<WalletTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<WalletTransaction> transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getMinTransactionAmount() {
        return minTransactionAmount;
    }

    public void setMinTransactionAmount(BigDecimal minTransactionAmount) {
        this.minTransactionAmount = minTransactionAmount;
    }

    public Integer getDailyTransactionCount() {
        return dailyTransactionCount;
    }

    public void setDailyTransactionCount(Integer dailyTransactionCount) {
        this.dailyTransactionCount = dailyTransactionCount;
    }

    public Integer getMaxDailyTransactionCount() {
        return maxDailyTransactionCount;
    }

    public void setMaxDailyTransactionCount(Integer maxDailyTransactionCount) {
        this.maxDailyTransactionCount = maxDailyTransactionCount;
    }

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Status getWalletStatus() {
        return walletStatus;
    }

    public void setWalletStatus(Status walletStatus) {
        this.walletStatus = walletStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getDailyUsedAmount() {
        return dailyUsedAmount;
    }

    public void setDailyUsedAmount(BigDecimal dailyUsedAmount) {
        this.dailyUsedAmount = dailyUsedAmount;
    }

    public BigDecimal getMonthlyUsedAmount() {
        return monthlyUsedAmount;
    }

    public void setMonthlyUsedAmount(BigDecimal monthlyUsedAmount) {
        this.monthlyUsedAmount = monthlyUsedAmount;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

}
