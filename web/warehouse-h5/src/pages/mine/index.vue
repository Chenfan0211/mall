<template>
  <view class="page phone-shell mine-page">
    <view class="warehouse-top">
      <view class="status-row">
        <button v-if="isRecordMode" class="back-btn record-back" @click="closeRecord">‹</button>
        <text v-else>14:49</text>
        <text>{{ profile.badge }}</text>
      </view>
      <text class="eyebrow">{{ isDriver ? 'WORK STATS' : 'MY WMS' }}</text>
      <text class="title">{{ pageTitle }}</text>
      <view v-if="!isRecordMode" class="profile-row">
        <view class="avatar">{{ profile.name.slice(0, 1) }}</view>
        <view>
          <text class="profile-name">{{ profile.name }}</text>
          <text class="profile-line">{{ profile.account }} / {{ profile.warehouse }}</text>
        </view>
      </view>
      <view v-else class="record-headline">
        <text class="status-pill green">{{ profile.name }}</text>
      </view>
    </view>

    <template v-if="isDriver">
      <view class="filter-bar driver-stats-filter">
        <view class="search-box">
          <text>⌕</text>
          <input v-model="driverKeyword" placeholder="搜索线路名称" />
        </view>
        <picker mode="date" @change="setDriverDate">
          <view class="filter-chip">{{ driverDate || '全部日期' }}</view>
        </picker>
      </view>

      <view class="driver-stats-summary">
        <view v-for="item in stats" :key="item.label">
          <text>{{ item.label }}</text>
          <text>{{ item.value }}</text>
        </view>
      </view>

      <view class="driver-list">
        <view v-for="row in filteredDriverOrders" :key="row.id" class="driver-order-card driver-stats-card">
          <view class="driver-card-head">
            <view>
              <text class="driver-route-title">{{ row.routeName }}</text>
              <text class="driver-route-sub">{{ row.deliveryDate }} / {{ row.vehicle }}</text>
            </view>
            <text class="status-pill" :class="row.statusTone">{{ row.status }}</text>
          </view>
          <view class="driver-stats-meta">
            <view><text>装车量</text><text>{{ row.cargoCount }}件</text></view>
            <view><text>配送点</text><text>{{ row.stationCount }}个</text></view>
            <view><text>配送件数</text><text>{{ row.cargoCount }}</text></view>
            <view><text>退货取回</text><text>{{ row.returnCount }}件</text></view>
            <view><text>线路距离</text><text>{{ row.distance }}</text></view>
            <view><text>完成状态</text><text>{{ row.status }}</text></view>
          </view>
        </view>
        <view v-if="filteredDriverOrders.length === 0" class="empty-panel">
          <text class="section-title">暂无匹配作业统计</text>
        </view>
      </view>
    </template>

    <template v-else-if="recordMode === 'receive'">
      <view class="section warehouse-profile-card warehouse-receive-record-card">
        <view class="warehouse-section-head">
          <view>
            <text class="section-title">收货历史数据</text>
          </view>
        </view>

        <view class="warehouse-record-tools warehouse-receive-record-tools">
          <view class="warehouse-record-search">
            <text>⌕</text>
            <input v-model="receiveKeyword" placeholder="入库单号/商品/条码" />
          </view>
          <picker mode="date" @change="setReceiveDate">
            <view class="warehouse-record-filter">{{ receiveDate || '收货日期' }}</view>
          </picker>
          <picker :range="receiveWarehouseOptions" @change="setReceiveWarehouse">
            <view class="warehouse-record-filter">{{ receiveWarehouse || '全部仓库' }}</view>
          </picker>
        </view>

        <view class="warehouse-record-list">
          <button v-for="row in filteredReceiveRecords" :key="row.id" class="warehouse-record-item" @click="openTask(row)">
            <view class="warehouse-record-thumb" :class="firstImageTone(row)">{{ firstTitle(row).slice(0, 1) || '收' }}</view>
            <view class="warehouse-record-main">
              <view class="warehouse-record-title-row">
                <text>{{ row.currentNo }}</text>
                <text class="status-pill" :class="row.statusTone">{{ row.status }}</text>
              </view>
              <text class="warehouse-record-meta">{{ row.warehouse }} / {{ row.area }} · {{ recordDate(row) }}</text>
              <view class="warehouse-receive-counts">
                <view><text>应收</text><text>{{ taskQty(row) }}</text></view>
                <view class="received"><text>已收</text><text>{{ taskDone(row) }}</text></view>
                <view class="diff" :class="{ done: taskDiff(row) === 0 }"><text>差异</text><text>{{ taskDiff(row) }}</text></view>
              </view>
            </view>
          </button>
          <view v-if="filteredReceiveRecords.length === 0" class="empty-panel record-empty">
            <text class="section-title">暂无匹配的收货记录</text>
          </view>
        </view>
      </view>
    </template>

    <template v-else-if="recordMode === 'pick'">
      <view class="section warehouse-profile-card warehouse-pick-record-card">
        <view class="warehouse-section-head">
          <view>
            <text class="section-title">拣货记录查询</text>
          </view>
        </view>

        <view class="warehouse-record-tools">
          <view class="warehouse-record-search">
            <text>⌕</text>
            <input v-model="pickKeyword" placeholder="输入拣货单号/SKU" />
          </view>
          <picker mode="date" @change="setPickDate">
            <view class="warehouse-record-filter">{{ pickDate || '拣货日期' }}</view>
          </picker>
        </view>

        <view class="warehouse-record-list">
          <button v-for="row in filteredPickRecords" :key="row.id" class="warehouse-record-item" @click="openTask(row)">
            <view class="warehouse-record-thumb" :class="firstImageTone(row)">{{ firstTitle(row).slice(0, 1) || '拣' }}</view>
            <view class="warehouse-record-main">
              <view class="warehouse-record-title-row">
                <text>{{ row.currentNo }}</text>
                <text class="status-pill" :class="row.statusTone">{{ row.status }}</text>
              </view>
              <text class="warehouse-record-meta">{{ row.type }} / {{ row.route || row.area }}</text>
              <text class="warehouse-pick-line">已拣：{{ taskDone(row) }} / 应拣：{{ taskQty(row) }} · {{ recordDate(row) }}</text>
            </view>
          </button>
          <view v-if="filteredPickRecords.length === 0" class="empty-panel record-empty">
            <text class="section-title">暂无匹配的拣货记录</text>
          </view>
        </view>
      </view>
    </template>

    <template v-else>
      <view v-if="canOpenReceiveRecord" class="section warehouse-profile-card record-entry-card">
        <button class="warehouse-profile-link" @click="openReceiveRecord">
          <view class="warehouse-profile-link-icon record">收</view>
          <view class="warehouse-profile-link-copy">
            <text>收货记录</text>
          </view>
          <text class="warehouse-profile-link-arrow">›</text>
        </button>
      </view>

      <view v-if="canOpenPickRecord" class="section warehouse-profile-card record-entry-card">
        <button class="warehouse-profile-link" @click="openPickRecord">
          <view class="warehouse-profile-link-icon pick">拣</view>
          <view class="warehouse-profile-link-copy">
            <text>拣货记录</text>
          </view>
          <text class="warehouse-profile-link-arrow">›</text>
        </button>
      </view>

      <view class="section warehouse-profile-card">
        <text class="section-title">今日作业</text>
        <view class="stat-grid">
          <view v-for="item in stats" :key="item.label">
            <text>{{ item.value }}</text>
            <text>{{ item.label }}</text>
          </view>
        </view>
      </view>

      <view class="section warehouse-profile-card">
        <text class="section-title">最近操作</text>
        <view class="log-list">
          <text v-for="item in operations" :key="item">{{ item }}</text>
        </view>
      </view>
    </template>

    <view v-if="!isRecordMode" class="section warehouse-profile-card record-entry-card">
      <button class="warehouse-profile-link" @click="goRoles">
        <view class="warehouse-profile-link-icon switch">换</view>
        <view class="warehouse-profile-link-copy">
          <text>切换角色</text>
        </view>
        <text class="warehouse-profile-link-arrow">›</text>
      </button>
    </view>

    <view v-if="!isRecordMode" class="section warehouse-profile-card">
      <button class="danger logout-btn" @click="logout">退出登录</button>
    </view>

    <WarehouseBottomNav active="profile" :role="profile.key" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import WarehouseBottomNav from '@/components/WarehouseBottomNav.vue';
