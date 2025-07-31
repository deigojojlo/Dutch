#!/bin/bash

# Compile Java files
echo "Compiling Java source files..."
javac -encoding UTF-8 -d ./build $(find src -name "*.java")
if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting."
    exit 1
fi

# Create the JAR file
echo "Creating JAR file..."
jar cvfm DUTCH.jar MANIFEST.MF -C build/ . -C ./ README.md AUTHORS.md
if [ $? -ne 0 ]; then
    echo "JAR creation failed. Exiting."
    exit 1
fi

# Run the JAR file with arguments
echo "Running the application..."
nohup java -jar DUTCH.jar "$@" >/dev/null 2>&1 &
if [ $? -ne 0 ]; then
    echo "Application execution failed. Exiting."
    exit 1
fi

echo "Done."