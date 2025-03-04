--- CANDIDATES ---
CREATE SCHEMA IF NOT EXISTS APPLICATIONS;

-- Tabla principal para las posiciones abiertas
CREATE TABLE APPLICATIONS.POSITIONS
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    tags         TEXT,
    applications INT          DEFAULT 0,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para los requisitos de cada posición
CREATE TABLE APPLICATIONS.REQUIREMENTS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.POSITIONS (id) ON DELETE CASCADE,
    key         VARCHAR(255) NOT NULL,
    value       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    mandatory   BOOLEAN NOT NULL
);

-- Tabla para las condiciones de cada posición
CREATE TABLE APPLICATIONS.CONDITIONS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.POSITIONS (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

CREATE TABLE APPLICATIONS.TASKS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

CREATE TABLE APPLICATIONS.APPLICATIONS
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    cv         VARCHAR(255) NOT NULL,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.positions (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO APPLICATIONS.POSITIONS (title, description, tags)
VALUES
    ('Software Engineer', 'Desarrollo y mantenimiento de aplicaciones web.', 'Java, Spring, Angular'),
    ('Data Scientist', 'Análisis de datos y modelos de Machine Learning.', 'Python, TensorFlow, SQL'),
    ('DevOps Engineer', 'Automatización e infraestructura en la nube.', 'AWS, Docker, Kubernetes'),
    ('Product Manager', 'Gestión de producto y estrategias de negocio.', 'Agile, Scrum, UX'),
    ('QA Engineer', 'Pruebas automatizadas y control de calidad.', 'Selenium, Cypress, Jest'),
    ('Backend Developer', 'Desarrollo de microservicios escalables.', 'Node.js, Express, PostgreSQL'),
    ('Frontend Developer', 'Implementación de interfaces web responsivas.', 'React, Vue, TypeScript'),
    ('Cloud Architect', 'Diseño de arquitectura cloud.', 'AWS, Azure, GCP'),
    ('Security Analyst', 'Análisis y mitigación de vulnerabilidades.', 'SIEM, Firewalls, PenTesting'),
    ('Blockchain Developer', 'Desarrollo de aplicaciones descentralizadas.', 'Ethereum, Solidity, Web3'),
    ('AI Researcher', 'Investigación en inteligencia artificial.', 'Deep Learning, PyTorch, NLP'),
    ('Cybersecurity Engineer', 'Seguridad ofensiva y defensiva.', 'SOC, IDS, Threat Intelligence'),
    ('Database Administrator', 'Gestión y optimización de bases de datos.', 'PostgreSQL, MySQL, MongoDB'),
    ('Game Developer', 'Desarrollo de videojuegos.', 'Unity, Unreal Engine, C#'),
    ('Business Analyst', 'Análisis de requerimientos y procesos.', 'Power BI, Tableau, SQL'),
    ('Network Engineer', 'Administración de redes y seguridad.', 'Cisco, TCP/IP, VPN'),
    ('Embedded Systems Engineer', 'Desarrollo de sistemas embebidos.', 'C, ARM, IoT'),
    ('Full Stack Developer', 'Desarrollo completo de aplicaciones.', 'MERN, LAMP, MEAN'),
    ('IoT Specialist', 'Desarrollo de soluciones IoT.', 'Arduino, Raspberry Pi, MQTT'),
    ('ERP Consultant', 'Consultoría e implementación de ERP.', 'SAP, Oracle, Dynamics'),
    ('AI Ethics Researcher', 'Estudio del impacto ético de la IA.', 'Filosofía, Derecho, IA'),
    ('UX/UI Designer', 'Diseño de interfaces y experiencia de usuario.', 'Figma, Adobe XD, HTML/CSS'),
    ('Robotics Engineer', 'Desarrollo de sistemas robóticos.', 'ROS, C++, Python'),
    ('Site Reliability Engineer', 'Mantenimiento y escalabilidad de sistemas.', 'GCP, Kubernetes, Terraform'),
    ('Marketing Data Analyst', 'Análisis de datos para marketing.', 'Google Analytics, Python, R'),
    ('Systems Administrator', 'Administración de sistemas IT.', 'Linux, Windows Server, Bash'),
    ('Mobile Developer', 'Desarrollo de aplicaciones móviles.', 'Swift, Kotlin, Flutter'),
    ('CRM Specialist', 'Gestión de clientes y automatización.', 'Salesforce, HubSpot, Zoho'),
    ('Cloud Security Engineer', 'Seguridad en la nube.', 'AWS Security, IAM, SIEM');

-- Insertar requisitos variados para cada posición
INSERT INTO APPLICATIONS.REQUIREMENTS (position_id, key, value, description, mandatory)
SELECT id, 'Experience', '3+ years', 'Relevant experience in the field.', true FROM APPLICATIONS.POSITIONS
UNION ALL
SELECT id, 'Certifications', 'Industry Standard', 'Preferred certifications for the role.', false FROM APPLICATIONS.POSITIONS
UNION ALL
SELECT id, 'Soft Skills', 'Communication & Teamwork', 'Required for effective collaboration.', true FROM APPLICATIONS.POSITIONS;

-- Insertar condiciones variadas para cada posición
INSERT INTO APPLICATIONS.CONDITIONS (position_id, description)
SELECT id, 'Trabajo remoto disponible' FROM APPLICATIONS.POSITIONS
UNION ALL
SELECT id, 'Horario flexible' FROM APPLICATIONS.POSITIONS
UNION ALL
SELECT id, 'Oportunidad de crecimiento profesional' FROM APPLICATIONS.POSITIONS;

-- Insertar tareas variadas para cada posición
INSERT INTO APPLICATIONS.TASKS (position_id, description)
SELECT id, 'Desarrollo de nuevas funcionalidades' FROM APPLICATIONS.POSITIONS
UNION ALL
SELECT id, 'Optimización de código existente' FROM APPLICATIONS.POSITIONS
UNION ALL
SELECT id, 'Colaboración con equipos multidisciplinarios' FROM APPLICATIONS.POSITIONS;

-----------------------------------------------------------------------------------------------------------------------
--- RECRUITERS ---

CREATE SCHEMA IF NOT EXISTS RECRUITERS;
CREATE TABLE RECRUITERS.positions
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    status       NUMERIC(2)   NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at DATE,
    tags         TEXT
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

CREATE TABLE IF NOT EXISTS recruiters.applications (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    cv VARCHAR(255) NOT NULL,
    position_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Insert positions
INSERT INTO RECRUITERS.positions (id, title, description, status, created_at, published_at, tags) VALUES
                                                                                                      (1, 'Java Developer', 'Java Developer', 0, '2021-01-01', '2021-01-10', 'Java, Spring'),
                                                                                                      (2, 'Python Developer', 'Python Developer', 1, '2021-01-01', '2021-01-10', 'Python, Django'),
                                                                                                      (3, 'Frontend Developer', 'Frontend Developer', 0, '2021-01-01', '2021-01-10', 'JavaScript, React'),
                                                                                                      (4, 'Backend Developer', 'Backend Developer', 1, '2021-01-01', '2021-01-10', 'Java, Spring Boot'),
                                                                                                      (5, 'Fullstack Developer', 'Fullstack Developer', 0, '2021-01-01', '2021-01-10', 'Java, React'),
                                                                                                      (6, 'DevOps Engineer', 'DevOps Engineer', 1, '2021-01-01', '2021-01-10', 'AWS, Docker'),
                                                                                                      (7, 'Data Scientist', 'Data Scientist', 0, '2021-01-01', '2021-01-10', 'Python, Machine Learning'),
                                                                                                      (8, 'Data Engineer', 'Data Engineer', 1, '2021-01-01', '2021-01-10', 'Python, ETL'),
                                                                                                      (9, 'Mobile Developer', 'Mobile Developer', 0, '2021-01-01', '2021-01-10', 'Kotlin, Android'),
                                                                                                      (10, 'iOS Developer', 'iOS Developer', 1, '2021-01-01', '2021-01-10', 'Swift, iOS'),
                                                                                                      (11, 'QA Engineer', 'QA Engineer', 0, '2021-01-01', '2021-01-10', 'Selenium, Testing'),
                                                                                                      (12, 'Project Manager', 'Project Manager', 1, '2021-01-01', '2021-01-10', 'Agile, Scrum'),
                                                                                                      (13, 'Business Analyst', 'Business Analyst', 0, '2021-01-01', '2021-01-10', 'Analysis, Requirements'),
                                                                                                      (14, 'System Administrator', 'System Administrator', 1, '2021-01-01', '2021-01-10', 'Linux, Networking'),
                                                                                                      (15, 'Network Engineer', 'Network Engineer', 0, '2021-01-01', '2021-01-10', 'Cisco, Networking'),
                                                                                                      (16, 'Security Engineer', 'Security Engineer', 1, '2021-01-01', '2021-01-10', 'Security, Penetration Testing'),
                                                                                                      (17, 'Database Administrator', 'Database Administrator', 0, '2021-01-01', '2021-01-10', 'SQL, Database'),
                                                                                                      (18, 'Cloud Engineer', 'Cloud Engineer', 1, '2021-01-01', '2021-01-10', 'AWS, Cloud'),
                                                                                                      (19, 'AI Engineer', 'AI Engineer', 0, '2021-01-01', '2021-01-10', 'AI, Machine Learning'),
                                                                                                      (20, 'Blockchain Developer', 'Blockchain Developer', 1, '2021-01-01', '2021-01-10', 'Blockchain, Solidity'),
                                                                                                      (21, 'Game Developer', 'Game Developer', 0, '2021-01-01', '2021-01-10', 'Unity, C#'),
                                                                                                      (22, 'Embedded Systems Engineer', 'Embedded Systems Engineer', 1, '2021-01-01', '2021-01-10', 'C, Embedded'),
                                                                                                      (23, 'Robotics Engineer', 'Robotics Engineer', 0, '2021-01-01', '2021-01-10', 'Robotics, C++'),
                                                                                                      (24, 'Site Reliability Engineer', 'Site Reliability Engineer', 1, '2021-01-01', '2021-01-10', 'SRE, DevOps'),
                                                                                                      (25, 'Technical Support Engineer', 'Technical Support Engineer', 0, '2021-01-01', '2021-01-10', 'Support, Troubleshooting'),
                                                                                                      (26, 'UI/UX Designer', 'UI/UX Designer', 1, '2021-01-01', '2021-01-10', 'UI, UX'),
                                                                                                      (27, 'Product Manager', 'Product Manager', 0, '2021-01-01', '2021-01-10', 'Product, Management'),
                                                                                                      (28, 'Sales Engineer', 'Sales Engineer', 1, '2021-01-01', '2021-01-10', 'Sales, Engineering'),
                                                                                                      (29, 'Marketing Specialist', 'Marketing Specialist', 0, '2021-01-01', '2021-01-10', 'Marketing, SEO'),
                                                                                                      (30, 'Content Writer', 'Content Writer', 1, '2021-01-01', '2021-01-10', 'Writing, Content');

-- Insert requirements
INSERT INTO RECRUITERS.requirements (id, position_id, key, value, description, mandatory) VALUES
                                                                                              (1, 1, 'Java', 'More than 5 years', 'Experience with Java', true),
                                                                                              (2, 2, 'Python', 'More than 5 years', 'Experience with Python', true),
                                                                                              (3, 3, 'JavaScript', 'More than 3 years', 'Experience with JavaScript', true),
                                                                                              (4, 4, 'Java', 'More than 5 years', 'Experience with Java', true),
                                                                                              (5, 5, 'Java', 'More than 5 years', 'Experience with Java', true),
                                                                                              (6, 6, 'AWS', 'More than 3 years', 'Experience with AWS', true),
                                                                                              (7, 7, 'Python', 'More than 3 years', 'Experience with Python', true),
                                                                                              (8, 8, 'Python', 'More than 3 years', 'Experience with Python', true),
                                                                                              (9, 9, 'Kotlin', 'More than 3 years', 'Experience with Kotlin', true),
                                                                                              (10, 10, 'Swift', 'More than 3 years', 'Experience with Swift', true),
                                                                                              (11, 11, 'Selenium', 'More than 3 years', 'Experience with Selenium', true),
                                                                                              (12, 12, 'Agile', 'More than 3 years', 'Experience with Agile', true),
                                                                                              (13, 13, 'Analysis', 'More than 3 years', 'Experience with Analysis', true),
                                                                                              (14, 14, 'Linux', 'More than 3 years', 'Experience with Linux', true),
                                                                                              (15, 15, 'Cisco', 'More than 3 years', 'Experience with Cisco', true),
                                                                                              (16, 16, 'Security', 'More than 3 years', 'Experience with Security', true),
                                                                                              (17, 17, 'SQL', 'More than 3 years', 'Experience with SQL', true),
                                                                                              (18, 18, 'AWS', 'More than 3 years', 'Experience with AWS', true),
                                                                                              (19, 19, 'AI', 'More than 3 years', 'Experience with AI', true),
                                                                                              (20, 20, 'Blockchain', 'More than 3 years', 'Experience with Blockchain', true),
                                                                                              (21, 21, 'Unity', 'More than 3 years', 'Experience with Unity', true),
                                                                                              (22, 22, 'C', 'More than 3 years', 'Experience with C', true),
                                                                                              (23, 23, 'Robotics', 'More than 3 years', 'Experience with Robotics', true),
                                                                                              (24, 24, 'SRE', 'More than 3 years', 'Experience with SRE', true),
                                                                                              (25, 25, 'Support', 'More than 3 years', 'Experience with Support', true),
                                                                                              (26, 26, 'UI', 'More than 3 years', 'Experience with UI', true),
                                                                                              (27, 27, 'Product', 'More than 3 years', 'Experience with Product', true),
                                                                                              (28, 28, 'Sales', 'More than 3 years', 'Experience with Sales', true),
                                                                                              (29, 29, 'Marketing', 'More than 3 years', 'Experience with Marketing', true),
                                                                                              (30, 30, 'Writing', 'More than 3 years', 'Experience with Writing', true);

-- Insert tasks
INSERT INTO RECRUITERS.tasks (id, position_id, description) VALUES
                                                                (1, 1, 'Develop Java applications'),
                                                                (2, 2, 'Develop Python applications'),
                                                                (3, 3, 'Develop frontend applications'),
                                                                (4, 4, 'Develop backend applications'),
                                                                (5, 5, 'Develop fullstack applications'),
                                                                (6, 6, 'Manage DevOps processes'),
                                                                (7, 7, 'Analyze data'),
                                                                (8, 8, 'Engineer data pipelines'),
                                                                (9, 9, 'Develop mobile applications'),
                                                                (10, 10, 'Develop iOS applications'),
                                                                (11, 11, 'Test applications'),
                                                                (12, 12, 'Manage projects'),
                                                                (13, 13, 'Analyze business requirements'),
                                                                (14, 14, 'Administer systems'),
                                                                (15, 15, 'Engineer networks'),
                                                                (16, 16, 'Ensure security'),
                                                                (17, 17, 'Administer databases'),
                                                                (18, 18, 'Engineer cloud solutions'),
                                                                (19, 19, 'Develop AI solutions'),
                                                                (20, 20, 'Develop blockchain solutions'),
                                                                (21, 21, 'Develop games'),
                                                                (22, 22, 'Engineer embedded systems'),
                                                                (23, 23, 'Engineer robotics solutions'),
                                                                (24, 24, 'Ensure site reliability'),
                                                                (25, 25, 'Provide technical support'),
                                                                (26, 26, 'Design UI/UX'),
                                                                (27, 27, 'Manage products'),
                                                                (28, 28, 'Engineer sales solutions'),
                                                                (29, 29, 'Specialize in marketing'),
                                                                (30, 30, 'Write content');

-- Insert benefits
INSERT INTO RECRUITERS.benefits (id, position_id, description) VALUES
                                                                   (1, 1, 'Health insurance'),
                                                                   (2, 2, 'Health insurance'),
                                                                   (3, 3, 'Health insurance'),
                                                                   (4, 4, 'Health insurance'),
                                                                   (5, 5, 'Health insurance'),
                                                                   (6, 6, 'Health insurance'),
                                                                   (7, 7, 'Health insurance'),
                                                                   (8, 8, 'Health insurance'),
                                                                   (9, 9, 'Health insurance'),
                                                                   (10, 10, 'Health insurance'),
                                                                   (11, 11, 'Health insurance'),
                                                                   (12, 12, 'Health insurance'),
                                                                   (13, 13, 'Health insurance'),
                                                                   (14, 14, 'Health insurance'),
                                                                   (15, 15, 'Health insurance'),
                                                                   (16, 16, 'Health insurance'),
                                                                   (17, 17, 'Health insurance'),
                                                                   (18, 18, 'Health insurance'),
                                                                   (19, 19, 'Health insurance'),
                                                                   (20, 20, 'Health insurance'),
                                                                   (21, 21, 'Health insurance'),
                                                                   (22, 22, 'Health insurance'),
                                                                   (23, 23, 'Health insurance'),
                                                                   (24, 24, 'Health insurance'),
                                                                   (25, 25, 'Health insurance'),
                                                                   (26, 26, 'Health insurance'),
                                                                   (27, 27, 'Health insurance'),
                                                                   (28, 28, 'Health insurance'),
                                                                   (29, 29, 'Health insurance'),
                                                                   (30, 30, 'Health insurance');


ALTER SEQUENCE RECRUITERS.positions_id_seq RESTART WITH 31;
ALTER SEQUENCE RECRUITERS.requirements_id_seq RESTART WITH 31;
ALTER SEQUENCE RECRUITERS.tasks_id_seq RESTART WITH 31;
ALTER SEQUENCE RECRUITERS.benefits_id_seq RESTART WITH 31;


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
