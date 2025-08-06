package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.entity.enums.Status;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "portfolios")
@SQLRestriction("is_deleted = false")
public class Portfolio extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Column(name = "portfolio_name", nullable = false, length = 100)
    private String portfolioName;

    @Column(name = "description", length = 500)
    private String description;

    // Precision maksimuma çıkarıldı: 25 → 38 (Arithmetic overflow'ı kesin önlemek için)
    @Column(name = "total_value", precision = 38, scale = 4)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "total_cost", precision = 38, scale = 4)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "total_profit_loss", precision = 38, scale = 4)
    private BigDecimal totalProfitLoss = BigDecimal.ZERO;

    @Column(name = "profit_loss_percentage", precision = 8, scale = 2) // 999999.99 max
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
        this.totalValue = totalValue != null ? totalValue : BigDecimal.ZERO;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.totalProfitLoss = totalProfitLoss != null ? totalProfitLoss : BigDecimal.ZERO;
        this.profitLossPercentage = profitLossPercentage != null ? profitLossPercentage : BigDecimal.ZERO;
        this.riskProfile = riskProfile;
        this.portfolioType = portfolioType;
        this.status = status;
        this.isActive = isActive;
        this.lastRebalanceDate = lastRebalanceDate;
        this.transactions = transactions;
    }

    public Portfolio() {
        // Default constructor'da da null check ekledim
        this.totalValue = BigDecimal.ZERO;
        this.totalCost = BigDecimal.ZERO;
        this.totalProfitLoss = BigDecimal.ZERO;
        this.profitLossPercentage = BigDecimal.ZERO;
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
        return totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    public BigDecimal getTotalCost() {
        return totalCost != null ? totalCost : BigDecimal.ZERO;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
    }

    public BigDecimal getTotalProfitLoss() {
        return totalProfitLoss != null ? totalProfitLoss : BigDecimal.ZERO;
    }

    public void setTotalProfitLoss(BigDecimal totalProfitLoss) {
        this.totalProfitLoss = totalProfitLoss != null ? totalProfitLoss : BigDecimal.ZERO;
    }

    public BigDecimal getProfitLossPercentage() {
        return profitLossPercentage != null ? profitLossPercentage : BigDecimal.ZERO;
    }

    public void setProfitLossPercentage(BigDecimal profitLossPercentage) {
        this.profitLossPercentage = profitLossPercentage != null ? profitLossPercentage : BigDecimal.ZERO;
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