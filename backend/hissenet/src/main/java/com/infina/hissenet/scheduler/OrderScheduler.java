package com.infina.hissenet.scheduler;

import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.service.abstracts.ICacheManagerService;
import com.infina.hissenet.service.abstracts.IStockTransactionService;
import com.infina.hissenet.service.abstracts.IWalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.infina.hissenet.constants.OrderConstants.COMMISSION_RATE;

@Component
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final IWalletService walletService;
    private final ICacheManagerService stockCacheService;
    private final IStockTransactionService stockTransactionService;

    public OrderScheduler(OrderRepository orderRepository, IWalletService walletService,
                          ICacheManagerService stockCacheService, IStockTransactionService stockTransactionService) {
        this.orderRepository = orderRepository;
        this.walletService = walletService;
        this.stockCacheService = stockCacheService;
        this.stockTransactionService = stockTransactionService;
    }

    @Transactional
    @Scheduled(fixedDelay = 60000 * 5 + 1)
    public void processPendingLimitOrders() {
        List<Order> openOrders = orderRepository.findByStatus(OrderStatus.OPEN);

        for (Order order : openOrders) {
            try {
                String stockCode = order.getStockCode();
                BigDecimal marketPrice = stockCacheService.getCachedByCode(stockCode).lastPrice();

                if (marketPrice == null) {
                    continue;
                }

                BigDecimal limitPrice = order.getPrice();
                BigDecimal totalAmount = limitPrice.multiply(order.getQuantity());
                BigDecimal commission = totalAmount.multiply(COMMISSION_RATE);

                boolean shouldFill = false;

                if (order.getType() == OrderType.BUY && marketPrice.compareTo(limitPrice) <= 0) {
                    shouldFill = true;
                } else if (order.getType() == OrderType.SELL && marketPrice.compareTo(limitPrice) >= 0) {
                    shouldFill = true;
                }

                if (shouldFill) {
                    try {
                        if (order.getType() == OrderType.BUY) {
                            walletService.processStockPurchase(order.getCustomer().getId(), totalAmount, commission);
                        } else {
                            walletService.processStockSale(order.getCustomer().getId(), totalAmount, commission);
                        }

                        order.setStatus(OrderStatus.FILLED);
                    } catch (Exception e) {
                        order.setStatus(OrderStatus.FAILED);
                    }

                    orderRepository.save(order);
                    stockTransactionService.createTransactionFromOrder(order);
                }

            } catch (Exception e) {

            }
        }
    }
}
