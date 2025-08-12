package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.common.CustomerDto;
import com.infina.hissenet.dto.request.*;
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

@Tag(name = "Customer", description = "Müşteri yönetimi API'si")
public interface CustomerControllerDoc {

    @Operation(
            summary = "Yeni bireysel müşteri oluşturur",
            description = """
            Sistem için yeni bir bireysel müşteri oluşturur.
            Email ve TC kimlik numarası benzersiz olmalıdır.
            Müşteri oluşturulduktan sonra otomatik olarak cüzdan ve portföy oluşturulur.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Bireysel müşteri başarıyla oluşturuldu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Individual customer created successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": false,
                                "customerType": "INDIVIDUAL",
                                "firstName": "Ahmet",
                                "middleName": "Hakan",
                                "lastName": "YILMAZ",
                                "tcNumber": "12345678901",
                                "birthDate": "1990-01-15",
                                "birthPlace": "Istanbul",
                                "gender": "MALE",
                                "motherName": "Ayşe",
                                "fatherName": "Mehmet",
                                "profession": "Software Engineer",
                                "educationLevel": "University",
                                "commissionRate": 0.004
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
                                "email": "Email cannot be blank",
                                "firstName": "First name cannot be blank",
                                "lastName": "Last name cannot be blank"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Email veya TC kimlik numarası zaten mevcut",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/conflict",
                              "title": "Conflict Error",
                              "status": 409,
                              "detail": "Email already exists: ahmet@example.com",
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
    ResponseEntity<com.infina.hissenet.common.ApiResponse<CustomerDto>> createIndividualCustomer(
            @Parameter(description = "Oluşturulacak bireysel müşteri bilgileri", required = true,
                    schema = @Schema(implementation = IndividualCustomerCreateRequest.class,
                            example = """
                {
                  "email": "ahmet@example.com",
                  "phone": "+905551234567",
                  "nationality": "TR",
                  "firstName": "Ahmet",
                  "middleName": "Hakan",
                  "lastName": "YILMAZ",
                  "tcNumber": "12345678901",
                  "birthDate": "1990-01-15",
                  "birthPlace": "Istanbul",
                  "gender": "MALE",
                  "motherName": "Ayşe",
                  "fatherName": "Mehmet",
                  "profession": "Software Engineer",
                  "educationLevel": "University",
                  "commissionRate": 0.004
                }
                """
                    )
            )
            IndividualCustomerCreateRequest dto
    );

    @Operation(
            summary = "Bireysel müşteriyi günceller",
            description = """
            Belirtilen ID'ye sahip bireysel müşterinin bilgilerini günceller.
            Email ve TC kimlik numarası benzersizlik kontrolüne tabidir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bireysel müşteri başarıyla güncellendi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Individual customer updated successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet.updated@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": false,
                                "customerType": "INDIVIDUAL",
                                "firstName": "Ahmet",
                                "middleName": "Hakan",
                                "lastName": "YILMAZ",
                                "tcNumber": "12345678901",
                                "birthDate": "1990-01-15",
                                "birthPlace": "Istanbul",
                                "gender": "MALE",
                                "motherName": "Jane Doe",
                                "fatherName": "James Doe",
                                "profession": "Senior Software Engineer",
                                "educationLevel": "University",
                                "commissionRate": 0.004
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Validation hatası veya müşteri tipi uyumsuzluğu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/validation",
                              "title": "Validation Error",
                              "status": 400,
                              "detail": "Customer with id 1 is not an individual customer",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Customer not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Email veya TC kimlik numarası zaten kullanımda",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/conflict",
                              "title": "Conflict Error",
                              "status": 409,
                              "detail": "Email already exists: ahmet@example.com",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> updateIndividualCustomer(
            @Parameter(description = "Güncellenecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id,
            @Parameter(description = "Güncelleme bilgileri", required = true,
                    schema = @Schema(implementation = IndividualCustomerUpdateRequest.class))
            IndividualCustomerUpdateRequest dto
    );

    @Operation(
            summary = "Yeni kurumsal müşteri oluşturur",
            description = """
            Sistem için yeni bir kurumsal müşteri oluşturur.
            Email ve vergi numarası benzersiz olmalıdır.
            Müşteri oluşturulduktan sonra otomatik olarak cüzdan ve portföy oluşturulur.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Kurumsal müşteri başarıyla oluşturuldu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Corporate customer created successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 2,
                                "customerNumber": "COR-B2C3D4E5F6G7",
                                "email": "info@acmecorp.com",
                                "phone": "+902121234567",
                                "nationality": "TR",
                                "kycVerified": false,
                                "customerType": "CORPORATE",
                                "companyName": "ACME Corporation",
                                "taxNumber": "1234567890",
                                "tradeRegistryNumber": "TR-123456",
                                "establishmentDate": "2020-01-01",
                                "sector": "Technology",
                                "authorizedPersonName": "Ahmet Hakan YILMAZ",
                                "authorizedPersonTitle": "CEO",
                                "website": "https://www.acmecorp.com",
                                "commissionRate": 0.004
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
                                "email": "Email cannot be blank",
                                "companyName": "Company name cannot be blank"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Email veya vergi numarası zaten mevcut",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/conflict",
                              "title": "Conflict Error",
                              "status": 409,
                              "detail": "Tax Number already exists: 1234567890",
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
    ResponseEntity<com.infina.hissenet.common.ApiResponse<CustomerDto>> createCorporateCustomer(
            @Parameter(description = "Oluşturulacak kurumsal müşteri bilgileri", required = true,
                    schema = @Schema(implementation = CorporateCustomerCreateRequest.class,
                            example = """
                {
                  "email": "info@acmecorp.com",
                  "phone": "+902121234567",
                  "nationality": "TR",
                  "companyName": "ACME Corporation",
                  "taxNumber": "1234567890",
                  "tradeRegistryNumber": "TR-123456",
                  "establishmentDate": "2020-01-01",
                  "sector": "Technology",
                  "authorizedPersonName": "Ahmet Hakan YILMAZ",
                  "authorizedPersonTitle": "CEO",
                  "website": "https://www.acmecorp.com",
                  "commissionRate": 0.004
                }
                """
                    )
            )
            CorporateCustomerCreateRequest dto
    );

    @Operation(
            summary = "Kurumsal müşteriyi günceller",
            description = """
            Belirtilen ID'ye sahip kurumsal müşterinin bilgilerini günceller.
            Email ve vergi numarası benzersizlik kontrolüne tabidir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kurumsal müşteri başarıyla güncellendi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Corporate customer updated successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 2,
                                "customerNumber": "COR-B2C3D4E5F6G7",
                                "email": "info.updated@acmecorp.com",
                                "phone": "+902121234567",
                                "nationality": "TR",
                                "kycVerified": false,
                                "customerType": "CORPORATE",
                                "companyName": "ACME Corporation Ltd.",
                                "taxNumber": "1234567890",
                                "tradeRegistryNumber": "TR-123456",
                                "establishmentDate": "2020-01-01",
                                "sector": "Technology",
                                "authorizedPersonName": "Ahmet Hakan YILMAZ",
                                "authorizedPersonTitle": "CEO",
                                "website": "https://www.acmecorp.com",
                                "commissionRate": 0.004
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Validation hatası veya müşteri tipi uyumsuzluğu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/validation",
                              "title": "Validation Error",
                              "status": 400,
                              "detail": "Customer with id 2 is not a corporate customer",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Customer not found with id: 999",
                              "timestamp": "2025-08-02T14:30:15.123"
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "409", description = "Email veya vergi numarası zaten kullanımda",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/conflict",
                              "title": "Conflict Error",
                              "status": 409,
                              "detail": "Tax Number already exists: 1234567890",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> updateCorporateCustomer(
            @Parameter(description = "Güncellenecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "2")
            Long id,
            @Parameter(description = "Güncelleme bilgileri", required = true,
                    schema = @Schema(implementation = CorporateCustomerUpdateRequest.class))
            CorporateCustomerUpdateRequest dto
    );

    @Operation(
            summary = "ID ile müşteri getirir",
            description = """
            Belirtilen ID'ye sahip müşterinin detaylarını getirir.
            Bireysel veya kurumsal müşteri olabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Müşteri başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": true,
                                "customerType": "INDIVIDUAL",
                                "firstName": "Ahmet",
                                "middleName": "Hakan",
                                "lastName": "YILMAZ",
                                "tcNumber": "12345678901",
                                "birthDate": "1990-01-15",
                                "birthPlace": "Istanbul",
                                "gender": "MALE",
                                "motherName": "Ayşe",
                                "fatherName": "Mehmet",
                                "profession": "Software Engineer",
                                "educationLevel": "University",
                                "commissionRate": 0.004
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Customer not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> getCustomerById(
            @Parameter(description = "Getirilecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Tüm müşterileri getirir",
            description = """
            Sistemdeki tüm müşterileri liste halinde getirir.
            Bireysel ve kurumsal tüm müşteriler dahildir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tüm müşteriler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "All customers retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet@example.com",
                                  "phone": "+905551234567",
                                  "nationality": "TR",
                                  "kycVerified": true,
                                  "customerType": "INDIVIDUAL"
                                },
                                {
                                  "id": 2,
                                  "customerNumber": "COR-B2C3D4E5F6G7",
                                  "email": "info@acmecorp.com",
                                  "phone": "+902121234567",
                                  "nationality": "TR",
                                  "kycVerified": false,
                                  "customerType": "CORPORATE"
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
    com.infina.hissenet.common.ApiResponse<List<CustomerDto>> getAllCustomers();

    @Operation(
            summary = "Sayfalı müşteri listesi getirir",
            description = """
            Sistemdeki müşterileri sayfalı şekilde getirir.
            Sayfa numarası, sayfa boyutu ve sıralama parametreleri kullanılabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sayfalı müşteriler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Paged customers retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "content": [
                                  {
                                    "id": 1,
                                    "customerNumber": "IND-A1B2C3D4E5F6",
                                    "email": "ahmet@example.com",
                                    "phone": "+905551234567",
                                    "nationality": "TR",
                                    "kycVerified": true,
                                    "customerType": "INDIVIDUAL"
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
                                "totalElements": 25,
                                "totalPages": 3,
                                "last": false,
                                "first": true,
                                "size": 10,
                                "number": 0,
                                "numberOfElements": 10,
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
    com.infina.hissenet.common.ApiResponse<Page<CustomerDto>> getAllCustomersPaged(
            @Parameter(description = "Sayfalama bilgileri (page, size, sort)",
                    example = "?page=0&size=10&sort=id,asc")
            Pageable pageable
    );

    @Operation(
            summary = "Email ile müşteri getirir",
            description = """
            Belirtilen email adresine sahip müşterinin detaylarını getirir.
            Eğer müşteri bulunamazsa boş mesaj döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Müşteri bulundu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": true,
                                "customerType": "INDIVIDUAL",
                                
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "No customer found with email: nonexistent@example.com",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> getCustomerByEmail(
            @Parameter(description = "Aranacak email adresi", required = true,
                    in = ParameterIn.PATH, example = "ahmet@example.com")
            String email
    );

    @Operation(
            summary = "Müşteri numarası ile müşteri getirir",
            description = """
            Belirtilen müşteri numarasına sahip müşterinin detaylarını getirir.
            Eğer müşteri bulunamazsa boş mesaj döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Müşteri bulundu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": true,
                                "customerType": "INDIVIDUAL"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "No customer found with customer number: NONEXISTENT-123",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> getCustomerByCustomerNumber(
            @Parameter(description = "Aranacak müşteri numarası", required = true,
                    in = ParameterIn.PATH, example = "IND-A1B2C3D4E5F6")
            String customerNumber
    );

    @Operation(
            summary = "Bireysel müşterileri getirir",
            description = """
            Sistemdeki tüm bireysel müşterileri liste halinde getirir.
            Sadece INDIVIDUAL tipindeki müşteriler döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bireysel müşteriler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Individual customers retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet@example.com",
                                  "phone": "+905551234567",
                                  "nationality": "TR",
                                  "kycVerified": true,
                                  "customerType": "INDIVIDUAL",
                                  "firstName": "Ahmet",
                                  "middleName": "Hakan",
                                  "lastName": "YILMAZ",
                                  "tcNumber": "12345678901",
                                  "birthDate": "1990-01-15",
                                  "birthPlace": "Istanbul",
                                  "gender": "MALE",
                                  "motherName": "Ayşe",
                                  "fatherName": "Mehmet",
                                  "profession": "Software Engineer",
                                  "educationLevel": "University",
                                  "commissionRate": 0.004
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
    com.infina.hissenet.common.ApiResponse<List<CustomerDto>> getIndividualCustomers();

    @Operation(
            summary = "Kurumsal müşterileri getirir",
            description = """
            Sistemdeki tüm kurumsal müşterileri liste halinde getirir.
            Sadece CORPORATE tipindeki müşteriler döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kurumsal müşteriler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Corporate customers retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 2,
                                  "customerNumber": "COR-B2C3D4E5F6G7",
                                  "email": "info@acmecorp.com",
                                  "phone": "+902121234567",
                                  "nationality": "TR",
                                  "kycVerified": false,
                                  "customerType": "CORPORATE",
                                  "companyName": "ACME Corporation",
                                  "taxNumber": "1234567890",
                                  "tradeRegistryNumber": "TR-123456",
                                  "establishmentDate": "2020-01-01",
                                  "sector": "Technology",
                                  "authorizedPersonName": "Ahmet Hakan YILMAZ",
                                  "authorizedPersonTitle": "CEO",
                                  "website": "https://www.acmecorp.com",
                                  "commissionRate": 0.004
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
    com.infina.hissenet.common.ApiResponse<List<CustomerDto>> getCorporateCustomers();

    @Operation(
            summary = "Müşteri KYC doğrulamasını onaylar",
            description = """
            Belirtilen ID'ye sahip müşterinin KYC (Know Your Customer) doğrulamasını onaylar.
            KYC onaylandıktan sonra kycVerified true olur ve onay tarihi set edilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "KYC başarıyla doğrulandı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer KYC verified successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": true,
                                "customerType": "INDIVIDUAL",
                                "commissionRate": 0.004
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Customer not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> verifyKyc(
            @Parameter(description = "KYC doğrulanacak müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Müşteri KYC doğrulamasını iptal eder",
            description = """
            Belirtilen ID'ye sahip müşterinin KYC (Know Your Customer) doğrulamasını iptal eder.
            KYC iptal edildikten sonra kycVerified false olur ve onay tarihi null olur.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "KYC başarıyla iptal edildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer KYC unverified successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "customerNumber": "IND-A1B2C3D4E5F6",
                                "email": "ahmet@example.com",
                                "phone": "+905551234567",
                                "nationality": "TR",
                                "kycVerified": false,
                                "customerType": "INDIVIDUAL",
                                "commissionRate": 0.004
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Customer not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<CustomerDto> unverifyKyc(
            @Parameter(description = "KYC doğrulaması iptal edilecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "KYC doğrulanmış müşterileri getirir",
            description = """
            Sistemdeki KYC doğrulaması tamamlanmış tüm müşterileri liste halinde getirir.
            Sadece kycVerified = true olan müşteriler döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "KYC doğrulanmış müşteriler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "KYC verified customers retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet@example.com",
                                  "phone": "+905551234567",
                                  "nationality": "TR",
                                  "kycVerified": true,
                                  "customerType": "INDIVIDUAL",
                                  "commissionRate": 0.004
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
    com.infina.hissenet.common.ApiResponse<List<CustomerDto>> getKycVerifiedCustomers();

    @Operation(
            summary = "KYC doğrulanmamış müşterileri getirir",
            description = """
            Sistemdeki KYC doğrulaması tamamlanmamış tüm müşterileri liste halinde getirir.
            Sadece kycVerified = false olan müşteriler döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "KYC doğrulanmamış müşteriler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "KYC unverified customers retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 2,
                                  "customerNumber": "COR-B2C3D4E5F6G7",
                                  "email": "info@acmecorp.com",
                                  "phone": "+902121234567",
                                  "nationality": "TR",
                                  "kycVerified": false,
                                  "customerType": "CORPORATE",
                                  "commissionRate": 0.004
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
    com.infina.hissenet.common.ApiResponse<List<CustomerDto>> getKycUnverifiedCustomers();

    @Operation(
            summary = "Müşteriyi siler",
            description = """
            Belirtilen ID'ye sahip müşteriyi sistemden tamamen siler.
            Bu işlem geri alınamaz ve müşteriyle ilgili tüm veriler silinir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Müşteri başarıyla silindi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer deleted successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": null
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Customer not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<Void> deleteCustomer(
            @Parameter(description = "Silinecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Müşteri varlığını ID ile kontrol eder",
            description = """
            Belirtilen ID'ye sahip bir müşterinin sistemde var olup olmadığını kontrol eder.
            Sadece varlık kontrolü yapar, müşteri detaylarını getirmez.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Varlık kontrolü tamamlandı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer existence checked",
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
            @Parameter(description = "Kontrol edilecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Email varlığını kontrol eder",
            description = """
            Belirtilen email adresine sahip bir müşterinin sistemde var olup olmadığını kontrol eder.
            Email benzersizlik kontrolü için kullanılabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email varlık kontrolü tamamlandı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Email existence checked",
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
    com.infina.hissenet.common.ApiResponse<Boolean> existsByEmail(
            @Parameter(description = "Kontrol edilecek email adresi", required = true,
                    in = ParameterIn.PATH, example = "ahmet@example.com")
            String email
    );
}