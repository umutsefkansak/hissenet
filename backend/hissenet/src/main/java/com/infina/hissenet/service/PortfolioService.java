package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.exception.employee.UserNotFoundException;
import com.infina.hissenet.mapper.PortfolioMapper;
import com.infina.hissenet.repository.PortfolioRepository;
import com.infina.hissenet.service.abstracts.IPortfolioService;
import com.infina.hissenet.utils.GenericServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService extends GenericServiceImpl<Portfolio,Long> implements IPortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final CustomerService customerService;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(JpaRepository<Portfolio, Long> repository, PortfolioRepository portfolioRepository, CustomerService customerService, PortfolioMapper portfolioMapper) {
        super(repository);
        this.portfolioRepository = portfolioRepository;
        this.customerService = customerService;
        this.portfolioMapper = portfolioMapper;
    }

    // portföy getir
    protected Portfolio getPortfolio(Long id){
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio bulunamadı: " + id));
    }

    // portföy getir
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
        deleteById(portfolio.getId());
    }

    // Toplam değer hesaplama (quantity * currentPrice)
    private BigDecimal calculateTotalValue(Portfolio portfolio) {
        if (portfolio.getTransactions() == null || portfolio.getTransactions().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalValue = BigDecimal.ZERO;

        for (var tx : portfolio.getTransactions()) {
            if (tx.getTransactionStatus() != TransactionStatus.SETTLED) continue;
            if (tx.getTransactionType() != StockTransactionType.BUY) continue;

            BigDecimal currentPrice = tx.getCurrentPrice() != null ? tx.getCurrentPrice() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(tx.getQuantity());

            totalValue = totalValue.add(currentPrice.multiply(quantity));
        }

        return totalValue;
    }

    // Toplam maliyet hesaplama (price * quantity + komisyon + vergi + diğer masraflar)
    private BigDecimal calculateTotalCost(Portfolio portfolio) {
        if (portfolio.getTransactions() == null || portfolio.getTransactions().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = BigDecimal.ZERO;

        for (var tx : portfolio.getTransactions()) {
            if (tx.getTransactionStatus() != TransactionStatus.SETTLED) continue;
            if (tx.getTransactionType() != StockTransactionType.BUY) continue;

            BigDecimal price = tx.getPrice() != null ? tx.getPrice() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(tx.getQuantity());

            BigDecimal transactionCost = price.multiply(quantity);

            // Komisyon, vergi ve diğer masrafları ekle
            if (tx.getCommission() != null) {
                transactionCost = transactionCost.add(tx.getCommission());
            }
            if (tx.getTax() != null) {
                transactionCost = transactionCost.add(tx.getTax());
            }
            if (tx.getOtherFees() != null) {
                transactionCost = transactionCost.add(tx.getOtherFees());
            }

            totalCost = totalCost.add(transactionCost);
        }

        return totalCost;
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
        try {
            Portfolio portfolio = getPortfolio(id);

            BigDecimal totalValue = calculateTotalValue(portfolio);
            BigDecimal totalCost = calculateTotalCost(portfolio);
            BigDecimal totalProfitLoss = calculateProfitLoss(portfolio);

            // BigDecimal değerler için daha güvenli maksimum değerler
            BigDecimal maxDecimal = new BigDecimal("9999999999999999999999999999999999.9999"); // precision=38,scale=4

            // Overflow kontrolü - precision=38,scale=4 için
            if (totalValue.abs().compareTo(maxDecimal) > 0) {
                totalValue = totalValue.signum() > 0 ? maxDecimal : maxDecimal.negate();
            }

            if (totalCost.abs().compareTo(maxDecimal) > 0) {
                totalCost = totalCost.signum() > 0 ? maxDecimal : maxDecimal.negate();
            }

            if (totalProfitLoss.abs().compareTo(maxDecimal) > 0) {
                totalProfitLoss = totalProfitLoss.signum() > 0 ? maxDecimal : maxDecimal.negate();
            }

            portfolio.setTotalValue(totalValue);
            portfolio.setTotalCost(totalCost);
            portfolio.setTotalProfitLoss(totalProfitLoss);

            // Profit Loss Percentage hesaplama - EN KRİTİK KISIM!
            BigDecimal profitLossPercentage = BigDecimal.ZERO;
            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                profitLossPercentage = totalProfitLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                // PRECISION(5,2) için maksimum değer: 999.99
                BigDecimal maxPercentage = new BigDecimal("999.99");
                BigDecimal minPercentage = new BigDecimal("-999.99");

                // Percentage overflow kontrolü - database constraint'e uygun
                if (profitLossPercentage.compareTo(maxPercentage) > 0) {
                    profitLossPercentage = maxPercentage;
                } else if (profitLossPercentage.compareTo(minPercentage) < 0) {
                    profitLossPercentage = minPercentage;
                }
            }

            portfolio.setProfitLossPercentage(profitLossPercentage);

            Portfolio updatedPortfolio = save(portfolio);
            return portfolioMapper.toResponse(updatedPortfolio);

        } catch (Exception e) {
            // Debug için hangi değerlerin problematik olduğunu logla

            throw e;
        }
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
        return customerService.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("Müşteri "));
    }
    protected Portfolio getCustomerFirstPortfolio(Long customerId) {
        return portfolioRepository.findByCustomerId(customerId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Portfolio "));
    }

}
