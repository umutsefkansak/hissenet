package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    @Column(name = "amount", nullable = false, scale = 4)
    private BigDecimal amount=BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Size(max = 300, message = "Description must be max 300 characters")
    @Column(name = "description", length = 300)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "balance_before", scale = 4)
    private BigDecimal balanceBefore;


    @NotNull(message = "Reference number cannot be empty")
    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    @DecimalMin(value = "0.0", message = "Fee amount cannot be negative")
    @Column(name = "fee_amount", scale = 4)
    private BigDecimal feeAmount;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be empty")
    @Column(name = "tax_amount", scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "balance_after", scale = 4)
    private BigDecimal balanceAfter;

    
    public WalletTransaction(){}

    public WalletTransaction(Wallet wallet, BigDecimal amount, String referenceNumber, TransactionType transactionType, String description, TransactionStatus transactionStatus, LocalDateTime transactionDate) {
        this.wallet = wallet;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionStatus = transactionStatus;
        this.balanceBefore=wallet.getBalance();
        this.transactionDate = transactionDate;
        this.referenceNumber=referenceNumber;
    }
    public void completeTransaction(BigDecimal finalBalance){
        this.transactionStatus=TransactionStatus.COMPLETED;
        this.balanceAfter=finalBalance;
        this.transactionDate = LocalDateTime.now();
    }
    public void cancelTransaction(){
        this.transactionStatus = TransactionStatus.CANCELLED;
        this.transactionDate = LocalDateTime.now();
    }
    public boolean isCompleted(){
        return TransactionStatus.COMPLETED.equals(this.transactionStatus);
    }
    public boolean isPending(){
        return TransactionStatus.PENDING.equals(this.transactionStatus);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

}
