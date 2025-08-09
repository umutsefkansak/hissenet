package com.infina.hissenet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Enhanced portfolio response with T+2 settlement information
 * Includes total, available, and blocked stock quantities
 */
public record EnhancedPortfolioStockResponse(
    String stockCode,
    BigDecimal totalQuantity,          // Toplam sahip olunan
    BigDecimal availableQuantity,     // Satılabilir miktar
    BigDecimal blockedQuantity,       // T+2 bekleyen (bloke)
    BigDecimal averagePrice,
    LocalDateTime earliestUnblockTime, // En erken unlock zamanı
    String blockStatus                 // Bloke durumu açıklaması
) {
    
    /**
     * Factory method for creating response with block status message
     */
    public static EnhancedPortfolioStockResponse of(
            String stockCode,
            BigDecimal totalQuantity,
            BigDecimal availableQuantity,
            BigDecimal blockedQuantity,
            BigDecimal averagePrice,
            LocalDateTime earliestUnblockTime) {
        
        String blockStatus = generateBlockStatus(blockedQuantity, earliestUnblockTime);
        
        return new EnhancedPortfolioStockResponse(
            stockCode,
            totalQuantity,
            availableQuantity,
            blockedQuantity,
            averagePrice,
            earliestUnblockTime,
            blockStatus
        );
    }
    
    private static String generateBlockStatus(BigDecimal blockedQuantity, LocalDateTime earliestUnblockTime) {
        if (blockedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return "Tüm hisseler satılabilir durumda";
        }
        
        if (earliestUnblockTime != null) {
            return String.format("T+2 settlement nedeniyle %s hisse blokede. En erken unlock: %s",
                blockedQuantity, earliestUnblockTime);
        }
        
        return String.format("T+2 settlement nedeniyle %s hisse blokede", blockedQuantity);
    }
}
