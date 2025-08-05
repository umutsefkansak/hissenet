package com.infina.hissenet.service;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.client.InfinaApiClient;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.ICombinedCacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class CombinedCacheService implements ICombinedCacheService {

    public static final String CACHE_NAME = "combinedStock";
    private static final String CACHE_KEY  = "'ALL'";

    private final CollectApiClient stockClient;
    private final InfinaApiClient infinaClient;

    public CombinedCacheService(CollectApiClient stockClient,
                                InfinaApiClient infinaClient) {
        this.stockClient  = stockClient;
        this.infinaClient = infinaClient;
    }

    /**
     * İlk çağrıda:
     *  1) Collect API’den tüm hisseleri çek
     *  2) Her hisse kodu için Infina API’den fiyat al
     *  3) CombinedStockData listesini oluştur ve cache’e yaz
     *
     * Sonraki çağrılarda doğrudan cache’den döner.
     */
    @Cacheable(cacheNames = CACHE_NAME, key = CACHE_KEY)
    public List<CombinedStockData> getAllCombined() {

        // 1) Collect API’den hisse listesi
        StockApiResponse stockResp = stockClient.fetchStocks()
                .onErrorReturn(new StockApiResponse(false, Collections.emptyList()))
                .block();
        var stocks = stockResp != null
                ? stockResp.result()
                : Collections.<StockData>emptyList();

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        // 2) Her bir StockData için Infina’dan fiyat al ve dto oluştur
        return stocks.stream()
                .map(s -> {
                    String code = s.code().toUpperCase();
                    String assetCode = code + ".E";

                    HisseApiResponse infinResp = infinaClient
                            .fetchPriceByCodeAndDate(assetCode, today)
                            .onErrorReturn(new HisseApiResponse(null))
                            .block();
                    HisseFiyatEntry entry = (infinResp != null && infinResp.result() != null)
                            ? infinResp.result().data().HisseFiyat().stream().findFirst().orElse(null)
                            : null;

                    return new CombinedStockData(
                            code,
                            entry != null ? entry.closePrice() : null,
                            entry != null ? entry.openPrice()  : null,
                            s.rate(),
                            s.lastprice(),
                            s.hacim(),
                            s.hacimstr(),
                            s.min(),
                            s.max(),
                            s.time(),
                            s.text(),
                            s.icon()
                    );
                })
                .toList();
    }

}
