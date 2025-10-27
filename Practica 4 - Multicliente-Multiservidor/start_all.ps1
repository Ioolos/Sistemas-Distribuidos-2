<#
PowerShell script to compile, start all servers in the correct order and then open the GUI.
Usage: Right-click and "Run with PowerShell" or run from PowerShell:
    .\start_all.ps1
#>

Set-StrictMode -Version Latest

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Write-Host "Working directory: $scriptDir"

function Wait-ForPort {
    param(
        [int]$Port,
        [int]$TimeoutSeconds = 30
    )
    $end = (Get-Date).AddSeconds($TimeoutSeconds)
    while((Get-Date) -lt $end) {
        $hit = netstat -ano | Select-String ":$Port"
        if ($hit) { return $true }
        Start-Sleep -Seconds 1
    }
    return $false
}

function Start-ServerWindow {
    param(
        [string]$JavaClass,
        [int]$Port
    )
    Write-Host "Starting $JavaClass (expecting port $Port)..."
    $cmd = "Set-Location '$scriptDir'; java -cp target/classes $JavaClass"
    Start-Process powershell -ArgumentList '-NoExit','-Command', $cmd
    Write-Host "Waiting for port $Port to listen..."
    if (-not (Wait-ForPort -Port $Port -TimeoutSeconds 20)) {
        Write-Warning "Port $Port did not open within timeout. Check server logs in its window."
    } else {
        Write-Host "Port $Port is listening."
    }
}

Write-Host "Compiling project (mvn compile)..."
Push-Location $scriptDir
try {
    & mvn compile
} catch {
    Write-Warning "mvn compile failed or mvn not found in PATH. Verify Maven installation and try again."
}
Pop-Location

# Start servers in order
Start-ServerWindow -JavaClass 'com.mycompany.biblioteca.ServidorAutenticacionBiblioteca' -Port 7000
Start-ServerWindow -JavaClass 'com.mycompany.biblioteca.ServidorCatalogo' -Port 8000
Start-ServerWindow -JavaClass 'com.mycompany.biblioteca.ServidorSincronizacionBiblioteca' -Port 10000
Start-ServerWindow -JavaClass 'com.mycompany.biblioteca.ServidorPrestamos' -Port 9000

Write-Host "All servers started (or attempted). Launching GUI client..."
$guiCmd = "Set-Location '$scriptDir'; java -cp target/classes com.mycompany.biblioteca.ClienteBibliotecaGUI"
Start-Process powershell -ArgumentList '-NoExit','-Command', $guiCmd

Write-Host "Done. Check the opened PowerShell windows for server logs and use the GUI window to interact."
