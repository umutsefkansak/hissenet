package com.infina.hissenet.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.infina.hissenet.service.MarketHourService;
import com.infina.hissenet.service.abstracts.IOrderLifecycleService;

@Component
public class EndOfDayScheduler {

	private final IOrderLifecycleService lifecycleService;
	private final MarketHourService marketHourService;

	public EndOfDayScheduler(IOrderLifecycleService lifecycleService, MarketHourService marketHourService) {
		this.lifecycleService = lifecycleService;
		this.marketHourService = marketHourService;
	}

	@Scheduled(cron = "0 5 18 * * MON-FRI", zone = "Europe/Istanbul")
	public void cancelRemainingOpenOrdersAtClose() {
		
		if (marketHourService.isMarketOpen()) {
			return;
		}

	    lifecycleService.cancelOpenOrdersFor(LocalDate.now());

	}
	
}
