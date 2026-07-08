CREATE TABLE users (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
username VARCHAR(50) NOT NULL UNIQUE,
user_password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
role_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
user_id BIGINT NOT NULL,
role_id BIGINT NOT NULL,
PRIMARY KEY (user_id, role_id),
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE financial_accounts (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
account_name VARCHAR(50) NOT NULL,
balance FLOAT NOT NULL DEFAULT 0,
user_id BIGINT NOT NULL,
FOREIGN KEY(user_id) REFERENCES users(id),
CONSTRAINT unique_name UNIQUE(account_name, user_id)
);

CREATE TABLE counterparty (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
legal_name VARCHAR(50) NOT NULL,
tax_id VARCHAR(14) NOT NULL,
user_id BIGINT NOT NULL,
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TYPE transaction_type AS ENUM ('CREDIT', 'DEBIT');
CREATE TABLE transactions (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
transaction_doc VARCHAR(30) NOT NULL UNIQUE,
issue_date DATE NOT NULL,
transaction_type transaction_type NOT NULL,
transaction_description  VARCHAR(100),
counterparty_id BIGINT NOT NULL,
financial_account_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
FOREIGN KEY (financial_account_id) REFERENCES financial_accounts(id),
FOREIGN KEY (counterparty_id) REFERENCES counterparty(id),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TYPE status AS ENUM ('PENDING', 'PAID', 'PARTIALLY PAID', 'CANCELLED');
CREATE TABLE installments (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
installment_number INT NOT NULL,
amount FLOAT NOT NULL,
due_date DATE NOT NULL,
installment_status status NOT NULL,
transaction_id BIGINT NOT NULL,
payment_id BIGINT,
FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE payments (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
moment TIMESTAMP NOT NULL,
financial_account_id BIGINT NOT NULL,
installment_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
FOREIGN KEY (financial_account_id) references financial_accounts(id),
FOREIGN KEY (installment_id) references installments(id),
FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE installments ADD CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payments(id);

CREATE TABLE categories (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
category_name VARCHAR(30) NOT NULL UNIQUE,
color_hex VARCHAR(6) NOT NULL,
user_id BIGINT NOT NULL,
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE transactions_categories (
transaction_id BIGINT NOT NULL,
category_id BIGINT NOT NULL,
PRIMARY KEY (transaction_id, category_id),
FOREIGN KEY (transaction_id) references transactions(id),
FOREIGN KEY (category_id) references categories(id)
);


INSERT INTO roles (role_name) VALUES ('ROLE_USER');