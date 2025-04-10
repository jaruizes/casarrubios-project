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
    id         UUID PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    cv         VARCHAR(255) NOT NULL,
    position_id INT  NOT NULL REFERENCES APPLICATIONS.positions (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-----------------------------------------------------------------------------------------------------------------------
--- RECRUITERS ---

CREATE SCHEMA IF NOT EXISTS RECRUITERS;

CREATE TABLE recruiters.outbox (
   id UUID PRIMARY KEY,
   aggregatetype VARCHAR(255) NOT NULL,
   aggregateid VARCHAR(255) NOT NULL,
   type VARCHAR(255) NOT NULL,
   payload TEXT NOT NULL,
   timestamp TIMESTAMP DEFAULT now(),
   tracingspancontext TEXT
);



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


-- Insertar posiciones
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


-- Insertar requisitos para cada posición
INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (1, 'Experiencia', 2, 'Experiencia mínima de 2 años en desarrollo de software.', TRUE),
                                                                                          (1, 'Conocimiento', 3, 'Conocimiento avanzado en programación orientada a objetos.', TRUE),
                                                                                          (1, 'Capacidad', 1, 'Capacidad para trabajar en equipo.', TRUE),
                                                                                          (1, 'Conocimiento', 2, 'Conocimiento intermedio de bases de datos relacionales.', FALSE),
                                                                                          (1, 'Habilidad', 1, 'Habilidad para resolver problemas de forma creativa.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (2, 'Experiencia', 2, 'Experiencia mínima en análisis de datos y estadística.', TRUE),
                                                                                          (2, 'Dominio BI', 3, 'Dominio de herramientas de BI y visualización.', TRUE),
                                                                                          (2, 'Conocimiento SQL', 2, 'Conocimiento en SQL y bases de datos relacionales.', TRUE),
                                                                                          (2, 'Habilidad', 1, 'Habilidad para interpretar grandes volúmenes de datos.', FALSE),
                                                                                          (2, 'Capacidad', 1, 'Capacidad analítica y atención al detalle.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (3, 'Experiencia CI/CD', 3, 'Experiencia en automatización de despliegues y CI/CD.', TRUE),
                                                                                          (3, 'Conocimiento Docker Kubernetes', 2, 'Conocimiento en herramientas como Docker y Kubernetes.', TRUE),
                                                                                          (3, 'Familiaridad Linux', 2, 'Familiaridad con sistemas Linux y scripting.', TRUE),
                                                                                          (3, 'Capacidad', 1, 'Capacidad de trabajo en entornos ágiles.', FALSE),
                                                                                          (3, 'Habilidad', 1, 'Habilidad para solucionar incidencias en producción.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (4, 'Experiencia', 3, 'Experiencia demostrable en gestión de productos.', TRUE),
                                                                                          (4, 'Conocimiento', 2, 'Conocimiento en metodologías ágiles.', TRUE),
                                                                                          (4, 'Habilidades', 2, 'Habilidades de liderazgo y comunicación.', TRUE),
                                                                                          (4, 'Capacidad', 1, 'Capacidad analítica para interpretar métricas.', FALSE),
                                                                                          (4, 'Orientación', 1, 'Orientación a resultados y gestión de proyectos.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (5, 'Experiencia', 2, 'Experiencia en diseño de interfaces y prototipado.', TRUE),
                                                                                          (5, 'Dominio Sketch Figma', 2, 'Dominio de herramientas como Sketch o Figma.', TRUE),
                                                                                          (5, 'Conocimiento', 1, 'Conocimiento en diseño centrado en el usuario.', TRUE),
                                                                                          (5, 'Habilidad', 1, 'Habilidad para trabajar con equipos multidisciplinarios.', FALSE),
                                                                                          (5, 'Capacidad', 1, 'Capacidad creativa y atención al detalle.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (6, 'Experiencia', 2, 'Experiencia en pruebas de software y automatización.', TRUE),
                                                                                          (6, 'Conocimiento', 1, 'Conocimiento en herramientas de testing.', TRUE),
                                                                                          (6, 'Atención', 1, 'Atención al detalle y capacidad analítica.', TRUE),
                                                                                          (6, 'Familiaridad', 1, 'Familiaridad con metodologías ágiles.', FALSE),
                                                                                          (6, 'Habilidad', 1, 'Habilidad para documentar y reportar incidencias.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (7, 'Experiencia', 2, 'Experiencia en análisis y modelado de procesos de negocio.', TRUE),
                                                                                          (7, 'Conocimiento', 2, 'Conocimiento en herramientas de diagramación y análisis.', TRUE),
                                                                                          (7, 'Habilidad', 1, 'Habilidad para recopilar y analizar requerimientos.', TRUE),
                                                                                          (7, 'Capacidad', 1, 'Capacidad para comunicarse efectivamente con stakeholders.', FALSE),
                                                                                          (7, 'Conocimiento', 1, 'Conocimiento en metodologías ágiles.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (8, 'Experiencia Linux/Windows', 2, 'Experiencia en administración de sistemas Linux/Windows.', TRUE),
                                                                                          (8, 'Conocimiento', 2, 'Conocimiento en redes y protocolos de comunicación.', TRUE),
                                                                                          (8, 'Habilidad', 1, 'Habilidad para gestionar servidores y servicios.', TRUE),
                                                                                          (8, 'Capacidad', 1, 'Capacidad para resolver incidencias en infraestructura.', FALSE),
                                                                                          (8, 'Disponibilidad', 1, 'Disponibilidad para trabajo en turnos, si es necesario.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (9, 'Experiencia', 2, 'Experiencia en desarrollo de aplicaciones móviles.', TRUE),
                                                                                          (9, 'Conocimiento Android', 2, 'Conocimiento en plataformas Android e iOS.', TRUE),
                                                                                          (9, 'Habilidad APIs REST', 1, 'Habilidad para trabajar con APIs REST.', TRUE),
                                                                                          (9, 'Capacidad', 1, 'Capacidad para desarrollar interfaces responsivas.', FALSE),
                                                                                          (9, 'Conocimiento', 1, 'Conocimiento en metodologías ágiles.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (10, 'Experiencia', 3, 'Experiencia en diseño y gestión de infraestructuras en la nube.', TRUE),
                                                                                          (10, 'Conocimiento AWS, Azure GCP', 2, 'Conocimiento avanzado en AWS, Azure o GCP.', TRUE),
                                                                                          (10, 'Habilidad', 2, 'Habilidad para diseñar arquitecturas escalables.', TRUE),
                                                                                          (10, 'Capacidad', 1, 'Capacidad para implementar estrategias de seguridad en la nube.', FALSE),
                                                                                          (10, 'Experiencia', 1, 'Experiencia en migración de servicios a la nube.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (11, 'Experiencia HTML, CSS JavaScript', 2, 'Experiencia en desarrollo web con HTML, CSS y JavaScript.', TRUE),
                                                                                          (11, 'Conocimiento React Angular', 2, 'Conocimiento en frameworks como React o Angular.', TRUE),
                                                                                          (11, 'Habilidad', 1, 'Habilidad para crear interfaces responsivas.', TRUE),
                                                                                          (11, 'Capacidad', 1, 'Capacidad para optimizar el rendimiento web.', FALSE),
                                                                                          (11, 'Experiencia APIs REST', 1, 'Experiencia en integración con APIs REST.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (12, 'Experiencia APIs', 3, 'Experiencia en desarrollo de APIs y servicios web.', TRUE),
                                                                                          (12, 'Dominio Java, Python Nodejs', 2, 'Dominio de lenguajes como Java, Python o Node.js.', TRUE),
                                                                                          (12, 'Conocimiento NoSQL', 2, 'Conocimiento en bases de datos relacionales y NoSQL.', TRUE),
                                                                                          (12, 'Capacidad', 1, 'Capacidad para escribir código limpio y mantenible.', FALSE),
                                                                                          (12, 'Habilidad', 1, 'Habilidad para integrar servicios externos.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (13, 'Experiencia', 2, 'Experiencia en administración y optimización de bases de datos.', TRUE),
                                                                                          (13, 'Conocimiento SQL', 2, 'Conocimiento en SQL y sistemas de gestión de bases de datos.', TRUE),
                                                                                          (13, 'Capacidad', 1, 'Capacidad para realizar backups y restauraciones.', TRUE),
                                                                                          (13, 'Habilidad', 1, 'Habilidad para monitorear y solucionar cuellos de botella.', FALSE),
                                                                                          (13, 'Experiencia', 1, 'Experiencia en entornos de alta disponibilidad.', TRUE);

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory) VALUES
                                                                                          (14, 'Experiencia', 2, 'Experiencia en análisis de vulnerabilidades y riesgos.', TRUE),
                                                                                          (14, 'Conocimiento', 2, 'Conocimiento en normativas y estándares de seguridad.', TRUE),
                                                                                          (14, 'Capacidad', 1, 'Capacidad para identificar y mitigar amenazas.', TRUE),
                                                                                          (14, 'Familiaridad', 1, 'Familiaridad con herramientas de monitoreo de seguridad.', FALSE),
                                                                                          (14, 'Habilidad', 1, 'Habilidad para elaborar reportes de incidentes.', TRUE);

-- Insertar tareas y beneficios

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

-- Tareas para posición 2
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (2, 'Recopilar y procesar datos de diversas fuentes.'),
                                                            (2, 'Diseñar y generar informes y dashboards.'),
                                                            (2, 'Realizar análisis estadísticos para la toma de decisiones.'),
                                                            (2, 'Colaborar con otros equipos para definir requerimientos.'),
                                                            (2, 'Optimizar consultas y procesos de extracción de datos.');

-- Beneficios para posición 2
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (2, 'Acceso a capacitaciones y certificaciones.'),
                                                               (2, 'Plan de salud y seguro de vida.'),
                                                               (2, 'Bonificaciones anuales.'),
                                                               (2, 'Horario flexible.'),
                                                               (2, 'Ambiente de trabajo colaborativo.');

-- Tareas para posición 3
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (3, 'Implementar y mantener pipelines de CI/CD.'),
                                                            (3, 'Gestionar la infraestructura en la nube.'),
                                                            (3, 'Automatizar tareas operativas y de despliegue.'),
                                                            (3, 'Monitorear el rendimiento de los sistemas.'),
                                                            (3, 'Colaborar con equipos de desarrollo y operaciones.');

-- Beneficios para posición 3
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (3, 'Seguro médico completo.'),
                                                               (3, 'Oportunidades de crecimiento profesional.'),
                                                               (3, 'Bonificaciones por objetivos alcanzados.'),
                                                               (3, 'Flexibilidad laboral.'),
                                                               (3, 'Acceso a herramientas y certificaciones especializadas.');

-- Tareas para posición 4
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (4, 'Definir la visión y estrategia del producto.'),
                                                            (4, 'Coordinar equipos multifuncionales.'),
                                                            (4, 'Elaborar roadmap y plan de lanzamiento.'),
                                                            (4, 'Recopilar feedback de usuarios y stakeholders.'),
                                                            (4, 'Monitorear métricas y KPIs del producto.');

-- Beneficios para posición 4
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (4, 'Plan de incentivos y bonificaciones.'),
                                                               (4, 'Seguro médico y dental.'),
                                                               (4, 'Capacitaciones continuas.'),
                                                               (4, 'Horario flexible y remoto.'),
                                                               (4, 'Ambiente de trabajo innovador.');

-- Tareas para posición 5
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (5, 'Diseñar wireframes y prototipos interactivos.'),
                                                            (5, 'Realizar pruebas de usabilidad.'),
                                                            (5, 'Colaborar con desarrolladores y product managers.'),
                                                            (5, 'Investigar tendencias de diseño.'),
                                                            (5, 'Optimizar la experiencia del usuario en productos digitales.');

-- Beneficios para posición 5
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (5, 'Ambiente creativo y colaborativo.'),
                                                               (5, 'Seguro de salud.'),
                                                               (5, 'Capacitación en nuevas herramientas de diseño.'),
                                                               (5, 'Flexibilidad horaria.'),
                                                               (5, 'Oportunidades de desarrollo profesional.');

-- Tareas para posición 6
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (6, 'Diseñar y ejecutar planes de prueba.'),
                                                            (6, 'Automatizar scripts de testing.'),
                                                            (6, 'Identificar y reportar bugs.'),
                                                            (6, 'Colaborar con desarrolladores para la resolución de problemas.'),
                                                            (6, 'Realizar pruebas de regresión.');

-- Beneficios para posición 6
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (6, 'Seguro médico integral.'),
                                                               (6, 'Bonificaciones por desempeño.'),
                                                               (6, 'Capacitaciones en nuevas tecnologías.'),
                                                               (6, 'Horario flexible.'),
                                                               (6, 'Ambiente de trabajo dinámico.');

-- Tareas para posición 7
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (7, 'Reunir y documentar requerimientos de negocio.'),
                                                            (7, 'Elaborar diagramas de flujo y procesos.'),
                                                            (7, 'Coordinar reuniones con stakeholders.'),
                                                            (7, 'Realizar análisis de viabilidad y riesgos.'),
                                                            (7, 'Generar reportes y propuestas de mejora.');

-- Beneficios para posición 7
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (7, 'Salario competitivo.'),
                                                               (7, 'Plan de desarrollo profesional.'),
                                                               (7, 'Seguro de salud.'),
                                                               (7, 'Flexibilidad horaria.'),
                                                               (7, 'Bonificaciones por cumplimiento de objetivos.');

-- Tareas para posición 8
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (8, 'Administrar servidores y sistemas operativos.'),
                                                            (8, 'Configurar y mantener redes corporativas.'),
                                                            (8, 'Implementar medidas de seguridad y backup.'),
                                                            (8, 'Monitorear el rendimiento de la infraestructura.'),
                                                            (8, 'Soportar incidencias y mantenimiento preventivo.');

-- Beneficios para posición 8
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (8, 'Acceso a cursos de certificación.'),
                                                               (8, 'Seguro médico.'),
                                                               (8, 'Bonificaciones trimestrales.'),
                                                               (8, 'Ambiente colaborativo.'),
                                                               (8, 'Flexibilidad en el horario.');

-- Tareas para posición 9
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (9, 'Desarrollar aplicaciones móviles nativas.'),
                                                            (9, 'Implementar funcionalidades en Android y iOS.'),
                                                            (9, 'Integrar APIs y servicios externos.'),
                                                            (9, 'Realizar pruebas de usabilidad y rendimiento.'),
                                                            (9, 'Colaborar con el equipo de diseño.');

-- Beneficios para posición 9
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (9, 'Salario acorde al mercado.'),
                                                               (9, 'Seguro médico completo.'),
                                                               (9, 'Capacitaciones y conferencias técnicas.'),
                                                               (9, 'Ambiente flexible y dinámico.'),
                                                               (9, 'Bonificaciones por objetivos.');

-- Tareas para posición 10
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (10, 'Diseñar arquitecturas cloud eficientes.'),
                                                            (10, 'Implementar soluciones de alta disponibilidad.'),
                                                            (10, 'Colaborar en proyectos de migración a la nube.'),
                                                            (10, 'Monitorear el rendimiento de los servicios cloud.'),
                                                            (10, 'Garantizar la seguridad y cumplimiento de normativas.');

-- Beneficios para posición 10
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (10, 'Salario competitivo y acorde al rol.'),
                                                               (10, 'Plan de salud y bienestar.'),
                                                               (10, 'Acceso a certificaciones cloud.'),
                                                               (10, 'Horarios flexibles.'),
                                                               (10, 'Ambiente innovador y colaborativo.');

-- Tareas para posición 11
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (11, 'Desarrollar interfaces web interactivas.'),
                                                            (11, 'Implementar diseños responsivos.'),
                                                            (11, 'Optimizar la experiencia de usuario.'),
                                                            (11, 'Integrar servicios y APIs de backend.'),
                                                            (11, 'Colaborar con equipos de diseño y desarrollo.');

-- Beneficios para posición 11
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (11, 'Salario competitivo.'),
                                                               (11, 'Seguro médico.'),
                                                               (11, 'Bonificaciones por desempeño.'),
                                                               (11, 'Capacitaciones y cursos técnicos.'),
                                                               (11, 'Ambiente de trabajo creativo.');

-- Tareas para posición 12
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (12, 'Diseñar e implementar APIs RESTful.'),
                                                            (12, 'Optimizar procesos y consultas de bases de datos.'),
                                                            (12, 'Colaborar con equipos de frontend y DevOps.'),
                                                            (12, 'Realizar pruebas unitarias e integradas.'),
                                                            (12, 'Mantener documentación técnica actualizada.');

-- Beneficios para posición 12
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (12, 'Salario acorde a la experiencia.'),
                                                               (12, 'Seguro de salud integral.'),
                                                               (12, 'Bonificaciones periódicas.'),
                                                               (12, 'Oportunidades de crecimiento profesional.'),
                                                               (12, 'Ambiente colaborativo y flexible.');

-- Tareas para posición 13
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (13, 'Administrar y monitorizar bases de datos.'),
                                                            (13, 'Realizar tareas de backup y recuperación.'),
                                                            (13, 'Optimizar consultas y procesos de datos.'),
                                                            (13, 'Implementar medidas de seguridad en las bases.'),
                                                            (13, 'Colaborar con equipos de desarrollo para mejoras.');

-- Beneficios para posición 13
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (13, 'Salario competitivo.'),
                                                               (13, 'Seguro médico y dental.'),
                                                               (13, 'Plan de formación continua.'),
                                                               (13, 'Flexibilidad horaria.'),
                                                               (13, 'Bonificaciones por resultados.');

-- Tareas para posición 14
INSERT INTO RECRUITERS.tasks (position_id, description) VALUES
                                                            (14, 'Monitorear la seguridad de sistemas y redes.'),
                                                            (14, 'Realizar auditorías de seguridad periódicas.'),
                                                            (14, 'Investigar incidentes y vulnerabilidades.'),
                                                            (14, 'Implementar medidas de mitigación y mejora.'),
                                                            (14, 'Colaborar con equipos de IT para la seguridad integral.');

-- Beneficios para posición 14
INSERT INTO RECRUITERS.benefits (position_id, description) VALUES
                                                               (14, 'Salario competitivo.'),
                                                               (14, 'Seguro médico integral.'),
                                                               (14, 'Capacitación en seguridad y certificaciones.'),
                                                               (14, 'Flexibilidad y posibilidad de trabajo remoto.'),
                                                               (14, 'Bonificaciones por desempeño.');
