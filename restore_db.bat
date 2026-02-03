@echo off
echo Restoring database from backup.sql...
echo.
if not exist backup.sql (
    echo Error: backup.sql not found!
    echo Please make sure backup.sql is in this folder.
    pause
    exit /b
)

echo Enter your MySQL root password for this computer when prompted.
mysql -u root -p expense_tracker_db < backup.sql
echo.
echo Restore complete! Your data has been loaded.
pause
