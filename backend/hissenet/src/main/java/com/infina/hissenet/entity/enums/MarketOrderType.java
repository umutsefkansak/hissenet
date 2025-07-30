package com.infina.hissenet.entity.enums;

public enum MarketOrderType {
    MARKET,         // Piyasa emri
    LIMIT,          // Limit emri
    STOP_LOSS,      // Zarar durdurma emri
    STOP_LIMIT,     // Zarar durdurma limit emri
    TRAILING_STOP,  // İzleyen zarar durdurma
    ICEBERG,        // Buzdağı emri
    TWAP,           // Time Weighted Average Price
    VWAP            // Volume Weighted Average Price
} 