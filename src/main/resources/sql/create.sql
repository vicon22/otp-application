CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE otp_config (
                            id SERIAL PRIMARY KEY,
                            code_length INTEGER NOT NULL,
                            ttl_seconds INTEGER NOT NULL
);

CREATE TABLE otp_codes (
                           id SERIAL PRIMARY KEY,
                           user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                           code VARCHAR(10) NOT NULL,
                           status VARCHAR(20) CHECK (status IN ('ACTIVE', 'EXPIRED', 'USED')) NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           operation_id VARCHAR(255)
);
