INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('MONDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('TUESDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('WEDNESDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('THURSDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('FRIDAY', '07:30', '16:30');

INSERT INTO work_month_template (id, year_month, weeks) VALUES (1, '2023-10-01', 4);

INSERT INTO work_registration (id, working_day, start_time, finishing_time, break_duration, validated_hours) VALUES (1, '2023-10-02', '08:00:00', '17:00:00', NULL, '08:00:00');
INSERT INTO work_registration (id, working_day, start_time, finishing_time, break_duration, validated_hours) VALUES (2, '2023-10-03', '08:30:00', '17:30:00', NULL, '08:00:00');
INSERT INTO work_registration (id, working_day, start_time, finishing_time, break_duration, validated_hours) VALUES (3, '2023-10-04', '08:00:00', '14:00:00', NULL, '06:00:00');
INSERT INTO work_registration (id, working_day, start_time, finishing_time, break_duration, validated_hours) VALUES (4, '2023-10-05', '09:00:00', '18:00:00', NULL, '08:00:00');
INSERT INTO work_registration (id, working_day, start_time, finishing_time, break_duration, validated_hours) VALUES (5, '2023-10-06', '08:00:00', '15:00:00', NULL, '06:30:00');
ALTER TABLE work_registration ALTER COLUMN id RESTART WITH 6;
