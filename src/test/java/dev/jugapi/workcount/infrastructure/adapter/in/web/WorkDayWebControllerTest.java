package dev.jugapi.workcount.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebController;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebMapper;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkDayWebController.class)
@Import(WorkDayWebMapper.class)
public class WorkDayWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private WorkDayWebRequest request;

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

        request = new WorkDayWebRequest(
                LocalDate.of(2023, 10, 2),
                List.of(
                        new Clocking(LocalTime.of(8, 0), ClockingType.IN),
                        new Clocking(LocalTime.of(16, 0), ClockingType.OUT)
                )
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

    @Test
    @DisplayName("GET /api/workdays/2023-10-02 returns 200 with workday")
    void getWorkDayByDateTest() throws Exception {
        when(findWorkDayByDateUseCase
                .findWorkDayByDate(LocalDate.of(2023, 10, 2)))
                .thenReturn(workDay);

        mockMvc.perform(get("/api/workdays/2023-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workingDay").value("2023-10-02"))
                .andExpect(jsonPath("$.validatedHours").value(8.0))
                .andExpect(jsonPath("$.clockingList.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/workdays/range?from=2023-10-01&to=2023-10-31 returns 200 with list")
    void getWorkDaysByRangeTest() throws Exception {
        when(findWorkDaysByDateRangeUseCase.findWorkDaysByDateRange(
                LocalDate.of(2023, 10, 1),
                LocalDate.of(2023, 10, 31)
        )).thenReturn(List.of(workDay));

        mockMvc.perform(get("/api/workdays/range")
                        .param("from", "2023-10-01")
                        .param("to", "2023-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workingDay").value("2023-10-02"))
                .andExpect(jsonPath("$[0].validatedHours").value(8.0))
                .andExpect(jsonPath("$[0].clockingList.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/workdays/balance?month=2023-10 returns 200 with a double value")
    void getBalanceByMonthTest() throws Exception {
        when(calculateMonthlyBalanceUseCase
                .calculateMonthlyBalance(YearMonth.of(2023, 10)))
                .thenReturn(Duration.ofHours(8));

        mockMvc.perform(get("/api/workdays/balance")
                        .param("month", "2023-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(8.0));
    }

    @Test
    @DisplayName("DELETE /api/workdays/2023-10-02 returns 204 and deletes the workday")
    void deleteWorkDayTest() throws Exception {
        doNothing().when(deleteWorkDayUseCase)
                .deleteWorkDay(LocalDate.of(2023, 10, 2));

        mockMvc.perform(delete("/api/workdays/2023-10-02"))
                .andExpect(status().isNoContent());

        verify(deleteWorkDayUseCase)
                .deleteWorkDay(LocalDate.of(2023, 10, 2));
    }

    @Test
    @DisplayName("POST /api/workdays creates a new workday and returns 201")
    void postWorkDayTest() throws Exception {
        when(createWorkDayUseCase.createWorkDay(any(WorkDay.class))).thenReturn(workDay);

        mockMvc.perform(post("/api/workdays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workingDay").value("2023-10-02"))
                .andExpect(jsonPath("$.validatedHours").value(8.0))
                .andExpect(jsonPath("$.clockingList.length()").value(2));

        verify(createWorkDayUseCase).createWorkDay(any(WorkDay.class));
    }

    @Test
    @DisplayName("PUT /api/workdays updates an existing workday and returns 200")
    void putWorkDayTest() throws Exception {
        when(updateWorkDayUseCase.updateWorkDay(any(WorkDay.class))).thenReturn(workDay);

        mockMvc.perform(put("/api/workdays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workingDay").value("2023-10-02"))
                .andExpect(jsonPath("$.validatedHours").value(8.0))
                .andExpect(jsonPath("$.clockingList.length()").value(2));

        verify(updateWorkDayUseCase).updateWorkDay(any(WorkDay.class));
    }
}
