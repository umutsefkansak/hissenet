package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.CodeSendRequest;
import com.infina.hissenet.dto.request.CodeVerifyRequest;
import com.infina.hissenet.dto.request.MailSendRequest;
import com.infina.hissenet.dto.request.NotificationSendRequest;
import com.infina.hissenet.dto.response.CodeSendResponse;
import com.infina.hissenet.dto.response.CodeVerifyResponse;
import com.infina.hissenet.dto.response.MailSendResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Mail", description = "E-posta ve doğrulama kodu yönetimi API'si")
public interface MailControllerDoc {

    @Operation(
            summary = "E-posta gönderir",
            description = """
            Belirtilen alıcıya e-posta gönderir.
            Konu, içerik ve isteğe bağlı alıcı adı bilgilerini içerir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "E-posta başarıyla gönderildi",
                            content = @Content(schema = @Schema(implementation = MailSendResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz e-posta formatı veya eksik bilgi"),
                    @ApiResponse(responseCode = "500", description = "E-posta gönderme hatası")
            }
    )
    com.infina.hissenet.common.ApiResponse<MailSendResponse> sendMail(
            @Parameter(description = "Gönderilecek e-posta bilgileri", required = true,
                    schema = @Schema(implementation = MailSendRequest.class,
                            example = """
                {
                  "to": "kullanici@example.com",
                  "subject": "Hoş Geldiniz",
                  "content": "Sistemimize hoş geldiniz! Hesabınız başarıyla oluşturuldu.",
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
            Kod süresi, maksimum deneme sayısı ve açıklama belirtilebilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Doğrulama kodu başarıyla gönderildi",
                            content = @Content(schema = @Schema(implementation = CodeSendResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz parametreler veya e-posta formatı"),
                    @ApiResponse(responseCode = "429", description = "E-posta gönderim limiti aşıldı"),
                    @ApiResponse(responseCode = "500", description = "Kod oluşturma veya gönderme hatası")
            }
    )
    com.infina.hissenet.common.ApiResponse<CodeSendResponse> sendVerificationCode(
            @Parameter(description = "Doğrulama kodu gönderme bilgileri", required = true,
                    schema = @Schema(implementation = CodeSendRequest.class,
                            example = """
                {
                  "email": "kullanici@example.com",
                  "recipientName": "Ahmet Yılmaz",
                  "description": "Hesap doğrulama",
                  "maxAttempts": 3,
                  "expiryMinutes": 15,
                  "additionalInfo": "Lütfen 15 dakika içinde kodunuzu girin"
                }
                """
                    )
            )
            CodeSendRequest request
    );

    @Operation(
            summary = "Doğrulama kodunu kontrol eder",
            description = """
            Gönderilen doğrulama kodunu kontrol eder ve doğrular.
            Başarılı doğrulama sonrası kod otomatik olarak geçersiz hale gelir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kod başarıyla doğrulandı",
                            content = @Content(schema = @Schema(implementation = CodeVerifyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz kod formatı"),
                    @ApiResponse(responseCode = "401", description = "Yanlış doğrulama kodu"),
                    @ApiResponse(responseCode = "410", description = "Kodun süresi dolmuş"),
                    @ApiResponse(responseCode = "423", description = "Maksimum deneme sayısı aşıldı"),
                    @ApiResponse(responseCode = "404", description = "Kod bulunamadı")
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
            @Parameter(hidden = true)
            HttpServletRequest httpRequest
    );

    @Operation(
            summary = "Bildirim e-postası gönderir",
            description = """
            Belirtilen kullanıcıya bildirim e-postası gönderir.
            Bu işlem sadece EMPLOYEE rolüne sahip kullanıcılar tarafından gerçekleştirilebilir.
            """,
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bildirim başarıyla gönderildi"),
                    @ApiResponse(responseCode = "400", description = "Geçersiz bildirim içeriği"),
                    @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
                    @ApiResponse(responseCode = "403", description = "EMPLOYEE rolü gerekli"),
                    @ApiResponse(responseCode = "500", description = "Bildirim gönderme hatası")
            }
    )
    com.infina.hissenet.common.ApiResponse<String> sendNotification(
            @Parameter(description = "Gönderilecek bildirim bilgileri", required = true,
                    schema = @Schema(implementation = NotificationSendRequest.class,
                            example = """
                {
                  "email": "kullanici@example.com",
                  "recipientName": "Ahmet Yılmaz",
                  "message": "Hesabınızda önemli bir güncelleme yapıldı. Lütfen kontrol edin.",
                  "title": "Hesap Güncelleme Bildirimi"
                }
                """
                    )
            )
            NotificationSendRequest request
    );

    @Operation(
            summary = "E-posta gönderim limitini kontrol eder",
            description = """
            Belirtilen e-posta adresinin günlük gönderim limitini aşıp aşmadığını kontrol eder.
            Bu işlem sadece EMPLOYEE rolüne sahip kullanıcılar tarafından gerçekleştirilebilir.
            """,
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Limit durumu başarıyla kontrol edildi"),
                    @ApiResponse(responseCode = "400", description = "Geçersiz e-posta formatı"),
                    @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
                    @ApiResponse(responseCode = "403", description = "EMPLOYEE rolü gerekli")
            }
    )
    com.infina.hissenet.common.ApiResponse<Boolean> checkEmailLimit(
            @Parameter(description = "Kontrol edilecek e-posta adresi", required = true,
                    in = ParameterIn.PATH, example = "kullanici@example.com")
            String email
    );

    @Operation(
            summary = "Süresi dolmuş kodları temizler",
            description = """
            Sistemdeki süresi dolmuş doğrulama kodlarını temizler.
            Bu işlem sistem performansını artırmak için düzenli olarak çalıştırılabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Süresi dolmuş kodlar başarıyla temizlendi"),
                    @ApiResponse(responseCode = "500", description = "Temizleme işleminde hata oluştu")
            }
    )
    com.infina.hissenet.common.ApiResponse<String> cleanupExpiredCodes();

    @Operation(
            summary = "Süresi dolmuş bloke kodları serbest bırakır",
            description = """
            Maksimum deneme sayısını aşarak bloke olmuş ancak süresi dolmuş kodları serbest bırakır.
            Bu işlem sadece ADMIN rolüne sahip kullanıcılar tarafından gerçekleştirilebilir.
            """,
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bloke kodlar başarıyla serbest bırakıldı"),
                    @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
                    @ApiResponse(responseCode = "403", description = "ADMIN rolü gerekli"),
                    @ApiResponse(responseCode = "500", description = "İşlem sırasında hata oluştu")
            }
    )
    com.infina.hissenet.common.ApiResponse<String> unblockExpiredCodes();
}