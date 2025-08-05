package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.AddressControllerDoc;
import com.infina.hissenet.dto.request.AddressCreateRequest;
import com.infina.hissenet.dto.request.AddressUpdateRequest;
import com.infina.hissenet.dto.response.AddressResponse;
import com.infina.hissenet.service.abstracts.IAddressService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController implements AddressControllerDoc {

    private final IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@Valid @RequestBody AddressCreateRequest dto) {
        AddressResponse createdAddress = addressService.createAddress(dto);
        ApiResponse<AddressResponse> response = ApiResponse.ok("Address created successfully", createdAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getAddressById(@PathVariable Long id) {
        return ApiResponse.ok("Address retrieved successfully", addressService.getAddressById(id));
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAllAddresses() {
        return ApiResponse.ok("All addresses retrieved successfully", addressService.getAllAddresses());
    }

    @GetMapping("/page")
    public ApiResponse<Page<AddressResponse>> getAllAddressesPaged(Pageable pageable) {
        return ApiResponse.ok("Paged addresses retrieved successfully", addressService.getAllAddresses(pageable));
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<AddressResponse>> getAddressesByCustomerId(@PathVariable Long customerId) {
        return ApiResponse.ok("Customer addresses retrieved successfully", addressService.getAddressesByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/primary")
    public ApiResponse<AddressResponse> getPrimaryAddressByCustomerId(@PathVariable Long customerId) {
        return ApiResponse.ok("Primary address retrieved successfully",
                addressService.getPrimaryAddressByCustomerId(customerId));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable Long id,
                                                      @Valid @RequestBody AddressUpdateRequest dto) {
        return ApiResponse.ok("Address updated successfully", addressService.updateAddress(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.ok("Address deleted successfully");
    }

    @DeleteMapping("/customer/{customerId}")
    public ApiResponse<Void> deleteAllAddressesByCustomerId(@PathVariable Long customerId) {
        addressService.deleteAllAddressesByCustomerId(customerId);
        return ApiResponse.ok("All customer addresses deleted successfully");
    }
}