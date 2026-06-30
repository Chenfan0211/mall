<template>
  <view class="page phone-shell detail-page">
    <view class="warehouse-top detail-top">
      <view class="status-row">
        <button class="back-btn" @click="goBack">‹</button>
        <text>{{ modeTitle }}</text>
      </view>
      <view class="detail-title-row">
        <view>
          <text class="eyebrow">{{ profile.name }}作业</text>
          <text class="title">{{ headerTitle }}</text>
          <text class="subtle">{{ headerSub }}</text>
        </view>
        <text v-if="task" class="status-pill" :class="task.statusTone">{{ task.status }}</text>
        <text v-else-if="driverOrder" class="status-pill" :class="driverOrder.statusTone">{{ driverOrder.status }}</text>
        <text v-else-if="returnOrder" class="status-pill" :class="returnOrder.statusTone">{{ returnOrder.status }}</text>
      </view>
    </view>

    <template v-if="driverOrder">
      <view class="section driver-summary">
        <view class="driver-grid">
          <view><text>{{ driverOrder.stationCount }}</text><text>门店</text></view>
          <view><text>{{ driverOrder.cargoCount }}</text><text>货物</text></view>
          <view><text>{{ driverOrder.returnCount }}</text><text>退货</text></view>
          <view><text>{{ driverOrder.distance }}</text><text>路程</text></view>
        </view>
        <view class="detail-grid mt">
          <view><text>车辆</text><text>{{ driverOrder.vehicle }}</text></view>
          <view><text>出库单</text><text>{{ driverOrder.outboundNo }}</text></view>
          <view><text>装车回执</text><text>{{ driverOrder.handoverNo }}</text></view>
          <view><text>截止</text><text>{{ driverOrder.deadline }}</text></view>
        </view>
        <view class="action-row">
          <button class="plain" @click="showReceipt">装车回执</button>
          <button class="primary" @click="runDriver(driverOrder.status === '待发车' ? '确认出发' : '发起回仓交接')">
            {{ driverOrder.status === '待发车' ? '确认出发' : '发起回仓交接' }}
          </button>
        </view>
      </view>

      <view class="section">
        <view class="section-head">
          <view>
            <text class="section-title">配送门店</text>
          </view>
        </view>
        <view class="driver-mode-tabs">
          <button :class="{ active: driverDetailMode === 'list' }" @click="driverDetailMode = 'list'">列表模式</button>
          <button :class="{ active: driverDetailMode === 'map' }" @click="driverDetailMode = 'map'">地图模式</button>
        </view>
        <view v-if="driverDetailMode === 'map'" class="driver-map-mode">
          <view class="driver-map-card">
            <view class="driver-map-card-head">
              <view>
                <text>{{ driverOrder.routeName }}</text>
                <text>{{ driverOrder.warehouseAddress }} → {{ lastStationName }}</text>
              </view>
              <text>{{ driverOrder.distance }}</text>
            </view>
            <view class="driver-map-line">
              <view v-for="station in driverOrder.stations" :key="station.id" :class="{ done: station.status === '已完成', active: station.status === '已到达' || station.status === '配送中' }">
                <text>{{ station.index }}</text>
              </view>
            </view>
          </view>
          <view class="driver-map-stops">
            <view v-for="station in driverOrder.stations" :key="station.id" class="driver-map-stop">
              <text>{{ station.index }}. {{ station.name }}</text>
              <text>{{ station.distance }} · 配送 {{ station.deliveryQty }} · 退货 {{ station.returnQty }} · {{ station.status }}</text>
            </view>
          </view>
        </view>
        <view v-else class="station-list">
          <view v-for="station in driverOrder.stations" :key="station.id" class="station-card">
            <view class="task-head">
              <view>
                <text class="task-title">{{ station.index }}. {{ station.name }}</text>
                <text class="task-sub">{{ station.address }}</text>
              </view>
              <text class="status-pill" :class="station.statusTone">{{ station.status }}</text>
            </view>
            <view class="task-meta">
              <text>{{ station.distance }}</text>
              <text>配送 {{ station.deliveryQty }}</text>
              <text>退货 {{ station.returnQty }}</text>
            </view>
            <text v-if="station.abnormalNote" class="pending-strip">{{ station.abnormalNote }}</text>
            <view class="action-grid compact">
              <button class="plain" @click="openGoods(station)">配送明细</button>
              <button class="plain" @click="runDriver('到达自提点', station.id)">到达</button>
              <button class="primary" @click="runDriver('完成配送', station.id)">完成配送</button>
              <button class="danger soft-danger" @click="runDriver('无法送达', station.id)">无法送达</button>
              <button v-if="station.returnQty" class="plain" @click="runDriver('退货交接', station.id)">退货交接</button>
            </view>
          </view>
        </view>
      </view>

      <view class="section">
        <text class="section-title">操作历史</text>
        <view class="log-list">
          <text v-for="log in driverOrder.logs" :key="log">{{ log }}</text>
        </view>
      </view>
    </template>

    <template v-else-if="returnOrder">
      <view class="section">
        <view class="detail-grid">
          <view><text>线路</text><text>{{ returnOrder.routeName }}</text></view>
          <view><text>自提点</text><text>{{ returnOrder.station }}</text></view>
          <view><text>退货数</text><text>{{ returnOrder.returnQty }}</text></view>
          <view><text>日期</text><text>{{ returnOrder.deliveryDate }}</text></view>
        </view>
        <text class="task-note">{{ returnOrder.contact }} · {{ returnOrder.address }}</text>
      </view>
      <view class="section">
        <text class="section-title">退货商品</text>
        <view class="goods-list">
          <view v-for="item in returnOrder.items" :key="item.id" class="goods-card compact-goods">
            <view class="goods-img blue">{{ item.title.slice(0, 1) }}</view>
            <view>
              <text class="task-title">{{ item.title }}</text>
              <text class="goods-line">{{ item.sku }} · {{ item.spec }}</text>
              <view class="qty-line"><text>计划 {{ item.expected }}{{ item.unit }}</text><text>实交 {{ item.actual }}{{ item.unit }}</text></view>
            </view>
          </view>
        </view>
        <button class="primary full-btn" @click="confirmReturn">发起回仓交接</button>
      </view>
    </template>

    <template v-else-if="task">
      <view v-if="mode === 'purchaseReceipt'" class="section receipt-section">
        <view class="section-head">
          <view>
            <text class="section-title">入库图片</text>
          </view>
        </view>
        <view class="receipt-gallery">
          <view v-for="item in receiptImages" :key="item.label" class="receipt-photo" :class="item.tone">
            <text>{{ item.label }}</text>
          </view>
        </view>
        <view class="receipt-mini">
          <view><text>采购单号</text><text>{{ task.sourceNo }}</text></view>
          <view><text>入库仓库</text><text>{{ task.warehouse }}</text></view>
          <view><text>配送时间</text><text>{{ task.due }}</text></view>
          <view><text>入库数量</text><text>{{ task.actualQty || task.plannedQty }} / {{ task.plannedQty }}</text></view>
        </view>
      </view>

      <view v-if="mode === 'loadingReceipt'" class="section receipt-section">
        <view class="section-head">
          <view>
            <text class="section-title">装车完成回执</text>
          </view>
        </view>
        <view class="receipt-mini">
          <view><text>装车单</text><text>{{ task.currentNo }}</text></view>
          <view><text>出库单</text><text>{{ task.sourceNo }}</text></view>
          <view><text>装车月台</text><text>{{ task.location }}</text></view>
          <view><text>线路</text><text>{{ task.route }}</text></view>
        </view>
        <view class="receipt-stamp">
          <text>装车回执</text>
          <text>{{ task.receiptNo || `LOAD-RCPT-${task.currentNo}` }}</text>
        </view>
      </view>

      <view class="section task-basic-section">
        <view class="section-head">
          <view>
            <text class="section-title">任务基础信息</text>
          </view>
        </view>
        <view class="detail-grid">
          <view><text>任务单号</text><text>{{ task.currentNo }}</text></view>
          <view><text>来源单据</text><text>{{ task.sourceNo }}</text></view>
          <view><text>当前单据</text><text>{{ task.currentNo }}</text></view>
          <view><text>仓库</text><text>{{ task.warehouse }}</text></view>
          <view><text>库位/线路</text><text>{{ task.route || task.location }}</text></view>
          <view><text>处理人</text><text>{{ profile.account }}</text></view>
          <view><text>更新时间</text><text>{{ task.updatedAt }}</text></view>
        </view>
        <view class="quantity-confirm">
          <view>
            <text>{{ task.plannedQty }}</text>
            <text>计划数量</text>
          </view>
          <view>
            <text>{{ task.actualQty }}</text>
            <text>实际数量</text>
          </view>
          <view :class="{ danger: quantityDiff > 0 }">
            <text>{{ quantityDiff }}</text>
            <text>差异数量</text>
          </view>
        </view>
        <text v-if="task.submitPending" class="pending-strip">提交结果待确认：{{ task.submitPendingAction }}，请等待系统回写，避免重复提交。</text>
      </view>

      <view v-if="mode === 'receiveArea'" class="section">
        <text class="section-title">选择收货库区</text>
        <view class="area-options">
          <button v-for="area in areaOptions" :key="area" class="area-card" @click="selectArea(area)">
            <text>{{ area }}</text>
            <text>接单前确认暂存区和温区</text>
          </button>
        </view>
      </view>

      <view v-if="mode === 'purchaseCreate'" class="section">
        <view class="section-head">
          <view>
            <text class="section-title">新建采购单</text>
          </view>
        </view>
        <view class="purchase-form">
          <view>
            <text>仓库名称</text>
            <picker :range="purchaseWarehouseOptions" @change="setPurchaseWarehouse">
              <view>{{ purchaseWarehouse }}</view>
            </picker>
          </view>
          <view>
            <text>配送时间</text>
            <picker mode="date" @change="setPurchaseDate">
              <view>{{ purchaseDate }}</view>
            </picker>
          </view>
        </view>
        <view class="search-box standalone"><text>⌕</text><input v-model="catalogKeyword" placeholder="搜索商品名称或 SKU" /></view>
        <view class="purchase-summary-strip">
          <text>已选择 {{ selectedPurchaseItems.length }} 个 SKU</text>
          <text>计划 {{ selectedPurchaseQty }} 件</text>
        </view>
        <view class="goods-list">
          <view v-for="item in filteredCatalog" :key="item.id" class="goods-card compact-goods">
            <view class="goods-img" :class="item.imageTone">{{ item.title.slice(0, 1) }}</view>
            <view class="purchase-catalog-main">
              <text class="task-title">{{ item.title }}</text>
              <text class="goods-line">{{ item.sku }} · {{ item.spec }} · {{ item.location }}</text>
              <view class="qty-line"><text>建议 {{ item.planQty }}{{ item.unit }}</text><text>{{ purchaseQty(item.id) > 0 ? '已选择' : '未选择' }}</text></view>
              <view class="purchase-qty-stepper">
                <button @click="stepPurchaseQty(item.id, -1)">-</button>
                <input :value="purchaseQty(item.id)" type="number" @input="setPurchaseQty(item.id, $event)" />
                <button @click="stepPurchaseQty(item.id, 1)">+</button>
              </view>
            </view>
          </view>
        </view>
        <button class="primary full-btn" :disabled="selectedPurchaseItems.length === 0" @click="createPurchase">提交审核</button>
      </view>

      <view class="section">
        <view class="section-head">
          <view>
            <text class="section-title">{{ goodsSectionTitle }}</text>
          </view>
        </view>
        <view class="task-goods-list">
          <view v-for="item in task.items" :key="item.id" class="task-goods-row">
            <view class="task-goods-img" :class="item.imageTone">{{ item.title.slice(0, 1) }}</view>
            <view class="task-goods-main">
              <text class="task-goods-title">{{ item.title }}</text>
              <text class="task-goods-line">存储方式：{{ item.spec }}</text>
              <text class="task-goods-line">库位：{{ item.location }}</text>
              <text class="task-goods-barcode">{{ item.barcode }}</text>
            </view>
            <view class="task-goods-qty">
              <view>
                <text>应拣</text>
                <text>{{ item.planQty }}{{ item.unit }}</text>
              </view>
              <view>
                <text>已拣</text>
                <text>{{ item.actualQty }}{{ item.unit }}</text>
              </view>
            </view>
            <view v-if="editableQty" class="qty-editor task-goods-editor">
              <button @click="stepQty(item.id, -1)">-</button>
              <input :value="item.actualQty" type="number" @input="setQty(item.id, $event)" />
              <button @click="stepQty(item.id, 1)">+</button>
            </view>
            <view v-if="mode === 'receiveScan'" class="location-actions task-goods-actions">
              <button class="plain mini-btn" @click="changeLocation(item.id)">更改库位</button>
              <button class="plain mini-btn" @click="scanItem(item.id)">扫码条码</button>
            </view>
            <view v-if="mode === 'pickShortage' && item.stationDemand" class="shortage-list task-goods-shortage">
              <view v-for="row in item.stationDemand" :key="row.station" class="shortage-row">
                <view><text>{{ row.station }}</text><text>{{ row.route }} · 缺 {{ row.shortage }}{{ item.unit }}</text></view>
                <input :value="row.marked" type="number" @input="setShortage(item.id, row.station, $event)" />
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="mode === 'receiveQuality'" class="section quality-card">
        <text class="section-title">上传质检凭证</text>
        <view class="quality-grid">
          <view><text>质检结果</text><text>合格</text></view>
          <view><text>图片数量</text><text>3 张</text></view>
          <view><text>温度备注</text><text>非强制</text></view>
        </view>
        <textarea v-model="qualityNote" placeholder="填写质检说明、破损、温度或批次备注" />
      </view>
    </template>

    <view v-else class="section empty-panel">
      <text class="section-title">单据不存在</text>
      <text class="section-sub">请从任务列表重新进入。</text>
      <button class="primary" @click="goTasks">返回任务</button>
    </view>

    <WarehouseBottomNav v-if="task || driverOrder || returnOrder" :active="returnOrder ? 'stock' : 'tasks'" :role="profile.key" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import WarehouseBottomNav from '@/components/WarehouseBottomNav.vue';
