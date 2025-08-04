package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.StockTransactionCreateRequest;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Stock Transaction Management", description = "Hisse işlemleri yönetimi API'si")
public interface StockTransactionControllerDoc {

    @Operation(
        summary = "Order emri üzerine hisse işlemi oluşturur",
        description = """
            Mevcut bir order emri üzerine hisse işlemi oluşturur.
            
            İşlem türleri:
            - BUY: Alım işlemi
            - SELL: Satım işlemi
            
            İşlem durumları:
            - PENDING: Beklemede
            - COMPLETED: Tamamlandı
            - FAILED: Başarısız
            - CANCELLED: İptal edildi
            
            Market emir türleri:
            - MARKET: Piyasa emri
            - LIMIT: Limit emri
            - STOP_LOSS: Zarar durdurma emri
            - STOP_LIMIT: Zarar durdurma limit emri
            - TRAILING_STOP: İzleyen zarar durdurma
            - ICEBERG: Buzdağı emri
            - TWAP: Time Weighted Average Price
            - VWAP: Volume Weighted Average Price
            """,
        responses = {
            @ApiResponse(responseCode = "201", description = "Hisse işlemi başarıyla oluşturuldu",
                content = @Content(schema = @Schema(implementation = StockTransactionResponse.class,
                    example = """
                    {
                      "status": 201,
                      "path": null,
                      "message": "Transaction created from order successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "id": 123,
                        "portfolioId": 456,
                        "portfolioName": "Uzun Vadeli Yatırım",
                        "stockId": 789,
                        "stockTicker": "THYAO",
                        "stockName": "Türk Hava Yolları",
                        "orderId": 101,
                        "transactionType": "BUY",
                        "transactionStatus": "COMPLETED",
                        "quantity": 100,
                        "price": 45.30,
                        "totalAmount": 4530.00,
                        "commission": 15.00,
                        "tax": 0.00,
                        "otherFees": 0.00,
                        "marketOrderType": "MARKET",
                        "limitPrice": null,
                        "executionPrice": 45.30,
                        "transactionDate": "2025-08-03T14:30:15.123",
                        "settlementDate": "2025-08-05T14:30:15.123",
                        "notes": "Piyasa emri ile alım",
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
                        "portfolioId": "Portfolio ID boş olamaz",
                        "stockId": "Stock ID boş olamaz",
                        "quantity": "Miktar pozitif olmalıdır",
                        "price": "Fiyat negatif olamaz"
                      }
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Portföy veya hisse bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Portföy veya hisse bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "422", description = "İşlem oluşturulamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/processing",
                      "title": "Processing Error",
                      "status": 422,
                      "detail": "Hisse işlemi oluşturulamadı: Yetersiz bakiye",
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
    com.infina.hissenet.common.ApiResponse<StockTransactionResponse> createTransactionFromOrder(
        @Parameter(description = "Hisse işlemi oluşturma bilgileri", required = true,
            schema = @Schema(implementation = StockTransactionCreateRequest.class,
                example = """
                {
                  "portfolioId": 456,
                  "stockId": 789,
                  "orderId": 101,
                  "transactionType": "BUY",
                  "transactionStatus": "COMPLETED",
                  "quantity": 100,
                  "price": 45.30,
                  "totalAmount": 4530.00,
                  "commission": 15.00,
                  "tax": 0.00,
                  "otherFees": 0.00,
                  "marketOrderType": "MARKET",
                  "limitPrice": null,
                  "executionPrice": 45.30,
                  "transactionDate": "2025-08-03T14:30:15.123",
                  "settlementDate": "2025-08-05T14:30:15.123",
                  "notes": "Piyasa emri ile alım"
                }
                """
            )
        )
        StockTransactionCreateRequest request
    );

    @Operation(
        summary = "Temettü işlemi oluşturur",
        description = """
            Hisse temettü ödemesi için işlem oluşturur.
            
            Temettü işlemleri:
            - İşlem türü otomatik olarak DIVIDEND olarak ayarlanır
            - Miktar temettü adet hisse sayısına göre hesaplanır
            - Fiyat temettü tutarına göre belirlenir
            - Komisyon ve vergi genellikle 0 olur
            
            Bu işlem genellikle otomatik olarak sistem tarafından oluşturulur.
            """,
        responses = {
            @ApiResponse(responseCode = "201", description = "Temettü işlemi başarıyla oluşturuldu",
                content = @Content(schema = @Schema(implementation = StockTransactionResponse.class,
                    example = """
                    {
                      "status": 201,
                      "path": null,
                      "message": "Dividend transaction created successfully",
                      "localDateTime": "2025-08-03T14:30:15.123",
                      "data": {
                        "id": 124,
                        "portfolioId": 456,
                        "portfolioName": "Uzun Vadeli Yatırım",
                        "stockId": 789,
                        "stockTicker": "THYAO",
                        "stockName": "Türk Hava Yolları",
                        "orderId": null,
                        "transactionType": "DIVIDEND",
                        "transactionStatus": "COMPLETED",
                        "quantity": 100,
                        "price": 2.50,
                        "totalAmount": 250.00,
                        "commission": 0.00,
                        "tax": 0.00,
                        "otherFees": 0.00,
                        "marketOrderType": null,
                        "limitPrice": null,
                        "executionPrice": 2.50,
                        "transactionDate": "2025-08-03T14:30:15.123",
                        "settlementDate": "2025-08-05T14:30:15.123",
                        "notes": "2024 yılı temettü ödemesi",
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
                        "portfolioId": "Portfolio ID boş olamaz",
                        "stockId": "Stock ID boş olamaz",
                        "quantity": "Miktar pozitif olmalıdır"
                      }
                    }
                    """
                ))),
            @ApiResponse(responseCode = "404", description = "Portföy veya hisse bulunamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/not-found",
                      "title": "Resource Not Found",
                      "status": 404,
                      "detail": "Portföy veya hisse bulunamadı",
                      "timestamp": "2025-08-03T14:30:15.123"
                    }
                    """
                ))),
            @ApiResponse(responseCode = "422", description = "Temettü işlemi oluşturulamadı",
                content = @Content(schema = @Schema(
                    example = """
                    {
                      "type": "https://www.hissenet.com/errors/processing",
                      "title": "Processing Error",
                      "status": 422,
                      "detail": "Temettü işlemi oluşturulamadı: Hisse pozisyonu bulunamadı",
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
    com.infina.hissenet.common.ApiResponse<StockTransactionResponse> createDividendTransaction(
        @Parameter(description = "Temettü işlemi oluşturma bilgileri", required = true,
            schema = @Schema(implementation = StockTransactionCreateRequest.class,
                example = """
                {
                  "portfolioId": 456,
                  "stockId": 789,
                  "transactionType": "DIVIDEND",
                  "transactionStatus": "COMPLETED",
                  "quantity": 100,
                  "price": 2.50,
                  "totalAmount": 250.00,
                  "commission": 0.00,
                  "tax": 0.00,
                  "otherFees": 0.00,
                  "transactionDate": "2025-08-03T14:30:15.123",
                  "settlementDate": "2025-08-05T14:30:15.123",
                  "notes": "2024 yılı temettü ödemesi"
                }
                """
            )
        )
        StockTransactionCreateRequest request
    );

    // Diğer endpoint'ler için placeholder'lar
    com.infina.hissenet.common.ApiResponse<List<StockTransactionResponse>> getTransactionsByPortfolioId(Long portfolioId);
    com.infina.hissenet.common.ApiResponse<List<StockTransactionResponse>> getTransactionsByStockId(Long stockId);
    com.infina.hissenet.common.ApiResponse<List<StockTransactionResponse>> getTransactionsByOrderId(Long orderId);
    com.infina.hissenet.common.ApiResponse<List<StockTransactionResponse>> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end);
    com.infina.hissenet.common.ApiResponse<List<StockTransactionResponse>> getTransactionsByType(String transactionType);
} 