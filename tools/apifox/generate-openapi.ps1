param(
    [string]$OutputPath = "docs/api/openapi/mall-openapi.json"
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "../..")
$apiRoot = Join-Path $repoRoot "mall-api/src/main/java"
$controllerFiles = Get-ChildItem -LiteralPath $repoRoot -Recurse -Filter "*Controller.java" |
    Where-Object { $_.FullName -match "\\mall-[^\\]+\\src\\main\\java\\com\\mall\\[^\\]+\\controller\\" }

function T {
    param([string]$Text)

    return [regex]::Replace($Text, "\\u([0-9a-fA-F]{4})", {
        param($Match)
        return [string][char][Convert]::ToInt32($Match.Groups[1].Value, 16)
    })
}

$moduleNames = @{
    "auth" = T "\u8ba4\u8bc1\u4e2d\u5fc3"
    "system" = T "\u7cfb\u7edf\u6743\u9650"
    "operation" = T "\u8fd0\u8425\u540e\u53f0"
    "product" = T "\u5546\u54c1\u4e2d\u5fc3"
    "sale" = T "\u56e2\u671f\u53d1\u5e03"
    "trade" = T "\u8ba2\u5355\u4ea4\u6613"
    "payment" = T "\u652f\u4ed8\u9000\u6b3e"
    "aftersale" = T "\u552e\u540e\u4e2d\u5fc3"
    "supplier" = T "\u4f9b\u5e94\u5546\u91c7\u8d2d"
    "finance" = T "\u8d22\u52a1\u7ed3\u7b97"
    "wms" = T "\u4ed3\u50a8\u5c65\u7ea6"
    "station" = T "\u89d2\u8272\u5de5\u4f5c\u53f0"
    "user" = T "\u7528\u6237\u7aef"
}

$controllerNames = @{
    "Auth" = T "\u767b\u5f55\u8ba4\u8bc1"
    "Account" = T "\u8d26\u53f7\u7ba1\u7406"
    "Role" = T "\u89d2\u8272\u7ba1\u7406"
    "Menu" = T "\u83dc\u5355\u7ba1\u7406"
    "Dict" = T "\u5b57\u5178\u7ba1\u7406"
    "OperationDashboard" = T "\u8fd0\u8425\u770b\u677f"
    "Todo" = T "\u5f85\u529e\u4efb\u52a1"
    "ExceptionRecord" = T "\u5f02\u5e38\u8bb0\u5f55"
    "Product" = T "\u5546\u54c1"
    "Sku" = T "SKU"
    "Category" = T "\u5546\u54c1\u7c7b\u76ee"
    "PublishPeriod" = T "\u56e2\u671f"
    "PublishSku" = T "\u53d1\u5e03SKU"
    "Order" = T "\u8ba2\u5355"
    "OrderItem" = T "\u8ba2\u5355\u5546\u54c1"
    "StockoutRecord" = T "\u7f3a\u8d27\u8bb0\u5f55"
    "FulfillmentTrack" = T "\u5c65\u7ea6\u8f68\u8ff9"
    "PaymentTrade" = T "\u652f\u4ed8\u5355"
    "RefundTrade" = T "\u9000\u6b3e\u5355"
    "FundFlow" = T "\u8d44\u91d1\u6d41\u6c34"
    "AfterSale" = T "\u552e\u540e\u5355"
    "AfterSaleItem" = T "\u552e\u540e\u5546\u54c1"
    "AfterSaleReturnRecord" = T "\u9000\u8d27\u8bb0\u5f55"
    "SupplierWorkbench" = T "\u4f9b\u5e94\u5546\u5de5\u4f5c\u53f0"
    "SupplierProfile" = T "\u4f9b\u5e94\u5546\u6863\u6848"
    "Purchase" = T "\u91c7\u8d2d\u5355"
    "Delivery" = T "\u9001\u8d27\u5355"
    "Commission" = T "\u4f63\u91d1"
    "FinanceAccount" = T "\u8d44\u91d1\u8d26\u6237"
    "Settlement" = T "\u7ed3\u7b97\u5bf9\u8d26"
    "Withdraw" = T "\u63d0\u73b0"
    "WmsBase" = T "\u4ed3\u5e93\u57fa\u7840\u8d44\u6599"
    "WmsInbound" = T "\u5165\u5e93\u4f5c\u4e1a"
    "WmsInventory" = T "\u5e93\u5b58\u7ba1\u7406"
    "WmsOutbound" = T "\u51fa\u5e93\u914d\u9001"
    "StationBase" = T "\u81ea\u63d0\u70b9\u57fa\u7840\u8d44\u6599"
    "StationFinance" = T "\u81ea\u63d0\u70b9\u8d44\u91d1"
    "StationOrder" = T "\u81ea\u63d0\u70b9\u5c65\u7ea6"
    "UserHome" = T "\u7528\u6237\u9996\u9875"
    "UserProduct" = T "\u7528\u6237\u5546\u54c1"
    "UserCart" = T "\u8d2d\u7269\u8f66"
    "UserOrder" = T "\u7528\u6237\u8ba2\u5355"
    "UserAfterSale" = T "\u7528\u6237\u552e\u540e"
    "UserMessage" = T "\u7528\u6237\u6d88\u606f"
    "UserStation" = T "\u7528\u6237\u81ea\u63d0\u70b9"
}

