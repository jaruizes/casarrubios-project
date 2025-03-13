CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SCHEMA IF NOT EXISTS recruiters;

CREATE TABLE IF NOT EXISTS recruiters.applications (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    cv VARCHAR(255) NOT NULL,
    position_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.scoring (
    application_id UUID PRIMARY KEY REFERENCES recruiters.applications(id),
    score FLOAT NOT NULL,
    desc_score FLOAT NOT NULL,
    requirement_score FLOAT NOT NULL,
    tasks_score FLOAT NOT NULL,
    time_spent FLOAT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.resume_analysis (
    application_id UUID PRIMARY KEY REFERENCES recruiters.applications(id),
    summary TEXT,
    total_years_experience INT,
    average_permanency FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.strengths (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    strength TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.concerns (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    concern TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.hard_skills (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    skill VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.soft_skills (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    skill VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.key_responsibilities (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    responsibility TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.interview_questions (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    question TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.tags (
    id SERIAL PRIMARY KEY,
    application_id UUID REFERENCES recruiters.applications(id),
    tag VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO recruiters.applications (id, name, email, phone, cv, position_id)
VALUES
(gen_random_uuid(), 'Alice Smith', 'alice@example.com', '+123456789', '/path/to/cv1.pdf', 1),
(gen_random_uuid(), 'Bob Johnson', 'bob@example.com', '+987654321', '/path/to/cv2.pdf', 1),
(gen_random_uuid(), 'Charlie Brown', 'charlie@example.com', '+123123123', '/path/to/cv3.pdf', 1),
(gen_random_uuid(), 'David Wilson', 'david@example.com', '+321321321', '/path/to/cv4.pdf', 1),
(gen_random_uuid(), 'Eva Green', 'eva@example.com', '+456456456', '/path/to/cv5.pdf', 1),
(gen_random_uuid(), 'Frank White', 'frank@example.com', '+654654654', '/path/to/cv6.pdf', 1),
(gen_random_uuid(), 'Grace Black', 'grace@example.com', '+789789789', '/path/to/cv7.pdf', 1),
(gen_random_uuid(), 'Hannah Blue', 'hannah@example.com', '+987987987', '/path/to/cv8.pdf', 1),
(gen_random_uuid(), 'Ian Gray', 'ian@example.com', '+111222333', '/path/to/cv9.pdf', 1),
(gen_random_uuid(), 'Jack Brown', 'jack@example.com', '+444555666', '/path/to/cv10.pdf', 1),
(gen_random_uuid(), 'Karen White', 'karen@example.com', '+777888999', '/path/to/cv11.pdf', 1),
(gen_random_uuid(), 'Leo Green', 'leo@example.com', '+000111222', '/path/to/cv12.pdf', 1),
(gen_random_uuid(), 'Mia Black', 'mia@example.com', '+333444555', '/path/to/cv13.pdf', 1),
(gen_random_uuid(), 'Nina Blue', 'nina@example.com', '+666777888', '/path/to/cv14.pdf', 1),
(gen_random_uuid(), 'Oscar Gray', 'oscar@example.com', '+999000111', '/path/to/cv15.pdf', 1),
(gen_random_uuid(), 'Paul Brown', 'paul@example.com', '+222333444', '/path/to/cv16.pdf', 1),
(gen_random_uuid(), 'Quinn White', 'quinn@example.com', '+555666777', '/path/to/cv17.pdf', 1),
(gen_random_uuid(), 'Rachel Green', 'rachel@example.com', '+888999000', '/path/to/cv18.pdf', 1),
(gen_random_uuid(), 'Sam Black', 'sam@example.com', '+111222333', '/path/to/cv19.pdf', 1),
(gen_random_uuid(), 'Tina Blue', 'tina@example.com', '+444555666', '/path/to/cv20.pdf', 1),
(gen_random_uuid(), 'Uma Gray', 'uma@example.com', '+777888999', '/path/to/cv21.pdf', 1),
(gen_random_uuid(), 'Victor Brown', 'victor@example.com', '+000111222', '/path/to/cv22.pdf', 1);

