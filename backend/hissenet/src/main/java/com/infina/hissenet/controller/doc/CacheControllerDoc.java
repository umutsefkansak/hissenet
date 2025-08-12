package com.infina.hissenet.controller.doc;

import com.infina.hissenet.dto.response.CombinedStockData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Cache", description = "Önbelleğe alınmış birleşik hisse verileri")
public interface CacheControllerDoc {

    @Operation(
            summary = "Tek hisse için birleşik veriyi getir",
            description = "Önbellekte mevcutsa, verilen hisse kodu için CombinedStockData döner.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Kayıt bulundu",
                            content = @Content(schema = @Schema(implementation = CombinedStockData.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Kayıt bulunamadı")
            }
    )
    ResponseEntity<CombinedStockData> getOne(
            @Parameter(
                    description = "Hisse kodu",
                    required = true,
                    example = "THYAO"
            )
            String code
    );

    @Operation(
            summary = "Tüm cache'lenmiş birleşik verileri getir",
            description = "Önbellekte bulunan bütün CombinedStockData kayıtlarını döner (boş liste olabilir).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Liste döndü",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CombinedStockData.class)))
                    )
            }
    )
    ResponseEntity<List<CombinedStockData>> getAll();
}
