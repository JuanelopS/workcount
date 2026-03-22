package dev.jugapi.workcount.infrastructure.adapter.in.web;

import dev.jugapi.workcount.domain.model.WorkDay;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class WorkDayWebMapper {

    public WorkDay toDomain(WorkDayWebRequest request) {
        return WorkDay.of(
                request.workingDay(),
                request.clockingList(),
                Duration.ZERO   // validated hours cannot be sent for client
        );
    }

    public WorkDayWebResponse toResponse(WorkDay workDay) {
        return new WorkDayWebResponse(
                workDay.getDate(),
                workDay.getClockingList(),
                workDay.getNetTimeWorked().toMinutes() / 60.0
        );
    }

    public List<WorkDayWebResponse> toResponseList(List<WorkDay> list) {
        return list.stream()
                .map(this::toResponse)
                .toList();
    }
}
