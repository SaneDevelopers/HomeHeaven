@echo off
REM HomeHeaven Stop Script for Windows

echo Stopping HomeHeaven application...

REM Find process on port 8080
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    set PID=%%a
)

if "%PID%"=="" (
    echo [ERROR] No application running on port 8080
    pause
    exit /b 1
)

REM Kill the process
taskkill /F /PID %PID% >nul 2>&1

if %errorlevel% equ 0 (
    echo [OK] HomeHeaven stopped successfully (PID: %PID%)
) else (
    echo [ERROR] Failed to stop application
    pause
    exit /b 1
)

pause
