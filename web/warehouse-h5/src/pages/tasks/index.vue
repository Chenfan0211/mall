<template>
  <view class="page phone-shell tasks-page" :class="{ 'receiver-tasks-page': role === 'receiver' }">
    <view v-if="role === 'receiver'" class="receiver-task-top">
      <view class="status-row">
        <text>14:49</text>
        <text>WiFi 80%</text>
      </view>
      <view class="receiver-task-title-row">
        <view>
          <text class="receiver-task-title">收货员任务</text>
          <text class="receiver-task-sub">{{ profile.entity }}</text>
        </view>
        <text class="receiver-task-badge">{{ profile.badge }}</text>
      </view>
    </view>

    <view v-else class="warehouse-top">
      <view class="status-row">
        <text>14:49</text>
        <text>{{ profile.name }} · {{ profile.badge }}</text>
      </view>
      <text class="eyebrow">{{ role === 'driver' ? '配送单' : profile.name + '任务' }}</text>
      <text class="title">{{ profile.title }}</text>
      <text class="subtle">{{ profile.subtitle }}</text>
      <view class="seg-tabs">
        <button :class="{ active: tab === 'today' }" @click="tab = 'today'">今日任务</button>
        <button :class="{ active: tab === 'risk' }" @click="tab = 'risk'">异常优先</button>
        <button :class="{ active: tab === 'role' }" @click="tab = 'role'">按角色</button>
      </view>
    </view>

    <view v-if="role === 'receiver'" class="receiver-filter-panel">
      <picker class="receiver-status-picker-wrap" mode="selector" :range="receiverStatusOptions" @change="setStatus">
        <view class="receiver-status-row">
          <view class="receiver-status-label">
            <text class="receiver-status-dot"></text>
            <text>单据状态</text>
          </view>
          <view class="receiver-status-picker">
            <text>{{ statusFilter }}</text>
            <text class="receiver-status-arrow">⌄</text>
          </view>
        </view>
      </picker>
      <view class="receiver-filter-row">
        <view class="receiver-search-box">
          <text>⌕</text>
          <input v-model="keyword" placeholder="搜索任务单、商品、SKU" />
        </view>
        <picker mode="date" @change="setDate">
          <view class="receiver-date-chip">
            <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
              <path d="M8 2v4" />
              <path d="M16 2v4" />
              <path d="M3 9h18" />
              <path d="M5 5h14a2 2 0 0 1 2 2v13H3V7a2 2 0 0 1 2-2Z" />
            </svg>
            <text>{{ receiverDateLabel }}</text>
          </view>
        </picker>
      </view>
    </view>

    <view v-else class="filter-bar">
      <view class="search-box">
        <text>⌕</text>
        <input v-model="keyword" placeholder="搜索任务单、商品、SKU、库位、批次" />
      </view>
      <picker mode="date" @change="setDate">
        <view class="filter-chip date-chip">
          <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M8 2v4" />
            <path d="M16 2v4" />
            <path d="M3 9h18" />
            <path d="M5 5h14a2 2 0 0 1 2 2v13H3V7a2 2 0 0 1 2-2Z" />
          </svg>
          <text>{{ dateLabel }}</text>
        </view>
      </picker>
      <picker mode="selector" :range="statusOptions" @change="setStatus">
        <view class="filter-chip">{{ statusFilter }}</view>
      </picker>
    </view>

    <view v-if="role === 'driver'" class="driver-tabs">
      <button :class="{ active: driverTab === 'delivery' }" @click="driverTab = 'delivery'">配送单</button>
      <button :class="{ active: driverTab === 'return' }" @click="driverTab = 'return'">退货单</button>
    </view>

    <view v-if="role === 'driver' && driverTab === 'delivery'" class="page-list warehouse-order-list">
      <view v-for="order in filteredDriverOrders" :key="order.id" class="driver-order-card warehouse-task-card" @click="openDriver(order.id)">
        <view class="task-head">
          <view>
            <text class="task-title">{{ order.routeName }}</text>
            <text class="task-sub">{{ order.id }} · {{ order.vehicle }}</text>
          </view>
          <text class="status-pill" :class="order.statusTone">{{ order.status }}</text>
        </view>
        <view class="driver-grid">
          <view><text>{{ order.stationCount }}</text><text>门店</text></view>
          <view><text>{{ order.cargoCount }}</text><text>货物</text></view>
          <view><text>{{ order.returnCount }}</text><text>退货</text></view>
          <view><text>{{ order.distance }}</text><text>路程</text></view>
        </view>
        <view class="action-row">
          <button class="plain" @click.stop="openDriver(order.id)">配送门店</button>
          <button class="primary" @click.stop="runDriver(order.id, order.status === '待发车' ? '确认出发' : '发起回仓交接')">
            {{ order.status === '待发车' ? '确认出发' : '发起回仓交接' }}
          </button>
        </view>
      </view>
      <view v-if="filteredDriverOrders.length === 0" class="empty-panel">
        <text class="section-title">当前状态暂无配送单</text>
      </view>
    </view>

    <view v-else-if="role === 'driver'" class="page-list warehouse-order-list">
      <view v-for="order in filteredReturnOrders" :key="order.id" class="driver-order-card warehouse-task-card">
        <view class="task-head">
          <view>
            <text class="task-title">{{ order.station }}</text>
            <text class="task-sub">{{ order.id }} · {{ order.routeName }}</text>
          </view>
          <text class="status-pill" :class="order.statusTone">{{ order.status }}</text>
        </view>
        <view class="task-meta">
          <text>{{ order.returnQty }} 件退货</text>
          <text>{{ order.deliveryDate }}</text>
          <text>{{ order.contact }}</text>
        </view>
        <text class="task-note">{{ order.address }}</text>
        <button class="primary full-btn" @click="openReturn(order.id)">退货详情</button>
      </view>
      <view v-if="filteredReturnOrders.length === 0" class="empty-panel">
        <text class="section-title">当前状态暂无退货单</text>
      </view>
    </view>

    <view v-else-if="role === 'receiver'" class="receiver-task-list">
      <view v-for="task in filteredTasks" :key="task.id" class="receiver-task-card">
        <view class="receiver-card-head">
          <view>
            <text class="receiver-card-no">{{ task.currentNo }}</text>
            <text class="receiver-card-route">{{ task.type }} · {{ task.route || task.sourceNo }} → {{ task.warehouse }}</text>
          </view>
          <text class="receiver-status-pill" :class="task.statusTone">{{ task.status }}</text>
        </view>
        <view class="receiver-card-stats">
          <view><text>{{ task.plannedQty }}</text><text>商品件数</text></view>
          <view><text>{{ task.actualQty }}</text><text>已处理</text></view>
          <view><text>{{ task.skuCount }}</text><text>SKU</text></view>
        </view>
        <view class="receiver-card-meta">
          <view>
            <text>仓库库区</text>
            <text>{{ task.warehouse }} / {{ task.area }}</text>
          </view>
          <view>
            <text>截止时间</text>
            <text>{{ task.due }} · {{ task.priority }}</text>
          </view>
        </view>
        <text v-if="task.submitPending" class="pending-strip">提交结果待确认：{{ task.submitPendingAction }}</text>
        <view class="receiver-card-actions">
          <button class="receiver-detail-btn action-detail" @click.stop="openDetail(task.id)">
            <text>☰</text>
            <text>查看详情</text>
          </button>
          <button v-if="showReceiverPrimary(task)" class="receiver-primary-btn" :class="receiverPrimaryActionClass(task.primaryAction)" @click.stop="openReceiverWork(task)">
            <text>✓</text>
            <text>{{ task.primaryAction }}</text>
          </button>
        </view>
      </view>
      <view v-if="filteredTasks.length === 0" class="empty-panel">
        <text class="section-title">暂无当前收货单</text>
      </view>
    </view>

    <view v-else class="task-list page-list warehouse-order-list">
      <view v-for="task in filteredTasks" :key="task.id" class="task-card warehouse-task-card" @click="openDetail(task.id)">
        <view class="task-head">
          <view>
            <text class="task-title">{{ task.title }}</text>
            <text class="task-sub">{{ task.currentNo }} · 来源 {{ task.sourceNo }}</text>
          </view>
          <text class="status-pill" :class="task.statusTone">{{ task.status }}</text>
        </view>
        <view class="task-summary">
          <view><text>{{ task.plannedQty }}</text><text>计划数量</text></view>
          <view><text>{{ task.actualQty }}</text><text>实际数量</text></view>
          <view><text>{{ Math.max(0, task.plannedQty - task.actualQty) }}</text><text>差异数量</text></view>
        </view>
        <view class="task-meta">
          <text>{{ task.warehouse }}</text>
          <text>{{ task.route || task.location }}</text>
          <text>{{ task.skuCount }} SKU</text>
        </view>
        <text v-if="task.submitPending" class="pending-strip">提交结果待确认：{{ task.submitPendingAction }}</text>
        <text class="task-note">{{ task.diffNote }}</text>
        <view class="action-row">
          <button class="plain" @click.stop="openDetail(task.id)">查看详情</button>
          <button class="primary" @click.stop="runAction(task, task.primaryAction)">{{ task.primaryAction }}</button>
        </view>
      </view>
      <view v-if="filteredTasks.length === 0" class="empty-panel">
        <text class="section-title">暂无当前任务</text>
      </view>
    </view>

    <WarehouseBottomNav :active="role === 'driver' && driverTab === 'return' ? 'stock' : 'tasks'" :role="role" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import WarehouseBottomNav from '@/components/WarehouseBottomNav.vue';
