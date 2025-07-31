package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.StockHistoryCreateRequest;
import com.infina.hissenet.dto.request.StockHistoryUpdateRequest;
import com.infina.hissenet.dto.response.StockHistoryResponse;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockHistory;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockHistoryMapper {

    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    @Mapping(target = "createdBy",      ignore = true)
    @Mapping(target = "updatedBy",      ignore = true)
    @Mapping(target = "deleted",        ignore = true)
    @Mapping(source = "stock",             target = "stock")
    @Mapping(source = "request.recordDate", target = "recordDate")
    @Mapping(source = "request.dataDate",   target = "dataDate")
    @Mapping(source = "request.openPrice",  target = "openPrice")
    @Mapping(source = "request.highPrice",  target = "highPrice")
    @Mapping(source = "request.lowPrice",   target = "lowPrice")
    @Mapping(source = "request.closePrice", target = "closePrice")
    StockHistory toEntity(StockHistoryCreateRequest request, Stock stock);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "stock",       ignore = true) // stock linkage unchanged
    @Mapping(source = "request.recordDate", target = "recordDate")
    @Mapping(source = "request.dataDate",   target = "dataDate")
    @Mapping(source = "request.openPrice",  target = "openPrice")
    @Mapping(source = "request.highPrice",  target = "highPrice")
    @Mapping(source = "request.lowPrice",   target = "lowPrice")
    @Mapping(source = "request.closePrice", target = "closePrice")
    void updateEntityFromDto(StockHistoryUpdateRequest request, @MappingTarget StockHistory entity);

    @Mapping(source = "id",               target = "id")
    @Mapping(source = "stock.id",         target = "stockId")
    @Mapping(source = "recordDate",       target = "recordDate")
    @Mapping(source = "dataDate",         target = "dataDate")
    @Mapping(source = "openPrice",        target = "openPrice")
    @Mapping(source = "highPrice",        target = "highPrice")
    @Mapping(source = "lowPrice",         target = "lowPrice")
    @Mapping(source = "closePrice",       target = "closePrice")
    StockHistoryResponse toResponse(StockHistory history);
}
