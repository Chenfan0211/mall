<template>
  <view class="page role-page">
    <view class="role-top">
      <view class="role-brand-row">
        <view class="role-brand">
          <text>角色工作台</text>
          <text>GROUP OPS</text>
        </view>
        <text class="role-badge">团点</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="title">{{ stationName }}</text>
          <text class="subtle">团长能力 + 门店作业 + 退货处理</text>
        </view>
        <text class="role-chip">当前身份</text>
      </view>
    </view>

    <view class="role-main">
      <view class="role-kpis">
        <view v-for="item in metrics" :key="item.label" class="metric role-kpi">
          <text class="title">{{ item.value }}</text>
          <text class="subtle">{{ item.label }}</text>
          <text>{{ item.tip }}</text>
        </view>
      </view>

      <view class="section role-section">
        <view class="role-section-head">
          <view>
            <text class="title">快捷入口</text>
            <text class="subtle">按当前主体隔离数据</text>
          </view>
          <text class="pill">今日</text>
        </view>
        <view class="role-quick-grid">
          <button v-for="item in quickActions" :key="item.title" class="role-quick-btn" @click="open(item.url)">
            <text>{{ item.title }}</text>
            <text>{{ item.tip }}</text>
          </button>
        </view>
      </view>

      <view class="section role-section">
        <view class="role-section-head">
          <view>
            <text class="title">今日作业</text>
            <text class="subtle">订单、到货、异常和消息</text>
          </view>
        </view>
        <view class="role-task-list">
          <view v-for="item in tasks" :key="item.title" class="role-task" @click="open(item.url)">
            <view>
              <text class="task-title">{{ item.title }}</text>
              <text class="task-tip">{{ item.tip }}</text>
            </view>
            <text class="pill">{{ item.status }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getWorkbenchSummary } from '@/api/station';

const stationName = ref('自提点工作台');
const metrics = ref([
    { label: '待提货', value: 0, tip: '核销用户到店提货' },
    { label: '待收货', value: 0, tip: '确认到货与缺货' },
    { label: '今日佣金', value: 0, tip: '待结算金额' }
]);

const quickActions = [
    { title: '提货订单', tip: '核销与商品汇总', url: '/pages/orders/index' },
    { title: '门店作业', tip: '到货确认与退货', url: '/pages/store/index' },
    { title: '财务账户', tip: '佣金与提现', url: '/pages/finance/index' },
    { title: '我的账号', tip: '身份与操作记录', url: '/pages/mine/index' }
];

const tasks = [
    { title: '提货订单', tip: '默认当前提货日，可按订单或商品汇总查询', status: '待核销', url: '/pages/orders/index' },
    { title: '门店作业', tip: '到货确认、缺货上报、退货处理', status: '待通知', url: '/pages/store/index' },
    { title: '供应商视图', tip: '供应商身份独立入口', status: '切换查看', url: '/pages/supplier/index' },
    { title: '消息中心', tip: '订单、到货、佣金提醒', status: '未读', url: '/pages/messages/index' },
    { title: '休假申请', tip: '休假期间停止履约', status: '待审核', url: '/pages/leave/index' },
    { title: '操作记录', tip: '关键动作留痕可追溯', status: '已完成', url: '/pages/records/index' }
];

async function loadData() {
    const summary = await getWorkbenchSummary({ stationId: 720001, leaderId: 730001, bizDate: '2026-06-26' });
    stationName.value = `自提点 ${summary.stationId || 720001}`;
    metrics.value = [
        { label: '待提货', value: summary.waitPickupCount || 0, tip: '核销用户到店提货' },
        { label: '待收货', value: summary.arrivalCount || 0, tip: '确认到货与缺货' },
        { label: '今日佣金', value: summary.pendingCommissionAmount || 0, tip: '待结算金额' }
    ];
}

function open(url: string) {
    if (url === '/pages/orders/index' || url === '/pages/store/index' || url === '/pages/finance/index' || url === '/pages/mine/index') {
        uni.switchTab({ url });
        return;
    }
    uni.navigateTo({ url });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.role-page {
  padding: 0 0 156rpx;
}

.role-top {
  position: relative;
  overflow: hidden;
  padding: 16rpx 28rpx 28rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
}

.role-top::before,
.role-top::after {
  content: "";
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
}

.role-top::before {
  left: -36rpx;
  bottom: -108rpx;
  width: 272rpx;
  height: 272rpx;
}

.role-top::after {
  right: -88rpx;
  top: -76rpx;
  width: 336rpx;
  height: 336rpx;
}

.role-brand-row,
.role-work-title {
  position: relative;
  z-index: 1;
}

.role-brand-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 16rpx;
}

.role-brand {
  display: grid;
  gap: 8rpx;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.1;
}

.role-brand text:last-child {
  font-size: 22rpx;
  letter-spacing: 0.08em;
  opacity: 0.9;
}

.role-badge,
.role-chip {
  display: inline-flex;
  align-items: center;
  min-height: 56rpx;
  padding: 0 22rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 800;
}

.role-work-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 28rpx;
}

.role-work-title .title {
  color: #ffffff;
  font-size: 44rpx;
}

.role-work-title .subtle {
  color: rgba(255, 255, 255, 0.92);
}

.role-main {
  padding: 24rpx;
}

.role-kpis {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18rpx;
  margin-bottom: 24rpx;
}

.role-kpi {
  margin: 0;
  min-width: 0;
}

.role-kpi > text:last-child {
  display: block;
  margin-top: 14rpx;
  color: rgba(255, 255, 255, 0.9);
  font-size: 22rpx;
  line-height: 1.35;
}

.role-section {
  padding: 28rpx;
}

.role-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.role-quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}

.role-quick-btn {
  display: grid;
  gap: 8rpx;
  align-content: start;
  min-height: 132rpx;
  padding: 26rpx 24rpx;
  color: #7a4f34 !important;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%) !important;
  box-shadow: inset 0 0 0 1rpx #f0d6c5;
  text-align: left;
}

.role-quick-btn text:first-child {
  color: #3f2e24;
  font-size: 28rpx;
  font-weight: 900;
}

.role-quick-btn text:last-child {
  color: #8b6b57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-task-list {
  display: grid;
  gap: 20rpx;
}

.role-task {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 24rpx 26rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 32rpx;
}

.task-title,
.task-tip {
  display: block;
}

.task-title {
  color: #3b2f28;
  font-size: 28rpx;
  font-weight: 900;
}

.task-tip {
  margin-top: 8rpx;
  color: #8b6b57;
  font-size: 24rpx;
  line-height: 1.45;
}
</style>
