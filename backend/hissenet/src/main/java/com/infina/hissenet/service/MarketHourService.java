package com.infina.hissenet.service;

import com.infina.hissenet.service.abstracts.IMarketHourService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class MarketHourService implements IMarketHourService {

    private static final LocalTime MARKET_OPEN= LocalTime.of(10, 0);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(18, 0);
    private static  final LocalTime ORDER_COLLECTION_START = LocalTime.of(9, 30);
    private static  final  LocalTime ORDER_COLLECTION_END = LocalTime.of(17, 30);

    public boolean isMarketOpen(){
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return false;
        }
        return currentTime.isAfter(MARKET_OPEN) && currentTime.isBefore(MARKET_CLOSE);
    }
    public boolean canPlaceOrder(){
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek== DayOfWeek.SUNDAY){
            return false;
        }
        return currentTime.isBefore(ORDER_COLLECTION_END) && currentTime.isAfter(ORDER_COLLECTION_START);
    }



}
