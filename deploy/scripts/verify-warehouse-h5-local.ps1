param(
    [string]$Url = "http://localhost:5177/",
    [int]$DebugPort = 9333
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Assert-True {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

function Start-DebugChrome {
    param([string]$TargetUrl, [int]$Port)
    try {
        Invoke-RestMethod -Uri "http://127.0.0.1:$Port/json/version" -TimeoutSec 2 | Out-Null
        return
    } catch {
        $profile = Join-Path $env:TEMP "codex-warehouse-h5-chrome"
        if (-not (Test-Path $profile)) {
            New-Item -ItemType Directory -Path $profile | Out-Null
        }
        Start-Process -FilePath "chrome.exe" -ArgumentList @(
            "--remote-debugging-port=$Port",
            "--user-data-dir=$profile",
            "--no-first-run",
            "--new-window",
            $TargetUrl
        )
        Start-Sleep -Seconds 3
        Invoke-RestMethod -Uri "http://127.0.0.1:$Port/json/version" -TimeoutSec 10 | Out-Null
    }
}

Start-DebugChrome -TargetUrl $Url -Port $DebugPort

$nodeScript = @'
const http = require('http');
const targetUrl = process.argv[2];
const port = Number(process.argv[3]);

function getJson(path) {
  return new Promise((resolve, reject) => {
    http.get({ hostname: '127.0.0.1', port, path }, (res) => {
      let data = '';
      res.on('data', (chunk) => data += chunk);
      res.on('end', () => resolve(JSON.parse(data)));
    }).on('error', reject);
  });
}

function cdp(wsUrl) {
  const ws = new WebSocket(wsUrl);
  let seq = 0;
  const pending = new Map();
  const events = [];
  ws.addEventListener('message', (event) => {
    const msg = JSON.parse(event.data.toString());
    if (msg.id && pending.has(msg.id)) {
      const pendingItem = pending.get(msg.id);
      pending.delete(msg.id);
      if (msg.error) pendingItem.reject(new Error(JSON.stringify(msg.error)));
      else pendingItem.resolve(msg.result || {});
    } else if (msg.method) {
      events.push(msg);
    }
  });
  return new Promise((resolve, reject) => {
    ws.addEventListener('open', () => resolve({
      events,
      send(method, params = {}) {
        const id = ++seq;
        ws.send(JSON.stringify({ id, method, params }));
        return new Promise((resolve, reject) => pending.set(id, { resolve, reject }));
      },
      close() { ws.close(); }
    }));
    ws.addEventListener('error', reject);
  });
}

async function evalExpr(client, expression) {
  const result = await client.send('Runtime.evaluate', { expression, awaitPromise: true, returnByValue: true });
  if (result.exceptionDetails) throw new Error(JSON.stringify(result.exceptionDetails));
  return result.result.value;
}

async function waitFor(client, expression, timeout = 30000) {
  const startedAt = Date.now();
  while (Date.now() - startedAt < timeout) {
    const value = await evalExpr(client, expression);
    if (value) return value;
    await new Promise((resolve) => setTimeout(resolve, 300));
  }
  const text = await evalExpr(client, "document.body ? document.body.innerText.slice(0, 500) : ''").catch(() => '');
  throw new Error(`waitFor timeout: ${expression}\n${text}`);
}

async function loginRole(client, index, key) {
  const networkStart = client.events.length;
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.readyState === 'complete' || document.readyState === 'interactive'");
  await evalExpr(client, "localStorage.removeItem('mall_warehouse_h5_token'); localStorage.removeItem('mall_warehouse_h5_role'); localStorage.removeItem('mall_warehouse_h5_session'); localStorage.removeItem('mall_warehouse_h5_driver_tab'); true");
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page') && document.querySelectorAll('.login-role-option').length === 6");
  await evalExpr(client, `document.querySelectorAll('.login-role-option')[${index}].click(); true`);
  await waitFor(client, "document.querySelector('.login-btn')");
  await evalExpr(client, "document.querySelector('.login-btn').click(); true");
  await waitFor(client, "document.querySelector('.workbench-page') && document.querySelectorAll('.warehouse-nav-btn').length >= 3", 35000);
  await new Promise((resolve) => setTimeout(resolve, 1800));
  return await evalExpr(client, `(() => {
    const text = document.body.innerText;
    let session = {};
    try { session = JSON.parse(localStorage.getItem('mall_warehouse_h5_session') || '{}'); } catch (error) {}
    return {
      key: ${JSON.stringify(key)},
      selectedRole: localStorage.getItem('mall_warehouse_h5_role'),
      hasApi: text.includes('\u63a5\u53e3\u6570\u636e'),
      hasMock: text.includes('\u6f14\u793a\u6570\u636e'),
      nav: [...document.querySelectorAll('.warehouse-nav-btn')].map((button) => button.innerText.trim()),
      kpiCount: document.querySelectorAll('.warehouse-kpi').length,
      shellWidth: Math.round(document.querySelector('.phone-shell').getBoundingClientRect().width),
      overflowX: document.documentElement.scrollWidth > window.innerWidth,
      session
    };
  })()`).then((state) => {
    const events = client.events.slice(networkStart);
    const requestUrls = new Map();
    const wmsResponses = [];
    for (const event of events) {
      if (event.method === 'Network.requestWillBeSent') {
        requestUrls.set(event.params.requestId, event.params.request.url);
      }
      if (event.method === 'Network.responseReceived') {
        const url = requestUrls.get(event.params.requestId) || event.params.response.url || '';
        if (url.includes('/api/wms/')) {
          wmsResponses.push({ url, status: event.params.response.status });
        }
      }
    }
    return {
      ...state,
      wmsRequestCount: wmsResponses.length,
      wmsOkCount: wmsResponses.filter((item) => item.status >= 200 && item.status < 300).length,
      wmsPaths: [...new Set(wmsResponses.map((item) => new URL(item.url).pathname))].sort()
    };
  });
}

async function verifyDriverMapMode(client) {
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.readyState === 'complete' || document.readyState === 'interactive'");
  await evalExpr(client, "localStorage.removeItem('mall_warehouse_h5_token'); localStorage.removeItem('mall_warehouse_h5_role'); localStorage.removeItem('mall_warehouse_h5_session'); localStorage.removeItem('mall_warehouse_h5_driver_tab'); true");
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page') && document.querySelectorAll('.login-role-option').length === 6");
  await evalExpr(client, "document.querySelectorAll('.login-role-option')[4].click(); true");
  await evalExpr(client, "document.querySelector('.login-btn').click(); true");
  await waitFor(client, "document.querySelector('.workbench-page') && document.querySelectorAll('.warehouse-nav-btn').length === 4", 35000);
  await evalExpr(client, "document.querySelectorAll('.warehouse-nav-btn')[1].click(); true");
  await waitFor(client, "document.querySelector('.tasks-page') && document.querySelectorAll('.warehouse-task-card').length > 0", 30000);
  await evalExpr(client, "document.querySelector('.tasks-page .warehouse-task-card').click(); true");
  await waitFor(client, "document.querySelector('.driver-mode-tabs') && document.querySelectorAll('.driver-mode-tabs uni-button').length === 2", 30000);
  await evalExpr(client, "document.querySelectorAll('.driver-mode-tabs uni-button')[1].click(); true");
  await waitFor(client, "document.querySelector('.driver-map-mode') && document.querySelectorAll('.driver-map-stop').length > 0", 15000);
  await new Promise((resolve) => setTimeout(resolve, 500));
  return await evalExpr(client, `(() => ({
    hasMapMode: Boolean(document.querySelector('.driver-map-mode')),
    mapStopCount: document.querySelectorAll('.driver-map-stop').length,
    mapPointCount: document.querySelectorAll('.driver-map-line uni-view').length,
    activeMode: [...document.querySelectorAll('.driver-mode-tabs uni-button')].find((button) => button.classList.contains('active'))?.innerText.trim(),
    shellWidth: Math.round(document.querySelector('.phone-shell').getBoundingClientRect().width),
    overflowX: document.documentElement.scrollWidth > window.innerWidth,
    hasApi: document.body.innerText.includes('\u63a5\u53e3\u6570\u636e')
  }))()`);
}

async function verifyProfileRecord(client, roleIndex, entrySelector, cardSelector, expectedTitle) {
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.readyState === 'complete' || document.readyState === 'interactive'");
  await evalExpr(client, "localStorage.removeItem('mall_warehouse_h5_token'); localStorage.removeItem('mall_warehouse_h5_role'); localStorage.removeItem('mall_warehouse_h5_session'); localStorage.removeItem('mall_warehouse_h5_driver_tab'); true");
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page') && document.querySelectorAll('.login-role-option').length === 6");
  await evalExpr(client, `document.querySelectorAll('.login-role-option')[${roleIndex}].click(); true`);
  await evalExpr(client, "document.querySelector('.login-btn').click(); true");
  await waitFor(client, "document.querySelector('.workbench-page') && document.querySelectorAll('.warehouse-nav-btn').length >= 3", 35000);
  await evalExpr(client, "document.querySelectorAll('.warehouse-nav-btn')[document.querySelectorAll('.warehouse-nav-btn').length - 1].click(); true");
  await waitFor(client, `document.querySelector('.mine-page') && document.querySelector(${JSON.stringify(entrySelector)})`, 30000);
  await evalExpr(client, `document.querySelector(${JSON.stringify(entrySelector)}).click(); true`);
  await waitFor(client, `document.querySelector(${JSON.stringify(cardSelector)}) && document.querySelectorAll('.warehouse-record-item').length > 0`, 30000);
  await new Promise((resolve) => setTimeout(resolve, 500));
  return await evalExpr(client, `(() => ({
    title: document.querySelector('.warehouse-top .title')?.innerText.trim() || '',
    hasExpectedTitle: document.body.innerText.includes(${JSON.stringify(expectedTitle)}),
    recordCard: Boolean(document.querySelector(${JSON.stringify(cardSelector)})),
    toolCount: document.querySelectorAll('.warehouse-record-tools').length,
    recordCount: document.querySelectorAll('.warehouse-record-item').length,
    shellWidth: Math.round(document.querySelector('.phone-shell').getBoundingClientRect().width),
    overflowX: document.documentElement.scrollWidth > window.innerWidth,
    hasApi: document.body.innerText.includes('\u63a5\u53e3\u6570\u636e')
  }))()`);
}

async function verifyBuyerPurchaseCreate(client) {
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.readyState === 'complete' || document.readyState === 'interactive'");
  await evalExpr(client, "localStorage.removeItem('mall_warehouse_h5_token'); localStorage.removeItem('mall_warehouse_h5_role'); localStorage.removeItem('mall_warehouse_h5_session'); localStorage.removeItem('mall_warehouse_h5_driver_tab'); true");
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page') && document.querySelectorAll('.login-role-option').length === 6");
  await evalExpr(client, "document.querySelectorAll('.login-role-option')[1].click(); true");
  await evalExpr(client, "document.querySelector('.login-btn').click(); true");
  await waitFor(client, "document.querySelector('.workbench-page') && document.querySelectorAll('.warehouse-nav-btn').length === 3", 35000);
  await evalExpr(client, "document.querySelectorAll('.warehouse-nav-btn')[1].click(); true");
  await waitFor(client, "document.querySelector('.tasks-page') && document.querySelectorAll('.warehouse-task-card').length > 0", 30000);
  await evalExpr(client, `(() => {
    const cards = [...document.querySelectorAll('.warehouse-task-card')];
    const draft = cards.find((card) => card.innerText.includes('\u8349\u7a3f')) || cards[0];
    draft.click();
    return true;
  })()`);
  await waitFor(client, "document.querySelector('.detail-page')", 30000);
  await evalExpr(client, `(() => {
    if (!document.querySelector('.purchase-qty-stepper')) {
      location.hash = '#/pages/tasks/detail?id=PO20260611024&mode=purchaseCreate';
    }
    return true;
  })()`);
  await waitFor(client, "document.querySelector('.purchase-form') && document.querySelectorAll('.purchase-qty-stepper').length > 0", 30000);
  const before = await evalExpr(client, `(() => {
    const text = document.querySelector('.purchase-summary-strip')?.innerText || '';
    const nums = text.match(/\\d+/g) || ['0', '0'];
    return Number(nums[1] || nums[0] || 0);
  })()`);
  await evalExpr(client, "document.querySelector('.purchase-qty-stepper uni-button:last-child, .purchase-qty-stepper button:last-child').click(); true");
  await waitFor(client, `(() => {
    const text = document.querySelector('.purchase-summary-strip')?.innerText || '';
    const nums = text.match(/\\d+/g) || ['0', '0'];
    return Number(nums[1] || nums[0] || 0) > ${before};
  })()`, 10000);
  return await evalExpr(client, `(() => ({
    hasForm: Boolean(document.querySelector('.purchase-form')),
    stepperCount: document.querySelectorAll('.purchase-qty-stepper').length,
    selectedText: document.querySelector('.purchase-summary-strip')?.innerText || '',
    submitDisabled: Boolean(document.querySelector('.detail-page .full-btn')?.disabled),
    shellWidth: Math.round(document.querySelector('.phone-shell').getBoundingClientRect().width),
    overflowX: document.documentElement.scrollWidth > window.innerWidth
  }))()`);
}

async function verifyTaskReceipt(client, roleIndex, taskId, mode, expectedSelector, expectedText) {
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.readyState === 'complete' || document.readyState === 'interactive'");
  await evalExpr(client, "localStorage.removeItem('mall_warehouse_h5_token'); localStorage.removeItem('mall_warehouse_h5_role'); localStorage.removeItem('mall_warehouse_h5_session'); localStorage.removeItem('mall_warehouse_h5_driver_tab'); true");
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page') && document.querySelectorAll('.login-role-option').length === 6");
  await evalExpr(client, `document.querySelectorAll('.login-role-option')[${roleIndex}].click(); true`);
  await evalExpr(client, "document.querySelector('.login-btn').click(); true");
  await waitFor(client, "document.querySelector('.workbench-page') && document.querySelectorAll('.warehouse-nav-btn').length >= 3", 35000);
  await evalExpr(client, `location.hash = '#/pages/tasks/detail?id=${encodeURIComponent(taskId)}&mode=${encodeURIComponent(mode)}'; true`);
  await waitFor(client, `document.querySelector('.detail-page') && document.querySelector(${JSON.stringify(expectedSelector)})`, 30000);
  await new Promise((resolve) => setTimeout(resolve, 500));
  return await evalExpr(client, `(() => ( {
    mode: ${JSON.stringify(mode)},
    hasExpectedSelector: Boolean(document.querySelector(${JSON.stringify(expectedSelector)})),
    hasExpectedText: document.body.innerText.includes(${JSON.stringify(expectedText)}),
    photoCount: document.querySelectorAll('.receipt-photo').length,
    miniRowCount: document.querySelectorAll('.receipt-mini > uni-view, .receipt-mini > view').length,
    stampCount: document.querySelectorAll('.receipt-stamp').length,
    shellWidth: Math.round(document.querySelector('.phone-shell').getBoundingClientRect().width),
    overflowX: document.documentElement.scrollWidth > window.innerWidth
  }))()`);
}

(async () => {
  const tabs = await getJson('/json');
  const tab = tabs.find((item) => item.type === 'page' && item.url.includes(new URL(targetUrl).host)) || tabs.find((item) => item.type === 'page');
  const client = await cdp(tab.webSocketDebuggerUrl);
  await client.send('Runtime.enable');
  await client.send('Page.enable');
  await client.send('Network.enable');
  await client.send('Log.enable');
  await client.send('Emulation.setDeviceMetricsOverride', { width: 375, height: 812, deviceScaleFactor: 2, mobile: true });

  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page,.workbench-page,.tasks-page')");
  await evalExpr(client, "localStorage.removeItem('mall_warehouse_h5_token'); localStorage.removeItem('mall_warehouse_h5_role'); localStorage.removeItem('mall_warehouse_h5_session'); true");
  await client.send('Page.navigate', { url: targetUrl });
  await waitFor(client, "document.querySelector('.login-page') && document.querySelectorAll('.login-role-option').length === 6");
  const loginPage = await evalExpr(client, `(() => ({
    roleButtonCount: document.querySelectorAll('.login-role-option').length,
    isOpen: document.querySelector('.role-panel')?.classList.contains('open'),
    shellWidth: Math.round(document.querySelector('.phone-shell').getBoundingClientRect().width),
    overflowX: document.documentElement.scrollWidth > window.innerWidth
  }))()`);

  const roles = ['receiver', 'buyer', 'picker', 'recheck', 'driver', 'manager'];
  const results = [];
  for (let i = 0; i < roles.length; i += 1) {
    results.push(await loginRole(client, i, roles[i]));
  }
  const driverMap = await verifyDriverMapMode(client);
  const receiveRecord = await verifyProfileRecord(client, 0, '.warehouse-profile-link', '.warehouse-receive-record-card', '\u6536\u8d27\u8bb0\u5f55');
  const pickRecord = await verifyProfileRecord(client, 2, '.warehouse-profile-link', '.warehouse-pick-record-card', '\u62e3\u8d27\u8bb0\u5f55');
  const buyerPurchase = await verifyBuyerPurchaseCreate(client);
  const purchaseReceipt = await verifyTaskReceipt(client, 0, 'IN20260610032', 'purchaseReceipt', '.receipt-gallery', '\u5165\u5e93\u56fe\u7247');
  const loadingReceipt = await verifyTaskReceipt(client, 3, 'LOAD20260610063', 'loadingReceipt', '.receipt-stamp', '\u88c5\u8f66\u56de\u6267');

  const errors = client.events
    .filter((event) => event.method === 'Runtime.consoleAPICalled' && event.params.type === 'error')
    .map((event) => event.params.args.map((arg) => arg.value || arg.description).join(' '));
  const exceptions = client.events
    .filter((event) => event.method === 'Runtime.exceptionThrown')
    .map((event) => event.params.exceptionDetails?.text || 'exception');

  console.log(JSON.stringify({ loginPage, results, driverMap, receiveRecord, pickRecord, buyerPurchase, purchaseReceipt, loadingReceipt, errors, exceptions }, null, 2));
  client.close();
})().catch((error) => {
  console.error(error.stack || error);
  process.exit(1);
});
'@

$node = "node"
$nodeRuntime = "C:\Users\Administrator\AppData\Local\OpenAI\Codex\runtimes\cua_node\1b23c930bdf84ed6\bin\node.exe"
if (Test-Path $nodeRuntime) {
    $node = $nodeRuntime
}

$result = $nodeScript | & $node - $Url $DebugPort
Write-Host $result
$json = $result | ConvertFrom-Json

Assert-True ($json.loginPage.roleButtonCount -eq 6) "Login page should show six role buttons."
Assert-True ($json.loginPage.isOpen) "Login role panel should be open by default."
Assert-True ($json.loginPage.shellWidth -eq 375) "Login shell width should be 375px."
Assert-True (-not $json.loginPage.overflowX) "Login page should not overflow horizontally."
Assert-True ($json.errors.Count -eq 0) ("Console errors: " + ($json.errors -join "; "))
Assert-True ($json.exceptions.Count -eq 0) ("Runtime exceptions: " + ($json.exceptions -join "; "))
Assert-True ($json.driverMap.hasMapMode) "Driver delivery detail should show map mode."
Assert-True ($json.driverMap.mapStopCount -gt 0) "Driver map mode should show route stops."
Assert-True ($json.driverMap.mapPointCount -gt 0) "Driver map mode should show route points."
Assert-True ($json.driverMap.activeMode -eq ($mapLabel = [char]0x5730 + [char]0x56FE + [char]0x6A21 + [char]0x5F0F)) "Driver map mode tab should be active."
Assert-True ($json.driverMap.shellWidth -eq 375) "Driver detail shell width should be 375px."
Assert-True (-not $json.driverMap.overflowX) "Driver detail should not overflow horizontally."
Assert-True ($json.receiveRecord.hasExpectedTitle) "Receiver profile should open receive record page."
Assert-True ($json.receiveRecord.recordCard) "Receive record card should render."
Assert-True ($json.receiveRecord.toolCount -gt 0) "Receive record filters should render."
Assert-True ($json.receiveRecord.recordCount -gt 0) "Receive record list should have rows."
Assert-True ($json.receiveRecord.shellWidth -eq 375) "Receive record shell width should be 375px."
Assert-True (-not $json.receiveRecord.overflowX) "Receive record should not overflow horizontally."
Assert-True ($json.pickRecord.hasExpectedTitle) "Picker profile should open pick record page."
Assert-True ($json.pickRecord.recordCard) "Pick record card should render."
Assert-True ($json.pickRecord.toolCount -gt 0) "Pick record filters should render."
Assert-True ($json.pickRecord.recordCount -gt 0) "Pick record list should have rows."
Assert-True ($json.pickRecord.shellWidth -eq 375) "Pick record shell width should be 375px."
Assert-True (-not $json.pickRecord.overflowX) "Pick record should not overflow horizontally."
Assert-True ($json.buyerPurchase.hasForm) "Buyer purchase create should show warehouse/date form."
Assert-True ($json.buyerPurchase.stepperCount -gt 0) "Buyer purchase create should show quantity steppers."
Assert-True ($json.buyerPurchase.selectedText -match "SKU") "Buyer purchase create should show selected SKU summary."
Assert-True (-not $json.buyerPurchase.submitDisabled) "Buyer purchase submit should be enabled when items are selected."
Assert-True ($json.buyerPurchase.shellWidth -eq 375) "Buyer purchase create shell width should be 375px."
Assert-True (-not $json.buyerPurchase.overflowX) "Buyer purchase create should not overflow horizontally."
Assert-True ($json.purchaseReceipt.hasExpectedSelector) "Purchase receipt should render receipt gallery."
Assert-True ($json.purchaseReceipt.hasExpectedText) "Purchase receipt should show inbound receipt text."
Assert-True ($json.purchaseReceipt.photoCount -gt 0) "Purchase receipt should show receipt photos."
Assert-True ($json.purchaseReceipt.miniRowCount -gt 0) "Purchase receipt should show receipt summary rows."
Assert-True ($json.purchaseReceipt.shellWidth -eq 375) "Purchase receipt shell width should be 375px."
Assert-True (-not $json.purchaseReceipt.overflowX) "Purchase receipt should not overflow horizontally."
Assert-True ($json.loadingReceipt.hasExpectedSelector) "Loading receipt should render receipt stamp."
Assert-True ($json.loadingReceipt.hasExpectedText) "Loading receipt should show loading receipt text."
Assert-True ($json.loadingReceipt.stampCount -gt 0) "Loading receipt should show receipt stamp."
Assert-True ($json.loadingReceipt.miniRowCount -gt 0) "Loading receipt should show receipt summary rows."
Assert-True ($json.loadingReceipt.shellWidth -eq 375) "Loading receipt shell width should be 375px."
Assert-True (-not $json.loadingReceipt.overflowX) "Loading receipt should not overflow horizontally."

$workbenchLabel = [char]0x5DE5 + [char]0x4F5C + [char]0x53F0
$receiveLabel = [char]0x6536 + [char]0x8D27 + [char]0x5355
$mineLabel = [char]0x6211 + [char]0x7684
$purchaseLabel = [char]0x91C7 + [char]0x8D2D + [char]0x5355
$taskLabel = [char]0x4EFB + [char]0x52A1
$deliveryLabel = [char]0x914D + [char]0x9001 + [char]0x5355
$returnLabel = [char]0x9000 + [char]0x8D27 + [char]0x5355
$statsLabel = [char]0x4F5C + [char]0x4E1A + [char]0x7EDF + [char]0x8BA1
$stockLabel = [char]0x5E93 + [char]0x5B58

$expectedNav = @{
    receiver = @($workbenchLabel, $receiveLabel, $mineLabel)
    buyer = @($workbenchLabel, $purchaseLabel, $mineLabel)
    picker = @($workbenchLabel, $taskLabel, $mineLabel)
    recheck = @($workbenchLabel, $taskLabel, $mineLabel)
    driver = @($workbenchLabel, $deliveryLabel, $returnLabel, $statsLabel)
    manager = @($workbenchLabel, $taskLabel, $stockLabel, $mineLabel)
}

foreach ($row in $json.results) {
    Assert-True ($row.selectedRole -eq $row.key) "$($row.key) selected role mismatch."
    Assert-True ($row.shellWidth -eq 375) "$($row.key) shell width should be 375px."
    Assert-True (-not $row.overflowX) "$($row.key) should not overflow horizontally."
    $expected = $expectedNav[$row.key]
    Assert-True (($row.nav -join "|") -eq ($expected -join "|")) "$($row.key) nav mismatch: $($row.nav -join ', ')"
    Assert-True $row.hasApi "$($row.key) should show API data source."
    Assert-True ($row.wmsRequestCount -gt 0) "$($row.key) should call WMS API."
    Assert-True ($row.wmsOkCount -gt 0) "$($row.key) should receive a successful WMS API response."
}

Write-Host "Warehouse H5 local browser smoke check passed."
