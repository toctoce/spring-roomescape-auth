INSERT INTO theme (name, description, thumbnail_url, runtime)
VALUES ('공포의 저택', '어두운 저택에 숨겨진 비밀을 찾아 탈출하는 테마', 'https://example.com/themes/haunted-house.png', 1),
       ('시간 여행자', '시간의 균열을 따라 단서를 모아 현재로 돌아오는 테마', 'https://example.com/themes/time-traveler.png', 1),
       ('비밀 연구소', '폐쇄된 연구소에서 실험 기록을 추적하는 테마', 'https://example.com/themes/secret-lab.png', 1);

INSERT INTO reservation_time (start_at)
VALUES ('10:00'),
       ('13:00'),
       ('16:00');

INSERT INTO stores (name)
VALUES ('잠실점'),
       ('강남점');

INSERT INTO members (name, email, password, role, store_id)
VALUES ('봉구스', 'bongus@example.com', 'password', 'USER', NULL),
       ('밀란', 'milan@example.com', 'password', 'MANAGER', 1);

INSERT INTO reservation (member_id, store_id, date, time_id, theme_id)
VALUES (1, 1, '2026-05-06', 1, 1),
       (2, 1, '2026-05-07', 2, 2);
