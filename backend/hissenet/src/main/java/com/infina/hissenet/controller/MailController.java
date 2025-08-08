
package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.MailControllerDoc;
import com.infina.hissenet.dto.request.*;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.IMailService;
import com.infina.hissenet.service.abstracts.IVerificationService;
import com.infina.hissenet.utils.MessageUtils;
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
        return ApiResponse.ok(MessageUtils.getMessage("mail.sent.successfully"), response);
    }

    @PostMapping("/send-verification")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<CodeSendResponse> sendVerificationCode(@Valid @RequestBody CodeSendRequest request) {
        CodeSendResponse response = mailService.sendVerificationCode(request);
        return ApiResponse.ok(MessageUtils.getMessage("mail.verification.code.sent.successfully"), response);
    }


    @PostMapping("/verify")
    public ApiResponse<CodeVerifyResponse> verifyCode(@Valid @RequestBody CodeVerifyRequest request,
                                                      HttpServletRequest httpRequest) {
        CodeVerifyResponse result = mailService.verifyCode(request, httpRequest);
        return ApiResponse.ok(MessageUtils.getMessage("mail.code.verification.completed"), result);
    }

    @PostMapping("/send-notification")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<String> sendNotification(@Valid @RequestBody NotificationSendRequest request) {
        mailService.sendNotification(request);
        return ApiResponse.ok(MessageUtils.getMessage("mail.notification.sent.successfully"));
    }

    @PostMapping("/send-verification-by-identification")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<CodeSendResponse> sendVerificationCodeByIdentification(@Valid @RequestBody CustomerIdentificationRequest request) {
        CodeSendResponse response = mailService.sendVerificationCodeByIdentification(request);
        return ApiResponse.ok(MessageUtils.getMessage("mail.verification.code.sent.to.customer"), response);
    }

    @GetMapping("/check-email-limit/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<Boolean> checkEmailLimit(@PathVariable String email) {
        boolean exceeded = mailService.isEmailLimitExceeded(email);
        return ApiResponse.ok(MessageUtils.getMessage("mail.email.limit.status.checked"),exceeded);
    }

    @DeleteMapping("/clear-verification/{email}")
    public ApiResponse<String> clearVerificationCode(@PathVariable String email) {
        verificationService.clearVerificationCode(email);
        return ApiResponse.ok(MessageUtils.getMessage("mail.verification.code.cleared", email));
    }

    @PostMapping("/send-password-change-token")
    public ApiResponse<PasswordChangeTokenResponse> sendPasswordChangeToken(@Valid @RequestBody PasswordChangeTokenRequest request) {
        PasswordChangeTokenResponse response = mailService.sendPasswordChangeToken(request);
        return ApiResponse.ok(MessageUtils.getMessage("mail.password.change.token.sent.successfully"),response);
    }

    @PostMapping("/verify-password-change-token")
    public ApiResponse<VerifyPasswordChangeTokenResponse> verifyPasswordChangeToken(@Valid @RequestBody VerifyPasswordChangeTokenRequest request) {
        VerifyPasswordChangeTokenResponse response = verificationService.verifyPasswordChangeToken(request);
        return ApiResponse.ok(MessageUtils.getMessage("mail.password.change.token.verification.completed"), response);
    }
}