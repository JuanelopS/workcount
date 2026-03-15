package dev.jugapi.workcount.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jugapi.workcount.application.port.in.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.application.port.in.DeleteWorkDayUseCase;
import dev.jugapi.workcount.application.port.in.FindByMonthUseCase;
import dev.jugapi.workcount.application.port.in.RegisterWorkDayUseCase;
import dev.jugapi.workcount.domain.model.WorkDay;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkRegistrationWebController.class)
@Import(WorkRegistrationWebMapper.class)
public class WorkDayWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterWorkDayUseCase registerWorkDayUseCase;

    @MockitoBean
    private FindByMonthUseCase findByMonthUseCase;

    @MockitoBean
    private CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;

    @MockitoBean
    private DeleteWorkDayUseCase deleteWorkDayUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 201 Created")
    void shouldReturn201WhenRegistrationIsValid() throws Exception {
        WorkDay request = WorkDay.of(
                LocalDate.of(2026, 3, 3),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                null,
                Duration.ofHours(8)
        );

        when(registerWorkDayUseCase.registerWorkDay(any())).thenReturn(request);

        mockMvc.perform(post("/api/work-registrations/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.validatedHours").value("8.0"));

        verify(registerWorkDayUseCase).registerWorkDay(any());
    }

    @Test
    @DisplayName("Should return a list with two records with data")
    void shouldReturnAListOfRecordsWhenMonthIsRequested() throws Exception {
        WorkDay wr1 = WorkDay.of(
                LocalDate.of(2026, 3, 3),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                null,
                Duration.ofHours(8)
        );
        WorkDay wr2 = WorkDay.of(
                LocalDate.of(2026, 3, 4),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                null,
                Duration.ofHours(8)
        );

        List<WorkDay> list = List.of(wr1, wr2);

        when(findByMonthUseCase.findByMonth(any())).thenReturn(list);

        mockMvc.perform(get("/api/work-registrations/search/month/2026-03")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workingDay").value("2026-03-03"))
                .andExpect(jsonPath("$[0].validatedHours").value(8.0))
                .andExpect(jsonPath("$[1].startTime").value("08:00:00"));

    }

    @Test
    @DisplayName("Should return the monthly balance as a decimal number of hours")
    void shouldReturnDecimalBalanceWhenMonthIsRequested() throws Exception {
        when(calculateMonthlyBalanceUseCase.calculateMonthlyBalance(any()))
                .thenReturn(Duration.ofMinutes(7230));

        mockMvc.perform(get("/api/work-registrations/balance/2026-03")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(120.5));
    }

    @Test
    @DisplayName("Should return http code 204 No Content")
    void shouldReturn204WhenRegistrationIsDeleted() throws Exception {
        mockMvc.perform(delete("/api/work-registrations/delete/{date}",
                "2026-03-08")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deleteWorkDayUseCase).deleteByWorkingDay(LocalDate.of(2026, 3, 8));
    }
}
