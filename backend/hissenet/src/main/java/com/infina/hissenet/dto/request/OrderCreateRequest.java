package com.infina.hissenet.dto.request;

import java.math.BigDecimal;

import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(

        @NotNull(message = "{validation.customer.id.required}")
        Long customerId,

        @NotNull(message = "{validation.order.category.required}")
        OrderCategory category,

        @NotNull(message = "{validation.order.type.required}")
        OrderType type,

        @NotNull(message = "{validation.stock.code.required}")
        String stockCode,

        @NotNull(message = "{validation.quantity.required}")
        @DecimalMin(value = "0.0001", message = "{validation.quantity.min}")
        BigDecimal quantity,

        @DecimalMin(value = "0.0001", inclusive = false, message = "{validation.price.positive}")
        BigDecimal price
        
) {

}
