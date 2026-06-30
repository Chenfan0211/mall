param(
    [string]$SqlFile = "Sql/mall.sql",
    [string]$OutputFile = "Sql/warehouse-h5-test-data-20260629.sql"
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$anchorMarker = "ACC_TEST_WMS_BUYER_H5"
$separatorMarker = "-- ----------------------------"

if (-not (Test-Path $SqlFile)) {
    throw "SQL file not found: $SqlFile"
}

$lines = Get-Content -Encoding UTF8 $SqlFile
$anchor = -1
for ($i = 0; $i -lt $lines.Count; $i++) {
    if ($lines[$i].Contains($anchorMarker)) {
        $anchor = $i
        break
    }
}

if ($anchor -lt 0) {
    throw "Warehouse H5 2026-06-29 test-data block not found in $SqlFile"
}

$start = 0
for ($i = $anchor; $i -ge 0; $i--) {
    if ($lines[$i].StartsWith($separatorMarker)) {
        $start = $i
        break
    }
}

$end = $lines.Count - 1
for ($i = $start + 1; $i -lt $lines.Count; $i++) {
    if ($lines[$i].StartsWith($separatorMarker) -and $i -gt ($start + 6)) {
        $end = $i - 1
        break
    }
}

$block = @(
    "-- Warehouse H5 test data exported from $SqlFile"
    "-- Import this after the base schema/data when validating http://192.168.28.211/warehouse/"
    "SET NAMES utf8mb4;"
    "SET FOREIGN_KEY_CHECKS = 0;"
    ""
) + $lines[$start..$end] + @(
    ""
    "SET FOREIGN_KEY_CHECKS = 1;"
)

$outDir = Split-Path -Parent $OutputFile
if ($outDir -and -not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

Set-Content -Path $OutputFile -Encoding UTF8 -Value $block
Write-Host "Exported warehouse H5 test SQL: $OutputFile"
