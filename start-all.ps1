$ErrorActionPreference = "Stop"

$root = $PSScriptRoot
$services = @(
    @{
        Name = "Product MS - 8081"
        Path = "rest_ms_product_service"
    },
    @{
        Name = "Cart MS - 8082"
        Path = "rest_ms_cart_service"
    },
    @{
        Name = "Order MS - 8083"
        Path = "rest_ms_order-service"
    },
    @{
        Name = "API Gateway - 8080"
        Path = "rest_api_gateway"
    }
)

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker no esta instalado o no se encuentra en PATH."
}

foreach ($service in $services) {
    $wrapper = Join-Path $root "$($service.Path)\gradlew.bat"

    if (-not (Test-Path -LiteralPath $wrapper)) {
        throw "No se encontro el Gradle Wrapper: $wrapper"
    }
}

$postgresAvailable = Test-NetConnection `
    -ComputerName "localhost" `
    -Port 5432 `
    -InformationLevel Quiet `
    -WarningAction SilentlyContinue

if (-not $postgresAvailable) {
    throw @"
PostgreSQL no esta disponible en localhost:5432.
Inicia PostgreSQL y verifica las bases ms_product_db y ms_order_db.
"@
}

Write-Host "Iniciando Kafka y Redis..." -ForegroundColor Cyan
Push-Location $root

try {
    docker compose up -d --wait

    if ($LASTEXITCODE -ne 0) {
        throw "Docker Compose no pudo iniciar Kafka y Redis."
    }
}
finally {
    Pop-Location
}

foreach ($service in $services) {
    $servicePath = Join-Path $root $service.Path
    $escapedPath = $servicePath.Replace("'", "''")
    $windowTitle = $service.Name.Replace("'", "''")

    $childCommand = @"
Set-Location -LiteralPath '$escapedPath'
`$Host.UI.RawUI.WindowTitle = '$windowTitle'
& '.\gradlew.bat' bootRun
"@

    $encodedCommand = [Convert]::ToBase64String(
        [Text.Encoding]::Unicode.GetBytes($childCommand)
    )

    $process = Start-Process `
        -FilePath "powershell.exe" `
        -ArgumentList "-NoExit", "-EncodedCommand", $encodedCommand `
        -PassThru

    Write-Host (
        "Iniciado {0} (PID {1})" -f
        $service.Name,
        $process.Id
    ) -ForegroundColor Green

    Start-Sleep -Milliseconds 500
}

Write-Host ""
Write-Host "Servicios en proceso de arranque:" -ForegroundColor Cyan
Write-Host "  Gateway : http://localhost:8080"
Write-Host "  Product : http://localhost:8081"
Write-Host "  Cart    : http://localhost:8082"
Write-Host "  Order   : http://localhost:8083"
Write-Host "  Kafka   : localhost:9092"
Write-Host "  Redis   : localhost:6379"
Write-Host ""
Write-Host "Revisa cada terminal hasta ver que Spring Boot inicio correctamente."
