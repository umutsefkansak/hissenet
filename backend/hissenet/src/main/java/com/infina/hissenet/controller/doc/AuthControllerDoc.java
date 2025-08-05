package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.LoginRequest;
import com.infina.hissenet.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication API", description = "Kullanıcı giriş ve çıkış işlemleri")
public interface AuthControllerDoc {

    @Operation(
        summary = "Kullanıcı girişi yapar",
        description = """
            Kullanıcının e-posta ve şifre ile giriş yapmasını sağlar.
            
            Başarılı girişte:
            - Yanıt gövdesinde ApiResponse ile sarmalanmış AuthResponse döner
            - HTTP-only cookie (sessionId) Set-Cookie başlığıyla yanıt edilir
            - Cookie'nin HttpOnly olarak işaretlenmesi XSS saldırılarına karşı koruma sağlar
            
            Güvenlik özellikleri:
            - Şifre hash'lenmiş olarak saklanır
            - Session token güvenli şekilde oluşturulur
            - Cookie HttpOnly flag ile korunur
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Giriş başarılı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Login Successful",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "employee": {
                          "id": 123,
                          "firstName": "Ahmet",
                          "lastName": "Yılmaz",
                          "email": "ahmet.yilmaz@example.com",
                          "phone": "+90 555 123 4567",
                          "role": "EMPLOYEE",
                          "status": "ACTIVE",
                          "createdAt": "2024-01-15T10:30:00",
                          "updatedAt": "2024-08-03T14:30:15"
                        }
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
                        "password": "Şifre 6-100 karakter arasında olmalıdır"
                      }
                    }
                    """
                ))),
            @ApiResponse(responseCode = "401", description = "Geçersiz kimlik bilgileri",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/unauthorized",
                      "title": "Unauthorized",
                      "status": 401,
                      "detail": "Geçersiz e-posta veya şifre",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "403", description = "Hesap bloke edilmiş",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/forbidden",
                      "title": "Forbidden",
                      "status": 403,
                      "detail": "Hesabınız geçici olarak bloke edilmiştir. Lütfen daha sonra tekrar deneyiniz",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı",
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
    ResponseEntity<com.infina.hissenet.common.ApiResponse<AuthResponse>> login(
        @Parameter(description = "Giriş bilgileri", required = true,
            schema = @Schema(implementation = LoginRequest.class,
                example = """
                {
                  "email": "ahmet.yilmaz@example.com",
                  "password": "güvenliŞifre123"
                }
                """
            )
        )
        LoginRequest request
    );

    @Operation(
        summary = "Kullanıcı çıkışı yapar",
        description = """
            Kullanıcının oturumunu sonlandırır ve session token'ını geçersiz kılar.
            
            İşlem adımları:
            - Session token'ı Redis'ten silinir
            - HTTP-only cookie temizlenir (maxAge=0)
            - Kullanıcı oturumu tamamen sonlandırılır
            
            Güvenlik özellikleri:
            - Token server-side olarak geçersiz kılınır
            - Cookie client-side'dan da temizlenir
            - Çift taraflı güvenlik sağlanır
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Çıkış başarılı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Logout Successful",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": null
                    }
                    """
                ))),
            @ApiResponse(responseCode = "400", description = "Geçersiz token",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/bad-request",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Geçersiz session token",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "401", description = "Token bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/unauthorized",
                      "title": "Unauthorized",
                      "status": 401,
                      "detail": "Session token bulunamadı",
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
    ResponseEntity<com.infina.hissenet.common.ApiResponse<Void>> logout(
        @Parameter(description = "Session token (cookie'den otomatik alınır)", required = false,
            in = ParameterIn.COOKIE, example = "abc123def456ghi789")
        String token
    );
}
