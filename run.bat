@echo off
echo ==============================================================
echo AI-Powered Gamified Habit Tracking System (Enterprise Edition)
echo ==============================================================

echo [1] Ensuring "bin" directory exists...
if not exist "bin" mkdir bin

echo [2] Compiling all Java Source Files...
dir /s /B src\*.java > sources.txt
javac -d bin -cp "lib/mysql-connector-j-8.3.0.jar" @sources.txt
del sources.txt

if %errorlevel% neq 0 (
    echo.
    echo ❌ COMPILATION FAILED. Please check the Java errors above.
    pause
    exit /b %errorlevel%
)

echo [3] Compilation Successful! Launching Client Application...
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" com.habit.ui.MainApplication

pause
