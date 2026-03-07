package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.domain.exception.AlreadyRegisteredDayException;
import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
import dev.jugapi.workcount.domain.model.DailyPolicy;
import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import dev.jugapi.workcount.domain.port.out.DailyPolicyRepository;
import dev.jugapi.workcount.domain.port.out.WorkMonthTemplateRepository;
import dev.jugapi.workcount.domain.port.out.WorkRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkRegistrationServiceTest {

    @Mock
    private WorkRegistrationRepository workRegistrationRepository;
    @Mock
    private WorkMonthTemplateRepository workMonthTemplate;
    @Mock
    private DailyPolicyRepository dailyPolicyRepo;

    private Duration weeklyTarget;
    private WorkRegistrationService service;
    private WorkRegistration wr;

    public WorkRegistrationServiceTest() {
    }

    @BeforeEach
    void setUp() {
        weeklyTarget = Duration.ofHours(37).plusMinutes(30);
        service = new WorkRegistrationService(workRegistrationRepository, workMonthTemplate, dailyPolicyRepo,
                weeklyTarget);
        wr = new WorkRegistration(
                LocalDate.of(2026, 3, 3),
                LocalTime.of(7, 30),
                LocalTime.of(14, 30),
                null,
                Duration.ofHours(7));
    }

    // RegisterWorkDayUseCase

    @Test
    @DisplayName("The work day should be registered successfully")
    void shouldRegisterWorkDaySuccessfully() {
        DailyPolicy dp = new DailyPolicy(
                wr.getWorkingDay().getDayOfWeek(),
                wr.getStartTime(),
                wr.getFinishingTime()
        );

        when(workRegistrationRepository.existsByWorkingDay(any())).thenReturn(false);
        when(dailyPolicyRepo.getPolicyFor(wr.getWorkingDay().getDayOfWeek())).thenReturn(Optional.of(dp));
        service.registerWorkDay(wr);
        verify(workRegistrationRepository).save(any(WorkRegistration.class));
    }

    @Test
    @DisplayName("Already registered working day error")
    void shouldThrowExceptionWhenDayIsAlreadyRegistered() {
        when(workRegistrationRepository.existsByWorkingDay(any())).thenReturn(true);
        assertThrows(AlreadyRegisteredDayException.class, () -> service.registerWorkDay(wr));
    }

    @Test
    @DisplayName("Policy not found for that working day")
    void shouldThrowExceptionWhenPolicyNotFound() {
        when(workRegistrationRepository.existsByWorkingDay(any())).thenReturn(false);
        when(dailyPolicyRepo.getPolicyFor(wr.getWorkingDay().getDayOfWeek())).thenReturn(Optional.empty());
        assertThrows(PolicyNotFoundException.class, () -> service.registerWorkDay(wr));
    }

    // FindByMonthUseCase

    @Test
    @DisplayName("Returns the list of days recorded in a month")
    void shouldReturnWorkRegistrationListByMonth() {
        List<WorkRegistration> list = List.of(
                new WorkRegistration(
                        LocalDate.of(2026, 2, 22),
                        LocalTime.of(7, 30),
                        LocalTime.of(14, 30),
                        null,
                        Duration.ofHours(7)),
                new WorkRegistration(
                        LocalDate.of(2026, 2, 23),
                        LocalTime.of(7, 30),
                        LocalTime.of(14, 30),
                        null,
                        Duration.ofHours(7)));

        when(workRegistrationRepository.findByMonth(YearMonth.of(2026,2))).thenReturn(list);
        List<WorkRegistration> result = service.findByMonth(YearMonth.of(2026, 2));
        assertEquals(2, result.size());
    }

    // CalculateMonthlyBalanceUseCase

    @Test
    @DisplayName("Returns a balance of 136 hours")
    void shouldReturnMonthlyBalance() {
        YearMonth month = YearMonth.of(2026, 2);
        List<WorkRegistration> list = List.of(
                new WorkRegistration(
                        LocalDate.of(2026, 2, 22),
                        LocalTime.of(7, 30),
                        LocalTime.of(14, 30),
                        null,
                        Duration.ofHours(7)),
                new WorkRegistration(
                        LocalDate.of(2026, 2, 23),
                        LocalTime.of(7, 30),
                        LocalTime.of(14, 30),
                        null,
                        Duration.ofHours(7)));
        WorkMonthTemplate template = new WorkMonthTemplate(month, 4);
        Duration target = template.monthlyTargetHours(this.weeklyTarget);

        when(workRegistrationRepository.findByMonth(month)).thenReturn(list);
        when(workMonthTemplate.getWorkMonthTemplate(month)).thenReturn(Optional.of(template));

        Duration balance = service.calculateMonthlyBalance(month);
        Duration expectedBalance = Duration.ofHours(136).negated(); // -136
        assertEquals(expectedBalance, balance,
                "El balance debería ser la suma de horas menos el objetivo mensual");
    }
}
