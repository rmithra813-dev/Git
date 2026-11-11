@echo off
title Compile Personal Expense Tracker
echo Compiling Personal Expense Tracker...

cd /d "%~dp0"
if not exist bin mkdir bin

javac -d bin src\com\tracker\model\*.java src\com\tracker\validation\*.java src\com\tracker\storage\*.java src\com\tracker\service\*.java src\com\tracker\ui\*.java src\com\tracker\Main.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Build completed cleanly. Class files placed in bin/
) else (
    echo [ERROR] Build failed.
)
pause
