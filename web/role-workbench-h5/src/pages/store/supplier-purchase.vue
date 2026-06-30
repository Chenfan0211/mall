<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>采购 / 到仓</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="role-work-title-main">采购 / 到仓</text>
          <text class="role-work-sub">查看采购申请、到仓凭证和处理进度</text>
        </view>
        <text class="role-work-entity">{{ profile.entity }}</text>
      </view>
    </view>

    <view class="role-main">
      <view class="role-purchase-tabs">
        <button v-for="item in tabs" :key="item.value" :class="{ active: tab === item.value }" @click="tab = item.value">
          {{ item.label }}
        </button>
      </view>
      <view class="role-order-actions clear-actions">
        <button class="role-filter-clear" @click="tab = 'all'">清空筛选</button>
      </view>

      <view v-if="loading" class="role-empty">
        <text class="title">正在加载采购单</text>
        <text class="subtle">正在同步采购单...</text>
      </view>
      <view v-else-if="error" class="role-empty">
        <text class="title">采购单加载失败</text>
        <text class="subtle">{{ error }}</text>
        <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
      </view>

      <view v-else class="role-section">
        <view class="role-section-head">
          <view>
            <text class="title">采购单</text>
            <text class="subtle">{{ rows.length }} 条</text>
          </view>
        </view>
        <view class="role-purchase-list">
          <view v-for="item in rows" :key="item.id" class="role-purchase-card" @click="open(item.id)">
            <view class="role-leave-head">
              <text>{{ item.purchaseNo || `采购单 #${item.id}` }}</text>
              <text class="role-status" :class="roleStatusClass(purchaseStatusText(item))">{{ purchaseStatusText(item) }}</text>
            </view>
            <text class="role-purchase-copy">
              送达仓库：#{{ item.warehouseId || '-' }}
              <br />
              预计到仓：{{ item.expectedArrivalTime || item.deliveryDate || '-' }}
              <br />
              提交时间：{{ item.createTime || '-' }}
            </text>
            <view class="role-purchase-summary">
              <view>
                <text>{{ auditStatusText(item.auditStatus) }}</text>
                <text>审核状态</text>
              </view>
              <view>
                <text>#{{ item.cityId || '-' }}</text>
                <text>城市</text>
              </view>
              <view>
                <text>{{ item.deliveryDate || '-' }}</text>
                <text>配送日期</text>
              </view>
            </view>
            <view class="role-status-note">
              <text>{{ actionState(purchaseStatusText(item)).need }}</text>
              <text>{{ actionState(purchaseStatusText(item)).next }}</text>
            </view>
            <view class="role-order-actions">
              <button class="role-action-btn primary" @click.stop="open(item.id)">查看进度</button>
            </view>
          </view>
          <view v-if="rows.length === 0" class="role-empty inline-empty">
            <text class="title">暂无采购单数据</text>
          </view>
        </view>
      </view>
    </view>
    <RoleBottomNav active="orders" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { pageSupplierPurchases, type SupplierPurchaseDTO } from '@/api/station';
import { currentProfile, currentRole, getRequiredRoleQuery } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

type PurchaseTab = 'all' | 'waiting' | 'arrival' | 'supplement' | 'done';

const tab = ref<PurchaseTab>('all');
const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const tabs = [
    { value: 'all', label: '全部' },
    { value: 'waiting', label: '已提交待处理' },
    { value: 'arrival', label: '待提交到仓' },
    { value: 'supplement', label: '待我补充' },
    { value: 'done', label: '已完成' }
] as const;
const loading = ref(false);
const error = ref('');
const purchases = ref<SupplierPurchaseDTO[]>([]);

const rows = computed(() => {
    if (tab.value === 'waiting') {
        return purchases.value.filter((item) => purchaseStatusText(item) === '已提交待处理');
    }
    if (tab.value === 'arrival') {
        return purchases.value.filter((item) => purchaseStatusText(item) === '待提交到仓');
    }
    if (tab.value === 'supplement') {
        return purchases.value.filter((item) => purchaseStatusText(item) === '待我补充');
    }
    if (tab.value === 'done') {
        return purchases.value.filter((item) => purchaseStatusText(item) === '已完成');
    }
    return purchases.value;
});

onShow(load);

