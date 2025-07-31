package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.StockCreateRequest;
import com.infina.hissenet.dto.request.StockUpdateRequest;
import com.infina.hissenet.dto.response.StockResponse;

import java.util.List;

public interface IStockService {
    StockResponse createStock(StockCreateRequest request);
    StockResponse getStock(Long id);
    List<StockResponse> getAllStocks();
    StockResponse updateStock(Long id, StockUpdateRequest request);
    void deleteStock(Long id);
}