import {
    getCurrentRole,
    getRoleProfile,
    loadWarehouseDashboard,
    logoutWarehouse,
    type WarehouseDriverOrder,
    type WarehouseTask,
    type RoleProfile,
    type WarehouseDashboard,
    type WorkMetric
} from '@/stores/warehouse';

const profile = ref<RoleProfile>(getRoleProfile());
const stats = ref<WorkMetric[]>([]);
const operations = ref<string[]>([]);
const driverOrders = ref<WarehouseDriverOrder[]>([]);
const tasks = ref<WarehouseTask[]>([]);
const driverKeyword = ref('');
const driverDate = ref('');
const recordMode = ref<'profile' | 'receive' | 'pick'>('profile');
const receiveKeyword = ref('');
const receiveDate = ref('');
const receiveWarehouse = ref('');
const pickKeyword = ref('');
const pickDate = ref('');

const isDriver = computed(() => profile.value.key === 'driver');
const isRecordMode = computed(() => recordMode.value !== 'profile');
const canOpenReceiveRecord = computed(() => profile.value.key === 'receiver');
const canOpenPickRecord = computed(() => profile.value.key === 'picker');
const pageTitle = computed(() => {
    if (isDriver.value) return '作业统计';
    if (recordMode.value === 'receive') return '收货记录';
    if (recordMode.value === 'pick') return '拣货记录';
    return '我的';
});
const receiveWarehouseOptions = computed(() => ['全部仓库', ...Array.from(new Set(tasks.value.map((item) => item.warehouse).filter(Boolean)))]);
const filteredReceiveRecords = computed(() => filterReceiveRecords(tasks.value));
const filteredPickRecords = computed(() => filterPickRecords(tasks.value));
const filteredDriverOrders = computed(() => {
    const q = driverKeyword.value.trim().toLowerCase();
    return driverOrders.value.filter((item) => {
        if (driverDate.value && item.deliveryDate !== driverDate.value) return false;
        if (!q) return true;
        return [item.routeName, item.id, item.vehicle, item.status].join(' ').toLowerCase().includes(q);
    });
});

