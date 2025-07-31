package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;

import java.util.List;

public interface IPortfolioService {
    PortfolioResponse createPortfolio(PortfolioCreateRequest request, Long customerId);
    PortfolioResponse updatePortfolio(Long id, PortfolioUpdateRequest request);
    void deletePortfolio(Long id);
    PortfolioResponse getPortfolioResponse(Long portfolioId);
    List<PortfolioSummaryResponse> getPortfoliosByCustomer(Long customerId);
    List<PortfolioSummaryResponse> getActivePortfolios();
    PortfolioResponse updatePortfolioValues(Long id);
}
