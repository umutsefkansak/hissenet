package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.UpdateWalletRequest;
import com.infina.hissenet.dto.response.WalletResponse;
import com.infina.hissenet.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@Tag(name = "Wallets", description = "Cüzdan yönetimi API'si")
public interface WalletControllerDoc {

    @Operation(
            summary = "Yeni cüzdan oluşturur",
            description = """
            Müşteri için yeni bir cüzdan oluşturur.
            Cüzdan, müşterinin finansal işlemlerini yönetmek için kullanılır.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cüzdan başarıyla oluşturuldu",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Geçersiz parametre"),
                    @ApiResponse(responseCode = "500", description = "Sunucu hatası")
            }
    )
    ResponseEntity<com.infina.hissenet.common.ApiResponse<WalletResponse>> createWallet(
            @Parameter(description = "Oluşturulacak cüzdan bilgileri", required = true,
                    schema = @Schema(
                            implementation = CreateWalletRequest.class,
                            example = "{\n" +
                                    "  \"customerId\": 101,\n" +
                                    "  \"currency\": \"TRY\",\n" +
                                    "  \"dailyLimit\": 10000.00,\n" +
                                    "  \"monthlyLimit\": 100000.00\n" +
                                    "}"
                    )
            )
            CreateWalletRequest request
    );

    @Operation(
            summary = "Müşteri ID'sine göre cüzdan getirir",
            description = "Belirtilen müşteri ID'sine ait cüzdan bilgilerini getirir.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cüzdan başarıyla getirildi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> getWalletByCustomerId(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId
    );

    @Operation(
            summary = "Cüzdan bakiyesini getirir",
            description = "Belirtilen müşteri ID'sine ait cüzdanın güncel bakiyesini getirir.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bakiye başarıyla getirildi",
                            content = @Content(schema = @Schema(implementation = BigDecimal.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<BigDecimal> getWalletBalance(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId
    );

    @Operation(
            summary = "Cüzdan limitlerini günceller",
            description = "Belirtilen müşteri ID'sine ait cüzdanın günlük ve aylık limitlerini günceller.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Limitler başarıyla güncellendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Geçersiz limit değerleri")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> updateWalletLimits(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Güncellenecek limit bilgileri", required = true,
                    schema = @Schema(
                            implementation = UpdateWalletRequest.class,
                            example = "{\n" +
                                    "  \"dailyLimit\": 15000.00,\n" +
                                    "  \"monthlyLimit\": 150000.00\n" +
                                    "}"
                    )
            )
            UpdateWalletRequest request
    );

    @Operation(
            summary = "Cüzdana bakiye ekler",
            description = "Belirtilen müşteri ID'sine ait cüzdana belirtilen tutarda bakiye ekler.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bakiye başarıyla eklendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Geçersiz tutar")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> addBalance(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Eklenecek tutar", required = true, example = "1000.00")
            BigDecimal amount,
            @Parameter(description = "İşlem tipi", required = true, example = "DEPOSIT")
            TransactionType transactionType
    );

    @Operation(
            summary = "Cüzdandan bakiye çıkarır",
            description = "Belirtilen müşteri ID'sine ait cüzdandan belirtilen tutarda bakiye çıkarır.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bakiye başarıyla çıkarıldı",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Yetersiz bakiye veya geçersiz tutar")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> subtractBalance(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Çıkarılacak tutar", required = true, example = "500.00")
            BigDecimal amount,
            @Parameter(description = "İşlem tipi", required = true, example = "WITHDRAWAL")
            TransactionType transactionType
    );

    @Operation(
            summary = "Hisse senedi alım işlemini işler",
            description = "Hisse senedi alım işlemi için gerekli bakiye düzenlemelerini yapar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hisse alım işlemi başarıyla işlendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Yetersiz bakiye")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> processStockPurchase(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Toplam tutar", required = true, example = "2500.00")
            BigDecimal totalAmount,
            @Parameter(description = "Komisyon tutarı", required = true, example = "25.00")
            BigDecimal commission,
            @Parameter(description = "Vergi tutarı", required = true, example = "15.00")
            BigDecimal tax
    );

    @Operation(
            summary = "Hisse senedi satış işlemini işler",
            description = "Hisse senedi satış işlemi için gerekli bakiye düzenlemelerini yapar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hisse satış işlemi başarıyla işlendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> processStockSale(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Toplam tutar", required = true, example = "2500.00")
            BigDecimal totalAmount,
            @Parameter(description = "Komisyon tutarı", required = true, example = "25.00")
            BigDecimal commission,
            @Parameter(description = "Vergi tutarı", required = true, example = "15.00")
            BigDecimal tax
    );

    @Operation(
            summary = "Para yatırma işlemini işler",
            description = "Cüzdana para yatırma işlemini gerçekleştirir.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Para yatırma işlemi başarıyla işlendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Geçersiz tutar")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> processDeposit(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Yatırılacak tutar", required = true, example = "1000.00")
            BigDecimal amount
    );

    @Operation(
            summary = "Para çekme işlemini işler",
            description = "Cüzdandan para çekme işlemini gerçekleştirir.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Para çekme işlemi başarıyla işlendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı"),
                    @ApiResponse(responseCode = "400", description = "Yetersiz bakiye")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> processWithdrawal(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId,
            @Parameter(description = "Çekilecek tutar", required = true, example = "500.00")
            BigDecimal amount
    );


    @Operation(
            summary = "Cüzdanı kilitler",
            description = "Belirtilen müşteri ID'sine ait cüzdanı kilitler. Kilitli cüzdanlarda işlem yapılamaz.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cüzdan başarıyla kilitlendi",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> lockWallet(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId
    );

    @Operation(
            summary = "Cüzdan kilidini açar",
            description = "Belirtilen müşteri ID'sine ait cüzdanın kilidini açar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cüzdan kilidi başarıyla açıldı",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> unlockWallet(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId
    );

    @Operation(
            summary = "Günlük limitleri sıfırlar",
            description = "Belirtilen müşteri ID'sine ait cüzdanın günlük kullanım limitlerini sıfırlar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Günlük limitler başarıyla sıfırlandı",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> resetDailyLimits(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId
    );

    @Operation(
            summary = "Aylık limitleri sıfırlar",
            description = "Belirtilen müşteri ID'sine ait cüzdanın aylık kullanım limitlerini sıfırlar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Aylık limitler başarıyla sıfırlandı",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<WalletResponse> resetMonthlyLimits(
            @Parameter(description = "Müşteri ID'si", required = true, example = "101")
            Long customerId
    );

    @Operation(
            summary = "Cüzdanı siler",
            description = "Belirtilen cüzdan ID'sine ait cüzdanı sistemden siler.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cüzdan başarıyla silindi"),
                    @ApiResponse(responseCode = "404", description = "Cüzdan bulunamadı")
            }
    )
    com.infina.hissenet.common.ApiResponse<String> deleteWalletById(
            @Parameter(description = "Silinecek cüzdan ID'si", required = true, example = "101")
            Long walletId
    );
}
