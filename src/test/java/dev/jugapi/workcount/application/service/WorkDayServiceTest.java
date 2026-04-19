package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.out.policy.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.workday.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.AlreadyWorkDayException;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
import dev.jugapi.workcount.domain.model.clocking.Clocking;
import dev.jugapi.workcount.domain.model.clocking.ClockingType;
import dev.jugapi.workcount.domain.model.policy.DailyPolicy;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkDayServiceTest {

    @Mock
    private WorkDayRepository workDayRepository;

    @Mock
    private DailyPolicyRepository dailyPolicyRepository;

    @InjectMocks
    private WorkDayService workDayService;

    private LocalDate day;
    private WorkDay workDay;
    private DailyPolicy policy;

    @BeforeEach
    void init() {
        day = LocalDate.of(2026, 4, 16);
        workDay = WorkDay.create(day);
        workDay.addClocking(new Clocking(LocalTime.of(9, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        workDay.addClocking(new Clocking(LocalTime.of(15, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(20, 0), ClockingType.OUT));

        policy = new DailyPolicy(day.getDayOfWeek(),
                LocalTime.of(7, 30), LocalTime.of(19, 30));
    }

    @Test
    @DisplayName("createWorkDay: it should create a workDay successfully")
    void createWorkDayTest() {
        when(workDayRepository.exists(day)).thenReturn(false);

        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class))).thenReturn(workDay);

        WorkDay result = workDayService.createWorkDay(workDay);

        verify(workDayRepository).save(workDay);
        assertEquals(workDay, result);
    }

    @Test
    @DisplayName("createWorkDay: it should throw an exception because it already exists on that date")
    void createWorkDayWhenAlreadyExists() {
        when(workDayRepository.exists(day)).thenReturn(true);

        assertThrows(AlreadyWorkDayException.class, () -> {
            workDayService.createWorkDay(workDay);
        });
        verify(workDayRepository, never()).save(workDay);
    }

    @Test
    @DisplayName("updateWorkDay: it should update workDay successfully")
    void updateWorkDayTest() {
        WorkDay updatedWorkDay = WorkDay.of(day, List.of(), Duration.ZERO);

        when(workDayRepository.exists(day)).thenReturn(true);

        when(dailyPolicyRepository.getPolicyFor(day.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class))).thenReturn(updatedWorkDay);

        WorkDay result = workDayService.updateWorkDay(updatedWorkDay);

        verify(workDayRepository).save(updatedWorkDay);
        assertEquals(updatedWorkDay, result);
    }

    @Test
    @DisplayName("updateWorkDay: it should throw an exception because workDay doesn't exists")
    void updateWorkDayWhenNoExists() {
        when(workDayRepository.exists(day)).thenReturn(false);

        assertThrows(InexistentWorkDayException.class, () -> {
            workDayService.updateWorkDay(workDay);
        });
        verify(workDayRepository, never()).save(workDay);
    }

    @Test
    @DisplayName("deleteWorkDay: it should delete workDay successfully")
    void deleteWorkDayTest() {
        when(workDayRepository.exists(day)).thenReturn(true);

        doNothing().when(workDayRepository).delete(day);

        workDayService.deleteWorkDay(day);
        verify(workDayRepository).delete(day);
    }

    @Test
    @DisplayName("deleteWorkDay: it should throw an exception because workDay doesn't exists")
    void deleteWorkDayWhenNoExists() {
        when(workDayRepository.exists(day)).thenReturn(false);

        assertThrows(InexistentWorkDayException.class, () -> {
            workDayService.deleteWorkDay(day);
        });
        verify(workDayRepository, never()).delete(day);
    }

    @Test
    @DisplayName("findWorkDaysByDate: it should return a existent workday")
    void findWorkDayByDateTest() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.of(workDay));

        WorkDay result = workDayService.findWorkDayByDate(day);

        assertEquals(workDay, result);
        assertNotNull(result);
    }

    @Test
    @DisplayName("findWorkDaysByDate: it should throw an exception because workDay doesn't exists")
    void findWorkDayByDateWhenNoExists() {
        when(workDayRepository.findByDate(day)).thenReturn(Optional.empty());

        assertThrows(InexistentWorkDayException.class, () -> workDayService.findWorkDayByDate(day));
    }

    @Test
    @DisplayName("findWorkDaysByDateRange: it should return a list of workdays")
    void findWorkDaysByDateRangeTest() {
        WorkDay anotherWorkDay = WorkDay.create(LocalDate.of(2026, 4, 17));
        anotherWorkDay.addClocking(new Clocking(LocalTime.of(9, 0), ClockingType.IN));
        anotherWorkDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        when(workDayRepository.findByDateBetween(day, day.plusDays(1)))
                .thenReturn(List.of(workDay, anotherWorkDay));

        List<WorkDay> result = workDayService.findWorkDaysByDateRange(day,
                day.plusDays(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(workDay));
        assertTrue(result.contains(anotherWorkDay));
    }

    @Test
    @DisplayName("findWorkDaysByDateRange: it should throw an IllegalArgumentException because " +
            "start date is after end date")
    void findWorkDaysByDateRangeTestWhenStartDateAfterEndDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            workDayService.findWorkDaysByDateRange(day.plusDays(1), day);
        });
    }

    @Test
    @DisplayName("findWorkDaysByDateRange: it should return an empty list when no workdays are " +
            "found")
    void findWorkDayByDateRangeTestWhenNoWorkDaysAreFound() {
        when(workDayRepository.findByDateBetween(day, day.plusDays(1)))
                .thenReturn(List.of());

        List<WorkDay> result = workDayService
                .findWorkDaysByDateRange(day, day.plusDays(1));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCurrentStatus: it should return current status successfully")
    void getCurrentStatusTest() {
        LocalDate today = LocalDate.now();
        when(workDayRepository.exists(today)).thenReturn(true);
        when(workDayRepository.findByDate(today)).thenReturn(Optional.of(workDay));

        Optional<ClockingType> result = workDayService.getCurrentStatus();

        assertEquals(Optional.of(ClockingType.OUT), result);
    }

    @Test
    @DisplayName("getCurrentStatus: it should throw an exception because workDay doesn't exists")
    void getCurrentStatusWhenNoWorkDay() {
        LocalDate today = LocalDate.now();

        when(workDayRepository.exists(today)).thenReturn(false);

        assertThrows(InexistentWorkDayException.class, () -> workDayService.getCurrentStatus());
        verify(workDayRepository, never()).findByDate(any());
    }
}
