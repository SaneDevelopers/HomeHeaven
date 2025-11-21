@echo off
REM HomeHeaven Setup Script for Windows
REM This script downloads Maven and installs all project dependencies

setlocal enabledelayedexpansion

echo =========================================
echo HomeHeaven Setup Script
echo =========================================
echo.

REM Configuration
set MAVEN_VERSION=3.9.6
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_DIR=tools\apache-maven-%MAVEN_VERSION%
set JAVA_REQUIRED_VERSION=21

REM Check if Java is installed
echo Checking Java installation...
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed
    echo.
    echo Please install Java %JAVA_REQUIRED_VERSION% from:
    echo   - Oracle: https://www.oracle.com/java/technologies/downloads/
    echo   - OpenJDK: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Get Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_STRING=%%g
)
set JAVA_VERSION_STRING=!JAVA_VERSION_STRING:"=!
for /f "tokens=1 delims=." %%a in ("!JAVA_VERSION_STRING!") do set JAVA_MAJOR_VERSION=%%a

if !JAVA_MAJOR_VERSION! GEQ %JAVA_REQUIRED_VERSION% (
    echo [OK] Java !JAVA_MAJOR_VERSION! is installed
) else (
    echo [ERROR] Java %JAVA_REQUIRED_VERSION% or higher is required. Found Java !JAVA_MAJOR_VERSION!
    echo.
    echo Please install Java %JAVA_REQUIRED_VERSION% from:
    echo   - Oracle: https://www.oracle.com/java/technologies/downloads/
    echo   - OpenJDK: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Check if Maven is already installed
echo.
echo Checking Maven installation...
set MAVEN_CMD=

if exist "%MAVEN_DIR%\bin\mvn.cmd" (
    echo [OK] Maven found in local tools directory
    set MAVEN_CMD=%MAVEN_DIR%\bin\mvn.cmd
) else (
    where mvn >nul 2>nul
    if !ERRORLEVEL! EQU 0 (
        echo [OK] Maven is installed in system PATH
        set MAVEN_CMD=mvn
    ) else (
        echo [WARN] Maven not found. Downloading Maven %MAVEN_VERSION%...
        echo.
        
        REM Create tools directory
        if not exist "tools" mkdir tools
        
        REM Download Maven using PowerShell
        echo Downloading from: %MAVEN_URL%
        powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile 'tools\maven.zip'}"
        
        if !ERRORLEVEL! NEQ 0 (
            echo [ERROR] Failed to download Maven
            pause
            exit /b 1
        )
        
        REM Extract Maven using PowerShell
        echo Extracting Maven...
        powershell -Command "& {Expand-Archive -Path 'tools\maven.zip' -DestinationPath 'tools' -Force}"
        
        if !ERRORLEVEL! NEQ 0 (
            echo [ERROR] Failed to extract Maven
            pause
            exit /b 1
        )
        
        REM Clean up
        del /q "tools\maven.zip"
        
        echo [OK] Maven %MAVEN_VERSION% installed successfully
        set MAVEN_CMD=%MAVEN_DIR%\bin\mvn.cmd
    )
)

REM Display Maven version
echo.
echo Maven version:
call "%MAVEN_CMD%" -version | findstr "Apache Maven"

REM Install project dependencies
echo.
echo =========================================
echo Installing Project Dependencies
echo =========================================
echo.
echo This may take a few minutes on first run...
echo.

call "%MAVEN_CMD%" clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to install dependencies
    pause
    exit /b 1
)

echo [OK] Dependencies installed successfully!

REM Create uploads directory if it doesn't exist
echo.
echo Creating required directories...
if not exist "uploads" mkdir uploads
if not exist "src\main\resources\static\uploads" mkdir src\main\resources\static\uploads
echo [OK] Directories created

REM Display next steps
echo.
echo =========================================
echo Setup Complete!
echo =========================================
echo.
echo [OK] All dependencies have been installed successfully!
echo.
echo Next steps:
echo   1. Configure your database in src\main\resources\application.properties
echo   2. Set up MySQL database (see DATABASE_QUICK_REFERENCE.md)
echo   3. Run the application:
echo      start-homeheaven.bat
echo.
echo Or run with Maven directly:
if "%MAVEN_CMD%"=="%MAVEN_DIR%\bin\mvn.cmd" (
    echo   %MAVEN_CMD% spring-boot:run
) else (
    echo   mvn spring-boot:run
)
echo.
pause
