package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClockingServiceTest {

    @Mock
    private WorkDayRepository workDayRepository;

    @Mock
    private DailyPolicyRepository dailyPolicyRepository;

    @InjectMocks
    private ClockingService clockingService;

    @Test
    void clockInTest() {
        LocalDate today = LocalDate.now();
        when(workDayRepository.findByDate(today)).thenReturn(Optional.empty());

        DailyPolicy policy = new DailyPolicy(today.getDayOfWeek(), LocalTime.of(7, 30),
                LocalTime.of(19, 30));
        when(dailyPolicyRepository.getPolicyFor(today.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService.clockIn();

        assertNotNull(result);
        assertEquals(1, result.getClockingList().size());
        assertEquals(ClockingType.IN, result.getClockingList().get(0).type());
        verify(workDayRepository).save(any(WorkDay.class));
    }

}
