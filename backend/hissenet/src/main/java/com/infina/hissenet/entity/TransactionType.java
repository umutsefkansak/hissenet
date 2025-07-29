package com.infina.hissenet.entity;

public enum TransactionType {

        DEPOSIT, //para yatırma
        WITHDRAWAL, //para cekme
        TRANSFER, //transfer
        STOCK_PURCHASE, // hisse alım
        STOCK_SALE, //hisse satıs
        DIVIDEND, //temettü
        FEE, //komisyon
        REFUND, //iade
        BONUS, //bonus
        PENALTY, //cezai islem
        INTEREST
}
