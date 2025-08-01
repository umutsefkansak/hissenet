package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.LoginRequest;
import com.infina.hissenet.dto.response.AuthResponse;
import com.infina.hissenet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    /**
     * Kullanıcının giriş işlemini gerçekleştirir.
     *
     * Başarılı girişte, yanıt gövdesinde {@link ApiResponse} ile sarmalanmış bir {@link AuthResponse} döner
     * ve ayrıca bir HTTP-only cookie (login-token) `Set-Cookie` başlığıyla yanıt edilir.
     *
     * Burada {@link ResponseEntity} kullanmamızın sebebi, hem gövdeyi hem de header'daki cookie bilgisini
     * aynı anda dönebilmek içindir.
     *
     * Cookie'nin HttpOnly olarak işaretlenmesi, client-side JavaScript'in bu cookie'ye erişimini engeller,
     * böylece XSS (Cross-Site Scripting) saldırılarına karşı ek bir koruma sağlar.
     *
     * @param request Giriş için gerekli kullanıcı bilgilerini içerir (email ve şifre).
     * @return Giriş sonucu ve token bilgisini içeren HTTP yanıtı.
     */
    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        ResponseCookie cookie = ResponseCookie.from("sessionId", response.sessionId()).path("/").maxAge(response.time())
                .httpOnly(true).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(ApiResponse.ok("Login Successful", response));
    }
    // logout
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "sessionId",required = false) String token){
        authService.logout(token);
        ResponseCookie cookie=ResponseCookie.from("sessionId","").path("/").maxAge(0).httpOnly(true).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(ApiResponse.ok("Logout Successful"));

    }
}
