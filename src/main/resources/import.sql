INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('MONDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('TUESDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('WEDNESDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('THURSDAY', '07:30', '19:30');
INSERT INTO daily_policies (day_of_week, limit_entry_time, limit_exit_time) VALUES ('FRIDAY', '07:30', '16:30');

INSERT INTO work_month_template (id, year_month, weeks) VALUES (1, '2023-10-01', 4);
INSERT INTO work_month_template (id, year_month, weeks) VALUES (2, '2023-11-01', 4);
INSERT INTO work_month_template (id, year_month, weeks) VALUES (3, '2023-12-01', 5);

INSERT INTO work_registration (id, working_day, validated_hours) VALUES (1, '2023-10-02', '08:00:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (2, '2023-10-03', '08:00:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (3, '2023-10-04', '06:00:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (4, '2023-10-05', '08:00:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (5, '2023-10-06', '06:30:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (6, '2023-10-09', '08:15:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (7, '2023-10-10', '07:45:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (8, '2023-11-02', '08:00:00');
INSERT INTO work_registration (id, working_day, validated_hours) VALUES (9, '2023-11-03', '05:30:00');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (1, 0, '08:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (1, 1, '14:00:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (1, 2, '15:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (1, 3, '17:00:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (2, 0, '08:30:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (2, 1, '12:30:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (2, 2, '13:30:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (2, 3, '17:30:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (3, 0, '08:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (3, 1, '14:00:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (4, 0, '09:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (4, 1, '13:00:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (4, 2, '14:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (4, 3, '18:00:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (5, 0, '08:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (5, 1, '12:00:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (5, 2, '12:30:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (5, 3, '15:00:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (6, 0, '07:45:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (6, 1, '12:45:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (6, 2, '13:15:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (6, 3, '17:15:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (7, 0, '08:15:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (7, 1, '12:00:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (7, 2, '13:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (7, 3, '17:00:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (8, 0, '08:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (8, 1, '14:00:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (8, 2, '15:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (8, 3, '17:00:00', 'OUT');

INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (9, 0, '09:00:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (9, 1, '12:00:00', 'OUT');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (9, 2, '12:30:00', 'IN');
INSERT INTO work_registration_clocking (work_registration_id, clocking_order, clocking_time, clocking_type) VALUES (9, 3, '15:00:00', 'OUT');

ALTER TABLE work_registration ALTER COLUMN id RESTART WITH 10;
