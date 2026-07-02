param(
    [string]$Url = "http://192.168.28.242/warehouse/",
    [int]$DebugPort = 9333
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$scriptPath = Join-Path $PSScriptRoot "verify-warehouse-h5-local.ps1"
if (-not (Test-Path $scriptPath)) {
    throw "Browser smoke script not found: $scriptPath"
}

Write-Host "Running warehouse H5 browser smoke check: $Url"
& powershell -NoProfile -ExecutionPolicy Bypass -File $scriptPath -Url $Url -DebugPort $DebugPort
if ($LASTEXITCODE -ne 0) {
    throw "Warehouse H5 browser smoke check failed (exit code $LASTEXITCODE)"
}
