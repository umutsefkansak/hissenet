package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.StockHistoryCreateRequest;
import com.infina.hissenet.dto.request.StockHistoryUpdateRequest;
import com.infina.hissenet.dto.response.StockHistoryResponse;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockHistory;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.mapper.StockHistoryMapper;
import com.infina.hissenet.repository.StockHistoryRepository;
import com.infina.hissenet.repository.StockRepository;
import com.infina.hissenet.service.abstracts.IStockHistoryService;
import com.infina.hissenet.utils.GenericServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockHistoryService extends GenericServiceImpl<StockHistory, Long> implements IStockHistoryService {

    private final StockHistoryRepository historyRepository;
    private final StockRepository stockRepository;
    private final StockHistoryMapper historyMapper;

    public StockHistoryService(JpaRepository<StockHistory, Long> repository, StockHistoryRepository historyRepository,
                               StockRepository stockRepository, StockHistoryMapper historyMapper) {
        super(repository);
        this.historyRepository = historyRepository;
        this.stockRepository   = stockRepository;
        this.historyMapper     = historyMapper;
    }

    @Transactional
    public StockHistoryResponse createStockHistory(StockHistoryCreateRequest request) {
        Stock stock = stockRepository.findById(request.stockId())
                .orElseThrow(() -> new NotFoundException("Stock not found: " + request.stockId()));
        StockHistory entity = historyMapper.toEntity(request, stock);
        StockHistory saved  = historyRepository.save(entity);
        return historyMapper.toResponse(saved);
    }

    public StockHistoryResponse getStockHistory(Long id) {
        StockHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StockHistory not found: " + id));
        return historyMapper.toResponse(history);
    }

    public List<StockHistoryResponse> getAllStockHistories() {
        return historyRepository.findAll()
                .stream()
                .map(historyMapper::toResponse)
                .toList();
    }

    @Transactional
    public StockHistoryResponse updateStockHistory(Long id, StockHistoryUpdateRequest request) {
        StockHistory existing = historyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StockHistory not found: " + id));
        historyMapper.updateEntityFromDto(request, existing);
        StockHistory updated = historyRepository.save(existing);
        return historyMapper.toResponse(updated);
    }

    @Transactional
    public void deleteStockHistory(Long id) {
        if (!historyRepository.existsById(id)) {
            throw new NotFoundException("StockHistory not found: " + id);
        }
        historyRepository.deleteById(id);
    }
}
