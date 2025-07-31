@echo off
setlocal

:: Compile Java files
echo Compiling Java source files...
powershell -Command "$files = Get-ChildItem -Path 'src' -Recurse -Filter '*.java'; & javac -encoding UTF-8 -d build $files.FullName;"
if errorlevel 1 (
    echo Compilation failed. Exiting.
    pause
    exit /b 1
)

:: Create the JAR file
echo Creating JAR file...
jar cvfm DUTCH.jar MANIFEST.MF -C build/ . -C ./ README.md AUTHORS.md
if errorlevel 1 (
    echo JAR creation failed. Exiting.
    exit /b 1
)

:: Run the JAR file with arguments
echo Running the application...
start javaw -jar DUTCH.jar %*
if errorlevel 1 (
    echo Application execution failed. Exiting.
    exit /b 1
)

echo Done.
endlocal