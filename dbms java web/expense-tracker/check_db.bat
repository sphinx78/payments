@echo off
echo Checking Database Content...
echo.
call mvn exec:java -Dexec.mainClass=com.expensetracker.util.DatabaseInspector -q
pause
