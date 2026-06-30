<template>
  <view class="page phone-shell inventory-page">
    <view class="warehouse-top">
      <view class="status-row">
        <text>14:49</text>
        <text>{{ profile.name }}</text>
      </view>
      <text class="eyebrow">库存查询</text>
      <text class="title">{{ profile.key === 'manager' ? '仓库商品与库存盘点' : 'SKU、批次、库位和库存状态' }}</text>
    </view>

    <view v-if="!allowed" class="section denied-card">
      <text class="section-title">当前角色不展示库存</text>
      <button class="primary" @click="goTasks">返回任务</button>
    </view>

    <template v-else>
      <view v-if="profile.key === 'manager'" class="section manager-inventory-tools">
        <view class="section-head">
          <view>
            <text class="section-title">主管库存工作台</text>
          </view>
        </view>
        <view class="quick-grid">
          <button class="quick-card" :class="{ active: inventoryMode === 'list' }" @click="inventoryMode = 'list'"><text>仓库商品</text></button>
          <button class="quick-card" :class="{ active: inventoryMode === 'create' }" @click="openStocktake"><text>新增盘点单</text></button>
        </view>
      </view>

      <view v-if="inventoryMode === 'create'" class="section stocktake-panel">
        <view class="section-head">
          <view>
            <text class="section-title">新增盘点单</text>
          </view>
          <text class="data-source-chip">{{ selectedStocktakeRows.length }} 个商品</text>
        </view>
        <view class="stocktake-list">
          <view v-for="item in filteredRows" :key="item.id" class="stocktake-row" :class="{ selected: isStocktakeSelected(item.id) }">
            <button class="stocktake-check" @click="toggleStocktake(item.id)">{{ isStocktakeSelected(item.id) ? '✓' : '+' }}</button>
            <view class="stocktake-info">
              <text>{{ item.title }}</text>
              <text>{{ item.sku }} · {{ item.batch }} · {{ item.location }}</text>
            </view>
            <view class="stocktake-qty">
              <text>账面 {{ item.inStock }}</text>
              <input :disabled="!isStocktakeSelected(item.id)" :value="stocktakeQty[item.id] ?? item.inStock" type="number" @input="setStocktakeQty(item.id, $event)" />
            </view>
          </view>
        </view>
        <button class="primary full-btn" :disabled="selectedStocktakeRows.length === 0" @click="submitStocktake">提交盘点单</button>
      </view>

      <view v-if="inventoryMode === 'list'" class="section inventory-summary">
        <view>
          <text>{{ filteredRows.length }}</text>
          <text>库存批次</text>
        </view>
        <view>
          <text>{{ waitPutaway }}</text>
          <text>待上架</text>
        </view>
        <view>
          <text>{{ waitConfirm }}</text>
          <text>待确认</text>
        </view>
      </view>

      <view v-if="inventoryMode === 'list'" class="filter-bar inventory-filter">
        <view class="search-box">
          <text>⌕</text>
          <input v-model="keyword" placeholder="搜索 SKU、批次、库区、库位" />
        </view>
        <picker mode="selector" :range="statusOptions" @change="setStatus">
          <view class="filter-chip">{{ statusFilter }}</view>
        </picker>
      </view>

      <view v-if="inventoryMode === 'list'" class="inventory-list">
        <view v-for="item in filteredRows" :key="item.id" class="inventory-card warehouse-stock-card">
          <view class="inventory-head">
            <view>
              <text class="sku-title">{{ item.title }}</text>
              <text class="subtle">{{ item.sku }} · {{ item.batch }}</text>
            </view>
            <text class="status-pill" :class="item.statusTone">{{ item.status }}</text>
          </view>
          <view class="location-line">{{ item.zone }} / {{ item.location }}</view>
          <view class="inventory-counts">
            <view>
              <text>{{ item.inStock }}</text>
              <text>在库</text>
            </view>
            <view>
              <text>{{ item.locked }}</text>
              <text>锁定</text>
            </view>
            <view>
              <text>{{ item.available }}</text>
              <text>可用</text>
            </view>
            <view>
              <text>{{ item.frozen }}</text>
              <text>冻结</text>
            </view>
          </view>
        </view>
        <view v-if="filteredRows.length === 0" class="empty-panel">
          <text class="section-title">暂无匹配库存</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">库存流水摘要</text>
        <view class="log-list">
          <text v-for="item in operations" :key="item">{{ item }}</text>
        </view>
      </view>
    </template>

    <WarehouseBottomNav active="stock" :role="profile.key" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import WarehouseBottomNav from '@/components/WarehouseBottomNav.vue';
