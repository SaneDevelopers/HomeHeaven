@echo off
REM HomeHeaven Startup Script for Windows
REM This script starts the HomeHeaven application

echo ========================================================================
echo                    HOMEHEAVEN STARTUP SCRIPT
echo ========================================================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed!
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

echo [OK] Java is installed
echo.

REM Check if JAR file exists
set JAR_FILE=target\home-heaven-0.0.1-SNAPSHOT.jar
if not exist "%JAR_FILE%" (
    echo [ERROR] Application JAR not found!
    echo Building application...
    echo.
    
    call mvn clean package -DskipTests
    
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed!
        pause
        exit /b 1
    )
    echo.
)

REM Check if port 8080 is already in use
netstat -ano | findstr :8080 | findstr LISTENING >nul 2>&1
if %errorlevel% equ 0 (
    echo [WARNING] Port 8080 is already in use!
    echo Please stop the existing process or use a different port
    pause
    exit /b 1
)

REM Start the application
echo [INFO] Starting HomeHeaven application...
echo.

start "HomeHeaven" java -jar "%JAR_FILE%"

REM Wait for application to start
timeout /t 8 /nobreak >nul

echo.
echo ========================================================================
echo              [OK] HOMEHEAVEN IS RUNNING SUCCESSFULLY!
echo ========================================================================
echo.
echo Application URLs:
echo   * Main Site:      http://localhost:8080
echo   * Login:          http://localhost:8080/login.html
echo   * Dashboard:      http://localhost:8080/dashboard.html
echo   * Super Admin:    http://localhost:8080/super-admin.html
echo.
echo Test Credentials:
echo   * Super Admin:    superadmin / admin123
echo   * Regular User:   john_doe / password123
echo.
echo To stop the application, close the HomeHeaven window
echo or use Task Manager to end the java.exe process
echo.
pause
