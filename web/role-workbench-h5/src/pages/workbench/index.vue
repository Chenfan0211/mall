<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '供应商' : '团点' }}</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="role-work-title-main">{{ roleType === 'supplier' ? '供应商工作台' : '自提点工作台' }}</text>
        </view>
        <text class="role-work-entity">{{ headerEntityName }}</text>
      </view>
    </view>

    <view class="role-main">
      <view v-if="loading" class="role-empty">
        <text class="title">正在加载工作台</text>
      </view>

      <view v-else-if="error" class="role-empty">
        <text class="title">工作台加载失败</text>
        <text class="subtle">{{ error }}</text>
        <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
      </view>

      <template v-else>
        <view class="role-kpis workbench-kpis">
          <button v-for="item in metrics" :key="item.label" class="role-kpi" :class="{ 'money-kpi': item.label === '今日佣金' }" @click="open(item.page)">
            <text class="value" :class="item.valueClass">{{ item.value }}</text>
            <text class="label">{{ item.label }}</text>
          </button>
        </view>

        <view class="role-section">
          <view class="role-section-head">
            <view>
              <text class="title">我需要处理</text>
            </view>
          </view>
          <view class="role-task-list">
            <button v-for="item in tasks" :key="item.title" class="role-task" @click="open(item.page)">
              <text class="task-title">{{ item.title }}</text>
              <view class="task-desc">
                <text class="task-count">{{ taskCount(item.desc) }}</text>
                <text class="task-unit">{{ taskUnit(item.desc) }}</text>
              </view>
            </button>
            <view v-if="tasks.length === 0" class="role-empty inline-empty">
              <text class="title">暂无待处理事项</text>
            </view>
          </view>
        </view>

        <view class="role-section">
          <view class="role-section-head">
            <view>
              <text class="title">我已提交，等待处理</text>
            </view>
          </view>
          <view class="role-task-list">
            <view class="role-empty inline-empty">
              <text class="title">暂无等待处理事项</text>
            </view>
          </view>
        </view>

        <view v-if="roleType === 'supplier'" class="role-section">
          <view class="role-section-head">
            <view>
              <text class="title">快捷入口</text>
            </view>
          </view>
          <view class="role-quick-grid">
            <button v-for="item in quicks" :key="item.title" class="role-quick-btn" @click="open(item.page)">
              <text class="task-title">{{ item.title }}</text>
              <text class="task-desc">{{ item.desc }}</text>
            </button>
          </view>
        </view>
      </template>
    </view>

    <RoleBottomNav active="home" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getLeaderDetail, getStationDetail, getSupplierWorkbenchSummary, getWorkbenchSummary, type SupplierWorkbenchDTO, type StationWorkbenchDTO } from '@/api/station';
import { currentProfile, currentRole, currentSession, getRequiredRoleQuery, goPage, setCurrentEntityName, stationQuickItems, supplierQuickItems, type RoleMetric, type RoleItem } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const loading = ref(false);
const error = ref('');
const stationSummary = ref<StationWorkbenchDTO | null>(null);
const supplierSummary = ref<SupplierWorkbenchDTO | null>(null);
const entityName = ref('');

const headerEntityName = computed(() => entityName.value || profile.value.entity);

const metrics = computed<RoleMetric[]>(() => {
    if (roleType.value === 'supplier') {
        const data = supplierSummary.value || {};
        return [
            metricItem(String(data.waitAuditPurchaseCount ?? 0), '待审核采购', '/pages/store/supplier-purchase'),
            metricItem(String(data.waitDeliveryPurchaseCount ?? 0), '待送货采购', '/pages/store/supplier-purchase'),
            metricItem(String(data.deliveringOrderCount ?? 0), '配送中', '/pages/orders/index')
        ];
    }
    const data = stationSummary.value || {};
    return [
        metricItem(String(data.todayOrderCount ?? 0), '今日订单', '/pages/orders/index'),
        metricItem(String(data.waitPickupCount ?? 0), '待自提', '/pages/orders/index'),
        metricItem(`¥${data.availableAmount ?? '0.00'}`, '今日佣金', '/pages/mine/index')
    ];
});

const tasks = computed<RoleItem[]>(() => {
    if (roleType.value === 'supplier') {
        const data = supplierSummary.value || {};
        return [
            { title: '待审核采购', desc: `${data.waitAuditPurchaseCount ?? 0} 条采购单待处理`, page: '/pages/store/supplier-purchase', tag: '采购' },
            { title: '待送货采购', desc: `${data.waitDeliveryPurchaseCount ?? 0} 条采购单待送货`, page: '/pages/store/supplier-purchase', tag: '送货' }
        ].filter((item) => !item.desc.startsWith('0 '));
    }
    const data = stationSummary.value || {};
    return [
        { title: '待提货', desc: `${data.waitPickupCount ?? 0} 单待用户自提`, page: '/pages/orders/index', tag: '核销' },
        { title: '到货作业', desc: `${data.arrivalCount ?? 0} 条到货记录`, page: '/pages/store/index', tag: '到货' },
        { title: '异常处理', desc: `${data.exceptionCount ?? 0} 条异常记录`, page: '/pages/store/index', tag: '异常' }
    ].filter((item) => !item.desc.startsWith('0 '));
});

