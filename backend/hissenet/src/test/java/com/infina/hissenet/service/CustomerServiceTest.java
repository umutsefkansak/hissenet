package com.infina.hissenet.service;

import com.infina.hissenet.constants.CustomerConstants;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.common.IndividualCustomerDto;
import com.infina.hissenet.dto.common.CorporateCustomerDto;
import com.infina.hissenet.dto.request.IndividualCustomerCreateRequest;
import com.infina.hissenet.dto.request.CorporateCustomerCreateRequest;
import com.infina.hissenet.dto.request.IndividualCustomerUpdateRequest;
import com.infina.hissenet.dto.request.CorporateCustomerUpdateRequest;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.entity.CorporateCustomer;
import com.infina.hissenet.entity.enums.Gender;
import com.infina.hissenet.entity.enums.IncomeRange;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.event.CustomerCreatedEvent;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.exception.customer.EmailAlreadyExistsException;
import com.infina.hissenet.exception.customer.TaxNumberAlreadyExistsException;
import com.infina.hissenet.exception.customer.TcNumberAlreadyExistsException;
import com.infina.hissenet.mapper.CustomerMapper;
import com.infina.hissenet.repository.CustomerRepository;
import com.infina.hissenet.repository.IndividualCustomerRepository;
import com.infina.hissenet.repository.CorporateCustomerRepository;
import com.infina.hissenet.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IndividualCustomerRepository individualCustomerRepository;

    @Mock
    private CorporateCustomerRepository corporateCustomerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CustomerService customerService;

    private IndividualCustomer individualCustomer;
    private CorporateCustomer corporateCustomer;
    private IndividualCustomerDto individualCustomerDto;
    private CorporateCustomerDto corporateCustomerDto;
    private IndividualCustomerCreateRequest individualCreateRequest;
    private CorporateCustomerCreateRequest corporateCreateRequest;

    @BeforeEach
    void setUp() {
        individualCustomer = new IndividualCustomer();
        individualCustomer.setId(1L);
        individualCustomer.setEmail("john.doe@example.com");
        individualCustomer.setPhone("+905551234567");
        individualCustomer.setFirstName("John");
        individualCustomer.setLastName("Doe");
        individualCustomer.setTcNumber("12345678901");
        individualCustomer.setCustomerNumber("IND-123456789ABC");
        individualCustomer.setKycVerified(false);
        individualCustomer.setBirthDate(LocalDate.of(1990, 1, 1));
        individualCustomer.setGender(Gender.MALE);
        individualCustomer.setRiskProfile(RiskProfile.MODERATE);
        individualCustomer.setCommissionRate(BigDecimal.valueOf(0.25));
        individualCustomer.setIncomeRange(IncomeRange.RANGE_0_10K);

        individualCustomerDto = new IndividualCustomerDto(
                1L, "IND-123456789ABC", "john.doe@example.com", "+905551234567",
                "Turkish", false, "INDIVIDUAL", "John", null, "Doe",
                "12345678901", LocalDate.of(1990, 1, 1), "Istanbul", "MALE",
                "Jane Doe", "Jack Doe", "Engineer", "University", RiskProfile.MODERATE,
                BigDecimal.valueOf(0.25), IncomeRange.RANGE_0_10K
        );

        individualCreateRequest = new IndividualCustomerCreateRequest(
                "john.doe@example.com", "+905551234567", "Turkish", "John", null, "Doe",
                "12345678901", LocalDate.of(1990, 1, 1), "Istanbul", Gender.MALE,
                "Jane Doe", "Jack Doe", "Engineer", "University", RiskProfile.MODERATE,
                BigDecimal.valueOf(0.25), IncomeRange.RANGE_0_10K
        );

        corporateCustomer = new CorporateCustomer();
        corporateCustomer.setId(2L);
        corporateCustomer.setEmail("info@company.com");
        corporateCustomer.setPhone("+905551234568");
        corporateCustomer.setCompanyName("Test Company Ltd.");
        corporateCustomer.setTaxNumber("1234567890");
        corporateCustomer.setCustomerNumber("CORP-123456789DEF");
        corporateCustomer.setKycVerified(false);
        corporateCustomer.setCommissionRate(BigDecimal.valueOf(0.15));

        corporateCustomerDto = new CorporateCustomerDto(
                2L, "CORP-123456789DEF", "info@company.com", "+905551234568",
                "Turkish", false, "CORPORATE", "Test Company Ltd.", "1234567890",
                "TR123456", LocalDate.of(2010, 5, 15), "Technology",
                "John Smith", "CEO", "www.testcompany.com", BigDecimal.valueOf(0.15),
                "+905551234569", "98765432101", "john.smith@testcompany.com", "Kadıköy"
        );

        corporateCreateRequest = new CorporateCustomerCreateRequest(
                "info@company.com", "+905551234568", "Turkish", "Test Company Ltd.",
                "1234567890", "TR123456", LocalDate.of(2010, 5, 15), "Technology",
                "John Smith", "CEO", "www.testcompany.com", BigDecimal.valueOf(0.15),
                "+905551234569", "98765432101", "john.smith@testcompany.com", "Kadıköy"
        );
    }

    @Test
    void createIndividualCustomer_ShouldCreateSuccessfully() {
        // Given
        when(customerMapper.toEntity(individualCreateRequest)).thenReturn(individualCustomer);
        when(customerRepository.save(any(IndividualCustomer.class))).thenReturn(individualCustomer);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.createIndividualCustomer(individualCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals(individualCustomerDto.email(), result.email());
        assertTrue(result.customerNumber().startsWith(CustomerConstants.INDIVIDUAL_CUSTOMER_PREFIX));

        ArgumentCaptor<CustomerCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        CustomerCreatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(individualCustomer.getId(), publishedEvent.getCustomerId());
        assertEquals("INDIVIDUAL", publishedEvent.getCustomerType());
    }

    @Test
    void createCorporateCustomer_ShouldCreateSuccessfully() {
        // Given
        when(customerMapper.toEntity(corporateCreateRequest)).thenReturn(corporateCustomer);
        when(customerRepository.save(any(CorporateCustomer.class))).thenReturn(corporateCustomer);
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        CustomerDto result = customerService.createCorporateCustomer(corporateCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals(corporateCustomerDto.email(), result.email());
        assertTrue(result.customerNumber().startsWith(CustomerConstants.CORPORATE_CUSTOMER_PREFIX));

        ArgumentCaptor<CustomerCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        CustomerCreatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(corporateCustomer.getId(), publishedEvent.getCustomerId());
        assertEquals("CORPORATE", publishedEvent.getCustomerType());
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("john.doe@example.com", result.email());
    }

    @Test
    void getCustomerById_WhenCustomerNotExists_ShouldThrowException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(1L));
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() {
        // Given
        List<Customer> customers = Arrays.asList(individualCustomer, corporateCustomer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        List<CustomerDto> result = customerService.getAllCustomers();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.id().equals(1L)));
        assertTrue(result.stream().anyMatch(c -> c.id().equals(2L)));
    }

    @Test
    void getAllCustomersWithPageable_ShouldReturnPagedCustomers() {
        // Given
        List<Customer> customers = Arrays.asList(individualCustomer, corporateCustomer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        Pageable pageable = PageRequest.of(0, 10);

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        Page<CustomerDto> result = customerService.getAllCustomers(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void getCustomerByEmail_WhenExists_ShouldReturnCustomer() {
        // Given
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(individualCustomer));
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerByEmail("john.doe@example.com");

        // Then
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.email());
    }

    @Test
    void getCustomerByEmail_WhenNotExists_ShouldThrowException() {
        // Given
        when(customerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerByEmail("nonexistent@example.com"));
    }

    @Test
    void getCustomerByTcNumber_WhenExists_ShouldReturnCustomer() {
        // Given
        when(individualCustomerRepository.findByTcNumber("12345678901")).thenReturn(Optional.of(individualCustomer));
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerByTcNumber("12345678901");

        // Then
        assertNotNull(result);
        assertEquals(individualCustomerDto.tcNumber(), result instanceof IndividualCustomerDto ?
                ((IndividualCustomerDto) result).tcNumber() : null);
    }

    @Test
    void getCustomerByTaxNumber_WhenExists_ShouldReturnCustomer() {
        // Given
        when(corporateCustomerRepository.findByTaxNumber("1234567890")).thenReturn(Optional.of(corporateCustomer));
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerByTaxNumber("1234567890");

        // Then
        assertNotNull(result);
        assertEquals(corporateCustomerDto.taxNumber(), result instanceof CorporateCustomerDto ?
                ((CorporateCustomerDto) result).taxNumber() : null);
    }

    @Test
    void updateIndividualCustomer_WhenValidUpdate_ShouldUpdateSuccessfully() {
        // Given
        IndividualCustomerUpdateRequest updateRequest = new IndividualCustomerUpdateRequest(
                "newemail@example.com", "+905559876543", "Turkish", "Jane", null, "Smith",
                "98765432109", LocalDate.of(1992, 5, 15), "Ankara", Gender.FEMALE,
                "Mary Smith", "Bob Smith", "Doctor", "PhD", RiskProfile.CONSERVATIVE,
                BigDecimal.valueOf(0.20), IncomeRange.RANGE_ABOVE_100K
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(customerRepository.existsByEmailAndIdNot("newemail@example.com", 1L)).thenReturn(false);
        when(individualCustomerRepository.existsByTcNumberAndIdNot("98765432109", 1L)).thenReturn(false);
        when(customerRepository.save(any(IndividualCustomer.class))).thenReturn(individualCustomer);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.updateIndividualCustomer(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(customerMapper).updateIndividualCustomerFromDto(updateRequest, individualCustomer);
        verify(customerRepository).save(individualCustomer);
    }

    @Test
    void updateIndividualCustomer_WhenEmailExists_ShouldThrowException() {
        // Given
        IndividualCustomerUpdateRequest updateRequest = new IndividualCustomerUpdateRequest(
                "existing@example.com", null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(customerRepository.existsByEmailAndIdNot("existing@example.com", 1L)).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class,
                () -> customerService.updateIndividualCustomer(1L, updateRequest));
    }

    @Test
    void updateIndividualCustomer_WhenTcNumberExists_ShouldThrowException() {
        // Given
        IndividualCustomerUpdateRequest updateRequest = new IndividualCustomerUpdateRequest(
                null, null, null, null, null, null, "11111111111", null, null, null,
                null, null, null, null, null, null, null
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(individualCustomerRepository.existsByTcNumberAndIdNot("11111111111", 1L)).thenReturn(true);

        // When & Then
        assertThrows(TcNumberAlreadyExistsException.class,
                () -> customerService.updateIndividualCustomer(1L, updateRequest));
    }

    @Test
    void updateIndividualCustomer_WhenCustomerNotIndividual_ShouldThrowException() {
        // Given
        IndividualCustomerUpdateRequest updateRequest = new IndividualCustomerUpdateRequest(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(corporateCustomer));

        try (MockedStatic<MessageUtils> messageUtilsMock = mockStatic(MessageUtils.class)) {
            messageUtilsMock.when(() -> MessageUtils.getMessage("customer.not.individual", 1L))
                    .thenReturn("Customer is not individual");

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> customerService.updateIndividualCustomer(1L, updateRequest));
        }
    }

    @Test
    void updateCorporateCustomer_WhenValidUpdate_ShouldUpdateSuccessfully() {
        // Given
        CorporateCustomerUpdateRequest updateRequest = new CorporateCustomerUpdateRequest(
                "newcorp@example.com", "+905559876544", "Turkish", "New Company Name",
                "9876543210", "TR654321", LocalDate.of(2015, 3, 20), "Finance",
                "Jane Smith", "CTO", "www.newcomp.com", BigDecimal.valueOf(0.18),
                "+905559876545", "11122233344", "jane.smith@newcomp.com", "Beyoğlu"
        );

        when(customerRepository.findById(2L)).thenReturn(Optional.of(corporateCustomer));
        when(customerRepository.existsByEmailAndIdNot("newcorp@example.com", 2L)).thenReturn(false);
        when(corporateCustomerRepository.existsByTaxNumberAndIdNot("9876543210", 2L)).thenReturn(false);
        when(customerRepository.save(any(CorporateCustomer.class))).thenReturn(corporateCustomer);
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        CustomerDto result = customerService.updateCorporateCustomer(2L, updateRequest);

        // Then
        assertNotNull(result);
        verify(customerMapper).updateCorporateCustomerFromDto(updateRequest, corporateCustomer);
        verify(customerRepository).save(corporateCustomer);
    }

    @Test
    void deleteCustomer_WhenCustomerExists_ShouldDeleteSuccessfully() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(customerRepository.save(any(IndividualCustomer.class))).thenReturn(individualCustomer);

        // When
        customerService.deleteCustomer(1L);

        // Then
        assertTrue(individualCustomer.getDeleted());
        verify(customerRepository).save(individualCustomer);
        verify(customerRepository, never()).delete(any());
    }

    @Test
    void deleteCustomer_WhenCustomerNotExists_ShouldThrowException() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(1L));
    }

    @Test
    void verifyKyc_WhenCustomerExists_ShouldVerifySuccessfully() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(individualCustomer);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.verifyKyc(1L);

        // Then
        assertNotNull(result);
        assertTrue(individualCustomer.getKycVerified());
        assertNotNull(individualCustomer.getKycVerifiedAt());
        assertEquals(LocalDate.now(), individualCustomer.getKycVerifiedAt());
    }

    @Test
    void unverifyKyc_WhenCustomerExists_ShouldUnverifySuccessfully() {
        // Given
        individualCustomer.setKycVerified(true);
        individualCustomer.setKycVerifiedAt(LocalDate.now());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(individualCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(individualCustomer);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.unverifyKyc(1L);

        // Then
        assertNotNull(result);
        assertFalse(individualCustomer.getKycVerified());
        assertNull(individualCustomer.getKycVerifiedAt());
    }

    @Test
    void getKycVerifiedCustomers_ShouldReturnVerifiedCustomers() {
        // Given
        List<Customer> verifiedCustomers = Arrays.asList(individualCustomer);
        when(customerRepository.findByKycVerifiedTrue()).thenReturn(verifiedCustomers);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        List<CustomerDto> result = customerService.getKycVerifiedCustomers();

        // Then
        assertEquals(1, result.size());
        assertEquals(individualCustomerDto.id(), result.get(0).id());
    }

    @Test
    void getKycUnverifiedCustomers_ShouldReturnUnverifiedCustomers() {
        // Given
        List<Customer> unverifiedCustomers = Arrays.asList(corporateCustomer);
        when(customerRepository.findByKycVerifiedFalse()).thenReturn(unverifiedCustomers);
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        List<CustomerDto> result = customerService.getKycUnverifiedCustomers();

        // Then
        assertEquals(1, result.size());
        assertEquals(corporateCustomerDto.id(), result.get(0).id());
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = customerService.existsById(1L);

        // Then
        assertTrue(result);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(customerRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = customerService.existsById(1L);

        // Then
        assertFalse(result);
    }

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        // Given
        when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When
        boolean result = customerService.existsByEmail("john.doe@example.com");

        // Then
        assertTrue(result);
    }

    @Test
    void getIndividualCustomers_ShouldReturnOnlyIndividualCustomers() {
        // Given
        List<Customer> individualCustomers = Arrays.asList(individualCustomer);
        when(customerRepository.findIndividualCustomers()).thenReturn(individualCustomers);
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        List<CustomerDto> result = customerService.getIndividualCustomers();

        // Then
        assertEquals(1, result.size());
        assertEquals("INDIVIDUAL", result.get(0).customerType());
    }

    @Test
    void getCorporateCustomers_ShouldReturnOnlyCorporateCustomers() {
        // Given
        List<Customer> corporateCustomers = Arrays.asList(corporateCustomer);
        when(customerRepository.findCorporateCustomers()).thenReturn(corporateCustomers);
        when(customerMapper.toDto(corporateCustomer)).thenReturn(corporateCustomerDto);

        // When
        List<CustomerDto> result = customerService.getCorporateCustomers();

        // Then
        assertEquals(1, result.size());
        assertEquals("CORPORATE", result.get(0).customerType());
    }

    @Test
    void getReferenceById_ShouldReturnReference() {
        // Given
        when(customerRepository.getReferenceById(1L)).thenReturn(individualCustomer);

        // When
        Customer result = customerService.getReferenceById(1L);

        // Then
        assertNotNull(result);
        assertEquals(individualCustomer, result);
    }

    @Test
    void getCustomerByCustomerNumber_WhenExists_ShouldReturnCustomer() {
        // Given
        when(customerRepository.findByCustomerNumber("IND-123456789ABC"))
                .thenReturn(Optional.of(individualCustomer));
        when(customerMapper.toDto(individualCustomer)).thenReturn(individualCustomerDto);

        // When
        CustomerDto result = customerService.getCustomerByCustomerNumber("IND-123456789ABC");

        // Then
        assertNotNull(result);
        assertEquals("IND-123456789ABC", result.customerNumber());
    }

    @Test
    void getCustomerByCustomerNumber_WhenNotExists_ShouldThrowException() {
        // Given
        when(customerRepository.findByCustomerNumber("NONEXISTENT")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerByCustomerNumber("NONEXISTENT"));
    }
}