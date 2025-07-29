package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
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

    @Column(name = "daily_limit", scale = 2)
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit", scale = 2)
    private BigDecimal monthlyLimit;

    @Column(name = "daily_used_amount", scale = 2)
    private BigDecimal dailyUsedAmount = BigDecimal.ZERO;

    @Column(name = "monthly_used_amount", scale = 2)
    private BigDecimal monthlyUsedAmount = BigDecimal.ZERO;

    @Column(name = "max_transaction_amount", scale = 2)
    private BigDecimal maxTransactionAmount;

    @Column(name = "min_transaction_amount", scale = 2)
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

    public Customer getUser() {
        return customer;
    }

    public void setUser(Customer customer) {
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
