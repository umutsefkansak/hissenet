package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Portfolio Management", description = "Portföy yönetimi API'si")
public interface PortfolioControllerDoc {

    @Operation(
        summary = "Yeni portföy oluşturur",
        description = """
            Belirtilen müşteri için yeni bir portföy oluşturur.
            
            Portföy özellikleri:
            - Portföy adı ve açıklaması
            - Risk profili (CONSERVATIVE, MODERATE, AGGRESSIVE, VERY_AGGRESSIVE)
            - Portföy türü (ACTIVE, PASSIVE, BALANCED, AGGRESSIVE, CONSERVATIVE, SECTOR_FOCUSED, INDEX_TRACKING, DIVIDEND_FOCUSED)
            
            Oluşturulan portföy otomatik olarak aktif durumda başlar.
            """,
        responses = {
            @ApiResponse(responseCode = "201", description = "Portföy başarıyla oluşturuldu",
                content = @Content(schema = @Schema(implementation = PortfolioResponse.class,
                    example = """
                    {
                      "status": 201,
                      "path": null,
                      "message": "Portfolio created successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "id": 123,
                        "customerId": 456,
                        "customerName": "Ahmet Yılmaz",
                        "portfolioName": "Uzun Vadeli Yatırım",
                        "description": "Emeklilik için uzun vadeli yatırım portföyü",
                        "totalValue": 0.00,
                        "totalCost": 0.00,
                        "totalProfitLoss": 0.00,
                        "profitLossPercentage": 0.00,
                        "riskProfile": "MODERATE",
                        "portfolioType": "PASSIVE",
                        "status": "ACTIVE",
                        "isActive": true,
                        "lastRebalanceDate": null,
                        "createdAt": "2025-08-03T14:30:15.123",
                        "updatedAt": "2025-08-03T14:30:15.123"
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
                      "timestamp": "2025-08-03T14:30:15.123",
                      "errors": {
                        "portfolioName": "Portföy adı 2-100 karakter arasında olmalıdır",
                        "riskProfile": "Risk profili boş olamaz",
                        "portfolioType": "Portföy türü boş olamaz"
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
                      "detail": "Müşteri bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    ResponseEntity<com.infina.hissenet.common.ApiResponse<PortfolioResponse>> createPortfolio(
        @Parameter(description = "Oluşturulacak portföy bilgileri", required = true,
            schema = @Schema(implementation = PortfolioCreateRequest.class,
                example = """
                {
                  "portfolioName": "Uzun Vadeli Yatırım",
                  "description": "Emeklilik için uzun vadeli yatırım portföyü",
                  "riskProfile": "MODERATE",
                  "portfolioType": "PASSIVE"
                }
                """
            )
        )
        PortfolioCreateRequest request,
        @Parameter(description = "Portföy oluşturulacak müşteri ID'si", required = true,
            in = ParameterIn.PATH, example = "456")
        Long customerId
    );

    @Operation(
        summary = "Portföy bilgilerini günceller",
        description = """
            Mevcut portföyün bilgilerini günceller.
            
            Güncellenebilir alanlar:
            - Portföy adı ve açıklaması
            - Risk profili
            - Portföy türü
            - Aktiflik durumu
            
            Portföy değerleri ve finansal bilgiler otomatik olarak hesaplanır.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Portföy başarıyla güncellendi",
                content = @Content(schema = @Schema(implementation = PortfolioResponse.class,
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Portfolio updated successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "id": 123,
                        "customerId": 456,
                        "customerName": "Ahmet Yılmaz",
                        "portfolioName": "Güncellenmiş Portföy",
                        "description": "Güncellenmiş açıklama",
                        "totalValue": 150000.00,
                        "totalCost": 140000.00,
                        "totalProfitLoss": 10000.00,
                        "profitLossPercentage": 7.14,
                        "riskProfile": "AGGRESSIVE",
                        "portfolioType": "ACTIVE",
                        "status": "ACTIVE",
                        "isActive": true,
                        "lastRebalanceDate": "2025-08-01T10:00:00",
                        "createdAt": "2025-01-15T10:30:00",
                        "updatedAt": "2025-08-03T14:30:15.123"
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
                      "timestamp": "2025-08-03T14:30:15.123",
                      "errors": {
                        "portfolioName": "Portföy adı 2-100 karakter arasında olmalıdır",
                        "isActive": "Aktiflik durumu boş olamaz"
                      }
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Portföy bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Portföy bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    com.infina.hissenet.common.ApiResponse<PortfolioResponse> updatePortfolio(
        @Parameter(description = "Güncellenecek portföy ID'si", required = true,
            in = ParameterIn.PATH, example = "123")
        Long id,
        @Parameter(description = "Güncelleme bilgileri", required = true,
            schema = @Schema(implementation = PortfolioUpdateRequest.class,
                example = """
                {
                  "portfolioName": "Güncellenmiş Portföy",
                  "description": "Güncellenmiş açıklama",
                  "riskProfile": "AGGRESSIVE",
                  "portfolioType": "ACTIVE",
                  "isActive": true
                }
                """
            )
        )
        PortfolioUpdateRequest request
    );

    @Operation(
        summary = "Müşterinin portföy listesini getirir",
        description = """
            Belirtilen müşterinin tüm portföylerini özet bilgileriyle listeler.
            
            Dönen bilgiler:
            - Portföy ID ve adı
            - Toplam değer ve kar/zarar
            - Risk profili ve portföy türü
            - Aktiflik durumu ve son rebalancing tarihi
            
            Aktif ve pasif portföyler dahil edilir.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Müşteri portföyleri başarıyla getirildi",
                content = @Content(schema = @Schema(implementation = PortfolioSummaryResponse.class,
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Customer portfolios retrieved successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": [
                        {
                          "id": 123,
                          "portfolioName": "Uzun Vadeli Yatırım",
                          "totalValue": 150000.00,
                          "totalProfitLoss": 10000.00,
                          "profitLossPercentage": 7.14,
                          "riskProfile": "MODERATE",
                          "portfolioType": "PASSIVE",
                          "isActive": true,
                          "lastRebalanceDate": "2025-08-01T10:00:00"
                        },
                        {
                          "id": 124,
                          "portfolioName": "Aktif Trading",
                          "totalValue": 75000.00,
                          "totalProfitLoss": 5000.00,
                          "profitLossPercentage": 7.14,
                          "riskProfile": "AGGRESSIVE",
                          "portfolioType": "ACTIVE",
                          "isActive": true,
                          "lastRebalanceDate": "2025-08-02T15:30:00"
                        }
                      ]
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
                      "detail": "Müşteri bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    com.infina.hissenet.common.ApiResponse<List<PortfolioSummaryResponse>> getPortfoliosByCustomer(
        @Parameter(description = "Portföyleri getirilecek müşteri ID'si", required = true,
            in = ParameterIn.PATH, example = "456")
        Long customerId
    );

    @Operation(
        summary = "Portföyü siler",
        description = """
            Belirtilen portföyü sistemden kalıcı olarak siler.
            
            Dikkat:
            - Bu işlem geri alınamaz
            - Portföydeki tüm hisse pozisyonları da silinir
            - İşlem geçmişi korunur
            - Sadece aktif olmayan portföyler silinebilir
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Portföy başarıyla silindi",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Portfolio deleted successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": null
                    }
                    """
                ))),
            @ApiResponse(responseCode = "400", description = "Aktif portföy silinemez",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/bad-request",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Aktif portföy silinemez. Önce portföyü pasif hale getiriniz",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Portföy bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Portföy bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    com.infina.hissenet.common.ApiResponse<Void> deletePortfolio(
        @Parameter(description = "Silinecek portföy ID'si", required = true,
            in = ParameterIn.PATH, example = "123")
        Long id
    );

    @Operation(
        summary = "Portföy değerlerini günceller",
        description = """
            Portföydeki hisse fiyatlarını güncelleyerek portföy değerlerini yeniden hesaplar.
            
            Güncellenen değerler:
            - Toplam portföy değeri
            - Toplam maliyet
            - Kar/zarar tutarı ve yüzdesi
            - Son rebalancing tarihi
            
            Bu işlem gerçek zamanlı hisse fiyatları kullanılarak yapılır.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Portföy değerleri başarıyla güncellendi",
                content = @Content(schema = @Schema(implementation = PortfolioResponse.class,
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Portfolio values updated successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "id": 123,
                        "customerId": 456,
                        "customerName": "Ahmet Yılmaz",
                        "portfolioName": "Uzun Vadeli Yatırım",
                        "description": "Emeklilik için uzun vadeli yatırım portföyü",
                        "totalValue": 152500.00,
                        "totalCost": 140000.00,
                        "totalProfitLoss": 12500.00,
                        "profitLossPercentage": 8.93,
                        "riskProfile": "MODERATE",
                        "portfolioType": "PASSIVE",
                        "status": "ACTIVE",
                        "isActive": true,
                        "lastRebalanceDate": "2025-08-03T14:30:15.123",
                        "createdAt": "2025-01-15T10:30:00",
                        "updatedAt": "2025-08-03T14:30:15.123"
                      }
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Portföy bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Portföy bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "422", description = "Hisse fiyatları güncellenemedi",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/processing",
                      "title": "Processing Error",
                      "status": 422,
                      "detail": "Hisse fiyatları güncellenirken hata oluştu",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    com.infina.hissenet.common.ApiResponse<PortfolioResponse> updatePortfolioValues(
        @Parameter(description = "Değerleri güncellenecek portföy ID'si", required = true,
            in = ParameterIn.PATH, example = "123")
        Long id
    );

    @Operation(
        summary = "Aktif portföyleri listeler",
        description = """
            Sistemdeki tüm aktif portföyleri özet bilgileriyle listeler.
            
            Filtreleme:
            - Sadece aktif (isActive=true) portföyler
            - Tüm müşterilerin portföyleri dahil
            - Son rebalancing tarihine göre sıralama
            
            Bu endpoint genellikle yönetim paneli için kullanılır.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Aktif portföyler başarıyla getirildi",
                content = @Content(schema = @Schema(implementation = PortfolioSummaryResponse.class,
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Active portfolios retrieved successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": [
                        {
                          "id": 123,
                          "portfolioName": "Uzun Vadeli Yatırım",
                          "totalValue": 150000.00,
                          "totalProfitLoss": 10000.00,
                          "profitLossPercentage": 7.14,
                          "riskProfile": "MODERATE",
                          "portfolioType": "PASSIVE",
                          "isActive": true,
                          "lastRebalanceDate": "2025-08-01T10:00:00"
                        },
                        {
                          "id": 124,
                          "portfolioName": "Aktif Trading",
                          "totalValue": 75000.00,
                          "totalProfitLoss": 5000.00,
                          "profitLossPercentage": 7.14,
                          "riskProfile": "AGGRESSIVE",
                          "portfolioType": "ACTIVE",
                          "isActive": true,
                          "lastRebalanceDate": "2025-08-02T15:30:00"
                        }
                      ]
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    com.infina.hissenet.common.ApiResponse<List<PortfolioSummaryResponse>> getActivePortfolios();

    @Operation(
        summary = "Tek portföy detaylarını getirir",
        description = """
            Belirtilen portföyün tüm detaylarını getirir.
            
            Dönen bilgiler:
            - Portföy temel bilgileri
            - Müşteri bilgileri
            - Finansal değerler (toplam değer, maliyet, kar/zarar)
            - Risk profili ve portföy türü
            - Durum bilgileri ve tarihler
            
            Bu endpoint portföy detay sayfası için kullanılır.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Portföy detayları başarıyla getirildi",
                content = @Content(schema = @Schema(implementation = PortfolioResponse.class,
                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Portfolio retrieved successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "id": 123,
                        "customerId": 456,
                        "customerName": "Ahmet Yılmaz",
                        "portfolioName": "Uzun Vadeli Yatırım",
                        "description": "Emeklilik için uzun vadeli yatırım portföyü",
                        "totalValue": 150000.00,
                        "totalCost": 140000.00,
                        "totalProfitLoss": 10000.00,
                        "profitLossPercentage": 7.14,
                        "riskProfile": "MODERATE",
                        "portfolioType": "PASSIVE",
                        "status": "ACTIVE",
                        "isActive": true,
                        "lastRebalanceDate": "2025-08-01T10:00:00",
                        "createdAt": "2025-01-15T10:30:00",
                        "updatedAt": "2025-08-03T14:30:15.123"
                      }
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Portföy bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Portföy bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/internal",
                      "title": "Internal Server Error",
                      "status": 500,
                      "detail": "Internal server error occurred",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                )))
        }
    )
    com.infina.hissenet.common.ApiResponse<PortfolioResponse> getPortfolio(
        @Parameter(description = "Getirilecek portföy ID'si", required = true,
            in = ParameterIn.PATH, example = "123")
        Long id
    );
} 