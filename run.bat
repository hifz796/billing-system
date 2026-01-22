@echo off
REM Billing System (MySQL) - Run Script for Windows

echo =========================================
echo   Starting Billing System ^(MySQL^)...
echo =========================================
echo.

REM Check if compiled files exist
if not exist "bin" (
    echo Error: Compiled files not found!
    echo Please run compile.bat first
    pause
    exit /b 1
)

REM Find MySQL connector jar
for %%f in (lib\mysql-connector-*.jar) do (
    set MYSQL_JAR=%%f
    goto :found
)

:found
if "%MYSQL_JAR%"=="" (
    echo Error: Could not find MySQL connector JAR in lib\
    pause
    exit /b 1
)

echo Using MySQL Connector: %MYSQL_JAR%
echo.
echo Connecting to MySQL database...
echo If connection fails, check:
echo 1. MySQL server is running ^(mysql -u root -p^)
echo 2. Database credentials in DatabaseManager.java
echo 3. MySQL is accessible on localhost:3306
echo.

REM Run the application
java -cp "bin;%MYSQL_JAR%" BillingSystem