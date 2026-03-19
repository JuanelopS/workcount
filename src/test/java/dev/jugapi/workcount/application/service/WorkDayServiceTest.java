//package dev.jugapi.workcount.application.service;
//
//import dev.jugapi.workcount.domain.exception.AlreadyWorkDayException;
//import dev.jugapi.workcount.domain.exception.PolicyNotFoundException;
//import dev.jugapi.workcount.domain.model.DailyPolicy;
//import dev.jugapi.workcount.domain.model.WorkMonthTemplate;
//import dev.jugapi.workcount.domain.model.WorkDay;
//import dev.jugapi.workcount.application.port.out.DailyPolicyRepository;
//import dev.jugapi.workcount.application.port.out.WorkMonthTemplateRepository;
//import dev.jugapi.workcount.application.port.out.WorkDayRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.YearMonth;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class WorkDayServiceTest {
//
//    @Mock
//    private WorkDayRepository workDayRepository;
//    @Mock
//    private WorkMonthTemplateRepository workMonthTemplate;
//    @Mock
//    private DailyPolicyRepository dailyPolicyRepo;
//
//    private Duration weeklyTarget;
//    private WorkDayService service;
//    private WorkDay wr;
//
//    public WorkDayServiceTest() {
//    }
//
//    @BeforeEach
//    void setUp() {
//        weeklyTarget = Duration.ofHours(37).plusMinutes(30);
//        service = new WorkDayService(workDayRepository, workMonthTemplate, dailyPolicyRepo,
//                weeklyTarget);
//        wr = WorkDay.of(
//                LocalDate.of(2026, 3, 3),
//                LocalTime.of(7, 30),
//                LocalTime.of(14, 30),
//                null,
//                Duration.ofHours(7));
//    }
//
//    // RegisterWorkDayUseCase
//
//    @Test
//    @DisplayName("The work day should be registered successfully")
//    void shouldRegisterWorkDaySuccessfully() {
//        DailyPolicy dp = new DailyPolicy(
//                wr.getDate().getDayOfWeek(),
//                wr.getStartTime(),
//                wr.getFinishingTime()
//        );
//
//        when(workDayRepository.exists(any())).thenReturn(false);
//        when(dailyPolicyRepo.getPolicyFor(wr.getDate().getDayOfWeek())).thenReturn(Optional.of(dp));
//        service.registerWorkDay(wr);
//        verify(workDayRepository).save(any(WorkDay.class));
//    }
//
//    @Test
//    @DisplayName("Already registered working day error")
//    void shouldThrowExceptionWhenDayIsAlreadyRegistered() {
//        when(workDayRepository.exists(any())).thenReturn(true);
//        assertThrows(AlreadyWorkDayException.class, () -> service.registerWorkDay(wr));
//    }
//
//    @Test
//    @DisplayName("Policy not found for that working day")
//    void shouldThrowExceptionWhenPolicyNotFound() {
//        when(workDayRepository.exists(any())).thenReturn(false);
//        when(dailyPolicyRepo.getPolicyFor(wr.getDate().getDayOfWeek())).thenReturn(Optional.empty());
//        assertThrows(PolicyNotFoundException.class, () -> service.registerWorkDay(wr));
//    }
//
//    // FindByMonthUseCase
//
//    @Test
//    @DisplayName("Returns the list of days recorded in a month")
//    void shouldReturnWorkRegistrationListByMonth() {
//        List<WorkDay> list = List.of(
//                WorkDay.of(
//                        LocalDate.of(2026, 2, 22),
//                        LocalTime.of(7, 30),
//                        LocalTime.of(14, 30),
//                        null,
//                        Duration.ofHours(7)),
//                WorkDay.of(
//                        LocalDate.of(2026, 2, 23),
//                        LocalTime.of(7, 30),
//                        LocalTime.of(14, 30),
//                        null,
//                        Duration.ofHours(7)));
//
//        when(workDayRepository.findByMonth(YearMonth.of(2026,2))).thenReturn(list);
//        List<WorkDay> result = service.findWorkDayByMonth(YearMonth.of(2026, 2));
//        assertEquals(2, result.size());
//    }
//
//    // CalculateMonthlyBalanceUseCase
//
//    @Test
//    @DisplayName("Returns a balance of 136 hours")
//    void shouldReturnMonthlyBalance() {
//        YearMonth month = YearMonth.of(2026, 2);
//        List<WorkDay> list = List.of(
//                WorkDay.of(
//                        LocalDate.of(2026, 2, 22),
//                        LocalTime.of(7, 30),
//                        LocalTime.of(14, 30),
//                        null,
//                        Duration.ofHours(7)),
//                WorkDay.of(
//                        LocalDate.of(2026, 2, 23),
//                        LocalTime.of(7, 30),
//                        LocalTime.of(14, 30),
//                        null,
//                        Duration.ofHours(7)));
//        WorkMonthTemplate template = new WorkMonthTemplate(month, 4);
//        Duration target = template.monthlyTargetHours(this.weeklyTarget);
//
//        when(workDayRepository.findByMonth(month)).thenReturn(list);
//        when(workMonthTemplate.getWorkMonthTemplate(month)).thenReturn(Optional.of(template));
//
//        Duration balance = service.calculateMonthlyBalance(month);
//        Duration expectedBalance = Duration.ofHours(136).negated(); // -136
//        assertEquals(expectedBalance, balance,
//                "El balance debería ser la suma de horas menos el objetivo mensual");
//    }
//}
