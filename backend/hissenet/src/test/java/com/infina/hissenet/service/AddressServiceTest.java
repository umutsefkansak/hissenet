package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.AddressCreateRequest;
import com.infina.hissenet.dto.request.AddressUpdateRequest;
import com.infina.hissenet.dto.response.AddressResponse;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.common.IndividualCustomerDto;
import com.infina.hissenet.entity.Address;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.entity.enums.AddressType;
import com.infina.hissenet.entity.enums.IncomeRange;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.exception.address.AddressNotFoundException;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.mapper.AddressMapper;
import com.infina.hissenet.repository.AddressRepository;
import com.infina.hissenet.service.abstracts.ICustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ICustomerService customerService;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private AddressResponse addressResponse;
    private AddressCreateRequest addressCreateRequest;
    private AddressUpdateRequest addressUpdateRequest;
    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        // Customer setup
        customer = new IndividualCustomer();
        customer.setId(1L);
        customer.setEmail("john.doe@example.com");
        customer.setCustomerNumber("IND-123456789ABC");

        customerDto = new IndividualCustomerDto(
                1L, "IND-123456789ABC", "john.doe@example.com", "+905551234567",
                "Turkish", false, "INDIVIDUAL", "John", null, "Doe",
                "12345678901", LocalDate.of(1990, 1, 1), "Istanbul", "MALE",
                "Jane Doe", "Jack Doe", "Engineer", "University", RiskProfile.MODERATE,
                BigDecimal.valueOf(0.25), IncomeRange.RANGE_0_10K
        );

        // Address setup
        address = new Address();
        address.setId(1L);
        address.setAddressType(AddressType.HOME);
        address.setStreet("Test Street 123");
        address.setDistrict("Test District");
        address.setCity("Istanbul");
        address.setState("Istanbul");
        address.setCountry("Turkey");
        address.setPostalCode("34000");
        address.setPrimary(true);
        address.setCustomer(customer);

        addressResponse = new AddressResponse(
                1L,
                AddressType.HOME,
                "Test Street 123",
                "Test District",
                "Istanbul",
                "Istanbul",
                "Turkey",
                "34000",
                true,
                customerDto,
                "Test Street 123, Test District, Istanbul/Istanbul 34000"
        );

        addressCreateRequest = new AddressCreateRequest(
                AddressType.HOME,
                "Test Street 123",
                "Test District",
                "Istanbul",
                "Istanbul",
                "Turkey",
                "34000",
                true,
                1L
        );

        addressUpdateRequest = new AddressUpdateRequest(
                AddressType.WORK,
                "Updated Street 456",
                "Updated District",
                "Ankara",
                "Ankara",
                "Turkey",
                "06000",
                false,
                1L
        );
    }

    @Test
    void createAddress_WhenCustomerExistsAndFirstAddress_ShouldCreateAsPrimary() {
        // Given
        AddressCreateRequest requestWithNullPrimary = new AddressCreateRequest(
                AddressType.HOME, "Test Street", "District", "City", "State", "Country", "12345", null, 1L
        );

        when(customerService.existsById(1L)).thenReturn(true);
        when(customerService.getReferenceById(1L)).thenReturn(customer);
        when(addressRepository.countByCustomerId(1L)).thenReturn(0L);
        when(addressMapper.toEntity(requestWithNullPrimary)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        AddressResponse result = addressService.createAddress(requestWithNullPrimary);

        // Then
        assertNotNull(result);
        assertTrue(address.getPrimary());
        verify(customerService).existsById(1L);
        verify(customerService).getReferenceById(1L);
        verify(addressRepository).countByCustomerId(1L);
        verify(addressRepository).save(address);
    }

    @Test
    void createAddress_WhenCustomerExistsAndNotFirstAddress_ShouldCreateAsSecondary() {
        // Given
        AddressCreateRequest requestWithNullPrimary = new AddressCreateRequest(
                AddressType.WORK, "Work Street", "District", "City", "State", "Country", "12345", null, 1L
        );

        Address secondaryAddress = new Address();
        secondaryAddress.setCustomer(customer);

        when(customerService.existsById(1L)).thenReturn(true);
        when(customerService.getReferenceById(1L)).thenReturn(customer);
        when(addressRepository.countByCustomerId(1L)).thenReturn(1L);
        when(addressMapper.toEntity(requestWithNullPrimary)).thenReturn(secondaryAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(secondaryAddress);
        when(addressMapper.toDto(secondaryAddress)).thenReturn(addressResponse);

        // When
        AddressResponse result = addressService.createAddress(requestWithNullPrimary);

        // Then
        assertNotNull(result);
        assertFalse(secondaryAddress.getPrimary());
        verify(addressRepository).countByCustomerId(1L);
    }

    @Test
    void createAddress_WhenSetAsPrimary_ShouldClearOtherPrimaryAddresses() {
        // Given
        when(customerService.existsById(1L)).thenReturn(true);
        when(customerService.getReferenceById(1L)).thenReturn(customer);
        when(addressMapper.toEntity(addressCreateRequest)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        Address existingPrimary = new Address();
        existingPrimary.setPrimary(true);
        when(addressRepository.findAllByCustomerIdAndIsPrimaryTrue(1L))
                .thenReturn(Arrays.asList(existingPrimary));

        // When
        AddressResponse result = addressService.createAddress(addressCreateRequest);

        // Then
        assertNotNull(result);
        assertFalse(existingPrimary.getPrimary());
        verify(addressRepository).findAllByCustomerIdAndIsPrimaryTrue(1L);
        verify(addressRepository).saveAll(Arrays.asList(existingPrimary));
    }

    @Test
    void createAddress_WhenCustomerNotExists_ShouldThrowException() {
        // Given
        when(customerService.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(CustomerNotFoundException.class,
                () -> addressService.createAddress(addressCreateRequest));

        verify(customerService).existsById(1L);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void getAddressById_WhenAddressExists_ShouldReturnAddress() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        AddressResponse result = addressService.getAddressById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(AddressType.HOME, result.addressType());
        assertEquals("Test Street 123", result.street());

        verify(addressRepository).findById(1L);
        verify(addressMapper).toDto(address);
    }

    @Test
    void getAddressById_WhenAddressNotExists_ShouldThrowException() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AddressNotFoundException.class,
                () -> addressService.getAddressById(1L));

        verify(addressRepository).findById(1L);
        verify(addressMapper, never()).toDto(any());
    }

    @Test
    void getAllAddresses_ShouldReturnAllAddresses() {
        // Given
        Address workAddress = new Address();
        workAddress.setId(2L);
        workAddress.setAddressType(AddressType.WORK);

        List<Address> addresses = Arrays.asList(address, workAddress);
        when(addressRepository.findAll()).thenReturn(addresses);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);
        when(addressMapper.toDto(workAddress)).thenReturn(addressResponse);

        // When
        List<AddressResponse> result = addressService.getAllAddresses();

        // Then
        assertEquals(2, result.size());
        verify(addressRepository).findAll();
    }

    @Test
    void getAllAddressesWithPageable_ShouldReturnPagedAddresses() {
        // Given
        List<Address> addresses = Arrays.asList(address);
        Page<Address> addressPage = new PageImpl<>(addresses);
        Pageable pageable = PageRequest.of(0, 10);

        when(addressRepository.findAll(pageable)).thenReturn(addressPage);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        Page<AddressResponse> result = addressService.getAllAddresses(pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalElements());

        verify(addressRepository).findAll(pageable);
    }

    @Test
    void getAddressesByCustomerId_ShouldReturnCustomerAddresses() {
        // Given
        List<Address> customerAddresses = Arrays.asList(address);
        when(addressRepository.findByCustomerId(1L)).thenReturn(customerAddresses);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        List<AddressResponse> result = addressService.getAddressesByCustomerId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).customer().id());

        verify(addressRepository).findByCustomerId(1L);
    }

    @Test
    void getPrimaryAddressByCustomerId_WhenExists_ShouldReturnPrimaryAddress() {
        // Given
        when(addressRepository.findByCustomerIdAndIsPrimaryTrue(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        AddressResponse result = addressService.getPrimaryAddressByCustomerId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isPrimary());
        assertEquals(1L, result.customer().id());

        verify(addressRepository).findByCustomerIdAndIsPrimaryTrue(1L);
    }

    @Test
    void getPrimaryAddressByCustomerId_WhenNotExists_ShouldThrowException() {
        // Given
        when(addressRepository.findByCustomerIdAndIsPrimaryTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AddressNotFoundException.class,
                () -> addressService.getPrimaryAddressByCustomerId(1L));

        verify(addressRepository).findByCustomerIdAndIsPrimaryTrue(1L);
    }

    @Test
    void updateAddress_WhenValidUpdate_ShouldUpdateSuccessfully() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        AddressResponse result = addressService.updateAddress(1L, addressUpdateRequest);

        // Then
        assertNotNull(result);
        verify(addressMapper).updateEntityFromDto(addressUpdateRequest, address);
        verify(addressRepository).save(address);
    }

    @Test
    void updateAddress_WhenAddressNotExists_ShouldThrowException() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AddressNotFoundException.class,
                () -> addressService.updateAddress(1L, addressUpdateRequest));

        verify(addressRepository).findById(1L);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void updateAddress_WhenCustomerChangedAndNewCustomerNotExists_ShouldThrowException() {
        // Given
        AddressUpdateRequest requestWithDifferentCustomer = new AddressUpdateRequest(
                AddressType.HOME, "Street", "District", "City", "State", "Country", "12345", false, 2L
        );

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(customerService.existsById(2L)).thenReturn(false);

        // When & Then
        assertThrows(CustomerNotFoundException.class,
                () -> addressService.updateAddress(1L, requestWithDifferentCustomer));

        verify(customerService).existsById(2L);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void updateAddress_WhenSetAsPrimary_ShouldClearOtherPrimaryAddresses() {
        // Given
        address.setPrimary(false); // Initially not primary

        AddressUpdateRequest setPrimaryRequest = new AddressUpdateRequest(
                AddressType.HOME, "Street", "District", "City", "State", "Country", "12345", true, 1L
        );

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        Address existingPrimary = new Address();
        existingPrimary.setPrimary(true);
        when(addressRepository.findAllByCustomerIdAndIsPrimaryTrue(1L))
                .thenReturn(Arrays.asList(existingPrimary));

        // When
        AddressResponse result = addressService.updateAddress(1L, setPrimaryRequest);

        // Then
        assertNotNull(result);
        assertFalse(existingPrimary.getPrimary());
        verify(addressRepository).findAllByCustomerIdAndIsPrimaryTrue(1L);
        verify(addressRepository).saveAll(Arrays.asList(existingPrimary));
    }

    @Test
    void deleteAddress_WhenAddressExists_ShouldDeleteSuccessfully() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // When
        addressService.deleteAddress(1L);

        // Then
        assertTrue(address.getDeleted()); // Soft delete check
        verify(addressRepository).findById(1L);
        verify(addressRepository).save(address);
    }

    @Test
    void deleteAddress_WhenAddressNotExists_ShouldThrowException() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AddressNotFoundException.class,
                () -> addressService.deleteAddress(1L));

        verify(addressRepository).findById(1L);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void deleteAllAddressesByCustomerId_ShouldDeleteAllCustomerAddresses() {
        // Given
        Address address1 = new Address();
        Address address2 = new Address();
        List<Address> addresses = Arrays.asList(address1, address2);

        when(addressRepository.findByCustomerId(1L)).thenReturn(addresses);
        when(addressRepository.save(any(Address.class))).thenReturn(address1, address2);

        // When
        addressService.deleteAllAddressesByCustomerId(1L);

        // Then
        assertTrue(address1.getDeleted());
        assertTrue(address2.getDeleted());
        verify(addressRepository).findByCustomerId(1L);
        verify(addressRepository, times(2)).save(any(Address.class));
    }

    @Test
    void existsById_WhenAddressExists_ShouldReturnTrue() {
        // Given
        when(addressRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = addressService.existsById(1L);

        // Then
        assertTrue(result);
        verify(addressRepository).existsById(1L);
    }

    @Test
    void existsById_WhenAddressNotExists_ShouldReturnFalse() {
        // Given
        when(addressRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = addressService.existsById(1L);

        // Then
        assertFalse(result);
        verify(addressRepository).existsById(1L);
    }

    @Test
    void createAddress_WhenCustomerChangedInUpdate_ShouldSetNewCustomer() {
        // Given
        Customer newCustomer = new IndividualCustomer();
        newCustomer.setId(2L);

        AddressUpdateRequest requestWithNewCustomer = new AddressUpdateRequest(
                AddressType.HOME, "Street", "District", "City", "State", "Country", "12345", false, 2L
        );

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(customerService.existsById(2L)).thenReturn(true);
        when(customerService.getReferenceById(2L)).thenReturn(newCustomer);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);

        // When
        AddressResponse result = addressService.updateAddress(1L, requestWithNewCustomer);

        // Then
        assertNotNull(result);
        assertEquals(newCustomer, address.getCustomer());
        verify(customerService).existsById(2L);
        verify(customerService).getReferenceById(2L);
    }

    @Test
    void clearPrimaryAddresses_ShouldSetAllPrimaryAddressesToFalse() {
        // Given - This tests the private method indirectly through createAddress
        Address existingPrimary1 = new Address();
        existingPrimary1.setPrimary(true);
        Address existingPrimary2 = new Address();
        existingPrimary2.setPrimary(true);

        when(customerService.existsById(1L)).thenReturn(true);
        when(customerService.getReferenceById(1L)).thenReturn(customer);
        when(addressMapper.toEntity(addressCreateRequest)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressResponse);
        when(addressRepository.findAllByCustomerIdAndIsPrimaryTrue(1L))
                .thenReturn(Arrays.asList(existingPrimary1, existingPrimary2));

        // When
        addressService.createAddress(addressCreateRequest);

        // Then
        assertFalse(existingPrimary1.getPrimary());
        assertFalse(existingPrimary2.getPrimary());
        verify(addressRepository).saveAll(Arrays.asList(existingPrimary1, existingPrimary2));
    }
}