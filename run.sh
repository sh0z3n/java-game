#!/bin/bash
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi
chmod +x ./gradlew
./gradlew desktop:run
