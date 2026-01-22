@echo off
REM Billing System (MySQL) - Compile Script for Windows

echo =========================================
echo   Billing System - MySQL Version
echo   Build Script
echo =========================================
echo.

REM Check if Java is installed
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java compiler ^(javac^) not found!
    echo Please install JDK 8 or higher.
    pause
    exit /b 1
)

REM Display Java version
echo Java version:
java -version
echo.

REM Create directories if they don't exist
echo Creating directories...
if not exist "bin" mkdir bin
if not exist "lib" mkdir lib

REM Check if MySQL Connector/J exists
if not exist "lib\mysql-connector-j-8.2.0.jar" (
    if not exist "lib\mysql-connector-java-8.0.33.jar" (
        echo Warning: MySQL Connector/J not found in lib\
        echo Please download from: https://dev.mysql.com/downloads/connector/j/
        echo Supported versions:
        echo   - mysql-connector-j-8.2.0.jar ^(recommended^)
        echo   - mysql-connector-java-8.0.33.jar
        echo.
        echo Place the JAR file in the lib\ directory
        pause
        exit /b 1
    )
)

REM Find MySQL connector jar
for %%f in (lib\mysql-connector-*.jar) do (
    set MYSQL_JAR=%%f
    goto :found
)

:found
if "%MYSQL_JAR%"=="" (
    echo Error: Could not find MySQL connector JAR
    pause
    exit /b 1
)

echo Found MySQL Connector: %MYSQL_JAR%
echo.

REM Compile Java files
echo Compiling Java files...
javac -cp "%MYSQL_JAR%" -d bin src\*.java

if %errorlevel% equ 0 (
    echo.
    echo =========================================
    echo   Compilation successful!
    echo =========================================
    echo.
    echo Before running the application:
    echo 1. Ensure MySQL server is running
    echo 2. Update database credentials in src\DatabaseManager.java
    echo    - DB_USER ^(default: root^)
    echo    - DB_PASSWORD ^(default: empty^)
    echo.
    echo To run the application, use:
    echo   run.bat
    echo.
) else (
    echo.
    echo =========================================
    echo   Compilation failed!
    echo =========================================
)

pause