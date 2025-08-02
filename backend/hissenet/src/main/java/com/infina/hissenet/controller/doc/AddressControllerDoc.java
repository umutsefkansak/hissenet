package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.AddressCreateDto;
import com.infina.hissenet.dto.request.AddressUpdateDto;
import com.infina.hissenet.dto.response.AddressResponse;
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

@Tag(name = "Address", description = "Adres yönetimi API'si")
public interface AddressControllerDoc {

    @Operation(
            summary = "Yeni adres oluşturur",
            description = """
            Müşteri için yeni bir adres oluşturur.
            Müşterinin ilk adresi otomatik olarak birincil adres olarak işaretlenir.
            Birincil adres olarak işaretlenirse, müşterinin diğer birincil adresleri otomatik olarak kaldırılır.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Adres başarıyla oluşturuldu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Address created successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "addressType": "HOME",
                                "street": "Atatürk Caddesi No:15 Daire:3",
                                "district": "Kadıköy",
                                "city": "İstanbul",
                                "state": "İstanbul",
                                "country": "Türkiye",
                                "postalCode": "34710",
                                "isPrimary": true,
                                "customer": {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet.yilmaz@example.com",
                                  "phone": "+905551234567",
                                  "nationality": "TR",
                                  "kycVerified": true,
                                  "customerType": "INDIVIDUAL"
                                },
                                "fullAddress": "Atatürk Caddesi No:15 Daire:3, Kadıköy, İstanbul, İstanbul, Türkiye 34710"
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
                                "addressType": "Address type cannot be null",
                                "street": "Street cannot be blank",
                                "city": "City cannot be blank",
                                "customerId": "Customer ID cannot be null"
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
    ResponseEntity<com.infina.hissenet.common.ApiResponse<AddressResponse>> createAddress(
            @Parameter(description = "Oluşturulacak adres bilgileri", required = true,
                    schema = @Schema(implementation = AddressCreateDto.class,
                            example = """
                {
                  "addressType": "HOME",
                  "street": "Atatürk Caddesi No:15 Daire:3",
                  "district": "Kadıköy",
                  "city": "İstanbul",
                  "state": "İstanbul",
                  "country": "Türkiye",
                  "postalCode": "34710",
                  "isPrimary": true,
                  "customerId": 1
                }
                """
                    )
            )
            AddressCreateDto dto
    );

    @Operation(
            summary = "ID ile adres getirir",
            description = """
            Belirtilen ID'ye sahip adresin detaylarını getirir.
            Adres bilgileriyle birlikte müşteri bilgileri de döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Adres başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Address retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "addressType": "WORK",
                                "street": "Maslak Mahallesi Büyükdere Caddesi No:255",
                                "district": "Sarıyer",
                                "city": "İstanbul",
                                "state": "İstanbul",
                                "country": "Türkiye",
                                "postalCode": "34485",
                                "isPrimary": false,
                                "customer": {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet.yilmaz@example.com",
                                  "phone": "+905551234567",
                                  "nationality": "TR",
                                  "kycVerified": true,
                                  "customerType": "INDIVIDUAL"
                                },
                                "fullAddress": "Maslak Mahallesi Büyükdere Caddesi No:255, Sarıyer, İstanbul, İstanbul, Türkiye 34485"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Adres bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Address not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<AddressResponse> getAddressById(
            @Parameter(description = "Getirilecek adresin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Tüm adresleri getirir",
            description = """
            Sistemdeki tüm adresleri liste halinde getirir.
            Tüm müşterilerin tüm adresleri dahildir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tüm adresler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "All addresses retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "addressType": "HOME",
                                  "street": "Atatürk Caddesi No:15 Daire:3",
                                  "district": "Kadıköy",
                                  "city": "İstanbul",
                                  "state": "İstanbul",
                                  "country": "Türkiye",
                                  "postalCode": "34710",
                                  "isPrimary": true,
                                  "fullAddress": "Atatürk Caddesi No:15 Daire:3, Kadıköy, İstanbul, İstanbul, Türkiye 34710"
                                },
                                {
                                  "id": 2,
                                  "addressType": "WORK",
                                  "street": "Levent Mahallesi Büyükdere Caddesi No:201",
                                  "district": "Beşiktaş",
                                  "city": "İstanbul",
                                  "state": "İstanbul",
                                  "country": "Türkiye",
                                  "postalCode": "34330",
                                  "isPrimary": false,
                                  "fullAddress": "Levent Mahallesi Büyükdere Caddesi No:201, Beşiktaş, İstanbul, İstanbul, Türkiye 34330"
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
    com.infina.hissenet.common.ApiResponse<List<AddressResponse>> getAllAddresses();

    @Operation(
            summary = "Sayfalı adres listesi getirir",
            description = """
            Sistemdeki adresleri sayfalı şekilde getirir.
            Sayfa numarası, sayfa boyutu ve sıralama parametreleri kullanılabilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sayfalı adresler başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Paged addresses retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "content": [
                                  {
                                    "id": 1,
                                    "addressType": "HOME",
                                    "street": "Atatürk Caddesi No:15 Daire:3",
                                    "district": "Kadıköy",
                                    "city": "İstanbul",
                                    "state": "İstanbul",
                                    "country": "Türkiye",
                                    "postalCode": "34710",
                                    "isPrimary": true,
                                    "fullAddress": "Atatürk Caddesi No:15 Daire:3, Kadıköy, İstanbul, İstanbul, Türkiye 34710"
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
    com.infina.hissenet.common.ApiResponse<Page<AddressResponse>> getAllAddressesPaged(
            @Parameter(description = "Sayfalama bilgileri (page, size, sort)",
                    example = "?page=0&size=10&sort=id,asc")
            Pageable pageable
    );

    @Operation(
            summary = "Müşteriye ait adresleri getirir",
            description = """
            Belirtilen müşteri ID'sine ait tüm adresleri liste halinde getirir.
            Müşterinin ev, iş ve diğer adres tiplerindeki tüm adresleri döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Müşteri adresleri başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Customer addresses retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": [
                                {
                                  "id": 1,
                                  "addressType": "HOME",
                                  "street": "Atatürk Caddesi No:15 Daire:3",
                                  "district": "Kadıköy",
                                  "city": "İstanbul",
                                  "state": "İstanbul",
                                  "country": "Türkiye",
                                  "postalCode": "34710",
                                  "isPrimary": true,
                                  "customer": {
                                    "id": 1,
                                    "customerNumber": "IND-A1B2C3D4E5F6",
                                    "email": "ahmet.yilmaz@example.com",
                                    "customerType": "INDIVIDUAL"
                                  },
                                  "fullAddress": "Atatürk Caddesi No:15 Daire:3, Kadıköy, İstanbul, İstanbul, Türkiye 34710"
                                },
                                {
                                  "id": 2,
                                  "addressType": "WORK",
                                  "street": "Maslak Mahallesi Büyükdere Caddesi No:255",
                                  "district": "Sarıyer",
                                  "city": "İstanbul",
                                  "state": "İstanbul",
                                  "country": "Türkiye",
                                  "postalCode": "34485",
                                  "isPrimary": false,
                                  "customer": {
                                    "id": 1,
                                    "customerNumber": "IND-A1B2C3D4E5F6",
                                    "email": "ahmet.yilmaz@example.com",
                                    "customerType": "INDIVIDUAL"
                                  },
                                  "fullAddress": "Maslak Mahallesi Büyükdere Caddesi No:255, Sarıyer, İstanbul, İstanbul, Türkiye 34485"
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
    com.infina.hissenet.common.ApiResponse<List<AddressResponse>> getAddressesByCustomerId(
            @Parameter(description = "Adresleri getirilecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long customerId
    );

    @Operation(
            summary = "Müşterinin birincil adresini getirir",
            description = """
            Belirtilen müşteri ID'sine ait birincil (primary) adresi getirir.
            Her müşterinin en fazla bir birincil adresi olabilir.
            Eğer birincil adres bulunamazsa boş mesaj döner.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Birincil adres bulundu",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Primary address retrieved successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "addressType": "HOME",
                                "street": "Atatürk Caddesi No:15 Daire:3",
                                "district": "Kadıköy",
                                "city": "İstanbul",
                                "state": "İstanbul",
                                "country": "Türkiye",
                                "postalCode": "34710",
                                "isPrimary": true,
                                "customer": {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet.yilmaz@example.com",
                                  "customerType": "INDIVIDUAL"
                                },
                                "fullAddress": "Atatürk Caddesi No:15 Daire:3, Kadıköy, İstanbul, İstanbul, Türkiye 34710"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "200", description = "Birincil adres bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "No primary address found",
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
    com.infina.hissenet.common.ApiResponse<AddressResponse> getPrimaryAddressByCustomerId(
            @Parameter(description = "Birincil adresi getirilecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long customerId
    );

    @Operation(
            summary = "Adresi günceller",
            description = """
            Belirtilen ID'ye sahip adresin bilgilerini günceller.
            Birincil adres olarak işaretlenirse, müşterinin diğer birincil adresleri otomatik olarak kaldırılır.
            Müşteri değiştirilirse, yeni müşterinin varlığı kontrol edilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Adres başarıyla güncellendi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Address updated successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": {
                                "id": 1,
                                "addressType": "HOME",
                                "street": "Bağdat Caddesi No:125 Daire:5",
                                "district": "Kadıköy",
                                "city": "İstanbul",
                                "state": "İstanbul",
                                "country": "Türkiye",
                                "postalCode": "34728",
                                "isPrimary": true,
                                "customer": {
                                  "id": 1,
                                  "customerNumber": "IND-A1B2C3D4E5F6",
                                  "email": "ahmet.yilmaz@example.com",
                                  "customerType": "INDIVIDUAL"
                                },
                                "fullAddress": "Bağdat Caddesi No:125 Daire:5, Kadıköy, İstanbul, İstanbul, Türkiye 34728"
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
                                "postalCode": "Postal code must be 5 digits"
                              }
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Adres veya müşteri bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Address not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<AddressResponse> updateAddress(
            @Parameter(description = "Güncellenecek adresin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id,
            @Parameter(description = "Güncelleme bilgileri", required = true,
                    schema = @Schema(implementation = AddressUpdateDto.class,
                            example = """
                {
                  "addressType": "HOME",
                  "street": "Bağdat Caddesi No:125 Daire:5",
                  "district": "Kadıköy",
                  "city": "İstanbul",
                  "state": "İstanbul",
                  "country": "Türkiye",
                  "postalCode": "34728",
                  "isPrimary": true,
                  "customerId": 1
                }
                """
                    )
            )
            AddressUpdateDto dto
    );

    @Operation(
            summary = "Adresi siler",
            description = """
            Belirtilen ID'ye sahip adresi sistemden tamamen siler.
            Bu işlem geri alınamaz.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Adres başarıyla silindi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "Address deleted successfully",
                              "localDateTime": "2025-08-02T14:30:15.123",
                              "data": null
                            }
                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Adres bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "type": "https://www.hissenet.com/errors/not-found",
                              "title": "Resource Not Found",
                              "status": 404,
                              "detail": "Address not found with id: 999",
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
    com.infina.hissenet.common.ApiResponse<Void> deleteAddress(
            @Parameter(description = "Silinecek adresin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long id
    );

    @Operation(
            summary = "Müşteriye ait tüm adresleri siler",
            description = """
            Belirtilen müşteri ID'sine ait tüm adresleri sistemden siler.
            Bu işlem geri alınamaz ve müşterinin tüm adresleri silinir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Müşteri adresleri başarıyla silindi",
                            content = @Content(schema = @Schema(
                                    example = """
                            {
                              "status": 200,
                              "path": null,
                              "message": "All customer addresses deleted successfully",
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
    com.infina.hissenet.common.ApiResponse<Void> deleteAllAddressesByCustomerId(
            @Parameter(description = "Adresleri silinecek müşterinin ID'si", required = true,
                    in = ParameterIn.PATH, example = "1")
            Long customerId
    );
}