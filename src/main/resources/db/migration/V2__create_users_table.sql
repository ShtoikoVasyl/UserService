CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       role_id BIGINT,
                       status VARCHAR(50),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,

                       CONSTRAINT fk_role
                           FOREIGN KEY (role_id)
                               REFERENCES roles(id)
);