import {
    actionIsCritical,
    applyDriverAction,
    applyTaskAction,
    findDriverOrder,
    findTask,
    getCurrentRole,
    getReturnOrders,
    getRoleProfile,
    loadWarehouseDashboard,
    markShortage,
    markTaskPending,
    purchaseCatalog,
    updateTaskItemLocation,
    updateTaskItemQty,
    type DriverStation,
    type RoleProfile,
    type WarehouseDriverOrder,
    type WarehouseReturnOrder,
    type WarehouseTask
} from '@/stores/warehouse';

const id = ref('');
const mode = ref('');
const profile = ref<RoleProfile>(getRoleProfile());
const task = ref<WarehouseTask | null>(null);
const driverOrder = ref<WarehouseDriverOrder | null>(null);
const returnOrder = ref<WarehouseReturnOrder | null>(null);
const reason = ref('');
const qualityNote = ref('');
const catalogKeyword = ref('');
const driverDetailMode = ref<'list' | 'map'>('list');
const purchaseWarehouseOptions = ['广州中心仓', '佛山前置仓', '深圳南山仓'];
const purchaseWarehouse = ref('广州中心仓');
const purchaseDate = ref('2026-06-12');
const purchaseQtyMap = ref<Record<string, number>>({});

const areaOptions = ['R-收货暂存区', 'C-冷藏收货口', 'F-冷冻收货口', 'B-常温暂存区'];

