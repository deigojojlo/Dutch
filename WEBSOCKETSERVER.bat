@echo off
setlocal

:: Compile Java files using PowerShell
powershell -Command "$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java'; & javac -encoding UTF-8 -d build $files.FullName;"
if errorlevel 1 (
    echo Compilation failed. Exiting.
    pause
    exit /b 1
)

echo Compilation successful.
echo Close the server using CTRL-C

:: Start the Java process in the background
cmd /k "java -cp build main.java.server.WebsocketServer"

endlocal