package com.infina.hissenet.mapper;
import com.infina.hissenet.dto.common.CorporateCustomerDto;
import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.common.IndividualCustomerDto;
import com.infina.hissenet.dto.request.AddressCreateDto;
import com.infina.hissenet.dto.request.AddressUpdateDto;
import com.infina.hissenet.dto.response.AddressResponse;
import com.infina.hissenet.entity.Address;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.entity.CorporateCustomer;
import com.infina.hissenet.repository.CustomerRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CustomerRepository.class})
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customer", source = "customerId", qualifiedByName = "mapCustomerReference")
    Address toEntity(AddressCreateDto createAddressDto);

    @Mapping(target = "customer", source = "customer", qualifiedByName = "mapCustomerToDto")
    @Mapping(target = "fullAddress", source = ".", qualifiedByName = "getFullAddress")
    AddressResponse toDto(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(AddressUpdateDto updateAddressDto, @MappingTarget Address address);

    @Named("mapCustomerReference")
    default Customer mapCustomerReference(Long customerId) {
        if (customerId == null) {
            return null;
        }
        Customer customer = new Customer() {};
        customer.setId(customerId);
        return customer;
    }

    @Named("mapCustomerToDto")
    default CustomerDto mapCustomerToDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        // Hibernate proxy'yi gerçek objeye dönüştür
        Customer realCustomer = (Customer) org.hibernate.Hibernate.unproxy(customer);


        String customerType;
        if (realCustomer instanceof IndividualCustomer) {
            customerType = "INDIVIDUAL";
        } else if (realCustomer instanceof CorporateCustomer) {
            customerType = "CORPORATE";
        } else {
            customerType = "UNKNOWN";
        }

        // Individual customer ise
        if (realCustomer instanceof IndividualCustomer individual) {
            return new IndividualCustomerDto(
                    realCustomer.getId(),
                    realCustomer.getCustomerNumber(),
                    realCustomer.getEmail(),
                    realCustomer.getPhone(),
                    realCustomer.getNationality(),
                    realCustomer.getKycVerified(),
                    customerType,
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
                    individual.getRiskProfile()
            );
        }

        // Corporate customer ise
        if (realCustomer instanceof CorporateCustomer corporate) {
            return new CorporateCustomerDto(
                    realCustomer.getId(),
                    realCustomer.getCustomerNumber(),
                    realCustomer.getEmail(),
                    realCustomer.getPhone(),
                    realCustomer.getNationality(),
                    realCustomer.getKycVerified(),
                    customerType,
                    corporate.getCompanyName(),
                    corporate.getTaxNumber(),
                    corporate.getTradeRegistryNumber(),
                    corporate.getEstablishmentDate(),
                    corporate.getSector(),
                    corporate.getAuthorizedPersonName(),
                    corporate.getAuthorizedPersonTitle(),
                    corporate.getWebsite()
            );
        }

        // Bu duruma normalde gelmemeli ama güvenlik için
        throw new IllegalArgumentException("Unknown customer type: " + realCustomer.getClass().getSimpleName());
    }

    @Named("getFullAddress")
    default String getFullAddress(Address address) {
        return address.getFullAddress();
    }
}