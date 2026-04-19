package dev.jugapi.workcount.infrastructure.adapter.in.web.clocking;

import dev.jugapi.workcount.application.port.in.clocking.ClockInUseCase;
import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebMapper;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayWebResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/clockings")
public class ClockingWebController {

    private final ClockInUseCase clockInUseCase;
    private final CreateClockingUseCase createClockingUseCase;
    private final UpdateClockingUseCase updateClockingUseCase;
    private final DeleteClockingUseCase deleteClockingUseCase;
    private final WorkDayWebMapper mapper;

    public ClockingWebController(ClockInUseCase clockInUseCase,
                                 CreateClockingUseCase createClockingUseCase,
                                 UpdateClockingUseCase updateClockingUseCase,
                                 DeleteClockingUseCase deleteClockingUseCase,
                                 WorkDayWebMapper mapper) {
        this.clockInUseCase = clockInUseCase;
        this.createClockingUseCase = createClockingUseCase;
        this.updateClockingUseCase = updateClockingUseCase;
        this.deleteClockingUseCase = deleteClockingUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<WorkDayWebResponse> clockIn() {
        WorkDay workDay = clockInUseCase.clockIn();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(workDay));
    }

    @PostMapping
    public ResponseEntity<WorkDayWebResponse> createClocking(
            @RequestBody CreateClockingWebRequest request
    ) {
        WorkDay workDay = createClockingUseCase
                .createClocking(request.date(), request.time(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(workDay));
    }

    @PutMapping
    public ResponseEntity<WorkDayWebResponse> updateClocking(
            @RequestBody UpdateClockingWebRequest request
    ) {
        WorkDay workDay = updateClockingUseCase
                .updateClocking(request.date(), request.originalTime(), request.newTime(),
                        request.type());
        return ResponseEntity.ok(mapper.toResponse(workDay));
    }

    @DeleteMapping("/{date}/{time}")
    public ResponseEntity<Void> deleteClocking(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime time
    ) {
        deleteClockingUseCase.deleteClocking(date, time);
        return ResponseEntity.noContent().build();
    }

}
