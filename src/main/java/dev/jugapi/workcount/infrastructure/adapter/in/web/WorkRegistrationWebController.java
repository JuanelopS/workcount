package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.application.service.WorkRegistrationService;
import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/work-registrations")
public class WorkRegistrationWebController {
    private final WorkRegistrationService service;
    private final WorkRegistrationWebMapper mapper;

    public WorkRegistrationWebController(WorkRegistrationService service, WorkRegistrationWebMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/balance/{yearMonth}")
    public Duration getBalance(@PathVariable("yearMonth") String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        return service.calculateMonthlyBalance(ym);
    }

    @PostMapping
    public void registerWork(@RequestBody WorkRegistrationWebRequest wre) {
        WorkRegistration wr = mapper.toDomain(wre);
        service.registerDay(wr);
    }
}