import {
    actionIsCritical,
    applyDriverAction,
    applyTaskAction,
    consumePendingReceiverStatusFilter,
    consumePendingTarget,
    getCurrentRole,
    getRoleProfile,
    loadWarehouseDashboard,
    sortWarehouseTasks,
    type RoleProfile,
    type WarehouseDriverOrder,
    type WarehouseReturnOrder,
    type WarehouseRole,
    type WarehouseTask
} from '@/stores/warehouse';

const DRIVER_TAB_KEY = 'mall_warehouse_h5_driver_tab';

const tab = ref<'today' | 'risk' | 'role'>('today');
const keyword = ref('');
const statusFilter = ref('全部');
const statusOptions = ['全部', '待处理', '处理中', '待确认', '已完成', '异常'];
const receiverStatusOptions = ['全部', '待接单', '待收货', '收货中', '待确认收货', '已收货', '质检异常'];
const selectedDate = ref('all');
const driverTab = ref<'delivery' | 'return'>('delivery');
const role = ref<WarehouseRole>(getCurrentRole());
const profile = ref<RoleProfile>(getRoleProfile());
const tasks = ref<WarehouseTask[]>([]);
const driverOrders = ref<WarehouseDriverOrder[]>([]);
const returnOrders = ref<WarehouseReturnOrder[]>([]);
const dateLabel = computed(() => selectedDate.value === 'all' ? '全部日期' : selectedDate.value);
const receiverDateLabel = computed(() => selectedDate.value === 'all' ? '今天' : formatMonthDay(selectedDate.value));

