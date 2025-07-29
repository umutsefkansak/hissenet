package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {
    /**
     * Müşteri ID'sine göre adresleri getirir
     */
    List<Address> findByCustomerId(Long customerId);

    /**
     * Müşterinin ana adresini getirir
     */
    Optional<Address> findByCustomerIdAndIsPrimaryTrue(Long customerId);

    /**
     * Müşterinin tüm ana adreslerini getirir (birden fazla ana adres varsa)
     */
    List<Address> findAllByCustomerIdAndIsPrimaryTrue(Long customerId);

    /**
     * Müşteri ID'sine göre adresleri siler
     */
    void deleteByCustomerId(Long customerId);

    /**
     * Müşterinin adres sayısını getirir
     */
    @Query("SELECT COUNT(a) FROM Address a WHERE a.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * Şehir adına göre adresleri getirir
     */
    List<Address> findByCityIgnoreCase(String city);

    /**
     * Eyalet adına göre adresleri getirir
     */
    List<Address> findByStateIgnoreCase(String state);

    /**
     * Adres tipine göre adresleri getirir
     */
    List<Address> findByAddressType(com.infina.hissenet.entity.enums.AddressType addressType);
}
