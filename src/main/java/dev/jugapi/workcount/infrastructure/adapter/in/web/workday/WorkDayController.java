package dev.jugapi.workcount.infrastructure.adapter.in.web.workday;

import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/workdays")
public class WorkDayController {

    private final FindWorkDayByDateUseCase findWorkDayByDateUseCase;
    private final FindWorkDaysByDateRangeUseCase findWorkDaysByDateRangeUseCase;
    private final FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase;
    private final CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;
    private final CreateWorkDayUseCase createWorkDayUseCase;
    private final UpdateWorkDayUseCase updateWorkDayUseCase;
    private final DeleteWorkDayUseCase deleteWorkDayUseCase;
    private final WorkDayMapper mapper;

    public WorkDayController(FindWorkDaysByMonthUseCase findWorkDaysByMonthUseCase,
                             FindWorkDayByDateUseCase findWorkDayByDateUseCase,
                             FindWorkDaysByDateRangeUseCase findWorkDaysByDateRangeUseCase,
                             CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase,
                             CreateWorkDayUseCase createWorkDayUseCase,
                             UpdateWorkDayUseCase updateWorkDayUseCase,
                             DeleteWorkDayUseCase deleteWorkDayUseCase,
                             WorkDayMapper mapper) {
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
    public ResponseEntity<List<WorkDayResponse>> getWorkDaysByMonth(
            @RequestParam String month
    ) {
        List<WorkDay> workDays = findWorkDaysByMonthUseCase
                .findWorkDaysByMonth(YearMonth.parse(month));

        return ResponseEntity.ok(mapper.toResponseList(workDays));
    }

    @GetMapping("/{date}")
    public ResponseEntity<WorkDayResponse> getWorkDayByDate(
            @PathVariable String date
    ) {
        WorkDay workDay = findWorkDayByDateUseCase.findWorkDayByDate(LocalDate.parse(date));
        return ResponseEntity.ok(mapper.toResponse(workDay));
    }

    @GetMapping("/range")
    public ResponseEntity<List<WorkDayResponse>> getWorkDaysByRange(
            @RequestParam String from,
            @RequestParam String to
    ) {
        List<WorkDay> workDays = findWorkDaysByDateRangeUseCase.findWorkDaysByDateRange(
                LocalDate.parse(from),
                LocalDate.parse(to)
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
    public ResponseEntity<WorkDayResponse> postWorkDay(
            @RequestBody WorkDayRequest request
    ) {
        WorkDay created = createWorkDayUseCase.createWorkDay(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping
    public ResponseEntity<WorkDayResponse> putWorkDay(
            @RequestBody WorkDayRequest request
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
