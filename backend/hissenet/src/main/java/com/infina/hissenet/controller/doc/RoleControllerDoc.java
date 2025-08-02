package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.RoleCreateDto;
import com.infina.hissenet.dto.request.RoleUpdateDto;
import com.infina.hissenet.dto.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Role", description = "Rol yönetimi API'si")
public interface RoleControllerDoc {

    @Operation(
            summary = "Yeni rol oluşturur",
            description = """
            Sistem için yeni bir rol oluşturur.
            Rol adı, açıklama ve aktiflik durumu belirtilebilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Rol başarıyla oluşturuldu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role created successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 5,
                                "name": "MANAGER",
                                "description": "Yönetici rolü - departman yönetimi yetkilerine sahip",
                                "isActive": true,
                                "createdAt": "2025-08-02T14:30:15.123",
                                "updatedAt": "2025-08-02T14:30:15.123",
                                "employees": []
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
                              "timestamp": "2025-08-02T14:30:15.123",
                              "errors": {
                                "name": "Role name cannot be blank"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Rol adı zaten mevcut",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/conflict",
                              "title": "Conflict Error",
                              "status": 409,
                              "detail": "Role already exists with name: ADMIN",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    ResponseEntity<com.infina.hissenet.common.ApiResponse<RoleResponse>> createRole(
            @Parameter(description = "Oluşturulacak rol bilgileri", required = true,
                    schema = @Schema(implementation = RoleCreateDto.class,
                            example = """
                {
                  "name": "MANAGER",
                  "description": "Yönetici rolü - departman yönetimi yetkilerine sahip",
                  "isActive": true
                }
                """
                    )
            )
            RoleCreateDto dto
    );

    @Operation(
            summary = "ID ile rol getirir",
            description = """
            Belirtilen ID'ye sahip rolün detaylarını getirir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "name": "ADMIN",
                                "description": "Sistem yöneticisi rolü - tüm yetkilere sahip",
                                "isActive": true,
                                "createdAt": "2025-08-01T10:00:00.000",
                                "updatedAt": "2025-08-02T14:30:15.123",
                                "employees": []
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Role not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<RoleResponse> getRoleById(
            @Parameter(description = "Getirilecek rolün ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "İsim ile rol getirir",
            description = """
            Belirtilen isme sahip rolün detaylarını getirir.
            Eğer rol bulunamazsa boş mesaj döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol bulundu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "name": "ADMIN",
                                "description": "Sistem yöneticisi rolü",
                                "isActive": true,
                                "createdAt": "2025-08-01T10:00:00.000",
                                "updatedAt": "2025-08-02T14:30:15.123",
                                "employees": []
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Rol bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "No role found with name: NONEXISTENT",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": null
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<RoleResponse> getRoleByName(
            @Parameter(description = "Getirilecek rolün adı", required = true,
                    in = ParameterIn.PATH, example = "ADMIN")
            String name
    );

    @Operation(
            summary = "Tüm rolleri getirir",
            description = """
            Sistemdeki tüm rolleri liste halinde getirir.
            Aktif ve pasif tüm roller dahildir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tüm roller başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "All roles retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "name": "ADMIN",
                                  "description": "Sistem yöneticisi rolü",
                                  "isActive": true,
                                  "createdAt": "2025-08-01T10:00:00.000",
                                  "updatedAt": "2025-08-02T14:30:15.123",
                                  "employees": []
                                },
                                {
                                  "id": 2,
                                  "name": "USER",
                                  "description": "Standart kullanıcı rolü",
                                  "isActive": true,
                                  "createdAt": "2025-08-01T10:00:00.000",
                                  "updatedAt": "2025-08-02T14:30:15.123",
                                  "employees": []
                                }
                              ]
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<List<RoleResponse>> getAllRoles();

    @Operation(
            summary = "Sayfalı rol listesi getirir",
            description = """
            Sistemdeki rolleri sayfalı şekilde getirir.
            Sayfa numarası, sayfa boyutu ve sıralama parametreleri kullanılabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sayfalı roller başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Paged roles retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "content": [
                                  {
                                    "id": 1,
                                    "name": "ADMIN",
                                    "description": "Sistem yöneticisi rolü",
                                    "isActive": true,
                                    "createdAt": "2025-08-01T10:00:00.000",
                                    "updatedAt": "2025-08-02T14:30:15.123",
                                    "employees": []
                                  }
                                ],
                                "pageable": {
                                  "pageNumber": 0,
                                  "pageSize": 10,
                                  "sort": {
                                    "empty": false,
                                    "sorted": true,
                                    "unsorted": false
                                  }
                                },
                                "totalElements": 5,
                                "totalPages": 1,
                                "last": true,
                                "first": true,
                                "size": 10,
                                "number": 0,
                                "numberOfElements": 5,
                                "empty": false
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Page<RoleResponse>> getAllRolesPaged(
            @Parameter(description = "Sayfalama bilgileri (page, size, sort)",
                    example = "?page=0&size=10&sort=name,asc")
            Pageable pageable
    );

    @Operation(
            summary = "Aktif rolleri getirir",
            description = """
            Sistemdeki sadece aktif durumda olan rolleri getirir.
            Bu roller şu anda kullanımda olan rollerdir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Aktif roller başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Active roles retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "name": "ADMIN",
                                  "description": "Sistem yöneticisi rolü",
                                  "isActive": true,
                                  "createdAt": "2025-08-01T10:00:00.000",
                                  "updatedAt": "2025-08-02T14:30:15.123",
                                  "employees": []
                                }
                              ]
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<List<RoleResponse>> getActiveRoles();

