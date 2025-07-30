package com.infina.hissenet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.service.EmployeeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/employee")
public class EmployeeController {

	private final EmployeeService service;

	public EmployeeController(EmployeeService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
			@Valid @RequestBody EmployeeCreateRequest request) {
		ApiResponse<EmployeeResponse> response = ApiResponse.created("Employee created successfully",
				service.createEmployee(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping
	public ApiResponse<EmployeeResponse> updateEmployee(@Valid @RequestBody EmployeeUpdateRequest request) {
		return ApiResponse.ok("Employee updated successfully", service.updateEmployee(request));
	}

	@GetMapping("/{id}")
	public ApiResponse<EmployeeResponse> getEmployee(@PathVariable Long id) {
		return ApiResponse.ok("Employee retrieved successfully", service.getEmployeeById(id));
	}

	@GetMapping
	public ApiResponse<List<EmployeeResponse>> getAllEmployees() {
		return ApiResponse.ok("Employees retrieved successfully", service.getAllEmployees());
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
		service.deleteEmployee(id);
		return ApiResponse.ok("Employee deleted successfully");
	}
}
