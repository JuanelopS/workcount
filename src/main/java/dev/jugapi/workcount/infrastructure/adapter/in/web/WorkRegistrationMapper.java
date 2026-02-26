package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.springframework.stereotype.Component;

@Component
public class WorkRegistrationMapper {

    public WorkRegistration toDomain(WorkRegistrationRequest request) {
        return new WorkRegistration(
                request.workingDay(),
                request.startTime(),
                request.finishingTime(),
                request.breakDuration(),
                request.validatedHours()
        );
    }
}
