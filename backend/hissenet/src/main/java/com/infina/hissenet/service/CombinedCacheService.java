package com.infina.hissenet.service;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.client.InfinaApiClient;
import com.infina.hissenet.dto.response.*;
import com.infina.hissenet.service.abstracts.ICombinedCacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

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
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        System.out.println("today: " + today);

        // 2) Her bir StockData için Infina’dan fiyat al ve dto oluştur
        return stocks.stream()
                .map(s -> {
                    String code = s.code().toUpperCase();
                    String assetCode = code + ".E";

//                    HisseApiResponse infinResp = infinaClient
//                            .fetchPriceByCodeAndDate(assetCode, today)
//                            .onErrorReturn(new HisseApiResponse(null))
//                            .block();
//                    System.out.println("infinResp: " + infinResp);
//
//                    HisseFiyatEntry entry = (infinResp != null && infinResp.result() != null)
//                            ? infinResp.result().data().HisseFiyat().stream().findFirst().orElse(null)
//                            : null;
//
//                    // Fiyatları al
                    BigDecimal openPrice = null ;


                    String prevDate = todayDate.minusDays(1).format(DateTimeFormatter.ISO_DATE);
                    System.out.println("Fiyat boş, önceki gün sorgulanıyor: " + prevDate);

                    HisseApiResponse prevResp = infinaClient
                            .fetchPriceByCodeAndDate(assetCode, prevDate)
                            .onErrorReturn(new HisseApiResponse(null))
                            .block();
                    HisseFiyatEntry prevEntry = (prevResp != null && prevResp.result() != null)
                            ? prevResp.result().data().HisseFiyat().stream().findFirst().orElse(null)
                            : null;

                    if (prevEntry != null && prevEntry.closePrice() != null) {
                        openPrice = prevEntry.closePrice();
                        System.out.println("Önceki gün kapanışı openPrice olarak atandı: " + openPrice);
                    }

                    return new CombinedStockData(
                            code,
                            null,
                            openPrice,
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
