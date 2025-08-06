package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.OrderControllerDoc;
import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.dto.response.PopularStockCodesResponse;
import com.infina.hissenet.dto.response.PortfolioStockQuantityResponse;
import com.infina.hissenet.dto.response.RecentOrderResponse;
import com.infina.hissenet.service.abstracts.IOrderService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController implements OrderControllerDoc {

	private final IOrderService service;

	public OrderController(IOrderService service) {
		this.service = service;
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
		ApiResponse<OrderResponse> response = ApiResponse.created("Order created successfully",
				service.createOrder(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override
	@PatchMapping("/{id}")
	public ApiResponse<OrderResponse> updateOrder(@PathVariable Long id,
			@Valid @RequestBody OrderUpdateRequest request) {
		return ApiResponse.ok("Order updated successfully", service.updateOrder(id, request));
	}

	@Override
	@GetMapping("/{id}")
	public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
		return ApiResponse.ok("Order retrieved successfully", service.getOrderById(id));
	}

	@Override
	@GetMapping
	public ApiResponse<List<OrderResponse>> getAllOrders() {
		return ApiResponse.ok("Orders retrieved successfully", service.getAllOrders());
	}

	@GetMapping("/by-customer")
	public ApiResponse<List<OrderResponse>> getOrdersByCustomerId(@RequestParam Long customerId) {
		return ApiResponse.ok("Orders by customer retrieved successfully",
				service.getOrdersByCustomerId(customerId));
	}

	@GetMapping("/owned-quantity")
	public ApiResponse<BigDecimal> getOwnedStockQuantity(@RequestParam Long customerId,
			@RequestParam String stockCode) {
		return ApiResponse.ok("Owned stock quantity calculated successfully",
				service.getOwnedStockQuantity(customerId, stockCode));
	}

	@GetMapping("/portfolio")
	public ApiResponse<List<PortfolioStockQuantityResponse>> getPortfolio(@RequestParam Long customerId) {
		return ApiResponse.ok("Customer portfolio retrieved successfully",
				service.getPortfolioByCustomerId(customerId));
	}
	
	@GetMapping("/recent")
	public ApiResponse<List<RecentOrderResponse>> getLastFiveOrders() {
	    return ApiResponse.ok("Last 5 filled orders retrieved successfully", service.getLastFiveOrders());
	}
	
	@GetMapping("/filled")
	public ApiResponse<List<OrderResponse>> getAllFilledOrders() {
	    return ApiResponse.ok("All FILLED orders retrieved successfully", service.getAllFilledOrders());
	}
	
	@GetMapping("/filled/today")
	public ApiResponse<List<OrderResponse>> getTodayFilledOrders() {
	    return ApiResponse.ok("Today's FILLED orders retrieved successfully", service.getTodayFilledOrders());
	}
	
	@GetMapping("/filled/today/volume")
	public ApiResponse<BigDecimal> getTodayTotalTradeVolume() {
	    return ApiResponse.ok("Today's total trade volume calculated successfully",
	            service.getTodayTotalTradeVolume());
	}
	
	@GetMapping("/popular")
	public ApiResponse<List<PopularStockCodesResponse>> getPopularStockCodes() {
	    List<PopularStockCodesResponse> popularStockCodes = service.getPopularStockCodes();
	    return ApiResponse.ok("Top 10 most popular stocks listed successfully", popularStockCodes);
	}

	@GetMapping("/volume/total")
	public ApiResponse<BigDecimal> getTotalTradeVolume() {
	    return ApiResponse.ok("Total trade volume calculated successfully", service.getTotalTradeVolume());
	}


}
