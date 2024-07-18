-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
                                     user_id SERIAL PRIMARY KEY,
                                     first_name VARCHAR(255) NOT NULL,
                                     last_name VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL
);

-- Create Roles Table
CREATE TABLE IF NOT EXISTS roles (
                                     role_id SERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL UNIQUE
);

-- Create Users_Roles Table (Many-to-Many Relationship)
CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id BIGINT REFERENCES users(user_id),
                                           role_id BIGINT REFERENCES roles(role_id),
                                           PRIMARY KEY (user_id, role_id)
);

-- Create Currency Table
CREATE TABLE IF NOT EXISTS currency (
                                        currency_id SERIAL PRIMARY KEY,
                                        code VARCHAR(255) NOT NULL,
                                        full_name VARCHAR(255) NOT NULL
);

-- Create Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
                                        account_id SERIAL PRIMARY KEY,
                                        owner_id BIGINT REFERENCES users(user_id),
                                        account_name VARCHAR(255),
                                        currency_id BIGINT REFERENCES currency(currency_id),
                                        amount BIGINT,
                                        status VARCHAR(50) NOT NULL,
                                        FOREIGN KEY (owner_id) REFERENCES users(user_id),
                                        FOREIGN KEY (currency_id) REFERENCES currency(currency_id)
);

-- Create Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id SERIAL PRIMARY KEY,
                                            date_time TIMESTAMP,
                                            received_transaction_id BIGINT REFERENCES accounts(account_id),
                                            sent_transaction_id BIGINT REFERENCES accounts(account_id),
                                            amount BIGINT,
                                            description VARCHAR(255),
                                            status VARCHAR(50)
);

