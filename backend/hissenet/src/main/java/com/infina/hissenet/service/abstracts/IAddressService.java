package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.AddressCreateRequest;
import com.infina.hissenet.dto.request.AddressUpdateRequest;
import com.infina.hissenet.dto.response.AddressResponse;
import com.infina.hissenet.exception.address.AddressNotFoundException;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Address entity operations.
 * Provides business logic methods for address management including
 * creation, retrieval, update, and deletion operations.
 *
 * <p>This interface defines the contract for address-related business operations
 * such as creating addresses for customers, managing primary address assignments,
 * and performing CRUD operations on address entities.</p>
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 * @since 1.0
 */
public interface IAddressService {

    /**
     * Creates a new address for a customer.
     * If the address is marked as primary, clears primary status from other addresses of the same customer.
     * If isPrimary is null and this is the first address for the customer, it will be set as primary automatically.
     *
     * @param createAddressDto the address creation data transfer object
     * @return the created address response
     * @throws CustomerNotFoundException if the customer does not exist
     */
    AddressResponse createAddress(AddressCreateRequest createAddressDto);

    /**
     * Retrieves an address by its unique identifier.
     *
     * @param id the address identifier
     * @return the address response
     * @throws AddressNotFoundException if the address does not exist
     */
    AddressResponse getAddressById(Long id);

    /**
     * Retrieves all addresses in the system.
     *
     * @return list of all address responses
     */
    List<AddressResponse> getAllAddresses();

    /**
     * Retrieves all addresses with pagination support.
     *
     * @param pageable pagination information
     * @return paginated address responses
     */
    Page<AddressResponse> getAllAddresses(Pageable pageable);

    /**
     * Retrieves all addresses belonging to a specific customer.
     *
     * @param customerId the customer identifier
     * @return list of address responses for the specified customer
     */
    List<AddressResponse> getAddressesByCustomerId(Long customerId);

    /**
     * Retrieves the primary address of a specific customer.
     *
     * @param customerId the customer identifier
     * @return optional containing the primary address response, empty if no primary address exists
     */
    AddressResponse  getPrimaryAddressByCustomerId(Long customerId);

    /**
     * Updates an existing address with new information.
     * If the address is being set as primary, clears primary status from other addresses of the same customer.
     *
     * @param id the address identifier
     * @param updateAddressDto the address update data transfer object
     * @return the updated address response
     * @throws AddressNotFoundException if the address does not exist
     * @throws CustomerNotFoundException if the new customer does not exist
     */
    AddressResponse updateAddress(Long id, AddressUpdateRequest updateAddressDto);

    /**
     * Deletes an address by its unique identifier.
     *
     * @param id the address identifier
     * @throws AddressNotFoundException if the address does not exist
     */
    void deleteAddress(Long id);

    /**
     * Checks if an address exists by its unique identifier.
     *
     * @param id the address identifier
     * @return true if the address exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Deletes all addresses belonging to a specific customer.
     * This method is typically used when a customer is being deleted.
     *
     * @param customerId the customer identifier
     */
    void deleteAllAddressesByCustomerId(Long customerId);
}