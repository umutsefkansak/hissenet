package com.infina.hissenet.service;


import com.infina.hissenet.constants.CustomerConstants;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.request.CorporateCustomerCreateRequest;
import com.infina.hissenet.dto.request.CorporateCustomerUpdateRequest;
import com.infina.hissenet.dto.request.IndividualCustomerCreateRequest;
import com.infina.hissenet.dto.request.IndividualCustomerUpdateRequest;
import com.infina.hissenet.entity.CorporateCustomer;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.event.CustomerCreatedEvent;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.exception.customer.EmailAlreadyExistsException;
import com.infina.hissenet.exception.customer.TaxNumberAlreadyExistsException;
import com.infina.hissenet.exception.customer.TcNumberAlreadyExistsException;
import com.infina.hissenet.mapper.CustomerMapper;
import com.infina.hissenet.repository.CorporateCustomerRepository;
import com.infina.hissenet.repository.CustomerRepository;
import com.infina.hissenet.repository.IndividualCustomerRepository;
import com.infina.hissenet.service.abstracts.ICustomerService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerService extends GenericServiceImpl<Customer, Long> implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final IndividualCustomerRepository individualCustomerRepository;
    private final CorporateCustomerRepository corporateCustomerRepository;

    private final CustomerMapper customerMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Autowired
    public CustomerService(CustomerRepository customerRepository, IndividualCustomerRepository individualCustomerRepository, CorporateCustomerRepository corporateCustomerRepository,
                           CustomerMapper customerMapper, ApplicationEventPublisher eventPublisher) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.individualCustomerRepository = individualCustomerRepository;
        this.corporateCustomerRepository = corporateCustomerRepository;
        this.customerMapper = customerMapper;
        this.eventPublisher = eventPublisher;
    }


    public CustomerDto createIndividualCustomer(IndividualCustomerCreateRequest createDto) {

        if (customerRepository.existsByEmail(createDto.email())) {
            throw new EmailAlreadyExistsException(createDto.email());
        }


        if (createDto.tcNumber() != null && individualCustomerRepository.existsByTcNumber(createDto.tcNumber())) {
            throw new TcNumberAlreadyExistsException(createDto.tcNumber());
        }

        IndividualCustomer customer = customerMapper.toEntity(createDto);
        customer.setCustomerNumber(generateCustomerNumber(CustomerConstants.INDIVIDUAL_CUSTOMER_PREFIX));

        IndividualCustomer savedCustomer = (IndividualCustomer) save(customer);


        eventPublisher.publishEvent(new CustomerCreatedEvent(this, savedCustomer.getId(), "INDIVIDUAL"));
        return customerMapper.toDto(savedCustomer);
    }


    public CustomerDto createCorporateCustomer(CorporateCustomerCreateRequest createDto) {

        if (customerRepository.existsByEmail(createDto.email())) {
            throw new EmailAlreadyExistsException(createDto.email());
        }


        if (createDto.taxNumber() != null && corporateCustomerRepository.existsByTaxNumber(createDto.taxNumber())) {
            throw new TaxNumberAlreadyExistsException(createDto.taxNumber());
        }

        CorporateCustomer customer = customerMapper.toEntity(createDto);
        customer.setCustomerNumber(generateCustomerNumber(CustomerConstants.CORPORATE_CUSTOMER_PREFIX));

        CorporateCustomer savedCustomer = (CorporateCustomer) save(customer);


        eventPublisher.publishEvent(new CustomerCreatedEvent(this, savedCustomer.getId(), "CORPORATE"));
        return customerMapper.toDto(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer customer = findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return customerMapper.toDto(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return findAll()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return findAll(pageable)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerByCustomerNumber(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new CustomerNotFoundException("No customer found with customer number: " + customerNumber));
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getIndividualCustomers() {
        return customerRepository.findIndividualCustomers()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getCorporateCustomers() {
        return customerRepository.findCorporateCustomers()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public CustomerDto updateIndividualCustomer(Long id, IndividualCustomerUpdateRequest updateDto) {
        Customer existingCustomer = findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!(existingCustomer instanceof IndividualCustomer)) {
            throw new IllegalArgumentException("Customer with id " + id + " is not an individual customer");
        }

        IndividualCustomer individualCustomer = (IndividualCustomer) existingCustomer;


        if (updateDto.email() != null && !updateDto.email().equals(individualCustomer.getEmail())) {
            if (customerRepository.existsByEmailAndIdNot(updateDto.email(), id)) {
                throw new EmailAlreadyExistsException(updateDto.email());
            }
        }


        if (updateDto.tcNumber() != null && !updateDto.tcNumber().equals(individualCustomer.getTcNumber())) {
            if (individualCustomerRepository.existsByTcNumberAndIdNot(updateDto.tcNumber(), id)) {
                throw new TcNumberAlreadyExistsException(updateDto.tcNumber());
            }
        }

        customerMapper.updateIndividualCustomerFromDto(updateDto, individualCustomer);
        Customer updatedCustomer = update(individualCustomer);
        return customerMapper.toDto(updatedCustomer);
    }

    public CustomerDto updateCorporateCustomer(Long id, CorporateCustomerUpdateRequest updateDto) {
        Customer existingCustomer = findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!(existingCustomer instanceof CorporateCustomer)) {
            throw new IllegalArgumentException("Customer with id " + id + " is not a corporate customer");
        }

        CorporateCustomer corporateCustomer = (CorporateCustomer) existingCustomer;


        if (updateDto.email() != null && !updateDto.email().equals(corporateCustomer.getEmail())) {
            if (customerRepository.existsByEmailAndIdNot(updateDto.email(), id)) {
                throw new EmailAlreadyExistsException(updateDto.email());
            }
        }


        if (updateDto.taxNumber() != null && !updateDto.taxNumber().equals(corporateCustomer.getTaxNumber())) {
            if (corporateCustomerRepository.existsByTaxNumberAndIdNot(updateDto.taxNumber(), id)) {
                throw new TaxNumberAlreadyExistsException(updateDto.taxNumber());
            }
        }

        customerMapper.updateCorporateCustomerFromDto(updateDto, corporateCustomer);
        Customer updatedCustomer = update(corporateCustomer);
        return customerMapper.toDto(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        delete(customer);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public CustomerDto verifyKyc(Long customerId) {
        Customer customer = findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.setKycVerified(true);
        customer.setKycVerifiedAt(LocalDateTime.now().toLocalDate());

        Customer updatedCustomer = update(customer);
        return customerMapper.toDto(updatedCustomer);
    }

    public CustomerDto unverifyKyc(Long customerId) {
        Customer customer = findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customer.setKycVerified(false);
        customer.setKycVerifiedAt(null);

        Customer updatedCustomer = update(customer);
        return customerMapper.toDto(updatedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getKycVerifiedCustomers() {
        return customerRepository.findByKycVerifiedTrue()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getKycUnverifiedCustomers() {
        return customerRepository.findByKycVerifiedFalse()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public Customer getReferenceById(Long id) {
        return customerRepository.getReferenceById(id);
    }


    private String generateCustomerNumber(String prefix) {

        String shortUuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String customerNumber = prefix + "-" + shortUuid;

        if (customerRepository.existsByCustomerNumber(customerNumber)) {
            return generateCustomerNumber(prefix);
        }
        return customerNumber;
    }
}