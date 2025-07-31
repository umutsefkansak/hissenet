package com.infina.hissenet.controller.doc;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.infina.hissenet.dto.request.AccountCreateRequest;
import com.infina.hissenet.dto.request.AccountUpdateRequest;
import com.infina.hissenet.dto.response.AccountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Accounts", description = "Hesap yönetimi API'si")
public interface AccountControllerDoc {
	
		@Operation(
	        summary = "Yeni hesap oluşturur",
	        description = """
	            Yeni bir hesap oluşturur.
	            Kullanıcı adı, şifre ve çalışana bağlı bilgileri içerir.
	            """,
	        responses = {
	            @ApiResponse(responseCode = "201", description = "Hesap başarıyla oluşturuldu",
	                content = @Content(schema = @Schema(implementation = AccountResponse.class))),
	            @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
	            @ApiResponse(responseCode = "404", description = "Çalışan bulunamadı")
	        }
	    )
		ResponseEntity<com.infina.hissenet.common.ApiResponse<AccountResponse>> createAccount(
	        @Parameter(description = "Oluşturulacak hesap bilgileri", required = true,
	            schema = @Schema(implementation = AccountCreateRequest.class,
	                example = """
	                {
	                  "username": "ornek.kullanici",
	                  "passwordHash": "password",
	                  "employeeId": 123
	                }
	                """
	            )
	        )
	        AccountCreateRequest request
	    );

	    @Operation(
	        summary = "Var olan hesabı günceller",
	        description = """
	            Hesap bilgilerini günceller. Kullanıcı adı ve çalışan değiştirilebilir.
	            """,
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Hesap başarıyla güncellendi",
	                content = @Content(schema = @Schema(implementation = AccountResponse.class))),
	            @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
	            @ApiResponse(responseCode = "404", description = "Hesap veya çalışan bulunamadı")
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<AccountResponse> updateAccount(
	        @Parameter(description = "Güncellenecek hesap ID'si", required = true, in = ParameterIn.PATH, example = "101")
	        Long id,
	        @Parameter(description = "Güncelleme bilgileri", required = true,
	            schema = @Schema(implementation = AccountUpdateRequest.class,
	                example = """
	                {
	                  "username": "yeni.kullanici",
	                  "employeeId": 124
	                }
	                """
	            )
	        )
	        AccountUpdateRequest request
	    );

	    @Operation(
	        summary = "ID ile hesap getirir",
	        description = "Belirtilen ID'ye sahip hesabı getirir.",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Hesap başarıyla getirildi",
	                content = @Content(schema = @Schema(implementation = AccountResponse.class))),
	            @ApiResponse(responseCode = "404", description = "Hesap bulunamadı")
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<AccountResponse> getAccount(
	        @Parameter(description = "Getirilecek hesap ID'si", required = true, example = "101")
	        Long id
	    );

	    @Operation(
	        summary = "Tüm hesapları listeler",
	        description = "Sistemdeki tüm hesapları listeler.",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Hesaplar başarıyla listelendi",
	                content = @Content(schema = @Schema(implementation = AccountResponse.class)))
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<List<AccountResponse>> getAllAccounts();

	    @Operation(
	        summary = "ID ile hesap siler",
	        description = "Belirtilen ID'ye sahip hesabı siler.",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Hesap başarıyla silindi"),
	            @ApiResponse(responseCode = "404", description = "Hesap bulunamadı")
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<Void> deleteAccount(
	        @Parameter(description = "Silinecek hesap ID'si", required = true, example = "101")
	        Long id
	    );
	    
}
