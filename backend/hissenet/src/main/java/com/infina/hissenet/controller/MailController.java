
package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.MailControllerDoc;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.IMailService;
import com.infina.hissenet.service.abstracts.IVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController implements MailControllerDoc {

    private final IMailService mailService;
    private final IVerificationService verificationService;

    public MailController(IMailService mailService, IVerificationService verificationService) {
        this.mailService = mailService;
        this.verificationService = verificationService;
    }

    @PostMapping("/send")
    public ApiResponse<MailSendResponse> sendMail(@Valid @RequestBody MailSendRequest request) {
        MailSendResponse response = mailService.sendMail(request);
        return ApiResponse.ok("Mail sent", response);
    }

    @PostMapping("/send-verification")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<CodeSendResponse> sendVerificationCode(@Valid @RequestBody CodeSendRequest request) {
        CodeSendResponse response = mailService.sendVerificationCode(request);
        return ApiResponse.ok("Verification code sent", response);
    }

    @PostMapping("/send-password-reset")
    public ApiResponse<CodeSendResponse> sendPasswordResetCode(@Valid @RequestBody CodeSendRequest request) {
        CodeSendResponse response = mailService.sendPasswordResetCode(request);
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

    @DeleteMapping("/clear-verification/{email}")
    public ApiResponse<String> clearVerificationCode(@PathVariable String email) {
        verificationService.clearVerificationCode(email);
        return ApiResponse.ok("Verification code cleared for: " + email);
    }

    @PostMapping("/send-password-change-token")
    public ApiResponse<PasswordChangeTokenResponse> sendPasswordChangeToken(@Valid @RequestBody PasswordChangeTokenRequest request) {
        PasswordChangeTokenResponse response = mailService.sendPasswordChangeToken(request);
        return ApiResponse.ok("Password change token sent", response);
    }

    @PostMapping("/verify-password-change-token")
    public ApiResponse<VerifyPasswordChangeTokenResponse> verifyPasswordChangeToken(@Valid @RequestBody VerifyPasswordChangeTokenRequest request) {
        VerifyPasswordChangeTokenResponse response = verificationService.verifyPasswordChangeToken(request);
        return ApiResponse.ok("Password change token verification completed", response);
    }
}