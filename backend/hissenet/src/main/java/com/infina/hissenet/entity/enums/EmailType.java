package com.infina.hissenet.entity.enums;

public enum EmailType {
    VERIFICATION_CODE("Doğrulama Kodu"),
    PASSWORD_RESET("Şifre Sıfırlama"),
    LOGIN_NOTIFICATION("Giriş Bildirimi"),
    TRADE_NOTIFICATION("İşlem Bildirimi"),
    ACCOUNT_STATUS("Hesap Durumu"),
    GENERAL_INFO("Genel Bilgilendirme"),
    ERROR_NOTIFICATION("Hata Bildirimi");

    private final String description;

    EmailType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}