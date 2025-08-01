package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.IMailService;
import com.infina.hissenet.service.abstracts.IMailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/mail")
public class MailController{

    private final IMailService mailService;

    public MailController(IMailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ApiResponse<MailSendResponse> sendMail(@Valid @RequestBody MailSendRequest request) {
        MailSendResponse response = mailService.sendMail(request);
        return ApiResponse.ok("Mail sent", response);
    }

    @PostMapping("/send-verification")
    public ApiResponse<CodeSendResponse> sendVerificationCode(@Valid @RequestBody CodeSendRequest request) {
        CodeSendResponse response = mailService.sendVerificationCode(request);
        return ApiResponse.ok("Verification code sent", response);
    }

    @PostMapping("/verify")
    public ApiResponse<CodeVerifyResponse> verifyCode(@Valid @RequestBody CodeVerifyRequest request,
                                                      HttpServletRequest httpRequest) {
        CodeVerifyResponse result = mailService.verifyCode(request, httpRequest);
        return ApiResponse.ok("Code verification completed", result);
    }

    @PostMapping("/send-notification")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<String> sendNotification(@Valid @RequestBody NotificationSendRequest request) {
        mailService.sendNotification(request);
        return ApiResponse.ok("Notification sent");
    }

    @GetMapping("/check-email-limit/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<Boolean> checkEmailLimit(@PathVariable String email) {
        boolean exceeded = mailService.isEmailLimitExceeded(email);
        return ApiResponse.ok("Email limit status", exceeded);
    }

    @PostMapping("/cleanup-expired")
    public ApiResponse<String> cleanupExpiredCodes() {
        mailService.cleanupExpiredCodes();
        return ApiResponse.ok("Expired codes cleared");
    }

    @PostMapping("/unblock-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> unblockExpiredCodes() {
        mailService.unblockExpiredCodes();
        return ApiResponse.ok("Expired blocked codes released");
    }
}