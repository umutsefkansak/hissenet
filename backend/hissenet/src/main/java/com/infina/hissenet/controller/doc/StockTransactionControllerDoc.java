package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.response.StockTransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Stock Transaction Management", description = "Hisse işlemleri yönetimi API'si")
public interface StockTransactionControllerDoc {

    @Operation(
            summary = "Portföydeki satın alınan hisseleri listeler",
            description = """
            Belirtilen portföydeki tüm satın alınan hisse işlemlerini getirir.
            
            Filtreleme kriterleri:
            - Sadece BUY (alım) işlemleri
            - Sadece SETTLED (takas edilmiş) işlemler
            - Aynı hisse koduna sahip işlemler birleştirilir
            
            Dönen bilgiler:
            - Hisse kodu ve portföy bilgileri
            - Toplam miktar ve tutarlar
            - İşlem tarihleri ve durumları
            - Komisyon ve vergi bilgileri
            
            Bu endpoint portföy detay sayfasında hisse pozisyonlarını göstermek için kullanılır.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Satın alınan hisseler başarıyla getirildi",
                            content = @Content(schema = @Schema(implementation = StockTransactionResponse.class,
                                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "Satın Alınan Hisseler",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": [
                        {
                          "id": 123,
                          "portfolioId": 456,
                          "portfolioName": "Uzun Vadeli Yatırım",
                          "stockCode": "THYAO",
                          "orderId": 101,
                          "transactionType": "BUY",
                          "transactionStatus": "SETTLED",
                          "quantity": 500,
                          "price": 45.30,
                          "totalAmount": 22650.00,
                          "commission": 45.30,
                          "tax": 0.00,
                          "otherFees": 0.00,
                          "marketOrderType": "MARKET",
                          "limitPrice": null,
                          "executionPrice": 45.30,
                          "currentPrice": 47.50,
                          "transactionDate": "2025-08-01T10:30:00",
                          "settlementDate": "2025-08-03T10:30:00",
                          "notes": "Piyasa emri ile alım",
                          "createdAt": "2025-08-01T10:30:00",
                          "updatedAt": "2025-08-03T10:30:00"
                        },
                        {
                          "id": 124,
                          "portfolioId": 456,
                          "portfolioName": "Uzun Vadeli Yatırım",
                          "stockCode": "GARAN",
                          "orderId": 102,
                          "transactionType": "BUY",
                          "transactionStatus": "SETTLED",
                          "quantity": 200,
                          "price": 32.75,
                          "totalAmount": 6550.00,
                          "commission": 13.10,
                          "tax": 0.00,
                          "otherFees": 0.00,
                          "marketOrderType": "LIMIT",
                          "limitPrice": 32.50,
                          "executionPrice": 32.75,
                          "currentPrice": 34.20,
                          "transactionDate": "2025-08-02T14:15:00",
                          "settlementDate": "2025-08-04T14:15:00",
                          "notes": "Limit emri ile alım",
                          "createdAt": "2025-08-02T14:15:00",
                          "updatedAt": "2025-08-04T14:15:00"
                        }
                      ]
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
                    @ApiResponse(responseCode = "403", description = "Portföye erişim yetkisi yok",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/forbidden",
                      "title": "Forbidden",
                      "status": 403,
                      "detail": "Bu portföye erişim yetkiniz bulunmamaktadır",
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
    com.infina.hissenet.common.ApiResponse<List<StockTransactionResponse>> getStockTransactions(
            @Parameter(description = "Hisse işlemleri getirilecek portföy ID'si", required = true,
                    in = ParameterIn.PATH, example = "456")
            Long portfolioId
    );

    @Operation(
            summary = "Hisse işlemini başka portföye taşır",
            description = """
            Belirtilen hisse işlemini başka bir portföye taşır.
            
            Taşıma kuralları:
            - Sadece aynı müşteriye ait portföyler arasında taşıma yapılabilir
            - İşlem durumu değişmez
            - Taşıma sonrası her iki portföyün değerleri güncellenir
            - İşlem geçmişi korunur
            
            Bu işlem genellikle portföy yönetimi ve reorganizasyon için kullanılır.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hisse işlemi başarıyla taşındı",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "hisse yeni pörtföye taşındı",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": null
                    }
                    """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz taşıma işlemi",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/bad-request",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Geçersiz portföy ID'si",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Yetkisiz işlem",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/forbidden",
                      "title": "Forbidden",
                      "status": 403,
                      "detail": "You are not authorized to modify this portfolio",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                            ))),
                    @ApiResponse(responseCode = "404", description = "İşlem veya portföy bulunamadı",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Stock transaction or portfolio not found",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                            ))),
                    @ApiResponse(responseCode = "422", description = "Taşıma işlemi başarısız",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/processing",
                      "title": "Processing Error",
                      "status": 422,
                      "detail": "Hisse işlemi taşınamadı: Farklı müşteri portföyü",
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
    com.infina.hissenet.common.ApiResponse<Void> updatePortfolio(
            @Parameter(description = "Taşınacak hisse işlemi ID'si", required = true,
                    in = ParameterIn.PATH, example = "123")
            Long transactionId,
            @Parameter(description = "Hedef portföy ID'si", required = true,
                    in = ParameterIn.PATH, example = "789")
            Long portfolioId
    );

    @Operation(
            summary = "Belirli bir müşterinin belirli bir hisse koduna ait toplam miktarını döner",
            description = """
            Müşteri ve hisse kodu bazında toplam hisse miktarını döndürür.
            
