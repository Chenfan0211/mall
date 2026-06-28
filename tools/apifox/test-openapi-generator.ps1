param(
    [string]$OpenApiPath = "docs/api/openapi/mall-openapi.json"
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "../..")
$openApiFullPath = Join-Path $repoRoot $OpenApiPath

if (-not (Test-Path -LiteralPath $openApiFullPath)) {
    throw "OpenAPI file not found: $OpenApiPath"
}

$jsonText = Get-Content -LiteralPath $openApiFullPath -Raw -Encoding UTF8
$doc = $jsonText | ConvertFrom-Json

if ($doc.openapi -ne "3.0.3") {
    throw "Expected OpenAPI 3.0.3, got: $($doc.openapi)"
}

if (-not $doc.paths) {
    throw "OpenAPI paths is empty"
}

$operationCount = 0
$missingFailureExamples = @()
$missingSuccessExamples = @()
$missingChineseText = @()
$requiredCheckFailures = @()

foreach ($pathProperty in $doc.paths.PSObject.Properties) {
    foreach ($methodProperty in $pathProperty.Value.PSObject.Properties) {
        $method = $methodProperty.Name.ToLowerInvariant()
        if ($method -notin @("get", "post", "put", "delete", "patch")) {
            continue
        }

        $operationCount++
        if ($methodProperty.Value.summary -notmatch "[\u4e00-\u9fa5]") {
            $missingChineseText += "$($method.ToUpperInvariant()) $($pathProperty.Name) summary"
        }

        if ($methodProperty.Value.description -notmatch "[\u4e00-\u9fa5]") {
            $missingChineseText += "$($method.ToUpperInvariant()) $($pathProperty.Name) description"
        }

        foreach ($parameter in @($methodProperty.Value.parameters)) {
            if ($parameter.description -notmatch "[\u4e00-\u9fa5]") {
                $missingChineseText += "$($method.ToUpperInvariant()) $($pathProperty.Name) parameter $($parameter.name)"
            }
        }

        $requiredParameterChecks = @(
            @{ path = "/api/user/orders/{id}"; method = "get"; name = "id"; required = $true },
            @{ path = "/api/user/orders/{id}"; method = "get"; name = "userId"; required = $true },
            @{ path = "/api/user/orders/{id}/tracks"; method = "get"; name = "id"; required = $true },
            @{ path = "/api/user/orders/{id}/tracks"; method = "get"; name = "userId"; required = $true },
            @{ path = "/api/system/menus"; method = "get"; name = "portalCode"; required = $false }
        )

        foreach ($check in $requiredParameterChecks) {
            if ($pathProperty.Name -ne $check.path -or $method -ne $check.method) {
                continue
            }

            $parameter = @($methodProperty.Value.parameters) | Where-Object { $_.name -eq $check.name } | Select-Object -First 1
            if (-not $parameter) {
                $requiredCheckFailures += "$($method.ToUpperInvariant()) $($pathProperty.Name) missing parameter $($check.name)"
                continue
            }

            if ([bool]$parameter.required -ne [bool]$check.required) {
                $requiredCheckFailures += "$($method.ToUpperInvariant()) $($pathProperty.Name) parameter $($check.name) required should be $($check.required)"
            }
        }

        if ($pathProperty.Name -eq "/api/station/workbench/order-items/{id}/pickup-confirm" -and $method -eq "post") {
            if (-not $methodProperty.Value.requestBody) {
                $requiredCheckFailures += "POST /api/station/workbench/order-items/{id}/pickup-confirm missing optional requestBody"
            } elseif ([bool]$methodProperty.Value.requestBody.required -ne $false) {
                $requiredCheckFailures += "POST /api/station/workbench/order-items/{id}/pickup-confirm requestBody should be optional"
            }
        }

        if ($pathProperty.Name -eq "/api/station/workbench/delivery-stations/{id}/arrival-confirm" -and $method -eq "post") {
            if (-not $methodProperty.Value.requestBody) {
                $requiredCheckFailures += "POST /api/station/workbench/delivery-stations/{id}/arrival-confirm missing optional requestBody"
            } elseif ([bool]$methodProperty.Value.requestBody.required -ne $false) {
                $requiredCheckFailures += "POST /api/station/workbench/delivery-stations/{id}/arrival-confirm requestBody should be optional"
            }
        }

        $responses = $methodProperty.Value.responses
        if (-not $responses."200".content."application/json".examples.success) {
            $missingSuccessExamples += "$($method.ToUpperInvariant()) $($pathProperty.Name)"
        }

        foreach ($code in @("400", "401", "403", "500")) {
            if (-not $responses.$code.content."application/json".examples.failure) {
                $missingFailureExamples += "$($method.ToUpperInvariant()) $($pathProperty.Name) missing $code"
            }
        }
    }
}

if ($operationCount -lt 150) {
    throw "Expected at least 150 API operations, got: $operationCount"
}

if ($missingSuccessExamples.Count -gt 0) {
    throw "Missing success examples: $($missingSuccessExamples -join '; ')"
}

if ($missingFailureExamples.Count -gt 0) {
    throw "Missing failure examples: $($missingFailureExamples -join '; ')"
}

if ($missingChineseText.Count -gt 0) {
    $sample = $missingChineseText | Select-Object -First 20
    throw "Missing Chinese text count $($missingChineseText.Count): $($sample -join '; ')"
}

if ($requiredCheckFailures.Count -gt 0) {
    throw "Required parameter check failed: $($requiredCheckFailures -join '; ')"
}

Write-Host "OpenAPI verification passed. Operation count: $operationCount"
