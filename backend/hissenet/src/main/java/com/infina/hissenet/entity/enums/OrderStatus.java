package com.infina.hissenet.entity.enums;

public enum OrderStatus {
	PENDING,    	  // Sipariş oluşturuldu, bekliyor
    OPEN,       	  // Sipariş aktif ve gerçekleşmeyi bekliyor (limit emirleri için)
    FILLED,           // Sipariş tamamen gerçekleşti
    CANCELED,         // Sipariş iptal edildi
    REJECTED,         // Sipariş reddedildi (örn. geçersiz parametreler yüzünden)
}
