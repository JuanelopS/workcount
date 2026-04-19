package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebController;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkDayWebController.class)
@Import(WorkDayWebMapper.class)
public class WorkDayWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindWorkDayByDateUseCase findWorkDayByDateUseCase;

    @MockitoBean
    private FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase;

    @MockitoBean
    private FindWorkDaysByDateRangeUseCase findWorkDaysByDateRangeUseCase;

    @MockitoBean
    private CreateWorkDayUseCase createWorkDayUseCase;

    @MockitoBean
    private UpdateWorkDayUseCase updateWorkDayUseCase;

    @MockitoBean
    private DeleteWorkDayUseCase deleteWorkDayUseCase;

    @MockitoBean
    private CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;

    private WorkDay workDay;

    @BeforeEach
    void setUp() {
        workDay = WorkDay.of(
                LocalDate.of(2023, 10, 2),
                List.of(
                        new Clocking(LocalTime.of(8, 0), ClockingType.IN),
                        new Clocking(LocalTime.of(16, 0), ClockingType.OUT)
                ),
                Duration.ofHours(8)
        );
    }

    @Test
    @DisplayName("GET /api/workdays?month=2023-10 returns 200 with list")
    void getWorkDaysByMonthReturns200() throws Exception {
        when(findWorkDaysByMonthUseCase.findWorkDaysByMonth(YearMonth.of(2023, 10)))
                .thenReturn(List.of(workDay));

        mockMvc.perform(get("/api/workdays").param("month", "2023-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workingDay").value("2023-10-02"))
                .andExpect(jsonPath("$[0].validatedHours").value(8.0));


    }
}