$fieldNames = @{
    "id" = T "ID"
    "keyword" = T "\u5173\u952e\u5b57"
    "pageNum" = T "\u9875\u7801"
    "pageSize" = T "\u6bcf\u9875\u6570\u91cf"
    "userId" = T "\u7528\u6237ID"
    "username" = T "\u767b\u5f55\u8d26\u53f7"
    "password" = T "\u767b\u5f55\u5bc6\u7801"
    "mobile" = T "\u624b\u673a\u53f7"
    "accountId" = T "\u8d26\u53f7ID"
    "accountNo" = T "\u8d26\u53f7\u7f16\u53f7"
    "accountType" = T "\u8d26\u53f7\u7c7b\u578b"
    "roleCode" = T "\u89d2\u8272\u7f16\u7801"
    "roleName" = T "\u89d2\u8272\u540d\u79f0"
    "cityId" = T "\u57ce\u5e02ID"
    "regionId" = T "\u533a\u57dfID"
    "stationId" = T "\u81ea\u63d0\u70b9ID"
    "stationNo" = T "\u81ea\u63d0\u70b9\u7f16\u53f7"
    "stationName" = T "\u81ea\u63d0\u70b9\u540d\u79f0"
    "leaderId" = T "\u56e2\u957fID"
    "leaderNo" = T "\u56e2\u957f\u7f16\u53f7"
    "leaderName" = T "\u56e2\u957f\u540d\u79f0"
    "leaderMobile" = T "\u56e2\u957f\u624b\u673a\u53f7"
    "supplierId" = T "\u4f9b\u5e94\u5546ID"
    "supplierNo" = T "\u4f9b\u5e94\u5546\u7f16\u53f7"
    "supplierName" = T "\u4f9b\u5e94\u5546\u540d\u79f0"
    "warehouseId" = T "\u4ed3\u5e93ID"
    "warehouseCode" = T "\u4ed3\u5e93\u7f16\u7801"
    "warehouseName" = T "\u4ed3\u5e93\u540d\u79f0"
    "warehouseType" = T "\u4ed3\u5e93\u7c7b\u578b"
    "orderId" = T "\u8ba2\u5355ID"
    "orderNo" = T "\u8ba2\u5355\u7f16\u53f7"
    "orderItemId" = T "\u8ba2\u5355\u5546\u54c1ID"
    "orderItemNo" = T "\u8ba2\u5355\u5546\u54c1\u7f16\u53f7"
    "skuId" = T "SKU ID"
    "skuNo" = T "SKU\u7f16\u53f7"
    "skuName" = T "SKU\u540d\u79f0"
    "productId" = T "\u5546\u54c1ID"
    "productNo" = T "\u5546\u54c1\u7f16\u53f7"
    "productName" = T "\u5546\u54c1\u540d\u79f0"
    "categoryId" = T "\u7c7b\u76eeID"
    "categoryCode" = T "\u7c7b\u76ee\u7f16\u7801"
    "categoryName" = T "\u7c7b\u76ee\u540d\u79f0"
    "periodId" = T "\u56e2\u671fID"
    "periodNo" = T "\u56e2\u671f\u7f16\u53f7"
    "periodName" = T "\u56e2\u671f\u540d\u79f0"
    "publishSkuId" = T "\u53d1\u5e03SKU ID"
    "status" = T "\u72b6\u6001"
    "tradeStatus" = T "\u4ea4\u6613\u72b6\u6001"
    "payStatus" = T "\u652f\u4ed8\u72b6\u6001"
    "refundStatus" = T "\u9000\u6b3e\u72b6\u6001"
    "fulfillStatus" = T "\u5c65\u7ea6\u72b6\u6001"
    "auditStatus" = T "\u5ba1\u6838\u72b6\u6001"
    "saleStatus" = T "\u9500\u552e\u72b6\u6001"
    "startTime" = T "\u5f00\u59cb\u65f6\u95f4"
    "endTime" = T "\u7ed3\u675f\u65f6\u95f4"
    "createTime" = T "\u521b\u5efa\u65f6\u95f4"
    "updateTime" = T "\u66f4\u65b0\u65f6\u95f4"
    "deliveryDate" = T "\u914d\u9001\u65e5\u671f"
    "deliveryId" = T "\u914d\u9001\u5355ID"
    "deliveryNo" = T "\u914d\u9001\u5355\u7f16\u53f7"
    "lineId" = T "\u7ebf\u8defID"
    "lineName" = T "\u7ebf\u8def\u540d\u79f0"
    "zoneId" = T "\u5e93\u533aID"
    "zoneName" = T "\u5e93\u533a\u540d\u79f0"
    "locationId" = T "\u5e93\u4f4dID"
    "locationName" = T "\u5e93\u4f4d\u540d\u79f0"
    "inboundId" = T "\u5165\u5e93\u5355ID"
    "inboundNo" = T "\u5165\u5e93\u5355\u7f16\u53f7"
    "outboundId" = T "\u51fa\u5e93\u5355ID"
    "outboundNo" = T "\u51fa\u5e93\u5355\u7f16\u53f7"
    "pickId" = T "\u62e3\u8d27\u4efb\u52a1ID"
    "pickNo" = T "\u62e3\u8d27\u4efb\u52a1\u7f16\u53f7"
    "waveId" = T "\u6ce2\u6b21ID"
    "waveNo" = T "\u6ce2\u6b21\u7f16\u53f7"
    "loadingId" = T "\u88c5\u8f66\u5355ID"
    "loadingNo" = T "\u88c5\u8f66\u5355\u7f16\u53f7"
    "afterSaleId" = T "\u552e\u540e\u5355ID"
    "afterSaleNo" = T "\u552e\u540e\u5355\u7f16\u53f7"
    "afterSaleType" = T "\u552e\u540e\u7c7b\u578b"
    "afterSaleStatus" = T "\u552e\u540e\u72b6\u6001"
    "returnStatus" = T "\u9000\u8d27\u72b6\u6001"
    "responsibilityType" = T "\u8d23\u4efb\u7c7b\u578b"
    "qty" = T "\u6570\u91cf"
    "applyQty" = T "\u7533\u8bf7\u6570\u91cf"
    "approvedQty" = T "\u5ba1\u6838\u901a\u8fc7\u6570\u91cf"
    "purchaseQty" = T "\u91c7\u8d2d\u6570\u91cf"
    "deliveryQty" = T "\u914d\u9001\u6570\u91cf"
    "receiveQty" = T "\u6536\u8d27\u6570\u91cf"
    "receivedQty" = T "\u5df2\u6536\u8d27\u6570\u91cf"
    "outboundQty" = T "\u51fa\u5e93\u6570\u91cf"
    "inStockQty" = T "\u5728\u5e93\u6570\u91cf"
    "availableQty" = T "\u53ef\u7528\u6570\u91cf"
    "lockedQty" = T "\u9501\u5b9a\u6570\u91cf"
    "amount" = T "\u91d1\u989d"
    "totalAmount" = T "\u603b\u91d1\u989d"
    "payAmount" = T "\u652f\u4ed8\u91d1\u989d"
    "refundAmount" = T "\u9000\u6b3e\u91d1\u989d"
    "commissionAmount" = T "\u4f63\u91d1\u91d1\u989d"
    "withdrawAmount" = T "\u63d0\u73b0\u91d1\u989d"
    "salePrice" = T "\u9500\u552e\u4ef7"
    "purchasePrice" = T "\u91c7\u8d2d\u4ef7"
    "reason" = T "\u539f\u56e0"
    "applyReason" = T "\u7533\u8bf7\u539f\u56e0"
    "cancelReason" = T "\u53d6\u6d88\u539f\u56e0"
    "auditReason" = T "\u5ba1\u6838\u539f\u56e0"
    "remark" = T "\u5907\u6ce8"
    "title" = T "\u6807\u9898"
    "content" = T "\u5185\u5bb9"
    "description" = T "\u8bf4\u660e"
}

