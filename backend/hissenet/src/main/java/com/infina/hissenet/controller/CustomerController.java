package com.infina.hissenet.controller;


import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.CustomerControllerDoc;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.service.abstracts.ICustomerService;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController implements CustomerControllerDoc {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/individual")
    public ResponseEntity<ApiResponse<CustomerDto>> createIndividualCustomer(
            @Valid @RequestBody IndividualCustomerCreateRequest dto) {
        CustomerDto createdCustomer = customerService.createIndividualCustomer(dto);
        ApiResponse<CustomerDto> response = ApiResponse.ok(MessageUtils.getMessage("customer.individual.created.successfully"), createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/individual/{id}")
    public ApiResponse<CustomerDto> updateIndividualCustomer(
            @PathVariable Long id,
            @Valid @RequestBody IndividualCustomerUpdateRequest dto) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.individual.updated.successfully"),
                customerService.updateIndividualCustomer(id, dto));
    }

    @PostMapping("/corporate")
    public ResponseEntity<ApiResponse<CustomerDto>> createCorporateCustomer(
            @Valid @RequestBody CorporateCustomerCreateRequest dto) {
        CustomerDto createdCustomer = customerService.createCorporateCustomer(dto);
        ApiResponse<CustomerDto> response = ApiResponse.ok(MessageUtils.getMessage("customer.corporate.created.successfully"),  createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/corporate/{id}")
    public ApiResponse<CustomerDto> updateCorporateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CorporateCustomerUpdateRequest dto) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.corporate.updated.successfully"),
                customerService.updateCorporateCustomer(id, dto));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerDto> getCustomerById(@PathVariable Long id) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.retrieved.successfully"), customerService.getCustomerById(id));
    }

    @GetMapping
    public ApiResponse<List<CustomerDto>> getAllCustomers() {
        return ApiResponse.ok(MessageUtils.getMessage("customer.list.retrieved.successfully"), customerService.getAllCustomers());
    }

    @GetMapping("/page")
    public ApiResponse<Page<CustomerDto>> getAllCustomersPaged(Pageable pageable) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.paged.retrieved.successfully"),  customerService.getAllCustomers(pageable));
    }

    @GetMapping("/email/{email}")
    public ApiResponse<CustomerDto> getCustomerByEmail(@PathVariable String email) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.retrieved.successfully"),  customerService.getCustomerByEmail(email));
    }

    @GetMapping("/customer-number/{customerNumber}")
    public ApiResponse<CustomerDto> getCustomerByCustomerNumber(@PathVariable String customerNumber) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.retrieved.successfully"),
                customerService.getCustomerByCustomerNumber(customerNumber));
    }


    @GetMapping("/individual")
    public ApiResponse<List<CustomerDto>> getIndividualCustomers() {
        return ApiResponse.ok(MessageUtils.getMessage("customer.individual.list.retrieved.successfully"),
                customerService.getIndividualCustomers());
    }

    @GetMapping("/corporate")
    public ApiResponse<List<CustomerDto>> getCorporateCustomers() {
        return ApiResponse.ok(MessageUtils.getMessage("customer.corporate.list.retrieved.successfully"),
                customerService.getCorporateCustomers());
    }


    @PostMapping("/{id}/verify-kyc")
    public ApiResponse<CustomerDto> verifyKyc(@PathVariable Long id) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.kyc.verified.successfully"),  customerService.verifyKyc(id));
    }

    @PostMapping("/{id}/unverify-kyc")
    public ApiResponse<CustomerDto> unverifyKyc(@PathVariable Long id) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.kyc.unverified.successfully"),  customerService.unverifyKyc(id));
    }

    @GetMapping("/kyc-verified")
    public ApiResponse<List<CustomerDto>> getKycVerifiedCustomers() {
        return ApiResponse.ok(MessageUtils.getMessage("customer.kyc.verified.list.retrieved.successfully"),
                customerService.getKycVerifiedCustomers());
    }

    @GetMapping("/kyc-unverified")
    public ApiResponse<List<CustomerDto>> getKycUnverifiedCustomers() {
        return ApiResponse.ok(MessageUtils.getMessage("customer.kyc.unverified.list.retrieved.successfully"),
                customerService.getKycUnverifiedCustomers());
    }


    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.ok(MessageUtils.getMessage("customer.deleted.successfully"));
    }


    @GetMapping("/{id}/exists")
    public ApiResponse<Boolean> existsById(@PathVariable Long id) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.existence.checked"), customerService.existsById(id));
    }

    @GetMapping("/email/{email}/exists")
    public ApiResponse<Boolean> existsByEmail(@PathVariable String email) {
        return ApiResponse.ok(MessageUtils.getMessage("customer.email.existence.checked"), customerService.existsByEmail(email));
    }
}