package com.infina.hissenet.service.abstracts;

import java.math.BigDecimal;
import java.util.List;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.dto.response.PortfolioStockQuantityResponse;
import com.infina.hissenet.dto.response.RecentOrderResponse;

/**
 * Service interface for order operations.
 * Handles creation, update, retrieval, and listing of orders.
 */
public interface IOrderService {
    
    /**
     * Creates a new order.
     *
     * @param request order creation data
     * @return created order details
     */
    OrderResponse createOrder(OrderCreateRequest request);

    /**
     * Updates an existing order.
     *
     * @param request order update data
     * @return updated order details
     */
    OrderResponse updateOrder(Long id, OrderUpdateRequest request);

    /**
     * Retrieves an order by ID.
     *
     * @param id order identifier
     * @return order details
     */
    OrderResponse getOrderById(Long id);

    /**
     * Lists all orders.
     *
     * @return list of orders
     */
    List<OrderResponse> getAllOrders();

    /**
     * Calculates the net owned stock quantity (BUY - SELL) for a specific stock
     * and customer based on filled orders.
     *
     * @param customerId the ID of the customer
     * @param stockCode  the stock symbol
     * @return net owned quantity
     */
    BigDecimal getOwnedStockQuantity(Long customerId, String stockCode);

    /**
     * Retrieves the customer’s stock portfolio.
     * Returns all stock codes with their corresponding net owned quantities.
     *
     * @param customerId the ID of the customer
     * @return list of stock codes and net owned quantities
     */
    List<PortfolioStockQuantityResponse> getPortfolioByCustomerId(Long customerId);

    /**
     * Retrieves all orders placed by a specific customer.
     *
     * @param customerId the ID of the customer
     * @return list of order responses
     */
    List<OrderResponse> getOrdersByCustomerId(Long customerId);
    
    /**
     * Retrieves the latest 5 filled orders across all customers.
     * <p>
     * This method returns a simplified view of recent orders, including only
     * stock code, total amount, and order type. Only orders with status {@code FILLED}
     * are considered.
     *
     * @return a list of recent order summaries
     */
    List<RecentOrderResponse> getLastFiveOrders();
    
    /**
     * Retrieves all orders with status {@code FILLED} across all customers.
     * <p>
     * This method filters and returns only the orders that have been successfully
     * executed (i.e., filled orders). Useful for reporting, auditing, or historical views.
     *
     * @return list of filled order responses
     */
    List<OrderResponse> getAllFilledOrders();
    
    /**
     * Retrieves all orders with status {@code FILLED} created today.
     *
     * @return list of today's filled orders
     */
    List<OrderResponse> getTodayFilledOrders();
    
    /**
     * Calculates the total trade volume (sum of totalAmount) for today's FILLED orders.
     *
     * @return today's total trade volume
     */
    BigDecimal getTodayTotalTradeVolume();

}
