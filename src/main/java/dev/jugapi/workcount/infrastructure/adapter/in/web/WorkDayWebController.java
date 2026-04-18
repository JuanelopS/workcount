package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.application.port.in.workday.CreateWorkDayUseCase;
import dev.jugapi.workcount.application.port.in.workday.FindWorkDaysByMonthUseCase;
import dev.jugapi.workcount.application.port.in.workday.UpdateWorkDayUseCase;
import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/workday")
public class WorkDayWebController {

    private final FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase;
    private final CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;
    private final CreateWorkDayUseCase createWorkDayUseCase;
    private final UpdateWorkDayUseCase updateWorkDayUseCase;
    private final WorkDayWebMapper mapper;

    public WorkDayWebController(FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase,
                                CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase,
                                CreateWorkDayUseCase createWorkDayUseCase,
                                UpdateWorkDayUseCase updateWorkDayUseCase,
                                WorkDayWebMapper mapper) {
        this.findWorkDaysByMonthUseCase = findWorkDaysByMonthUseCase;
        this.calculateMonthlyBalanceUseCase = calculateMonthlyBalanceUseCase;
        this.createWorkDayUseCase = createWorkDayUseCase;
        this.updateWorkDayUseCase = updateWorkDayUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<WorkDayWebResponse>> getWorkDaysByMonth(
            @RequestParam String month
    ) {
        List<WorkDay> workDays = findWorkDaysByMonthUseCase
                .findWorkDaysByMonth(YearMonth.parse(month));

        return ResponseEntity.ok(mapper.toResponseList(workDays));
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalanceByMonth(
            @RequestParam String month
    ) {
        double hours = calculateMonthlyBalanceUseCase
                .calculateMonthlyBalance(YearMonth.parse(month)).toMinutes() / 60.0;
        return ResponseEntity.ok(hours);
    }

    @PostMapping
    public ResponseEntity<WorkDayWebResponse> postWorkDay(
            @RequestBody WorkDayWebRequest request
    ) {
        WorkDay created = createWorkDayUseCase.createWorkDay(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping
    public ResponseEntity<WorkDayWebResponse> putWorkDay(
            @RequestBody WorkDayWebRequest request
    ) {
        WorkDay updated = updateWorkDayUseCase.updateWorkDay(mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

}