import {
    canViewInventory,
    getCurrentRole,
    getRoleProfile,
    inventoryRows,
    loadWarehouseInventory,
    type InventoryRow,
    type RoleProfile
} from '@/stores/warehouse';

const profile = ref<RoleProfile>(getRoleProfile());
const allowed = ref(true);
const rows = ref<InventoryRow[]>(inventoryRows);
const operations = ref<string[]>([]);
const keyword = ref('');
const statusFilter = ref('全部');
const inventoryMode = ref<'list' | 'create'>('list');
const stocktakeQty = ref<Record<string, number>>({});
const selectedStocktakeIds = ref<string[]>([]);
const statusOptions = ['全部', '可售', '待上架', '待确认库存', '冻结', '锁定'];

const waitPutaway = computed(() => rows.value.filter((item) => item.status === '待上架').length);
const waitConfirm = computed(() => rows.value.filter((item) => item.status === '待确认库存').length);
const filteredRows = computed(() => {
    const q = keyword.value.trim().toLowerCase();
    return rows.value.filter((item) => {
        if (statusFilter.value !== '全部' && !statusMatches(item)) return false;
        if (!q) return true;
        return [item.title, item.sku, item.batch, item.zone, item.location, item.status].join(' ').toLowerCase().includes(q);
    });
});
const selectedStocktakeRows = computed(() => rows.value.filter((item) => selectedStocktakeIds.value.includes(item.id)));

async function loadData() {
    const role = getCurrentRole();
    profile.value = getRoleProfile(role);
    allowed.value = canViewInventory(role);
    if (!allowed.value) {
        rows.value = [];
        operations.value = [];
        return;
    }
    const dashboard = await loadWarehouseInventory();
    rows.value = dashboard.rows;
    operations.value = dashboard.operations;
}

function goTasks() {
    uni.switchTab({ url: '/pages/tasks/index' });
}

function setStatus(event: { detail: { value: number } }) {
    statusFilter.value = statusOptions[event.detail.value] || '全部';
}

function openStocktake() {
    inventoryMode.value = 'create';
    if (selectedStocktakeIds.value.length === 0) {
        selectedStocktakeIds.value = rows.value.slice(0, 3).map((item) => item.id);
        stocktakeQty.value = Object.fromEntries(rows.value.slice(0, 3).map((item) => [item.id, item.inStock]));
    }
}

function isStocktakeSelected(id: string) {
    return selectedStocktakeIds.value.includes(id);
}

function toggleStocktake(id: string) {
    if (isStocktakeSelected(id)) {
        selectedStocktakeIds.value = selectedStocktakeIds.value.filter((item) => item !== id);
        return;
    }
    selectedStocktakeIds.value = [...selectedStocktakeIds.value, id];
    const row = rows.value.find((item) => item.id === id);
    stocktakeQty.value = { ...stocktakeQty.value, [id]: row?.inStock || 0 };
}

function setStocktakeQty(id: string, event: Event) {
    const value = Number((event.target as HTMLInputElement).value || 0);
    stocktakeQty.value = { ...stocktakeQty.value, [id]: value };
}

function submitStocktake() {
    if (selectedStocktakeRows.value.length === 0) return;
    const no = `STK-${Date.now().toString().slice(-8)}`;
    operations.value = [
        `${no} 已提交，${selectedStocktakeRows.value.length} 个商品进入待确认库存。`,
        ...operations.value
    ].slice(0, 8);
    inventoryMode.value = 'list';
    selectedStocktakeIds.value = [];
    stocktakeQty.value = {};
    uni.showToast({ title: '盘点单已提交', icon: 'none' });
}

function statusMatches(item: InventoryRow) {
    if (statusFilter.value === '锁定') return item.locked > 0;
    if (statusFilter.value === '冻结') return item.frozen > 0 || item.status === '冻结';
    return item.status === statusFilter.value;
}

function toast(title: string) {
    uni.showToast({ title, icon: 'none' });
}

onShow(loadData);
</script>
