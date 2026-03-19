/*
package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.application.port.in.workday.*;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/workday")
public class WorkDayWebController {

    private final CreateWorkDayUseCase createWorkDayUseCase;
    private final UpdateWorkDayUseCase updateWorkDayUseCase;
    private final DeleteWorkDayUseCase deleteWorkDayUseCase;
    private final FindWorkDayByMonthUseCase findWorkDayByMonthUseCase;
    private final GetCurrentStatusUseCase getCurrentStatusUseCase;
    private final WorkDayWebMapper mapper;

    public WorkDayWebController(CreateWorkDayUseCase createWorkDayUseCase,
                                UpdateWorkDayUseCase updateWorkDayUseCase,
                                DeleteWorkDayUseCase deleteWorkDayUseCase,
                                FindWorkDayByMonthUseCase findWorkDayByMonthUseCase,
                                GetCurrentStatusUseCase getCurrentStatusUseCase,
                                WorkDayWebMapper mapper) {
        this.createWorkDayUseCase = createWorkDayUseCase;
        this.updateWorkDayUseCase = updateWorkDayUseCase;
        this.deleteWorkDayUseCase = deleteWorkDayUseCase;
        this.findWorkDayByMonthUseCase = findWorkDayByMonthUseCase;
        this.getCurrentStatusUseCase = getCurrentStatusUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/save")
    public ResponseEntity<WorkDayWebResponse> registerWork(
            @RequestBody WorkDayWebRequest request) {
        WorkDay workDay = mapper.toDomain(request);
        WorkDay savedWorkDay = createWorkDayUseCase.createWorkDay(workDay);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(savedWorkDay));
    }

    @PutMapping("/modify")
    public ResponseEntity<WorkDayWebResponse> modifyWork(
            @RequestBody WorkDayWebRequest request) {
        WorkDay wr = mapper.toDomain(request);
        WorkDay modified = updateWorkDayUseCase.updateWorkDay(wr);
        return ResponseEntity.ok(mapper.toResponse(modified));
    }

    @DeleteMapping("/delete/{date}")
    public ResponseEntity<Void> deleteWork(
            @PathVariable("date") String date) {
        deleteWorkDayUseCase.deleteWorkDay(LocalDate.parse(date));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/month/{yearMonth}")
    public ResponseEntity<List<WorkDayWebResponse>> findByMonth(
            @PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        List<WorkDay> registrations = findWorkDayByMonthUseCase.findWorkDayByMonth(ym);
        return ResponseEntity.ok(mapper.toResponseList(registrations));
    }

    @GetMapping("/balance/{yearMonth}")
    public ResponseEntity<Double> getBalance(@PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        Double result = calculateMonthlyBalanceUseCase
                .calculateMonthlyBalance(ym)
                .toMinutes() / 60.0;
        return ResponseEntity.ok(result);
    }
}
*/
