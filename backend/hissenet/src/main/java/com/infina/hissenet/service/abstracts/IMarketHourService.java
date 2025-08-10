package com.infina.hissenet.service.abstracts;


/**
 * Service interface for market hour operations.
 * Handles market opening/closing time checks and order placement time validations.
 *
 * @author Atalay Berk Çırak
 */
public interface IMarketHourService {

    /**
     * Checks if the market is currently open for trading.
     *
     * Market is considered open when:
     * - Current day is Monday through Friday (excluding weekends)
     * - Current time is between 10:00 AM and 6:00 PM
     *
     * @return true if market is open, false otherwise
     */
    boolean isMarketOpen();

    /**
     * Checks if orders can currently be placed.
     *
     * Orders can be placed when:
     * - Current day is Monday through Friday (excluding weekends)
     * - Current time is between 9:30 AM and 5:30 PM
     *
     * This allows for order collection before market opens and
     * prevents new orders from being placed too close to market close.
     *
     * @return true if orders can be placed, false otherwise
     */
    boolean canPlaceOrder();


}
