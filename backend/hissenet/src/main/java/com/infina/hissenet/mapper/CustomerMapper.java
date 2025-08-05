package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.common.IndividualCustomerDto;
import com.infina.hissenet.dto.common.CorporateCustomerDto;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.entity.CorporateCustomer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerNumber", ignore = true)
    @Mapping(target = "kycVerified", constant = "false")
    @Mapping(target = "kycVerifiedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "nationality", defaultValue = "TR")
    @Mapping(target = "riskProfile", source = "riskProfile")
    @Mapping(target = "commissionRate", expression = "java(createDto.commissionRate() != null ? createDto.commissionRate() : com.infina.hissenet.constants.CustomerConstants.DEFAULT_COMMISSION_RATE)")
    IndividualCustomer toEntity(IndividualCustomerCreateRequest createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerNumber", ignore = true)
    @Mapping(target = "kycVerified", constant = "false")
    @Mapping(target = "kycVerifiedAt", ignore = true)
    @Mapping(target = "riskProfile", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "nationality", defaultValue = "TR")
    @Mapping(target = "commissionRate", expression = "java(createDto.commissionRate() != null ? createDto.commissionRate() : com.infina.hissenet.constants.CustomerConstants.DEFAULT_COMMISSION_RATE)")
    CorporateCustomer toEntity(CorporateCustomerCreateRequest createDto);

    default CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        // Hibernate proxy'yi gerçek objeye dönüştür
        Customer realCustomer = (Customer) org.hibernate.Hibernate.unproxy(customer);

        if (realCustomer instanceof IndividualCustomer individual) {
            return new IndividualCustomerDto(
                    realCustomer.getId(),
                    realCustomer.getCustomerNumber(),
                    realCustomer.getEmail(),
                    realCustomer.getPhone(),
                    realCustomer.getNationality(),
                    realCustomer.getKycVerified(),
                    "INDIVIDUAL",
                    individual.getFirstName(),
                    individual.getMiddleName(),
                    individual.getLastName(),
                    individual.getTcNumber(),
                    individual.getBirthDate(),
                    individual.getBirthPlace(),
                    individual.getGender() != null ? individual.getGender().name() : null,
                    individual.getMotherName(),
                    individual.getFatherName(),
                    individual.getProfession(),
                    individual.getEducationLevel(),
                    realCustomer.getRiskProfile(),
                    realCustomer.getCommissionRate(),
                    individual.getIncomeRange()

            );
        }

        if (realCustomer instanceof CorporateCustomer corporate) {
            return new CorporateCustomerDto(
                    realCustomer.getId(),
                    realCustomer.getCustomerNumber(),
                    realCustomer.getEmail(),
                    realCustomer.getPhone(),
                    realCustomer.getNationality(),
                    realCustomer.getKycVerified(),
                    "CORPORATE",
                    corporate.getCompanyName(),
                    corporate.getTaxNumber(),
                    corporate.getTradeRegistryNumber(),
                    corporate.getEstablishmentDate(),
                    corporate.getSector(),
                    corporate.getAuthorizedPersonName(),
                    corporate.getAuthorizedPersonTitle(),
                    corporate.getWebsite(),
                    realCustomer.getCommissionRate(),
                    corporate.getAuthorizedPersonPhone(),
                    corporate.getAuthorizedPersonTcNumber(),
                    corporate.getAuthorizedPersonEmail(),
                    corporate.getTaxOffice()

            );
        }

        throw new IllegalArgumentException("Unknown customer type: " + realCustomer.getClass().getSimpleName());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerNumber", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "commissionRate", source = "commissionRate")
    void updateIndividualCustomerFromDto(IndividualCustomerUpdateRequest updateDto, @MappingTarget IndividualCustomer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerNumber", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "commissionRate", source = "commissionRate")
    void updateCorporateCustomerFromDto(CorporateCustomerUpdateRequest updateDto, @MappingTarget CorporateCustomer customer);
}