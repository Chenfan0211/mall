<template>
  <view class="page order-page">
    <view class="order-list-head">
      <text class="order-kicker">我的订单</text>
      <text class="order-title">按状态跟进履约</text>
      <view class="order-summary-card">
        <view>
          <text>{{ waitPayCount }}</text>
          <text>待付款</text>
        </view>
        <view>
          <text>{{ pickupCount }}</text>
          <text>待自提</text>
        </view>
        <view>
          <text>{{ afterSaleCount }}</text>
          <text>售后中</text>
        </view>
      </view>
    </view>

    <scroll-view class="order-tabs" scroll-x>
      <text
        v-for="tab in tabs"
        :key="tab.value"
        class="order-tab"
        :class="{ active: activeTab === tab.value }"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </text>
    </scroll-view>

    <view class="order-list">
      <view v-for="item in filteredOrders" :key="item.id" class="order-card">
        <view class="order-card-head">
          <view>
            <text class="order-no">{{ item.orderNo }}</text>
            <text class="order-time">{{ item.createTime || '待同步时间' }}</text>
          </view>
          <text class="order-status">{{ statusText(item) }}</text>
        </view>
        <view class="order-station">
          <text>自提点 {{ item.stationId }}</text>
          <text>{{ item.pickupName || '测试用户' }}</text>
        </view>
        <view class="order-meta-grid">
          <view>
            <text>交易状态</text>
            <text>{{ tradeText(item.tradeStatus) }}</text>
          </view>
          <view>
            <text>履约状态</text>
            <text>{{ fulfillText(item.fulfillStatus) }}</text>
          </view>
          <view>
            <text>应付金额</text>
            <text>¥{{ item.payAmount || item.totalAmount || '0.00' }}</text>
          </view>
        </view>
        <view class="order-card-actions">
          <button class="plain" @click="openDetail(item.id)">详情</button>
          <button v-if="item.tradeStatus === 10" class="primary" @click="openPay(item.id)">继续支付</button>
          <button v-if="item.tradeStatus === 10" class="plain" @click="cancelOrder(item.id)">取消订单</button>
          <button v-if="item.fulfillStatus === 60" class="primary" @click="confirmReceive(item.id)">确认收货</button>
          <button v-if="item.fulfillStatus === 70" class="plain" @click="openReview(item.id)">评价</button>
          <button v-if="item.tradeStatus !== 10" class="plain" @click="openAftersale(item.id)">售后</button>
        </view>
      </view>

      <view v-if="filteredOrders.length === 0" class="empty-panel">
        <text class="title">暂无订单</text>
        <text class="subtle">切换其他状态或返回首页继续选购。</text>
      </view>
    </view>
    <ConfirmDialog
      :visible="confirmDialog.visible"
      :title="confirmDialog.title"
      :content="confirmDialog.content"
      :confirm-text="confirmDialog.confirmText"
      @cancel="confirmDialog.visible = false"
      @confirm="confirmAction"
    />
    <UserToast />
    <UserTabBar active="order" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageOrders, type UserOrderDTO } from '@/api/user';
import { navigateUser, showUserToast, statusText as mapStatusText, useUserState } from '@/stores/userState';

const tabs = [
    { label: '全部', value: -1 },
    { label: '待付款', value: 10 },
    { label: '备货中', value: 30 },
    { label: '待自提', value: 60 },
    { label: '已完成', value: 70 },
    { label: '售后中', value: 40 }
];

const state = useUserState();
const orders = ref<UserOrderDTO[]>(state.localOrders.map((item) => ({ ...item })));
const activeTab = ref(-1);
const confirmDialog = reactive({
    visible: false,
    title: '',
    content: '',
    confirmText: '确认',
    action: ''
});
const filteredOrders = computed(() => {
    if (activeTab.value === -1) {
        return orders.value;
    }
    if (activeTab.value === 10) {
        return orders.value.filter((item) => item.tradeStatus === 10);
    }
    if (activeTab.value === 40) {
        return orders.value.filter((item) => item.tradeStatus === 40 || item.fulfillStatus === 40);
    }
    return orders.value.filter((item) => item.fulfillStatus === activeTab.value);
});
const waitPayCount = computed(() => orders.value.filter((item) => item.tradeStatus === 10).length);
const pickupCount = computed(() => orders.value.filter((item) => item.fulfillStatus === 60).length);
const afterSaleCount = computed(() => orders.value.filter((item) => item.tradeStatus === 40 || item.fulfillStatus === 40).length);

async function loadData() {
    try {
        const page = await pageOrders({
            pageNum: 1,
            pageSize: 20,
            userId: state.user.id
        });
        orders.value = page.list?.length ? page.list : state.localOrders.map((item) => ({ ...item }));
    } catch (error) {
        showUserToast('订单接口暂不可用，已展示本地订单状态', 'warn');
        orders.value = state.localOrders.map((item) => ({ ...item }));
    }
}

function tradeText(status: number) {
    return mapStatusText('trade', status);
}

