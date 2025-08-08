package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.exception.transaction.InsufficientStockException;
import com.infina.hissenet.exception.transaction.UnauthorizedOperationException;
import com.infina.hissenet.mapper.StockTransactionMapper;
import com.infina.hissenet.repository.StockTransactionRepository;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockTransactionService extends GenericServiceImpl<StockTransaction, Long> implements IStockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final PortfolioService portfolioService;
    private final ICacheManagerService cacheManagerService;
    private final StockTransactionMapper mapper;
    private final CustomerService customerService;
    private final CommonFinancialService commonFinancialService;


    public StockTransactionService(JpaRepository<StockTransaction, Long> repository, StockTransactionRepository stockTransactionRepository, PortfolioService portfolioService, ICacheManagerService cacheManagerService, StockTransactionMapper mapper, CustomerService customerService, CommonFinancialService commonFinancialService) {
        super(repository);
        this.stockTransactionRepository = stockTransactionRepository;
        this.portfolioService = portfolioService;
        this.cacheManagerService = cacheManagerService;
        this.mapper = mapper;
        this.customerService = customerService;
        this.commonFinancialService = commonFinancialService;

    }

    // Order oluştuğunda otomatik StockTransaction oluştur
    @Transactional
    public void createTransactionFromOrder(Order order) {

        Portfolio portfolio = portfolioService.getCustomerFirstPortfolio(order.getCustomer().getId());

        // StockTransaction oluştur
        StockTransaction transaction = new StockTransaction();
        transaction.setPortfolio(portfolio);
        transaction.setStockCode(order.getStockCode());
        transaction.setTransactionType(order.getType() == OrderType.BUY?
                StockTransactionType.BUY : StockTransactionType.SELL);
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setQuantity(order.getQuantity().intValue());


        transaction.setPrice(order.getPrice());
        transaction.setExecutionPrice(order.getPrice());
        transaction.setTotalAmount(order.getTotalAmount());
        System.out.println();
        // Cache'den anlık fiyatı al
        BigDecimal currentPrice = cacheManagerService.getCachedByCode(order.getStockCode()).lastPrice();

        transaction.setCurrentPrice(currentPrice);
        transaction.setOrder(order);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSettlementDate(LocalDateTime.now().plusMinutes(1));
        transaction.setNotes("Order üzerinden oluşturulan işlem");


        BigDecimal commissionRate = portfolio.getCustomer().getCommissionRate();
        if (commissionRate == null) {
            commissionRate = BigDecimal.valueOf(0.001);
        }
        BigDecimal commission = order.getTotalAmount().multiply(commissionRate);
        transaction.setCommission(commission);

        if(order.getType()== OrderType.SELL){
            try{
             Integer currentQuantity=  getQuantityForStockTransactionWithStream(order.getCustomer().getId(),transaction.getStockCode());
            // fifoService.processFIFOForSell(order.getCustomer().getId(),transaction.getStockCode(),currentQuantity);
             if(order.getQuantity().intValue()>currentQuantity){
                 throw new InsufficientStockException("The quantity to sell exceeds the available stock for: " + transaction.getStockCode());
             }
            }catch (RuntimeException e){
                throw new InsufficientStockException("The quantity to sell exceeds the available stock for: " + transaction.getStockCode());
            }
        }

        save(transaction);
        portfolioService.updatePortfolioValues(transaction.getPortfolio().getId());
    }

   @Override
   @Transactional
    public void saveAll(List<StockTransaction> stockTransactions) {
        stockTransactionRepository.saveAll(stockTransactions);
    }

    public List<StockTransactionResponse> getAllBuyTransactions(Long portfolioId) {
        List<StockTransactionResponse> list = commonFinancialService.getAllBuyTransactions(portfolioId);
        portfolioService.updatePortfolioValues(portfolioId);
        return list;
    }

    @Transactional
    public void processStockSettlements() {
        List<StockTransaction> transactionsReadyForSettlement = stockTransactionRepository
                .findStockTransactionsReadyForSettlement(
                        LocalDateTime.now(),
                        TransactionStatus.COMPLETED,
                        StockTransactionType.BUY,
                        StockTransactionType.SELL
                );
        System.out.println(transactionsReadyForSettlement.toString());

        for (StockTransaction transaction : transactionsReadyForSettlement) {
            processStockSettlement(transaction);
        }
    }

    private void processStockSettlement(StockTransaction transaction) {
        // Settlement işlemlerini burada yapabilirsin (gerekirse)
        // Örneğin portfolio güncellemeleri vb.

        transaction.setTransactionStatus(TransactionStatus.SETTLED);
        // Settlement tarihini güncelleyebilirsin veya ayrı bir actualSettlementDate alanı ekleyebilirsin

        save(transaction);

        // Portfolio değerlerini güncelle
        portfolioService.updatePortfolioValues(transaction.getPortfolio().getId());
    }

    private StockTransactionResponse mergeTransactions(List<StockTransaction> transactions) {
       return commonFinancialService.mergeTransactions(transactions);
    }
    public void updatePortfolioIdForStockTransactions(Long transactionId,Long portfolioId) {
        StockTransaction transaction = stockTransactionRepository.findById(transactionId).orElseThrow(()->new NotFoundException("Stock "));
        Long oldPortfolioId = transaction.getPortfolio().getId();
        Portfolio portfolio=portfolioService.findById(portfolioId).orElseThrow(()->new NotFoundException("Portfolio "));
        if (!portfolio.getCustomer().getId().equals(transaction.getPortfolio().getCustomer().getId())) {
            throw new UnauthorizedOperationException("You are not authorized to modify this portfolio");
        }
        transaction.setPortfolio(portfolio);
        save(transaction);
        portfolioService.updatePortfolioValues(transaction.getPortfolio().getId());
        portfolioService.updatePortfolioValues(oldPortfolioId);
    }
    // aktif hisse adedini döndüren methot
    public Integer getQuantityForStockTransactionWithStream(Long customerId, String stockCode) {
        return commonFinancialService.getQuantityForStockTransactionWithStream(customerId, stockCode);
    }
    public Integer getTotalStock(Long customerId) {
        Customer customer=customerService.findById(customerId).orElseThrow(()->new NotFoundException("Cursomer"));
        Integer result=0;
        for (Portfolio portfolio : customer.getPortfolios()) {
            result+=getAllBuyTransactions(portfolio.getId()).size();
        }
        return result;
    }

}