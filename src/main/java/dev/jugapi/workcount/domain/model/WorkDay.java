package dev.jugapi.workcount.domain.model;

import dev.jugapi.workcount.domain.exception.InexistentClockingException;
import dev.jugapi.workcount.domain.exception.InvalidClockingSequenceException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WorkDay {
    private final LocalDate date;
    private final List<Clocking> clockingList;
    private Duration netTimeWorked;

    private WorkDay(LocalDate date, List<Clocking> clockingList, Duration netTimeWorked) {
        netTimeWorked = netTimeWorked != null ? netTimeWorked : Duration.ZERO;
        validate(date, netTimeWorked);

        this.date = date;
        this.clockingList = new ArrayList<>(clockingList != null ? clockingList : List.of());
        this.netTimeWorked = netTimeWorked;
    }

    public void validate(LocalDate date, Duration netTimeWorked) {
        if (!checkMaxNetHours(netTimeWorked)) {
            throw new IllegalArgumentException("Las horas validadas no pueden superar las 24 horas");
        }
    }

    private boolean checkMaxNetHours(Duration netTimeWorked) {
        return netTimeWorked.toHours() < 24;
    }

    public static WorkDay of(LocalDate date, List<Clocking> clockingList,
                             Duration netTimeWorked) {
        return new WorkDay(date, clockingList, netTimeWorked);
    }

    public static WorkDay create(LocalDate day) {
        return new WorkDay(day, List.of(), Duration.ZERO);
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Clocking> getClockingList() {
        return clockingList;
    }

    public Duration getNetTimeWorked() {
        return netTimeWorked;
    }

    public Optional<LocalTime> getStartTime() {
        return clockingList.isEmpty() ? Optional.empty() :
                Optional.of(clockingList.get(0).time());
    }

    // returns the last Clocking.OUT to validate only IN - OUT duration blocks
    public Optional<LocalTime> getFinishingTime() {
        if (clockingList.isEmpty() ||
                clockingList.get(clockingList.size() - 1).type() != ClockingType.OUT) {
            return clockingList.stream().filter(c -> c.type() == ClockingType.OUT)
                    .max(Comparator.comparing(Clocking::time))
                    .map(Clocking::time);
        }

        return Optional.of(clockingList.get(clockingList.size() - 1).time());
    }

    public void addClocking(Clocking clocking) {
        List<Clocking> tempList = new ArrayList<>(clockingList);

        tempList.add(clocking);
        tempList.sort(Comparator.comparing(Clocking::time));
        validateSequence(tempList);
        clockingList.clear();
        clockingList.addAll(tempList);
    }

    public void updateClocking(LocalTime originalTime, LocalTime newTime) {
        if (clockingList.isEmpty()) {
            throw new InexistentClockingException(originalTime);
        }

        List<Clocking> tempList = new ArrayList<>(clockingList);

        Clocking clockingToUpdate = tempList.stream()
                .filter(c -> c.time().equals(originalTime))
                .findFirst()
                .orElseThrow(() -> new InexistentClockingException(originalTime));

        int indexToUpdate = tempList.indexOf(clockingToUpdate);
        ClockingType type = tempList.get(indexToUpdate).type();

        tempList.set(indexToUpdate, new Clocking(newTime, type));
        tempList.sort(Comparator.comparing(Clocking::time));
        validateSequence(tempList);
        clockingList.clear();
        clockingList.addAll(tempList);
    }

    public void deleteClocking(LocalTime time) {
        if (clockingList.isEmpty()) {
            throw new InexistentClockingException(time);
        }

        boolean removed = clockingList.removeIf(c -> c.time().equals(time));

        if (!removed) {
            throw new InexistentClockingException(time);
        }
    }

    public Duration calculateTotalHours() {
        if (this.clockingList.isEmpty())
            return Duration.ZERO;

        Duration total = Duration.ZERO;
        LocalTime start = null;

        for (Clocking clocking : this.clockingList) {
            if (clocking.type() == ClockingType.IN) {
                start = clocking.time();
            } else if (clocking.type() == ClockingType.OUT && start != null) {
                total = total.plus(Duration.between(start, clocking.time()));
                start = null;
            }
        }
        return total;
    }

    public WorkDay calculateNetTimeWorked(DailyPolicy policy) {
        Optional<LocalTime> optRealStart = this.getStartTime();
        Optional<LocalTime> optRealFinishing = this.getFinishingTime();

        if (optRealStart.isEmpty() || optRealFinishing.isEmpty()) {
            this.netTimeWorked = Duration.ZERO;
            return this;
        }

        LocalTime realStart = optRealStart.get();
        LocalTime realFinishing = optRealFinishing.get();

        LocalTime policyStart = policy.adjustEntry(realStart);
        LocalTime policyFinishing = policy.adjustExit(realFinishing);

        Duration cutStart = Duration.between(realStart, policyStart);
        if (cutStart.isNegative())
            cutStart = Duration.ZERO;

        Duration cutFinishing = Duration.between(policyFinishing, realFinishing);
        if (cutFinishing.isNegative())
            cutFinishing = Duration.ZERO;

        this.netTimeWorked = this.calculateTotalHours()
                .minus(cutStart)
                .minus(cutFinishing);

        return this;
    }

    public Optional<ClockingType> getCurrentStatus() {
        if (this.clockingList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.clockingList.get(clockingList.size() - 1).type());
    }

    private void validateSequence(List<Clocking> list) {
        if (list.isEmpty())
            return;

        if (list.get(0).type() == ClockingType.OUT) {
            throw new InvalidClockingSequenceException(list.get(0));
        }

        for (int i = 0; i < list.size() - 1; i++) {
            Clocking current = list.get(i);
            Clocking next = list.get(i + 1);

            if (current.type() == next.type()) {
                throw new InvalidClockingSequenceException(next);
            }
        }
    }
}
