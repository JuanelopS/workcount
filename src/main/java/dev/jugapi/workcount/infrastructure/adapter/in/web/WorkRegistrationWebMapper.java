package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class WorkRegistrationWebMapper {

    public WorkDay toDomain(WorkRegistrationWebRequest request) {
        return WorkDay.of(
                request.workingDay(),
                request.startTime(),
                request.finishingTime(),
                request.breakDuration(),
                Duration.ZERO   // validated hours cannot be sent for client
        );
    }

    public WorkRegistrationWebResponse toResponse(WorkDay wr) {
        return new WorkRegistrationWebResponse(
                wr.getWorkingDay(),
                wr.getStartTime(),
                wr.getFinishingTime(),
                wr.getBreakDuration(),
                wr.getValidatedHours().toMinutes() / 60.0
        );
    }

    public List<WorkRegistrationWebResponse> toResponseList(List<WorkDay> list) {
        return list.stream()
                .map(this::toResponse)
                .toList();
    }
}
