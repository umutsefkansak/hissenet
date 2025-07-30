package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(target = "customer", ignore = true)
    Wallet toEntity(CreateWalletRequest dto);

    @Mapping(source = "customer.id", target = "customerId")
    WalletResponse toResponse(Wallet entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "dailyUsedAmount", ignore = true)
    @Mapping(target = "monthlyUsedAmount", ignore = true)
    @Mapping(target = "dailyTransactionCount", ignore = true)
    @Mapping(target = "lastTransactionDate", ignore = true)
    @Mapping(target = "lastResetDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(UpdateWalletRequest dto, @org.mapstruct.MappingTarget Wallet wallet);

}
