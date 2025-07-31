package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.StockCreateRequest;
import com.infina.hissenet.dto.request.StockUpdateRequest;
import com.infina.hissenet.dto.response.StockResponse;
import com.infina.hissenet.entity.Stock;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    @Mapping(target = "createdBy",    ignore = true)
    @Mapping(target = "updatedBy",    ignore = true)
    @Mapping(target = "deleted",      ignore = true)
    @Mapping(source = "request.ticker",     target = "ticker")
    @Mapping(source = "request.issuerName", target = "issuerName")
    @Mapping(source = "request.currency",   target = "currency")
    @Mapping(source = "request.exchange",   target = "exchange")
    @Mapping(source = "request.lotSize",    target = "lotSize")
    @Mapping(source = "request.status",     target = "status")
    Stock toEntity(StockCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ticker",       ignore = true)  // ticker is immutable
    @Mapping(source = "request.issuerName", target = "issuerName")
    @Mapping(source = "request.currency",   target = "currency")
    @Mapping(source = "request.exchange",   target = "exchange")
    @Mapping(source = "request.lotSize",    target = "lotSize")
    @Mapping(source = "request.status",     target = "status")
    void updateEntityFromDto(StockUpdateRequest request, @MappingTarget Stock entity);

    @Mapping(source = "id",           target = "id")
    @Mapping(source = "ticker",       target = "ticker")
    @Mapping(source = "issuerName",   target = "issuerName")
    @Mapping(source = "currency",     target = "currency")
    @Mapping(source = "exchange",     target = "exchange")
    @Mapping(source = "lotSize",      target = "lotSize")
    @Mapping(source = "status",       target = "status")
    StockResponse toResponse(Stock stock);
}