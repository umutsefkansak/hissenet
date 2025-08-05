package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@SQLRestriction("is_deleted = false")
public class WalletTransaction extends BaseEntity {

    @NotNull(message = "Wallet cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    @Column(name = "amount", nullable = false, scale = 4)
    private BigDecimal amount=BigDecimal.ZERO;

    @NotNull(message = "TransactionType cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;


    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();



    @DecimalMin(value = "0.0", message = "Fee amount cannot be negative")
    @Column(name = "fee_amount", scale = 4)
    private BigDecimal feeAmount;


    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;
    public WalletTransaction(){}

    public WalletTransaction(Wallet wallet, BigDecimal amount, TransactionType transactionType,
                             TransactionStatus transactionStatus, LocalDateTime transactionDate,
                             String source, String destination) {
        this.wallet = wallet;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.transactionDate = transactionDate;
        this.source = source;
        this.destination = destination;
    }
    public void completeTransaction(){
        this.transactionStatus=TransactionStatus.COMPLETED;
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
        if(wallet != null && !wallet.getTransactions().contains(this)){
            wallet.addTransaction(this);
        }
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

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }


    public String getSource() {
        return source;
    }

    public LocalDateTime getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDateTime settlementDate) {
        this.settlementDate = settlementDate;
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


    @Override
    public String toString() {
        return "WalletTransaction{" +
                "wallet=" + wallet +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", transactionStatus=" + transactionStatus +
                ", transactionDate=" + transactionDate +
                ",  feeAmount=" + feeAmount +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
