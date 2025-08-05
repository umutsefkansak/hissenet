package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.CombinedStockData;

import java.util.List;

public interface ICombinedCacheService {
    /**
     * Tüm birleşik hisse verilerini cache'den getirir veya bir kez fetch edip cache'ler.
     * @return List<CombinedStockData>
     */
    List<CombinedStockData> getAllCombined();

}
