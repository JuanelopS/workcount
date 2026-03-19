package dev.jugapi.workcount.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkDayTest {

    @Test
    @DisplayName("Should sum valid intervals")
    void calculateTotalHoursTest() {

        LocalDate today = LocalDate.now();
        WorkDay workDay = WorkDay.create(today);

        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        workDay.addClocking(new Clocking(LocalTime.of(15, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(18, 30), ClockingType.OUT));

        Duration total = workDay.calculateTotalHours();

        System.out.println(total.toMinutes());
        assertEquals(Duration.ofMinutes(570), total); // 9.5 hours
    }
}
