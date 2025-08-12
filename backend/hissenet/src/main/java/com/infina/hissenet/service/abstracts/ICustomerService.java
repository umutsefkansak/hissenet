package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.request.CorporateCustomerCreateRequest;
import com.infina.hissenet.dto.request.CorporateCustomerUpdateRequest;
import com.infina.hissenet.dto.request.IndividualCustomerCreateRequest;
import com.infina.hissenet.dto.request.IndividualCustomerUpdateRequest;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Customer entity operations.
 * Provides business logic methods for customer management including
 * creation, retrieval, update, and deletion operations for both
 * individual and corporate customers.
 *
 * <p>This interface defines the contract for customer-related business operations
 * such as creating different types of customers, managing KYC verification status,
 * and performing CRUD operations on customer entities.</p>
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 * @since 1.0
 */
public interface ICustomerService {

    /**
     * Creates a new individual customer.
     * Validates email and TC number uniqueness before creation.
     * Automatically generates a customer number and publishes customer creation event.
     *
     * @param createDto the individual customer creation data transfer object
     * @return the created customer response
     * @throws IllegalArgumentException if email or TC number already exists
     */
    CustomerDto createIndividualCustomer(IndividualCustomerCreateRequest createDto);

    /**
     * Creates a new corporate customer.
     * Validates email and tax number uniqueness before creation.
     * Automatically generates a customer number and publishes customer creation event.
     *
     * @param createDto the corporate customer creation data transfer object
     * @return the created customer response
     * @throws IllegalArgumentException if email or tax number already exists
     */
    CustomerDto createCorporateCustomer(CorporateCustomerCreateRequest createDto);

    /**
     * Retrieves a customer by its unique identifier.
     *
     * @param id the customer identifier
     * @return the customer response
     * @throws CustomerNotFoundException if the customer does not exist
     */
    CustomerDto getCustomerById(Long id);

    /**
     * Retrieves all customers in the system.
     *
     * @return list of all customer responses
     */
    List<CustomerDto> getAllCustomers();

    /**
     * Retrieves all customers with pagination support.
     *
     * @param pageable pagination information
     * @return paginated customer responses
     */
    Page<CustomerDto> getAllCustomers(Pageable pageable);

    /**
     * Retrieves a customer by email address.
     *
     * @param email the customer email address
     * @return optional containing the customer response, empty if not found
     */
    CustomerDto  getCustomerByEmail(String email);

    /**
     * Retrieves a customer by customer number.
     *
     * @param customerNumber the unique customer number
     * @return optional containing the customer response, empty if not found
     */
    CustomerDto getCustomerByCustomerNumber(String customerNumber);

    /**
     * Retrieves all individual customers.
     *
     * @return list of individual customer responses
     */
    List<CustomerDto> getIndividualCustomers();

    /**
     * Retrieves all corporate customers.
     *
     * @return list of corporate customer responses
     */
    List<CustomerDto> getCorporateCustomers();

    /**
     * Updates an existing individual customer with new information.
     * Validates email and TC number uniqueness before update (excluding current customer).
     *
     * @param id the customer identifier
     * @param updateDto the individual customer update data transfer object
     * @return the updated customer response
     * @throws CustomerNotFoundException if the customer does not exist
     * @throws IllegalArgumentException if customer is not individual type or email/TC number already exists
     */
    CustomerDto updateIndividualCustomer(Long id, IndividualCustomerUpdateRequest updateDto);

    /**
     * Updates an existing corporate customer with new information.
     * Validates email and tax number uniqueness before update (excluding current customer).
     *
     * @param id the customer identifier
     * @param updateDto the corporate customer update data transfer object
     * @return the updated customer response
     * @throws CustomerNotFoundException if the customer does not exist
     * @throws IllegalArgumentException if customer is not corporate type or email/tax number already exists
     */
    CustomerDto updateCorporateCustomer(Long id, CorporateCustomerUpdateRequest updateDto);

    /**
     * Deletes a customer by its unique identifier.
     *
     * @param id the customer identifier
     * @throws CustomerNotFoundException if the customer does not exist
     */
    void deleteCustomer(Long id);

    /**
     * Checks if a customer exists by its unique identifier.
     *
     * @param id the customer identifier
     * @return true if the customer exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Checks if a customer exists by email address.
     *
     * @param email the customer email address
     * @return true if a customer with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Verifies KYC status for a customer.
     * Sets KYC verified flag to true and records verification date.
     *
     * @param customerId the customer identifier
     * @return the updated customer response
     * @throws CustomerNotFoundException if the customer does not exist
     */
    CustomerDto verifyKyc(Long customerId);

    /**
     * Removes KYC verification for a customer.
     * Sets KYC verified flag to false and clears verification date.
     *
     * @param customerId the customer identifier
     * @return the updated customer response
     * @throws CustomerNotFoundException if the customer does not exist
     */
    CustomerDto unverifyKyc(Long customerId);

    /**
     * Retrieves all KYC verified customers.
     *
     * @return list of KYC verified customer responses
     */
    List<CustomerDto> getKycVerifiedCustomers();

    /**
     * Retrieves all KYC unverified customers.
     *
     * @return list of KYC unverified customer responses
     */
    List<CustomerDto> getKycUnverifiedCustomers();

    /**
     * Gets a reference to the customer entity without loading it from database.
     * This method is used for setting foreign key relationships.
     *
     * @param id the customer identifier
     * @return the customer entity reference
     */
    Customer getReferenceById(Long id);


    /**
     * Retrieves a customer by Turkish Identification Number (TC number).
     *
     * @param tcNumber Turkish ID number
     * @return customer data transfer object
     * @throws CustomerNotFoundException if no customer exists with the given TC number
     */
    CustomerDto getCustomerByTcNumber(String tcNumber);

    /**
     * Retrieves a customer by tax number.
     *
     * @param taxNumber tax number
     * @return customer data transfer object
     * @throws CustomerNotFoundException if no customer exists with the given tax number
     */
    CustomerDto getCustomerByTaxNumber(String taxNumber);
}