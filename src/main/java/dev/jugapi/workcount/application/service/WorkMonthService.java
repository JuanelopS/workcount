package dev.jugapi.workcount.application.service;

import dev.jugapi.workcount.application.port.in.workmonth.CalculateMonthlyBalanceUseCase;
import dev.jugapi.workcount.application.port.out.workday.WorkDayRepository;
import dev.jugapi.workcount.application.port.out.workmonth.WorkMonthTemplateRepository;
import dev.jugapi.workcount.domain.exception.TemplateNotFoundException;
import dev.jugapi.workcount.domain.model.workmonth.WorkMonth;
import dev.jugapi.workcount.domain.model.workmonth.WorkMonthTemplate;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

@Service
public class WorkMonthService implements CalculateMonthlyBalanceUseCase {

    private final WorkDayRepository workDayRepository;
    private final WorkMonthTemplateRepository workMonthTemplateRepository;
    private final Duration weeklyTarget;

    public WorkMonthService(WorkDayRepository workDayRepository,
                            WorkMonthTemplateRepository workMonthTemplateRepository,
                            @Value("${ss.policy.target-weekly-hours}") Duration weeklyTarget) {
        this.workDayRepository = workDayRepository;
        this.workMonthTemplateRepository = workMonthTemplateRepository;
        this.weeklyTarget = weeklyTarget;
    }

    @Override
    public Duration calculateMonthlyBalance(YearMonth month) {
        List<WorkDay> registrations = workDayRepository.findByMonth(month);
        WorkMonthTemplate template = workMonthTemplateRepository
                .getWorkMonthTemplate(month)
                .orElseThrow(() -> new TemplateNotFoundException(month));
        Duration target = template.monthlyTargetHours(this.weeklyTarget);
        return new WorkMonth(month, registrations, target).calculateBalance();
    }
}
