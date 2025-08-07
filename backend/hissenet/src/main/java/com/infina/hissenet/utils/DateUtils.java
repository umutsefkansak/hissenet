package com.infina.hissenet.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtils {

    public static LocalDateTime calculateT2SettlementDate(LocalDateTime transactionDate) {
        LocalDate date = transactionDate.toLocalDate();
        int addedDays = 0;
        while (addedDays < 2) {
            date = date.plusDays(1);
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY /* && !isHoliday(date) */) {
                addedDays++;
            }
        }
        // BIST takas saati: 17:00
        return date.atTime(17, 0);
    }





}
