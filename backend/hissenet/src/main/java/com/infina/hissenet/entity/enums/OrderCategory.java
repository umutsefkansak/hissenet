package com.infina.hissenet.entity.enums;

public enum OrderCategory {
	MARKET, // Anında, mevcut piyasa fiyatından gerçekleşen emir.
	LIMIT   // Kullanıcının belirlediği belirli bir fiyattan veya daha iyisinden gerçekleşmeyi bekleyen emir.
}
