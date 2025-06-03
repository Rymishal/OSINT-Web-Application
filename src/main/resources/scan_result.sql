CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE scan_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    domain VARCHAR(100) NOT NULL
);

CREATE TABLE scan_element(
    id      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    scan_id UUID REFERENCES scan_data(id) ON DELETE CASCADE,
    value   VARCHAR(2000) NOT NULL
)