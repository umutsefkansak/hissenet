package com.infina.hissenet.scheduler;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;
import com.infina.hissenet.repository.OrderRepository;
import com.infina.hissenet.service.abstracts.IStockCacheService;
import com.infina.hissenet.service.abstracts.IWalletService;

import static com.infina.hissenet.constants.OrderConstants.COMMISSION_RATE;

@Component
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final IWalletService walletService;
    private final IStockCacheService stockCacheService;

    public OrderScheduler(OrderRepository orderRepository, IWalletService walletService,
            IStockCacheService stockCacheService) {
        this.orderRepository = orderRepository;
        this.walletService = walletService;
        this.stockCacheService = stockCacheService;
    }

    @Transactional
    @Scheduled(fixedRate = 32000)
    public void processPendingLimitOrders() {
        List<Order> openOrders = orderRepository.findByStatus(OrderStatus.OPEN);

        for (Order order : openOrders) {
            try {
                String stockCode = order.getStockCode();
                BigDecimal marketPrice = stockCacheService.getPriceByCodeOrNull(stockCode);

                if (marketPrice == null) {
                    continue;
                }

                BigDecimal limitPrice = order.getPrice();
                BigDecimal totalAmount = limitPrice.multiply(order.getQuantity());
                BigDecimal commission = totalAmount.multiply(COMMISSION_RATE);
                BigDecimal tax = BigDecimal.ZERO;

                boolean shouldFill = false;

                if (order.getType() == OrderType.BUY && marketPrice.compareTo(limitPrice) <= 0) {
                    shouldFill = true;
                } else if (order.getType() == OrderType.SELL && marketPrice.compareTo(limitPrice) >= 0) {
                    shouldFill = true;
                }

                if (shouldFill) {
                    try {
                        if (order.getType() == OrderType.BUY) {
                            walletService.processStockPurchase(order.getCustomer().getId(), totalAmount, commission, tax);
                        } else {
                            walletService.processStockSale(order.getCustomer().getId(), totalAmount, commission, tax);
                        }

                        order.setStatus(OrderStatus.FILLED);
                    } catch (Exception e) {
                        order.setStatus(OrderStatus.FAILED);
                    }

                    orderRepository.save(order);
                }

            } catch (Exception e) {

            }
        }
    }
}
