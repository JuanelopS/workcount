package dev.jugapi.workcount.domain.model;

import dev.jugapi.workcount.domain.exception.InexistentClockingException;
import dev.jugapi.workcount.domain.exception.InvalidClockingSequenceException;
import dev.jugapi.workcount.domain.model.clocking.Clocking;
import dev.jugapi.workcount.domain.model.clocking.ClockingType;
import dev.jugapi.workcount.domain.model.policy.DailyPolicy;
import dev.jugapi.workcount.domain.model.workday.WorkDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorkDayTest {

    private LocalDate day;
    private WorkDay workDay;

    @BeforeEach
    void init() {
        day = LocalDate.of(2026, 4, 18);
        workDay = WorkDay.create(day);
    }

    @Test
    @DisplayName("addClocking: clock in should be added correctly")
    void addClockingTest() {
        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        assertEquals(2, workDay.getClockingList().size());
    }

    @Test
    @DisplayName("addClocking: clocking in twice in a row the same type should throw an exception")
    void invalidClockingSequenceTestByType() {
        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));

        assertThrows(InvalidClockingSequenceException.class, () -> {
            workDay.addClocking(new Clocking(LocalTime.of(9, 0), ClockingType.IN));
        });
    }

    @Test
    @DisplayName("addClocking: new clock in shouldn't be before than last clock in")
    void invalidClockingSequenceTestByTime() {
        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        assertThrows(InvalidClockingSequenceException.class, () -> {
            workDay.addClocking(new Clocking(LocalTime.of(13, 0), ClockingType.IN));
        });
    }

    @Test
    @DisplayName("addClocking: an exception should be thrown if first clock in is out type")
    void invalidFirstTypeOfClocking() {
        assertThrows(InvalidClockingSequenceException.class, () -> {
            workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        });
    }

    @Test
    @DisplayName("updateClocking: valid clock in update")
    void updateClockingTest() {
        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));

        workDay.updateClocking(LocalTime.of(8, 0), LocalTime.of(8, 30),
                ClockingType.IN);

        LocalTime clockingTime = workDay.getClockingList().get(0).time();

        assertEquals(clockingTime, LocalTime.of(8, 30));
    }

    @Test
    @DisplayName("updateClocking: an exception should be thrown if update a clocking breaks time :P")
    void invalidUpdateClockingTest() {
        workDay.addClocking(new Clocking(LocalTime.of(10, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        assertThrows(InvalidClockingSequenceException.class, () -> {
            workDay.updateClocking(LocalTime.of(14, 0), LocalTime.of(9, 30),
                    ClockingType.IN);
        });
    }

    @Test
    @DisplayName("deleteClocking: clock in should be deleted correctly")
    void deleteClockingTest() {
        workDay.addClocking(new Clocking(LocalTime.of(10, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        workDay.deleteClocking(LocalTime.of(14, 0));

        int total = workDay.getClockingList().size();

        assertEquals(1, total);
    }

    @Test
    @DisplayName("deleteClocking: exception should be thrown if you try to delete inexistent")
    void invalidDeleteClockingTest() {
        workDay.addClocking(new Clocking(LocalTime.of(10, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));

        assertThrows(InexistentClockingException.class, () -> {
            workDay.deleteClocking(LocalTime.of(12, 0));
        });
    }

    @Test
    @DisplayName("calculateTotalHours: should sum only closed intervals")
    void notClockingOut() {
        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        workDay.addClocking(new Clocking(LocalTime.of(15, 0), ClockingType.IN));

        Duration total = workDay.calculateTotalHours();

        assertEquals(Duration.ofHours(6), total);
    }

    @Test
    @DisplayName("calculateTotalHours: should sum valid intervals")
    void sumValidIntervals() {
        workDay.addClocking(new Clocking(LocalTime.of(8, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        workDay.addClocking(new Clocking(LocalTime.of(15, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(18, 30), ClockingType.OUT));

        Duration total = workDay.calculateTotalHours();

        assertEquals(Duration.ofMinutes(570), total); // 9.5 hours
    }

    @Test
    @DisplayName("calculateNetTimeWorked: should follow the policy of the day")
    void calculateNetTimeWorkedTest() {
        DailyPolicy policy = new DailyPolicy(DayOfWeek.THURSDAY,
                LocalTime.of(7, 30),
                LocalTime.of(19, 30));

        workDay.getClockingList().clear();
        workDay.addClocking(new Clocking(LocalTime.of(7, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(14, 0), ClockingType.OUT));
        workDay.addClocking(new Clocking(LocalTime.of(15, 0), ClockingType.IN));
        workDay.addClocking(new Clocking(LocalTime.of(20, 30), ClockingType.OUT));

        workDay.calculateNetTimeWorked(policy);

        assertEquals(660, workDay.getNetTimeWorked().toMinutes()); // 11 hours
    }
}
