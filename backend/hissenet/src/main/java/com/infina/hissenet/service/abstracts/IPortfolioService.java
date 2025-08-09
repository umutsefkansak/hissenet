package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.PortfolioCreateRequest;
import com.infina.hissenet.dto.request.PortfolioUpdateRequest;
import com.infina.hissenet.dto.response.PortfolioResponse;
import com.infina.hissenet.dto.response.PortfolioSummaryResponse;
import com.infina.hissenet.entity.Portfolio;

import java.util.List;

/**
 * Contract for portfolio management operations including CRUD and
 * computed financial metrics such as total value, cost, and P/L.
 * Designed to separate domain behavior from controllers and persistence.
 *
 * Responsibilities:
 * - Create, update, delete portfolio resources
 * - Provide detailed and summary DTOs
 * - Recalculate and persist computed financial values safely
 *
 * Author: Furkan Can
 */
public interface IPortfolioService {

    /**
     * Creates a new portfolio for the given customer.
     * @param request portfolio creation payload
     * @param customerId owner customer identifier
     * @return created portfolio as response DTO
     */
    PortfolioResponse createPortfolio(PortfolioCreateRequest request, Long customerId);

    /**
     * Updates portfolio attributes.
     * @param id portfolio identifier
     * @param request portfolio update payload
     * @return updated portfolio as response DTO
     */
    PortfolioResponse updatePortfolio(Long id, PortfolioUpdateRequest request);

    /**
     * Deletes the portfolio by id.
     * @param id portfolio identifier
     */
    void deletePortfolio(Long id);

    /**
     * Returns a detailed portfolio response for UI consumption.
     * @param portfolioId portfolio identifier
     * @return portfolio response DTO
     */
    PortfolioResponse getPortfolioResponse(Long portfolioId);

    /**
     * Lists portfolios belonging to a customer in summary form.
     * @param customerId customer identifier
     * @return list of portfolio summaries
     */
    List<PortfolioSummaryResponse> getPortfoliosByCustomer(Long customerId);

    /**
     * Lists active portfolios.
     * @return list of active portfolio summaries
     */
    List<PortfolioSummaryResponse> getActivePortfolios();

    /**
     * Recomputes and persists financial metrics (value, cost, P/L) for a portfolio.
     * @param id portfolio identifier
     * @return updated portfolio as response DTO
     */
    PortfolioResponse updatePortfolioValues(Long id);
    
    /**
     * Retrieves the first portfolio of the specified customer.
     * Useful as a default target when an explicit portfolio is not selected.
     * @param customerId customer identifier
     * @return the first portfolio
     */
    Portfolio getCustomerFirstPortfolio(Long customerId);
}
