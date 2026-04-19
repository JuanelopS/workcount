package dev.jugapi.workcount.infrastructure.adapter.in.web.workday;

import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/workdays")
public class WorkDayWebController {

    private final FindWorkDayByDateUseCase findWorkDayByDateUseCase;
    private final FindWorkDaysByDateRangeUseCase findWorkDaysByDateRangeUseCase;
    private final FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase;
    private final CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;
    private final CreateWorkDayUseCase createWorkDayUseCase;
    private final UpdateWorkDayUseCase updateWorkDayUseCase;
    private final DeleteWorkDayUseCase deleteWorkDayUseCase;
    private final WorkDayWebMapper mapper;

    public WorkDayWebController(FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase,
                                FindWorkDayByDateUseCase findWorkDayByDateUseCase,
                                FindWorkDaysByDateRangeUseCase findWorkDaysByDateRangeUseCase,
                                CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase,
                                CreateWorkDayUseCase createWorkDayUseCase,
                                UpdateWorkDayUseCase updateWorkDayUseCase,
                                DeleteWorkDayUseCase deleteWorkDayUseCase,
                                WorkDayWebMapper mapper) {
        this.findWorkDayByDateUseCase = findWorkDayByDateUseCase;
        this.findWorkDaysByDateRangeUseCase = findWorkDaysByDateRangeUseCase;
        this.findWorkDaysByMonthUseCase = findWorkDaysByMonthUseCase;
        this.calculateMonthlyBalanceUseCase = calculateMonthlyBalanceUseCase;
        this.createWorkDayUseCase = createWorkDayUseCase;
        this.updateWorkDayUseCase = updateWorkDayUseCase;
        this.deleteWorkDayUseCase = deleteWorkDayUseCase;
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

    @GetMapping("/{date}")
    public ResponseEntity<WorkDayWebResponse> getWorkDayByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        WorkDay workDay = findWorkDayByDateUseCase.findWorkDayByDate(date);
        return ResponseEntity.ok(mapper.toResponse(workDay));
    }

    @GetMapping("/range")
    public ResponseEntity<List<WorkDayWebResponse>> getWorkDaysByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<WorkDay> workDays = findWorkDaysByDateRangeUseCase.findWorkDaysByDateRange(
                from,
                to
        );
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

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteWorkDay(
            @PathVariable LocalDate date
    ) {
        deleteWorkDayUseCase.deleteWorkDay(date);
        return ResponseEntity.noContent().build();
    }
}
