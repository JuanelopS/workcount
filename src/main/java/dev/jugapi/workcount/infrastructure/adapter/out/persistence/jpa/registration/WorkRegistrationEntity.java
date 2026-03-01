package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.registration;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_registration")
public class WorkRegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "working_day", unique = true)
    private LocalDate workingDay;
    private LocalTime startTime;
    private LocalTime finishingTime;
    private Duration breakDuration;
    private Duration validatedHours;

    public WorkRegistrationEntity() {
    }

    public WorkRegistrationEntity(LocalDate workingDay, LocalTime startTime,
                                  LocalTime finishingTime, Duration breakDuration,
                                  Duration validatedHours) {
        this.workingDay = workingDay;
        this.startTime = startTime;
        this.finishingTime = finishingTime;
        this.breakDuration = breakDuration;
        this.validatedHours = validatedHours;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(LocalDate workingDay) {
        this.workingDay = workingDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getFinishingTime() {
        return finishingTime;
    }

    public void setFinishingTime(LocalTime finishingTime) {
        this.finishingTime = finishingTime;
    }

    public Duration getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(Duration breakDuration) {
        this.breakDuration = breakDuration;
    }

    public Duration getValidatedHours() {
        return validatedHours;
    }

    public void setValidatedHours(Duration validatedHours) {
        this.validatedHours = validatedHours;
    }
}
