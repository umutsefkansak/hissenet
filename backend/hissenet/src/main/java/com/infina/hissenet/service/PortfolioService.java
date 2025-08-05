package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Portfolio;
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
import java.util.List;

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

    // toplam değer hesapla (güncel fiyat * miktar)
    private BigDecimal calculateTotalValue(Portfolio portfolio) {
        if (portfolio.getTransactions() == null || portfolio.getTransactions().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return portfolio.getTransactions().stream()
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.COMPLETED)
                .map(transaction -> {
                    BigDecimal currentPrice = transaction.getCurrentPrice() != null ?
                            transaction.getCurrentPrice() : BigDecimal.ZERO;
                    return currentPrice.multiply(BigDecimal.valueOf(transaction.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // toplam maliyet hesapla (gerçekleşme fiyatı * miktar + komisyon + vergi + diğer masraflar)
    private BigDecimal calculateTotalCost(Portfolio portfolio) {
        if (portfolio.getTransactions() == null || portfolio.getTransactions().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return portfolio.getTransactions().stream()
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.COMPLETED)
                .map(transaction -> {
                    // Gerçekleşme fiyatı * miktar + masraflar
                    BigDecimal executionPrice = transaction.getExecutionPrice() != null ?
                            transaction.getExecutionPrice() :
                            (transaction.getPrice() != null ? transaction.getPrice() : BigDecimal.ZERO);

                    BigDecimal transactionCost = executionPrice.multiply(BigDecimal.valueOf(transaction.getQuantity()));

                    // Masrafları ekle
                    if (transaction.getCommission() != null) {
                        transactionCost = transactionCost.add(transaction.getCommission());
                    }
                    if (transaction.getTax() != null) {
                        transactionCost = transactionCost.add(transaction.getTax());
                    }
                    if (transaction.getOtherFees() != null) {
                        transactionCost = transactionCost.add(transaction.getOtherFees());
                    }

                    return transactionCost;
                })
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
        try {
            Portfolio portfolio = getPortfolio(id);

            BigDecimal totalValue = calculateTotalValue(portfolio);
            BigDecimal totalCost = calculateTotalCost(portfolio);
            BigDecimal totalProfitLoss = calculateProfitLoss(portfolio);




            // Overflow kontrolü
            if (totalValue.compareTo(BigDecimal.valueOf(999999999)) > 0) {

                totalValue = BigDecimal.valueOf(999999999);
            }

            if (totalCost.compareTo(BigDecimal.valueOf(999999999)) > 0) {

                totalCost = BigDecimal.valueOf(999999999);
            }

            if (totalProfitLoss.abs().compareTo(BigDecimal.valueOf(999999999)) > 0) {

                totalProfitLoss = totalProfitLoss.signum() > 0 ?
                        BigDecimal.valueOf(999999999) : BigDecimal.valueOf(-999999999);
            }

            portfolio.setTotalValue(totalValue);
            portfolio.setTotalCost(totalCost);
            portfolio.setTotalProfitLoss(totalProfitLoss);

            BigDecimal profitLossPercentage = BigDecimal.ZERO;
            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                profitLossPercentage = totalProfitLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));


                // Kar oranı overflow kontrolü - en kritik kısım!
                if (profitLossPercentage.abs().compareTo(BigDecimal.valueOf(999999)) > 0) {


                    profitLossPercentage = profitLossPercentage.signum() > 0 ?
                            BigDecimal.valueOf(999999) : BigDecimal.valueOf(-999999);
                }
            }

            portfolio.setProfitLossPercentage(profitLossPercentage);



            Portfolio updatedPortfolio = save(portfolio);


            return portfolioMapper.toResponse(updatedPortfolio);

        } catch (Exception e) {
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
