package com.infina.hissenet.controller;

import java.util.List;

import com.infina.hissenet.dto.request.ForgotPasswordRequest;
import com.infina.hissenet.utils.MessageUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.EmployeeControllerDoc;
import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.service.abstracts.IEmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/employees")
public class EmployeeController implements EmployeeControllerDoc {

	private final IEmployeeService service;

	public EmployeeController(IEmployeeService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
			@Valid @RequestBody EmployeeCreateRequest request) {
		ApiResponse<EmployeeResponse> response = ApiResponse.created(MessageUtils.getMessage("employee.created.successfully"),
				service.createEmployee(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping
	public ApiResponse<EmployeeResponse> updateEmployee(@Valid @RequestBody EmployeeUpdateRequest request) {
		return ApiResponse.ok(MessageUtils.getMessage("employee.updated.successfully"), service.updateEmployee(request));
	}

	@GetMapping("/{id}")
	public ApiResponse<EmployeeResponse> getEmployee(@PathVariable Long id) {
		return ApiResponse.ok(MessageUtils.getMessage("employee.retrieved.successfully"),  service.getEmployeeById(id));
	}

	@GetMapping
	public ApiResponse<List<EmployeeResponse>> getAllEmployees() {
		return ApiResponse.ok(MessageUtils.getMessage("employee.list.retrieved.successfully"),  service.getAllEmployees());
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
		service.deleteEmployee(id);
		return ApiResponse.ok(MessageUtils.getMessage("employee.deleted.successfully"));
	}
	@PatchMapping("/changePassword")
	public ApiResponse<Void> changePassword(@Valid @RequestBody ForgotPasswordRequest request){
		service.changePassword(request);
		return ApiResponse.ok(MessageUtils.getMessage("employee.password.changed.successfully"));
	}

	@GetMapping("/pageable")
	public ApiResponse<Page<EmployeeResponse>> getAllEmployeesPageable(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {

		Page<EmployeeResponse> employees = service.getAllEmployeesPageable(page, size, sortBy, sortDir);
		return ApiResponse.ok(MessageUtils.getMessage("employee.paged.retrieved.successfully"), employees);
	}

}
