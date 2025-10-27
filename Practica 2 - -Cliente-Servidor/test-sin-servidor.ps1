Write-Host "=== TEST: Cliente sin Servidor ===" -ForegroundColor Cyan
Write-Host "Intentando ejecutar el cliente sin que el servidor esté corriendo..." -ForegroundColor Yellow
Write-Host ""

cd $PSScriptRoot
java -cp target/classes com.mycompany.atm.cs.ClienteGUI

Write-Host ""
Write-Host "Si ves un mensaje de ERROR arriba, entonces la prueba PASÓ :)" -ForegroundColor Green
Write-Host "El cliente correctamente rechaza ejecutarse sin el servidor."  -ForegroundColor Green
