package com.infina.hissenet.entity.enums;

/**
 * Emir türünü temsil eder.
 */
public enum OrderCategory {
	
	 /**
     * Anında, mevcut piyasa fiyatından gerçekleşen emir.
     */
    MARKET,

    /**
     * Kullanıcının belirlediği belirli bir fiyattan veya daha iyisinden gerçekleşmeyi bekleyen emir.
     */
    LIMIT
}
