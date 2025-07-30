package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", source = "portfolio")
    @Mapping(target = "stock", source = "stock")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "transactionType", source = "request.transactionType")
    @Mapping(target = "transactionStatus", source = "request.transactionStatus")
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "totalAmount", source = "request.totalAmount")
    @Mapping(target = "commission", source = "request.commission")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    StockTransaction toEntity(StockTransactionCreateRequest request, Portfolio portfolio, Stock stock, Order order);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "portfolioId", source = "portfolio.id")
    @Mapping(target = "portfolioName", source = "portfolio.portfolioName")
    @Mapping(target = "stockId", source = "stock.id")
    @Mapping(target = "stockTicker", source = "stock.ticker")
    @Mapping(target = "stockName", source = "stock.issuerName")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    StockTransactionResponse toResponse(StockTransaction transaction);

}