package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.AddressCreateDto;
import com.infina.hissenet.dto.request.AddressUpdateDto;
import com.infina.hissenet.dto.response.AddressResponse;
import com.infina.hissenet.entity.Address;
import com.infina.hissenet.exception.address.AddressNotFoundException;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.mapper.AddressMapper;
import com.infina.hissenet.repository.AddressRepository;
import com.infina.hissenet.service.abstracts.IAddressService;
import com.infina.hissenet.service.abstracts.ICustomerService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddressService extends GenericServiceImpl<Address, Long> implements IAddressService {

    private final AddressRepository addressRepository;
    private final ICustomerService customerService;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository,
                          ICustomerService customerService,
                          AddressMapper addressMapper) {
        super(addressRepository);
        this.addressRepository = addressRepository;
        this.customerService = customerService;
        this.addressMapper = addressMapper;
    }


    public AddressResponse createAddress(AddressCreateDto createAddressDto) {

        if (!customerService.existsById(createAddressDto.customerId())) {
            throw new CustomerNotFoundException(createAddressDto.customerId());
        }

        Address address = addressMapper.toEntity(createAddressDto);


        address.setCustomer(customerService.getReferenceById(createAddressDto.customerId()));

        if (Boolean.TRUE.equals(createAddressDto.isPrimary())) {
            clearPrimaryAddresses(createAddressDto.customerId());
        } else if (createAddressDto.isPrimary() == null) {
            long addressCount = addressRepository.countByCustomerId(createAddressDto.customerId());
            if (addressCount == 0) {
                address.setPrimary(true);
            }
        }

        Address savedAddress = save(address);
        return addressMapper.toDto(savedAddress);
    }


    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long id) {
        Address address = findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
        return addressMapper.toDto(address);
    }


    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses() {
        return findAll()
                .stream()
                .map(addressMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AddressResponse> getAllAddresses(Pageable pageable) {
        return findAll(pageable)
                .map(addressMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByCustomerId(Long customerId) {
        return addressRepository.findByCustomerId(customerId)
                .stream()
                .map(addressMapper::toDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public AddressResponse getPrimaryAddressByCustomerId(Long customerId) {
        return addressRepository.findByCustomerIdAndIsPrimaryTrue(customerId)
                .map(addressMapper::toDto)
                .orElseThrow(() -> new AddressNotFoundException("No primary address found for customer ID: " + customerId));
    }


    public AddressResponse updateAddress(Long id, AddressUpdateDto updateAddressDto) {
        Address existingAddress = findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));


        if (updateAddressDto.customerId() != null &&
                !updateAddressDto.customerId().equals(existingAddress.getCustomer().getId())) {
            if (!customerService.existsById(updateAddressDto.customerId())) {
                throw new CustomerNotFoundException(updateAddressDto.customerId());
            }
            existingAddress.setCustomer(customerService.getReferenceById(updateAddressDto.customerId()));
        }


        if (Boolean.TRUE.equals(updateAddressDto.isPrimary()) &&
                !Boolean.TRUE.equals(existingAddress.getPrimary())) {
            clearPrimaryAddresses(existingAddress.getCustomer().getId());
        }


        addressMapper.updateEntityFromDto(updateAddressDto, existingAddress);

        Address updatedAddress = update(existingAddress);
        return addressMapper.toDto(updatedAddress);
    }


    public void deleteAddress(Long id) {
        Address address = findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
        delete(address);
    }


    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return addressRepository.existsById(id);
    }


    public void deleteAllAddressesByCustomerId(Long customerId) {
        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        addresses.forEach(this::delete);
    }

    private void clearPrimaryAddresses(Long customerId) {
        List<Address> primaryAddresses = addressRepository.findAllByCustomerIdAndIsPrimaryTrue(customerId);
        primaryAddresses.forEach(address -> address.setPrimary(false));
        addressRepository.saveAll(primaryAddresses);
    }
}