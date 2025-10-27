# Test script: authenticate, borrow B100, then return B100 using raw sockets
Write-Host "Starting test sequence..."

function Send-Request {
    param($server, $port, $lines)
    $client = New-Object System.Net.Sockets.TcpClient
    $client.Connect($server, $port)
    $stream = $client.GetStream()
    $writer = New-Object System.IO.StreamWriter($stream)
    $writer.NewLine = "`n"
    $reader = New-Object System.IO.StreamReader($stream)
    foreach ($l in $lines) { $writer.WriteLine($l) }
    $writer.Flush()
    Start-Sleep -Milliseconds 100
    $out = $reader.ReadLine()
    $reader.Close(); $writer.Close(); $client.Close()
    return $out
}

$serverHost = 'localhost'

# 1) Auth
 $res = Send-Request -server $serverHost -port 7000 -lines @('user1','1111')
Write-Host "Auth response: $res"

# 2) Check B100 (catalog)
 $res = Send-Request -server $serverHost -port 8000 -lines @('C','B100')
Write-Host "Catalog B100 before: $res"

# 3) Borrow B100
 $res = Send-Request -server $serverHost -port 9000 -lines @('B','user1','B100')
Write-Host "Borrow response: $res"

# 4) Check B100 after borrow
 $res = Send-Request -server $serverHost -port 8000 -lines @('C','B100')
Write-Host "Catalog B100 after borrow: $res"

# 5) Return B100
 $res = Send-Request -server $serverHost -port 9000 -lines @('R','user1','B100')
Write-Host "Return response: $res"

# 6) Check B100 after return
 $res = Send-Request -server $serverHost -port 8000 -lines @('C','B100')
Write-Host "Catalog B100 after return: $res"

Write-Host "Test sequence finished."