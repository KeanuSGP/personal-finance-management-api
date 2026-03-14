CREATE DATABASE personal_financial_system;
USE personal_financial_system;

CREATE TABLE users (
id BIGINT AUTO_INCREMENT NOT NULL,
username VARCHAR(50) NOT NULL UNIQUE,
user_password VARCHAR(255) NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE roles (
id BIGINT AUTO_INCREMENT NOT NULL,
role_name VARCHAR(100) NOT NULL UNIQUE,
PRIMARY KEY (id)
);

CREATE TABLE users_roles (
user_id BIGINT NOT NULL,
role_id BIGINT NOT NULL,
PRIMARY KEY (user_id, role_id),
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE financial_accounts (
id BIGINT AUTO_INCREMENT NOT NULL,
account_name VARCHAR(50) NOT NULL,
balance FLOAT NOT NULL DEFAULT 0,
user_id BIGINT NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY(user_id) REFERENCES users(id),
CONSTRAINT unique_name UNIQUE(account_name, user_id)
);

CREATE TABLE counterparty (
id BIGINT AUTO_INCREMENT NOT NULL,
legal_name VARCHAR(50) NOT NULL,
tax_id VARCHAR(14) NOT NULL,
user_id BIGINT NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
id BIGINT AUTO_INCREMENT NOT NULL,
transaction_doc VARCHAR(30) NOT NULL UNIQUE,
issue_date DATE NOT NULL,
transaction_type ENUM('CREDIT', 'DEBIT') NOT NULL,
transaction_description  VARCHAR(100),
counterparty_id BIGINT NOT NULL,
financial_account_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (financial_account_id) REFERENCES financial_accounts(id),
FOREIGN KEY (counterparty_id) REFERENCES counterparty(id),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE installments (
id BIGINT AUTO_INCREMENT NOT NULL,
installment_number INT NOT NULL,
amount FLOAT NOT NULL,
due_date DATE NOT NULL,
installment_status ENUM('PENDING', 'PAID', 'PARTIALLY PAID', 'CANCELLED') NOT NULL,
transaction_id BIGINT NOT NULL,
payment_id BIGINT NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE payments (
id BIGINT NOT NULL,
moment TIMESTAMP NOT NULL,
financial_account_id BIGINT NOT NULL,
installment_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (financial_account_id) references financial_accounts(id),
FOREIGN KEY (installment_id) references installments(id),
FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE installments ADD CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payments(id);

CREATE TABLE categories (
id BIGINT AUTO_INCREMENT NOT NULL,
category_name VARCHAR(30) NOT NULL UNIQUE,
color_hex VARCHAR(6) NOT NULL,
user_id BIGINT NOT NULL,
PRIMARY KEY(id),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE transactions_categories (
transaction_id BIGINT NOT NULL,
category_id BIGINT NOT NULL,
PRIMARY KEY (transaction_id, category_id),
FOREIGN KEY (transaction_id) references transactions(id),
FOREIGN KEY (category_id) references categories(id)
);


INSERT INTO roles VALUES(1, "ROLE_USER");