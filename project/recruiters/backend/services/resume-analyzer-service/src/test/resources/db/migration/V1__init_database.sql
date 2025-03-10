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


--- Data Scientist (Senior)
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'Senior Data Scientist',
           'Buscamos un Data Scientist con experiencia en machine learning y análisis de datos para desarrollar modelos predictivos y optimizar procesos empresariales.',
           1,
           'Data Science, Machine Learning, Python, Big Data, AI'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (1, 'Python', 'Avanzado', 'Experiencia en Python para manipulación de datos y modelado.', TRUE),
    (1, 'Machine Learning', 'Avanzado', 'Conocimiento profundo en algoritmos de ML y Deep Learning.', TRUE),
    (1, 'Big Data', 'Intermedio', 'Experiencia con Apache Spark o Hadoop.', FALSE),
    (1, 'SQL', 'Avanzado', 'Manejo de SQL para análisis de grandes volúmenes de datos.', TRUE),
    (1, 'Cloud', 'Intermedio', 'Experiencia en GCP, AWS o Azure.', FALSE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (1, 'Desarrollar modelos de machine learning para predecir tendencias del mercado.'),
    (1, 'Analizar grandes volúmenes de datos y generar insights accionables.'),
    (1, 'Optimizar procesos internos mediante inteligencia artificial.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (1, 'Seguro de salud premium.'),
    (1, 'Horario flexible y trabajo remoto.'),
    (1, 'Acceso a conferencias y formación en IA.');

---- Backend Developer (Java + Spring Boot)
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'Backend Developer (Java + Spring Boot)',
           'Buscamos un desarrollador backend con experiencia en Java y Spring Boot para desarrollar APIs escalables y eficientes.',
           1,
           'Java, Spring Boot, Microservices, PostgreSQL, Docker, Kubernetes'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (2, 'Java', 'Avanzado', 'Experiencia en desarrollo backend con Java 11 o superior.', TRUE),
    (2, 'Spring Boot', 'Avanzado', 'Conocimiento profundo del framework Spring Boot.', TRUE),
    (2, 'REST APIs', 'Intermedio', 'Experiencia diseñando y consumiendo APIs REST.', TRUE),
    (2, 'Docker', 'Intermedio', 'Conocimiento en contenedores y despliegue en Docker.', FALSE),
    (2, 'Kubernetes', 'Básico', 'Familiaridad con orquestación de contenedores.', FALSE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (2, 'Desarrollar y mantener microservicios en Java y Spring Boot.'),
    (2, 'Optimizar bases de datos y mejorar la eficiencia de las consultas.'),
    (2, 'Diseñar y documentar APIs REST para clientes internos y externos.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (2, 'Bono anual por desempeño.'),
    (2, 'Opción de trabajo híbrido.'),
    (2, 'Formación en tecnologías cloud.');


---- DevOps Engineer
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'DevOps Engineer',
           'Buscamos un ingeniero DevOps para automatizar despliegues, monitoreo y asegurar la infraestructura escalable de nuestros servicios.',
           1,
           'DevOps, AWS, CI/CD, Terraform, Kubernetes, Monitoring'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (3, 'AWS', 'Avanzado', 'Experiencia en servicios de AWS (EC2, RDS, S3).', TRUE),
    (3, 'CI/CD', 'Intermedio', 'Conocimiento en integración y despliegue continuo con Jenkins o GitHub Actions.', TRUE),
    (3, 'Kubernetes', 'Intermedio', 'Experiencia en orquestación de contenedores con Kubernetes.', TRUE),
    (3, 'Terraform', 'Básico', 'Conocimiento en IaC con Terraform o CloudFormation.', FALSE),
    (3, 'Observabilidad', 'Intermedio', 'Experiencia en monitoreo con Prometheus, Grafana o Datadog.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (3, 'Automatizar despliegues mediante CI/CD en entornos de producción.'),
    (3, 'Monitorear el rendimiento de los sistemas y prevenir fallos.'),
    (3, 'Optimizar la infraestructura cloud para mejorar costos y eficiencia.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (3, 'Stock options en la empresa.'),
    (3, 'Trabajo remoto 100%.'),
    (3, 'Presupuesto anual para certificaciones.');


---- Product Manager (Tech)
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'Product Manager (Tech)',
           'Buscamos un Product Manager con experiencia en desarrollo de software y metodologías ágiles para liderar equipos de producto.',
           1,
           'Product Management, Agile, Scrum, UX/UI, Roadmap, Stakeholders'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (4, 'Product Management', 'Avanzado', 'Experiencia gestionando productos digitales.', TRUE),
    (4, 'Scrum', 'Intermedio', 'Conocimiento en metodologías ágiles y gestión de sprints.', TRUE),
    (4, 'UX/UI', 'Básico', 'Capacidad de entender y mejorar la experiencia de usuario.', FALSE),
    (4, 'Stakeholder Management', 'Intermedio', 'Experiencia interactuando con clientes y equipos técnicos.', TRUE),
    (4, 'Roadmap', 'Intermedio', 'Capacidad para definir y gestionar roadmaps de producto.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (4, 'Definir estrategias de producto basadas en necesidades del mercado.'),
    (4, 'Gestionar el backlog y asegurar entregas de alto valor.'),
    (4, 'Colaborar con equipos de desarrollo y UX para mejorar la experiencia del usuario.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (4, 'Plan de carrera y crecimiento.'),
    (4, 'Opción de acciones en la compañía.'),
    (4, 'Viajes a conferencias internacionales.');


---- Cybersecurity Engineer
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'Cybersecurity Engineer',
           'Buscamos un especialista en ciberseguridad para proteger nuestros sistemas, detectar vulnerabilidades y aplicar medidas de mitigación.',
           1,
           'Cybersecurity, Ethical Hacking, SIEM, SOC, Security Operations'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (5, 'Penetration Testing', 'Avanzado', 'Experiencia realizando pruebas de seguridad y ethical hacking.', TRUE),
    (5, 'SIEM', 'Intermedio', 'Uso de herramientas de monitoreo de seguridad como Splunk o ELK.', TRUE),
    (5, 'Cloud Security', 'Básico', 'Conocimientos en seguridad de AWS, Azure o GCP.', FALSE),
    (5, 'SOC Operations', 'Intermedio', 'Experiencia en respuesta a incidentes de seguridad.', TRUE),
    (5, 'ISO 27001', 'Básico', 'Familiaridad con normativas de seguridad.', FALSE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (5, 'Realizar pruebas de penetración para identificar vulnerabilidades.'),
    (5, 'Monitorear y analizar incidentes de seguridad en tiempo real.'),
    (5, 'Implementar medidas para fortalecer la seguridad de la infraestructura.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (5, 'Seguro de vida y salud.'),
    (5, 'Certificaciones en seguridad cubiertas.'),
    (5, 'Horario flexible.');


---- Frontend Developer (React + TypeScript)
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'Frontend Developer (React + TypeScript)',
           'Buscamos un desarrollador frontend con experiencia en React y TypeScript para construir interfaces modernas y escalables.',
           1,
           'Frontend, React, TypeScript, UI/UX, JavaScript, Web Development'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (6, 'React', 'Avanzado', 'Experiencia en desarrollo con React y gestión de estado (Redux, Context API).', TRUE),
    (6, 'TypeScript', 'Intermedio', 'Uso de TypeScript para mejorar la robustez del código.', TRUE),
    (6, 'CSS/SASS', 'Intermedio', 'Manejo de estilos avanzados y diseño responsivo.', TRUE),
    (6, 'UI/UX', 'Básico', 'Conocimiento en diseño centrado en el usuario.', FALSE),
    (6, 'Testing', 'Básico', 'Familiaridad con pruebas unitarias (Jest, React Testing Library).', FALSE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (6, 'Desarrollar interfaces modernas y escalables con React y TypeScript.'),
    (6, 'Colaborar con diseñadores para mejorar la experiencia de usuario.'),
    (6, 'Optimizar el rendimiento y accesibilidad de la aplicación.'),
    (6, 'Implementar pruebas unitarias para asegurar la calidad del código.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (6, 'Trabajo remoto y flexibilidad horaria.'),
    (6, 'Bono de productividad por entregas exitosas.'),
    (6, 'Formación en últimas tecnologías de frontend.');


---- Solution Architect (Cloud & Microservices)
INSERT INTO RECRUITERS.positions (title, description, status, tags)
VALUES (
           'Solution Architect (Cloud & Microservices)',
           'Buscamos un arquitecto de soluciones con experiencia en diseño de arquitecturas escalables en la nube y microservicios.',
           1,
           'Architecture, Cloud, Microservices, AWS, Kubernetes, Software Design'
       );

INSERT INTO RECRUITERS.requirements (position_id, key, value, description, mandatory)
VALUES
    (7, 'Software Architecture', 'Avanzado', 'Experiencia en diseño de arquitecturas escalables y resilientes.', TRUE),
    (7, 'Microservices', 'Avanzado', 'Implementación de arquitecturas basadas en microservicios.', TRUE),
    (7, 'Cloud', 'Intermedio', 'Experiencia con AWS, Azure o GCP.', TRUE),
    (7, 'Kubernetes', 'Intermedio', 'Despliegue y gestión de aplicaciones en contenedores.', FALSE),
    (7, 'API Design', 'Intermedio', 'Diseño de APIs REST y GraphQL.', TRUE);

INSERT INTO RECRUITERS.tasks (position_id, description)
VALUES
    (7, 'Definir arquitecturas escalables y de alto rendimiento en la nube.'),
    (7, 'Asesorar a equipos de desarrollo en mejores prácticas de software.'),
    (7, 'Diseñar e implementar estrategias de integración entre servicios.'),
    (7, 'Garantizar la seguridad y gobernanza de las aplicaciones empresariales.');

INSERT INTO RECRUITERS.benefits (position_id, description)
VALUES
    (7, 'Bonos por certificaciones en arquitectura de software.'),
    (7, 'Opción de trabajo remoto parcial.'),
    (7, 'Acceso a eventos y conferencias de arquitectura.');




ALTER SEQUENCE RECRUITERS.positions_id_seq RESTART WITH 31;
ALTER SEQUENCE RECRUITERS.requirements_id_seq RESTART WITH 31;
ALTER SEQUENCE RECRUITERS.tasks_id_seq RESTART WITH 31;
ALTER SEQUENCE RECRUITERS.benefits_id_seq RESTART WITH 31;