async function loadData() {
    const role = getCurrentRole();
    const dashboard = await loadWarehouseDashboard(role);
    profile.value = dashboard.profile;
    if ((recordMode.value === 'receive' && role !== 'receiver') || (recordMode.value === 'pick' && role !== 'picker')) {
        recordMode.value = 'profile';
    }
    uni.setNavigationBarTitle({ title: role === 'driver' ? '作业统计' : pageTitle.value });
    operations.value = dashboard.operations.slice(0, 6);
    driverOrders.value = dashboard.driverOrders;
    tasks.value = dashboard.tasks;
    stats.value = role === 'driver' ? buildDriverStats(dashboard) : buildRoleStats(dashboard);
}

function buildRoleStats(dashboard: WarehouseDashboard): WorkMetric[] {
    return dashboard.kpis.slice(0, 6).map((item) => ({
        label: item.label,
        value: item.value
    }));
}

function buildDriverStats(dashboard: WarehouseDashboard): WorkMetric[] {
    const orders = dashboard.driverOrders;
    const returns = dashboard.returnOrders;
    const stationCount = orders.reduce((sum, item) => sum + item.stationCount, 0);
    const cargoCount = orders.reduce((sum, item) => sum + item.cargoCount, 0);
    const returnCount = returns.reduce((sum, item) => sum + item.returnQty, 0);
    const timeoutCount = orders.reduce((sum, item) => sum + item.stations.filter((station) => station.status === '无法送达').length, 0);
    const doneCount = orders.filter((item) => item.status === '已完成' || item.status === '已回仓').length;
    const loadedCount = orders.reduce((sum, item) => sum + item.cargoCount, 0);
    return [
        { label: '线路数', value: orders.length },
        { label: '超时任务', value: timeoutCount },
        { label: '装车量', value: loadedCount },
        { label: '配送点', value: stationCount },
        { label: '配送件数', value: cargoCount },
        { label: '退货取回', value: returnCount },
        { label: '完成状态', value: `${doneCount}/${orders.length || 0}` }
    ];
}

