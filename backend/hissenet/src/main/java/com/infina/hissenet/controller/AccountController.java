package com.infina.hissenet.controller;

import java.util.List;

import com.infina.hissenet.utils.MessageUtils;
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
import com.infina.hissenet.controller.doc.AccountControllerDoc;
import com.infina.hissenet.dto.request.AccountCreateRequest;
import com.infina.hissenet.dto.request.AccountUpdateRequest;
import com.infina.hissenet.dto.response.AccountResponse;
import com.infina.hissenet.service.abstracts.IAccountService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/accounts")
public class AccountController implements AccountControllerDoc {
	
	private final IAccountService service;

	public AccountController(IAccountService service) {
		this.service = service;
	}
	
	@Override
	@PostMapping
	public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
			@Valid @RequestBody AccountCreateRequest request) {
		ApiResponse<AccountResponse> response = ApiResponse.created(
				MessageUtils.getMessage("account.created.successfully"),
				service.createAccount(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override
	@PutMapping("/{id}")
	public ApiResponse<AccountResponse> updateAccount(@PathVariable Long id,
			@Valid @RequestBody AccountUpdateRequest request) {
		return ApiResponse.ok(MessageUtils.getMessage("account.updated.successfully"), service.updateAccount(id, request));
	}

	@Override
	@GetMapping("/{id}")
	public ApiResponse<AccountResponse> getAccount(@PathVariable Long id) {
		return ApiResponse.ok(MessageUtils.getMessage("account.retrieved.successfully"), service.getAccountById(id));
	}

	@Override
	@GetMapping
	public ApiResponse<List<AccountResponse>> getAllAccounts() {
		return ApiResponse.ok(MessageUtils.getMessage("account.list.retrieved.successfully"),  service.getAllAccounts());
	}

	@Override
	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
		service.deleteAccount(id);
		return ApiResponse.ok(MessageUtils.getMessage("account.deleted.successfully"));
	}

}