$javaTypeIndex = @{}
Get-ChildItem -LiteralPath $apiRoot -Recurse -Filter "*.java" | ForEach-Object {
    $relative = $_.FullName.Substring($apiRoot.Length + 1).Replace("\", ".").Replace(".java", "")
    $javaTypeIndex[$relative] = $_.FullName
    $simpleName = [System.IO.Path]::GetFileNameWithoutExtension($_.FullName)
    if (-not $javaTypeIndex.ContainsKey($simpleName)) {
        $javaTypeIndex[$simpleName] = $_.FullName
    }
}

function Normalize-JavaSource {
    param([string]$Source)

    $source = $Source -replace "(?s)/\*.*?\*/", ""
    $source = $source -replace "(?m)//.*$", ""
    return $source
}

function Get-AnnotationPath {
    param(
        [string]$Annotation,
        [string]$DefaultPath = ""
    )

    if ($Annotation -match '"([^"]*)"') {
        return $matches[1]
    }

    return $DefaultPath
}

function Join-ApiPath {
    param(
        [string]$BasePath,
        [string]$SubPath
    )

    $path = (($BasePath.TrimEnd("/") + "/" + $SubPath.TrimStart("/")).TrimEnd("/"))
    if ([string]::IsNullOrWhiteSpace($path)) {
        return "/"
    }
    if (-not $path.StartsWith("/")) {
        $path = "/" + $path
    }
    return $path
}

function Get-ControllerDisplayName {
    param([string]$ControllerKey)

    if ($controllerNames.ContainsKey($ControllerKey)) {
        return $controllerNames[$ControllerKey]
    }

    return (T "\u63a5\u53e3\u8d44\u6e90") + " " + $ControllerKey
}

function Get-FieldDisplayName {
    param([string]$FieldName)

    if ($fieldNames.ContainsKey($FieldName)) {
        return $fieldNames[$FieldName]
    }

    $suffixNames = [ordered]@{
        "Id" = T "ID"
        "No" = T "\u7f16\u53f7"
        "Code" = T "\u7f16\u7801"
        "Name" = T "\u540d\u79f0"
        "Type" = T "\u7c7b\u578b"
        "Status" = T "\u72b6\u6001"
        "Flag" = T "\u6807\u8bb0"
        "Qty" = T "\u6570\u91cf"
        "Count" = T "\u6570\u91cf"
        "Amount" = T "\u91d1\u989d"
        "Price" = T "\u4ef7\u683c"
        "Time" = T "\u65f6\u95f4"
        "Date" = T "\u65e5\u671f"
        "Reason" = T "\u539f\u56e0"
        "Remark" = T "\u5907\u6ce8"
        "Mobile" = T "\u624b\u673a\u53f7"
        "Json" = T "JSON\u6570\u636e"
        "Url" = T "\u94fe\u63a5"
    }

    foreach ($suffix in $suffixNames.Keys) {
        if ($FieldName.EndsWith($suffix) -and $FieldName.Length -gt $suffix.Length) {
            $prefix = $FieldName.Substring(0, $FieldName.Length - $suffix.Length)
            return $prefix + $suffixNames[$suffix]
        }
    }

    return (T "\u5b57\u6bb5") + " " + $FieldName
}

function Get-FieldDescription {
    param(
        [string]$FieldName,
        [string]$Prefix
    )

    if ([string]::IsNullOrWhiteSpace($Prefix)) {
        $Prefix = T "\u8bf7\u6c42\u5b57\u6bb5\uff1a"
    }

    return $Prefix + (Get-FieldDisplayName $FieldName)
}

function Get-OperationSummary {
    param(
        [string]$MethodName,
        [string]$ResourceName
    )

    $exactActions = @{
        "passwordLogin" = T "\u8d26\u53f7\u5bc6\u7801\u767b\u5f55"
        "markMessageRead" = T "\u6807\u8bb0\u6d88\u606f\u5df2\u8bfb"
        "confirmPickup" = T "\u63d0\u8d27\u6838\u9500"
        "confirmArrival" = T "\u5230\u8d27\u786e\u8ba4"
        "createPaymentTrade" = T "\u521b\u5efa\u652f\u4ed8\u5355"
        "submitOrder" = T "\u63d0\u4ea4\u8ba2\u5355"
        "addCartItem" = T "\u52a0\u5165\u8d2d\u7269\u8f66"
        "removeCartItem" = T "\u79fb\u9664\u8d2d\u7269\u8f66"
        "updateCartItem" = T "\u66f4\u65b0\u8d2d\u7269\u8f66"
        "applyAfterSale" = T "\u7533\u8bf7\u552e\u540e"
        "applyLeave" = T "\u7533\u8bf7\u8bf7\u5047"
        "applyWithdraw" = T "\u7533\u8bf7\u63d0\u73b0"
        "cancelWaitPayOrder" = T "\u53d6\u6d88\u5f85\u652f\u4ed8\u8ba2\u5355"
        "getSummary" = T "\u67e5\u8be2\u6c47\u603b"
        "getHome" = T "\u67e5\u8be2\u9996\u9875\u6570\u636e"
        "getWorkbench" = T "\u67e5\u8be2\u5de5\u4f5c\u53f0\u6570\u636e"
    }

    if ($exactActions.ContainsKey($MethodName)) {
        return $exactActions[$MethodName]
    }

    switch -Regex ($MethodName) {
        "^page" { return (T "\u5206\u9875\u67e5\u8be2") + $ResourceName }
        "^list" { return (T "\u5217\u8868\u67e5\u8be2") + $ResourceName }
        "^get" { return (T "\u67e5\u770b") + $ResourceName + (T "\u8be6\u60c5") }
        "^(create|add|submit|apply)" { return (T "\u63d0\u4ea4") + $ResourceName }
        "^update" { return (T "\u66f4\u65b0") + $ResourceName }
        "^(delete|remove)" { return (T "\u5220\u9664") + $ResourceName }
        "^cancel" { return (T "\u53d6\u6d88") + $ResourceName }
        default { return (T "\u5904\u7406") + $ResourceName }
    }
}

function Get-OperationDescription {
    param(
        [string]$Summary,
        [string]$ResourceName
    )

    return (T "\u7528\u4e8e") + $Summary + (T "\uff0c\u4e1a\u52a1\u5bf9\u8c61\u4e3a") + $ResourceName + (T "\u3002\u8bf7\u6c42\u540e\u7aef\u63a5\u53e3\u540e\u8fd4\u56de\u7edf\u4e00 Result \u54cd\u5e94\uff0ccode=0 \u8868\u793a\u6210\u529f\u3002")
}

function Get-JavaFields {
    param([string]$TypeName)

    $cleanName = ($TypeName -replace "^.*\.", "") -replace "<.*$", ""
    if (-not $javaTypeIndex.ContainsKey($cleanName)) {
        return @()
    }

    $source = Normalize-JavaSource (Get-Content -LiteralPath $javaTypeIndex[$cleanName] -Raw -Encoding UTF8)
    $fields = @()
    $pendingRequired = $false

    foreach ($line in ($source -split "`r?`n")) {
        $trimmed = $line.Trim()
        if ($trimmed -match "@(NotNull|NotBlank|NotEmpty)") {
            $pendingRequired = $true
            continue
        }

        if ($trimmed -match "private\s+(?!static)(?:final\s+)?(.+?)\s+([A-Za-z][A-Za-z0-9_]*)\s*(?:=.*)?;") {
            $field = [ordered]@{
                type = $matches[1].Trim()
                name = $matches[2].Trim()
                required = $pendingRequired
            }
            $fields += $field
            $pendingRequired = $false
        }
    }

    return $fields
}

function Resolve-JavaType {
    param([string]$TypeName)

    if ([string]::IsNullOrWhiteSpace($TypeName)) {
        return [ordered]@{ type = "object" }
    }

    $type = ($TypeName.Trim() -replace "^final\s+", "") -replace "\?$", ""
    if ($type -match "^Result<(.+)>$") {
        return New-ResultSchema $matches[1]
    }
    if ($type -match "^PageResult<(.+)>$") {
        return New-PageSchema $matches[1]
    }
    if ($type -match "^(List|ArrayList)<(.+)>$") {
        return [ordered]@{
            type = "array"
            items = Resolve-JavaType $matches[2]
        }
    }

    switch -Regex ($type) {
        "^(Long|Integer|Short|Byte|long|int|short|byte)$" { return [ordered]@{ type = "integer"; format = "int64"; example = 1 } }
        "^(BigDecimal|Double|Float|double|float)$" { return [ordered]@{ type = "string"; example = "19.90" } }
        "^(Boolean|boolean)$" { return [ordered]@{ type = "boolean"; example = $true } }
        "^(LocalDateTime|Date|Instant)$" { return [ordered]@{ type = "string"; format = "date-time"; example = "2026-06-26 10:00:00" } }
        "^(LocalDate)$" { return [ordered]@{ type = "string"; format = "date"; example = "2026-06-26" } }
        "^(String)$" { return [ordered]@{ type = "string"; example = (T "\u793a\u4f8b\u6587\u672c") } }
    }

    $cleanName = ($type -replace "^.*\.", "") -replace "<.*$", ""
    if ($javaTypeIndex.ContainsKey($cleanName)) {
        return [ordered]@{ '$ref' = "#/components/schemas/$cleanName" }
    }

    return [ordered]@{ type = "object" }
}

function New-ResultSchema {
    param([string]$DataType)

    return [ordered]@{
        type = "object"
        properties = [ordered]@{
            code = [ordered]@{ type = "string"; description = (T "\u8fd4\u56de\u7801\uff0c0 \u8868\u793a\u6210\u529f"); example = "0" }
            message = [ordered]@{ type = "string"; description = (T "\u8fd4\u56de\u6d88\u606f"); example = (T "\u6210\u529f") }
            data = Resolve-JavaType $DataType
            traceId = [ordered]@{ type = "string"; description = (T "\u94fe\u8def\u8ffd\u8e2aID"); example = "trace-demo-001" }
        }
    }
}

function New-PageSchema {
    param([string]$ItemType)

    return [ordered]@{
        type = "object"
        properties = [ordered]@{
            total = [ordered]@{ type = "integer"; format = "int64"; description = (T "\u603b\u8bb0\u5f55\u6570"); example = 1 }
            list = [ordered]@{
                type = "array"
                description = (T "\u5f53\u524d\u9875\u6570\u636e")
                items = Resolve-JavaType $ItemType
            }
        }
    }
}

function New-ExampleValue {
    param(
        [object]$Schema,
        [int]$Depth = 0
    )

    if ($null -eq $Schema) {
        return $null
    }

    if ($Schema.Contains('$ref')) {
        $name = ($Schema['$ref'] -split "/")[-1]
        if ($Depth -ge 2) {
            return [ordered]@{ id = 1 }
        }
        return New-ExampleFromType $name ($Depth + 1)
    }

    if ($Schema.type -eq "array") {
        return @(New-ExampleValue $Schema.items ($Depth + 1))
    }

    if ($Schema.type -eq "object") {
        $result = [ordered]@{}
        if ($Schema.properties) {
            foreach ($property in $Schema.properties.GetEnumerator()) {
                $result[$property.Key] = New-ExampleValue $property.Value ($Depth + 1)
            }
        }
        return $result
    }

    if ($null -ne $Schema.example) {
        return $Schema.example
    }

    switch ($Schema.type) {
        "integer" { return 1 }
        "number" { return 1 }
        "boolean" { return $true }
        default { return (T "\u793a\u4f8b\u6587\u672c") }
    }
}

function New-ExampleFromType {
    param(
        [string]$TypeName,
        [int]$Depth = 0
    )

    $fields = Get-JavaFields $TypeName
    if ($fields.Count -eq 0) {
        return [ordered]@{}
    }

    $result = [ordered]@{}
    foreach ($field in $fields) {
        $result[$field.name] = New-ExampleValue (Resolve-JavaType $field.type) $Depth
    }
    return $result
}

function New-FailureExample {
    param(
        [string]$Code,
        [string]$Message
    )

    return [ordered]@{
        code = $Code
        message = $Message
        data = $null
        traceId = "trace-demo-001"
    }
}

function New-ApiResponse {
    param(
        [string]$Description,
        [object]$Schema,
        [hashtable]$Examples
    )

    return [ordered]@{
        description = $Description
        content = [ordered]@{
            "application/json" = [ordered]@{
                schema = $Schema
                examples = $Examples
            }
        }
    }
}

function Convert-JavaMethodToOperation {
    param(
        [string]$Module,
        [string]$ControllerName,
        [string]$ClassPath,
        [string]$Annotation,
        [string]$ReturnType,
        [string]$MethodName,
        [string]$Parameters
    )

    $httpMethod = "get"
    if ($Annotation -match "@PostMapping") { $httpMethod = "post" }
    elseif ($Annotation -match "@PutMapping") { $httpMethod = "put" }
    elseif ($Annotation -match "@DeleteMapping") { $httpMethod = "delete" }
    elseif ($Annotation -match "@PatchMapping") { $httpMethod = "patch" }

    $subPath = Get-AnnotationPath $Annotation ""
    $path = Join-ApiPath $ClassPath $subPath
    $path = $path -replace "\{(\w+)\}", '{$1}'
    $tagPrefix = if ($moduleNames.ContainsKey($Module)) { $moduleNames[$Module] } else { $Module }
    $controllerKey = $ControllerName -replace 'Controller$', ''
    $resourceName = Get-ControllerDisplayName $controllerKey
    $tag = "$tagPrefix / $resourceName"
    $summary = Get-OperationSummary $MethodName $resourceName

    $operation = [ordered]@{
        tags = @($tag)
        summary = $summary
        description = Get-OperationDescription $summary $resourceName
        operationId = "$($Module)_$($ControllerName -replace 'Controller$', '')_$MethodName"
        parameters = @()
        responses = [ordered]@{}
    }

    if ($path -notlike "/api/auth/*") {
        $operation.security = @(@{ bearerAuth = @() })
    }

    $requestBodyType = $null
    $requestBodyRequired = $true
    $parameterParts = @()
    if (-not [string]::IsNullOrWhiteSpace($Parameters)) {
        $parameterParts = [regex]::Split($Parameters, ",(?=(?:[^<>]*<[^<>]*>)*[^<>]*$)")
    }

    foreach ($rawParameter in $parameterParts) {
        $parameter = $rawParameter.Trim() -replace "\s+", " "
        if ([string]::IsNullOrWhiteSpace($parameter)) {
            continue
        }

        $parameter = $parameter -replace "@Valid\s+", ""
        $parameter = $parameter -replace "@Validated\s+", ""
        $parameter = $parameter -replace "final\s+", ""

        if ($parameter -match "@RequestBody(?:\(([^)]*)\))?\s+([A-Za-z0-9_<>, ?\.]+)\s+([A-Za-z][A-Za-z0-9_]*)") {
            $annotationArgs = $matches[1]
            $bodyType = $matches[2].Trim()
            $requestBodyRequired = $true
            if ($annotationArgs -match "required\s*=\s*false") {
                $requestBodyRequired = $false
            }
            $requestBodyType = $bodyType
            continue
        }

        if ($parameter -match "@PathVariable(?:\([^)]*\))?\s+([A-Za-z0-9_<>, ?\.]+)\s+([A-Za-z][A-Za-z0-9_]*)") {
            $operation.parameters += [ordered]@{
                name = $matches[2]
                "in" = "path"
                required = $true
                schema = Resolve-JavaType $matches[1]
                description = Get-FieldDescription $matches[2] (T "\u8def\u5f84\u53c2\u6570\uff1a")
            }
            continue
        }

        if ($parameter -match "@RequestParam(?:\(([^)]*)\))?\s+([A-Za-z0-9_<>, ?\.]+)\s+([A-Za-z][A-Za-z0-9_]*)") {
            $annotationArgs = $matches[1]
            $paramType = $matches[2].Trim()
            $paramName = $matches[3].Trim()
            $requestParamRequired = $true
            if ($annotationArgs -match "required\s*=\s*false") {
                $requestParamRequired = $false
            }
            $operation.parameters += [ordered]@{
                name = $paramName
                "in" = "query"
                required = $requestParamRequired
                schema = Resolve-JavaType $paramType
                description = Get-FieldDescription $paramName (T "\u67e5\u8be2\u53c2\u6570\uff1a")
            }
            continue
        }

        if ($parameter -match "^([A-Za-z0-9_<>, ?\.]+)\s+([A-Za-z][A-Za-z0-9_]*)$") {
            $typeName = $matches[1].Trim()
            $simpleType = ($typeName -replace "^.*\.", "") -replace "<.*$", ""
            if ($javaTypeIndex.ContainsKey($simpleType)) {
                foreach ($field in (Get-JavaFields $simpleType)) {
                    $operation.parameters += [ordered]@{
                        name = $field.name
                        "in" = "query"
                        required = [bool]$field.required
                        schema = Resolve-JavaType $field.type
                        description = Get-FieldDescription $field.name (T "\u67e5\u8be2\u53c2\u6570\uff1a")
                    }
                }
            } else {
                $operation.parameters += [ordered]@{
                    name = $matches[2]
                    "in" = "query"
                    required = $false
                    schema = Resolve-JavaType $typeName
                    description = Get-FieldDescription $matches[2] (T "\u67e5\u8be2\u53c2\u6570\uff1a")
                }
            }
        }
    }

    if ($requestBodyType) {
        $operation.requestBody = [ordered]@{
            required = $requestBodyRequired
            content = [ordered]@{
                "application/json" = [ordered]@{
                    schema = Resolve-JavaType $requestBodyType
                    examples = [ordered]@{
                        request = [ordered]@{
                            summary = T "\u8bf7\u6c42\u793a\u4f8b"
                            value = New-ExampleFromType (($requestBodyType -replace "^.*\.", "") -replace "<.*$", "")
                        }
                    }
                }
            }
        }
    }

    $responseSchema = Resolve-JavaType $ReturnType
    $successValue = New-ExampleValue $responseSchema
    if ($successValue -is [System.Collections.IDictionary]) {
        $successValue["code"] = "0"
        $successValue["message"] = T "\u6210\u529f"
    }

    $operation.responses["200"] = New-ApiResponse (T "\u6210\u529f") $responseSchema ([ordered]@{
        success = [ordered]@{ summary = (T "\u6210\u529f\u793a\u4f8b"); value = $successValue }
    })

    $failureResponseSchema = [ordered]@{
        type = "object"
        properties = [ordered]@{
            code = [ordered]@{ type = "string"; description = (T "\u9519\u8bef\u7801") }
            message = [ordered]@{ type = "string"; description = (T "\u9519\u8bef\u63d0\u793a") }
            data = [ordered]@{ nullable = $true; description = (T "\u5931\u8d25\u65f6\u4e00\u822c\u4e3a\u7a7a") }
            traceId = [ordered]@{ type = "string"; description = (T "\u94fe\u8def\u8ffd\u8e2aID") }
        }
    }

    $failureMap = [ordered]@{
        "400" = T "\u53c2\u6570\u9519\u8bef"
        "401" = T "\u672a\u767b\u5f55\u6216\u767b\u5f55\u5df2\u5931\u6548"
        "403" = T "\u65e0\u6743\u9650\u8bbf\u95ee"
        "404" = T "\u6570\u636e\u4e0d\u5b58\u5728"
        "409" = T "\u6570\u636e\u72b6\u6001\u51b2\u7a81"
        "500" = T "\u7cfb\u7edf\u5f02\u5e38"
    }

    foreach ($code in $failureMap.Keys) {
        $operation.responses[$code] = New-ApiResponse $failureMap[$code] $failureResponseSchema ([ordered]@{
            failure = [ordered]@{ summary = (T "\u5931\u8d25\u793a\u4f8b"); value = New-FailureExample $code $failureMap[$code] }
        })
    }

    return [ordered]@{
        path = $path
        method = $httpMethod
        operation = $operation
        tag = $tag
    }
}

$paths = [ordered]@{}
$tags = [ordered]@{}

foreach ($controllerFile in $controllerFiles) {
    $source = Normalize-JavaSource (Get-Content -LiteralPath $controllerFile.FullName -Raw -Encoding UTF8)
    $module = if ($controllerFile.FullName -match "\\mall-([^\\]+)\\") { $matches[1] } else { "default" }
    $controllerName = $controllerFile.BaseName
    $classPath = ""

    if ($source -match '(?s)@RequestMapping\s*\((.*?)\)\s*public\s+class') {
        $classPath = Get-AnnotationPath $matches[1] ""
    }

    $pendingAnnotations = @()
    $lines = $source -split "`r?`n"
    for ($index = 0; $index -lt $lines.Count; $index++) {
        $trimmedLine = $lines[$index].Trim()
        if ([string]::IsNullOrWhiteSpace($trimmedLine)) {
            continue
        }

        if ($trimmedLine.StartsWith("@")) {
            $pendingAnnotations += $trimmedLine
            continue
        }

        if ($trimmedLine -notmatch "^public\s+") {
            $pendingAnnotations = @()
            continue
        }

        $mappingAnnotation = ($pendingAnnotations | Where-Object { $_ -match "@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)" } | Select-Object -First 1)
        if (-not $mappingAnnotation) {
            $pendingAnnotations = @()
            continue
        }

        $signature = $trimmedLine
        while ($signature -notmatch "\)\s*\{" -and ($index + 1) -lt $lines.Count) {
            $index++
            $signature += " " + $lines[$index].Trim()
        }

        if ($signature -notmatch "public\s+(.+?)\s+([A-Za-z][A-Za-z0-9_]*)\s*\((.*?)\)\s*\{") {
            $pendingAnnotations = @()
            continue
        }

        $operationInfo = Convert-JavaMethodToOperation `
            -Module $module `
            -ControllerName $controllerName `
            -ClassPath $classPath `
            -Annotation $mappingAnnotation `
            -ReturnType $matches[1].Trim() `
            -MethodName $matches[2].Trim() `
            -Parameters $matches[3].Trim()

        if (-not $paths.Contains($operationInfo.path)) {
            $paths[$operationInfo.path] = [ordered]@{}
        }
        $paths[$operationInfo.path][$operationInfo.method] = $operationInfo.operation
        $tags[$operationInfo.tag] = [ordered]@{ name = $operationInfo.tag }
        $pendingAnnotations = @()
    }
}

$schemas = [ordered]@{}
foreach ($typeName in ($javaTypeIndex.Keys | Sort-Object -Unique)) {
    if ($typeName -match "\.") {
        continue
    }

    $fields = Get-JavaFields $typeName
    if ($fields.Count -eq 0) {
        continue
    }

    $properties = [ordered]@{}
    $required = @()
    foreach ($field in $fields) {
        $properties[$field.name] = Resolve-JavaType $field.type
        $properties[$field.name].description = Get-FieldDescription $field.name (T "\u8bf7\u6c42\u5b57\u6bb5\uff1a")
        if ($field.required) {
            $required += $field.name
        }
    }

    $schema = [ordered]@{
        type = "object"
        properties = $properties
    }
    if ($required.Count -gt 0) {
        $schema.required = $required
    }
    $schemas[$typeName] = $schema
}

$openApi = [ordered]@{
    openapi = "3.0.3"
    info = [ordered]@{
        title = T "\u793e\u533a\u56e2\u8d2d\u5546\u57ce\u5168\u94fe\u8def\u7cfb\u7edf\u63a5\u53e3\u6587\u6863"
        version = "1.0.0"
        description = T "\u6839\u636e Spring Controller \u6e90\u7801\u81ea\u52a8\u751f\u6210\uff0c\u7528\u4e8e\u5bfc\u5165 Apifox\u3002\u6240\u6709\u63a5\u53e3\u7edf\u4e00\u8fd4\u56de Result \u7ed3\u6784\uff0ccode=0 \u8868\u793a\u6210\u529f\u3002"
    }
    servers = @(
        [ordered]@{ url = "http://localhost:18000"; description = (T "\u672c\u5730\u7f51\u5173") },
        [ordered]@{ url = "/api"; description = (T "\u524d\u7aef\u4ee3\u7406") }
    )
    tags = @($tags.Values)
    paths = $paths
    components = [ordered]@{
        securitySchemes = [ordered]@{
            bearerAuth = [ordered]@{
                type = "http"
                scheme = "bearer"
                bearerFormat = "JWT"
            }
        }
        schemas = $schemas
    }
}

$outputFullPath = Join-Path $repoRoot $OutputPath
$outputDir = Split-Path $outputFullPath -Parent
if (-not (Test-Path -LiteralPath $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

$json = $openApi | ConvertTo-Json -Depth 100 -Compress
[System.IO.File]::WriteAllText($outputFullPath, $json, (New-Object System.Text.UTF8Encoding($false)))

$operationCount = 0
foreach ($pathProperty in $paths.GetEnumerator()) {
    $operationCount += $pathProperty.Value.Count
}

Write-Host "Generated OpenAPI: $OutputPath"
Write-Host "Operation count: $operationCount"
