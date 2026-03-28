package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    public LocalDate today;
    public WorkDay workDay;
    public DailyPolicy policy;

    @BeforeEach
    void init() {
        today = LocalDate.now();
        workDay = WorkDay.create(today);
        workDay.addClocking(new Clocking(LocalTime.of(9, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        policy = new DailyPolicy(today.getDayOfWeek(),
                LocalTime.of(7, 30), LocalTime.of(19, 30));
    }

    @Test
    @DisplayName("clockIn: should create new WorkDay and clock in successfully")
    void clockInTestAndWorkDayNotExists() {
        when(workDayRepository.findByDate(today)).thenReturn(Optional.empty());

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

    @Test
    @DisplayName("clockIn: should add clockIn to existent workDay")
    void clockInTestAndWorkDayExists() {
        when(workDayRepository.findByDate(today)).thenReturn(Optional.of(workDay));

        when(dailyPolicyRepository.getPolicyFor(today.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService.clockIn();

        assertNotNull(result);
        assertEquals(3, result.getClockingList().size());
        assertEquals(ClockingType.IN, result.getClockingList().get(2).type());
        verify(workDayRepository).save(result);
    }

    @Test
    @DisplayName("clockIn: policy not found scenario throws exception")
    void clockInTestPolicyNotFound() {
        when(workDayRepository.findByDate(today)).thenReturn(Optional.empty());

        when(dailyPolicyRepository.getPolicyFor(today.getDayOfWeek()))
                .thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class, () -> clockingService.clockIn());
    }

    @Test
    void createClockingTest() {
        when(workDayRepository.findByDate(today)).thenReturn(Optional.of(workDay));

        when(dailyPolicyRepository.getPolicyFor(today.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService.createClocking(today, LocalTime.of(15, 0),
                ClockingType.IN);

        assertNotNull(result);
        assertEquals(3, result.getClockingList().size());
        assertEquals(ClockingType.IN, result.getClockingList().get(0).type());
        verify(workDayRepository).save(any(WorkDay.class));
    }
}