    @Operation(
            summary = "Pasif rolleri getirir",
            description = """
            Sistemdeki sadece pasif durumda olan rolleri getirir.
            Bu roller şu anda kullanımda olmayan rollerdir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pasif roller başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Inactive roles retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 3,
                                  "name": "GUEST",
                                  "description": "Misafir kullanıcı rolü",
                                  "isActive": false,
                                  "createdAt": "2025-08-01T10:00:00.000",
                                  "updatedAt": "2025-08-02T14:30:15.123",
                                  "employees": []
                                }
                              ]
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<List<RoleResponse>> getInactiveRoles();

    @Operation(
            summary = "Rol araması yapar",
            description = """
            Rol adına göre arama yapar. İsteğe bağlı olarak aktiflik durumu da filtrelenebilir.
            Kısmi eşleşme desteklenir (LIKE sorgusu).
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Arama işlemi başarıyla tamamlandı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Roles searched successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "name": "ADMIN",
                                  "description": "Sistem yöneticisi rolü",
                                  "isActive": true,
                                  "createdAt": "2025-08-01T10:00:00.000",
                                  "updatedAt": "2025-08-02T14:30:15.123",
                                  "employees": []
                                }
                              ]
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<List<RoleResponse>> searchRoles(
            @Parameter(description = "Aranacak rol adı (kısmi eşleşme desteklenir)", required = true,
                    example = "ADMIN")
            String name,
            @Parameter(description = "Aktiflik durumu filtresi (isteğe bağlı)",
                    example = "true")
            Boolean isActive
    );

    @Operation(
            summary = "Rolü günceller",
            description = """
            Belirtilen ID'ye sahip rolün bilgilerini günceller.
            Rol adı, açıklama ve aktiflik durumu güncellenebilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol başarıyla güncellendi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role updated successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "name": "SENIOR_ADMIN",
                                "description": "Kıdemli sistem yöneticisi rolü",
                                "isActive": true,
                                "createdAt": "2025-08-01T10:00:00.000",
                                "updatedAt": "2025-08-02T14:30:15.123",
                                "employees": []
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
                              "timestamp": "2025-08-02T14:30:15.123",
                              "errors": {
                                "name": "Role name cannot exceed 50 characters"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Role not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Rol adı zaten kullanımda",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/conflict",
                              "title": "Conflict Error",
                              "status": 409,
                              "detail": "Role already exists with name: ADMIN",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<RoleResponse> updateRole(
            @Parameter(description = "Güncellenecek rolün ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id,
            @Parameter(description = "Güncelleme bilgileri", required = true,
                    schema = @Schema(implementation = RoleUpdateDto.class,
                            example = """
                {
                  "name": "SENIOR_ADMIN",
                  "description": "Kıdemli sistem yöneticisi rolü",
                  "isActive": true
                }
                """
                    )
            )
            RoleUpdateDto dto
    );

    @Operation(
            summary = "Rolü siler",
            description = """
            Belirtilen ID'ye sahip rolü sistemden kalıcı olarak siler.
            Bu işlem geri alınamaz.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol başarıyla silindi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role deleted successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": null
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Role not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Void> deleteRole(
            @Parameter(description = "Silinecek rolün ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Rolü aktif hale getirir",
            description = """
            Belirtilen ID'ye sahip rolü aktif duruma geçirir.
            Aktif olan roller kullanıcılara atanabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol başarıyla aktif hale getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role activated successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": null
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Role not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Void> activateRole(
            @Parameter(description = "Aktif hale getirilecek rolün ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Rolü pasif hale getirir",
            description = """
            Belirtilen ID'ye sahip rolü pasif duruma geçirir.
            Pasif olan roller yeni kullanıcılara atanamaz ancak mevcut atamalar korunur.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rol başarıyla pasif hale getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role deactivated successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": null
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Rol bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Role not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Void> deactivateRole(
            @Parameter(description = "Pasif hale getirilecek rolün ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Rol ID'si varlığını kontrol eder",
            description = """
            Belirtilen ID'ye sahip bir rolün sistemde var olup olmadığını kontrol eder.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kontrol işlemi başarıyla tamamlandı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role existence checked successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": true
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Boolean> existsById(
            @Parameter(description = "Kontrol edilecek rolün ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Rol adı varlığını kontrol eder",
            description = """
            Belirtilen ada sahip bir rolün sistemde var olup olmadığını kontrol eder.
            Büyük/küçük harf duyarlıdır.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kontrol işlemi başarıyla tamamlandı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Role name existence checked successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": false
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/internal",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "Internal server error occurred",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            )))
            }
    )
    com.infina.hissenet.common.ApiResponse<Boolean> existsByName(
            @Parameter(description = "Kontrol edilecek rol adı", required = true,
                    in = ParameterIn.PATH, example = "ADMIN")
            String name
    );
}