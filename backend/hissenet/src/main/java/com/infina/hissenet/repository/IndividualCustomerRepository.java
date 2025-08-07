package com.infina.hissenet.repository;

import com.infina.hissenet.entity.IndividualCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndividualCustomerRepository extends JpaRepository<IndividualCustomer, Long> {
    boolean existsByTcNumberAndIdNot(String tcNumber, Long id);
    boolean existsByTcNumber(String tcNumber);
    Optional<IndividualCustomer> findByTcNumber(String tcNumber);
}