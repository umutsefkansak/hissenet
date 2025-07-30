package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.enums.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByCustomerNumber(String customerNumber);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT c FROM Customer c WHERE TYPE(c) = :customerType")
    List<Customer> findByCustomerType(@Param("customerType") String customerType);

    @Query("SELECT c FROM Customer c WHERE TYPE(c) = IndividualCustomer")
    List<Customer> findIndividualCustomers();

    @Query("SELECT c FROM Customer c WHERE TYPE(c) = CorporateCustomer")
    List<Customer> findCorporateCustomers();


    List<Customer> findByKycVerifiedTrue();
    List<Customer> findByKycVerifiedFalse();
    boolean existsByCustomerNumber(String customerNumber);

} 