function fulfillText(status: number) {
    return mapStatusText('fulfill', status);
}

function statusText(item: UserOrderDTO) {
    if (item.tradeStatus === 10) {
        return '待付款';
    }
    return fulfillText(item.fulfillStatus);
}

function openPay(id: number) {
    navigateUser(`/pages/payment/index?orderId=${id}`);
}

function openAftersale(id: number) {
    navigateUser(`/pages/aftersale/apply?orderId=${id}`);
}

function openDetail(id: number) {
    navigateUser(`/pages/order/detail?id=${id}`);
}

function openReview(id: number) {
    navigateUser(`/pages/order/review?id=${id}`);
}

function cancelOrder(id: number) {
    confirmDialog.visible = true;
    confirmDialog.title = '取消订单';
    confirmDialog.content = `确认取消订单 ${id}？未支付订单取消后会释放库存。`;
    confirmDialog.confirmText = '确认取消';
    confirmDialog.action = 'cancel';
}

function confirmReceive(id: number) {
    confirmDialog.visible = true;
    confirmDialog.title = '确认收货';
    confirmDialog.content = `确认订单 ${id} 已提货？确认后订单进入已完成，可评价或申请售后。`;
    confirmDialog.confirmText = '确认收货';
    confirmDialog.action = 'receive';
}

function confirmAction() {
    confirmDialog.visible = false;
    showUserToast(confirmDialog.action === 'receive' ? '确认收货接口暂缺，已保留页面状态' : '取消订单接口暂缺，已保留页面状态', 'warn');
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  padding: 0 0 150rpx;
  background:
    radial-gradient(circle at 86% 8%, rgba(255, 214, 172, 0.85), transparent 24%),
    linear-gradient(180deg, #d94b34 0%, #ef7a37 260rpx, #f7f1ea 262rpx, #f7f1ea 100%);
}

.order-list-head {
  padding: 46rpx 24rpx 20rpx;
  color: #ffffff;
}

.order-kicker,
.order-title {
  display: block;
}

.order-kicker {
  color: rgba(255, 255, 255, 0.82);
  font-size: 22rpx;
  font-weight: 900;
}

.order-title {
  margin-top: 8rpx;
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.12;
}

.order-summary-card {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0;
  margin-top: 24rpx;
  padding: 22rpx 12rpx;
  background: rgba(255, 255, 255, 0.94);
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(121, 74, 43, 0.16);
}

.order-summary-card view {
  display: grid;
  gap: 8rpx;
  text-align: center;
  border-right: 1rpx solid #f0dfd6;
}

.order-summary-card view:last-child {
  border-right: 0;
}

.order-summary-card text:first-child {
  color: #d94b34;
  font-size: 36rpx;
  font-weight: 900;
}

.order-summary-card text:last-child {
  color: #7b5f51;
  font-size: 22rpx;
  font-weight: 900;
}

.order-tabs {
  white-space: nowrap;
  padding: 4rpx 20rpx 18rpx;
}

.order-tab {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 56rpx;
  margin-right: 10rpx;
  padding: 0 22rpx;
  color: #8c6a58;
  background: rgba(255, 255, 255, 0.86);
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.order-tab.active {
  color: #ffffff;
  background: #172033;
  border-color: #172033;
}

.order-list {
  display: grid;
  gap: 18rpx;
  padding: 0 20rpx;
}

.order-card {
  padding: 22rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.order-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.order-no,
.order-time {
  display: block;
}

.order-no {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.25;
}

.order-time {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.order-status {
  min-height: 44rpx;
  padding: 0 16rpx;
  color: #b73518;
  background: #fff1e9;
  border: 1rpx solid rgba(232, 93, 63, 0.18);
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  line-height: 44rpx;
  white-space: nowrap;
}

.order-station {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 18rpx;
  padding: 18rpx;
  color: #7b5f51;
  background: #fffaf6;
  border-radius: 20rpx;
  font-size: 23rpx;
  font-weight: 800;
}

.order-meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10rpx;
  margin-top: 18rpx;
}

.order-meta-grid view {
  min-height: 96rpx;
  padding: 14rpx 10rpx;
  background: #fff7f1;
  border: 1rpx solid #f4e2d8;
  border-radius: 18rpx;
}

.order-meta-grid text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-meta-grid text:first-child {
  color: #8c6a58;
  font-size: 20rpx;
}

.order-meta-grid text:last-child {
  margin-top: 8rpx;
  color: #172033;
  font-size: 24rpx;
  font-weight: 900;
}

.order-card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
  margin-top: 18rpx;
}

.order-card-actions button {
  min-width: 156rpx;
  min-height: 62rpx;
  padding: 0 24rpx;
  font-size: 24rpx;
}

.order-card-actions .plain {
  color: #c2412d !important;
  background: #fff2e9 !important;
  border: 1rpx solid #f2d6c4;
  box-shadow: none;
}

.empty-panel {
  padding: 40rpx 24rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
}
</style>
