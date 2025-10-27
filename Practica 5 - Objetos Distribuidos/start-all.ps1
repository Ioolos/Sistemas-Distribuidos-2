# Start-all script for Biblioteca project (Windows PowerShell)
# It compiles the project, starts rmiregistry and opens separate PowerShell windows for each server and the client.
# Usage: Right-click -> Run with PowerShell or execute from a PowerShell prompt in project root.

param(
    [switch]$RunClient
)

$projectRoot = "d:\ESCOM\8vo\SD\ObjetosDistribuidos"
$classesDir = "${projectRoot}\target\classes"

Write-Host "Compiling project..."
cd $projectRoot
mvn -DskipTests=true compile
if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven compile failed. Aborting."
    exit 1
}

# Start rmiregistry in a new window
Write-Host "Starting rmiregistry..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$classesDir'; rmiregistry"
Start-Sleep -Seconds 1

# Start ServidorBibliotecaInventario
Write-Host "Starting ServidorBibliotecaInventario..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$projectRoot'; java -cp target\classes com.mycompany.objetosdistribuidos.ServidorBibliotecaInventario"
Start-Sleep -Seconds 1

# Start ServidorBibliotecaSincronizacion
Write-Host "Starting ServidorBibliotecaSincronizacion..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$projectRoot'; java -cp target\classes com.mycompany.objetosdistribuidos.ServidorBibliotecaSincronizacion"
Start-Sleep -Seconds 1

# Start ServidorBibliotecaAutenticacion
Write-Host "Starting ServidorBibliotecaAutenticacion..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$projectRoot'; java -cp target\classes com.mycompany.objetosdistribuidos.ServidorBibliotecaAutenticacion"
Start-Sleep -Seconds 1

# Start ServidorBibliotecaOperaciones (last)
Write-Host "Starting ServidorBibliotecaOperaciones..."
Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$projectRoot'; java -cp target\classes com.mycompany.objetosdistribuidos.ServidorBibliotecaOperaciones"
Start-Sleep -Seconds 1

if ($RunClient) {
    Write-Host "Starting ClienteBiblioteca..."
    Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$projectRoot'; java -cp target\classes com.mycompany.objetosdistribuidos.ClienteBiblioteca"
}

Write-Host "All processes started. Check the opened windows for logs."
