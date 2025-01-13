CREATE SCHEMA IF NOT EXISTS RECRUITERS;
CREATE TABLE RECRUITERS.positions
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    status       NUMERIC(2)   NOT NULL,
    created_at   DATE         NOT NULL,
    published_at DATE
);

CREATE TABLE RECRUITERS.requirements
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES RECRUITERS.positions (id) ON DELETE CASCADE,
    key VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    mandatory BOOLEAN NOT NULL
);

CREATE TABLE RECRUITERS.tasks
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES RECRUITERS.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

CREATE TABLE RECRUITERS.benefits
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES RECRUITERS.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

INSERT INTO RECRUITERS.positions (id, title, description, status, created_at) VALUES (1, 'Java Developer', 'Java Developer', 0, '2021-01-01');
INSERT INTO RECRUITERS.positions (id, title, description, status, created_at, published_at) VALUES (2, 'Python Developer', 'Python Developer', 1, '2021-01-01', '2021-01-10');
INSERT INTO RECRUITERS.requirements (id, position_id, key, value, description, mandatory) VALUES (1, 1, 'Java', 'More than 10 years', 'Knowledge of Java', true);
INSERT INTO RECRUITERS.requirements (id, position_id, key, value, description, mandatory) VALUES (2, 2, 'Python', 'More than 10 years', 'Knowledge of Python', true);
INSERT INTO RECRUITERS.tasks (id, position_id, description) VALUES (1, 1, 'Java');
INSERT INTO RECRUITERS.tasks (id, position_id, description) VALUES (2, 2, 'Python');
INSERT INTO RECRUITERS.benefits (id, position_id, description) VALUES (1, 1, '23 days of vacation');
INSERT INTO RECRUITERS.benefits (id, position_id, description) VALUES (2, 2, '23 days of vacation');


ALTER SEQUENCE RECRUITERS.positions_id_seq RESTART WITH 3;
ALTER SEQUENCE RECRUITERS.requirements_id_seq RESTART WITH 3;
ALTER SEQUENCE RECRUITERS.tasks_id_seq RESTART WITH 3;
ALTER SEQUENCE RECRUITERS.benefits_id_seq RESTART WITH 3;
