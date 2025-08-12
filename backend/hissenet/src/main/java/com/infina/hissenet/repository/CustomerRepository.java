package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.enums.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations.
 * Handles customer data access including individual and corporate customer types.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds a customer by email address.
     *
     * @param email the customer's email
     * @return optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Finds a customer by customer number.
     *
     * @param customerNumber the unique customer number
     * @return optional containing the customer if found
     */
    Optional<Customer> findByCustomerNumber(String customerNumber);


    /**
     * Checks if an email exists in the system.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);


    /**
     * Checks if an email exists for a different customer.
     *
     * @param email the email to check
     * @param id the customer ID to exclude
     * @return true if email exists for another customer
     */
    boolean existsByEmailAndIdNot(String email, Long id);



    /**
     * Finds customers by their type using polymorphic query.
     *
     * @param customerType the type of customer
     * @return list of customers with specified type
     */
    @Query("SELECT c FROM Customer c WHERE TYPE(c) = :customerType")
    List<Customer> findByCustomerType(@Param("customerType") String customerType);



    /**
     * Finds all individual customers.
     *
     * @return list of individual customers
     */
    @Query("SELECT c FROM Customer c WHERE TYPE(c) = IndividualCustomer")
    List<Customer> findIndividualCustomers();


    /**
     * Finds all corporate customers.
     *
     * @return list of corporate customers
     */
    @Query("SELECT c FROM Customer c WHERE TYPE(c) = CorporateCustomer")
    List<Customer> findCorporateCustomers();



    /**
     * Finds customers with verified KYC status.
     *
     * @return list of KYC verified customers
     */
    List<Customer> findByKycVerifiedTrue();

    /**
     * Finds customers without verified KYC status.
     *
     * @return list of customers with unverified KYC
     */
    List<Customer> findByKycVerifiedFalse();

    /**
     * Checks if a customer number already exists.
     *
     * @param customerNumber the customer number to check
     * @return true if customer number exists
     */
    boolean existsByCustomerNumber(String customerNumber);
} 

