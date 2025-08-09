package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.exception.employee.UserNotFoundException;
import com.infina.hissenet.mapper.PortfolioMapper;
import com.infina.hissenet.repository.PortfolioRepository;
import com.infina.hissenet.service.abstracts.IPortfolioService;
import com.infina.hissenet.utils.GenericServiceImpl;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;



@Service
public class PortfolioService extends GenericServiceImpl<Portfolio,Long> implements IPortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final CustomerService customerService;
    private final PortfolioMapper portfolioMapper;
    private final CommonFinancialService commonFinancialService;
    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);


    public PortfolioService(JpaRepository<Portfolio, Long> repository, PortfolioRepository portfolioRepository, CustomerService customerService, PortfolioMapper portfolioMapper, CommonFinancialService commonFinancialService) {
        super(repository);
        this.portfolioRepository = portfolioRepository;
        this.customerService = customerService;
        this.portfolioMapper = portfolioMapper;
        this.commonFinancialService = commonFinancialService;
    }

    // portföy getir
    protected Portfolio getPortfolio(Long id){
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("portfolio.not.found", id)));
    }

    // portföy getir
    protected Portfolio getPortfolioWithCustomer(Long id){
        return portfolioRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("portfolio.not.found", id)));
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



    // Debug için güncellenmiş toplam değer hesaplama
    private BigDecimal calculateTotalValue(Portfolio portfolio) {
        List<StockTransactionResponse> buyTransactions = commonFinancialService.getAllBuyTransactions(portfolio.getId());

        if (buyTransactions == null || buyTransactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalValue = BigDecimal.ZERO;

        // Debug için log ekle
        logger.info("=== PORTFOLIO VALUE CALCULATION DEBUG ===");
        logger.info("Portfolio ID: {}", portfolio.getId());
        logger.info("Transaction count: {}", buyTransactions.size());

        for (StockTransactionResponse tx : buyTransactions) {
            // quantity değeri zaten mergeTransactions'da doğru hesaplanmış
            // Bu değer sadece mevcut (satılmamış) hisseleri içeriyor
            BigDecimal currentPrice = tx.currentPrice() != null ? tx.currentPrice() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(tx.quantity());
            BigDecimal stockValue = currentPrice.multiply(quantity);

            totalValue = totalValue.add(stockValue);

            // Debug için her hisseyi logla
            logger.info("Stock: {} | Quantity: {} | Current Price: {} | Stock Value: {} | Running Total: {}",
                    tx.stockCode(), tx.quantity(), currentPrice, stockValue, totalValue);
        }

        logger.info("Final Total Value: {}", totalValue);
        logger.info("=== END DEBUG ===");

        return totalValue;
    }

    // Toplam maliyet hesaplama (price * quantity + komisyon + vergi + diğer masraflar)
    private BigDecimal calculateTotalCost(Portfolio portfolio) {
        List<StockTransactionResponse> buyTransactions = commonFinancialService.getAllBuyTransactions(portfolio.getId());

        if (buyTransactions == null || buyTransactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = BigDecimal.ZERO;

        for (StockTransactionResponse tx : buyTransactions) {
            // Merge edilmiş veriden direkt quantity kullan
            BigDecimal price = tx.price() != null ? tx.price() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(tx.quantity());

            BigDecimal transactionCost = price.multiply(quantity);

            // Komisyon, vergi ve diğer masrafları ekle
            if (tx.commission() != null) {
                transactionCost = transactionCost.add(tx.commission());
            }
            if (tx.tax() != null) {
                transactionCost = transactionCost.add(tx.tax());
            }
            if (tx.otherFees() != null) {
                transactionCost = transactionCost.add(tx.otherFees());
            }

            totalCost = totalCost.add(transactionCost);
        }

        return totalCost;
    }

    // Kar zarar hesapla - merge edilmiş veriden direkt hesaplama
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


            BigDecimal profitLossPercentage = BigDecimal.ZERO;
            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                profitLossPercentage = totalProfitLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));


                BigDecimal maxPercentage = new BigDecimal("999.99");
                BigDecimal minPercentage = new BigDecimal("-999.99");


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
                .orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("customer.not.found.id", customerId)));
    }
    public Portfolio getCustomerFirstPortfolio(Long customerId) {
        return portfolioRepository.findByCustomerId(customerId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(MessageUtils.getMessage("portfolio.not.found", customerId)));
    }

}
