@echo off
echo ========================================
echo   GESTOR DE NOTAS V2 - INICIAR SERVIDOR
echo ========================================
echo.
cd /d "%~dp0"
echo Compilando el proyecto...
call mvn clean compile
echo.
echo Iniciando el servidor en el puerto 3000 (Version con ReentrantLock)...
echo Presiona Ctrl+C para detener el servidor
echo.
java -cp target/classes com.mycompany.atm.cs.Servidor
pause
