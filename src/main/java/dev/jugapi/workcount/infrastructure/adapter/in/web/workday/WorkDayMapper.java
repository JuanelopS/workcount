package dev.jugapi.workcount.infrastructure.adapter.in.web.workday;

import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class WorkDayMapper {

    public WorkDay toDomain(WorkDayRequest request) {
        return WorkDay.of(
                request.workingDay(),
                request.clockingList(),
                Duration.ZERO   // validated hours cannot be sent for client
        );
    }

    public WorkDayResponse toResponse(WorkDay workDay) {
        return new WorkDayResponse(
                workDay.getDate(),
                workDay.getClockingList(),
                workDay.getNetTimeWorked().toMinutes() / 60.0
        );
    }

    public List<WorkDayResponse> toResponseList(List<WorkDay> list) {
        return list.stream()
                .map(this::toResponse)
                .toList();
    }
}
