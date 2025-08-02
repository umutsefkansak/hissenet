package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Address entity operations.
 * Provides CRUD operations and custom queries for address management.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface AddressRepository extends JpaRepository<Address,Long> {

    /**
     * Finds all addresses for a specific customer.
     *
     * @param customerId the ID of the customer
     * @return list of addresses belonging to the customer
     */
    List<Address> findByCustomerId(Long customerId);


    /**
     * Finds the primary address for a customer.
     *
     * @param customerId the ID of the customer
     * @return optional containing the primary address if exists
     */
    Optional<Address> findByCustomerIdAndIsPrimaryTrue(Long customerId);


    /**
     * Finds all primary addresses for a customer.
     *
     * @param customerId the ID of the customer
     * @return list of primary addresses
     */
    List<Address> findAllByCustomerIdAndIsPrimaryTrue(Long customerId);

    /**
     * Deletes all addresses for a specific customer.
     *
     * @param customerId the ID of the customer
     */
    void deleteByCustomerId(Long customerId);

    /**
     * Counts the number of addresses for a customer.
     *
     * @param customerId the ID of the customer
     * @return total count of addresses
     */
    @Query("SELECT COUNT(a) FROM Address a WHERE a.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * Finds addresses by city name (case insensitive).
     *
     * @param city the city name
     * @return list of addresses in the specified city
     */
    List<Address> findByCityIgnoreCase(String city);

    /**
     * Finds addresses by state name (case insensitive).
     *
     * @param state the state name
     * @return list of addresses in the specified state
     */
    List<Address> findByStateIgnoreCase(String state);

    /**
     * Finds addresses by address type.
     *
     * @param addressType the type of address
     * @return list of addresses with the specified type
     */
    List<Address> findByAddressType(com.infina.hissenet.entity.enums.AddressType addressType);
}
