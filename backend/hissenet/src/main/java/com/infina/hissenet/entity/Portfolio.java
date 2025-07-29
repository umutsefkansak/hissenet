package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.entity.enums.Status;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "portfolios")
public class Portfolio extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
    
    @Column(name = "portfolio_name", nullable = false, length = 100)
    private String portfolioName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "total_value", precision = 19, scale = 4)
    private BigDecimal totalValue = BigDecimal.ZERO;
    
    @Column(name = "total_cost", precision = 19, scale = 4)
    private BigDecimal totalCost = BigDecimal.ZERO;
    
    @Column(name = "total_profit_loss", precision = 19, scale = 4)
    private BigDecimal totalProfitLoss = BigDecimal.ZERO;
    
    @Column(name = "profit_loss_percentage", precision = 5, scale = 2)
    private BigDecimal profitLossPercentage = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_profile")
    private RiskProfile riskProfile;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "portfolio_type")
    private PortfolioType portfolioType = PortfolioType.BALANCED;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "last_rebalance_date")
    private LocalDateTime lastRebalanceDate;
    
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockTransaction> transactions;

    public Portfolio(Customer customer, String portfolioName, String description, BigDecimal totalValue, BigDecimal totalCost, BigDecimal totalProfitLoss, BigDecimal profitLossPercentage, RiskProfile riskProfile, PortfolioType portfolioType, Status status, Boolean isActive, LocalDateTime lastRebalanceDate, List<StockTransaction> transactions) {
        this.customer = customer;
        this.portfolioName = portfolioName;
        this.description = description;
        this.totalValue = totalValue;
        this.totalCost = totalCost;
        this.totalProfitLoss = totalProfitLoss;
        this.profitLossPercentage = profitLossPercentage;
        this.riskProfile = riskProfile;
        this.portfolioType = portfolioType;
        this.status = status;
        this.isActive = isActive;
        this.lastRebalanceDate = lastRebalanceDate;
        this.transactions = transactions;
    }

    public Portfolio() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalProfitLoss() {
        return totalProfitLoss;
    }

    public void setTotalProfitLoss(BigDecimal totalProfitLoss) {
        this.totalProfitLoss = totalProfitLoss;
    }

    public BigDecimal getProfitLossPercentage() {
        return profitLossPercentage;
    }

    public void setProfitLossPercentage(BigDecimal profitLossPercentage) {
        this.profitLossPercentage = profitLossPercentage;
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }

    public PortfolioType getPortfolioType() {
        return portfolioType;
    }

    public void setPortfolioType(PortfolioType portfolioType) {
        this.portfolioType = portfolioType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastRebalanceDate() {
        return lastRebalanceDate;
    }

    public void setLastRebalanceDate(LocalDateTime lastRebalanceDate) {
        this.lastRebalanceDate = lastRebalanceDate;
    }

    public List<StockTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<StockTransaction> transactions) {
        this.transactions = transactions;
    }
}
