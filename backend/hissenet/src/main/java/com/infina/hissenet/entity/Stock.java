package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.Currency;
import com.infina.hissenet.entity.enums.Exchange;
import com.infina.hissenet.entity.enums.Status;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "stocks")
public class Stock extends BaseEntity {

    @Column(name = "ticker", nullable = false, unique = true, length = 20)
    private String ticker;

    @Column(name = "issuer_name", nullable = false)
    private String issuerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange", length = 10, nullable = false)
    private Exchange exchange;

    @Column(name = "lot_size")
    private Integer lotSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status;

    @OneToOne(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private StockPrice currentPrice;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHistory> history;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StockPrice getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(StockPrice currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<StockHistory> getHistory() {
        return history;
    }

    public void setHistory(List<StockHistory> history) {
        this.history = history;
    }
}