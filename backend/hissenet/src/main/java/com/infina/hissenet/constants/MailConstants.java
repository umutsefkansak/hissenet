package com.infina.hissenet.constants;


/**
 * Constants for mail service operations including security configurations,
 * limits, message templates, and system settings.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public final class MailConstants {

    private MailConstants() {
    }



    public static final class Config {
        private Config() {}

        public static final String DEFAULT_MAIL_ENCODING = "UTF-8";
        public static final String DEFAULT_NOTIFICATION_SUBJECT = "Bildirim";
        public static final String DEFAULT_VERIFICATION_SUBJECT = "Doğrulama Kodu";
        public static final String VERIFICATION_SUBJECT_WITH_DESC_FORMAT = "%s - Doğrulama Kodu";
    }

    /**
     * Email subject lines for different email types.
     */
    public static final class Subjects {
        private Subjects() {}

        public static final String VERIFICATION_CODE_FORMAT = "%s - Doğrulama Kodu";
        public static final String PASSWORD_RESET_FORMAT = "%s - Şifre Sıfırlama";
        public static final String LOGIN_NOTIFICATION_FORMAT = "%s - Giriş Bildirimi";
        public static final String TRADE_NOTIFICATION_FORMAT = "%s - İşlem Bildirimi";
        public static final String ACCOUNT_STATUS_FORMAT = "%s - Hesap Durumu";
        public static final String ERROR_NOTIFICATION_FORMAT = "%s - Sistem Bildirimi";
        public static final String DEFAULT_FORMAT = "%s - Bilgilendirme";
    }



    /**
     * User-facing messages and error codes.
     */
    public static final class Messages {
        private Messages() {}

        // Success messages

        public static final String CODE_VERIFIED_SUCCESS = "Kod başarıyla doğrulandı";
        public static final String PASSWORD_CHANGE_TOKEN_VALID = "Şifre değiştirme token'ı geçerli";
        public static final String INVALID_PASSWORD_CHANGE_TOKEN = "Geçersiz veya süresi dolmuş şifre değiştirme linki";

        // Security and limit messages
        public static final String DAILY_LIMIT_EXCEEDED = "Günlük kod gönderim limitine ulaşıldı";
        public static final String TOO_MANY_WRONG_ATTEMPTS = "Bu işlem için çok fazla yanlış deneme yapıldı. Lütfen daha sonra tekrar deneyin.";
        public static final String IP_LIMIT_EXCEEDED = "Çok fazla deneme yapıldı. Lütfen daha sonra tekrar deneyin.";
        public static final String ACTIVE_CODE_NOT_FOUND = "Geçerli bir doğrulama kodu bulunamadı";
        public static final String CODE_BLOCKED = "Çok fazla yanlış deneme yapıldı. Kod bloke edildi.";

        public static final String WRONG_CODE_FORMAT = "Yanlış kod. Kalan deneme hakkı: %d";

    }

    /**
     * Email greeting templates.
     */
    public static final class Greetings {
        private Greetings() {}

        /** Format: "Dear {name}," */
        public static final String NAMED_GREETING = "Sayın %s,";
        public static final String DEFAULT_GREETING = "Sayın Müşterimiz,";
    }


    /**
     * HTTP headers for IP detection.
     */
    public static final class HttpHeaders {
        private HttpHeaders() {}

        public static final String X_FORWARDED_FOR = "X-Forwarded-For";
        public static final String X_REAL_IP = "X-Real-IP";
    }

}