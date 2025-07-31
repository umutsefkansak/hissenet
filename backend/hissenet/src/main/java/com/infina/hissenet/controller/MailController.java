package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.IMailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final IMailService mailService;

    public MailController(IMailService mailService) {
        this.mailService = mailService;
    }


    @PostMapping("/send")
    public ApiResponse<MailSendResponse> sendMail(@Valid @RequestBody MailSendDto dto) {
        MailSendResponse response = mailService.sendMail(dto);
        return ApiResponse.ok("Mail gönderildi", response);
    }


    @PostMapping("/send-verification-code")
    public ApiResponse<MailSendResponse> sendVerificationCode(@Valid @RequestBody VerificationCodeSendDto dto) {
        MailSendResponse response = mailService.sendVerificationCode(dto);
        return response.success() ?
                ApiResponse.ok("Doğrulama kodu gönderildi", response) :
                ApiResponse.ok(response.message(), response);
    }


    @PostMapping("/send-high-security-code")
    public ApiResponse<MailSendResponse> sendHighSecurityCode(@Valid @RequestBody VerificationCodeSendDto dto) {
        MailSendResponse response = mailService.sendHighSecurityCode(dto);
        return response.success() ?
                ApiResponse.ok("Yüksek güvenlikli doğrulama kodu gönderildi", response) :
                ApiResponse.ok(response.message(), response);
    }


    @PostMapping("/send-low-security-code")
    public ApiResponse<MailSendResponse> sendLowSecurityCode(@Valid @RequestBody VerificationCodeSendDto dto) {
        MailSendResponse response = mailService.sendLowSecurityCode(dto);
        return response.success() ?
                ApiResponse.ok("Doğrulama kodu gönderildi", response) :
                ApiResponse.ok(response.message(), response);
    }


    @PostMapping("/verify-code")
    public ApiResponse<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeDto dto,
                                                      HttpServletRequest request) {
        VerifyCodeResponse response = mailService.verifyCode(dto, request);
        return response.valid() ?
                ApiResponse.ok("Kod başarıyla doğrulandı", response) :
                ApiResponse.ok(response.message(), response);
    }


    @PostMapping("/send-password-reset-code")
    public ApiResponse<MailSendResponse> sendPasswordResetCode(@Valid @RequestBody PasswordResetCodeDto dto) {
        MailSendResponse response = mailService.sendPasswordResetCode(dto);
        return response.success() ?
                ApiResponse.ok("Şifre sıfırlama kodu gönderildi", response) :
                ApiResponse.ok(response.message(), response);
    }


    @PostMapping("/send-login-code")
    public ApiResponse<MailSendResponse> sendLoginCode(@Valid @RequestBody LoginCodeDto dto) {
        MailSendResponse response = mailService.sendLoginCode(dto);
        return response.success() ?
                ApiResponse.ok("Giriş doğrulama kodu gönderildi", response) :
                ApiResponse.ok(response.message(), response);
    }


    @PostMapping("/send-trade-notification")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<String> sendTradeNotification(@Valid @RequestBody TradeNotificationDto dto) {
        mailService.sendNotification(dto.email(), dto.recipientName(),
                dto.type(), dto.message());
        return ApiResponse.ok("Bildirim gönderildi");
    }


    @GetMapping("/check-email-limit/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<Boolean> checkEmailLimit(@PathVariable String email) {
        boolean exceeded = mailService.isEmailLimitExceeded(email);
        return ApiResponse.ok("Email limit durumu", exceeded);
    }


    @PostMapping("/cleanup-expired")
    public ApiResponse<String> cleanupExpiredCodes() {
        mailService.cleanupExpiredCodes();
        return ApiResponse.ok("Süresi dolmuş kodlar temizlendi");
    }


    @PostMapping("/unblock-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> unblockExpiredCodes() {
        mailService.unblockExpiredCodes();
        return ApiResponse.ok("Süresi dolmuş bloke kodları serbest bırakıldı");
    }
}