package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;
import com.infina.hissenet.dto.response.PopularStockCodesResponse;
import com.infina.hissenet.dto.response.PortfolioStockQuantityResponse;
import com.infina.hissenet.dto.response.RecentOrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;

@Tag(name = "Orders", description = "Sipariş (emir) yönetimi API'si")
public interface OrderControllerDoc {
	
	  	@Operation(
		        summary = "Yeni emir oluşturur",
		        description = """
		            Yeni bir emir oluşturur. 
		            - MARKET emirleri anlık piyasa fiyatı üzerinden anında işlenir.
		            - LIMIT emirleri belirtilen fiyattan veya daha iyisinden gerçekleşmeyi bekler.
		            Emir tipi BUY (alış) veya SELL (satış) olabilir.
		            """,
		        responses = {
		            @ApiResponse(responseCode = "201", description = "Emir başarıyla oluşturuldu",
		                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
		            @ApiResponse(responseCode = "400", description = "Geçersiz parametre",
		                content = @Content(schema = @Schema())),
		            @ApiResponse(responseCode = "500", description = "Sunucu hatası",
		                content = @Content(schema = @Schema()))
		        }
		)
		ResponseEntity<com.infina.hissenet.common.ApiResponse<OrderResponse>> createOrder(
	        @Parameter(description = "Oluşturulacak emir bilgileri", required = true,
	            schema = @Schema(
	                implementation = OrderCreateRequest.class,
	                example = "{\n" +
	                          "  \"customerId\": 101,\n" +
	                          "  \"category\": \"MARKET\",\n" +
	                          "  \"type\": \"BUY\",\n" +
	                          "  \"stockCode\": \"THYAO\",\n" +
	                          "  \"quantity\": 100.0,\n" +
	                          "  \"price\": 25.50\n" +
	                          "}"
	            )
	        )
	        OrderCreateRequest request
	    );

	    @Operation(
	        summary = "Mevcut emri günceller",
	        description = """
	            Mevcut bir emrin durumunu günceller. 
	            Sadece belirli durum değişiklikleri kabul edilir, örn. açık emirler iptal edilebilir.
	            """,
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Emir başarıyla güncellendi",
	                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
	            @ApiResponse(responseCode = "404", description = "Emir bulunamadı"),
	            @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
	            @ApiResponse(responseCode = "500", description = "Sunucu hatası")
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<OrderResponse> updateOrder(
	        @Parameter(description = "Güncellenecek emir ID'si", required = true, in = ParameterIn.PATH, example = "101")
	        Long id,
	        @Parameter(description = "Güncellenecek emir durumu", required = true,
	            schema = @Schema(
	                implementation = OrderUpdateRequest.class,
	                example = "{\n" +
	                          "  \"status\": \"CANCELED\"\n" +
	                          "}"
	            )
	        )
	        OrderUpdateRequest request
	    );

	    @Operation(
	        summary = "ID ile belirli bir emri getirir",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Emir başarıyla getirildi",
	                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
	            @ApiResponse(responseCode = "404", description = "Emir bulunamadı")
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<OrderResponse> getOrder(
	        @Parameter(description = "Getirilecek emir ID'si", required = true, example = "101")
	        Long id
	    );

	    @Operation(
	        summary = "Tüm emirleri listeler",
	        responses = {
	            @ApiResponse(responseCode = "200", description = "Emirler başarıyla listelendi",
	                content = @Content(schema = @Schema(implementation = OrderResponse.class)))
	        }
	    )
	    com.infina.hissenet.common.ApiResponse<List<OrderResponse>> getAllOrders();
	    
	    
	    @Operation(
	    	    summary = "Belirli bir müşteriye ait tüm emirleri getirir",
	    	    responses = {
	    	        @ApiResponse(responseCode = "200", description = "Müşteriye ait emirler getirildi",
	    	            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
	    	    }
	    	)
	    	com.infina.hissenet.common.ApiResponse<List<OrderResponse>> getOrdersByCustomerId(
	    	    @Parameter(description = "Müşteri ID", required = true, example = "101")
	    	    Long customerId
	    	);

	    	@Operation(
	    	    summary = "Belirli bir hisse için müşterinin sahip olduğu net miktarı getirir",
	    	    responses = {
	    	        @ApiResponse(responseCode = "200", description = "Net miktar başarıyla hesaplandı",
	    	            content = @Content(schema = @Schema(implementation = BigDecimal.class)))
	    	    }
	    	)
	    	com.infina.hissenet.common.ApiResponse<BigDecimal> getOwnedStockQuantity(
	    	    @Parameter(description = "Müşteri ID", required = true, example = "101")
	    	    Long customerId,
	    	    @Parameter(description = "Hisse kodu", required = true, example = "THYAO")
	    	    String stockCode
	    	);

	    	@Operation(
	    	    summary = "Müşterinin sahip olduğu tüm hisseleri ve miktarlarını getirir",
	    	    description = "Sadece 'FILLED' durumundaki işlemler dikkate alınır.",
	    	    responses = {
	    	        @ApiResponse(responseCode = "200", description = "Portföy başarıyla getirildi",
	    	            content = @Content(schema = @Schema(implementation = PortfolioStockQuantityResponse.class)))
	    	    }
	    	)
	    	com.infina.hissenet.common.ApiResponse<List<PortfolioStockQuantityResponse>> getPortfolio(
	    	    @Parameter(description = "Müşteri ID", required = true, example = "101")
	    	    Long customerId
	    	);

	    	@Operation(
	    	    summary = "Son 5 FILLED emri getirir",
	    	    description = """
	    	        Tüm kullanıcılar arasından 'FILLED' durumundaki en son 5 işlem listelenir.
	    	        Her işlem için sadece hisse kodu, emir tipi (BUY/SELL) ve toplam tutar döndürülür.
	    	        """,
	    	    responses = {
	    	        @ApiResponse(responseCode = "200", description = "Son 5 işlem başarıyla getirildi",
	    	            content = @Content(schema = @Schema(implementation = RecentOrderResponse.class)))
	    	    }
	    	)
	    	com.infina.hissenet.common.ApiResponse<List<RecentOrderResponse>> getLastFiveOrders();
	    
	    	@Operation(
	    		    summary = "Tüm FILLED durumundaki emirleri getirir",
	    		    description = "Sistemdeki tüm başarıyla gerçekleşmiş (FILLED) emirleri listeler.",
	    		    responses = {
	    		        @ApiResponse(responseCode = "200", description = "Tüm FILLED emirler getirildi",
	    		            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<List<OrderResponse>> getAllFilledOrders();

	    	@Operation(
	    		    summary = "Bugün oluşturulan FILLED emirleri getirir",
	    		    description = """
	    		        Sistem tarihine göre (bugün) oluşturulmuş ve durumu 'FILLED' olan tüm emirleri listeler.
	    		        Bu endpoint, yalnızca bugünkü işlenmiş (gerçekleşmiş) işlemleri döndürür.
	    		        """,
	    		    responses = {
	    		        @ApiResponse(
	    		            responseCode = "200",
	    		            description = "Bugünkü FILLED emirler başarıyla getirildi",
	    		            content = @Content(schema = @Schema(implementation = OrderResponse.class))
	    		        )
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<List<OrderResponse>> getTodayFilledOrders();
	    	
	    	@Operation(
	    		    summary = "Bugünkü toplam işlem hacmini getirir",
	    		    description = """
	    		        Sistem tarihine göre bugün oluşturulmuş ve durumu 'FILLED' olan tüm emirlerin
	    		        toplam işlem hacmini (totalAmount) hesaplar. Hem alış hem satışlar dahildir.
	    		        """,
	    		    responses = {
	    		        @ApiResponse(
	    		            responseCode = "200",
	    		            description = "Toplam işlem hacmi başarıyla hesaplandı",
	    		            content = @Content(schema = @Schema(implementation = BigDecimal.class))
	    		        )
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<BigDecimal> getTodayTotalTradeVolume();
	    	
	    	@Operation(
	    		    summary = "En popüler 10 hisseyi getirir",
	    		    description = """
	    		        'FILLED' durumundaki işlemler baz alınarak toplam işlem hacmine göre en popüler 
	    		        (en yüksek hacimli) 10 hisse kodunu döndürür.
	    		        Hisse kodları, işlem hacmi sıralamasına göre azalan şekilde listelenir.
	    		        """,
	    		    responses = {
	    		        @ApiResponse(
	    		            responseCode = "200",
	    		            description = "Popüler hisseler başarıyla listelendi",
	    		            content = @Content(schema = @Schema(implementation = PopularStockCodesResponse.class))
	    		        )
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<List<PopularStockCodesResponse>> getPopularStockCodes();

	    	@Operation(
	    		    summary = "Toplam işlem hacmini getirir",
	    		    description = """
	    		        Sistemdeki tüm FILLED emirler (alış ve satışlar) için toplam işlem hacmini (totalAmount) hesaplar.
	    		        Bu, tüm zamanların toplam işlem hacmidir.
	    		        """,
	    		    responses = {
	    		        @ApiResponse(
	    		            responseCode = "200",
	    		            description = "Toplam işlem hacmi başarıyla hesaplandı",
	    		            content = @Content(schema = @Schema(implementation = BigDecimal.class))
	    		        )
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<BigDecimal> getTotalTradeVolume();

	    	@Operation(
	    		    summary = "Bugünkü toplam emir sayısını getirir",
	    		    description = """
	    		        Sistem tarihine göre bugün oluşturulmuş tüm emirlerin sayısını döndürür.
	    		        Emir durumu (örn. FILLED, PENDING) dikkate alınmaz; sadece tarih filtresi uygulanır.
	    		        """,
	    		    responses = {
	    		        @ApiResponse(
	    		            responseCode = "200",
	    		            description = "Bugünkü toplam emir sayısı başarıyla getirildi",
	    		            content = @Content(schema = @Schema(implementation = Long.class))
	    		        )
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<Long> getTodayOrderCount();
	    	
	    	@Operation(
	    		    summary = "Belirli bir müşteriye ait tüm emirleri (yeniden eskiye) getirir",
	    		    description = """
	    		        Verilen müşteri ID'sine ait tüm emirleri, oluşturulma tarihine göre azalan
	    		        (createdAt DESC) sıralama ile döndürür. Dönen payload, getOrdersByCustomerId ile
	    		        aynıdır; yalnızca sıralama farkı vardır.
	    		        """,
	    		    responses = {
	    		        @ApiResponse(
	    		            responseCode = "200",
	    		            description = "Müşteriye ait emirler (yeniden eskiye) başarıyla getirildi",
	    		            content = @Content(schema = @Schema(implementation = OrderResponse.class))
	    		        )
	    		    }
	    		)
	    		com.infina.hissenet.common.ApiResponse<List<OrderResponse>> getOrdersByCustomerIdSorted(
	    		    @Parameter(description = "Müşteri ID", required = true, example = "101")
	    		    Long customerId
	    		);

}
