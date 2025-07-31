package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.StockPriceCreateRequest;
import com.infina.hissenet.dto.request.StockPriceUpdateRequest;
import com.infina.hissenet.dto.response.StockPriceResponse;

import java.util.List;

public interface IStockPriceService {
    StockPriceResponse createStockPrice(StockPriceCreateRequest request);
    StockPriceResponse getStockPrice(Long id);
    List<StockPriceResponse> getAllStockPrices();
    StockPriceResponse updateStockPrice(Long id, StockPriceUpdateRequest request);
    void deleteStockPrice(Long id);
}
