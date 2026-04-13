param(
    [string]$BackupDir = "./backups"
)

$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path $BackupDir | Out-Null

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$outFile = Join-Path $BackupDir ("backup_" + $timestamp + ".sql")

$mysqlUser = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "shift_user" }
$mysqlPassword = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "shift_pass" }
$mysqlDatabase = if ($env:MYSQL_DATABASE) { $env:MYSQL_DATABASE } else { "shiftmanage" }

$dump = "mysqldump -u$mysqlUser -p$mysqlPassword $mysqlDatabase"
$cmd = "docker compose exec -T mysql sh -c '$dump'"
Invoke-Expression "$cmd > `"$outFile`""

Write-Host "Backup created: $outFile"
