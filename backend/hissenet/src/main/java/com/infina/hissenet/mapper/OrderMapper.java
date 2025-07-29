/*package com.infina.hissenet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	@Mapping(source = "customerId", target = "customer.id")
	@Mapping(source = "stockId", target = "stock.id")
	Order toEntity(OrderCreateRequest dto);
	
	Order toEntity(OrderUpdateRequest dto);

	@Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "stock.id", target = "stockId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "updatedBy.id", target = "updatedById")
    OrderResponse toResponse(Order entity);
	
}*/
