param(
    [string]$HostUrl = "http://192.168.28.242",
    [string]$Password = "Test@123456",
    [int]$DebugPort = 9333,
    [switch]$SkipBrowser,
    [switch]$SkipStrictRoleAccounts
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Invoke-Step {
    param([string]$Name, [scriptblock]$Block)
    Write-Host ""
    Write-Host "==> $Name"
    & $Block
}

function Invoke-CheckedPowerShell {
    param([string[]]$Arguments, [string]$FailureMessage)
    & powershell @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$FailureMessage (exit code $LASTEXITCODE)"
    }
}

$hostBase = $HostUrl.TrimEnd("/")
$browserUrl = "$hostBase/warehouse/"
$apiScript = Join-Path $PSScriptRoot "verify-warehouse-h5.ps1"
$browserScript = Join-Path $PSScriptRoot "verify-warehouse-h5-browser.ps1"

if (-not (Test-Path $apiScript)) {
    throw "API smoke script not found: $apiScript"
}
if (-not $SkipBrowser -and -not (Test-Path $browserScript)) {
    throw "Browser smoke script not found: $browserScript"
}

Invoke-Step "API and bundle verification" {
    $args = @(
        "-NoProfile",
        "-ExecutionPolicy", "Bypass",
        "-File", $apiScript,
        "-HostUrl", $hostBase,
        "-Password", $Password
    )
    if (-not $SkipStrictRoleAccounts) {
        $args += "-StrictRoleAccounts"
    }
    Invoke-CheckedPowerShell -Arguments $args -FailureMessage "API and bundle verification failed"
}

if (-not $SkipBrowser) {
    Invoke-Step "Chrome browser verification" {
        Invoke-CheckedPowerShell `
            -Arguments @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $browserScript, "-Url", $browserUrl, "-DebugPort", "$DebugPort") `
            -FailureMessage "Chrome browser verification failed"
    }
}

Write-Host ""
Write-Host "Warehouse H5 full verification passed."