const quicks = computed(() => (roleType.value === 'supplier' ? supplierQuickItems : stationQuickItems));

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const query = getRequiredRoleQuery();
        if (roleType.value === 'supplier') {
            supplierSummary.value = await getSupplierWorkbenchSummary(query);
            entityName.value = profile.value.entity;
        } else {
            stationSummary.value = await getWorkbenchSummary(query);
            await loadStationEntityName(query);
        }
    } catch (err) {
        error.value = friendlyErrorMessage(err, '工作台加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function open(url: string) {
    goPage(url);
}

function metricItem(value: string, label: string, page: string): RoleMetric {
    return {
        value,
        label,
        page,
        valueClass: metricValueClass(value)
    };
}

function metricValueClass(value: string) {
    const text = String(value || '').replace(/\s/g, '');
    const digitLength = text.replace(/\D/g, '').length;
    const visibleLength = text.length;
    if (digitLength >= 7 || visibleLength >= 8) return 'metric-value-7';
    if (digitLength >= 6 || visibleLength >= 7) return 'metric-value-6';
    if (digitLength >= 5 || visibleLength >= 6) return 'metric-value-5';
    return '';
}

async function loadStationEntityName(query: { stationId?: number; leaderId?: number }) {
    entityName.value = profile.value.entity;
    try {
        const shouldShowLeader = currentSession.value.subjectType === 5 || (query.leaderId && !query.stationId);
        if (shouldShowLeader && query.leaderId) {
            const leader = await getLeaderDetail(Number(query.leaderId));
            entityName.value = leader.leaderName || profile.value.entity;
            setCurrentEntityName(entityName.value);
            return;
        }
        if (query.stationId) {
            const station = await getStationDetail(Number(query.stationId));
            entityName.value = station.stationName || station.contactName || profile.value.entity;
            setCurrentEntityName(entityName.value);
        }
    } catch {
        entityName.value = profile.value.entity;
    }
}

function taskCount(desc: string) {
    const match = desc.trim().match(/^(\d+(?:\.\d+)?)/);
    return match?.[1] || '';
}

function taskUnit(desc: string) {
    return desc.trim().replace(/^(\d+(?:\.\d+)?)\s*/, '');
}
</script>

<style lang="scss" scoped>
.role-work-title {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 16rpx;
}

.role-work-title-main {
  display: block;
  color: #ffffff;
  font-size: 40rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-main {
  padding-top: 4rpx;
}

.workbench-kpis {
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10rpx;
  margin: 0 0 24rpx;
  width: 100%;
}

.workbench-kpis .role-kpi {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-width: 0;
  min-height: 150rpx;
  padding: 20rpx 6rpx 18rpx;
  border-radius: 24rpx;
  text-align: center;
  box-shadow: 0 18rpx 40rpx rgba(217, 75, 52, 0.18);
}

.workbench-kpis .role-kpi.money-kpi {
  padding-right: 8rpx;
  padding-left: 8rpx;
}

.workbench-kpis .role-kpi .value,
.workbench-kpis .role-kpi .value :deep(span) {
  display: block;
  font-family: "Arial Narrow", "DIN Alternate", Arial, "Microsoft YaHei", sans-serif;
  overflow: visible;
  max-width: 100%;
  font-size: 30px !important;
  font-weight: 900;
  line-height: 1;
  text-align: center;
  text-overflow: clip;
  white-space: nowrap;
}

.workbench-kpis .role-kpi:not(.money-kpi) .value,
.workbench-kpis .role-kpi:not(.money-kpi) .value :deep(span) {
  font-size: 30px !important;
  line-height: 1;
}

.workbench-kpis .role-kpi.money-kpi .value,
.workbench-kpis .role-kpi.money-kpi .value :deep(span) {
  font-family: "Arial Narrow", Arial, "Microsoft YaHei", sans-serif;
  font-size: 30px !important;
  letter-spacing: 0;
}

.workbench-kpis .role-kpi .value.metric-value-5,
.workbench-kpis .role-kpi .value.metric-value-5 :deep(span) {
  font-size: 27px !important;
}

.workbench-kpis .role-kpi .value.metric-value-6,
.workbench-kpis .role-kpi .value.metric-value-6 :deep(span) {
  font-size: 24px !important;
}

.workbench-kpis .role-kpi .value.metric-value-7,
.workbench-kpis .role-kpi .value.metric-value-7 :deep(span) {
  font-size: 21px !important;
  letter-spacing: -0.5px;
}

.workbench-kpis .role-kpi .label {
  display: block;
  width: 100%;
  margin-top: 10rpx;
  text-align: center;
}

.workbench-kpis .role-kpi .label,
.workbench-kpis .role-kpi .label :deep(span) {
  font-size: 28rpx !important;
  font-weight: 900;
  line-height: 1.12;
  text-align: center;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}

.role-section {
  padding: 26rpx 24rpx;
}

.role-section .title {
  font-size: 36rpx;
  line-height: 1.15;
}

.role-section-head {
  margin-bottom: 18rpx;
}

.role-task-list {
  gap: 16rpx;
}

.role-task {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24rpx;
  width: 100%;
  min-height: 112rpx;
  padding: 28rpx 28rpx;
  border-radius: 24rpx;
}

.role-task .task-title {
  flex-shrink: 0;
  color: #2e211a;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1.25;
}

.role-task .task-desc {
  display: inline-flex;
  align-items: baseline;
  justify-content: flex-end;
  min-width: 0;
  color: #5f4031;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.2;
  text-align: right;
  white-space: nowrap;
}

.role-task .task-count {
  color: #d94b34;
  font-family: "Arial Black", Arial, "Microsoft YaHei", sans-serif;
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1;
}

.role-task .task-unit {
  margin-left: 2rpx;
  font-size: 30rpx;
  font-weight: 800;
}

.role-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 68rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 34rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-status.green {
  color: #17885f;
  background: #edf9f0;
}

.role-status.orange {
  color: #d66323;
  background: #fff0e4;
}
</style>
