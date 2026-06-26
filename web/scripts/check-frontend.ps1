param(
    [string]$Root = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = 'Stop'

$jsonFiles = Get-ChildItem -Path $Root -Recurse -File -Include package.json,tsconfig.json,tsconfig.base.json,pages.json,manifest.json |
    Where-Object { $_.FullName -notmatch '\\node_modules\\|\\dist\\|\\unpackage\\' }
foreach ($file in $jsonFiles) {
    Get-Content -Raw -Encoding UTF8 -LiteralPath $file.FullName | ConvertFrom-Json | Out-Null
    Write-Host "JSON OK $($file.FullName)"
}

$mobileApps = @('user-h5', 'role-workbench-h5', 'warehouse-h5')
foreach ($app in $mobileApps) {
    $appRoot = Join-Path $Root $app
    $required = @(
        'src\App.vue',
        'src\main.ts',
        'src\pages.json',
        'src\manifest.json',
        'src\uni.scss'
    )

    foreach ($path in $required) {
        $fullPath = Join-Path $appRoot $path
        if (-not (Test-Path -LiteralPath $fullPath)) {
            throw "Missing required uni-app file: $fullPath"
        }
        Write-Host "ENTRY OK $fullPath"
    }

    $pagesJson = Get-Content -Raw -Encoding UTF8 -LiteralPath (Join-Path $appRoot 'src\pages.json') | ConvertFrom-Json
    foreach ($page in $pagesJson.pages) {
        $pageFile = Join-Path $appRoot ("src\" + $page.path + ".vue")
        if (-not (Test-Path -LiteralPath $pageFile)) {
            throw "Missing configured page: $pageFile"
        }
        Write-Host "PAGE OK $app $($page.path)"
    }
}

Write-Host 'FRONTEND STRUCTURE CHECK PASSED'