const modeTitle = computed(() => {
    if (driverOrder.value) return '配送门店详情';
    if (returnOrder.value) return '退货详情';
    const map: Record<string, string> = {
        receiveArea: '选择库区',
        receiveScan: '扫码库位',
        receiveQuality: '质检凭证',
        pickWork: '拣货作业',
        pickShortage: '履约缺货',
        recheckWork: '装车作业',
        loadingReceipt: '装车回执',
        purchaseReceipt: '入库回执',
        managerPutaway: '上架处理',
        purchaseCreate: '新建采购单'
    };
    return map[mode.value] || '任务详情';
});

const headerTitle = computed(() => driverOrder.value?.routeName || returnOrder.value?.station || task.value?.title || '任务详情');
const headerSub = computed(() => driverOrder.value?.id || returnOrder.value?.id || (task.value ? `${task.value.currentNo} · ${task.value.sourceNo}` : ''));
const lastStationName = computed(() => {
    const stations = driverOrder.value?.stations || [];
    return stations[stations.length - 1]?.name || '线路终点';
});
const editableQty = computed(() => ['receiveScan', 'pickWork', 'recheckWork', 'managerPutaway'].includes(mode.value));
const goodsSectionTitle = computed(() => {
    if (mode.value === 'pickShortage') return '履约缺货商品';
    if (mode.value === 'recheckWork') return '装车商品明细';
    if (mode.value === 'managerPutaway') return '上架商品明细';
    return '商品明细';
});
const filteredCatalog = computed(() => {
    const q = catalogKeyword.value.trim().toLowerCase();
    return purchaseCatalog.filter((item) => !q || [item.title, item.sku, item.barcode, item.spec, item.location].join(' ').toLowerCase().includes(q));
});
const selectedPurchaseItems = computed(() => purchaseCatalog.filter((item) => purchaseQty(item.id) > 0));
const selectedPurchaseQty = computed(() => selectedPurchaseItems.value.reduce((sum, item) => sum + purchaseQty(item.id), 0));
const receiptImages = computed(() => {
    const labels = ['收货现场', '质检凭证', '差异留痕'];
    return (task.value?.items || []).slice(0, 3).map((item, index) => ({
        label: labels[index] || item.title,
        tone: item.imageTone || 'green'
    }));
});
const quantityDiff = computed(() => Math.abs(Number(task.value?.plannedQty || 0) - Number(task.value?.actualQty || 0)));

