$ErrorActionPreference = "Stop"

Set-Location "$PSScriptRoot\.."

$nodeExe = "node"
$waitOnCli = Join-Path (Get-Location) "node_modules/wait-on/bin/wait-on"
$cypressCli = Join-Path (Get-Location) "node_modules/cypress/bin/cypress"

try {
    Write-Host "[E2E] Starting Docker services with fresh app build..."
    docker-compose up --build -d | Out-Host

    Write-Host "[E2E] Waiting for app health endpoint..."
    & $nodeExe $waitOnCli "http://localhost:8081/login" "--timeout" "180000" | Out-Host

    Write-Host "[E2E] Running Cypress in headless mode with video recording..."
    & $nodeExe $cypressCli run --config video=true
    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0) {
        throw "Cypress exited with code $exitCode"
    }

    Write-Host "[E2E] Done. Videos saved under cypress/videos"
}
finally {
    Write-Host "[E2E] Stopping Docker services..."
    docker-compose down | Out-Host
}
