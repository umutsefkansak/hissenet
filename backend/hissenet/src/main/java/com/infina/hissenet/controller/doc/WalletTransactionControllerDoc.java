package com.infina.hissenet.controller.doc;


import com.infina.hissenet.dto.request.CreateWalletTransactionRequest;
import com.infina.hissenet.dto.request.UpdateWalletTransactionRequest;
import com.infina.hissenet.dto.response.WalletTransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Wallet Transactions", description = "Cüzdan işlemleri yönetimi API'si")
public interface WalletTransactionControllerDoc {

    @Operation(
            summary = "Yeni cüzdan işlemi oluşturur",
            description = """
            Yeni bir cüzdan işlemi oluşturur. İşlem PENDING durumunda başlar.
            İşlem türü DEPOSIT, WITHDRAWAL, STOCK_PURCHASE, STOCK_SALE, DIVIDEND olabilir.
            Referans numarası benzersiz olmalıdır.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cüzdan işlemi başarıyla oluşturuldu",
                            content = @Content(schema = @Schema(implementation = WalletTransactionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz parametre"),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası")
            }
    )
    ResponseEntity<com.infina.hissenet.common.ApiResponse<WalletTransactionResponse>> createWalletTransaction(
            @Parameter(description = "Oluşturulacak işlem bilgileri", required = true,
                    schema = @Schema(
                            implementation = CreateWalletTransactionRequest.class,
                            example = "{\n" +
                                    "  \"walletId\": 101,\n" +
                                    "  \"amount\": 1000.00,\n" +
                                    "  \"transactionType\": \"DEPOSIT\",\n" +
                                    "  \"feeAmount\": 5.00,\n" +
                                    "  \"source\": \"BANK_TRANSFER\",\n" +
                                    "  \"destination\": \"WALLET\"\n" +
                                    "}"
                    )
            )
            CreateWalletTransactionRequest request
    );

    @Operation(
            summary = "ID ile belirli bir işlemi getirir",
            description = "Belirtilen işlem ID'sine ait cüzdan işlemini getirir.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "İşlem başarıyla getirildi",
                            content = @Content(schema = @Schema(implementation = WalletTransactionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "İşlem bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletTransactionResponse> getTransactionById(
            @Parameter(description = "Getirilecek işlem ID'si", required = true, example = "101")
            Long transactionId
    );

    @Operation(
            summary = "Cüzdan işlem geçmişini getirir",
            description = "Belirtilen cüzdan ID'sine ait tüm işlemleri tarih sırasına göre getirir.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "İşlem geçmişi başarıyla getirildi",
                            content = @Content(schema = @Schema(implementation = WalletTransactionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<List<WalletTransactionResponse>> getTransactionHistory(
            @Parameter(description = "Cüzdan ID'si", required = true, example = "101")
            Long walletId
    );

    @Operation(
            summary = "Tüm işlemleri sayfalı olarak getirir",
            description = """
            Tüm cüzdan işlemlerini sayfalı olarak getirir.
            Sayfalama parametreleri: page (sayfa numarası), size (sayfa boyutu), sort (sıralama)
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "İşlemler başarıyla getirildi",
                            content = @Content(schema = @Schema(implementation = WalletTransactionResponse.class)))
            }
    )
    com.infina.hissenet.common.ApiResponse<Page<WalletTransactionResponse>> getAllTransactions(
            @Parameter(description = "Sayfalama parametreleri", required = false,
                    schema = @Schema(
                            example = "{\n" +
                                    "  \"page\": 0,\n" +
                                    "  \"size\": 10,\n" +
                                    "  \"sort\": \"transactionDate,desc\"\n" +
                                    "}"
                    )
            )
            org.springframework.data.domain.Pageable pageable
    );

    @Operation(
            summary = "İşlem durumunu günceller",
            description = """
            Belirtilen işlemin durumunu günceller.
            Sadece belirli durum değişiklikleri kabul edilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "İşlem başarıyla güncellendi",
                            content = @Content(schema = @Schema(implementation = WalletTransactionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "İşlem bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Geçersiz durum değişikliği")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletTransactionResponse> updateWalletTransaction(
            @Parameter(description = "Güncellenecek işlem ID'si", required = true, example = "101")
            Long transactionId,
            @Parameter(description = "Güncellenecek işlem bilgileri", required = true,
                    schema = @Schema(
                            implementation = UpdateWalletTransactionRequest.class,
                            example = "{\n" +
                                    "  \"transactionStatus\": \"COMPLETED\"\n" +
                                    "}"
                    )
            )
            UpdateWalletTransactionRequest request
    );

    @Operation(
            summary = "İşlemi tamamlar",
            description = """
            Belirtilen işlemi tamamlar ve final bakiyeyi set eder.
            İşlem durumu COMPLETED olarak güncellenir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "İşlem başarıyla tamamlandı"),
                    @ApiResponse(responseCode = "404", description = "İşlem bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "İşlem zaten tamamlanmış")
            }
    )
    com.infina.hissenet.common.ApiResponse<String> completeTransaction(
            @Parameter(description = "Tamamlanacak işlem ID'si", required = true, example = "101")
            Long transactionId
    );

    @Operation(
            summary = "İşlemi iptal eder",
            description = """
            Belirtilen işlemi iptal eder.
            İşlem durumu CANCELLED olarak güncellenir.
            Sadece PENDING durumundaki işlemler iptal edilebilir.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "İşlem başarıyla iptal edildi"),
                    @ApiResponse(responseCode = "404", description = "İşlem bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "İşlem zaten tamamlanmış veya iptal edilmiş")
            }
    )
    com.infina.hissenet.common.ApiResponse<String> cancelTransaction(
            @Parameter(description = "İptal edilecek işlem ID'si", required = true, example = "101")
            Long transactionId
    );

}
