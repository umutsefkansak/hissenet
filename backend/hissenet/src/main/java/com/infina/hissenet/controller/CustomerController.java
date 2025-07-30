package com.infina.hissenet.controller;


import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/individual")
    public ResponseEntity<ApiResponse<CustomerDto>> createIndividualCustomer(
            @Valid @RequestBody IndividualCustomerCreateDto dto) {
        CustomerDto createdCustomer = customerService.createIndividualCustomer(dto);
        ApiResponse<CustomerDto> response = ApiResponse.ok("Individual customer created successfully", createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/individual/{id}")
    public ApiResponse<CustomerDto> updateIndividualCustomer(
            @PathVariable Long id,
            @Valid @RequestBody IndividualCustomerUpdateDto dto) {
        return ApiResponse.ok("Individual customer updated successfully",
                customerService.updateIndividualCustomer(id, dto));
    }

    @PostMapping("/corporate")
    public ResponseEntity<ApiResponse<CustomerDto>> createCorporateCustomer(
            @Valid @RequestBody CorporateCustomerCreateDto dto) {
        CustomerDto createdCustomer = customerService.createCorporateCustomer(dto);
        ApiResponse<CustomerDto> response = ApiResponse.ok("Corporate customer created successfully", createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/corporate/{id}")
    public ApiResponse<CustomerDto> updateCorporateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CorporateCustomerUpdateDto dto) {
        return ApiResponse.ok("Corporate customer updated successfully",
                customerService.updateCorporateCustomer(id, dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerDto> getCustomerById(@PathVariable Long id) {
        return ApiResponse.ok("Customer retrieved successfully", customerService.getCustomerById(id));
    }

    @GetMapping
    public ApiResponse<List<CustomerDto>> getAllCustomers() {
        return ApiResponse.ok("All customers retrieved successfully", customerService.getAllCustomers());
    }

    @GetMapping("/page")
    public ApiResponse<Page<CustomerDto>> getAllCustomersPaged(Pageable pageable) {
        return ApiResponse.ok("Paged customers retrieved successfully", customerService.getAllCustomers(pageable));
    }

    @GetMapping("/email/{email}")
    public ApiResponse<CustomerDto> getCustomerByEmail(@PathVariable String email) {
        Optional<CustomerDto> customer = customerService.getCustomerByEmail(email);
        if (customer.isPresent()) {
            return ApiResponse.ok("Customer retrieved successfully", customer.get());
        } else {
            return ApiResponse.ok("No customer found with email: " + email);
        }
    }

    @GetMapping("/customer-number/{customerNumber}")
    public ApiResponse<CustomerDto> getCustomerByCustomerNumber(@PathVariable String customerNumber) {
        Optional<CustomerDto> customer = customerService.getCustomerByCustomerNumber(customerNumber);
        if (customer.isPresent()) {
            return ApiResponse.ok("Customer retrieved successfully", customer.get());
        } else {
            return ApiResponse.ok("No customer found with customer number: " + customerNumber);
        }
    }


    @GetMapping("/individual")
    public ApiResponse<List<CustomerDto>> getIndividualCustomers() {
        return ApiResponse.ok("Individual customers retrieved successfully",
                customerService.getIndividualCustomers());
    }

    @GetMapping("/corporate")
    public ApiResponse<List<CustomerDto>> getCorporateCustomers() {
        return ApiResponse.ok("Corporate customers retrieved successfully",
                customerService.getCorporateCustomers());
    }


    @PostMapping("/{id}/verify-kyc")
    public ApiResponse<CustomerDto> verifyKyc(@PathVariable Long id) {
        return ApiResponse.ok("Customer KYC verified successfully", customerService.verifyKyc(id));
    }

    @PostMapping("/{id}/unverify-kyc")
    public ApiResponse<CustomerDto> unverifyKyc(@PathVariable Long id) {
        return ApiResponse.ok("Customer KYC unverified successfully", customerService.unverifyKyc(id));
    }

    @GetMapping("/kyc-verified")
    public ApiResponse<List<CustomerDto>> getKycVerifiedCustomers() {
        return ApiResponse.ok("KYC verified customers retrieved successfully",
                customerService.getKycVerifiedCustomers());
    }

    @GetMapping("/kyc-unverified")
    public ApiResponse<List<CustomerDto>> getKycUnverifiedCustomers() {
        return ApiResponse.ok("KYC unverified customers retrieved successfully",
                customerService.getKycUnverifiedCustomers());
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.ok("Customer deleted successfully");
    }


    @GetMapping("/{id}/exists")
    public ApiResponse<Boolean> existsById(@PathVariable Long id) {
        return ApiResponse.ok("Customer existence checked", customerService.existsById(id));
    }

    @GetMapping("/email/{email}/exists")
    public ApiResponse<Boolean> existsByEmail(@PathVariable String email) {
        return ApiResponse.ok("Email existence checked", customerService.existsByEmail(email));
    }
}