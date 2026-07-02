<template>
  <view class="page phone-shell workbench-page" :class="{ 'manager-home-page': role === 'manager' }">
    <template v-if="role === 'manager'">
      <view class="manager-top">
        <view class="status-row manager-status">
          <text>14:49</text>
          <text>WiFi 80%</text>
        </view>
        <view class="manager-top-title">
          <view></view>
          <text>仓库主管</text>
          <text class="manager-capsule">· ·</text>
        </view>
        <view class="manager-home-hero">
          <view class="manager-avatar">仓</view>
          <view>
            <view class="manager-user-name">
              <text>{{ profile.account }}</text>
              <text class="manager-role-chip">中心仓-主管</text>
            </view>
            <text class="manager-user-line">仓库：{{ profile.warehouse }}</text>
            <text class="manager-user-line">ID: 266</text>
          </view>
        </view>
        <text class="manager-login-time">上次登录时间：{{ loginTime }}</text>
      </view>

      <view class="manager-home-actions">
        <button v-for="action in profile.quicks" :key="action.label" class="manager-action-card" @click="handleQuick(action.target)">
          <view class="manager-action-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M5 5h14v14H5z" />
              <path d="M8 9h8" />
              <path d="M8 13h8" />
              <path d="M8 17h5" />
            </svg>
          </view>
          <text>{{ action.label }}</text>
        </button>
      </view>

      <view class="manager-status-overview">
        <text class="section-title">任务状态总览</text>
        <view class="manager-status-grid">
          <view v-for="item in managerStatusRows" :key="item.label">
            <text>{{ item.label }}</text>
            <text>{{ item.value }}</text>
          </view>
        </view>
      </view>

      <view class="warehouse-section">
        <view class="warehouse-section-head">
          <text class="section-title">全员最近操作记录</text>
        </view>
        <view class="log-list">
          <text v-for="item in operations" :key="item">{{ item }}</text>
        </view>
      </view>

      <WarehouseBottomNav active="home" :role="role" />
    </template>

    <template v-else>
      <view class="warehouse-work-head">
        <view class="status-row">
          <text>14:49</text>
          <text>WiFi 80%</text>
        </view>
        <view class="warehouse-work-title">
          <view>
            <text class="title">{{ profile.name }}工作台</text>
            <text class="subtle">{{ profile.entity }}</text>
          </view>
          <text class="role-badge">{{ profile.badge }}</text>
        </view>
      </view>

      <view class="warehouse-main">
        <view class="warehouse-kpis" :class="{ six: kpis.length > 3 }">
          <button v-for="item in kpis" :key="item.label" class="warehouse-kpi" @click="goKpi(item.target)">
            <text>{{ item.value }}</text>
            <text>{{ item.label }}</text>
          </button>
        </view>

        <view class="warehouse-section">
          <view class="warehouse-section-head">
            <text class="section-title">今日待办</text>
          </view>
          <view v-if="role === 'receiver'" class="receiver-todo-summary">
            <button v-for="item in receiverTodoSummary" :key="item.label" class="receiver-todo-card" @click="openReceiverSummary(item)">
              <text class="receiver-todo-label">{{ item.label }}</text>
              <text class="receiver-todo-desc">
                <text class="receiver-todo-count">{{ item.value }}</text>
                <text>{{ receiverTodoSummaryText(item) }}</text>
              </text>
            </button>
          </view>
          <view v-else class="warehouse-task-list">
            <button v-for="task in todoTasks" :key="task.id" class="warehouse-task warehouse-home-task-card" @click="openTodo(task.id)">
              <view class="home-task-head">
                <view>
                  <text class="warehouse-task-title">{{ task.title }}</text>
                  <text class="warehouse-task-desc">{{ task.currentNo }} · 来源 {{ task.sourceNo }}</text>
                </view>
                <text class="status-pill" :class="task.statusTone">{{ task.status }}</text>
              </view>
              <view class="home-task-meta">
                <text>{{ task.warehouse }}</text>
                <text>{{ task.route || task.location }}</text>
                <text>{{ task.skuCount }} SKU</text>
              </view>
              <text class="task-note">{{ task.diffNote }}</text>
              <view class="home-task-foot">
                <text>{{ task.plannedQty }} / {{ task.actualQty }} 件</text>
                <text class="home-task-action">{{ task.primaryAction }}</text>
              </view>
            </button>
            <button
              v-if="todoTasks.length === 0"
              v-for="order in homeDriverOrders"
              :key="order.id"
              class="warehouse-task warehouse-home-task-card"
              @click="openDriverOrder(order.id)"
            >
              <view class="home-task-head">
                <view>
                  <text class="warehouse-task-title">{{ order.routeName }}</text>
                  <text class="warehouse-task-desc">{{ order.id }} · {{ order.vehicle }}</text>
                </view>
                <text class="status-pill" :class="order.statusTone">{{ order.status }}</text>
              </view>
              <view class="home-task-meta">
                <text>{{ order.warehouse }}</text>
                <text>{{ order.stationCount }} 个自提点</text>
                <text>{{ order.cargoCount }} 件</text>
              </view>
              <text class="task-note">{{ order.returnCount }} 件退货待回仓</text>
              <view class="home-task-foot">
                <text>{{ order.deliveryDate }}</text>
                <text class="home-task-action">{{ order.status === '待发车' ? '确认出发' : '发起回仓' }}</text>
              </view>
            </button>
            <button v-if="todoTasks.length === 0 && homeDriverOrders.length === 0" v-for="todo in todos" :key="todo.title" class="warehouse-task" @click="openTodo(todo.taskId)">
              <text class="warehouse-task-title">{{ todo.title }}</text>
              <text class="warehouse-task-desc">{{ todo.desc }}</text>
            </button>
          </view>
        </view>

        <view class="warehouse-section">
          <view class="warehouse-section-head">
            <text class="section-title">最近操作记录</text>
          </view>
          <view class="log-list">
            <text v-for="item in operations" :key="item">{{ item }}</text>
          </view>
        </view>
      </view>

      <WarehouseBottomNav active="home" :role="role" />
    </template>
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
    loadWarehouseDashboard,
    setPendingReceiverStatusFilter,
    setPendingTarget,
    type RoleProfile,
    type WarehouseActionTarget,
    type WarehouseDriverOrder,
    type WarehouseKpi,
    type ReceiverTodoSummary,
    type WarehouseRole,
    type WarehouseTask
} from '@/stores/warehouse';