            Hesaplama kriterleri:
            - Sadece BUY (alım) işlemleri dahil edilir
            - Sadece SETTLED (takas edilmiş) işlemler hesaplanır
            - Tüm portföylerdeki işlemler toplanır
            - Satış işlemleri bu hesaplamaya dahil edilmez
            
            Kullanım alanları:
            - Satış emri verirken maksimum miktar kontrolü
            - Portföy pozisyon hesaplamaları
            - Risk yönetimi ve limit kontrolleri
            - Toplam hisse senedi pozisyonu görüntüleme
            
            Bu endpoint gerçek zamanlı pozisyon bilgisi sağlar ve satış işlemlerinde kritik rol oynar.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hisse miktarı başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "THYAO koduna ait hisse sayısı",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": 1500
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
                      "detail": "Customer not found with id: 123",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz parametre",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/bad-request",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Geçersiz hisse kodu formatı",
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
    com.infina.hissenet.common.ApiResponse<Integer> getQuantityForStockTransaction(
            @Parameter(description = "Müşteri ID'si", required = true,
                    in = ParameterIn.PATH, example = "123")
            Long customerId,
            @Parameter(description = "Hisse kodu (örn: THYAO, GARAN)", required = true,
                    in = ParameterIn.PATH, example = "THYAO")
            String stockCode
    );

    @Operation(
            summary = "Belirli bir müşterinin sahip olduğu toplam hisse adedi (farklı hisse sayısı)",
            description = """
            Müşterinin tüm portföylerindeki pozisyonlara bakarak, sahip olduğu farklı hisse senedi sayısını döndürür.
            
            Hesaplama kriterleri:
            - Sadece BUY (alım) ve SETTLED (takas edilmiş) işlemler baz alınır
            - Farklı hisse kodları tekil olarak sayılır (aynı koda ait birden fazla işlem 1 sayılır)
            - Tüm portföyler üzerinden hesaplanır
            
            Kullanım alanları:
            - Portföy çeşitliliği göstergesi
            - Dashboard/özet ekranları
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Toplam farklı hisse sayısı başarıyla getirildi",
                            content = @Content(schema = @Schema(
                                    example = """
                    {
                      "status": 200,
                      "path": null,
                      "message": "hisse sayısı",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": 7
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
                      "detail": "Customer not found with id: 123",
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
    com.infina.hissenet.common.ApiResponse<Integer> getStockSizeForStockTransaction(
            @Parameter(description = "Müşteri ID'si", required = true,
                    in = ParameterIn.PATH, example = "123")
            Long customerId
    );
}