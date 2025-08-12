package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.StockTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "totalAmount", source = "request.totalAmount")
    StockTransaction toEntity(StockTransactionCreateRequest request, Portfolio portfolio, Order order);

    @Mapping(source = "portfolio.id", target = "portfolioId")
    @Mapping(source = "portfolio.portfolioName", target = "portfolioName")
    @Mapping(source = "stockCode", target = "stockCode")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "currentPrice", target = "currentPrice")
    StockTransactionResponse toResponse(StockTransaction transaction);

}