package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.policy;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "daily_policies")
public class DailyPolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", unique = true)
    DayOfWeek dayOfWeek;

    @Column(name = "limit_entry_time")
    LocalTime limitEntryTime;

    @Column(name = "limit_exit_time")
    LocalTime limitExitTime;

    public DailyPolicyEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getLimitEntryTime() {
        return limitEntryTime;
    }

    public void setLimitEntryTime(LocalTime limitEntryTime) {
        this.limitEntryTime = limitEntryTime;
    }

    public LocalTime getLimitExitTime() {
        return limitExitTime;
    }

    public void setLimitExitTime(LocalTime limitExitTime) {
        this.limitExitTime = limitExitTime;
    }
}