async function loadData() {
    const role = getCurrentRole();
    profile.value = getRoleProfile(role);
    task.value = findTask(id.value) || null;
    driverOrder.value = findDriverOrder(id.value) || null;
    returnOrder.value = getReturnOrders().find((item) => item.id === id.value) || null;

    if (!task.value && !driverOrder.value && !returnOrder.value && id.value) {
        const dashboard = await loadWarehouseDashboard(role);
        profile.value = dashboard.profile;
        task.value = dashboard.tasks.find((item) => item.id === id.value || item.currentNo === id.value || item.sourceNo === id.value) || null;
        driverOrder.value = dashboard.driverOrders.find((item) => item.id === id.value) || null;
        returnOrder.value = dashboard.returnOrders.find((item) => item.id === id.value) || null;
    }

    if (!mode.value) {
        mode.value = inferMode();
    }
    initPurchaseDraft();
}

function initPurchaseDraft() {
    if (mode.value !== 'purchaseCreate') return;
    if (Object.keys(purchaseQtyMap.value).length) return;
    const fromTask = task.value?.items || [];
    const next: Record<string, number> = {};
    purchaseCatalog.forEach((catalog) => {
        const matched = fromTask.find((item) => item.sku === catalog.sku);
        next[catalog.id] = Number(matched?.planQty || catalog.planQty || 1);
    });
    purchaseQtyMap.value = next;
}

