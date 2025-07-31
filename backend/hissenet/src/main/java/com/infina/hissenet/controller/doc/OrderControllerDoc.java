package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.request.OrderCreateRequest;
import com.infina.hissenet.dto.request.OrderUpdateRequest;
import com.infina.hissenet.dto.response.OrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
	                          "  \"stockId\": 202,\n" +
	                          "  \"category\": \"MARKET\",\n" +
	                          "  \"type\": \"BUY\",\n" +
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
	    
}
	