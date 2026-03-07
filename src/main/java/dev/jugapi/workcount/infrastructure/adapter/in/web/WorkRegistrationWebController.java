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
    // TODO: this should returns WorkRegistration -> "201 Created" http code response (change test)
/*
    @PostMapping
    public WorkRegistration registerWork(@RequestBody WorkRegistrationWebRequest wre) {
        WorkRegistration wr = mapper.toDomain(wre);
        return registerWorkDayUseCase.registerWorkDay(wr);
    }
*/

    @PostMapping
    public ResponseEntity<WorkRegistration> registerWork(@RequestBody WorkRegistrationWebRequest wre) {
        WorkRegistration wr = mapper.toDomain(wre);
        WorkRegistration savedWr = registerWorkDayUseCase.registerWorkDay(wr);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWr);
    }

    @GetMapping("/search/month/{yearMonth}")
    public List<WorkRegistration> findByMonth(@PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        return findByMonthUseCase.findByMonth(ym);
    }

    @GetMapping("/balance/{yearMonth}")
    public Double getBalance(@PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        return calculateMonthlyBalanceUseCase.calculateMonthlyBalance(ym).toMinutes() / 60.0;
    }
}
