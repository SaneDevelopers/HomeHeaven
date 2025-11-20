#!/bin/bash

# HomeHeaven Startup Script for Mac/Linux
# This script starts the HomeHeaven application

echo "╔══════════════════════════════════════════════════════════════════════╗"
echo "║                    HOMEHEAVEN STARTUP SCRIPT                         ║"
echo "╚══════════════════════════════════════════════════════════════════════╝"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed!"
    echo "Please install Java 21 or higher"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Java version must be 21 or higher"
    echo "Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
echo ""

# Check if MySQL is running
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL client not found. Make sure MySQL server is running!"
else
    if mysql -u root -e "SELECT 1" &> /dev/null; then
        echo "✅ MySQL is running"
    else
        echo "❌ Cannot connect to MySQL. Please start MySQL server!"
        exit 1
    fi
fi
echo ""

# Check if JAR file exists
JAR_FILE="target/home-heaven-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Application JAR not found!"
    echo "Building application..."
    echo ""
    
    # Set JAVA_HOME for Maven
    if [ -d "/opt/homebrew/opt/openjdk@21" ]; then
        export JAVA_HOME=/opt/homebrew/opt/openjdk@21
    fi
    
    mvn clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        echo "❌ Build failed!"
        exit 1
    fi
    echo ""
fi

# Check if port 8080 is already in use
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠️  Port 8080 is already in use!"
    echo "Stopping existing process..."
    kill -9 $(lsof -ti :8080) 2>/dev/null
    sleep 2
fi

# Start the application
echo "🚀 Starting HomeHeaven application..."
echo ""

# Use Java 21 if available
if [ -d "/opt/homebrew/opt/openjdk@21" ]; then
    /opt/homebrew/opt/openjdk@21/bin/java -jar "$JAR_FILE" &
else
    java -jar "$JAR_FILE" &
fi

APP_PID=$!
echo "✅ Application started with PID: $APP_PID"
echo ""

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 8

# Check if application is running
if ps -p $APP_PID > /dev/null; then
    echo ""
    echo "╔══════════════════════════════════════════════════════════════════════╗"
    echo "║              ✅ HOMEHEAVEN IS RUNNING SUCCESSFULLY!                  ║"
    echo "╚══════════════════════════════════════════════════════════════════════╝"
    echo ""
    echo "📍 Application URLs:"
    echo "   • Main Site:      http://localhost:8080"
    echo "   • Login:          http://localhost:8080/login.html"
    echo "   • Dashboard:      http://localhost:8080/dashboard.html"
    echo "   • Super Admin:    http://localhost:8080/super-admin.html"
    echo ""
    echo "👤 Test Credentials:"
    echo "   • Super Admin:    superadmin / admin123"
    echo "   • Regular User:   john_doe / password123"
    echo ""
    echo "🛑 To stop the application:"
    echo "   kill -9 $APP_PID"
    echo "   or"
    echo "   ./stop-homeheaven.sh"
    echo ""
    echo "📝 Logs are being written to: app.log"
    echo ""
else
    echo "❌ Application failed to start!"
    echo "Check app.log for errors"
    exit 1
fi