const filteredTasks = computed(() => {
    const q = keyword.value.trim().toLowerCase();
    return sortWarehouseTasks(tasks.value).filter((task) => {
        if (tab.value === 'risk' && task.statusTone !== 'red' && !/差异|异常|缺货/.test(task.diffNote)) return false;
        if (tab.value === 'role' && task.role !== role.value && role.value !== 'manager') return false;
        if (!dateMatches(task.date)) return false;
        if (!statusMatches(task.status, statusFilter.value, task.statusTone)) return false;
        if (!q) return true;
        return [task.title, task.currentNo, task.sourceNo, task.route, task.location, ...task.items.map((item) => `${item.title} ${item.sku} ${item.batch} ${item.barcode}`)].join(' ').toLowerCase().includes(q);
    });
});

const filteredDriverOrders = computed(() => {
    const q = keyword.value.trim().toLowerCase();
    return driverOrders.value.filter((order) => {
        if (!dateMatches(order.deliveryDate)) return false;
        if (!statusMatches(order.status, statusFilter.value, order.statusTone)) return false;
        if (!q) return true;
        return [order.id, order.routeName, order.vehicle, order.warehouse].join(' ').toLowerCase().includes(q);
    });
});

const filteredReturnOrders = computed(() => {
    const q = keyword.value.trim().toLowerCase();
    return returnOrders.value.filter((order) => {
        if (!dateMatches(order.deliveryDate)) return false;
        if (!statusMatches(order.status, statusFilter.value, order.statusTone)) return false;
        if (!q) return true;
        return [order.id, order.routeName, order.station, order.contact].join(' ').toLowerCase().includes(q);
    });
});

async function loadData() {
    role.value = getCurrentRole();
    const savedDriverTab = uni.getStorageSync(DRIVER_TAB_KEY);
    if (role.value === 'driver' && (savedDriverTab === 'delivery' || savedDriverTab === 'return')) {
        driverTab.value = savedDriverTab;
    }
    const dashboard = await loadWarehouseDashboard(role.value);
    profile.value = dashboard.profile;
    tasks.value = dashboard.tasks;
    driverOrders.value = dashboard.driverOrders;
    returnOrders.value = dashboard.returnOrders;
    const pendingReceiverStatus = consumePendingReceiverStatusFilter();
    if (role.value === 'receiver' && pendingReceiverStatus) {
        statusFilter.value = pendingReceiverStatus;
        selectedDate.value = 'all';
        return;
    }
    const pending = consumePendingTarget();
    if (role.value === 'receiver' && applyReceiverPendingFilter(pending.target)) {
        return;
    }
    if (pending.taskId) {
        uni.navigateTo({ url: `/pages/tasks/detail?id=${pending.taskId}&mode=${pending.target || ''}` });
    }
}

