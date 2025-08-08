package com.infina.hissenet.service.abstracts;

import java.time.LocalDate;

public interface IOrderLifecycleService {
	void cancelOpenOrdersFor(LocalDate tradingDay);
}
