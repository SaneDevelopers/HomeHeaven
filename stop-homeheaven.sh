#!/bin/bash

# HomeHeaven Stop Script for Mac/Linux

echo "🛑 Stopping HomeHeaven application..."

# Find and kill process on port 8080
PID=$(lsof -ti :8080)

if [ -z "$PID" ]; then
    echo "❌ No application running on port 8080"
    exit 1
fi

kill -9 $PID 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ HomeHeaven stopped successfully (PID: $PID)"
else
    echo "❌ Failed to stop application"
    exit 1
fi
