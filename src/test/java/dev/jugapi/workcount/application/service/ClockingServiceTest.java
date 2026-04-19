package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.out.policy.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.workday.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.InexistentClockingException;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.exception.InvalidClockingSequenceException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.clocking.Clocking;
import dev.jugapi.workcount.domain.model.clocking.ClockingType;
import dev.jugapi.workcount.domain.model.policy.DailyPolicy;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
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

    private ClockingService clockingService;

    private LocalDate day;
    private WorkDay workDay;
    private DailyPolicy policy;
    private Clock fixedClock;

    @BeforeEach
    void init() {
        day = LocalDate.of(2026, 4, 19);
        fixedClock = Clock.fixed(Instant.parse("2026-04-19T10:00:00Z"), ZoneOffset.UTC);
        clockingService = new ClockingService(workDayRepository, dailyPolicyRepository, fixedClock);
        workDay = WorkDay.create(day);
        workDay.addClocking(new Clocking(LocalTime.of(9, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        policy = new DailyPolicy(day.getDayOfWeek(), LocalTime.of(7, 30),
                LocalTime.of(19, 30));
    }

    @Test
    @DisplayName("clockIn: should create new WorkDay and clock in successfully")
    void clockInTestAndWorkDayNotExists() {
        Mockito.lenient().when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());

        Mockito.lenient().when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
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
        workDay.getClockingList().clear(); // to get empty workDay

        Mockito.lenient().when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        Mockito.lenient().when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService.clockIn();

        assertNotNull(result);
        assertEquals(1, result.getClockingList().size());
        assertEquals(ClockingType.IN, result.getClockingList().get(0).type());
        verify(workDayRepository).save(result);
    }

    @Test
    @DisplayName("clockIn: policy not found scenario throws exception")
    void clockInTestPolicyNotFound() {
        Mockito.lenient().when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());

        Mockito.lenient().when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class,
                () -> clockingService.clockIn());
    }

    @Test
    @DisplayName("createClocking: workday exists -> should create clocking")
    void createClockingTestInExistentWorkDay() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService
                .createClocking(day, LocalTime.of(15, 0), ClockingType.IN);

        assertNotNull(result);
        assertEquals(3, result.getClockingList().size());
        assertEquals(ClockingType.IN, result.getClockingList().get(0).type());
        verify(workDayRepository).save(any(WorkDay.class));
    }

    @Test
    @DisplayName("createClocking: workday not exist -> should create workDay and clocking")
    void createClockingInInexistentWorkday() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());

        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService
                .createClocking(day, LocalTime.of(10, 0), ClockingType.IN);

        assertNotNull(result);
        assertEquals(1, result.getClockingList().size());
        assertEquals(ClockingType.IN, result.getClockingList().get(0).type());
        verify(workDayRepository).save(any(WorkDay.class));
    }

    @Test
    @DisplayName("createClocking: policy not found scenario throws exception")
    void createClockingTestPolicyNotFound() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());
        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class,
                () -> clockingService.createClocking(day, LocalTime.of(10, 0),
                        ClockingType.IN));
    }

    @Test
    @DisplayName("updateClocking: should update a clocking successfully")
    void updateClockingTest() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        WorkDay result = clockingService.updateClocking(day, LocalTime.of(9, 0),
                LocalTime.of(8, 30), ClockingType.IN);

        assertNotNull(result);
        assertEquals(2, result.getClockingList().size());
        assertEquals(LocalTime.of(8, 30), result.getClockingList().get(0).time());
        verify(workDayRepository).save(any(WorkDay.class));
    }

    @Test
    @DisplayName("updateClocking: should throw exception when workday not found")
    void updateClockingTestWhenWorkDayNotFound() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());

        assertThrows(InexistentWorkDayException.class, () -> clockingService.updateClocking(day,
                LocalTime.of(9, 0), LocalTime.of(8, 30), ClockingType.IN));
    }

    @Test
    @DisplayName("updateClocking: should throw exception when clocking not found")
    void updateClockingTestWhenClockingNotFound() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        workDay.getClockingList().clear(); // to get empty clocking list

        assertThrows(InexistentClockingException.class, () -> clockingService.updateClocking(day,
                LocalTime.of(9, 0), LocalTime.of(8, 30), ClockingType.IN));
    }

    @Test
    @DisplayName("updateClocking: invalid sequence should throw exception")
    void updateClockingInvalidSequenceTest() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        assertThrows(InvalidClockingSequenceException.class, () ->
                clockingService.updateClocking(day, LocalTime.of(9, 0),
                        LocalTime.of(8, 30), ClockingType.OUT));
    }

    @Test
    @DisplayName("deleteClockIn: should delete a clocking successfully")
    void deleteClockingTest() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class)))
                .thenAnswer(i -> i.getArgument(0));

        clockingService.deleteClocking(day, LocalTime.of(9, 0));

        assertEquals(1, workDay.getClockingList().size());
        assertEquals(ClockingType.IN, workDay.getClockingList().get(0).type());
        verify(workDayRepository).save(any(WorkDay.class));
    }

    @Test
    @DisplayName("deleteClockIn: should throw exception when workday not found")
    void deleteClockInTestWhenWorkDayNotFound() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());

        assertThrows(InexistentWorkDayException.class,
                () -> clockingService.deleteClocking(day, LocalTime.of(9, 0)));
    }

    @Test
    @DisplayName("deleteClockIn: should throw exception when clocking not found")
    void deleteClockInTestWhenClockingNotFound() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        workDay.getClockingList().clear(); // to get empty clocking list

        assertThrows(InexistentClockingException.class,
                () -> clockingService.deleteClocking(day, LocalTime.of(9, 0)));
    }
}