async function load() {
    if (roleType.value !== 'supplier') {
        uni.redirectTo({ url: '/pages/status/index?status=noPermission' });
        return;
    }
    loading.value = true;
    error.value = '';
    try {
        purchases.value = (await pageSupplierPurchases({ ...getRequiredRoleQuery(), pageNum: 1, pageSize: 50 })).list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '采购单加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function open(id: number) {
    uni.navigateTo({ url: `/pages/store/supplier-purchase-detail?id=${id}` });
}

function purchaseStatusText(item: SupplierPurchaseDTO) {
    const audit = Number(item.auditStatus || 0);
    const status = Number(item.purchaseStatus || 0);
    if ([30, 40, 50, 60].includes(status)) return '已完成';
    if ([30, 40, 50, 60].includes(audit)) return '已完成';
    if ([3, 70].includes(status) || audit === 30) return '待我补充';
    if ([20, 25].includes(status)) return '待提交到仓';
    return '已提交待处理';
}

function auditStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待审核',
        20: '已通过',
        30: '已拒绝',
        40: '已完成'
    };
    return map[Number(status || 0)] || '待审核';
}

function roleStatusClass(status: string) {
    if (status === '已完成') return 'green';
    if (status === '待提交到仓') return 'blue';
    if (status === '待我补充') return 'gray';
    return 'orange';
}

function actionState(status: string) {
    if (status === '已完成') return { need: '无需我处理', next: '采购单已完成，可查看采购商品和到仓记录。' };
    if (status === '待提交到仓') return { need: '需要我处理', next: '按采购要求准备到仓凭证，后续由仓库确认入库。' };
    if (status === '待我补充') return { need: '需要我处理', next: '采购申请被驳回或异常，按后台要求补充后重新提交。' };
    return { need: '无需我处理', next: '采购申请已提交，等待平台处理。' };
}
</script>

<style lang="scss" scoped>
.role-work-title {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 16rpx;
}

.role-work-title-main {
  display: block;
  color: #ffffff;
  font-size: 42rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-work-sub {
  display: block;
  margin-top: 10rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-purchase-tabs {
  display: flex;
  gap: 12rpx;
  margin-bottom: 18rpx;
  overflow-x: auto;
  padding-bottom: 4rpx;
}

.role-purchase-tabs button,
:deep(uni-button) {
  flex: 0 0 auto;
  min-height: 64rpx;
  padding: 0 22rpx;
  color: #8d6a57;
  background: #fff8f2;
  border: 1rpx solid #f1d8ca;
  border-radius: 999rpx;
  font-size: 23rpx;
  font-weight: 900;
}

.role-purchase-tabs button.active,
.role-purchase-tabs :deep(uni-button.active) {
  color: #ffffff;
  background: #e85d3f;
  border-color: #e85d3f;
}

.clear-actions {
  margin: 0 0 16rpx;
}

.role-purchase-list {
  display: grid;
  gap: 20rpx;
}

.role-purchase-card {
  padding: 24rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 28rpx;
}

.role-leave-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 14rpx;
}

.role-leave-head > text:first-child {
  min-width: 0;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-purchase-copy {
  display: block;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.65;
}

.role-purchase-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 18rpx;
}

.role-purchase-summary view {
  padding: 14rpx 12rpx;
  color: #8b6a57;
  background: #fff8f2;
  border-radius: 20rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-purchase-summary text:first-child {
  display: block;
  overflow: hidden;
  color: #d94b34;
  font-size: 25rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-status-note {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-status-note text:first-child {
  display: block;
  margin-bottom: 6rpx;
  color: #3a2c24;
  font-weight: 900;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-top: 18rpx;
}

.role-action-btn,
:deep(uni-button.role-action-btn) {
  min-height: 58rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-action-btn.primary,
:deep(uni-button.role-action-btn.primary) {
  color: #ffffff;
  background: #e85d3f;
}

.role-filter-clear,
:deep(uni-button.role-filter-clear) {
  min-height: 58rpx;
  padding: 0 24rpx;
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 48rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-status.orange {
  color: #d66323;
  background: #fff0e4;
}

.role-status.green {
  color: #17885f;
  background: #edf9f0;
}

.role-status.blue {
  color: #2b6cb0;
  background: #eef6ff;
}

.role-status.gray {
  color: #78665b;
  background: #f4f1ee;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}
</style>
