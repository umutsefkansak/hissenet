package com.infina.hissenet.entity;

import com.infina.hissenet.entity.base.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_prices")
public class StockPrice extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false, unique = true)
    private Stock stock;

    @Column(name = "current_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "open_price", precision = 19, scale = 4)
    private BigDecimal openPrice;

    @Column(name = "high_price", precision = 19, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 19, scale = 4)
    private BigDecimal lowPrice;

    @Column(name = "price_timestamp", nullable = false)
    private LocalDateTime timestamp;

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}