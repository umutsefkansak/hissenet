package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.StockPriceCreateRequest;
import com.infina.hissenet.dto.request.StockPriceUpdateRequest;
import com.infina.hissenet.dto.response.StockPriceResponse;
import com.infina.hissenet.entity.Stock;
import com.infina.hissenet.entity.StockPrice;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.mapper.StockPriceMapper;
import com.infina.hissenet.repository.StockPriceRepository;
import com.infina.hissenet.repository.StockRepository;
import com.infina.hissenet.service.abstracts.IStockPriceService;
import com.infina.hissenet.utils.GenericServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockPriceService extends GenericServiceImpl<StockPrice, Long> implements IStockPriceService {

    private final StockPriceRepository priceRepository;
    private final StockRepository stockRepository;
    private final StockPriceMapper priceMapper;

    public StockPriceService(JpaRepository<StockPrice, Long> repository, StockPriceRepository priceRepository,
                             StockRepository stockRepository, StockPriceMapper priceMapper) {
        super(repository);
        this.priceRepository = priceRepository;
        this.stockRepository = stockRepository;
        this.priceMapper     = priceMapper;
    }

    @Transactional
    public StockPriceResponse createStockPrice(StockPriceCreateRequest request) {
        Stock stock = stockRepository.findById(request.stockId())
                .orElseThrow(() -> new NotFoundException("Stock not found: " + request.stockId()));
        StockPrice entity = priceMapper.toEntity(request, stock);
        StockPrice saved  = priceRepository.save(entity);
        return priceMapper.toResponse(saved);
    }

    public StockPriceResponse getStockPrice(Long id) {
        StockPrice price = priceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StockPrice not found: " + id));
        return priceMapper.toResponse(price);
    }

    public List<StockPriceResponse> getAllStockPrices() {
        return priceRepository.findAll()
                .stream()
                .map(priceMapper::toResponse)
                .toList();
    }

    @Transactional
    public StockPriceResponse updateStockPrice(Long id, StockPriceUpdateRequest request) {
        StockPrice existing = priceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StockPrice not found: " + id));
        priceMapper.updateEntityFromDto(request, existing);
        StockPrice updated = priceRepository.save(existing);
        return priceMapper.toResponse(updated);
    }

    @Transactional
    public void deleteStockPrice(Long id) {
        if (!priceRepository.existsById(id)) {
            throw new NotFoundException("StockPrice not found: " + id);
        }
        priceRepository.deleteById(id);
    }
}
