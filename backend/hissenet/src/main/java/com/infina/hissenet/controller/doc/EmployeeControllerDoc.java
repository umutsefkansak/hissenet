package com.infina.hissenet.controller.doc;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Employees", description = "Çalışan yönetimi API'si")
public interface EmployeeControllerDoc {

    @Operation(
        summary = "Yeni çalışan oluşturur",
        description = """
            Yeni bir çalışan oluşturur.
            Çalışanın kişisel bilgileri, iletişim ve roller dahil edilir.
            """,
        responses = {
            @ApiResponse(responseCode = "201", description = "Çalışan başarıyla oluşturuldu",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
            @ApiResponse(responseCode = "404", description = "Rol bulunamadı")
        }
    )
    ResponseEntity<com.infina.hissenet.common.ApiResponse<EmployeeResponse>> createEmployee(
        @Parameter(description = "Oluşturulacak çalışan bilgileri", required = true,
            schema = @Schema(implementation = EmployeeCreateRequest.class,
                example = """
                {
                  "firstName": "Ahmet",
                  "lastName": "Yılmaz",
                  "email": "ahmet.yilmaz@example.com",
                  "phone": "+905551234567",
                  "position": "Yazılım Mühendisi",
                  "password": "Password123",
                  "emergencyContactName": "Ayşe Yılmaz",
                  "emergencyContactPhone": "+905551234568",
                  "roleIds": [1, 2]
                }
                """
            )
        )
        EmployeeCreateRequest request
    );

    @Operation(
        summary = "Var olan çalışanı günceller",
        description = """
            Çalışan bilgilerini günceller. İsim, e-posta, telefon, pozisyon ve roller değiştirilebilir.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Çalışan başarıyla güncellendi",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
            @ApiResponse(responseCode = "404", description = "Çalışan veya rol bulunamadı")
        }
    )
    com.infina.hissenet.common.ApiResponse<EmployeeResponse> updateEmployee(
        @Parameter(description = "Güncelleme bilgileri", required = true,
            schema = @Schema(implementation = EmployeeUpdateRequest.class,
                example = """
                {
                  "id": 101,
                  "firstName": "Ahmet",
                  "lastName": "Yılmaz",
                  "email": "ahmet.yilmaz@example.com",
                  "phone": "+905551234567",
                  "position": "Kıdemli Yazılım Mühendisi",
                  "emergencyContactName": "Ayşe Yılmaz",
                  "emergencyContactPhone": "+905551234568",
                  "roleIds": [1, 3]
                }
                """
            )
        )
        EmployeeUpdateRequest request
    );

    @Operation(
        summary = "ID ile çalışan getirir",
        description = "Belirtilen ID'ye sahip çalışanı getirir.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Çalışan başarıyla getirildi",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Çalışan bulunamadı")
        }
    )
    com.infina.hissenet.common.ApiResponse<EmployeeResponse> getEmployee(
        @Parameter(description = "Getirilecek çalışan ID'si", required = true, example = "101")
        Long id
    );

    @Operation(
        summary = "Tüm çalışanları listeler",
        description = "Sistemdeki tüm çalışanları listeler.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Çalışanlar başarıyla listelendi",
                content = @Content(schema = @Schema(implementation = EmployeeResponse.class)))
        }
    )
    com.infina.hissenet.common.ApiResponse<List<EmployeeResponse>> getAllEmployees();

    @Operation(
        summary = "ID ile çalışan siler",
        description = "Belirtilen ID'ye sahip çalışanı siler.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Çalışan başarıyla silindi"),
            @ApiResponse(responseCode = "404", description = "Çalışan bulunamadı")
        }
    )
    com.infina.hissenet.common.ApiResponse<Void> deleteEmployee(
        @Parameter(description = "Silinecek çalışan ID'si", required = true, example = "101")
        Long id
    );
    
}
