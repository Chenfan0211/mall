param(
    [string]$ReleaseZip = "",
    [string]$DeployHost = "192.168.28.211",
    [string]$DeployUser = "root",
    [int]$SshPort = 22,
    [string]$NginxContainer = "mall-nginx",
    [string]$RemoteTmpDir = "/tmp",
    [switch]$ImportSql,
    [string]$RemoteSqlImportCommand = "",
    [switch]$VerifyAfterDeploy,
    [switch]$SkipBrowserVerify,
    [int]$VerifyDebugPort = 9333
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Assert-True {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

function Require-Command {
    param([string]$CommandName)
    $cmd = Get-Command $CommandName -ErrorAction SilentlyContinue
    Assert-True ([bool]$cmd) "$CommandName command not found."
}

function Assert-LastExitCode {
    param([string]$Message)
    if ($LASTEXITCODE -ne 0) {
        throw "$Message (exit code $LASTEXITCODE)"
    }
}

function Test-WarehouseSql {
    param([string]$Path)
    Assert-True (Test-Path $Path) "Release package missing SQL test data: $Path"
    $text = Get-Content -Path $Path -Raw
    Assert-True ($text.Contains("ACC_TEST_WMS_BUYER_H5")) "SQL missing buyer test account."
    Assert-True ($text.Contains("ACC_TEST_WMS_MANAGER_H5")) "SQL missing manager test account."
    Assert-True ($text.Contains("ON DUPLICATE KEY UPDATE")) "SQL must update existing buyer/manager accounts."
    Assert-True ($text.Contains("module_code")) "SQL must use sys_role_button.module_code."
    Assert-True ($text.Contains("risk_level")) "SQL must use sys_role_button.risk_level."
    Assert-True (-not $text.Contains("resource_code")) "SQL contains invalid sys_role_button.resource_code."
}

Require-Command ssh
Require-Command scp
Require-Command tar

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
Push-Location $repoRoot
try {
    if (-not $ReleaseZip) {
        $latestZip = Get-ChildItem -Path "deploy\release" -Filter "warehouse-h5-release-*.zip" |
            Sort-Object LastWriteTime -Descending |
            Select-Object -First 1
        Assert-True ([bool]$latestZip) "Release zip not found. Pass -ReleaseZip or run deploy\scripts\package-warehouse-h5-release.ps1 first."
        $ReleaseZip = $latestZip.FullName
    }

    $releaseZipPath = Resolve-Path $ReleaseZip
    Assert-True (Test-Path $releaseZipPath) "Release zip not found: $ReleaseZip"
    if ($ImportSql -and -not $RemoteSqlImportCommand) {
        throw 'ImportSql requires -RemoteSqlImportCommand, for example: docker exec -i mall-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" mall'
    }

    $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $workDir = Join-Path $env:TEMP "warehouse-h5-deploy-$timestamp"
    if (Test-Path $workDir) {
        Remove-Item -Recurse -Force $workDir
    }
    New-Item -ItemType Directory -Path $workDir | Out-Null

    Expand-Archive -Path $releaseZipPath -DestinationPath $workDir -Force
    $warehouseDir = Join-Path $workDir "warehouse"
    $sqlPath = Join-Path $workDir "sql\warehouse-h5-test-data-20260629.sql"
    Assert-True (Test-Path (Join-Path $warehouseDir "index.html")) "Release package missing warehouse/index.html."
    Assert-True (Test-Path $sqlPath) "Release package missing SQL test data."
    Test-WarehouseSql $sqlPath

    $index = Get-Content -Path (Join-Path $warehouseDir "index.html") -Raw
    $entryAsset = ([regex]::Matches($index, '/warehouse/assets/[^"'']+\.js') | Select-Object -First 1).Value
    Assert-True ([bool]$entryAsset) "No /warehouse/ JS asset found in release index.html."

    $bundleText = Get-Content -Path (Join-Path $workDir $entryAsset.TrimStart("/").Replace("/", "\")) -Raw
    $chunkAssets = [regex]::Matches($bundleText, 'assets/[A-Za-z0-9_.-]+\.js') |
        ForEach-Object { $_.Value } |
        Select-Object -Unique
    foreach ($chunk in $chunkAssets) {
        $chunkPath = Join-Path $warehouseDir $chunk.Replace("/", "\")
        if (Test-Path $chunkPath) {
            $bundleText += Get-Content -Path $chunkPath -Raw
        }
    }
    Assert-True ($bundleText.Contains("/auth/password-login")) "Release package missing /auth/password-login."
    Assert-True ($bundleText.Contains("mall_warehouse_h5_session")) "Release package missing mall_warehouse_h5_session."
    Assert-True ($bundleText.Contains("mall_warehouse_h5_driver_tab")) "Release package missing mall_warehouse_h5_driver_tab."
    Assert-True ($bundleText.Contains("driver-stats-summary")) "Release package missing driver stats page."
    Assert-True ($bundleText.Contains("warehouse-bottom")) "Release package missing warehouse-bottom."

    $localArchive = Join-Path $workDir "warehouse-h5-release-$timestamp.tar.gz"
    tar -czf $localArchive -C $warehouseDir .
    Assert-LastExitCode "Failed to create local archive"
    Assert-True (Test-Path $localArchive) "Failed to create local archive."

    $sshTarget = "$DeployUser@$DeployHost"
    $remoteArchive = "$RemoteTmpDir/warehouse-h5-release-$timestamp.tar.gz"
    $remoteSqlPath = "$RemoteTmpDir/warehouse-h5-test-data-$timestamp.sql"
    $sshOptions = @("-p", "$SshPort", "-o", "ConnectTimeout=20", "-o", "ServerAliveInterval=30", "-o", "ServerAliveCountMax=6", "-o", "StrictHostKeyChecking=accept-new")
    $scpOptions = @("-P", "$SshPort", "-o", "ConnectTimeout=20", "-o", "ServerAliveInterval=30", "-o", "ServerAliveCountMax=6", "-o", "StrictHostKeyChecking=accept-new")

    Write-Host "Uploading static bundle to ${sshTarget}:${remoteArchive}"
    scp @scpOptions $localArchive "${sshTarget}:${remoteArchive}"
    Assert-LastExitCode "Failed to upload static bundle"

    if ($ImportSql) {
        Write-Host "Uploading SQL test data to ${sshTarget}:${remoteSqlPath}"
        scp @scpOptions $sqlPath "${sshTarget}:${remoteSqlPath}"
        Assert-LastExitCode "Failed to upload SQL test data"
    }

    $remoteDeployScript = @"
set -euo pipefail
work_dir="/tmp/warehouse-h5-release-${timestamp}"
backup_dir="/tmp/warehouse-h5-backup-${timestamp}"
mkdir -p "`$work_dir"
tar -xzf "${remoteArchive}" -C "`$work_dir"
docker exec "${NginxContainer}" sh -c "rm -rf '${backup_dir}' && cp -a /usr/share/nginx/html/warehouse '${backup_dir}'"
docker exec "${NginxContainer}" sh -c "rm -rf /usr/share/nginx/html/warehouse/*"
docker cp "`$work_dir/." "${NginxContainer}:/usr/share/nginx/html/warehouse/"
docker exec "${NginxContainer}" nginx -t
rm -rf "`$work_dir" "${remoteArchive}"
echo "WAREHOUSE_H5_DEPLOYED backup=${backup_dir}"
"@
    $remoteDeployScript | ssh @sshOptions $sshTarget "bash -s"
    Assert-LastExitCode "Remote warehouse H5 deployment failed"

    if ($ImportSql) {
        $commandBase64 = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($RemoteSqlImportCommand))
        $remoteSqlScript = @"
set -euo pipefail
remote_sql_import_command="`$(printf '%s' '${commandBase64}' | base64 -d)"
sh -c "`$remote_sql_import_command" < "${remoteSqlPath}"
rm -f "${remoteSqlPath}"
echo "WAREHOUSE_H5_SQL_IMPORTED"
"@
        $remoteSqlScript | ssh @sshOptions $sshTarget "bash -s"
        Assert-LastExitCode "Remote warehouse H5 SQL import failed"
    }

    Write-Host "Checking public warehouse entry"
    $indexOnline = (Invoke-WebRequest -Uri "http://$DeployHost/warehouse/" -UseBasicParsing -TimeoutSec 15).Content
    $assets = [regex]::Matches($indexOnline, '/warehouse/assets/[^"'']+\.js') |
        ForEach-Object { $_.Value } |
        Select-Object -Unique
    Assert-True ($assets.Count -gt 0) "No warehouse H5 JS asset found after deployment."
    $onlineBundle = ""
    foreach ($asset in $assets) {
        $onlineBundle += (Invoke-WebRequest -Uri "http://$DeployHost$asset" -UseBasicParsing -TimeoutSec 30).Content
    }
    $onlineChunks = [regex]::Matches($onlineBundle, 'assets/[A-Za-z0-9_.-]+\.js') |
        ForEach-Object { "/warehouse/$($_.Value)" } |
        Select-Object -Unique
    foreach ($chunk in $onlineChunks) {
        try {
            $onlineBundle += (Invoke-WebRequest -Uri "http://$DeployHost$chunk" -UseBasicParsing -TimeoutSec 30).Content
        } catch {
            Write-Host "Skipping optional chunk: $chunk"
        }
    }
    Assert-True ($onlineBundle.Contains("/auth/password-login")) "Warehouse H5 bundle is old after deployment: missing /auth/password-login."
    Assert-True ($onlineBundle.Contains("mall_warehouse_h5_session")) "Warehouse H5 bundle is old after deployment: missing session logic."
    Assert-True ($onlineBundle.Contains("mall_warehouse_h5_driver_tab")) "Warehouse H5 bundle is old after deployment: missing driver tab logic."
    Assert-True ($onlineBundle.Contains("driver-stats-summary")) "Warehouse H5 bundle is old after deployment: missing driver stats page."
    Assert-True ($onlineBundle.Contains("warehouse-bottom")) "Warehouse H5 bundle is old after deployment: missing custom bottom navigation."
    Write-Host "Warehouse H5 deployed: http://$DeployHost/warehouse/"

    if ($VerifyAfterDeploy) {
        $verifyScript = Join-Path $repoRoot "deploy\scripts\verify-warehouse-h5-full.ps1"
        Assert-True (Test-Path $verifyScript) "Full verify script not found: $verifyScript"
        $verifyArgs = @(
            "-NoProfile",
            "-ExecutionPolicy", "Bypass",
            "-File", $verifyScript,
            "-HostUrl", "http://$DeployHost",
            "-DebugPort", "$VerifyDebugPort"
        )
        if ($SkipBrowserVerify) {
            $verifyArgs += "-SkipBrowser"
        }
        Write-Host "Running post-deploy verification"
        & powershell @verifyArgs
        Assert-LastExitCode "Post-deploy verification failed"
    }
} finally {
    Pop-Location
}
