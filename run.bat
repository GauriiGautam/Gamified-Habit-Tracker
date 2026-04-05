@echo off
echo ========================================
echo        GAMIFIED HABIT TRACKER
echo ========================================
echo.

if not exist "bin" mkdir bin

echo Compiling Java files...

javac -cp "lib\mysql-connector-j-9.0.0.jar" -d bin ^
src\com\habit\db\DBConnection.java ^
src\com\habit\exceptions\HabitException.java ^
src\com\habit\interfaces\HabitOperations.java ^
src\com\habit\model\User.java ^
src\com\habit\model\AdminUser.java ^
src\com\habit\model\Habit.java ^
src\com\habit\model\HabitLog.java ^
src\com\habit\dao\HabitDAO.java ^
src\com\habit\gui\LoginFrame.java ^
src\com\habit\gui\RegisterFrame.java ^
src\com\habit\gui\DashboardFrame.java ^
src\com\habit\gui\AddHabitFrame.java ^
src\com\habit\gui\LogHabitFrame.java ^
src\com\habit\main\Main.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation Failed! Check errors above.
    pause
    exit
)

echo.
echo Compilation Successful!
echo Starting Application...
echo.
java -cp "bin;lib\mysql-connector-j-9.0.0.jar" com.habit.main.Main

pause