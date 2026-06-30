param(
    [string]$HostUrl = "http://192.168.28.211",
    [string]$DeployUser = "root",
    [int]$SshPort = 22,
    [string]$Password = "Test@123456"
)

$ErrorActionPreference = "Continue"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$base = $HostUrl.TrimEnd("/")
$summary = New-Object System.Collections.Generic.List[string]
$rows = New-Object System.Collections.Generic.List[object]

function Add-Row {
    param([string]$Area, [string]$Name, [bool]$Ok, [string]$Detail)
    $script:rows.Add([pscustomobject]@{
        Area = $Area
        Name = $Name
        Ok = $Ok
        Detail = $Detail
    })
    if (-not $Ok) {
        $script:summary.Add("$Area/${Name}: $Detail")
    }
}

function Invoke-Safe {
    param([scriptblock]$Block)
    try {
        & $Block
    } catch {
        $_
    }
}

$latestZip = Get-ChildItem -Path "deploy\release" -Filter "warehouse-h5-release-*.zip" -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1
if ($latestZip) {
    $hash = (Get-FileHash $latestZip.FullName -Algorithm SHA256).Hash
    Add-Row "release" "latest zip" $true "$($latestZip.FullName); sha256=$hash"
    $releaseCheckDir = Join-Path $env:TEMP "warehouse-h5-diagnose-release"
    if (Test-Path $releaseCheckDir) {
        Remove-Item -Recurse -Force $releaseCheckDir
    }
    $releaseExpand = Invoke-Safe {
        Expand-Archive -Path $latestZip.FullName -DestinationPath $releaseCheckDir -Force
    }
    if ($releaseExpand -is [System.Management.Automation.ErrorRecord]) {
        Add-Row "release" "sql content" $false $releaseExpand.Exception.Message
    } else {
        $sqlPath = Join-Path $releaseCheckDir "sql\warehouse-h5-test-data-20260629.sql"
        if (-not (Test-Path $sqlPath)) {
            Add-Row "release" "sql content" $false "warehouse-h5-test-data-20260629.sql not found"
        } else {
            $sqlText = Get-Content -Path $sqlPath -Raw
            Add-Row "release" "sql account upsert" ($sqlText.Contains("ON DUPLICATE KEY UPDATE")) "buyer/manager accounts must be repaired on repeated import"
            Add-Row "release" "sql role button fields" ($sqlText.Contains("module_code") -and $sqlText.Contains("risk_level") -and -not $sqlText.Contains("resource_code")) "must match sys_role_button schema"
        }
    }
} else {
    Add-Row "release" "latest zip" $false "not found"
}

$indexResponse = Invoke-Safe {
    Invoke-WebRequest -Uri "$base/warehouse/" -UseBasicParsing -TimeoutSec 15 -ErrorAction Stop
}
if ($indexResponse -is [System.Management.Automation.ErrorRecord]) {
    Add-Row "warehouse" "entry" $false $indexResponse.Exception.Message
} else {
    Add-Row "warehouse" "entry" $true "reachable"
    $index = [string]$indexResponse.Content
    $assetMatches = [regex]::Matches($index, "/warehouse/assets/[^`"<\s]+\.js")
    $bundleText = ""
    $assetList = New-Object System.Collections.Generic.List[string]
    foreach ($match in $assetMatches) {
        $asset = $match.Value
        if ($assetList.Contains($asset)) {
            continue
        }
        $assetList.Add($asset)
        $assetResponse = Invoke-Safe {
            Invoke-WebRequest -Uri "$base$asset" -UseBasicParsing -TimeoutSec 30 -ErrorAction Stop
        }
        if (-not ($assetResponse -is [System.Management.Automation.ErrorRecord])) {
            $bundleText += [string]$assetResponse.Content
        }
    }
    $chunkMatches = [regex]::Matches($bundleText, "assets/[A-Za-z0-9_.-]+\.js")
    foreach ($match in $chunkMatches) {
        $chunk = "/warehouse/$($match.Value)"
        if ($assetList.Contains($chunk)) {
            continue
        }
        $assetList.Add($chunk)
        $chunkResponse = Invoke-Safe {
            Invoke-WebRequest -Uri "$base$chunk" -UseBasicParsing -TimeoutSec 30 -ErrorAction Stop
        }
        if (-not ($chunkResponse -is [System.Management.Automation.ErrorRecord])) {
            $bundleText += [string]$chunkResponse.Content
        }
    }
    Add-Row "warehouse" "assets" ($assetList.Count -gt 0) (($assetList -join ", "))
    Add-Row "warehouse" "password-login bundle" ($bundleText.Contains("/auth/password-login")) "requires latest H5 bundle"
    Add-Row "warehouse" "session bundle" ($bundleText.Contains("mall_warehouse_h5_session")) "requires latest H5 bundle"
    Add-Row "warehouse" "driver map bundle" ($bundleText.Contains("driver-map-mode")) "requires latest H5 bundle"
    Add-Row "warehouse" "data source chip bundle" ($bundleText.Contains("data-source-chip")) "requires latest H5 bundle"
    Add-Row "warehouse" "driver stats bundle" ($bundleText.Contains("driver-stats-summary")) "requires latest H5 bundle"
    Add-Row "warehouse" "receive record bundle" ($bundleText.Contains("warehouse-receive-record-card")) "requires latest H5 bundle"
    Add-Row "warehouse" "pick record bundle" ($bundleText.Contains("warehouse-pick-record-card")) "requires latest H5 bundle"
    Add-Row "warehouse" "purchase editor bundle" ($bundleText.Contains("purchase-qty-stepper")) "requires latest H5 bundle"
    Add-Row "warehouse" "purchase receipt bundle" ($bundleText.Contains("receipt-gallery")) "requires latest H5 bundle"
    Add-Row "warehouse" "loading receipt bundle" ($bundleText.Contains("receipt-stamp")) "requires latest H5 bundle"
    Add-Row "warehouse" "bottom nav bundle" ($bundleText.Contains("warehouse-bottom")) "requires latest H5 bundle"
}

