package com.infina.hissenet.repository;

import com.infina.hissenet.entity.CorporateCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CorporateCustomerRepository extends JpaRepository<CorporateCustomer,Long> {
    boolean existsByTaxNumber(String taxNumber);
    boolean existsByTaxNumberAndIdNot(String taxNumber, Long id);
    Optional<CorporateCustomer> findByTaxNumber(String taxNumber);

}
