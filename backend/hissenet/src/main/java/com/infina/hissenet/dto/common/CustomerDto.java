package com.infina.hissenet.dto.common;


import java.math.BigDecimal;

public sealed interface CustomerDto permits IndividualCustomerDto, CorporateCustomerDto {
    Long id();
    String customerNumber();
    String email();
    String phone();
    String nationality();
    Boolean kycVerified();
    String customerType();
    BigDecimal commissionRate();

}