function inferMode() {
    if (driverOrder.value) return 'driverDelivery';
    if (!task.value) return '';
    if (profile.value.key === 'manager') return 'managerPutaway';
    if (task.value.role === 'receiver') return task.value.status === '待接单' ? 'receiveArea' : 'receiveScan';
    if (task.value.role === 'picker') return /履约缺货/.test(task.value.status) ? 'pickShortage' : 'pickWork';
    if (task.value.role === 'recheck') return 'recheckWork';
    if (task.value.role === 'manager') return 'managerPutaway';
    if (task.value.role === 'buyer' && task.value.status === '草稿') return 'purchaseCreate';
    return '';
}

function goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
        uni.navigateBack();
        return;
    }
    goTasks();
}

function goTasks() {
    uni.switchTab({ url: '/pages/tasks/index' });
}

function selectArea(area: string) {
    if (!task.value) return;
    updateTaskItemLocation(task.value, task.value.items[0]?.id || '', area);
    runAction('接单');
}

function stepQty(itemId: string, delta: number) {
    if (!task.value) return;
    const item = task.value.items.find((row) => row.id === itemId);
    if (!item) return;
    updateTaskItemQty(task.value, itemId, item.actualQty + delta);
    void loadData();
}

function setQty(itemId: string, event: Event) {
    if (!task.value) return;
    const value = Number((event.target as HTMLInputElement).value || 0);
    updateTaskItemQty(task.value, itemId, value);
    void loadData();
}

function setShortage(itemId: string, station: string, event: Event) {
    if (!task.value) return;
    const value = Number((event.target as HTMLInputElement).value || 0);
    markShortage(task.value, itemId, station, value);
    void loadData();
}

function purchaseQty(itemId: string) {
    return Number(purchaseQtyMap.value[itemId] || 0);
}

function setPurchaseWarehouse(event: { detail: { value: number | string } }) {
    const index = Number(event.detail.value || 0);
    purchaseWarehouse.value = purchaseWarehouseOptions[index] || purchaseWarehouseOptions[0];
}

function setPurchaseDate(event: { detail: { value: string } }) {
    purchaseDate.value = event.detail.value || purchaseDate.value;
}

function setPurchaseQty(itemId: string, event: Event) {
    const value = Number((event.target as HTMLInputElement).value || 0);
    updatePurchaseQty(itemId, value);
}

