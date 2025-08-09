package com.infina.hissenet.scheduler;

import com.infina.hissenet.service.MarketHourService;
import com.infina.hissenet.service.abstracts.IOrderLifecycleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndOfDaySchedulerTest {

    @Mock private IOrderLifecycleService lifecycleService;
    @Mock private MarketHourService marketHourService;

    @InjectMocks private EndOfDayScheduler scheduler;

    @Captor private ArgumentCaptor<LocalDate> dateCaptor;

    @Test
    void whenMarketOpen_thenDoNothing() {
        when(marketHourService.isMarketOpen()).thenReturn(true);

        scheduler.cancelRemainingOpenOrdersAtClose();

        verify(marketHourService).isMarketOpen();
        verifyNoInteractions(lifecycleService);
    }

    @Test
    void whenMarketClosed_thenCancelOpenOrdersForToday() {
        when(marketHourService.isMarketOpen()).thenReturn(false);

        LocalDate today = LocalDate.now();
        scheduler.cancelRemainingOpenOrdersAtClose();

        verify(marketHourService).isMarketOpen();
        verify(lifecycleService).cancelOpenOrdersFor(dateCaptor.capture());
        assertEquals(today, dateCaptor.getValue());
        verifyNoMoreInteractions(lifecycleService);
    }

    @Test
    void whenMarketClosed_andLifecycleThrows_thenExceptionPropagates() {
        when(marketHourService.isMarketOpen()).thenReturn(false);
        doThrow(new RuntimeException("cancel error"))
                .when(lifecycleService).cancelOpenOrdersFor(any(LocalDate.class));

        assertThrows(RuntimeException.class, () -> scheduler.cancelRemainingOpenOrdersAtClose());

        verify(marketHourService).isMarketOpen();
        verify(lifecycleService).cancelOpenOrdersFor(any(LocalDate.class));
    }
}