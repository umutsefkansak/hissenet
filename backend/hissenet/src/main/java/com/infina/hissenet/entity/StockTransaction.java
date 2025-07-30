package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.MarketOrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stock stock;
    
    @Enumerated(EnumType.STRING)
    private StockTransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus = TransactionStatus.PENDING;
    
    private Integer quantity;
    private BigDecimal price;
    
    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;
    
    @Column(name = "commission", precision = 19, scale = 4)
    private BigDecimal commission = BigDecimal.ZERO;
    
    @Column(name = "tax", precision = 19, scale = 4)
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(name = "other_fees", precision = 19, scale = 4)
    private BigDecimal otherFees = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "market_order_type", length = 20)
    private MarketOrderType marketOrderType;
    
    @Column(name = "limit_price", precision = 19, scale = 4)
    private BigDecimal limitPrice;
    
    @Column(name = "execution_price", precision = 19, scale = 4)
    private BigDecimal executionPrice;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
    
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;
    
    @Column(name = "notes", length = 500)
    private String notes;

    public StockTransaction(Portfolio portfolio, Stock stock, StockTransactionType transactionType, TransactionStatus transactionStatus, Integer quantity, BigDecimal price, BigDecimal totalAmount, BigDecimal commission, BigDecimal tax, BigDecimal otherFees, Order order, MarketOrderType marketOrderType, BigDecimal limitPrice, BigDecimal executionPrice, LocalDateTime transactionDate, LocalDateTime settlementDate, String notes) {
        this.portfolio = portfolio;
        this.stock = stock;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.commission = commission;
        this.tax = tax;
        this.otherFees = otherFees;
        this.order = order;
        this.marketOrderType = marketOrderType;
        this.limitPrice = limitPrice;
        this.executionPrice = executionPrice;
        this.transactionDate = transactionDate;
        this.settlementDate = settlementDate;
        this.notes = notes;
    }

    public StockTransaction() {
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public StockTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(StockTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getOtherFees() {
        return otherFees;
    }

    public void setOtherFees(BigDecimal otherFees) {
        this.otherFees = otherFees;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MarketOrderType getMarketOrderType() {
        return marketOrderType;
    }

    public void setMarketOrderType(MarketOrderType marketOrderType) {
        this.marketOrderType = marketOrderType;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public LocalDateTime getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDateTime settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
