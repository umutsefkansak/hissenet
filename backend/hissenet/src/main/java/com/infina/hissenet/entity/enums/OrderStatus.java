package com.infina.hissenet.entity.enums;

/**
 * Siparişin (emrin) işlem durumu durumunu temsil eder.
 */
public enum OrderStatus {

    /**
     * Sipariş aktif ve gerçekleşmeyi bekliyor (limit emirleri için).
     */
    OPEN,

    /**
     * Sipariş tamamen gerçekleşti.
     */
    FILLED,

    /**
     * Sipariş iptal edildi.
     */
    CANCELED,

    /**
     * Sipariş reddedildi (örn. geçersiz parametreler yüzünden).
     */
    REJECTED
}
