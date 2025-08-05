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


    @Mapping(target = "customer", ignore = true)
    Wallet toEntity(CreateWalletRequest dto);

    @Mapping(source = "customer.id", target = "customerId")
    WalletResponse toResponse(Wallet entity);


}
