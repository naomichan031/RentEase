# RentEase – Equipment Rental Management System

## Project Overview
RentEase is a Java-based Equipment Rental Management System designed to help administrators manage equipment, customers, rental transactions, returns, payments, and reports through a graphical user interface.

## Features
- **Login System** – Administrator login.
- **Equipment Management** – Add, view, update, delete, and search equipment.
- **Category Management** – Manage equipment categories.
- **Customer Management** – Maintain customer records.
- **Rental Management** – Create rentals, manage rental items, and calculate rental costs.
- **Return Management** – Process equipment returns and calculate late fees.
- **Payment Management** – Record payments and update payment statuses.
- **Reports Management** – View rental, payment, inventory, customer, and return reports.
- **Email Notifications** – Send rental confirmation emails using the Resend API.

## Technologies Used
- Java
- Java Swing
- MySQL
- JDBC
- Resend Email API
- Visual Studio Code
- Git and GitHub

## Prerequisites
- Java Development Kit (JDK)
- MySQL Server
- Visual Studio Code
- MySQL Connector/J
- A Resend account and API key for email notifications

## Configuration

RentEase uses environment variables for database credentials and the Resend API key.

Configure these Windows environment variables before running the application:

- `DB_USER` – MySQL username
- `DB_PASSWORD` – MySQL password
- `RESEND_API_KEY` – Resend API key

**Important:** Never commit passwords or API keys to GitHub.

## Running the Application

Open the project folder in VS Code and use the terminal.

Compile the source files:

```powershell
javac -cp ".;lib\mysql-connector-j-26.10.0.jar" -d out src\*.java
```

Run the application:

```powershell
java -cp ".;out;lib\mysql-connector-j-26.10.0.jar" LoginFrame
```

Make sure MySQL is running, the `rentease_db` database is configured, and the required environment variables are available.

## Project Documentation
The `docs` folder contains additional technical documentation:

- `equipment-customer-management.md`
- `rental-return-management.md`
- `api-reporting.md`

## External API Integration
RentEase integrates the Resend Email API to send rental confirmation emails after a rental transaction is successfully saved.

## Repository
[RentEase on GitHub](https://github.com/naomichan031/RentEase)

## Developer
Developed as an academic final project.
