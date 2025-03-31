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


CREATE TABLE recruiters.outbox (
    id UUID PRIMARY KEY,
    aggregatetype VARCHAR(255) NOT NULL,
    aggregateid VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    timestamp TIMESTAMP DEFAULT now()
);
