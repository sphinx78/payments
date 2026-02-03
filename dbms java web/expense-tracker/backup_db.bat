@echo off
echo Backing up entire database...
echo.
echo Enter your MySQL root password for this computer when prompted.
mysqldump -u root -p expense_tracker_db > backup.sql
echo.
echo Backup complete! File saved as 'backup.sql'
echo You can copy this 'backup.sql' file to your new computer.
pause
