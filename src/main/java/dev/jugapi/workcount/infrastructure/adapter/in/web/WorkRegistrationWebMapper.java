package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.domain.model.WorkRegistration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class WorkRegistrationWebMapper {

    public WorkRegistration toDomain(WorkRegistrationWebRequest request) {
        return new WorkRegistration(
                request.workingDay(),
                request.startTime(),
                request.finishingTime(),
                request.breakDuration(),
                Duration.ZERO   // validated hours cannot be sent for client
        );
    }
}
