CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE transporters (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	name VARCHAR(255) NOT NULL,
	SIRET VARCHAR(255) NOT NULL,
	postal_codes VARCHAR(8)[]
);

CREATE TABLE carriers (
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	name VARCHAR(255) NOT NULL,
	age INT NOT NULL,
	licenses VARCHAR(1)[],
	transporter_id UUID REFERENCES transporters(id)
);