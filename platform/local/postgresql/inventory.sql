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
CREATE TABLE APPLICATIONS.POSITIONS_REQUIREMENTS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.POSITIONS (id) ON DELETE CASCADE,
    key         VARCHAR(255) NOT NULL,
    value       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    mandatory   BOOLEAN NOT NULL
);

-- Tabla para las condiciones de cada posición
CREATE TABLE APPLICATIONS.POSITIONS_BENEFITS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.POSITIONS (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

CREATE TABLE APPLICATIONS.POSITIONS_TASKS
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


/*INSERT INTO APPLICATIONS.POSITIONS (title, description, tags)
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
*/
-----------------------------------------------------------------------------------------------------------------------
--- RECRUITERS ---

CREATE SCHEMA IF NOT EXISTS RECRUITERS;
CREATE TABLE RECRUITERS.positions
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    status       SMALLINT   NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at DATE,
    tags         TEXT
);

CREATE TABLE RECRUITERS.requirements
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES RECRUITERS.positions (id) ON DELETE CASCADE,
    key VARCHAR(255) NOT NULL,
    value SMALLINT NOT NULL,
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


-- Insertar 14 posiciones
INSERT INTO RECRUITERS.positions (title, description, status, published_at, tags) VALUES
                                                                                      ('Software Engineer', 'Desarrollo y mantenimiento de aplicaciones de software.', 1, CURRENT_DATE, 'software, engineering'),
                                                                                      ('Data Analyst', 'Análisis de datos y generación de informes.', 1, CURRENT_DATE, 'data, analysis'),
                                                                                      ('DevOps Engineer', 'Automatización e integración de procesos de desarrollo y operaciones.', 1, CURRENT_DATE, 'devops, automation'),
                                                                                      ('Product Manager', 'Gestión de producto y coordinación de equipos multidisciplinarios.', 1, CURRENT_DATE, 'product, management'),
                                                                                      ('UX Designer', 'Diseño de interfaces y experiencia de usuario.', 1, CURRENT_DATE, 'ux, design'),
                                                                                      ('QA Engineer', 'Pruebas y aseguramiento de la calidad de software.', 1, CURRENT_DATE, 'qa, testing'),
                                                                                      ('Business Analyst', 'Análisis de procesos y requisitos de negocio.', 1, CURRENT_DATE, 'business, analysis'),
                                                                                      ('System Administrator', 'Administración y mantenimiento de infraestructuras IT.', 1, CURRENT_DATE, 'systems, administration'),
                                                                                      ('Mobile Developer', 'Desarrollo de aplicaciones móviles nativas y multiplataforma.', 1, CURRENT_DATE, 'mobile, development'),
                                                                                      ('Cloud Architect', 'Diseño y administración de infraestructuras en la nube.', 1, CURRENT_DATE, 'cloud, architecture'),
                                                                                      ('Frontend Developer', 'Desarrollo de interfaces web responsivas y modernas.', 1, CURRENT_DATE, 'frontend, web'),
                                                                                      ('Backend Developer', 'Diseño y desarrollo de servicios y APIs.', 1, CURRENT_DATE, 'backend, development'),
                                                                                      ('Database Administrator', 'Administración y optimización de bases de datos.', 1, CURRENT_DATE, 'database, admin'),
                                                                                      ('Security Analyst', 'Monitoreo y análisis de la seguridad en sistemas y redes.', 1, CURRENT_DATE, 'security, analysis');

-------------------------------------------
-- Insertar para cada posición 5 requisitos, 5 tareas y 5 beneficios.
-- Suponiendo que las posiciones tienen IDs de 1 a 14

-- Ejemplo para la posición con id = 1 (Software Engineer)
-- Se pueden repetir patrones para cada posición

-- Requisitos para posición 1
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (1, 'Requisito 1', 2, 'Experiencia mínima de 2 años en desarrollo de software.', TRUE),
                                                                                          (1, 'Requisito 2', 3, 'Conocimiento avanzado en programación orientada a objetos.', TRUE),
                                                                                          (1, 'Requisito 3', 1, 'Capacidad para trabajar en equipo.', TRUE),
                                                                                          (1, 'Requisito 4', 2, 'Conocimiento intermedio de bases de datos relacionales.', FALSE),
                                                                                          (1, 'Requisito 5', 1, 'Habilidad para resolver problemas de forma creativa.', TRUE);

