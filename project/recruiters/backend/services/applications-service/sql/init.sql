-- Crear el esquema
CREATE SCHEMA IF NOT EXISTS recruiters;

-- Crear la tabla de aplicaciones
CREATE TABLE IF NOT EXISTS recruiters.applications (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    cv VARCHAR(255) NOT NULL,
    position_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos iniciales
INSERT INTO recruiters.applications (name, email, phone, cv, position_id)
VALUES
('Alice Smith', 'alice@example.com', '+123456789', '/path/to/cv1.pdf', 1),
('Bob Johnson', 'bob@example.com', '+987654321', '/path/to/cv2.pdf', 1),
('Charlie Brown', 'charlie@example.com', '+123123123', '/path/to/cv3.pdf', 1),
('David Wilson', 'david@example.com', '+321321321', '/path/to/cv4.pdf', 1),
('Eva Green', 'eva@example.com', '+456456456', '/path/to/cv5.pdf', 1),
('Frank White', 'frank@example.com', '+654654654', '/path/to/cv6.pdf', 1),
('Grace Black', 'grace@example.com', '+789789789', '/path/to/cv7.pdf', 1),
('Hannah Blue', 'hannah@example.com', '+987987987', '/path/to/cv8.pdf', 1),
('Ian Gray', 'ian@example.com', '+111222333', '/path/to/cv9.pdf', 1),
('Jack Brown', 'jack@example.com', '+444555666', '/path/to/cv10.pdf', 1),
('Karen White', 'karen@example.com', '+777888999', '/path/to/cv11.pdf', 1),
('Leo Green', 'leo@example.com', '+000111222', '/path/to/cv12.pdf', 1),
('Mia Black', 'mia@example.com', '+333444555', '/path/to/cv13.pdf', 1),
('Nina Blue', 'nina@example.com', '+666777888', '/path/to/cv14.pdf', 1),
('Oscar Gray', 'oscar@example.com', '+999000111', '/path/to/cv15.pdf', 1),
('Paul Brown', 'paul@example.com', '+222333444', '/path/to/cv16.pdf', 1),
('Quinn White', 'quinn@example.com', '+555666777', '/path/to/cv17.pdf', 1),
('Rachel Green', 'rachel@example.com', '+888999000', '/path/to/cv18.pdf', 1),
('Sam Black', 'sam@example.com', '+111222333', '/path/to/cv19.pdf', 1),
('Tina Blue', 'tina@example.com', '+444555666', '/path/to/cv20.pdf', 1),
('Uma Gray', 'uma@example.com', '+777888999', '/path/to/cv21.pdf', 1),
('Victor Brown', 'victor@example.com', '+000111222', '/path/to/cv22.pdf', 1);