function setDriverDate(event: { detail: { value: string } }) {
    driverDate.value = event.detail.value || '';
}

function setReceiveDate(event: { detail: { value: string } }) {
    receiveDate.value = event.detail.value || '';
}

function setPickDate(event: { detail: { value: string } }) {
    pickDate.value = event.detail.value || '';
}

function setReceiveWarehouse(event: { detail: { value: number | string } }) {
    const index = Number(event.detail.value || 0);
    receiveWarehouse.value = receiveWarehouseOptions.value[index] === '全部仓库' ? '' : receiveWarehouseOptions.value[index] || '';
}

function openReceiveRecord() {
    recordMode.value = 'receive';
    uni.setNavigationBarTitle({ title: '收货记录' });
    uni.showToast({ title: '已打开收货记录', icon: 'none' });
}

function openPickRecord() {
    recordMode.value = 'pick';
    uni.setNavigationBarTitle({ title: '拣货记录' });
    uni.showToast({ title: '已打开拣货记录', icon: 'none' });
}

function closeRecord() {
    recordMode.value = 'profile';
    uni.setNavigationBarTitle({ title: '我的' });
}

function openTask(row: WarehouseTask) {
    uni.navigateTo({ url: `/pages/tasks/detail?id=${encodeURIComponent(row.id || row.currentNo)}` });
}

function goRoles() {
    uni.navigateTo({ url: '/pages/roles/index' });
}

function filterReceiveRecords(rows: WarehouseTask[]) {
    const q = receiveKeyword.value.trim().toLowerCase();
    return rows.filter((row) => {
        if (receiveDate.value && recordDate(row) !== receiveDate.value) return false;
        if (receiveWarehouse.value && row.warehouse !== receiveWarehouse.value) return false;
        return !q || recordSearchText(row).includes(q);
    });
}

function filterPickRecords(rows: WarehouseTask[]) {
    const q = pickKeyword.value.trim().toLowerCase();
    return rows.filter((row) => {
        if (pickDate.value && recordDate(row) !== pickDate.value) return false;
        return !q || recordSearchText(row).includes(q);
    });
}

function recordSearchText(row: WarehouseTask) {
    return [
        row.id,
        row.currentNo,
        row.sourceNo,
        row.title,
        row.type,
        row.status,
        row.route,
        row.warehouse,
        row.area,
        ...row.items.flatMap((item) => [item.title, item.sku, item.batch, item.barcode, item.location])
    ].filter(Boolean).join(' ').toLowerCase();
}

function recordDate(row: WarehouseTask) {
    return row.date || '2026-06-10';
}

function taskQty(row: WarehouseTask) {
    return Number(row.plannedQty || row.items.reduce((sum, item) => sum + Number(item.planQty || 0), 0));
}

function taskDone(row: WarehouseTask) {
    return Number(row.actualQty || row.items.reduce((sum, item) => sum + Number(item.actualQty || 0), 0));
}

function taskDiff(row: WarehouseTask) {
    return Math.max(0, taskQty(row) - taskDone(row));
}

function firstTitle(row: WarehouseTask) {
    return row.items[0]?.title || row.title || '';
}

function firstImageTone(row: WarehouseTask) {
    return row.items[0]?.imageTone || 'green';
}

function logout() {
    logoutWarehouse();
    uni.reLaunch({ url: '/pages/login/index' });
}

onShow(loadData);
</script>