function stepPurchaseQty(itemId: string, delta: number) {
    updatePurchaseQty(itemId, purchaseQty(itemId) + delta);
}

function updatePurchaseQty(itemId: string, value: number) {
    const next = Math.max(0, Math.min(999, Math.round(Number(value || 0))));
    purchaseQtyMap.value = { ...purchaseQtyMap.value, [itemId]: next };
}

function changeLocation(itemId: string) {
    if (!task.value) return;
    const next = task.value.location.includes('C-') ? 'C-R-08' : 'R-03-02';
    updateTaskItemLocation(task.value, itemId, next);
    uni.showToast({ title: `库位已改为${next}`, icon: 'none' });
    void loadData();
}

function scanItem(itemId: string) {
    if (!task.value) return;
    const item = task.value.items.find((row) => row.id === itemId);
    if (!item) return;
    updateTaskItemQty(task.value, itemId, Math.min(item.planQty, item.actualQty + 1));
    uni.showToast({ title: '扫码成功，重复扫码不自动累加', icon: 'none' });
    void loadData();
}

function runAction(action: string) {
    if (!task.value) return;
    if (action === '查看入库回执') {
        mode.value = 'purchaseReceipt';
        uni.showToast({ title: '已打开入库回执', icon: 'none' });
        return;
    }
    if (action === '查看装车回执') {
        mode.value = 'loadingReceipt';
        uni.showToast({ title: '已打开装车回执', icon: 'none' });
        return;
    }
    if (/查看/.test(action)) {
        uni.showToast({ title: `${action}已打开`, icon: 'none' });
        return;
    }
    if (actionIsCritical(action)) {
        uni.showModal({
            title: action,
            content: `确认对 ${task.value.currentNo} 执行“${action}”？`,
            success(result) {
                if (result.confirm) finishAction(action);
            }
        });
        return;
    }
    finishAction(action);
}

function finishAction(action: string) {
    if (!task.value) return;
    applyTaskAction(task.value, action, { note: reason.value || qualityNote.value });
    uni.showToast({ title: `${action}完成`, icon: 'none' });
    void loadData();
}

function simulateWeakNetwork() {
    if (!task.value) return;
    markTaskPending(task.value, task.value.primaryAction);
    uni.showToast({ title: '提交结果待确认', icon: 'none' });
    void loadData();
}

function runDriver(action: string, stationId?: string) {
    if (!driverOrder.value) return;
    uni.showModal({
        title: action,
        content: `确认执行“${action}”？`,
        success(result) {
            if (result.confirm && driverOrder.value) {
                applyDriverAction(driverOrder.value.id, action, stationId);
                uni.showToast({ title: `${action}完成`, icon: 'none' });
                void loadData();
            }
        }
    });
}

function openGoods(station: DriverStation) {
    const total = station.goods.reduce((sum, item) => sum + item.expected, 0);
    uni.showModal({
        title: `${station.name}配送明细`,
        content: `共 ${station.goods.length} 个商品，计划 ${total} 件。按商品、SKU和数量记录。`,
        showCancel: false
    });
}

function showReceipt() {
    if (!driverOrder.value) return;
    uni.showModal({
        title: '装车回执',
        content: `${driverOrder.value.handoverNo} · ${driverOrder.value.handoverStatus} · ${driverOrder.value.outboundNo}`,
        showCancel: false
    });
}

function confirmReturn() {
    uni.showModal({
        title: '发起回仓交接',
        content: '确认将退货带回仓库交接？',
        success(result) {
            if (result.confirm) uni.showToast({ title: '回仓交接已发起', icon: 'none' });
        }
    });
}

function createPurchase() {
    if (selectedPurchaseItems.value.length === 0) {
        uni.showToast({ title: '请先选择采购商品', icon: 'none' });
        return;
    }
    uni.showModal({
        title: '提交采购单',
        content: `${purchaseWarehouse.value} / ${purchaseDate.value} / ${selectedPurchaseItems.value.length} 个 SKU / ${selectedPurchaseQty.value} 件，确认提交审核？`,
        success(result) {
            if (result.confirm) {
                uni.showToast({ title: '采购单已提交审核', icon: 'none' });
            }
        }
    });
}

onLoad((query) => {
    id.value = typeof query.id === 'string' ? query.id : '';
    mode.value = typeof query.mode === 'string' ? query.mode : '';
    void loadData();
});

onShow(() => {
    void loadData();
});
</script>
