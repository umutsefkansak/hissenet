package com.infina.hissenet.dto.common;


public sealed interface CustomerDto permits IndividualCustomerDto, CorporateCustomerDto {
    Long id();
    String customerNumber();
    String email();
    String phone();
    String nationality();
    Boolean kycVerified();
    String customerType();
}