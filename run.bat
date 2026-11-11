@echo off
title Personal Expense Tracker
echo ===================================================
echo     Compiling and Running Personal Expense Tracker
echo ===================================================

cd /d "%~dp0"

if not exist bin mkdir bin
if not exist data mkdir data

javac -d bin src\com\tracker\model\*.java src\com\tracker\validation\*.java src\com\tracker\storage\*.java src\com\tracker\service\*.java src\com\tracker\ui\*.java src\com\tracker\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please check the errors above.
    pause
    exit /b %ERRORLEVEL%
)

cls
java -cp bin com.tracker.Main
pause
