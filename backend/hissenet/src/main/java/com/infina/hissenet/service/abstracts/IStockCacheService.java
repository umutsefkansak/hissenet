package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.StockData;

import java.math.BigDecimal;
import java.util.List;

public interface IStockCacheService {
    /**
     * Cache'te tutulan tüm hisse verilerini döner.
     *
     * @return StockData listesi, eğer cache boşsa boş liste döner
     */
    List<StockData> getCachedStocks();


    /**
     * Verilen hisse koduna göre fiyat bilgisini döner.
     *
     * @param code hisse kodu (örnek: "THYAO")
     * @return BigDecimal tipinde fiyat bilgisi varsa döner, yoksa boş Optional döner
     */
    BigDecimal getPriceByCodeOrNull(String code);
}
