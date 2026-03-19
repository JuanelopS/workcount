package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import dev.jugapi.workcount.domain.model.Clocking;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "work_registration")
public class WorkDayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "working_day", unique = true)
    private LocalDate date;
    private List<Clocking> clockingList;
    private Duration validatedHours;

    public WorkDayEntity() {
    }

    public WorkDayEntity(LocalDate date, List<Clocking> clockingList,
                         Duration validatedHours) {
        this.date = date;
        this.clockingList = clockingList;
        this.validatedHours = validatedHours;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate workingDay) {
        this.date = workingDay;
    }

    public List<Clocking> getClockingList() {
        return clockingList;
    }

    public void setClockingList(List<Clocking> clockingList) {
        this.clockingList = clockingList;
    }

    public Duration getValidatedHours() {
        return validatedHours;
    }

    public void setValidatedHours(Duration validatedHours) {
        this.validatedHours = validatedHours;
    }
}
