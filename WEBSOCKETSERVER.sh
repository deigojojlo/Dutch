#!/bin/bash

# Compile all Java files
javac -encoding UTF-8 -d ./build $(find src -name '*.java')
if [ $? -ne 0 ]; then
  echo "Compilation failed."
  exit 1
fi

echo "Compilation successful.
Close the server using CTRL-C"

# Start the Java process in the background
java -cp build main.java.server.WebsocketServer
JAVA_PID=$!
wait $JAVA_PID
exec bash
