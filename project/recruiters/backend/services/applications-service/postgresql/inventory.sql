CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SCHEMA IF NOT EXISTS recruiters;

CREATE TABLE IF NOT EXISTS recruiters.candidates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    cv VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX idx_unique_lower_email ON recruiters.candidates (LOWER(email));

CREATE TABLE IF NOT EXISTS recruiters.candidate_applications (
    id UUID PRIMARY KEY,
    candidate_id UUID NOT NULL REFERENCES recruiters.candidates(id) ON DELETE CASCADE,
    position_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_candidate_applications_position_id ON recruiters.candidate_applications (position_id);


CREATE TABLE IF NOT EXISTS recruiters.application_scoring (
    application_id UUID PRIMARY KEY REFERENCES recruiters.candidate_applications(id),
    score FLOAT NOT NULL,
    desc_score FLOAT NOT NULL,
    requirement_score FLOAT NOT NULL,
    tasks_score FLOAT NOT NULL,
    time_spent FLOAT NOT NULL,
    explanation TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_analysis (
    candidate_id UUID PRIMARY KEY REFERENCES recruiters.candidates(id),
    summary TEXT,
    total_years_experience INT,
    average_permanency FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_strengths (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    strength TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_concerns (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    concern TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_hard_skills (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    skill VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_soft_skills (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    skill VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_key_responsibilities (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    responsibility TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_interview_questions (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    question TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters.candidate_tags (
    id SERIAL PRIMARY KEY,
    candidate_id UUID REFERENCES recruiters.candidates(id),
    tag VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('d99ed287-87e5-45cc-9b28-750f37523202', 'Alice Smith', 'alice@example.com', '+123456789', '/path/to/cv1.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('3982bb09-f0e4-42aa-8606-aa8ad3a63055', 'Bob Johnson', 'bob@example.com', '+987654321', '/path/to/cv2.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('b7e1fd0a-79b0-417f-abe9-ff4d17659073', 'Charlie Brown', 'charlie@example.com', '+123123123', '/path/to/cv3.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('f5b27420-3a6e-4590-8772-60e475692e2a', 'David Wilson', 'david@example.com', '+321321321', '/path/to/cv4.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('199483c9-c1fe-413c-912c-8f6f55bbac5d', 'Eva Green', 'eva@example.com', '+456456456', '/path/to/cv5.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('dd8ae6aa-83ea-4400-af15-f42d335ee352', 'Frank White', 'frank@example.com', '+654654654', '/path/to/cv6.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('b8daf4b6-d67e-4745-8109-d7dd23682d46', 'Grace Black', 'grace@example.com', '+789789789', '/path/to/cv7.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('2b111708-8ef7-45d5-b356-5213f4b85946', 'Hannah Blue', 'hannah@example.com', '+987987987', '/path/to/cv8.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('a18ce97c-6139-409d-838c-d4be38ec51d0', 'Ian Gray', 'ian@example.com', '+111222333', '/path/to/cv9.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('a330eacd-2ffa-426d-8c77-32aa9b2287e2', 'Jack Brown', 'jack@example.com', '+444555666', '/path/to/cv10.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('725870d1-7522-4bb3-920c-11537bc6d605', 'Karen White', 'karen@example.com', '+777888999', '/path/to/cv11.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('0f005de9-313f-4e75-8658-097ad894a1e7', 'Leo Green', 'leo@example.com', '+000111222', '/path/to/cv12.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('2e71f858-dfd4-4fcb-a312-cd3c4ef6bec2', 'Mia Black', 'mia@example.com', '+333444555', '/path/to/cv13.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('da396bb6-5381-4fd1-8bbc-d633d3340400', 'Nina Blue', 'nina@example.com', '+666777888', '/path/to/cv14.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('50f042ec-b3c3-477b-ab66-9bebb546160a', 'Oscar Gray', 'oscar@example.com', '+999000111', '/path/to/cv15.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('b596e9a6-7775-429e-903c-85c88360782b', 'Paul Brown', 'paul@example.com', '+222333444', '/path/to/cv16.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('f56a32ae-8385-4255-a801-8b31f9694f2a', 'Quinn White', 'quinn@example.com', '+555666777', '/path/to/cv17.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('f3752077-315f-4d50-bb7d-8458b303f1ad', 'Rachel Green', 'rachel@example.com', '+888999000', '/path/to/cv18.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('361ea9c5-7cc2-4800-b1d9-6823c9925903', 'Sam Black', 'sam@example.com', '+111222333', '/path/to/cv19.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('019ccb5e-478c-4eeb-a1b6-43e0e1b6fe0d', 'Tina Blue', 'tina@example.com', '+444555666', '/path/to/cv20.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('fb2700db-7365-43bd-8f8b-d5b3d438d1a5', 'Uma Gray', 'uma@example.com', '+777888999', '/path/to/cv21.pdf');
INSERT INTO recruiters.candidates (id, name, email, phone, cv) VALUES ('5e3716e4-308b-439e-9f2a-94f0b418b665', 'Victor Brown', 'victor@example.com', '+000111222', '/path/to/cv22.pdf');
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('785cd281-b2c5-4f38-b7ae-ac556d825c4c', 'd99ed287-87e5-45cc-9b28-750f37523202', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('b5299ca1-fa06-41b7-817d-3f33950f94f7', '3982bb09-f0e4-42aa-8606-aa8ad3a63055', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('baadd49a-5ea2-4d7d-bb69-9c842369a8d4', 'b7e1fd0a-79b0-417f-abe9-ff4d17659073', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('b8d9350a-e38a-4fc1-8110-b2bcf060362b', 'f5b27420-3a6e-4590-8772-60e475692e2a', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('9c8cc1da-3906-41eb-ba7e-0cdc4aef40eb', '199483c9-c1fe-413c-912c-8f6f55bbac5d', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('c449c98b-0dfd-41ca-a46c-2b4dc51559e5', 'dd8ae6aa-83ea-4400-af15-f42d335ee352', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('09081088-63bc-4a1c-88dc-6ebcf32b584f', 'b8daf4b6-d67e-4745-8109-d7dd23682d46', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('4deb4b35-a021-4fe3-aef6-76ee03616306', '2b111708-8ef7-45d5-b356-5213f4b85946', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('0894eb35-626d-4ed3-ba7f-a550ca13b1b9', 'a18ce97c-6139-409d-838c-d4be38ec51d0', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('efed5542-89bd-40a8-bafb-c87e7e7ba149', 'a330eacd-2ffa-426d-8c77-32aa9b2287e2', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('2e360fef-21ec-4027-94e0-ea0231330e30', '725870d1-7522-4bb3-920c-11537bc6d605', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('fba3e1cb-ee96-4eb1-9a20-0ba77ac4686e', '0f005de9-313f-4e75-8658-097ad894a1e7', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('06542bdb-a378-4d59-8147-104032622ad0', '2e71f858-dfd4-4fcb-a312-cd3c4ef6bec2', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('ba8919e7-332a-41b4-b304-f504254c03a7', 'da396bb6-5381-4fd1-8bbc-d633d3340400', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('96daa1e1-4c4f-4561-8aba-bd73af7456c6', '50f042ec-b3c3-477b-ab66-9bebb546160a', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('7a31829a-fd5c-45d8-a627-84aa47d0fbd4', 'b596e9a6-7775-429e-903c-85c88360782b', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('b86cd65b-c8d0-40d8-aac0-33facb145b11', 'f56a32ae-8385-4255-a801-8b31f9694f2a', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('b8ca09bf-242c-4f65-9f86-3ea5e84411c9', 'f3752077-315f-4d50-bb7d-8458b303f1ad', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('ebaf4efb-c363-4f15-bf41-5590e7deb8f1', '361ea9c5-7cc2-4800-b1d9-6823c9925903', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('98449230-3156-42ae-83d8-541a043f2f8a', '019ccb5e-478c-4eeb-a1b6-43e0e1b6fe0d', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('a9800373-b90c-4509-930c-2e78c6661015', 'fb2700db-7365-43bd-8f8b-d5b3d438d1a5', 1);
INSERT INTO recruiters.candidate_applications (id, candidate_id, position_id) VALUES ('03391f8d-15ce-4cd6-88df-cf5dfc72d064', '5e3716e4-308b-439e-9f2a-94f0b418b665', 1);

-- Scoring de la aplicación
INSERT INTO recruiters.application_scoring (
    application_id, score, desc_score, requirement_score, tasks_score, time_spent, explanation
) VALUES (
    '785cd281-b2c5-4f38-b7ae-ac556d825c4c', 0.85, 0.80, 0.90, 0.85, 3.5, 'Candidate matches requirements and has relevant experience.'
);

-- Análisis del candidato
INSERT INTO recruiters.candidate_analysis (
    candidate_id, summary, total_years_experience, average_permanency
) VALUES (
    'd99ed287-87e5-45cc-9b28-750f37523202', 'Experienced backend developer with strong problem-solving skills.', 10, 2.5
);

-- Fortalezas
INSERT INTO recruiters.candidate_strengths (candidate_id, strength) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', 'Strong problem-solving skills'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Experience with microservices'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Good communication and teamwork');

-- Preocupaciones
INSERT INTO recruiters.candidate_concerns (candidate_id, concern) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', 'Limited experience with frontend technologies');

-- Hard skills
INSERT INTO recruiters.candidate_hard_skills (candidate_id, skill, level) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', 'Java', 'Avanzado'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Spring Boot', 'Avanzado'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Kafka', 'Intermedio');

-- Soft skills
INSERT INTO recruiters.candidate_soft_skills (candidate_id, skill, level) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', 'Comunicación', 'Avanzado'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Trabajo en equipo', 'Avanzado');

-- Responsabilidades clave
INSERT INTO recruiters.candidate_key_responsibilities (candidate_id, responsibility) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', 'Diseño e implementación de APIs REST'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Gestión de servicios en producción');

-- Preguntas de entrevista
INSERT INTO recruiters.candidate_interview_questions (candidate_id, question) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', '¿Cómo aseguras la escalabilidad de un microservicio?'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'Describe un desafío técnico reciente y cómo lo resolviste.');

-- Tags
INSERT INTO recruiters.candidate_tags (candidate_id, tag) VALUES
('d99ed287-87e5-45cc-9b28-750f37523202', 'backend'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'microservices'),
('d99ed287-87e5-45cc-9b28-750f37523202', 'java');

