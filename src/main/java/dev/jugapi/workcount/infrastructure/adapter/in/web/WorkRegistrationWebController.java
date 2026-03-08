package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.application.port.in.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.application.port.in.FindByMonthUseCase;
import dev.jugapi.workcount.application.port.in.RegisterWorkDayUseCase;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.hibernate.jdbc.Work;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/work-registrations")
public class WorkRegistrationWebController {

    private final RegisterWorkDayUseCase registerWorkDayUseCase;
    private final FindByMonthUseCase findByMonthUseCase;
    private final CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase;
    private final WorkRegistrationWebMapper mapper;

    public WorkRegistrationWebController(RegisterWorkDayUseCase registerWorkDayUseCase,
                                         FindByMonthUseCase findByMonthUseCase,
                                         CalculateMonthlyBalanceUseCase calculateMonthlyBalanceUseCase,
                                         WorkRegistrationWebMapper mapper) {
        this.registerWorkDayUseCase = registerWorkDayUseCase;
        this.findByMonthUseCase = findByMonthUseCase;
        this.calculateMonthlyBalanceUseCase = calculateMonthlyBalanceUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<WorkRegistrationWebResponse> registerWork(@RequestBody WorkRegistrationWebRequest wre) {
        WorkRegistration wr = mapper.toDomain(wre);
        WorkRegistration savedWr = registerWorkDayUseCase.registerWorkDay(wr);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(savedWr));
    }

    @GetMapping("/search/month/{yearMonth}")
    public ResponseEntity<List<WorkRegistrationWebResponse>> findByMonth(@PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        List<WorkRegistration> registrations = findByMonthUseCase.findByMonth(ym);
        return ResponseEntity.ok(mapper.toResponseList(registrations));
    }

    @GetMapping("/balance/{yearMonth}")
    public ResponseEntity<Double> getBalance(@PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        Double result = calculateMonthlyBalanceUseCase.calculateMonthlyBalance(ym).toMinutes() / 60.0;
        return ResponseEntity.ok(result);
    }
}