const role = ref<WarehouseRole>(getCurrentRole());
const profile = ref<RoleProfile>(getRoleProfile());
const kpis = ref<WarehouseKpi[]>([]);
const todos = ref<RoleProfile['todos']>([]);
const tasks = ref<WarehouseTask[]>([]);
const driverOrders = ref<WarehouseDriverOrder[]>([]);
const receiverTodoSummary = ref<ReceiverTodoSummary[]>([]);
const operations = ref<string[]>([]);
const loginTime = ref('2026-06-29 14:49');
const managerStatusRows = ref<Array<{ label: string; value: number | string }>>([]);
const todoTasks = computed(() => tasks.value.slice(0, 3));
const homeDriverOrders = computed(() => role.value === 'driver' ? driverOrders.value.slice(0, 3) : []);

async function loadData() {
    role.value = getCurrentRole();
    const dashboard = await loadWarehouseDashboard(role.value);
    profile.value = dashboard.profile;
    kpis.value = dashboard.kpis;
    todos.value = dashboard.todos;
    tasks.value = dashboard.tasks;
    driverOrders.value = dashboard.driverOrders;
    receiverTodoSummary.value = dashboard.receiverTodoSummary;
    operations.value = dashboard.operations;
    managerStatusRows.value = dashboard.managerStatusRows;
}

function goTasks() {
    if (role.value === 'driver') {
        uni.setStorageSync('mall_warehouse_h5_driver_tab', 'delivery');
    }
    uni.switchTab({ url: '/pages/tasks/index' });
}

function openTodo(taskId?: string) {
    if (taskId) {
        uni.navigateTo({ url: `/pages/tasks/detail?id=${taskId}` });
        return;
    }
    goTasks();
}

function openDriverOrder(orderId: string) {
    uni.navigateTo({ url: `/pages/tasks/detail?id=${orderId}&mode=driverDelivery` });
}

function openReceiverSummary(item: ReceiverTodoSummary) {
    setPendingReceiverStatusFilter(item.statusFilter);
    setPendingTarget(item.statusFilter || item.target);
    uni.switchTab({
        url: '/pages/tasks/index',
        success() {
            uni.$emit('warehouse:receiverStatusFilter', item.statusFilter);
        }
    });
}

function receiverTodoSummaryText(item: ReceiverTodoSummary) {
    return ` 单${item.desc}`;
}

function goKpi(target: string) {
    if (target === 'inventory') {
        goInventory();
        return;
    }
    if (target === 'manager') {
        handleQuick('managerPutaway');
        return;
    }
    goTasks();
}

function goInventory() {
    if (!canViewInventory(role.value)) {
        uni.showToast({ title: '当前角色不展示库存入口', icon: 'none' });
        return;
    }
    uni.switchTab({ url: '/pages/inventory/index' });
}

function handleQuick(target: WarehouseActionTarget) {
    if (target === 'inventory' || target === 'managerStockGoods' || target === 'managerInventory') {
        goInventory();
        return;
    }
    const task = getDashboardTaskForTarget(target);
    setPendingTarget(target, task?.id);
    if (['receiveArea', 'receiveScan', 'receiveQuality', 'purchaseCreate', 'purchaseReceipt', 'pickWork', 'pickShortage', 'recheckWork', 'loadingReceipt', 'driverHandover', 'managerPutaway'].includes(target)) {
        uni.navigateTo({ url: `/pages/tasks/detail?id=${task?.id || ''}&mode=${target}` });
        return;
    }
    goTasks();
}

function getDashboardTaskForTarget(target: WarehouseActionTarget | string) {
    const list = tasks.value;
    if (target === 'receiveArea') return list.find((task) => task.status === '待接单') || list[0];
    if (target === 'receiveScan' || target === 'receiveQuality') return list.find((task) => /待收货|收货中|待确认收货/.test(task.status)) || list[0];
    if (target === 'purchaseReceipt') return list.find((task) => /已收货|待采购确认|已到仓|入库异常|采购/.test(`${task.status}${task.type}${task.title}`)) || list[0];
    if (target === 'pickWork') return list.find((task) => /待拣货|拣货中/.test(task.status)) || list[0];
    if (target === 'pickShortage') return list.find((task) => /履约缺货|缺货|差异/.test(`${task.status}${task.diffNote}`)) || list[0];
    if (target === 'recheckWork' || target === 'loadingReceipt') return list.find((task) => /待装车|已装车待出库|已出库/.test(task.status)) || list[0];
    if (target === 'managerPutaway') return list.find((task) => task.role === 'manager' || /上架/.test(task.type)) || list[0];
    return list[0];
}

onShow(loadData);
</script>
