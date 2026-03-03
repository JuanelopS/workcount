package dev.jugapi.workcount.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jugapi.workcount.application.port.in.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.application.port.in.FindByMonthUseCase;
import dev.jugapi.workcount.application.port.in.RegisterWorkDayUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkRegistrationWebController.class)
@Import(WorkRegistrationWebMapper.class)
public class WorkRegistrationWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterWorkDayUseCase registerWorkDayUseCase;

    @MockitoBean
    private FindByMonthUseCase findByMonthUseCase;

    @MockitoBean
    private CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 201 Created")
    void shouldReturn201WhenRegistrationIsValid() throws Exception {
        WorkRegistrationWebRequest request = new WorkRegistrationWebRequest(
                LocalDate.of(2026, 3, 3),
                LocalTime.of(8, 0),
                LocalTime.of(16,0),
                null
        );

        mockMvc.perform(post("/api/work-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(registerWorkDayUseCase).registerWorkDay(any());
    }

}