-- Tareas para posición 1
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (1, 'Desarrollar nuevas funcionalidades según requerimientos.'),
                                                            (1, 'Realizar mantenimiento y mejoras en el código existente.'),
                                                            (1, 'Colaborar con el equipo de diseño y producto.'),
                                                            (1, 'Participar en revisiones de código y asegurar buenas prácticas.'),
                                                            (1, 'Realizar pruebas unitarias y de integración.');

-- Beneficios para posición 1
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (1, 'Salario competitivo acorde al mercado.'),
                                                               (1, 'Plan de salud integral.'),
                                                               (1, 'Bonificaciones por desempeño.'),
                                                               (1, 'Oportunidades de formación y desarrollo profesional.'),
                                                               (1, 'Ambiente de trabajo flexible y colaborativo.');

-------------------------------------------
-- Repetir para cada una de las 14 posiciones

-- Para la posición 2: Data Analyst
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (2, 'Requisito 1', 2, 'Experiencia mínima en análisis de datos y estadística.', TRUE),
                                                                                          (2, 'Requisito 2', 3, 'Dominio de herramientas de BI y visualización.', TRUE),
                                                                                          (2, 'Requisito 3', 2, 'Conocimiento en SQL y bases de datos relacionales.', TRUE),
                                                                                          (2, 'Requisito 4', 1, 'Habilidad para interpretar grandes volúmenes de datos.', FALSE),
                                                                                          (2, 'Requisito 5', 1, 'Capacidad analítica y atención al detalle.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (2, 'Recopilar y procesar datos de diversas fuentes.'),
                                                            (2, 'Diseñar y generar informes y dashboards.'),
                                                            (2, 'Realizar análisis estadísticos para la toma de decisiones.'),
                                                            (2, 'Colaborar con otros equipos para definir requerimientos.'),
                                                            (2, 'Optimizar consultas y procesos de extracción de datos.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (2, 'Acceso a capacitaciones y certificaciones.'),
                                                               (2, 'Plan de salud y seguro de vida.'),
                                                               (2, 'Bonificaciones anuales.'),
                                                               (2, 'Horario flexible.'),
                                                               (2, 'Ambiente de trabajo colaborativo.');

-------------------------------------------
-- Posición 3: DevOps Engineer
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (3, 'Requisito 1', 3, 'Experiencia en automatización de despliegues y CI/CD.', TRUE),
                                                                                          (3, 'Requisito 2', 2, 'Conocimiento en herramientas como Docker y Kubernetes.', TRUE),
                                                                                          (3, 'Requisito 3', 2, 'Familiaridad con sistemas Linux y scripting.', TRUE),
                                                                                          (3, 'Requisito 4', 1, 'Capacidad de trabajo en entornos ágiles.', FALSE),
                                                                                          (3, 'Requisito 5', 1, 'Habilidad para solucionar incidencias en producción.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (3, 'Implementar y mantener pipelines de CI/CD.'),
                                                            (3, 'Gestionar la infraestructura en la nube.'),
                                                            (3, 'Automatizar tareas operativas y de despliegue.'),
                                                            (3, 'Monitorear el rendimiento de los sistemas.'),
                                                            (3, 'Colaborar con equipos de desarrollo y operaciones.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (3, 'Seguro médico completo.'),
                                                               (3, 'Oportunidades de crecimiento profesional.'),
                                                               (3, 'Bonificaciones por objetivos alcanzados.'),
                                                               (3, 'Flexibilidad laboral.'),
                                                               (3, 'Acceso a herramientas y certificaciones especializadas.');

-------------------------------------------
-- Posición 4: Product Manager
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (4, 'Requisito 1', 3, 'Experiencia demostrable en gestión de productos.', TRUE),
                                                                                          (4, 'Requisito 2', 2, 'Conocimiento en metodologías ágiles.', TRUE),
                                                                                          (4, 'Requisito 3', 2, 'Habilidades de liderazgo y comunicación.', TRUE),
                                                                                          (4, 'Requisito 4', 1, 'Capacidad analítica para interpretar métricas.', FALSE),
                                                                                          (4, 'Requisito 5', 1, 'Orientación a resultados y gestión de proyectos.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (4, 'Definir la visión y estrategia del producto.'),
                                                            (4, 'Coordinar equipos multifuncionales.'),
                                                            (4, 'Elaborar roadmap y plan de lanzamiento.'),
                                                            (4, 'Recopilar feedback de usuarios y stakeholders.'),
                                                            (4, 'Monitorear métricas y KPIs del producto.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (4, 'Plan de incentivos y bonificaciones.'),
                                                               (4, 'Seguro médico y dental.'),
                                                               (4, 'Capacitaciones continuas.'),
                                                               (4, 'Horario flexible y remoto.'),
                                                               (4, 'Ambiente de trabajo innovador.');

-------------------------------------------
-- Posición 5: UX Designer
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (5, 'Requisito 1', 2, 'Experiencia en diseño de interfaces y prototipado.', TRUE),
                                                                                          (5, 'Requisito 2', 2, 'Dominio de herramientas como Sketch o Figma.', TRUE),
                                                                                          (5, 'Requisito 3', 1, 'Conocimiento en diseño centrado en el usuario.', TRUE),
                                                                                          (5, 'Requisito 4', 1, 'Habilidad para trabajar con equipos multidisciplinarios.', FALSE),
                                                                                          (5, 'Requisito 5', 1, 'Capacidad creativa y atención al detalle.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (5, 'Diseñar wireframes y prototipos interactivos.'),
                                                            (5, 'Realizar pruebas de usabilidad.'),
                                                            (5, 'Colaborar con desarrolladores y product managers.'),
                                                            (5, 'Investigar tendencias de diseño.'),
                                                            (5, 'Optimizar la experiencia del usuario en productos digitales.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (5, 'Ambiente creativo y colaborativo.'),
                                                               (5, 'Seguro de salud.'),
                                                               (5, 'Capacitación en nuevas herramientas de diseño.'),
                                                               (5, 'Flexibilidad horaria.'),
                                                               (5, 'Oportunidades de desarrollo profesional.');

-------------------------------------------
-- Posición 6: QA Engineer
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (6, 'Requisito 1', 2, 'Experiencia en pruebas de software y automatización.', TRUE),
                                                                                          (6, 'Requisito 2', 1, 'Conocimiento en herramientas de testing.', TRUE),
                                                                                          (6, 'Requisito 3', 1, 'Atención al detalle y capacidad analítica.', TRUE),
                                                                                          (6, 'Requisito 4', 1, 'Familiaridad con metodologías ágiles.', FALSE),
                                                                                          (6, 'Requisito 5', 1, 'Habilidad para documentar y reportar incidencias.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (6, 'Diseñar y ejecutar planes de prueba.'),
                                                            (6, 'Automatizar scripts de testing.'),
                                                            (6, 'Identificar y reportar bugs.'),
                                                            (6, 'Colaborar con desarrolladores para la resolución de problemas.'),
                                                            (6, 'Realizar pruebas de regresión.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (6, 'Seguro médico integral.'),
                                                               (6, 'Bonificaciones por desempeño.'),
                                                               (6, 'Capacitaciones en nuevas tecnologías.'),
                                                               (6, 'Horario flexible.'),
                                                               (6, 'Ambiente de trabajo dinámico.');

-------------------------------------------
-- Posición 7: Business Analyst
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (7, 'Requisito 1', 2, 'Experiencia en análisis y modelado de procesos de negocio.', TRUE),
                                                                                          (7, 'Requisito 2', 2, 'Conocimiento en herramientas de diagramación y análisis.', TRUE),
                                                                                          (7, 'Requisito 3', 1, 'Habilidad para recopilar y analizar requerimientos.', TRUE),
                                                                                          (7, 'Requisito 4', 1, 'Capacidad para comunicarse efectivamente con stakeholders.', FALSE),
                                                                                          (7, 'Requisito 5', 1, 'Conocimiento en metodologías ágiles.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (7, 'Reunir y documentar requerimientos de negocio.'),
                                                            (7, 'Elaborar diagramas de flujo y procesos.'),
                                                            (7, 'Coordinar reuniones con stakeholders.'),
                                                            (7, 'Realizar análisis de viabilidad y riesgos.'),
                                                            (7, 'Generar reportes y propuestas de mejora.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (7, 'Salario competitivo.'),
                                                               (7, 'Plan de desarrollo profesional.'),
                                                               (7, 'Seguro de salud.'),
                                                               (7, 'Flexibilidad horaria.'),
                                                               (7, 'Bonificaciones por cumplimiento de objetivos.');

-------------------------------------------
-- Posición 8: System Administrator
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (8, 'Requisito 1', 2, 'Experiencia en administración de sistemas Linux/Windows.', TRUE),
                                                                                          (8, 'Requisito 2', 2, 'Conocimiento en redes y protocolos de comunicación.', TRUE),
                                                                                          (8, 'Requisito 3', 1, 'Habilidad para gestionar servidores y servicios.', TRUE),
                                                                                          (8, 'Requisito 4', 1, 'Capacidad para resolver incidencias en infraestructura.', FALSE),
                                                                                          (8, 'Requisito 5', 1, 'Disponibilidad para trabajo en turnos, si es necesario.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (8, 'Administrar servidores y sistemas operativos.'),
                                                            (8, 'Configurar y mantener redes corporativas.'),
                                                            (8, 'Implementar medidas de seguridad y backup.'),
                                                            (8, 'Monitorear el rendimiento de la infraestructura.'),
                                                            (8, 'Soportar incidencias y mantenimiento preventivo.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (8, 'Acceso a cursos de certificación.'),
                                                               (8, 'Seguro médico.'),
                                                               (8, 'Bonificaciones trimestrales.'),
                                                               (8, 'Ambiente colaborativo.'),
                                                               (8, 'Flexibilidad en el horario.');

-------------------------------------------
-- Posición 9: Mobile Developer
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (9, 'Requisito 1', 2, 'Experiencia en desarrollo de aplicaciones móviles.', TRUE),
                                                                                          (9, 'Requisito 2', 2, 'Conocimiento en plataformas Android e iOS.', TRUE),
                                                                                          (9, 'Requisito 3', 1, 'Habilidad para trabajar con APIs REST.', TRUE),
                                                                                          (9, 'Requisito 4', 1, 'Capacidad para desarrollar interfaces responsivas.', FALSE),
                                                                                          (9, 'Requisito 5', 1, 'Conocimiento en metodologías ágiles.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (9, 'Desarrollar aplicaciones móviles nativas.'),
                                                            (9, 'Implementar funcionalidades en Android y iOS.'),
                                                            (9, 'Integrar APIs y servicios externos.'),
                                                            (9, 'Realizar pruebas de usabilidad y rendimiento.'),
                                                            (9, 'Colaborar con el equipo de diseño.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (9, 'Salario acorde al mercado.'),
                                                               (9, 'Seguro médico completo.'),
                                                               (9, 'Capacitaciones y conferencias técnicas.'),
                                                               (9, 'Ambiente flexible y dinámico.'),
                                                               (9, 'Bonificaciones por objetivos.');

-------------------------------------------
-- Posición 10: Cloud Architect
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (10, 'Requisito 1', 3, 'Experiencia en diseño y gestión de infraestructuras en la nube.', TRUE),
                                                                                          (10, 'Requisito 2', 2, 'Conocimiento avanzado en AWS, Azure o GCP.', TRUE),
                                                                                          (10, 'Requisito 3', 2, 'Habilidad para diseñar arquitecturas escalables.', TRUE),
                                                                                          (10, 'Requisito 4', 1, 'Capacidad para implementar estrategias de seguridad en la nube.', FALSE),
                                                                                          (10, 'Requisito 5', 1, 'Experiencia en migración de servicios a la nube.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (10, 'Diseñar arquitecturas cloud eficientes.'),
                                                            (10, 'Implementar soluciones de alta disponibilidad.'),
                                                            (10, 'Colaborar en proyectos de migración a la nube.'),
                                                            (10, 'Monitorear el rendimiento de los servicios cloud.'),
                                                            (10, 'Garantizar la seguridad y cumplimiento de normativas.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (10, 'Salario competitivo y acorde al rol.'),
                                                               (10, 'Plan de salud y bienestar.'),
                                                               (10, 'Acceso a certificaciones cloud.'),
                                                               (10, 'Horarios flexibles.'),
                                                               (10, 'Ambiente innovador y colaborativo.');

-------------------------------------------
-- Posición 11: Frontend Developer
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (11, 'Requisito 1', 2, 'Experiencia en desarrollo web con HTML, CSS y JavaScript.', TRUE),
                                                                                          (11, 'Requisito 2', 2, 'Conocimiento en frameworks como React o Angular.', TRUE),
                                                                                          (11, 'Requisito 3', 1, 'Habilidad para crear interfaces responsivas.', TRUE),
                                                                                          (11, 'Requisito 4', 1, 'Capacidad para optimizar el rendimiento web.', FALSE),
                                                                                          (11, 'Requisito 5', 1, 'Experiencia en integración con APIs REST.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (11, 'Desarrollar interfaces web interactivas.'),
                                                            (11, 'Implementar diseños responsivos.'),
                                                            (11, 'Optimizar la experiencia de usuario.'),
                                                            (11, 'Integrar servicios y APIs de backend.'),
                                                            (11, 'Colaborar con equipos de diseño y desarrollo.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (11, 'Salario competitivo.'),
                                                               (11, 'Seguro médico.'),
                                                               (11, 'Bonificaciones por desempeño.'),
                                                               (11, 'Capacitaciones y cursos técnicos.'),
                                                               (11, 'Ambiente de trabajo creativo.');

-------------------------------------------
-- Posición 12: Backend Developer
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (12, 'Requisito 1', 3, 'Experiencia en desarrollo de APIs y servicios web.', TRUE),
                                                                                          (12, 'Requisito 2', 2, 'Dominio de lenguajes como Java, Python o Node.js.', TRUE),
                                                                                          (12, 'Requisito 3', 2, 'Conocimiento en bases de datos relacionales y NoSQL.', TRUE),
                                                                                          (12, 'Requisito 4', 1, 'Capacidad para escribir código limpio y mantenible.', FALSE),
                                                                                          (12, 'Requisito 5', 1, 'Habilidad para integrar servicios externos.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (12, 'Diseñar e implementar APIs RESTful.'),
                                                            (12, 'Optimizar procesos y consultas de bases de datos.'),
                                                            (12, 'Colaborar con equipos de frontend y DevOps.'),
                                                            (12, 'Realizar pruebas unitarias e integradas.'),
                                                            (12, 'Mantener documentación técnica actualizada.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (12, 'Salario acorde a la experiencia.'),
                                                               (12, 'Seguro de salud integral.'),
                                                               (12, 'Bonificaciones periódicas.'),
                                                               (12, 'Oportunidades de crecimiento profesional.'),
                                                               (12, 'Ambiente colaborativo y flexible.');

-------------------------------------------
-- Posición 13: Database Administrator
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (13, 'Requisito 1', 2, 'Experiencia en administración y optimización de bases de datos.', TRUE),
                                                                                          (13, 'Requisito 2', 2, 'Conocimiento en SQL y sistemas de gestión de bases de datos.', TRUE),
                                                                                          (13, 'Requisito 3', 1, 'Capacidad para realizar backups y restauraciones.', TRUE),
                                                                                          (13, 'Requisito 4', 1, 'Habilidad para monitorear y solucionar cuellos de botella.', FALSE),
                                                                                          (13, 'Requisito 5', 1, 'Experiencia en entornos de alta disponibilidad.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (13, 'Administrar y monitorizar bases de datos.'),
                                                            (13, 'Realizar tareas de backup y recuperación.'),
                                                            (13, 'Optimizar consultas y procesos de datos.'),
                                                            (13, 'Implementar medidas de seguridad en las bases.'),
                                                            (13, 'Colaborar con equipos de desarrollo para mejoras.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (13, 'Salario competitivo.'),
                                                               (13, 'Seguro médico y dental.'),
                                                               (13, 'Plan de formación continua.'),
                                                               (13, 'Flexibilidad horaria.'),
                                                               (13, 'Bonificaciones por resultados.');

-------------------------------------------
-- Posición 14: Security Analyst
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (14, 'Requisito 1', 2, 'Experiencia en análisis de vulnerabilidades y riesgos.', TRUE),
                                                                                          (14, 'Requisito 2', 2, 'Conocimiento en normativas y estándares de seguridad.', TRUE),
                                                                                          (14, 'Requisito 3', 1, 'Capacidad para identificar y mitigar amenazas.', TRUE),
                                                                                          (14, 'Requisito 4', 1, 'Familiaridad con herramientas de monitoreo de seguridad.', FALSE),
                                                                                          (14, 'Requisito 5', 1, 'Habilidad para elaborar reportes de incidentes.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (14, 'Monitorear la seguridad de sistemas y redes.'),
                                                            (14, 'Realizar auditorías de seguridad periódicas.'),
                                                            (14, 'Investigar incidentes y vulnerabilidades.'),
                                                            (14, 'Implementar medidas de mitigación y mejora.'),
                                                            (14, 'Colaborar con equipos de IT para la seguridad integral.');

INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (14, 'Salario competitivo.'),
                                                               (14, 'Seguro médico integral.'),
                                                               (14, 'Capacitación en seguridad y certificaciones.'),
                                                               (14, 'Flexibilidad y posibilidad de trabajo remoto.'),
                                                               (14, 'Bonificaciones por desempeño.');
