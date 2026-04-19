package dev.jugapi.workcount.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jugapi.workcount.application.port.in.clocking.ClockInUseCase;
import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.domain.model.Clocking;
import dev.jugapi.workcount.domain.model.ClockingType;
import dev.jugapi.workcount.domain.model.WorkDay;
import dev.jugapi.workcount.infrastructure.adapter.in.web.clocking.ClockingWebController;
import dev.jugapi.workcount.infrastructure.adapter.in.web.clocking.CreateClockingWebRequest;
import dev.jugapi.workcount.infrastructure.adapter.in.web.clocking.UpdateClockingWebRequest;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebMapper;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClockingWebController.class)
@Import(WorkDayWebMapper.class)
public class ClockingWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClockInUseCase clockInUseCase;

    @MockitoBean
    private CreateClockingUseCase createClockingUseCase;

    @MockitoBean
    private UpdateClockingUseCase updateClockingUseCase;

    @MockitoBean
    private DeleteClockingUseCase deleteClockingUseCase;

    private WorkDay workDay;

    @BeforeEach
    void setUp() {
        workDay = WorkDay.of(
                LocalDate.of(2023, 10, 2),
                List.of(
                        new Clocking(LocalTime.of(8, 0), ClockingType.IN),
                        new Clocking(LocalTime.of(14, 0), ClockingType.OUT)
                ),
                Duration.ofHours(8)
        );
    }

    @Test
    @DisplayName("POST /api/clockings/clock-in returns 201 with updated workday")
    void clockInTest() throws Exception {
        when(clockInUseCase.clockIn()).thenReturn(workDay);

        mockMvc.perform(post("/api/clockings/clock-in"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workingDay")
                        .value("2023-10-02"))
                .andExpect(jsonPath("$.clockingList[0].time")
                        .value("08:00:00"))
                .andExpect(jsonPath("$.clockingList[0].type")
                        .value("IN"))
                .andExpect(jsonPath("$.clockingList[1].time")
                        .value("14:00:00"))
                .andExpect(jsonPath("$.clockingList[1].type")
                        .value("OUT"))
                .andExpect(jsonPath("$.validatedHours")
                        .value(8.0));

        verify(clockInUseCase, times(1)).clockIn();
    }

    @Test
    @DisplayName("POST /api/clockings returns 201 with created clocking")
    void createClockingTest() throws Exception {
        CreateClockingWebRequest request = new CreateClockingWebRequest(
                LocalDate.of(2023, 10, 2),
                LocalTime.of(15, 0),
                ClockingType.IN);

        when(createClockingUseCase.createClocking(
                LocalDate.of(2023, 10, 2),
                LocalTime.of(15, 0),
                ClockingType.IN
        )).thenReturn(workDay);

        mockMvc.perform(post("/api/clockings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workingDay")
                        .value("2023-10-02"))
                .andExpect(jsonPath("$.clockingList.length()")
                        .value(2))
                .andExpect(jsonPath("$.validatedHours")
                        .value(8.0));


        verify(createClockingUseCase, times(1)).createClocking(
                LocalDate.of(2023, 10, 2),
                LocalTime.of(15, 0),
                ClockingType.IN
        );
    }

    @Test
    @DisplayName("POST /api/clockings with invalid type returns 400 and does not call use case")
    void createClockingInvalidTypeReturnsBadRequest() throws Exception {
        String invalidJson = """
                {
                  "date": "2023-10-02",
                  "time": "15:00:00",
                  "type": "INVALID_TYPE"
                }
                """;

        mockMvc.perform(post("/api/clockings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createClockingUseCase);
    }

    @Test
    @DisplayName("PUT /api/clockings returns 200 with updated workday")
    void updateClockingTest() throws Exception {
        UpdateClockingWebRequest request = new UpdateClockingWebRequest(
                LocalDate.of(2023, 10, 2),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                ClockingType.OUT
        );

        when(updateClockingUseCase.updateClocking(
                request.date(),
                request.originalTime(),
                request.newTime(),
                request.type()
        )).thenReturn(workDay);

        mockMvc.perform(put("/api/clockings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workingDay")
                        .value("2023-10-02"));

        verify(updateClockingUseCase, times(1)).updateClocking(
                request.date(),
                request.originalTime(),
                request.newTime(),
                request.type()
        );
    }

    @Test
    @DisplayName("DELETE /api/clockings/{date}/{time} returns 204")
    void deleteClockingTest() throws Exception {
        doNothing().when(deleteClockingUseCase).deleteClocking(
                LocalDate.of(2023, 10, 2),
                LocalTime.of(14, 0)
        );

        mockMvc.perform(delete("/api/clockings/2023-10-02/14:00"))
                .andExpect(status().isNoContent());

        verify(deleteClockingUseCase).deleteClocking(
                LocalDate.of(2023, 10, 2),
                LocalTime.of(14, 0)
        );
    }

    @Test
    @DisplayName("DELETE /api/clockings/{date}/{time} with invalid time format returns 400")
    void deleteClockingInvalidTimeFormatReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/clockings/2023-10-02/25-00"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(deleteClockingUseCase);
    }
}
