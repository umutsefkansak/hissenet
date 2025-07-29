package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.AddressCreateDto;
import com.infina.hissenet.dto.request.AddressUpdateDto;
import com.infina.hissenet.dto.response.AddressResponse;
import com.infina.hissenet.entity.Address;
import com.infina.hissenet.mapper.AddressMapper;
import com.infina.hissenet.repository.AddressRepository;
import com.infina.hissenet.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository,
                          CustomerRepository customerRepository,
                          AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * Yeni adres oluşturur
     */
    public AddressResponse createAddress(AddressCreateDto createAddressDto) {
        // Müşterinin varlığını kontrol et
        if (!customerRepository.existsById(createAddressDto.customerId())) {
            throw new EntityNotFoundException("Customer not found with id: " + createAddressDto.customerId());
        }

        Address address = addressMapper.toEntity(createAddressDto);

        // Customer referansını doğru şekilde set et
        address.setCustomer(customerRepository.getReferenceById(createAddressDto.customerId()));

        // Eğer primary adres olarak işaretlendiyse, aynı müşteriye ait diğer adreslerin primary durumunu kaldır
        if (Boolean.TRUE.equals(createAddressDto.isPrimary())) {
            clearPrimaryAddresses(createAddressDto.customerId());
        } else if (createAddressDto.isPrimary() == null) {
            // İlk adres otomatik olarak primary yapılabilir
            long addressCount = addressRepository.countByCustomerId(createAddressDto.customerId());
            if (addressCount == 0) {
                address.setPrimary(true);
            }
        }

        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    /**
     * ID'ye göre adres getirir
     */
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));
        return addressMapper.toDto(address);
    }

    /**
     * ID'ye göre adres getirir (müşteri bilgisi ile birlikte)
     */
    @Transactional(readOnly = true)
    public AddressResponse getAddressWithCustomerById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));
        return addressMapper.toDto(address);
    }

    /**
     * Tüm adresleri getirir
     */
    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(addressMapper::toDto)
                .toList();
    }

    /**
     * Sayfalı adres listesi getirir
     */
    @Transactional(readOnly = true)
    public Page<AddressResponse> getAllAddresses(Pageable pageable) {
        return addressRepository.findAll(pageable)
                .map(addressMapper::toDto);
    }

    /**
     * Müşteri ID'sine göre adresleri getirir
     */
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByCustomerId(Long customerId) {
        return addressRepository.findByCustomerId(customerId)
                .stream()
                .map(addressMapper::toDto)
                .toList();
    }

    /**
     * Müşterinin ana adresini getirir
     */
    @Transactional(readOnly = true)
    public Optional<AddressResponse> getPrimaryAddressByCustomerId(Long customerId) {
        return addressRepository.findByCustomerIdAndIsPrimaryTrue(customerId)
                .map(addressMapper::toDto);
    }

    /**
     * Adresi günceller
     */
    public AddressResponse updateAddress(Long id, AddressUpdateDto updateAddressDto) {
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));

        // Müşteri değiştiriliyorsa, yeni müşterinin varlığını kontrol et
        if (updateAddressDto.customerId() != null &&
                !updateAddressDto.customerId().equals(existingAddress.getCustomer().getId())) {
            if (!customerRepository.existsById(updateAddressDto.customerId())) {
                throw new EntityNotFoundException("Customer not found with id: " + updateAddressDto.customerId());
            }
            existingAddress.setCustomer(customerRepository.getReferenceById(updateAddressDto.customerId()));
        }

        // Eğer primary adres olarak güncelleniyorsa, aynı müşteriye ait diğer adreslerin primary durumunu kaldır
        if (Boolean.TRUE.equals(updateAddressDto.isPrimary()) &&
                !Boolean.TRUE.equals(existingAddress.getPrimary())) {
            clearPrimaryAddresses(existingAddress.getCustomer().getId());
        }

        addressMapper.updateEntityFromDto(updateAddressDto, existingAddress);
        Address updatedAddress = addressRepository.save(existingAddress);
        return addressMapper.toDto(updatedAddress);
    }

    /**
     * Adresi siler
     */
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new EntityNotFoundException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
    }

    /**
     * Adresin var olup olmadığını kontrol eder
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return addressRepository.existsById(id);
    }

    /**
     * Müşterinin tüm adreslerini siler
     */
    public void deleteAllAddressesByCustomerId(Long customerId) {
        addressRepository.deleteByCustomerId(customerId);
    }

    /**
     * Aynı müşteriye ait diğer adreslerin primary durumunu kaldırır
     */
    private void clearPrimaryAddresses(Long customerId) {
        List<Address> primaryAddresses = addressRepository.findAllByCustomerIdAndIsPrimaryTrue(customerId);
        primaryAddresses.forEach(address -> address.setPrimary(false));
        addressRepository.saveAll(primaryAddresses);
    }
}