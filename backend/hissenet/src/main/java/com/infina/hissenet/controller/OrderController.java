package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
		ApiResponse<OrderResponse> response = ApiResponse.created("Order created successfully",
				service.createOrder(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{id}")
	public ApiResponse<OrderResponse> updateOrder(@PathVariable Long id,
			@Valid @RequestBody OrderUpdateRequest request) {
		return ApiResponse.ok("Order updated successfully", service.updateOrder(id, request));
	}

	@GetMapping("/{id}")
	public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
		return ApiResponse.ok("Order retrieved successfully", service.getOrderById(id));
	}

	@GetMapping
	public ApiResponse<List<OrderResponse>> getAllOrders() {
		return ApiResponse.ok("Orders retrieved successfully", service.getAllOrders());
	}

}
