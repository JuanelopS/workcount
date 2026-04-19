package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.ClockingType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalTime;

@Embeddable
public class ClockingEmbeddable {

    @Column(name = "clocking_time", nullable = false)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "clocking_type", nullable = false)
    private ClockingType type;

    public ClockingEmbeddable() {
    }

    public ClockingEmbeddable(LocalTime time, ClockingType type) {
        this.time = time;
        this.type = type;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ClockingType getType() {
        return type;
    }

    public void setType(ClockingType type) {
        this.type = type;
    }
}

