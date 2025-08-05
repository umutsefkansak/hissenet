package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Mail", description = "E-posta yönetimi ve doğrulama API'si")
public interface MailControllerDoc {

    @Operation(
            summary = "E-posta gönderir",
            description = """
            Belirtilen alıcıya e-posta gönderir.
            Konu, içerik ve alıcı adı parametreleri ile özelleştirilebilir.
            İşlem asenkron olarak gerçekleştirilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "E-posta başarıyla gönderildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Mail sent",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": {
                                "success": true,
                                "message": "E-posta başarıyla gönderildi"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Validation hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/validation",
                              "title": "Validation Error",
                              "status": 400,
                              "detail": "Validation failed",
                              "timestamp": "2025-08-03T14:30:15.123",
                              "errors": {
                                "to": "Geçerli bir e-posta adresi giriniz",
                                "subject": "Konu boş olamaz",
                                "content": "İçerik boş olamaz"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "422", description = "E-posta gönderim hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/mail-processing",
                              "title": "Mail Processing Error",
                              "status": 422,
                              "detail": "E-posta gönderilemedi: SMTP sunucu hatası",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<MailSendResponse> sendMail(
            @Parameter(description = "E-posta gönderim bilgileri", required = true,
                    schema = @Schema(implementation = MailSendRequest.class,
                            example = """
                {
                  "to": "kullanici@example.com",
                  "subject": "Hoş geldiniz!",
                  "content": "<h1>Merhaba!</h1><p>Sistemimize hoş geldiniz.</p>",
                  "recipientName": "Ahmet Yılmaz"
                }
                """
                    )
            )
            MailSendRequest request
    );

    @Operation(
            summary = "Doğrulama kodu gönderir",
            description = """
            Belirtilen e-posta adresine doğrulama kodu gönderir.
            Kod süresi ve maksimum deneme sayısı özelleştirilebilir.
            Varsayılan olarak 10 dakika geçerli ve 3 deneme hakkı verilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doğrulama kodu başarıyla gönderildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Verification code sent",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": {
                                "success": true,
                                "message": "Doğrulama kodu başarıyla gönderildi",
                                "maxAttempts": 3,
                                "expiryMinutes": 10
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Validation hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/validation",
                              "title": "Validation Error",
                              "status": 400,
                              "detail": "Validation failed",
                              "timestamp": "2025-08-03T14:30:15.123",
                              "errors": {
                                "email": "Geçerli bir e-posta adresi giriniz"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "422", description = "Doğrulama kodu gönderim hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/mail-processing",
                              "title": "Mail Processing Error",
                              "status": 422,
                              "detail": "Doğrulama kodu gönderilemedi: E-posta servisi geçici olarak kullanılamıyor",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "429", description = "Çok fazla istek - Rate limit aşıldı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/rate-limit",
                              "title": "Too Many Requests",
                              "status": 429,
                              "detail": "Bu e-posta adresine çok fazla doğrulama kodu gönderildi. Lütfen daha sonra tekrar deneyiniz",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<CodeSendResponse> sendVerificationCode(
            @Parameter(description = "Doğrulama kodu gönderim bilgileri", required = true,
                    schema = @Schema(implementation = CodeSendRequest.class,
                            example = """
                {
                  "email": "kullanici@example.com",
                  "recipientName": "Ahmet Yılmaz",
                  "description": "Hesap doğrulama",
                  "maxAttempts": 3,
                  "expiryMinutes": 10,
                  "additionalInfo": "Güvenlik amacıyla gönderilmiştir"
                }
                """
                    )
            )
            CodeSendRequest request
    );

    @Operation(
            summary = "Doğrulama kodunu kontrol eder",
            description = """
            Kullanıcının girdiği doğrulama kodunu kontrol eder.
            Yanlış kod girildiğinde kalan deneme hakkı azalır.
            Maksimum deneme sayısı aşılırsa hesap geçici olarak bloke edilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doğrulama başarılı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Code verification completed",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": {
                                "success": true,
                                "message": "Doğrulama başarıyla tamamlandı",
                                "remainingAttempts": 0,
                                "blocked": false,
                                "blockedUntil": null
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Yanlış kod - Deneme hakkı var",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Code verification completed",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": {
                                "success": false,
                                "message": "Yanlış doğrulama kodu. Kalan deneme hakkınız: 2",
                                "remainingAttempts": 2,
                                "blocked": false,
                                "blockedUntil": null
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Hesap bloke edildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Code verification completed",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": {
                                "success": false,
                                "message": "Çok fazla yanlış deneme. Hesabınız geçici olarak bloke edildi",
                                "remainingAttempts": 0,
                                "blocked": true,
                                "blockedUntil": "2025-08-03T15:30:15.123"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Validation hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/validation",
                              "title": "Validation Error",
                              "status": 400,
                              "detail": "Validation failed",
                              "timestamp": "2025-08-03T14:30:15.123",
                              "errors": {
                                "email": "Geçerli bir e-posta adresi giriniz",
                                "code": "Doğrulama kodu 4-10 karakter arasında olmalıdır"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Doğrulama kodu bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Bu e-posta adresi için geçerli bir doğrulama kodu bulunamadı",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "422", description = "Doğrulama kodu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/mail-processing",
                              "title": "Mail Processing Error",
                              "status": 422,
                              "detail": "Doğrulama kodu süresi dolmuş",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<CodeVerifyResponse> verifyCode(
            @Parameter(description = "Doğrulama kodu kontrol bilgileri", required = true,
                    schema = @Schema(implementation = CodeVerifyRequest.class,
                            example = """
                {
                  "email": "kullanici@example.com",
                  "code": "123456"
                }
                """
                    )
            )
            CodeVerifyRequest request,
            HttpServletRequest httpRequest
    );

    @Operation(
            summary = "Bildirim e-postası gönderir",
            description = """
            Sistem bildirimi olarak e-posta gönderir.
            Bu endpoint sadece EMPLOYEE rolüne sahip kullanıcılar tarafından kullanılabilir.
            İşlem asenkron olarak gerçekleştirilir.
            """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bildirim başarıyla gönderildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Notification sent",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": "Bildirim e-postası başarıyla gönderildi"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Validation hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/validation",
                              "title": "Validation Error",
                              "status": 400,
                              "detail": "Validation failed",
                              "timestamp": "2025-08-03T14:30:15.123",
                              "errors": {
                                "email": "Geçerli bir e-posta adresi giriniz",
                                "message": "Mesaj boş olamaz"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "401", description = "Yetkilendirme hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/unauthorized",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "Authentication token gerekli",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Erişim yetkisi yok",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/forbidden",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "Bu işlem için EMPLOYEE yetkisi gereklidir",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "422", description = "E-posta gönderim hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/mail-processing",
                              "title": "Mail Processing Error",
                              "status": 422,
                              "detail": "Bildirim e-postası gönderilemedi",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<String> sendNotification(
            @Parameter(description = "Bildirim e-posta bilgileri", required = true,
                    schema = @Schema(implementation = NotificationSendRequest.class,
                            example = """
                {
                  "email": "kullanici@example.com",
                  "recipientName": "Ahmet Yılmaz",
                  "message": "THYAO hissesinden 50 adet satım işleminiz 45.30 TL fiyatından gerçekleşmiştir. İşlem numarası: TXN-2024-001234",
                  "title": "Hisse Satım İşlemi"
                }
                """
                    )
            )
            NotificationSendRequest request
    );

    @Operation(
            summary = "E-posta limit durumunu kontrol eder",
            description = """
            Belirtilen e-posta adresinin günlük limit durumunu kontrol eder.
            Bu endpoint sadece EMPLOYEE rolüne sahip kullanıcılar tarafından kullanılabilir.
            Rate limiting ve spam koruması için kullanılır.
            """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Limit durumu başarıyla kontrol edildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Email limit status",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": false
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Limit aşıldı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Email limit status",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": true
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "401", description = "Yetkilendirme hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/unauthorized",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "Authentication token gerekli",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Erişim yetkisi yok",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/forbidden",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "Bu işlem için EMPLOYEE yetkisi gereklidir",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Boolean> checkEmailLimit(
            @Parameter(description = "Kontrol edilecek e-posta adresi", required = true,
                    in = ParameterIn.PATH, example = "kullanici@example.com")
            String email
    );

    @Operation(
            summary = "Doğrulama kodunu temizler",
            description = """
            Belirtilen e-posta adresine ait aktif doğrulama kodunu sistemden temizler.
            Bu işlem geri alınamaz ve kullanıcının yeni kod talep etmesi gerekir.
            Güvenlik amaçlı veya test durumlarında kullanılır.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doğrulama kodu başarıyla temizlendi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Verification code cleared for: kullanici@example.com",
                              "localDateTime": "2025-08-03T14:30:15.123",
                              "data": "Doğrulama kodu başarıyla temizlendi"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-03T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<String> clearVerificationCode(
            @Parameter(description = "Doğrulama kodu temizlenecek e-posta adresi", required = true,
                    in = ParameterIn.PATH, example = "kullanici@example.com")
            String email
    );

}