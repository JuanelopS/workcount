package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_registrations")
public class WorkDayPersistenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "working_day", unique = true)
    private LocalDate date;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "work_registration_clockings",
            joinColumns = @JoinColumn(name = "work_registration_id")
    )
    @OrderColumn(name = "clocking_order")
    private List<ClockingEmbeddable> clockingList = new ArrayList<>();

    private Duration validatedHours;

    public WorkDayPersistenceEntity() {
    }

    public WorkDayPersistenceEntity(LocalDate date, List<ClockingEmbeddable> clockingList,
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

    public List<ClockingEmbeddable> getClockingList() {
        return clockingList;
    }

    public void setClockingList(List<ClockingEmbeddable> clockingList) {
        this.clockingList = clockingList;
    }

    public Duration getValidatedHours() {
        return validatedHours;
    }

    public void setValidatedHours(Duration validatedHours) {
        this.validatedHours = validatedHours;
    }
}
