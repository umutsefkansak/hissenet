package com.infina.hissenet.service;

import com.infina.hissenet.entity.Portfolio;
import com.infina.hissenet.exception.NotFoundException;
import com.infina.hissenet.repository.PortfolioRepository;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService extends GenericServiceImpl<Portfolio,Long> {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(JpaRepository<Portfolio, Long> repository, PortfolioRepository portfolioRepository) {
        super(repository);
        this.portfolioRepository = portfolioRepository;
    }

    protected Portfolio getPortfolio(Long id){
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio bulunamadı: " + id));
    }

    //
}
