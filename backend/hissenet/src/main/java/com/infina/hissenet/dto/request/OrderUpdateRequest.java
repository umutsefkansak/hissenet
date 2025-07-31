package com.infina.hissenet.dto.request;

import com.infina.hissenet.entity.enums.OrderStatus;

public record OrderUpdateRequest(
		OrderStatus status		
) {

}
