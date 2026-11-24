# RentEase – Installation and Setup Guide

## 1. System Requirements

Before installing RentEase, make sure your computer has the following:

- Windows operating system
- Java Development Kit (JDK)
- MySQL Server
- Visual Studio Code
- MySQL Connector/J
- Git (optional, for cloning the repository)

## 2. Download or Clone the Project

Clone the GitHub repository:

```bash
git clone https://github.com/naomichan031/RentEase.git
```

Open the `RentEase` folder in Visual Studio Code.

## 3. Configure the Database

1. Start your MySQL Server.
2. Open MySQL Workbench or your preferred MySQL client.
3. Create the database:

```sql
CREATE DATABASE rentease_db;
```

4. Import or execute the project's database schema and table creation scripts.
5. Confirm that the required tables are available.

## 4. Configure Environment Variables

RentEase uses environment variables to protect database credentials and the Resend API key.

Configure the following Windows environment variables:

| Variable | Description |
|---|---|
| `DB_USER` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `RESEND_API_KEY` | Resend API key |

Restart Visual Studio Code after configuring the variables so the terminal can detect them.

**Security reminder:** Never upload passwords or API keys to GitHub.

## 5. Check the MySQL Connector

Make sure the MySQL Connector/J file is located inside the project's `lib` folder.

The compile and run commands below expect this filename:

`mysql-connector-j-26.10.0.jar`

If your connector has a different filename, update the commands accordingly.

## 6. Compile the Application

Open the VS Code terminal in the RentEase project directory.

Run:

```powershell
javac -cp ".;lib\mysql-connector-j-26.10.0.jar" -d out src\*.java
```

## 7. Run the Application

After successful compilation, execute:

```powershell
java -cp ".;out;lib\mysql-connector-j-26.10.0.jar" LoginFrame
```

The RentEase login window should appear.

## 8. Configure Email Notifications

RentEase uses the Resend Email API to send rental confirmation emails.

1. Create or access a Resend account.
2. Obtain an API key.
3. Set the key as the `RESEND_API_KEY` environment variable.
4. Restart VS Code.
5. Test the email notification through the rental workflow.

The Resend testing sender may restrict recipients to the account's registered email unless a sending domain is verified.

## 9. Troubleshooting

| Problem | Possible Solution |
|---|---|
| MySQL connection failed | Check that MySQL is running and database credentials are correct. |
| Database not found | Confirm that `rentease_db` exists. |
| Connector error | Verify the MySQL Connector/J file and its filename. |
| Compilation error | Check the Java source files and classpath. |
| API authentication failed | Verify the Resend API key environment variable. |
| Email not received | Check the recipient address and Resend account restrictions. |

## 10. Project Repository

GitHub Repository:

https://github.com/naomichan031/RentEase

---

**RentEase – Equipment Rental Management System**  
Installation and Setup Documentation