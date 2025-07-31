package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.entity.CorporateCustomer;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.IndividualCustomer;
import com.infina.hissenet.entity.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PortfolioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "riskProfile", source = "request.riskProfile")
    Portfolio toEntity(PortfolioCreateRequest request, Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "active", source = "request.isActive")
    void updateEntity(@MappingTarget Portfolio portfolio, PortfolioUpdateRequest request);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(getCustomerName(portfolio.getCustomer()))")
    @Mapping(target = "isActive", source = "active")
    PortfolioResponse toResponse(Portfolio portfolio);

    @Mapping(target = "isActive", source = "active")
    PortfolioSummaryResponse toSummaryResponse(Portfolio portfolio);


    default String getCustomerName(Customer customer) {
        if (customer == null) {
            return "Unknown Customer";
        }

        Customer realCustomer = org.hibernate.Hibernate.unproxy(customer, Customer.class);

        if (realCustomer instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) realCustomer;
            return individual.getFirstName() + " " + individual.getLastName();
        } else if (realCustomer instanceof CorporateCustomer) {
            CorporateCustomer corporate = (CorporateCustomer) realCustomer;
            return corporate.getCompanyName();
        } else {
            return "Unknown Customer Type";
        }
    }
}
