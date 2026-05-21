DELETE
FROM reservation;

DELETE
FROM members;

DELETE
FROM reservation_time;

ALTER TABLE reservation_time
    ALTER COLUMN id RESTART WITH 1;

INSERT INTO reservation_time (start_at)
VALUES ('10:00'),
       ('13:00'),
       ('16:00');

DELETE
FROM theme;

ALTER TABLE theme
    ALTER COLUMN id RESTART WITH 1;

INSERT INTO theme (name, description, thumbnail_url, runtime)
VALUES ('테마1', '테마1 설명', 'https://example.com/themes/theme-1.png', 1),
       ('테마2', '테마2 설명', 'https://example.com/themes/theme-2.png', 1),
       ('테마3', '테마3 설명', 'https://example.com/themes/theme-3.png', 1),
       ('테마4', '테마4 설명', 'https://example.com/themes/theme-4.png', 1),
       ('테마5', '테마5 설명', 'https://example.com/themes/theme-5.png', 1),
       ('테마6', '테마6 설명', 'https://example.com/themes/theme-6.png', 1),
       ('테마7', '테마7 설명', 'https://example.com/themes/theme-7.png', 1),
       ('테마8', '테마8 설명', 'https://example.com/themes/theme-8.png', 1),
       ('테마9', '테마9 설명', 'https://example.com/themes/theme-9.png', 1),
       ('테마10', '테마10 설명', 'https://example.com/themes/theme-10.png', 1),
       ('테마11', '테마11 설명', 'https://example.com/themes/theme-11.png', 1),
       ('테마12', '테마12 설명', 'https://example.com/themes/theme-12.png', 1),
       ('테마13', '테마13 설명', 'https://example.com/themes/theme-13.png', 1),
       ('테마14', '테마14 설명', 'https://example.com/themes/theme-14.png', 1),
       ('테마15', '테마15 설명', 'https://example.com/themes/theme-15.png', 1);

ALTER TABLE members
    ALTER COLUMN id RESTART WITH 1;

INSERT INTO members (name, email, password)
VALUES ('예약자1', 'member1@example.com', 'password'),
       ('예약자2', 'member2@example.com', 'password'),
       ('예약자3', 'member3@example.com', 'password'),
       ('예약자4', 'member4@example.com', 'password'),
       ('예약자5', 'member5@example.com', 'password'),
       ('예약자6', 'member6@example.com', 'password'),
       ('예약자7', 'member7@example.com', 'password'),
       ('예약자8', 'member8@example.com', 'password'),
       ('예약자9', 'member9@example.com', 'password'),
       ('예약자10', 'member10@example.com', 'password'),
       ('예약자11', 'member11@example.com', 'password'),
       ('예약자12', 'member12@example.com', 'password'),
       ('예약자13', 'member13@example.com', 'password'),
       ('예약자14', 'member14@example.com', 'password'),
       ('예약자15', 'member15@example.com', 'password');

ALTER TABLE reservation
    ALTER COLUMN id RESTART WITH 1;

INSERT INTO reservation (member_id, store_id, date, time_id, theme_id)
VALUES
    -- 테마1: 1개
    (1, 1, DATEADD('DAY', -1, CURRENT_DATE), 1, 1),

    -- 테마2: 2개
    (2, 1, DATEADD('DAY', -1, CURRENT_DATE), 1, 2),
    (3, 1, DATEADD('DAY', -1, CURRENT_DATE), 2, 2),

    -- 테마3: 3개
    (4, 1, DATEADD('DAY', -1, CURRENT_DATE), 1, 3),
    (5, 1, DATEADD('DAY', -1, CURRENT_DATE), 2, 3),
    (6, 1, DATEADD('DAY', -1, CURRENT_DATE), 3, 3),

    -- 테마4: 4개
    (7, 1, DATEADD('DAY', -1, CURRENT_DATE), 1, 4),
    (8, 1, DATEADD('DAY', -1, CURRENT_DATE), 2, 4),
    (9, 1, DATEADD('DAY', -1, CURRENT_DATE), 3, 4),
    (10, 1, CURRENT_DATE, 1, 4),

    -- 테마5: 5개
    (11, 1, DATEADD('DAY', -1, CURRENT_DATE), 1, 5),
    (12, 1, DATEADD('DAY', -1, CURRENT_DATE), 2, 5),
    (13, 1, DATEADD('DAY', -1, CURRENT_DATE), 3, 5),
    (14, 1, CURRENT_DATE, 1, 5),
    (15, 1, CURRENT_DATE, 2, 5);
