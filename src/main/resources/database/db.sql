CREATE DATABASE personal_financial_system;
USE personal_financial_system;

CREATE TABLE users(
id BIGINT AUTO_INCREMENT NOT NULL,
username VARCHAR(50) NOT NULL,
email VARCHAR(50) UNIQUE NOT NULL,
user_password VARCHAR(15) NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE financial_accounts (
id BIGINT AUTO_INCREMENT NOT NULL,
account_name VARCHAR(50) NOT NULL,
balance FLOAT NOT NULL DEFAULT 0,
user_id BIGINT NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY(user_id) REFERENCES users(id)
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
PRIMARY KEY (id),
FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE category (
id BIGINT AUTO_INCREMENT NOT NULL,
category_name VARCHAR(30) NOT NULL UNIQUE,
color_hex VARCHAR(6) NOT NULL,
user_id BIGINT NOT NULL,
PRIMARY KEY(id),
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE transaction_category (
transaction_id BIGINT NOT NULL,
category_id BIGINT NOT NULL,
PRIMARY KEY (transaction_id, category_id),
FOREIGN KEY (transaction_id) references transactions(id),
FOREIGN KEY (category_id) references category(id)
);
