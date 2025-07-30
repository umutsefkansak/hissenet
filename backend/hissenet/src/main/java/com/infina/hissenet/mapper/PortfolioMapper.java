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
    @Mapping(target = "portfolioName", source = "request.portfolioName")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "riskProfile", source = "request.riskProfile")
    @Mapping(target = "portfolioType", source = "request.portfolioType")
    @Mapping(target = "lastRebalanceDate", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Portfolio toEntity(PortfolioCreateRequest request, Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "portfolioName", source = "request.portfolioName")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "riskProfile", source = "request.riskProfile")
    @Mapping(target = "portfolioType", source = "request.portfolioType")
    @Mapping(target = "active", source = "request.isActive")
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "totalProfitLoss", ignore = true)
    @Mapping(target = "profitLossPercentage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastRebalanceDate", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(@MappingTarget Portfolio portfolio, PortfolioUpdateRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(getCustomerName(portfolio.getCustomer()))")
    @Mapping(target = "portfolioName", source = "portfolioName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "totalCost", source = "totalCost")
    @Mapping(target = "totalProfitLoss", source = "totalProfitLoss")
    @Mapping(target = "profitLossPercentage", source = "profitLossPercentage")
    @Mapping(target = "riskProfile", source = "riskProfile")
    @Mapping(target = "portfolioType", source = "portfolioType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "lastRebalanceDate", source = "lastRebalanceDate")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PortfolioResponse toResponse(Portfolio portfolio);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "portfolioName", source = "portfolioName")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "totalProfitLoss", source = "totalProfitLoss")
    @Mapping(target = "profitLossPercentage", source = "profitLossPercentage")
    @Mapping(target = "riskProfile", source = "riskProfile")
    @Mapping(target = "portfolioType", source = "portfolioType")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "lastRebalanceDate", source = "lastRebalanceDate")
    PortfolioSummaryResponse toSummaryResponse(Portfolio portfolio);

    // Güvenli customer name alma metodu
    default String getCustomerName(Customer customer) {
        if (customer == null) {
            return "Unknown Customer";
        }
        
        // Hibernate proxy'den gerçek nesneyi al
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