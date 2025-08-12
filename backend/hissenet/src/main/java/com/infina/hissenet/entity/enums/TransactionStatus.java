package com.infina.hissenet.entity.enums;

public enum TransactionStatus {

        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED,
        SETTLED,
        PARTIALLY_SOLD,  // FIFO için: Kısmen satılmış
        SOLD             // FIFO için: Tamamen satılmış

}
