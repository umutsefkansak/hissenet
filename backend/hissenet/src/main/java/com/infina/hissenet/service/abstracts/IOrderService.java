package com.infina.hissenet.service.abstracts;

import java.util.List;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;

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

}
