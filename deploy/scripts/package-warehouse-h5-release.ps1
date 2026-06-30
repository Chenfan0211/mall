param(
    [string]$NodeBin = "C:\Users\Administrator\AppData\Local\OpenAI\Codex\runtimes\cua_node\1b23c930bdf84ed6\bin",
    [string]$ReleaseDir = "deploy\release",
    [string]$PublicBase = "/warehouse/"
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Assert-True {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

function Assert-PowerShellParse {
    param([string]$Path)
    $tokens = $null
    $errors = $null
    [System.Management.Automation.Language.Parser]::ParseFile($Path, [ref]$tokens, [ref]$errors) | Out-Null
    Assert-True (($errors | Measure-Object).Count -eq 0) ("PowerShell syntax error in $Path`: " + (($errors | ForEach-Object { $_.Message }) -join "; "))
}

function Assert-LastExitCode {
    param([string]$Message)
    if ($LASTEXITCODE -ne 0) {
        throw "$Message (exit code $LASTEXITCODE)"
    }
}

function Test-WarehouseSql {
    param([string]$Path)
    Assert-True (Test-Path $Path) "Warehouse H5 SQL file not found: $Path"
    $text = Get-Content -Path $Path -Raw
    Assert-True ($text.Contains("ACC_TEST_WMS_BUYER_H5")) "Warehouse H5 SQL missing buyer test account."
    Assert-True ($text.Contains("ACC_TEST_WMS_MANAGER_H5")) "Warehouse H5 SQL missing manager test account."
    Assert-True ($text.Contains("ON DUPLICATE KEY UPDATE")) "Warehouse H5 SQL must update existing buyer/manager accounts."
    Assert-True ($text.Contains("module_code")) "Warehouse H5 SQL must use sys_role_button.module_code."
    Assert-True ($text.Contains("risk_level")) "Warehouse H5 SQL must use sys_role_button.risk_level."
    Assert-True (-not $text.Contains("resource_code")) "Warehouse H5 SQL contains invalid sys_role_button.resource_code."
}

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$warehouseRoot = Join-Path $repoRoot "web\warehouse-h5"
$npm = Join-Path $NodeBin "npm.cmd"

Assert-True (Test-Path $warehouseRoot) "warehouse-h5 root not found."
Assert-True (Test-Path $npm) "npm.cmd not found: $npm"
Assert-PowerShellParse (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5.ps1")
Assert-PowerShellParse (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5-browser.ps1")
Assert-PowerShellParse (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5-full.ps1")
Assert-PowerShellParse (Join-Path $repoRoot "deploy\scripts\diagnose-warehouse-h5.ps1")
Assert-PowerShellParse (Join-Path $repoRoot "deploy\scripts\deploy-warehouse-h5-release.ps1")

$env:PATH = "$NodeBin;$env:PATH"
$env:VITE_PUBLIC_BASE = $PublicBase

Write-Host "Building warehouse-h5 with VITE_PUBLIC_BASE=$PublicBase"
Push-Location $warehouseRoot
try {
    & $npm run build:h5
    Assert-LastExitCode "warehouse-h5 build failed"
} finally {
    Pop-Location
}

$dist = Join-Path $warehouseRoot "dist\build\h5"
$sql = Join-Path $repoRoot "Sql\warehouse-h5-test-data-20260629.sql"
Assert-True (Test-Path (Join-Path $dist "index.html")) "H5 dist index.html not found."
Assert-True (Test-Path $sql) "Warehouse H5 SQL file not found."
Test-WarehouseSql $sql

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$releaseRoot = Join-Path $repoRoot $ReleaseDir
New-Item -ItemType Directory -Force -Path $releaseRoot | Out-Null

$temp = Join-Path $env:TEMP "warehouse-h5-release-$stamp"
if (Test-Path $temp) {
    Remove-Item -Recurse -Force $temp
}
New-Item -ItemType Directory -Path $temp | Out-Null
Copy-Item -Recurse -Path $dist -Destination (Join-Path $temp "warehouse")
New-Item -ItemType Directory -Path (Join-Path $temp "sql") | Out-Null
Copy-Item -Path $sql -Destination (Join-Path $temp "sql\warehouse-h5-test-data-20260629.sql")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5.ps1") -Destination (Join-Path $temp "verify-warehouse-h5.ps1")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5-local.ps1") -Destination (Join-Path $temp "verify-warehouse-h5-local.ps1")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5-browser.ps1") -Destination (Join-Path $temp "verify-warehouse-h5-browser.ps1")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5-full.ps1") -Destination (Join-Path $temp "verify-warehouse-h5-full.ps1")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\diagnose-warehouse-h5.ps1") -Destination (Join-Path $temp "diagnose-warehouse-h5.ps1")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\deploy-warehouse-h5.sh") -Destination (Join-Path $temp "deploy-warehouse-h5.sh")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\deploy-warehouse-h5-release.sh") -Destination (Join-Path $temp "deploy-warehouse-h5-release.sh")
Copy-Item -Path (Join-Path $repoRoot "deploy\scripts\deploy-warehouse-h5-release.ps1") -Destination (Join-Path $temp "deploy-warehouse-h5-release.ps1")
New-Item -ItemType Directory -Path (Join-Path $temp "docs") | Out-Null
$deployDoc = Get-ChildItem -Path (Join-Path $repoRoot "docs\deploy") -Filter "*H5*.md" |
    Where-Object { $_.Name -like "*发布*" -and $_.Name -like "*验收*" } |
    Select-Object -First 1
Assert-True ([bool]$deployDoc) "Warehouse H5 deploy document not found."
Copy-Item -Path $deployDoc.FullName -Destination (Join-Path $temp "docs\warehouse-h5-release.md")

$zip = Join-Path $releaseRoot "warehouse-h5-release-$stamp.zip"
$manifest = Join-Path $releaseRoot "warehouse-h5-release-$stamp-manifest.txt"
Compress-Archive -Path (Join-Path $temp "*") -DestinationPath $zip -Force

$index = Get-Content -Path (Join-Path $temp "warehouse\index.html") -Raw
$entryAsset = ([regex]::Matches($index, '/warehouse/assets/[^"'']+\.js') | Select-Object -First 1).Value
Assert-True ([bool]$entryAsset) "No warehouse JS entry found in index.html."

$bundleText = Get-Content -Path (Join-Path $temp $entryAsset.TrimStart("/").Replace("warehouse/", "warehouse/").Replace("/", "\")) -Raw
$releaseSqlPath = Join-Path $temp "sql\warehouse-h5-test-data-20260629.sql"
Test-WarehouseSql $releaseSqlPath
$chunkAssets = [regex]::Matches($bundleText, 'assets/[A-Za-z0-9_.-]+\.js') |
    ForEach-Object { $_.Value } |
    Select-Object -Unique
foreach ($chunk in $chunkAssets) {
    $chunkPath = Join-Path (Join-Path $temp "warehouse") $chunk.Replace("/", "\")
    if (Test-Path $chunkPath) {
        $bundleText += Get-Content -Path $chunkPath -Raw
    }
}

$hash = Get-FileHash -Algorithm SHA256 $zip
$checks = [PSCustomObject]@{
    Zip = (Resolve-Path $zip).Path
    Sha256 = $hash.Hash
    EntryAsset = $entryAsset
    ChunkCount = $chunkAssets.Count
    HasPasswordLogin = $bundleText.Contains("/auth/password-login")
    HasSession = $bundleText.Contains("mall_warehouse_h5_session")
    HasDriverTab = $bundleText.Contains("mall_warehouse_h5_driver_tab")
    HasDriverMapMode = $bundleText.Contains("driver-map-mode")
    HasDataSourceChip = $bundleText.Contains("data-source-chip")
    HasDriverStats = $bundleText.Contains("driver-stats-summary")
    HasReceiveRecord = $bundleText.Contains("warehouse-receive-record-card")
    HasPickRecord = $bundleText.Contains("warehouse-pick-record-card")
    HasPurchaseCreate = $bundleText.Contains("purchase-qty-stepper")
    HasPurchaseReceipt = $bundleText.Contains("receipt-gallery")
    HasLoadingReceipt = $bundleText.Contains("receipt-stamp")
    HasBottomNav = $bundleText.Contains("warehouse-bottom")
    HasWindowsDeployScript = Test-Path (Join-Path $temp "deploy-warehouse-h5-release.ps1")
    HasLinuxDeployScript = Test-Path (Join-Path $temp "deploy-warehouse-h5-release.sh")
    HasLegacyDeployScript = Test-Path (Join-Path $temp "deploy-warehouse-h5.sh")
    HasVerifyScript = Test-Path (Join-Path $temp "verify-warehouse-h5.ps1")
    HasLocalVerifyScript = Test-Path (Join-Path $temp "verify-warehouse-h5-local.ps1")
    HasBrowserVerifyScript = Test-Path (Join-Path $temp "verify-warehouse-h5-browser.ps1")
    HasFullVerifyScript = Test-Path (Join-Path $temp "verify-warehouse-h5-full.ps1")
    HasDiagnoseScript = Test-Path (Join-Path $temp "diagnose-warehouse-h5.ps1")
    HasDeployDoc = Test-Path (Join-Path $temp "docs\warehouse-h5-release.md")
    SqlBytes = (Get-Item $releaseSqlPath).Length
    SqlHasAccountUpsert = (Get-Content -Path $releaseSqlPath -Raw).Contains("ON DUPLICATE KEY UPDATE")
    SqlHasModuleCode = (Get-Content -Path $releaseSqlPath -Raw).Contains("module_code")
    SqlHasNoResourceCode = -not (Get-Content -Path $releaseSqlPath -Raw).Contains("resource_code")
}

Assert-True $checks.HasPasswordLogin "Release package missing /auth/password-login."
Assert-True $checks.HasSession "Release package missing mall_warehouse_h5_session."
Assert-True $checks.HasDriverTab "Release package missing mall_warehouse_h5_driver_tab."
Assert-True $checks.HasDriverMapMode "Release package missing driver-map-mode."
Assert-True $checks.HasDataSourceChip "Release package missing data-source-chip."
Assert-True $checks.HasDriverStats "Release package missing driver stats tab."
Assert-True $checks.HasReceiveRecord "Release package missing receiver receive record page."
Assert-True $checks.HasPickRecord "Release package missing picker pick record page."
Assert-True $checks.HasPurchaseCreate "Release package missing buyer purchase quantity editor."
Assert-True $checks.HasPurchaseReceipt "Release package missing purchase receipt gallery."
Assert-True $checks.HasLoadingReceipt "Release package missing loading receipt stamp."
Assert-True $checks.HasBottomNav "Release package missing warehouse-bottom."
Assert-True $checks.HasWindowsDeployScript "Release package missing deploy-warehouse-h5-release.ps1."
Assert-True $checks.HasLinuxDeployScript "Release package missing deploy-warehouse-h5-release.sh."
Assert-True $checks.HasLegacyDeployScript "Release package missing deploy-warehouse-h5.sh."
Assert-True $checks.HasVerifyScript "Release package missing verify-warehouse-h5.ps1."
Assert-True $checks.HasLocalVerifyScript "Release package missing verify-warehouse-h5-local.ps1."
Assert-True $checks.HasBrowserVerifyScript "Release package missing verify-warehouse-h5-browser.ps1."
Assert-True $checks.HasFullVerifyScript "Release package missing verify-warehouse-h5-full.ps1."
Assert-True $checks.HasDiagnoseScript "Release package missing diagnose-warehouse-h5.ps1."
Assert-True $checks.HasDeployDoc "Release package missing deployment document."
Assert-True ($checks.SqlBytes -gt 0) "Release package SQL is empty."
Assert-True $checks.SqlHasAccountUpsert "Release package SQL missing account upsert."
Assert-True $checks.SqlHasModuleCode "Release package SQL missing module_code."
Assert-True $checks.SqlHasNoResourceCode "Release package SQL contains invalid resource_code."

Push-Location $temp
try {
    $files = Get-ChildItem -Recurse -File |
        ForEach-Object { (Resolve-Path -Relative $_.FullName).TrimStart(".", "\", "/") + "`t" + $_.Length }
} finally {
    Pop-Location
}
$checks | Format-List | Out-String | Set-Content -Path $manifest -Encoding UTF8
"`nFiles:" | Add-Content -Path $manifest -Encoding UTF8
$files | Add-Content -Path $manifest -Encoding UTF8

$checks | Format-List
Write-Host "Manifest: $manifest"
