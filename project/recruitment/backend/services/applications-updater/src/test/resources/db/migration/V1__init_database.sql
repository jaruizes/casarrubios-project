CREATE EXTENSION IF NOT EXISTS "pgcrypto";
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

CREATE TABLE recruiters.outbox (
    id UUID PRIMARY KEY,
    aggregatetype VARCHAR(255) NOT NULL,
    aggregateid VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT now(),
    tracingspancontext TEXT
);
