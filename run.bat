@echo off
echo ========================================
echo        GAMIFIED HABIT TRACKER
echo ========================================
echo.

if not exist "bin" mkdir bin

echo Compiling Java files...
javac -cp "lib\mysql-connector-j-9.6.0.jar" -d bin -sourcepath src src\com\habit\main\Main.java src\com\habit\gui\*.java src\com\habit\dao\*.java src\com\habit\db\*.java src\com\habit\model\*.java src\com\habit\interfaces\*.java src\com\habit\exceptions\*.java

echo.
echo Starting Application...
java -cp "bin;lib\mysql-connector-j-9.6.0.jar" com.habit.main.Main

pause