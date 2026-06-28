param(
    [string]$OpenApiPath = "docs/api/openapi/mall-openapi.json",
    [string]$ProjectId = $env:APIFOX_PROJECT_ID,
    [string]$ApiToken = $env:APIFOX_API_TOKEN,
    [switch]$Generate,
    [switch]$SplitByTag,
    [switch]$DeleteUnmatchedResources
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "../..")

if ($Generate) {
    & (Join-Path $PSScriptRoot "generate-openapi.ps1") -OutputPath $OpenApiPath
}

if ([string]::IsNullOrWhiteSpace($ProjectId)) {
    throw "APIFOX_PROJECT_ID is required"
}

if ([string]::IsNullOrWhiteSpace($ApiToken)) {
    throw "APIFOX_API_TOKEN is required"
}

if ($SplitByTag -and $DeleteUnmatchedResources) {
    throw "Do not combine -SplitByTag with -DeleteUnmatchedResources. Full import is required when deleting unmatched resources."
}

$openApiFullPath = Join-Path $repoRoot $OpenApiPath
if (-not (Test-Path -LiteralPath $openApiFullPath)) {
    throw "OpenAPI file not found: $OpenApiPath"
}

$uri = "https://api.apifox.com/v1/projects/$ProjectId/import-openapi?locale=zh-CN"

$curl = Get-Command curl.exe -ErrorAction SilentlyContinue
if (-not $curl) {
    throw "curl.exe is required for Apifox sync"
}

function Invoke-ApifoxImport {
    param(
        [string]$InputText,
        [string]$Name
    )

    $body = @{
        input = $InputText
        options = @{
            targetEndpointFolderId = 0
            targetSchemaFolderId = 0
            endpointOverwriteBehavior = if ($DeleteUnmatchedResources) { "deleteUnmatchedResources" } else { "OVERWRITE_EXISTING" }
            schemaOverwriteBehavior = "OVERWRITE_EXISTING"
            updateFolderOfChangedEndpoint = $true
            prependBasePath = $false
        }
    } | ConvertTo-Json -Depth 100 -Compress

    $tempPayload = Join-Path ([System.IO.Path]::GetTempPath()) ("mall-apifox-import-" + [System.Guid]::NewGuid().ToString("N") + ".json")
    try {
        [System.IO.File]::WriteAllText($tempPayload, $body, (New-Object System.Text.UTF8Encoding($false)))
        Write-Host "Syncing $Name ..."
        & $curl.Source --silent --show-error --location --request POST $uri `
            --header "X-Apifox-Api-Version: 2024-03-28" `
            --header "Authorization: Bearer $ApiToken" `
            --header "Content-Type: application/json" `
            --data-binary "@$tempPayload"
        if ($LASTEXITCODE -ne 0) {
            throw "curl sync failed with exit code $LASTEXITCODE"
        }
    } finally {
        if (Test-Path -LiteralPath $tempPayload) {
            Remove-Item -LiteralPath $tempPayload -Force
        }
    }
}

$openApiText = Get-Content -LiteralPath $openApiFullPath -Raw -Encoding UTF8

if (-not $SplitByTag) {
    Invoke-ApifoxImport -InputText $openApiText -Name "all APIs"
    exit 0
}

$doc = $openApiText | ConvertFrom-Json
$operationsByTag = [ordered]@{}
foreach ($pathProperty in $doc.paths.PSObject.Properties) {
    foreach ($methodProperty in $pathProperty.Value.PSObject.Properties) {
        $method = $methodProperty.Name.ToLowerInvariant()
        if ($method -notin @("get", "post", "put", "delete", "patch")) {
            continue
        }
        $tag = if ($methodProperty.Value.tags -and $methodProperty.Value.tags.Count -gt 0) { [string]$methodProperty.Value.tags[0] } else { "Default" }
        if (-not $operationsByTag.Contains($tag)) {
            $operationsByTag[$tag] = @()
        }
        $operationsByTag[$tag] += [ordered]@{
            path = $pathProperty.Name
            method = $method
            operation = $methodProperty.Value
        }
    }
}

foreach ($tag in $operationsByTag.Keys) {
    $partialPaths = [ordered]@{}
    foreach ($entry in $operationsByTag[$tag]) {
        if (-not $partialPaths.Contains($entry.path)) {
            $partialPaths[$entry.path] = [ordered]@{}
        }
        $partialPaths[$entry.path][$entry.method] = $entry.operation
    }

    $partialDoc = [ordered]@{
        openapi = $doc.openapi
        info = [ordered]@{
            title = "$($doc.info.title) - $tag"
            version = $doc.info.version
            description = $doc.info.description
        }
        servers = $doc.servers
        tags = @([ordered]@{ name = $tag })
        paths = $partialPaths
        components = $doc.components
    }

    $partialJson = $partialDoc | ConvertTo-Json -Depth 100 -Compress
    Invoke-ApifoxImport -InputText $partialJson -Name $tag
}
