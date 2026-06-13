package dev.jugapi.workcount.infrastructure.adapter.out.persistence.jpa.template;

import jakarta.persistence.*;

import java.time.YearMonth;

@Entity
@Table(name = "work_month_templates")
public class WorkMonthTemplateEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "year_month")
    private YearMonth yearMonth;
    private int weeks;

    public WorkMonthTemplateEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }
}
