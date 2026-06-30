<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>到货通知预览</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">到货通知预览</text>
        </view>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载通知预览</text>
      <text class="subtle">正在聚合到站、订单和商品数据...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">通知预览加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view class="role-detail-section">
          <text class="section-title">发送范围</text>
          <view class="role-total-box">
            <view>
              <text>影响用户</text>
              <text>{{ summary.userCount }}</text>
            </view>
            <view>
              <text>商品种类</text>
              <text>{{ summary.productCount }}</text>
            </view>
            <view>
              <text>实到数量</text>
              <text>{{ summary.goodsCount }}</text>
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <text class="section-title">通知确认</text>
          <view class="role-order-info">
            <view>
              <text>当前主体</text>
              <text>{{ profile.entity }}</text>
            </view>
            <view>
              <text>提货日</text>
              <text>{{ summary.pickupDate }}</text>
            </view>
            <view>
              <text>到站记录</text>
              <text>{{ summary.readyCount }} 条可通知 / {{ deliveryStations.length }} 条总记录</text>
            </view>
            <view>
              <text>通知渠道</text>
              <text>公众号模板消息 + 站内消息</text>
            </view>
            <view>
              <text>重复发送限制</text>
              <text>同一提货日再次发送前必须展示本预览</text>
            </view>
          </view>
          <view class="role-wallet-actions">
            <button class="role-wallet-action primary" @click="confirmSend">确认发送</button>
            <button class="role-wallet-action soft" @click="back">返回门店作业</button>
          </view>
        </view>

        <view class="role-detail-section">
          <text class="section-title">商品范围</text>
          <view v-if="productRows.length" class="role-product-range-list">
            <view v-for="item in productRows" :key="item.key" class="role-product-range-card">
              <view class="role-product-summary-img"><RoleProductThumb :label="item.title" /></view>
              <view>
                <text class="role-product-title">{{ item.title }}</text>
                <text class="role-product-desc">{{ item.spec }}</text>
                <view class="role-range-meta">
                  <text>{{ item.qty }} 件</text>
                  <text>{{ item.orders }} 单</text>
                </view>
              </view>
            </view>
          </view>
          <view v-else class="role-empty inline-empty">
            <text class="title">暂无商品范围</text>
            <text class="subtle">当前没有可聚合的订单商品数据。</text>
          </view>
        </view>
      </view>
    </template>
    <RoleBottomNav active="delivery" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import {
    pageDeliveryStations,
    pageStationOrderItems,
    pageStationOrders,
    type DeliveryStationDTO,
    type StationOrderDTO,
    type StationOrderItemDTO
} from '@/api/station';
import { currentProfile, getRequiredRoleQuery, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

interface ProductRangeRow {
    key: string;
    title: string;
    spec: string;
    qty: number;
    orders: number;
}

const profile = computed(() => currentProfile.value);
const loading = ref(false);
const error = ref('');
const deliveryStations = ref<DeliveryStationDTO[]>([]);
const orders = ref<StationOrderDTO[]>([]);
const orderItems = ref<StationOrderItemDTO[]>([]);

const readyStations = computed(() => deliveryStations.value.filter((item) => Number(item.status || 0) >= 20 && Number(item.status || 0) !== 40));
const productRows = computed<ProductRangeRow[]>(() => {
    const map = new Map<string, { key: string; title: string; spec: string; qty: number; orders: Set<string> }>();
    orderItems.value.forEach((item) => {
        const title = item.productName || `SKU #${item.skuId || item.id}`;
        const spec = item.skuName || '-';
        const key = `${title}__${spec}`;
        const row = map.get(key) || { key, title, spec, qty: 0, orders: new Set<string>() };
        row.qty += Number(item.qty || 0);
        const orderNo = item.orderNo || (item.orderId ? `订单#${item.orderId}` : '');
        if (orderNo) row.orders.add(orderNo);
        map.set(key, row);
    });
    return Array.from(map.values()).map((item) => ({
        key: item.key,
        title: item.title,
        spec: item.spec,
        qty: item.qty,
        orders: item.orders.size
    }));
});

const summary = computed(() => {
    const userKeys = new Set<string>();
    orders.value.forEach((item) => {
        const key = item.pickupMobile || item.pickupName || item.orderNo || String(item.id);
        if (key) userKeys.add(key);
    });
    const pickupDate = firstPickupDate.value || firstArrivalDate.value || '-';
    return {
        userCount: userKeys.size,
        productCount: productRows.value.length,
        goodsCount: orderItems.value.reduce((sum, item) => sum + Number(item.qty || 0), 0),
        readyCount: readyStations.value.length,
        pickupDate
    };
});

const firstPickupDate = computed(() => {
    const value = orderItems.value.find((item) => item.expectedPickupDate)?.expectedPickupDate;
    return formatDateOption(value);
});

const firstArrivalDate = computed(() => {
    const value = readyStations.value.find((item) => item.arriveTime || item.createTime);
    return formatDateOption(value?.arriveTime || value?.createTime);
});

const notifyTip = computed(() => {
    if (summary.value.readyCount <= 0) {
        return '当前暂无已到站记录，暂不能发送到货通知。';
    }
    if (summary.value.userCount <= 0 || summary.value.productCount <= 0) {
        return '已有到站记录，但暂无可聚合的用户或商品范围，请返回门店作业确认。';
    }
    return '到货后先预览影响用户和商品，再确认通知范围。';
});

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const query = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 100 };
        const [stationPage, orderPage, itemPage] = await Promise.all([
            pageDeliveryStations(query),
            pageStationOrders(query),
            pageStationOrderItems(query)
        ]);
        deliveryStations.value = stationPage.list || [];
        orders.value = orderPage.list || [];
        orderItems.value = itemPage.list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '通知预览加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function formatDateOption(value?: string) {
    if (!value) return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0] || '-';
}

