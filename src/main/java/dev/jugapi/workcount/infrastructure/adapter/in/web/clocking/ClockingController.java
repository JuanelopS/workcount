package dev.jugapi.workcount.infrastructure.adapter.in.web.clocking;

import dev.jugapi.workcount.application.port.in.clocking.ClockInUseCase;
import dev.jugapi.workcount.application.port.in.clocking.CreateClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.DeleteClockingUseCase;
import dev.jugapi.workcount.application.port.in.clocking.UpdateClockingUseCase;
import dev.jugapi.workcount.domain.model.WorkDay;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayMapper;
import dev.jugapi.workcount.infrastructure.adapter.in.web.workday.WorkDayResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/clockings")
public class ClockingController {

    private final ClockInUseCase clockInUseCase;
    private final CreateClockingUseCase createClockingUseCase;
    private final UpdateClockingUseCase updateClockingUseCase;
    private final DeleteClockingUseCase deleteClockingUseCase;
    private final WorkDayMapper mapper;

    public ClockingController(ClockInUseCase clockInUseCase,
                              CreateClockingUseCase createClockingUseCase,
                              UpdateClockingUseCase updateClockingUseCase,
                              DeleteClockingUseCase deleteClockingUseCase,
                              WorkDayMapper mapper) {
        this.clockInUseCase = clockInUseCase;
        this.createClockingUseCase = createClockingUseCase;
        this.updateClockingUseCase = updateClockingUseCase;
        this.deleteClockingUseCase = deleteClockingUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<WorkDayResponse> clockIn() {
        WorkDay workDay = clockInUseCase.clockIn();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(workDay));
    }

    @PostMapping
    public ResponseEntity<WorkDayResponse> createClocking(
            @RequestBody CreateClockingRequest request
    ) {
        WorkDay workDay = createClockingUseCase
                .createClocking(request.date(), request.time(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(workDay));
    }

    @PutMapping
    public ResponseEntity<WorkDayResponse> updateClocking(
            @RequestBody UpdateClockingRequest request
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
