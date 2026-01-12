#!/bin/bash

# Adventure Quest Engine - Run Script
# This script compiles and runs the game

echo "====================================="
echo "  Adventure Quest Engine"
echo "====================================="
echo ""

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

# Build and run the project
echo "Building and running the game..."
echo ""

./gradlew desktop:run

echo ""
echo "Game closed."