function confirmSend() {
    if (summary.value.readyCount <= 0) {
        showRoleToast('暂无可通知范围');
        return;
    }
    if (summary.value.userCount <= 0 || summary.value.productCount <= 0) {
        showRoleToast('暂无可通知用户或商品');
        return;
    }
    showRoleToast('通知提交通道暂未开放，未提交数据');
}

function back() {
    uni.redirectTo({ url: '/pages/store/index' });
}
</script>

<style lang="scss" scoped>
.role-detail-headline {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
  margin-top: 18rpx;
}

.role-detail-back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  min-height: 64rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.24);
  border-radius: 999rpx;
  font-size: 44rpx;
  font-weight: 900;
}

.role-detail-title {
  display: block;
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.25;
}

.role-detail-meta {
  display: block;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-main {
  padding-top: 0;
}

.role-detail-section {
  margin-bottom: 24rpx;
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.section-title {
  display: block;
  margin-bottom: 20rpx;
  color: #2b241f;
  font-size: 32rpx;
  font-weight: 900;
}

.role-total-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.role-total-box view {
  padding: 16rpx 12rpx;
  color: #8b6a57;
  background: #fff8f2;
  border-radius: 22rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-total-box text {
  display: block;
}

.role-total-box text:last-child {
  margin-top: 8rpx;
  color: #d94b34;
  font-size: 28rpx;
  font-weight: 900;
}

.role-order-info {
  display: grid;
  gap: 14rpx;
}

.role-order-info view {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.role-order-info text:first-child {
  color: #3a2c24;
  font-size: 24rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-order-info text:last-child {
  min-width: 0;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
  text-align: right;
}

.role-order-actions,
.role-wallet-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 22rpx;
}

.role-action-btn,
:deep(uni-button.role-action-btn),
.role-wallet-action,
:deep(uni-button.role-wallet-action) {
  min-height: 66rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-action-btn.primary,
:deep(uni-button.role-action-btn.primary),
.role-wallet-action.primary,
:deep(uni-button.role-wallet-action.primary) {
  color: #ffffff;
  background: #e85d3f;
}

.role-action-btn.soft,
:deep(uni-button.role-action-btn.soft),
.role-wallet-action.soft,
:deep(uni-button.role-wallet-action.soft) {
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
}

.role-wallet-action.disabled,
:deep(uni-button.role-wallet-action.disabled),
.role-wallet-action[disabled],
:deep(uni-button.role-wallet-action[disabled]) {
  color: #9a8a80;
  background: #f4f1ee;
  border: 1rpx solid #e8d8cf;
}

.role-product-range-list {
  display: grid;
  gap: 18rpx;
}

.role-product-range-card {
  display: grid;
  grid-template-columns: 88rpx minmax(0, 1fr);
  gap: 18rpx;
  align-items: center;
  padding: 18rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
}

.role-product-summary-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border-radius: 22rpx;
  font-size: 32rpx;
  font-weight: 900;
}

.role-product-title {
  display: block;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-product-desc,
.role-range-meta {
  margin-top: 8rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-range-meta {
  display: flex;
  gap: 16rpx;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}
</style>
