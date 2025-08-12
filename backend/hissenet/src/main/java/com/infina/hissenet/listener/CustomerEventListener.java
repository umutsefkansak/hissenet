package com.infina.hissenet.listener;

import com.infina.hissenet.constants.PortfolioConstants;
import com.infina.hissenet.constants.WalletConstants;
import com.infina.hissenet.dto.request.CreateWalletRequest;
import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.entity.enums.PortfolioType;
import com.infina.hissenet.entity.enums.RiskProfile;
import com.infina.hissenet.event.CustomerCreatedEvent;
import com.infina.hissenet.service.PortfolioService;
import com.infina.hissenet.service.WalletService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CustomerEventListener {

    private final WalletService walletService;
    private final PortfolioService portfolioService;

    public CustomerEventListener(WalletService walletService, PortfolioService portfolioService) {
        this.walletService = walletService;
        this.portfolioService = portfolioService;
    }

    @EventListener
    @Transactional
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        try {
            // Cüzdan oluştur
            createWalletForCustomer(event.getCustomerId());

            // Portfolio oluştur
            createPortfolioForCustomer(event.getCustomerId(), event.getCustomerType());

        } catch (Exception e) {

            System.err.println("Error creating wallet/portfolio for customer " +
                    event.getCustomerId() + ": " + e.getMessage());

            throw new RuntimeException("Failed to create wallet/portfolio for customer", e);
        }
    }

    private void createWalletForCustomer(Long customerId) {
        CreateWalletRequest walletRequest = new CreateWalletRequest(
                customerId,
                WalletConstants.DEFAULT_BALANCE,
                WalletConstants.DEFAULT_CURRENCY,
                WalletConstants.DEFAULT_DAILY_LIMIT,
                WalletConstants.DEFAULT_MONTHLY_LIMIT,
                WalletConstants.DEFAULT_MAX_TRANSACTION_AMOUNT,
                WalletConstants.DEFAULT_MIN_TRANSACTION_AMOUNT,
                WalletConstants.DEFAULT_MAX_DAILY_TRANSACTION_COUNT,
                WalletConstants.DEFAULT_BLOCKED_BALANCE,
                WalletConstants.DEFAULT_AVAILABLE_BALANCE
        );

        walletService.createWallet(walletRequest);
    }

    private void createPortfolioForCustomer(Long customerId, String customerType) {
        PortfolioCreateRequest portfolioRequest = new PortfolioCreateRequest(
                PortfolioConstants.DEFAULT_PORTFOLIO_NAME,
                PortfolioConstants.DEFAULT_PORTFOLIO_DESCRIPTION,
                PortfolioConstants.DEFAULT_RISK_PROFILE,
                PortfolioConstants.DEFAULT_PORTFOLIO_TYPE
        );

        portfolioService.createPortfolio(portfolioRequest, customerId);
    }
}