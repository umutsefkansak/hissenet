package com.infina.hissenet.service;

import com.infina.hissenet.dto.response.StockTransactionResponse;
import com.infina.hissenet.entity.Customer;
import com.infina.hissenet.entity.StockTransaction;
import com.infina.hissenet.entity.enums.StockTransactionType;
import com.infina.hissenet.entity.enums.TransactionStatus;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.mapper.StockTransactionMapper;
import com.infina.hissenet.repository.StockTransactionRepository;
import com.infina.hissenet.service.abstracts.ICommonFinancialService;
import com.infina.hissenet.utils.MessageUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common financial operations that aggregate and compute portfolio/transaction metrics.
 * Extracted to be reused by multiple domain services such as portfolio and stock transactions.
 *
 * <p>Responsibilities:</p>
 * - Merge BUY transactions by stock code and compute effective quantities and averages
 * - Compute customer-position quantities across portfolios
 *
 * <p>Notes:</p>
 * - Stream-based aggregations are used for readability and maintainability
 * - Monetary fields are handled with BigDecimal using safe rounding modes
 *
 * @author Furkan Can
 */
@Service
public class CommonFinancialService implements ICommonFinancialService {
    private final StockTransactionRepository stockTransactionRepository;
    private final StockTransactionMapper stockTransactionMapper;
    private final CustomerService customerService;

    public CommonFinancialService(StockTransactionRepository stockTransactionRepository, StockTransactionMapper stockTransactionMapper, CustomerService customerService) {
        this.stockTransactionRepository = stockTransactionRepository;
        this.stockTransactionMapper = stockTransactionMapper;
        this.customerService = customerService;
    }

    @Override
    public List<StockTransactionResponse> getAllBuyTransactions(Long portfolioId) {
        return stockTransactionRepository.findByPortfolioId(portfolioId).stream()
                .filter(tx -> tx.getTransactionType() == StockTransactionType.BUY
                        && (tx.getTransactionStatus() == TransactionStatus.SETTLED
                          ))
                .collect(Collectors.groupingBy(StockTransaction::getStockCode))
                .values().stream()
                .map(this::mergeTransactions)
                .filter(response -> response.quantity() > 0)
                .toList();
    }
    @Override
    public StockTransactionResponse mergeTransactions(List<StockTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException(MessageUtils.getMessage("transaction.list.empty"));
        }

        StockTransaction baseTx = transactions.get(0);
        StockTransactionResponse baseResponse = stockTransactionMapper.toResponse(baseTx);

        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalOtherFees = BigDecimal.ZERO;
        BigDecimal totalPriceAmount = BigDecimal.ZERO;
        BigDecimal averagePrice = BigDecimal.ZERO;

        for (StockTransaction tx : transactions) {
            int quantity = tx.getQuantity();

            totalQuantity=getQuantityForStockTransactionWithStream(tx.getPortfolio().getCustomer().getId(),tx.getStockCode());
            totalAmount = totalAmount.add(tx.getTotalAmount());
            totalCommission = totalCommission.add(tx.getCommission());
            totalTax = totalTax.add(tx.getTax());
            totalOtherFees = totalOtherFees.add(tx.getOtherFees());

            BigDecimal price = tx.getPrice();
            if (price != null) {
                totalPriceAmount = totalPriceAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
            }
             averagePrice = totalQuantity > 0
                    ? totalPriceAmount.divide(BigDecimal.valueOf(totalQuantity), 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
        }


        return new StockTransactionResponse(
                baseResponse.id(),
                baseResponse.portfolioId(),
                baseResponse.portfolioName(),
                baseResponse.stockCode(),
                baseResponse.orderId(),
                baseResponse.transactionType(),
                baseResponse.transactionStatus(),
                totalQuantity,
                averagePrice,
                totalAmount,
                totalCommission,
                totalTax,
                totalOtherFees,
                baseResponse.marketOrderType(),
                baseResponse.limitPrice(),
                baseResponse.executionPrice(),
                baseResponse.currentPrice(),
                baseResponse.transactionDate(),
                baseResponse.settlementDate(),
                baseResponse.notes(),
                baseResponse.createdAt(),
                baseResponse.updatedAt()
        );
    }
    @Override
    public Integer getQuantityForStockTransactionWithStream(Long customerId, String stockCode) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return customer.getPortfolios().stream()
                .flatMap(portfolio -> portfolio.getTransactions().stream())
                .filter(transaction ->
                        stockCode.equals(transaction.getStockCode()) &&
                                (TransactionStatus.SETTLED.equals(transaction.getTransactionStatus()) ||
                                 TransactionStatus.PARTIALLY_SOLD.equals(transaction.getTransactionStatus()))
                )
                .mapToInt(transaction -> {
                    if (transaction.getTransactionType() == StockTransactionType.BUY) {
                        return transaction.getQuantity();
                    } else if (transaction.getTransactionType() == StockTransactionType.SELL) {
                        return -transaction.getQuantity();
                    } else {
                        return 0;
                    }
                })
                .sum();
    }
}