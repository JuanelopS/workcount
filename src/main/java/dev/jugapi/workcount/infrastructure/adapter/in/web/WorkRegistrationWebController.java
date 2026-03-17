package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.application.port.in.workday.DeleteWorkDayUseCase;
import dev.jugapi.workcount.application.port.in.workday.FindWorkDayByMonthUseCase;
import dev.jugapi.workcount.application.port.in.workday.UpdateWorkDayUseCase;
import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/work-registrations")
public class WorkRegistrationWebController {

    private final RegisterWorkDayUseCase registerWorkDayUseCase;
    private final FindWorkDayByMonthUseCase findWorkDayByMonthUseCase;
    private final CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;
    private final DeleteWorkDayUseCase deleteWorkDayUseCase;
    private final UpdateWorkDayUseCase updateWorkDayUseCase;
    private final WorkRegistrationWebMapper mapper;

    public WorkRegistrationWebController(RegisterWorkDayUseCase registerWorkDayUseCase,
                                         FindWorkDayByMonthUseCase findWorkDayByMonthUseCase,
                                         CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase,
                                         DeleteWorkDayUseCase deleteWorkDayUseCase,
                                         UpdateWorkDayUseCase updateWorkDayUseCase,
                                         WorkRegistrationWebMapper mapper) {
        this.registerWorkDayUseCase = registerWorkDayUseCase;
        this.findWorkDayByMonthUseCase = findWorkDayByMonthUseCase;
        this.calculateMonthlyBalanceUseCase = calculateMonthlyBalanceUseCase;
        this.deleteWorkDayUseCase = deleteWorkDayUseCase;
        this.updateWorkDayUseCase = updateWorkDayUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/save")
    public ResponseEntity<WorkRegistrationWebResponse> registerWork(
            @RequestBody WorkRegistrationWebRequest wre) {
        WorkDay wr = mapper.toDomain(wre);
        WorkDay savedWr = registerWorkDayUseCase.registerWorkDay(wr);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(savedWr));
    }

    @PutMapping("/modify")
    public ResponseEntity<WorkRegistrationWebResponse> modifyWork(
            @RequestBody WorkRegistrationWebRequest wre) {
        WorkDay wr = mapper.toDomain(wre);
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
    public ResponseEntity<List<WorkRegistrationWebResponse>> findByMonth(
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
