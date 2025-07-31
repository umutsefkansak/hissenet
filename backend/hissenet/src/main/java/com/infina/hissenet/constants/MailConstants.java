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


    //Security attempt limits
    public static final int HIGH_SECURITY_MAX_ATTEMPTS = 2;
    public static final int STANDARD_SECURITY_MAX_ATTEMPTS = 3;
    public static final int LOW_SECURITY_MAX_ATTEMPTS = 5;


    /** Maximum verification codes per email address per day */
    public static final int MAX_CODES_PER_DAY = 10;


    // Cleanup and maintenance intervals
    public static final int EXPIRED_CODES_CLEANUP_DAYS = 1;
    public static final int BLOCKED_CODES_UNBLOCK_HOURS = 1;


    /**
     * Email subject lines for different email types.
     */
    public static final class Subjects {
        private Subjects() {}

        public static final String VERIFICATION_CODE = "HisseNet - Doğrulama Kodu";
        public static final String PASSWORD_RESET = "HisseNet - Şifre Sıfırlama";
        public static final String LOGIN_NOTIFICATION = "HisseNet - Giriş Bildirimi";
        public static final String TRADE_NOTIFICATION = "HisseNet - İşlem Bildirimi";
        public static final String ACCOUNT_STATUS = "HisseNet - Hesap Durumu";
        public static final String ERROR_NOTIFICATION = "HisseNet - Sistem Bildirimi";
        public static final String DEFAULT = "HisseNet - Bilgilendirme";
    }


    /**
     * Purpose messages for verification code templates.
     */
    public static final class PurposeMessages {
        private PurposeMessages() {}

        public static final String PASSWORD_RESET = "şifrenizi sıfırlamak";
        public static final String LOGIN_NOTIFICATION = "hesabınıza giriş yapmak";
        public static final String DEFAULT = "işleminizi tamamlamak";
    }


    /**
     * User-facing messages and error codes.
     */
    public static final class Messages {
        private Messages() {}

        // Success messages
        public static final String MAIL_SENT_SUCCESS = "Mail başarıyla gönderildi";
        public static final String MAIL_SEND_ERROR = "Mail gönderilirken hata oluştu: ";
        public static final String VERIFICATION_CODE_SENT = "Doğrulama kodu gönderildi";
        public static final String VERIFICATION_CODE_SEND_ERROR = "Doğrulama kodu gönderilirken hata oluştu: ";
        public static final String CODE_VERIFIED_SUCCESS = "Kod başarıyla doğrulandı";

        // Security and limit messages
        public static final String DAILY_LIMIT_EXCEEDED = "Günlük kod gönderim limitine ulaşıldı";
        public static final String TOO_MANY_WRONG_ATTEMPTS = "Bu işlem için çok fazla yanlış deneme yapıldı. Lütfen daha sonra tekrar deneyin.";
        public static final String IP_LIMIT_EXCEEDED = "Çok fazla deneme yapıldı. Lütfen daha sonra tekrar deneyin.";
        public static final String ACTIVE_CODE_NOT_FOUND = "Geçerli bir doğrulama kodu bulunamadı";
        public static final String CODE_BLOCKED = "Çok fazla yanlış deneme yapıldı. Kod bloke edildi.";

        /** Format: "Wrong code. Remaining attempts: {count}" */
        public static final String WRONG_CODE_FORMAT = "Yanlış kod. Kalan deneme hakkı: %d";

        // System messages
        public static final String EXPIRED_CODES_CLEANED = "Süresi dolmuş doğrulama kodları temizlendi";
        public static final String BLOCKED_CODES_UNBLOCKED = "Süresi dolmuş bloke kodları serbest bırakıldı";

        // Email content
        public static final String LOGIN_CODE_ADDITIONAL_INFO = "Hesabınıza güvenli giriş için bu kodu kullanın.";
        public static final String PASSWORD_RESET_ADDITIONAL_INFO = "Şifrenizi güvenli bir şekilde sıfırlayabilmek için bu kodu kullanın.";
        public static final String DO_NOT_SHARE_CODE = "Bu kodu kimseyle paylaşmayınız.";
        public static final String AUTO_GENERATED_MAIL = "Bu mail otomatik olarak gönderilmiştir. Lütfen yanıtlamayınız.";
        public static final String AUTO_GENERATED_NOTIFICATION = "Bu mail otomatik olarak gönderilmiştir.";
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

    /**
     * Company branding constants.
     */
    public static final class Company {
        private Company() {}

        public static final String NAME = "HisseNet Aracı Kurum";
    }
}