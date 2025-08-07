package com.infina.hissenet.service;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.client.InfinaApiClient;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.ICombinedCacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CombinedCacheService implements ICombinedCacheService {

    public static final String CACHE_NAME = "combinedStock";
    private static final String CACHE_KEY = "'ALL'";

    private final CollectApiClient stockClient;
    private final InfinaApiClient infinaClient;

    public CombinedCacheService(CollectApiClient stockClient,
                                InfinaApiClient infinaClient) {
        this.stockClient = stockClient;
        this.infinaClient = infinaClient;
    }

    /**
     * İlk çağrıda:
     * 1) Collect API’den tüm hisseleri çek
     * 2) Her hisse kodu için Infina API’den fiyat al
     * 3) CombinedStockData listesini oluştur ve cache’e yaz
     * <p>
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

        LocalDate todayDate = LocalDate.now();
        String todayStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String yesterdayStr = todayDate.minusDays(1).format(DateTimeFormatter.ISO_DATE);


        // 2) Her bir StockData için Infina’dan fiyat al ve dto oluştur
        return stocks.stream()
                .map(s -> buildCombined(s, todayStr, yesterdayStr))
                .toList();
    }

    private CombinedStockData buildCombined(StockData s, String todayStr, String yesterdayStr) {
        String code      = s.code().toUpperCase();
        String assetCode = code + ".E";

        // önce bugün ara, yoksa dünün kapanışını openPrice olarak al
        HisseFiyatEntry todayEntry     = fetchEntry(assetCode, todayStr);
        BigDecimal   closePrice        = Optional.ofNullable(todayEntry).map(HisseFiyatEntry::closePrice).orElse(null);
        BigDecimal   openPrice         = Optional.ofNullable(todayEntry).map(HisseFiyatEntry::openPrice)
                .orElseGet(() -> Optional.ofNullable(fetchEntry(assetCode, yesterdayStr))
                        .map(HisseFiyatEntry::closePrice)
                        .orElse(null));

        BigDecimal lastPrice = s.lastprice();
        BigDecimal changePrice = null;
//        BigDecimal changePrice = (openPrice != null && lastPrice != null)
//                ? lastPrice.subtract(openPrice).setScale(2, RoundingMode.HALF_UP)
//                : null;
        if (openPrice != null & s.rate() != null) {
             changePrice = openPrice
                    .multiply(s.rate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }


        return new CombinedStockData(
                code,
                closePrice,
                openPrice,
                changePrice,
                s.rate(),
                lastPrice,
                s.hacim(),
                s.hacimstr(),
                s.min(),
                s.max(),
                s.time(),
                s.text(),
                s.icon()
        );
    }

    private HisseFiyatEntry fetchEntry(String assetCode, String date) {
        try {
            HisseApiResponse resp = infinaClient
                    .fetchPriceByCodeAndDate(assetCode, date)
                    .onErrorReturn(new HisseApiResponse(null))
                    .block();
            return (resp != null && resp.result() != null)
                    ? resp.result().data().HisseFiyat().stream().findFirst().orElse(null)
                    : null;
        } catch (Exception ex) {
            return null;
        }
    }

}