uni.$on('warehouse:driverTaskTab', (value: unknown) => {
    if (value === 'delivery' || value === 'return') {
        driverTab.value = value;
        uni.setStorageSync(DRIVER_TAB_KEY, value);
    }
});

uni.$on('warehouse:receiverStatusFilter', (value: unknown) => {
    if (typeof value === 'string') {
        applyReceiverPendingFilter(value);
    }
});

function setStatus(event: { detail: { value: number } }) {
    const options = role.value === 'receiver' ? receiverStatusOptions : statusOptions;
    statusFilter.value = options[event.detail.value] || '全部';
}

function setDate(event: { detail: { value: string } }) {
    selectedDate.value = event.detail.value || 'all';
}

function dateMatches(value?: string) {
    if (selectedDate.value === 'all') return true;
    if (!value) return true;
    return value.startsWith(selectedDate.value);
}

function statusMatches(status: string, filter: string, tone: string) {
    if (filter === '全部') return true;
    if (filter === '待接单') return status === '待接单';
    if (filter === '待收货') return status === '待收货';
    if (filter === '收货中') return status === '收货中';
    if (filter === '待确认收货') return /收货中|待确认收货/.test(status);
    if (filter === '已收货') return status === '已收货';
    if (filter === '质检异常') return /质检异常|异常/.test(status) || tone === 'red';
    if (filter === '异常') return tone === 'red' || /异常|缺货|提交结果待确认/.test(status);
    if (filter === '待处理') return /待|草稿/.test(status) && !/待确认/.test(status);
    if (filter === '处理中') return /中|配送中|收货中|拣货中/.test(status);
    if (filter === '待确认') return /待确认|提交结果待确认|已装车待出库|待回仓/.test(status);
    if (filter === '已完成') return /已完成|已收货|已上架|已出库|已拣货|已关闭/.test(status);
    return true;
}

function applyReceiverPendingFilter(target?: string) {
    if (!target) return false;
    if (receiverStatusOptions.includes(target)) {
        statusFilter.value = target;
        selectedDate.value = 'all';
        return true;
    }
    if (target === 'receiveArea') {
        statusFilter.value = '待接单';
        selectedDate.value = 'all';
        return true;
    }
    if (target === 'receiveScan') {
        statusFilter.value = '待收货';
        selectedDate.value = 'all';
        return true;
    }
    return false;
}

function formatMonthDay(value: string) {
    const match = value.match(/^\d{4}-(\d{2})-(\d{2})/);
    if (!match) return value;
    return `${Number(match[1])}月${Number(match[2])}日`;
}

function openDetail(id: string) {
    const targetMode = role.value === 'manager' ? '&mode=managerPutaway' : role.value === 'receiver' ? '&mode=receiverGoods' : '';
    uni.navigateTo({ url: `/pages/tasks/detail?id=${id}${targetMode}` });
}

function openReceiverWork(task: WarehouseTask) {
    const mode = /接单/.test(task.primaryAction) ? 'receiveArea' : 'receiveScan';
    uni.navigateTo({ url: `/pages/tasks/detail?id=${task.id}&mode=${mode}` });
}

function openDriver(id: string) {
    uni.navigateTo({ url: `/pages/tasks/detail?id=${id}&mode=driverDelivery` });
}

function openReturn(id: string) {
    uni.navigateTo({ url: `/pages/tasks/detail?id=${id}&mode=driverReturn` });
}

function showReceiverPrimary(task: WarehouseTask) {
    return !/查看|已关闭|已完成|已收货/.test(`${task.primaryAction}${task.status}`);
}

function receiverPrimaryActionClass(action: string) {
    if (/接单/.test(action)) return 'action-accept';
    if (/确认收货|确认入库/.test(action)) return 'action-confirm';
    return 'action-default';
}

function runAction(task: WarehouseTask, action: string) {
    if (/查看|到仓跟踪|调整数量/.test(action)) {
        openDetail(task.id);
        return;
    }
    if (actionIsCritical(action)) {
        uni.showModal({
            title: action,
            content: `确认对 ${task.currentNo} 执行“${action}”？`,
            success(result) {
                if (result.confirm) finishAction(task, action);
            }
        });
        return;
    }
    finishAction(task, action);
}

function finishAction(task: WarehouseTask, action: string) {
    applyTaskAction(task, action);
    uni.showToast({ title: `${action}完成`, icon: 'none' });
    loadData();
}

function runDriver(orderId: string, action: string) {
    uni.showModal({
        title: action,
        content: `确认对 ${orderId} 执行“${action}”？`,
        success(result) {
            if (result.confirm) {
                applyDriverAction(orderId, action);
                uni.showToast({ title: `${action}完成`, icon: 'none' });
                loadData();
            }
        }
    });
}

onShow(loadData);
</script>
