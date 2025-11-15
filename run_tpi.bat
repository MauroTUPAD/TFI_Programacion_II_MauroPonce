@echo off
echo ===============================
echo Ejecutando TPI Programacion 2
echo ===============================
cd /d "%~dp0"
javac -d out -cp "lib\mysql-connector-j-8.3.0.jar" src\**\*.java
if %errorlevel% neq 0 (
    echo Error al compilar.
    pause
    exit /b
)
echo Compilacion exitosa.
java -cp "out;lib\mysql-connector-j-8.3.0.jar" main.AppMenu
pause
