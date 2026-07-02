param(
    [string]$HostUrl = "http://192.168.28.242",
    [string]$Username = "test_wms_receiver",
    [string]$Password = "Test@123456",
    [string]$PortalCode = "warehouse-h5",
    [switch]$SkipRoleAccounts,
    [switch]$StrictRoleAccounts,
    [switch]$OnlyRoleAccounts
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Assert-True {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

$base = $HostUrl.TrimEnd("/")

$roleAccounts = @(
    @{ Role = "receiver"; Username = "test_wms_receiver"; PortalCode = "warehouse-h5" },
    @{ Role = "buyer"; Username = "test_wms_buyer"; PortalCode = "warehouse-h5" },
    @{ Role = "picker"; Username = "test_wms_picker"; PortalCode = "warehouse-h5" },
    @{ Role = "recheck"; Username = "test_wms_loader"; PortalCode = "warehouse-h5" },
    @{ Role = "driver"; Username = "test_wms_driver"; PortalCode = "warehouse-h5" },
    @{ Role = "manager"; Username = "test_wms_manager"; PortalCode = "warehouse-h5" }
)

$fallbackAccounts = @(
    @{ Role = "buyer"; Username = "test_wms_receiver"; PortalCode = "warehouse-h5"; ApiPath = "/api/wms/inbound/orders" },
    @{ Role = "manager"; Username = "test_wms_supervisor"; PortalCode = "wms-admin"; ApiPath = "/api/wms/inventory/stocks" }
)

function Invoke-PasswordLogin {
    param([string]$LoginUsername, [string]$LoginPassword, [string]$LoginPortalCode)
    $body = @{
        username = $LoginUsername
        password = $LoginPassword
        portalCode = $LoginPortalCode
    } | ConvertTo-Json
    Invoke-RestMethod -Uri "$base/api/auth/password-login" -Method Post -ContentType "application/json" -Body $body -TimeoutSec 15
}

function Test-RoleAccounts {
    Write-Host "Checking warehouse H5 role accounts."
    $failedRoles = @()
    foreach ($account in $roleAccounts) {
        try {
            $roleLogin = Invoke-PasswordLogin -LoginUsername $account.Username -LoginPassword $Password -LoginPortalCode $account.PortalCode
            if (([string]$roleLogin.code -ne "0") -or (-not [bool]$roleLogin.data.accessToken)) {
                $failedRoles += "$($account.Role)/$($account.Username): code=$($roleLogin.code), message=$($roleLogin.message)"
            } else {
                Write-Host "$($account.Role) account passed: $($account.Username)"
            }
        } catch {
            $status = $null
            if ($_.Exception.Response) {
                $status = [int]$_.Exception.Response.StatusCode
            }
            $failedRoles += "$($account.Role)/$($account.Username): HTTP $status"
        }
    }
    Assert-True ($failedRoles.Count -eq 0) ("Warehouse H5 role account check failed. Import Sql/warehouse-h5-test-data-20260629.sql first: " + ($failedRoles -join "; "))
}

function Test-FallbackAccounts {
    Write-Host "Checking fallback API sessions for roles without dedicated test accounts."
    foreach ($account in $fallbackAccounts) {
        $login = Invoke-PasswordLogin -LoginUsername $account.Username -LoginPassword $Password -LoginPortalCode $account.PortalCode
        Assert-True ([string]$login.code -eq "0") "$($account.Role) fallback login failed: code=$($login.code), message=$($login.message)"
        Assert-True ([bool]$login.data.accessToken) "$($account.Role) fallback login missing accessToken."
        $headers = @{ Authorization = "Bearer $($login.data.accessToken)" }
        $res = Invoke-RestMethod -Uri "$base$($account.ApiPath)?pageNum=1&pageSize=1" -Headers $headers -TimeoutSec 15
        Assert-True ([string]$res.code -eq "0") "$($account.Role) fallback API failed: code=$($res.code), message=$($res.message)"
        Assert-True ([int64]$res.data.total -gt 0) "$($account.Role) fallback API returned no data."
        Write-Host "$($account.Role) fallback passed: $($account.Username) / $($account.ApiPath) total=$($res.data.total)"
    }
}

if ($OnlyRoleAccounts) {
    Test-RoleAccounts
    Write-Host "Warehouse H5 role account check passed."
    exit 0
}

Write-Host "Checking warehouse H5 entry: $base/warehouse/"
$index = (Invoke-WebRequest -Uri "$base/warehouse/" -UseBasicParsing -TimeoutSec 15).Content
$assets = [regex]::Matches($index, '/warehouse/assets/[^"'']+\.js') |
    ForEach-Object { $_.Value } |
    Select-Object -Unique
Assert-True ($assets.Count -gt 0) "No warehouse H5 JS asset found."

$bundleText = ""
foreach ($asset in $assets) {
    Write-Host "Reading asset: $asset"
    $bundleText += (Invoke-WebRequest -Uri "$base$asset" -UseBasicParsing -TimeoutSec 30).Content
}

$manifestText = ""
try {
    $manifestText = (Invoke-WebRequest -Uri "$base/warehouse/manifest.json" -UseBasicParsing -TimeoutSec 10).Content
} catch {
    $manifestText = ""
}

$chunkAssets = [regex]::Matches($bundleText + $manifestText, 'assets/[A-Za-z0-9_.-]+\.js') |
    ForEach-Object { "/warehouse/$($_.Value)" } |
    Select-Object -Unique
foreach ($asset in $chunkAssets) {
    if ($assets -contains $asset) {
        continue
    }
    try {
        Write-Host "Reading chunk: $asset"
        $bundleText += (Invoke-WebRequest -Uri "$base$asset" -UseBasicParsing -TimeoutSec 30).Content
    } catch {
        Write-Host "Skipping optional chunk: $asset"
    }
}

Assert-True ($bundleText.Contains("/auth/password-login")) "Warehouse H5 bundle is old: missing /auth/password-login."
Assert-True ($bundleText.Contains("mall_warehouse_h5_session")) "Warehouse H5 bundle is old: missing session storage logic."
Assert-True ($bundleText.Contains("mall_warehouse_h5_driver_tab")) "Warehouse H5 bundle is old: missing driver task tab logic."
Assert-True ($bundleText.Contains("driver-map-mode")) "Warehouse H5 bundle is old: missing driver map mode."
Assert-True ($bundleText.Contains("data-source-chip")) "Warehouse H5 bundle is old: missing data source chip."
Assert-True ($bundleText.Contains("driver-stats-summary")) "Warehouse H5 bundle is old: missing driver stats page."
Assert-True ($bundleText.Contains("warehouse-receive-record-card")) "Warehouse H5 bundle is old: missing receiver receive record page."
Assert-True ($bundleText.Contains("warehouse-pick-record-card")) "Warehouse H5 bundle is old: missing picker pick record page."
Assert-True ($bundleText.Contains("purchase-qty-stepper")) "Warehouse H5 bundle is old: missing buyer purchase quantity editor."
Assert-True ($bundleText.Contains("receipt-gallery")) "Warehouse H5 bundle is old: missing purchase receipt gallery."
Assert-True ($bundleText.Contains("receipt-stamp")) "Warehouse H5 bundle is old: missing loading receipt stamp."
Assert-True ($bundleText.Contains("warehouse-bottom")) "Warehouse H5 bundle is old: missing custom bottom navigation."
Write-Host "Bundle check passed."

Write-Host "Checking unauthenticated WMS access should be rejected."
try {
    Invoke-WebRequest -Uri "$base/api/wms/inbound/orders?pageNum=1&pageSize=1" -UseBasicParsing -TimeoutSec 10 | Out-Null
    throw "Unauthenticated WMS request unexpectedly succeeded."
} catch {
    $status = [int]$_.Exception.Response.StatusCode
    Assert-True (($status -eq 401) -or ($status -eq 403)) "Expected 401/403 for unauthenticated WMS request, got $status."
    Write-Host "Unauthenticated WMS status: $status"
}

Write-Host "Logging in as $Username / $PortalCode"
$loginBody = @{
    username = $Username
    password = $Password
    portalCode = $PortalCode
} | ConvertTo-Json
$login = Invoke-RestMethod -Uri "$base/api/auth/password-login" -Method Post -ContentType "application/json" -Body $loginBody -TimeoutSec 15
Assert-True ([string]$login.code -eq "0") "Login failed: code=$($login.code), message=$($login.message)"
Assert-True ([bool]$login.data.accessToken) "Login response missing accessToken."
$headers = @{ Authorization = "Bearer $($login.data.accessToken)" }
Write-Host "Login passed."

if ($StrictRoleAccounts -and -not $SkipRoleAccounts) {
    Test-RoleAccounts
}

if (-not $SkipRoleAccounts) {
    Test-FallbackAccounts
}

$paths = @(
    "/api/wms/inbound/orders",
    "/api/wms/inbound/putaway-tasks",
    "/api/wms/inventory/stocks",
    "/api/wms/outbound/picks",
    "/api/wms/outbound/deliveries"
)

foreach ($path in $paths) {
    $res = Invoke-RestMethod -Uri "$base$($path)?pageNum=1&pageSize=1" -Headers $headers -TimeoutSec 15
    Assert-True ([string]$res.code -eq "0") "$path failed: code=$($res.code), message=$($res.message)"
    Assert-True ([int64]$res.data.total -gt 0) "$path returned no test data."
    Write-Host "$path total=$($res.data.total)"
}

Write-Host "Warehouse H5 smoke check passed."
