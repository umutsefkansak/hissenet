package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.StockCreateRequest;
import com.infina.hissenet.dto.request.StockUpdateRequest;
import com.infina.hissenet.dto.response.StockResponse;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.exception.NotFoundException;
import com.infina.hissenet.mapper.StockMapper;
import com.infina.hissenet.repository.StockRepository;
import com.infina.hissenet.service.abstracts.IStockService;
import com.infina.hissenet.utils.GenericServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService extends GenericServiceImpl<Stock, Long> implements IStockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public StockService(JpaRepository<Stock, Long> repository, StockRepository stockRepository,
                        StockMapper stockMapper) {
        super(repository);
        this.stockRepository = stockRepository;
        this.stockMapper     = stockMapper;
    }

    @Transactional
    public StockResponse createStock(StockCreateRequest request) {
        Stock entity = stockMapper.toEntity(request);
        Stock saved  = stockRepository.save(entity);
        return stockMapper.toResponse(saved);
    }

    public StockResponse getStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stock not found: " + id));
        return stockMapper.toResponse(stock);
    }

    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(stockMapper::toResponse)
                .toList();
    }

    @Transactional
    public StockResponse updateStock(Long id, StockUpdateRequest request) {
        Stock existing = stockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stock not found: " + id));
        stockMapper.updateEntityFromDto(request, existing);
        Stock updated = stockRepository.save(existing);
        return stockMapper.toResponse(updated);
    }

    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new NotFoundException("Stock not found: " + id);
        }
        stockRepository.deleteById(id);
    }
}