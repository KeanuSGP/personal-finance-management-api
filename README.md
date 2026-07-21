# Personal Finance API

[![vídeo demonstrativo]](https://github.com/user-attachments/assets/67a143ab-fe83-4097-bd6c-0c13dd1bc495)

## Project Description
RESTful API for personal finance management. It enables the creation of users, financial accounts, counterparts, and categories, as well as the management of installments for both accounts payable and receivable.

## Domain Model
- User
- FinancialAccount
- Counterparty
- Category
- Transaction
- Installment
- Payment

## Technologies
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL (or H2 for dev)
- Maven
- JUnit
- Mockito
- JWT Token
- React.js

## Usage

### Login and Registration
Registration can be done using a username (min 3/max 20 characters) and a password between 4 and 6 characters. Login is performed using these credentials on the login screen.

### Dashboard
The main screen of the system, which displays the total balance of all financial accounts, the income and expenses for the current month, and the last three added payments and transactions.

### Financial Accounts
An entity that functions just like a bank account. It must be created so that income or expense transactions can update its balance.

### Counterparty
The entity that receives or makes the payment for a transaction. It is created to indicate who was paid or who made the payment for the record registered in the system.

### Category
Used to categorize transactions.

### Transaction
The complete record containing the document, issue date, due date, amount, and other details. It utilizes all previously created data (financial account, counterparty, and category), allowing the payment to be processed and recorded under the payments section, as well as updating the account balance based on the transaction type (credit/debit).

### Payment
Displays the timestamp, the account that made the payment, and the ID of the paid installment. Deleting payments is allowed; however, installments and transactions cannot be deleted while they are marked as paid.

## Analytics & Features

### Revenue & Expense Dashboard
<img width="1590" height="516" alt="image" src="https://github.com/user-attachments/assets/277038e1-494f-4e46-aa09-849905a85399" />

- **Doughnut Chart:** Visualizes top 5 categories with highest expenses vs. revenues.
- **Bar Chart:** Compares monthly revenues against expenses for the last 6 months.
