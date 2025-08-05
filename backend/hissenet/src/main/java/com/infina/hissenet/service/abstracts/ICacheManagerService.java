package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.response.CombinedStockData;

import java.util.List;

public interface ICacheManagerService {

    /**
     * Cache’e kaydedilmiş tüm birleşik hisseleri döner.
     */
    List<CombinedStockData> getAllCached();

    /**
     * Cache’den yalnızca tek bir kodu döner.
     * @param code Hisse kodu (büyük/küçük harfe duyarsız)
     */
    CombinedStockData getCachedByCode(String code);
}
