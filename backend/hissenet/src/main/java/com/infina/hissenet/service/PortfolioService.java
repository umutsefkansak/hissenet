package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.exception.NotFoundException;
import com.infina.hissenet.exception.UserNotFoundException;
import com.infina.hissenet.mapper.PortfolioMapper;
import com.infina.hissenet.repository.CustomerRepository;
import com.infina.hissenet.repository.PortfolioRepository;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service
public class PortfolioService extends GenericServiceImpl<Portfolio,Long> {
    private final PortfolioRepository portfolioRepository;
    private final CustomerRepository customerRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(JpaRepository<Portfolio, Long> repository, PortfolioRepository portfolioRepository, CustomerRepository customerRepository, PortfolioMapper portfolioMapper) {
        super(repository);
        this.portfolioRepository = portfolioRepository;
        this.customerRepository = customerRepository;
        this.portfolioMapper = portfolioMapper;
    }

    // portföy getir
    protected Portfolio getPortfolio(Long id){
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio bulunamadı: " + id));
    }

    // portföy getir (eager loading ile)
    protected Portfolio getPortfolioWithCustomer(Long id){
        return portfolioRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> new NotFoundException("Portfolio bulunamadı: " + id));
    }

    // pörtföy dto döndür
    public PortfolioResponse getPortfolioResponse(Long portfolioId){
        return portfolioMapper.toResponse(getPortfolioWithCustomer(portfolioId));
    }

    // portfolio oluştur (müşterinin kaydı olunca otomatik portfolioda oluşturulacak)
    @Transactional
    public PortfolioResponse createPortfolio(PortfolioCreateRequest request,Long customerId) {
        Customer customer = findCustomerOrThrow(customerId);
        Portfolio portfolio = portfolioMapper.toEntity(request,customer);
        Portfolio savedPortfolio = save(portfolio);
        return portfolioMapper.toResponse(savedPortfolio);
    }

    // portföy güncelle
    @Transactional
    public PortfolioResponse updatePortfolio(Long id, PortfolioUpdateRequest request) {
        Portfolio portfolio = getPortfolio(id);
        portfolioMapper.updateEntity(portfolio, request);
        Portfolio updatedPortfolio = save(portfolio);
        return portfolioMapper.toResponse(updatedPortfolio);
    }

    // portföy sil
    @Transactional
    public void deletePortfolio(Long id) {
        Portfolio portfolio = getPortfolio(id);
        delete(portfolio);
    }

    // toplam değer hesapla
    private BigDecimal calculateTotalValue(Portfolio portfolio) {
        if (portfolio.getTransactions() == null || portfolio.getTransactions().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return portfolio.getTransactions().stream()
                .map(transaction -> transaction.getPrice().multiply(BigDecimal.valueOf(transaction.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // toplam maliyet hesapla
    private BigDecimal calculateTotalCost(Portfolio portfolio) {
        if (portfolio.getTransactions() == null || portfolio.getTransactions().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return portfolio.getTransactions().stream()
                .map(transaction -> transaction.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // kar zarar hesapla
    private BigDecimal calculateProfitLoss(Portfolio portfolio) {
        BigDecimal totalValue = calculateTotalValue(portfolio);
        BigDecimal totalCost = calculateTotalCost(portfolio);
        return totalValue.subtract(totalCost);
    }

    // portföy değeri güncelleme patch
    @Transactional
    public PortfolioResponse updatePortfolioValues(Long id) {
        Portfolio portfolio = getPortfolio(id);
        BigDecimal totalValue = calculateTotalValue(portfolio);
        BigDecimal totalCost = calculateTotalCost(portfolio);
        BigDecimal totalProfitLoss = calculateProfitLoss(portfolio);
        
        portfolio.setTotalValue(totalValue);
        portfolio.setTotalCost(totalCost);
        portfolio.setTotalProfitLoss(totalProfitLoss);
        
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitLossPercentage = totalProfitLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            portfolio.setProfitLossPercentage(profitLossPercentage);
        } else {
            portfolio.setProfitLossPercentage(BigDecimal.ZERO);
        }
        
        Portfolio updatedPortfolio = save(portfolio);
        return portfolioMapper.toResponse(updatedPortfolio);
    }

    // müşterini tüm portföyleri
    public List<PortfolioSummaryResponse> getPortfoliosByCustomer(Long customerId) {
        List<Portfolio> portfolios = portfolioRepository.findByCustomerId(customerId);
        return portfolios.stream()
                .map(portfolioMapper::toSummaryResponse)
                .toList();
    }

    //Aktif portföyler
    public List<PortfolioSummaryResponse> getActivePortfolios() {
        List<Portfolio> portfolios = portfolioRepository.findByIsActiveTrue();
        return portfolios.stream()
                .map(portfolioMapper::toSummaryResponse)
                .toList();
    }

    private Customer findCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("Müşteri "));
    }
}