$roleAccounts = @(
    @{ Role = "receiver"; Username = "test_wms_receiver"; PortalCode = "warehouse-h5" },
    @{ Role = "buyer"; Username = "test_wms_buyer"; PortalCode = "warehouse-h5" },
    @{ Role = "picker"; Username = "test_wms_picker"; PortalCode = "warehouse-h5" },
    @{ Role = "recheck"; Username = "test_wms_loader"; PortalCode = "warehouse-h5" },
    @{ Role = "driver"; Username = "test_wms_driver"; PortalCode = "warehouse-h5" },
    @{ Role = "manager"; Username = "test_wms_manager"; PortalCode = "warehouse-h5" }
)

$receiverToken = $null
foreach ($account in $roleAccounts) {
    $body = @{
        username = $account.Username
        password = $Password
        portalCode = $account.PortalCode
    } | ConvertTo-Json
    $login = Invoke-Safe {
        Invoke-RestMethod -Uri "$base/api/auth/password-login" -Method Post -ContentType "application/json" -Body $body -TimeoutSec 15 -ErrorAction Stop
    }
    if ($login -is [System.Management.Automation.ErrorRecord]) {
        Add-Row "account" $account.Role $false $login.Exception.Message
    } else {
        $ok = ([string]$login.code -eq "0") -and [bool]$login.data.accessToken
        Add-Row "account" $account.Role $ok "username=$($account.Username); code=$($login.code); message=$($login.message)"
        if ($account.Role -eq "receiver" -and $ok) {
            $receiverToken = $login.data.accessToken
        }
    }
}

if ($receiverToken) {
    $headers = @{ Authorization = "Bearer $receiverToken" }
    $paths = @(
        "/api/wms/inbound/orders",
        "/api/wms/inbound/putaway-tasks",
        "/api/wms/inventory/stocks",
        "/api/wms/outbound/picks",
        "/api/wms/outbound/deliveries"
    )
    foreach ($path in $paths) {
        $url = $base + $path + "?pageNum=1" + [char]38 + "pageSize=1"
        $api = Invoke-Safe {
            Invoke-RestMethod -Uri $url -Headers $headers -TimeoutSec 15 -ErrorAction Stop
        }
        if ($api -is [System.Management.Automation.ErrorRecord]) {
            Add-Row "wms-api" $path $false $api.Exception.Message
        } else {
            $total = [int64]$api.data.total
            Add-Row "wms-api" $path ($total -gt 0) "code=$($api.code); total=$total"
        }
    }
} else {
    Add-Row "wms-api" "all" $false "receiver login failed, skipped WMS API checks"
}

$targetHost = ($base -replace "^https?://", "").TrimEnd("/")
$sshTarget = "$DeployUser@$targetHost"
$ssh = Invoke-Safe {
    ssh -o BatchMode=yes -o ConnectTimeout=10 -o StrictHostKeyChecking=accept-new -p $SshPort $sshTarget "echo SSH_OK" 2>&1
}
$sshText = ($ssh | Out-String).Trim()
$sshText = (($sshText -split "(`r`n|`n|`r)") | Where-Object { $_ } | Select-Object -First 1)
Add-Row "ssh" $sshTarget ($sshText -match "SSH_OK") $sshText

Write-Host "Warehouse H5 diagnose result:"
$rows | Format-Table -AutoSize

if ($summary.Count -gt 0) {
    Write-Host ""
    Write-Host "Summary:"
    foreach ($item in $summary) {
        Write-Host "- $item"
    }
    exit 1
}

Write-Host "All checks passed."
