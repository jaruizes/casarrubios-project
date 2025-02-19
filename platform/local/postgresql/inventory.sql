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
CREATE TABLE RECRUITERS.APPLICATIONS
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    surname    VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    cv         VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE RECRUITERS.POSITIONS
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para los requisitos de cada posición
CREATE TABLE RECRUITERS.REQUIREMENTS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES RECRUITERS.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

-- Tabla para las condiciones de cada posición
CREATE TABLE RECRUITERS.CONDITIONS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES RECRUITERS.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);
