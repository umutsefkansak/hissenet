package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import com.infina.hissenet.entity.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletTransactionMapper {

    WalletTransactionMapper INSTANCE = Mappers.getMapper(WalletTransactionMapper.class);

    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "transactionStatus", constant = "PENDING")
    @Mapping(target = "transactionDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "referenceNumber", ignore = true) // Service'de generate edilecek
    @Mapping(target = "balanceBefore", ignore = true) // Service'de set edilecek
    @Mapping(target = "balanceAfter", ignore = true) // Service'de set edilecek
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    WalletTransaction toEntity(CreateWalletTransactionRequest dto);

    @Mapping(source = "wallet.id", target = "walletId")
    WalletTransactionResponse toResponse(WalletTransaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "transactionType", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    @Mapping(target = "balanceBefore", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    @Mapping(target = "referenceNumber", ignore = true)
    @Mapping(target = "feeAmount", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(UpdateWalletTransactionRequest dto, @org.mapstruct.MappingTarget WalletTransaction walletTransaction);

}
