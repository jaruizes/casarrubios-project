--- CANDIDATES ---
CREATE SCHEMA IF NOT EXISTS CANDIDATES;
CREATE SCHEMA IF NOT EXISTS RECRUITERS;

-- Tabla principal para las posiciones abiertas
CREATE TABLE CANDIDATES.POSITIONS
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para los requisitos de cada posición
CREATE TABLE CANDIDATES.REQUIREMENTS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES CANDIDATES.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);

-- Tabla para las condiciones de cada posición
CREATE TABLE CANDIDATES.CONDITIONS
(
    id          SERIAL PRIMARY KEY,
    position_id INT  NOT NULL REFERENCES CANDIDATES.positions (id) ON DELETE CASCADE,
    description TEXT NOT NULL
);



CREATE TABLE CANDIDATES.APPLICATIONS
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    surname    VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    cv         VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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


INSERT INTO CANDIDATES.positions (id, title, description, created_at)
VALUES (1, 'Arquitecto Software', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur laoreet nisl urna, vel convallis felis ultrices at. Aliquam velit ante, interdum sit amet eros vel, consectetur hendrerit nunc. Sed vulputate felis risus, eu suscipit urna lobortis quis. Nullam egestas viverra quam a iaculis. Etiam mattis, ante in dapibus tristique, sem felis porta nunc, quis cursus est nibh at leo. Morbi facilisis ullamcorper lacus, at imperdiet arcu fringilla pharetra. Donec nisi lectus, lobortis eget vestibulum sollicitudin, fringilla non odio. Donec at mollis erat. Fusce dui enim, maximus in metus gravida, sodales suscipit lorem. Donec tempor consequat consectetur.

Aenean congue, ante efficitur mollis cursus, turpis mauris aliquet justo, et aliquet nisl diam et nisi. Ut quis metus ac velit eleifend gravida sed eu ligula. Aenean lectus lacus, consequat vitae dictum a, ultricies at odio. Nunc tincidunt nisl et nisi sagittis iaculis. Integer blandit risus at lorem placerat fermentum. Integer malesuada molestie turpis sed viverra. Sed suscipit eget odio at commodo. Praesent lacinia ipsum non nunc volutpat, et molestie urna vulputate. Maecenas rhoncus maximus dolor, ut consectetur libero vehicula vitae.

Duis varius in ante posuere sollicitudin. Nullam hendrerit volutpat porttitor. Sed porta feugiat eros, vitae molestie ligula cursus ut. Donec eget odio non enim lacinia vulputate id ut nunc. Nam mattis eros vel lorem porttitor, ut posuere dui bibendum. Sed eget urna elementum, tempor ipsum nec, pharetra velit. Nam lectus orci, pretium a nibh nec, maximus efficitur nulla. Sed at dignissim erat, ac pellentesque mi. Nam aliquam felis ut ultricies interdum. Quisque eu vehicula ex, quis fermentum nisi. Donec non ultricies ex. Nunc vitae dolor nibh. Donec maximus ipsum vel felis tincidunt vulputate. Proin tristique eros eu sapien vulputate lobortis.

Maecenas viverra odio orci, ac aliquam lacus dapibus eget. Suspendisse potenti. Vivamus a tellus quis turpis efficitur blandit. Integer tempor molestie orci ac suscipit. Praesent vitae tortor sollicitudin, ornare ligula at, dictum magna. Ut maximus lorem at eros finibus sodales. Sed sed aliquam enim. Aliquam ut fringilla est. Cras vel ullamcorper urna.

Donec placerat metus a posuere facilisis. Praesent a massa neque. Vestibulum at nibh pretium, tristique arcu a, varius dui. Pellentesque id tincidunt mauris. Vivamus nec nisi at ex tristique luctus eget sit amet leo. Nam efficitur felis sapien, tempor accumsan nunc auctor quis. Donec semper auctor arcu ut lacinia. Nulla egestas, quam malesuada ultrices ullamcorper, neque turpis aliquam sapien, sit amet tincidunt augue est quis lorem. Duis nec iaculis libero. Nulla ut varius lorem.',
        '2024-11-28 00:55:04.000000');


INSERT INTO CANDIDATES.requirements (id, position_id, description)
VALUES (1, 1, 'Esto es la descripción de los requisitos de la posición de Arquitecto Software - 1');
INSERT INTO CANDIDATES.requirements (id, position_id, description)
VALUES (2, 1, 'Esto es la descripción de los requisitos de la posición de Arquitecto Software - 1');
INSERT INTO CANDIDATES.conditions (id, position_id, description)
VALUES (1, 1, 'Esto es la descripción de las condiciones de la posición de Arquitecto Software - 1');
INSERT INTO CANDIDATES.conditions (id, position_id, description)
VALUES (2, 1, 'Esto es la descripción de las condiciones de la posición de Arquitecto Software - 2');

