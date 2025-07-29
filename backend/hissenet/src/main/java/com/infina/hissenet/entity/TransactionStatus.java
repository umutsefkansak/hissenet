package com.infina.hissenet.entity;

public enum TransactionStatus {

        PENDING, //beklemede
        PROCESSING, // isleniyor
        COMPLETED, //tamamlandı
        FAILED, //hata
        CANCELLED, //iptal edildi
        REVERSED //geri alındı


}
