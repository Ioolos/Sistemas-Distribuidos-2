@echo off
echo ========================================
echo   GESTOR DE NOTAS V2 - INICIAR CLIENTE
echo ========================================
echo.
cd /d "%~dp0"
echo Asegurandose de que el proyecto este compilado...
if not exist "target\classes\com\mycompany\atm\cs\ClienteGUI.class" (
    echo Compilando el proyecto...
    call mvn clean compile
)
echo.
echo Iniciando el cliente (Version con ReentrantLock)...
echo Asegurate de que el servidor este ejecutandose en el puerto 3000
echo.
java -cp target/classes com.mycompany.atm.cs.ClienteGUI
pause
