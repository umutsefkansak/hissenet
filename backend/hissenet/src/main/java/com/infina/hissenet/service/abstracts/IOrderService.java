package com.infina.hissenet.service.abstracts;

import java.math.BigDecimal;
import java.util.List;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.dto.response.PortfolioStockQuantityResponse;

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
}
