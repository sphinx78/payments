# How to Run the Expense Tracker Application

This is your complete guide to setting up, running, and managing the Expense Tracker application. **The project is set up to run on any computer**—you only configure the MySQL password once per machine.

## 1. Prerequisites
Before running, ensure you have:
1. **Java JDK 8 or higher**: Type `java -version` in terminal to check.
2. **MySQL Server**: Running on port 3306 (default username: `root`).
3. **Apache Maven**: Type `mvn -version` to check.

## 2. One-Time Setup Per Computer (MySQL Password)
The app reads the MySQL password from a config file or environment variable so you never edit Java source code.

**Option A – Config file (recommended):**
1. In the project folder, copy **`db.properties.example`** to **`db.properties`**.
2. Open **`db.properties`** and set `db.password` to your MySQL root password for this computer.
3. Save the file. `db.properties` is not committed to git, so each computer can have its own password.

**Option B – Environment variable:**  
Set `EXPENSE_TRACKER_DB_PASSWORD` to your MySQL root password (e.g. in System Properties → Environment Variables on Windows).

After this one-time setup, **setup_db.bat**, **check_db.bat**, and the web app all use the same password.

## 3. One-Time Setup (Create the Database)
If this is the first time on this computer, create the database:

1. Open the project folder in File Explorer.
2. Double-click **`setup_db.bat`** (or run `mvn exec:java -q` from the project folder).
3. This creates the `expense_tracker_db` database and tables, and adds a test user.

## 4. Run the Application
1. Open your terminal (Command Prompt or PowerShell).
2. Navigate to the project folder (the one containing `pom.xml`).
3. Run:
   ```powershell
   mvn jetty:run
   ```
4. Open your browser to: **[http://localhost:8080](http://localhost:8080)**

## 5. Verify Your Data
Double-click **`check_db.bat`** to print current users and groups from the database.

## 6. Moving to a New Computer (Backup & Restore)
Your data lives in the database. To move it to another machine:

**On the OLD computer:**
1. Double-click **`backup_db.bat`**.
2. When prompted, enter your MySQL root password for that computer.
3. This creates **`backup.sql`**. Copy the whole project folder (including `backup.sql`) to the new computer.

**On the NEW computer:**
1. Copy **`db.properties.example`** to **`db.properties`** and set this computer’s MySQL password.
2. Run **`setup_db.bat`** to create the empty database.
3. Double-click **`restore_db.bat`** and enter this computer’s MySQL password when prompted. Your data will be loaded.

## Troubleshooting
* **Port 8080 in use?**  
  Stop other servers, or find the process: `netstat -ano | findstr :8080`
* **"Access denied" for MySQL?**  
  Check that `db.properties` has the correct `db.password` for this computer (or that `EXPENSE_TRACKER_DB_PASSWORD` is set).
* **"Command not found"?**  
  In PowerShell, run batch files with `.\` (e.g. `.\setup_db.bat`).
