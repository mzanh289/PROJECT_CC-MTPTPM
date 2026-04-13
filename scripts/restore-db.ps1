param(
    [Parameter(Mandatory = $true)]
    [string]$BackupFile
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $BackupFile)) {
    throw "File not found: $BackupFile"
}

$mysqlUser = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "shift_user" }
$mysqlPassword = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "shift_pass" }
$mysqlDatabase = if ($env:MYSQL_DATABASE) { $env:MYSQL_DATABASE } else { "shiftmanage" }

Get-Content -Raw $BackupFile | docker compose exec -T mysql sh -c "mysql -u$mysqlUser -p$mysqlPassword $mysqlDatabase"

Write-Host "Restore completed from: $BackupFile"
