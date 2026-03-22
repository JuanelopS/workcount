package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.application.port.out.WorkDayRepository;
import dev.jugapi.workcount.domain.exception.AlreadyWorkDayException;
import dev.jugapi.workcount.domain.exception.InexistentWorkDayException;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkDayServiceTest {

    @Mock
    private WorkDayRepository workDayRepository;

    @Mock
    private DailyPolicyRepository dailyPolicyRepository;

    @InjectMocks
    private WorkDayService workDayService;

    public LocalDate today;
    public WorkDay workDay;
    public DailyPolicy policy;

    @BeforeEach
    void init() {
        today = LocalDate.now();
        workDay = WorkDay.create(today);
        workDay.addClocking(new Clocking(LocalTime.of(9, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        workDay.addClocking(new Clocking(LocalTime.of(15, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(20, 0), ClockingType.OUT));

        policy = new DailyPolicy(today.getDayOfWeek(),
                LocalTime.of(7, 30), LocalTime.of(19, 30));
    }

    @Test
    @DisplayName("createWorkDay: it should create a workDay successfully")
    void createWorkDayTest() {
        when(workDayRepository.exists(today)).thenReturn(false);

        when(dailyPolicyRepository.getPolicyFor(today.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class))).thenReturn(workDay);

        WorkDay result = workDayService.createWorkDay(workDay);

        verify(workDayRepository).save(workDay);
        assertEquals(workDay, result);
    }

    @Test
    @DisplayName("createWorkDay: it should throw an exception because it already exists on that date")
    void createWorkDayWhenAlreadyExists() {
        when(workDayRepository.exists(today)).thenReturn(true);

        assertThrows(AlreadyWorkDayException.class, () -> {
            workDayService.createWorkDay(workDay);
        });
        verify(workDayRepository, never()).save(workDay);
    }

    @Test
    @DisplayName("updateWorkDay: it should update workDay successfully")
    void updateWorkDayTest() {
        WorkDay updatedWorkDay = WorkDay.of(today, List.of(), Duration.ZERO);

        when(workDayRepository.exists(today)).thenReturn(true);

        when(dailyPolicyRepository.getPolicyFor(today.getDayOfWeek()))
                .thenReturn(Optional.of(policy));

        when(workDayRepository.save(any(WorkDay.class))).thenReturn(updatedWorkDay);

        WorkDay result = workDayService.updateWorkDay(updatedWorkDay);

        verify(workDayRepository).save(updatedWorkDay);
        assertEquals(updatedWorkDay, result);
    }

    @Test
    @DisplayName("updateWorkDay: it should throw an exception because workDay doesn't exists")
    void updateWorkDayWhenNoExists() {
        when(workDayRepository.exists(today)).thenReturn(false);

        assertThrows(InexistentWorkDayException.class, () -> {
            workDayService.updateWorkDay(workDay);
        });
        verify(workDayRepository, never()).save(workDay);
    }

    @Test
    @DisplayName("deleteWorkDay: it should delete workDay successfully")
    void deleteWorkDayTest() {
        when(workDayRepository.exists(today)).thenReturn(true);

        doNothing().when(workDayRepository).delete(today);

        workDayService.deleteWorkDay(today);
        verify(workDayRepository).delete(today);
    }

    @Test
    @DisplayName("deleteWorkDay: it should throw an exception because workDay doesn't exists")
    void deleteWorkDayWhenNoExists() {
        when(workDayRepository.exists(today)).thenReturn(false);

        assertThrows(InexistentWorkDayException.class, () -> {
            workDayService.deleteWorkDay(today);
        });
        verify(workDayRepository, never()).delete(today);
    }

    @Test
    @DisplayName("getCurrentStatus: it should return current status successfully")
    void getCurrentStatusTest() {
        when(workDayRepository.exists(today)).thenReturn(true);

        when(workDayRepository.findByDate(today)).thenReturn(Optional.of(workDay));

        Optional<ClockingType> result = workDayService.getCurrentStatus();

        assertEquals(Optional.of(ClockingType.OUT), result);
    }

    @Test
    @DisplayName("calculateNetTimeWorked: it should calculate successfully net worked hours")
    void calculateNetTimeWorkedTest() {

    }
}
