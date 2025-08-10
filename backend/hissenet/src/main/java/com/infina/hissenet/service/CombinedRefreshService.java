package com.infina.hissenet.service;

import com.infina.hissenet.client.CollectApiClient;
import com.infina.hissenet.client.InfinaApiClient;
import com.infina.hissenet.dto.response.CombinedStockData;
import com.infina.hissenet.dto.response.HisseApiResponse;
import com.infina.hissenet.dto.response.HisseFiyatEntry;
import com.infina.hissenet.dto.response.StockData;
import com.infina.hissenet.properties.FetchProperties;
import com.infina.hissenet.service.abstracts.ICacheRefreshService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CombinedRefreshService implements ICacheRefreshService<CombinedStockData> {

    private final CollectApiClient collect;
    private final InfinaApiClient infina;
    private final FetchProperties fetchProps;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public CombinedRefreshService(CollectApiClient collect, InfinaApiClient infina, FetchProperties fetchProps) {
        this.collect = collect;
        this.infina = infina;
        this.fetchProps = fetchProps;
    }

    @Override
    public Mono<List<CombinedStockData>> buildSnapshot() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ISO_DATE);
        String yesterdayStr = today.minusDays(1).format(DateTimeFormatter.ISO_DATE);

        return collect.fetchStocks()
                .map(resp -> resp != null && resp.result() != null ? resp.result() : Collections.<StockData>emptyList())
                .doOnNext(list -> {
                    System.out.println("[CombinedRefresh] Collect listesi alındı → adet=" + list.size());
                    list.stream().limit(5).forEach(s ->
                            System.out.println("  - " + s.code() + " last=" + s.lastprice() + " rate=" + s.rate()));
                })
                .flatMapMany(Flux::fromIterable)
                .flatMap(s -> buildCombinedSafe(s, todayStr, yesterdayStr), fetchProps.getConcurrency())
                .collectList()
                .doOnNext(list -> {
                    System.out.println("[CombinedRefresh] Combine sonucu → adet=" + list.size());
                    list.stream().limit(5).forEach(cs ->
                            System.out.println("  - " + cs.code() + " last=" + cs.lastPrice()
                                    + " open=" + cs.openPrice() + " change=" + cs.changePrice()));
                })
                .retry(fetchProps.getRetry())
                .onErrorReturn(Collections.emptyList());
    }

    private Mono<CombinedStockData> buildCombinedSafe(StockData s, String todayStr, String yesterdayStr) {
        String code = s.code().toUpperCase();
        String assetCode = code + ".E";

        Mono<Optional<HisseFiyatEntry>> todayOpt = infina.fetchPriceByCodeAndDate(assetCode, "2025-08-08")
                .timeout(fetchProps.getRequestTimeout())
                .map(this::firstEntry)
                .map(Optional::ofNullable)
                .onErrorResume(e -> {
                    System.out.println("[CombinedRefresh] Infina BUGÜN HATA code=" + code + " : " + e.getMessage());
                    return Mono.just(Optional.empty());
                });

        Mono<Optional<HisseFiyatEntry>> yesterdayOpt = infina.fetchPriceByCodeAndDate(assetCode, "2025-08-07")
                .timeout(fetchProps.getRequestTimeout())
                .map(this::firstEntry)
                .map(Optional::ofNullable)
                .onErrorResume(e -> {
                    System.out.println("[CombinedRefresh] Infina DÜN HATA code=" + code + " : " + e.getMessage());
                    return Mono.just(Optional.empty());
                });

        return Mono.zipDelayError(todayOpt, yesterdayOpt)
                .map(tuple -> {
                    HisseFiyatEntry today = tuple.getT1().orElse(null);
                    HisseFiyatEntry yest = tuple.getT2().orElse(null);
                    return toCombined(code, s, today, yest);
                })
                .onErrorResume(e -> {
                    System.out.println("[CombinedRefresh] zip hata, Collect-only fallback code=" + code + " : " + e.getMessage());
                    return Mono.just(toCombined(code, s, null, null));
                });
    }

    private HisseFiyatEntry firstEntry(HisseApiResponse resp) {
        if (resp == null || resp.result() == null || resp.result().data() == null) return null;
        var list = resp.result().data().HisseFiyat();
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    private CombinedStockData toCombined(String code, StockData s, HisseFiyatEntry today, HisseFiyatEntry yesterday) {
        BigDecimal closePrice = today != null ? today.closePrice() : null;
        BigDecimal openPrice =
                (today != null && today.openPrice() != null) ? today.openPrice()
                        : (yesterday != null ? yesterday.closePrice() : null);
        BigDecimal previousClosePrice = yesterday != null ? yesterday.closePrice() : null;
        BigDecimal lastPrice = s.lastprice();
        BigDecimal ratePct = s.rate();
        BigDecimal changePrice = computeChange(lastPrice, openPrice, ratePct);

        return new CombinedStockData(
                code,
                closePrice,
                openPrice,
                changePrice,
                previousClosePrice,
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
    }

    private BigDecimal computeChange(BigDecimal last, BigDecimal open, BigDecimal ratePct) {
        try {
            // 1) En doğru: last - open
            if (last != null && open != null) {
                return last.subtract(open).setScale(2, RoundingMode.HALF_UP);
            }
            // 2) open × (rate/100)
            if (open != null && ratePct != null) {
                return open.multiply(ratePct)
                        .divide(HUNDRED, 2, RoundingMode.HALF_UP);
            }
            // 3) open'ı rate'ten tahmin et: open ≈ last / (1 + rate/100)
            if (last != null && ratePct != null) {
                BigDecimal denom = BigDecimal.ONE.add(
                        ratePct.divide(HUNDRED, 8, RoundingMode.HALF_UP)
                );
                if (denom.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal openEst = last.divide(denom, 8, RoundingMode.HALF_UP);
                    return last.subtract(openEst).setScale(2, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }
}
