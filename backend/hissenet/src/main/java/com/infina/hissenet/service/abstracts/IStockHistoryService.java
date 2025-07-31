package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.StockHistoryCreateRequest;
import com.infina.hissenet.dto.request.StockHistoryUpdateRequest;
import com.infina.hissenet.dto.response.StockHistoryResponse;

import java.util.List;

public interface IStockHistoryService {
    StockHistoryResponse createStockHistory(StockHistoryCreateRequest request);
    StockHistoryResponse getStockHistory(Long id);
    List<StockHistoryResponse> getAllStockHistories();
    StockHistoryResponse updateStockHistory(Long id, StockHistoryUpdateRequest request);
    void deleteStockHistory(Long id);
}
