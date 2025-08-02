package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.StockData;

import java.util.List;

public interface IMarketDataService {
    List<StockData> getAllStocks();
}
