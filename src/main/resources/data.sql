DROP TABLE IF EXISTS USER CASCADE CONSTRAINTS;
CREATE TABLE USER
(
   user_id integer,
   primary key(user_id)
);
INSERT INTO USER(user_id) VALUES(1597932451);
INSERT INTO USER(user_id) VALUES(1593932475);
INSERT INTO USER(user_id) VALUES(1591932486);
INSERT INTO USER(user_id) VALUES(1599932496);

DROP TABLE IF EXISTS ROOM_PARTICIPANT CASCADE CONSTRAINTS;
CREATE TABLE ROOM_PARTICIPANT
(
    room_id varchar(30),
    participant_id integer
);

INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1597932451);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1593932475);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1591932486);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1599932417);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1596932453);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1591932468);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1599232462);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1592932419);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1592932435);
INSERT INTO ROOM_PARTICIPANT(room_id, participant_id) VALUES ('TEST_ROOM', 1592932448);

DROP TABLE IF EXISTS SPRAY_MONEY CASCADE CONSTRAINTS;
CREATE TABLE SPRAY_MONEY
(
    token varchar(3) NOT NULL,
    user_id integer,
    total_money integer,
    remain_money integer,
    room_id varchar(64),
    reg_date timestamp default current_timestamp,
    primary key(token)
);

-- for update 문 사용하자
-- isolation commited 써야함.. 락걸렸는데 repetable read면 이전버전 읽겟지..
DROP TABLE IF EXISTS USER_MAPPED_SPRAY_MONEY CASCADE CONSTRAINTS;
CREATE TABLE USER_MAPPED_SPRAY_MONEY
(
    id integer NOT NULL AUTO_INCREMENT,
    token varchar(3),
    user_id integer,
    allocated_money integer,
    received BOOLEAN default FALSE,
    primary key(id)
);