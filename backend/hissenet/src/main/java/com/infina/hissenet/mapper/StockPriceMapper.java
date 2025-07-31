package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.StockPriceCreateRequest;
import com.infina.hissenet.dto.request.StockPriceUpdateRequest;
import com.infina.hissenet.dto.response.StockPriceResponse;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockPrice;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockPriceMapper {

    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "createdAt",     ignore = true)
    @Mapping(target = "updatedAt",     ignore = true)
    @Mapping(target = "createdBy",     ignore = true)
    @Mapping(target = "updatedBy",     ignore = true)
    @Mapping(target = "deleted",       ignore = true)
    @Mapping(source = "stock",             target = "stock")
    @Mapping(source = "request.currentPrice", target = "currentPrice")
    @Mapping(source = "request.openPrice",    target = "openPrice")
    @Mapping(source = "request.highPrice",    target = "highPrice")
    @Mapping(source = "request.lowPrice",     target = "lowPrice")
    @Mapping(source = "request.timestamp",    target = "timestamp")
    StockPrice toEntity(StockPriceCreateRequest request, Stock stock);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "stock",        ignore = true) // stock linkage not updated here
    @Mapping(source = "request.currentPrice", target = "currentPrice")
    @Mapping(source = "request.openPrice",    target = "openPrice")
    @Mapping(source = "request.highPrice",    target = "highPrice")
    @Mapping(source = "request.lowPrice",     target = "lowPrice")
    @Mapping(source = "request.timestamp",    target = "timestamp")
    void updateEntityFromDto(StockPriceUpdateRequest request, @MappingTarget StockPrice entity);

    @Mapping(source = "id",               target = "id")
    @Mapping(source = "stock.id",         target = "stockId")
    @Mapping(source = "currentPrice",     target = "currentPrice")
    @Mapping(source = "openPrice",        target = "openPrice")
    @Mapping(source = "highPrice",        target = "highPrice")
    @Mapping(source = "lowPrice",         target = "lowPrice")
    @Mapping(source = "timestamp",        target = "timestamp")
    StockPriceResponse toResponse(StockPrice price);
}
