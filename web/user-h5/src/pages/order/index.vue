<template>
  <view class="order-page shop-page" data-m-page="orders">
    <view class="order-list-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>我的订单</b>
        <span>2026</span>
      </view>
      <view class="order-summary-card">
        <strong>{{ orders.length }} 笔订单</strong>
        <view class="order-count-grid">
          <button @click="activeTab = 30"><b>{{ preparingCount }}</b>备货中</button>
          <button @click="activeTab = 40"><b>{{ afterSaleCount }}</b>售后</button>
          <button @click="activeTab = -1"><b>¥{{ orderTotalAmount }}</b>实付合计</button>
        </view>
      </view>
    </view>

    <view class="order-panel">
      <view class="order-search-row">
        <span>⌕</span>
        <b>输入订单号 / 商品名称搜索</b>
      </view>
      <scroll-view class="order-tabs" scroll-x :show-scrollbar="false">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}
        </button>
      </scroll-view>
      <view class="order-list">
        <view v-for="item in filteredOrders" :key="item.id" class="order-card">
          <view class="order-card-top">
            <view class="order-station">
              <b>{{ item.stationName || stationName(item.stationId) }}</b>
              <button class="order-no-copy" @click.stop="copyOrderNo(item.orderNo)">订单号：{{ item.orderNo }}</button>
            </view>
            <span class="order-status" :class="statusClass(item)">{{ statusText(item) }}</span>
          </view>

          <view class="order-product-row" @click="openDetail(item.id)">
            <view class="order-product-preview">
              <view v-for="product in orderItems(item).slice(0, 3)" :key="product.id" class="order-thumb">
                <view class="order-thumb-img" :style="backgroundImageStyle(orderItemImage(product))" />
              </view>
            </view>
            <view class="order-product-main">
              <h4>{{ orderProductTitle(item) }}</h4>
              <p>{{ orderProductSpec(item) }}</p>
            </view>
            <span class="order-arrow">›</span>
          </view>

          <view class="order-meta-line">
            <span>{{ item.createTime || '待同步时间' }}</span>
            <span class="order-total">共 {{ orderQty(item) }} 件 <b>¥{{ item.payAmount || item.totalAmount || '0.00' }}</b></span>
          </view>

          <view class="order-card-actions">
            <view v-if="item.tradeStatus === 10" class="pay-countdown">剩余支付时间 15:00</view>
            <view class="order-action-buttons">
              <button @click="openDetail(item.id)">详情</button>
              <button v-if="item.tradeStatus === 10" class="primary" @click="openPay(item.id)">继续支付</button>
              <button v-if="item.tradeStatus === 10" @click="cancelOrder(item.id)">取消订单</button>
              <button v-if="item.fulfillStatus === 60" class="primary" @click="confirmReceive(item.id)">确认收货</button>
              <button v-if="canReviewOrder(item)" @click="openReview(item.id)">评价</button>
            </view>
          </view>
        </view>

        <view v-if="filteredOrders.length === 0" class="order-empty">
          <b>暂无订单</b>
          <span>近 6 个月内还没有下单记录，可以先去首页看看今天可提的商品。</span>
          <button @click="goHome">去首页逛逛</button>
        </view>
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
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageOrders, type UserOrderDTO } from '@/api/user';
import { navigateUser, showUserToast, statusText as mapStatusText, useUserState, type LocalOrder, type LocalOrderItem } from '@/stores/userState';
import { fallbackProducts, findFallbackProduct } from '@/utils/userFallbackData';
import { currentRouteQuery } from '@/utils/routeQuery';

type OrderView = UserOrderDTO & Partial<Pick<LocalOrder, 'stationName' | 'items'>>;

const tabs = [
    { label: '全部', value: -1 },
    { label: '待付款', value: 10 },
    { label: '备货中', value: 30 },
    { label: '配送中', value: 50 },
    { label: '待提货', value: 60 },
    { label: '已完成', value: 70 },
    { label: '售后', value: 40 }
];

const state = useUserState();
const orders = ref<OrderView[]>(state.localOrders.map((item) => ({ ...item })));
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
    if (activeTab.value === 30) {
        return orders.value.filter((item) => [20, 30, 40].includes(item.fulfillStatus) && item.tradeStatus !== 10 && item.tradeStatus !== 40);
    }
    return orders.value.filter((item) => item.fulfillStatus === activeTab.value);
});
const preparingCount = computed(() => orders.value.filter((item) => [20, 30, 40].includes(item.fulfillStatus) && item.tradeStatus !== 10 && item.tradeStatus !== 40).length);
const afterSaleCount = computed(() => orders.value.filter((item) => item.tradeStatus === 40 || item.fulfillStatus === 40).length);
const orderTotalAmount = computed(() => orders.value.reduce((sum, item) => sum + Number(item.payAmount || item.totalAmount || 0), 0).toFixed(2));

const statusTabMap: Record<string, number> = {
    all: -1,
    unpaid: 10,
    preparing: 30,
    picking: 30,
    paid: 30,
    shipping: 50,
    delivery: 50,
    pickup: 60,
    done: 70,
    completed: 70,
    aftersale: 40,
    refund: 40
};

function applyInitialStatusFilter() {
    const query = currentRouteQuery();
    const status = String(query.status || query.tab || '').toLowerCase();
    if (status && statusTabMap[status] !== undefined) {
        activeTab.value = statusTabMap[status];
    }
}

function syncStatusFromHash() {
    applyInitialStatusFilter();
}

async function loadData() {
    try {
        const page = await pageOrders(
            {
                pageNum: 1,
                pageSize: 20,
                userId: state.user.id
            },
            { silent: true }
        );
        orders.value = page?.list?.length ? page.list : state.localOrders.map((item) => ({ ...item }));
    } catch {
        orders.value = state.localOrders.map((item) => ({ ...item }));
        console.info('用户端订单接口不可用，已展示本地订单状态');
    }
}

function stationName(stationId: number) {
    return state.stations.find((item) => item.id === stationId)?.name || `自提点 ${stationId}`;
}

function fulfillText(status: number) {
    return mapStatusText('fulfill', status);
}

function statusText(item: OrderView) {
    if (item.tradeStatus === 10) {
        return '待付款';
    }
    if (item.tradeStatus === 40 || item.fulfillStatus === 40) {
        return '售后中';
    }
    return fulfillText(item.fulfillStatus);
}

function statusClass(item: OrderView) {
    if (item.tradeStatus === 10) {
        return 'unpaid';
    }
    if (item.tradeStatus === 40 || item.fulfillStatus === 40) {
        return 'rejected';
    }
    if (item.fulfillStatus === 60) {
        return 'pickup';
    }
    if (item.fulfillStatus === 70) {
        return 'done';
    }
    return 'preparing';
}

function canReviewOrder(item: OrderView) {
    return item.tradeStatus === 50 && item.fulfillStatus === 70;
}

function orderQty(item: OrderView) {
    return orderItems(item).reduce((sum: number, product: LocalOrderItem) => sum + Number(product.qty || 0), 0);
}

function orderProductTitle(item: OrderView) {
    const products = orderItems(item);
    if (!products.length) {
        return '订单商品';
    }
    return products.length > 1 ? `${products[0].productName} 等 ${products.length} 件` : products[0].productName;
}

function orderProductSpec(item: OrderView) {
    const product = orderItems(item)[0];
    return product ? `${product.skuName} · 预计提货 ${product.expectedPickupDate || '待定'}` : '默认规格';
}

function orderItems(item: OrderView): LocalOrderItem[] {
    if (item.items?.length) {
        return item.items;
    }
    const localOrder = state.localOrders.find((order) => order.id === item.id || order.orderNo === item.orderNo);
    if (localOrder?.items.length) {
        return localOrder.items;
    }
    const fallbackProduct = fallbackProducts[Math.abs(Number(item.id || 0)) % fallbackProducts.length] || fallbackProducts[0];
    return [
        {
            id: Number(`${item.id || 0}01`),
            productId: fallbackProduct.productId,
            productName: fallbackProduct.productName,
            skuName: fallbackProduct.skuName,
            image: fallbackProduct.mainImageUrl,
            salePrice: fallbackProduct.salePrice,
            qty: 1,
            refundableQty: item.tradeStatus === 10 ? 0 : 1,
            afterSaleStatus: item.tradeStatus === 40 || item.fulfillStatus === 40 ? 10 : 0,
            expectedPickupDate: fallbackProduct.deliveryDate || '明日'
        }
    ];
}

function orderItemImage(item: LocalOrderItem) {
    return item.image || findFallbackProduct(item.productId).mainImageUrl || '/static/user-home/shop-home.jpg';
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || '/static/user-home/shop-detail.jpg'}")`
    };
}

function copyOrderNo(orderNo: string) {
    uni.setClipboardData({
        data: orderNo,
        success() {
            showUserToast('订单号已复制');
        }
    });
}

function openPay(id: number) {
    navigateUser(`/pages/payment/index?orderId=${id}`);
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

function goMine() {
    navigateUser('/pages/mine/index', true);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

onMounted(() => {
    applyInitialStatusFilter();
    if (typeof window !== 'undefined') {
        window.addEventListener('hashchange', syncStatusFromHash);
    }
    void loadData();
});

onUnmounted(() => {
    if (typeof window !== 'undefined') {
        window.removeEventListener('hashchange', syncStatusFromHash);
    }
});
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  padding: 0 0 176rpx;
  background: #f7eee5;
}

.order-list-head {
  min-height: 356rpx;
  padding: 88rpx 28rpx 0;
  color: #ffffff;
  background:
    linear-gradient(135deg, #cf4b34 0%, #e78359 58%, #f2bc98 100%) top / 100% 300rpx no-repeat,
    #f7eee5;
  overflow: visible;
}

.order-nav,
.order-card-top,
.order-station,
.order-product-row,
.order-product-preview,
.order-meta-line,
.order-card-actions,
.order-action-buttons {
  display: flex;
  align-items: center;
}

.order-nav {
  display: grid;
  grid-template-columns: 84rpx minmax(0, 1fr) 84rpx;
  gap: 16rpx;
  align-items: center;
  min-height: 60rpx;
}

.order-back {
  width: 60rpx;
  min-width: 60rpx;
  height: 60rpx;
  min-height: 60rpx;
  padding: 0;
  color: #162033 !important;
  background: rgba(255, 255, 255, 0.94) !important;
  border: 0;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 46rpx;
  font-weight: 900;
  line-height: 68rpx;
}

.order-nav b {
  justify-self: center;
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1.2;
  text-shadow: 0 2rpx 4rpx rgba(126, 43, 25, 0.16);
}

.order-nav span {
  justify-self: end;
  color: rgba(255, 255, 255, 0.9);
  font-size: 26rpx;
  line-height: 1;
}

.order-summary-card {
  margin: 28rpx 0 0;
  padding: 24rpx;
  color: #172033;
  border: 1rpx solid rgba(255, 255, 255, 0.76);
  background: rgba(255, 255, 255, 0.96);
  border-radius: 44rpx;
  box-shadow: 0 24rpx 48rpx rgba(117, 69, 40, 0.14);
}

.order-summary-card strong {
  display: block;
  margin: 0 0 18rpx;
  color: #172033;
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1.12;
}

.order-count-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.order-count-grid button,
.order-count-grid :deep(uni-button) {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  min-width: 0;
  min-height: 92rpx;
  padding: 12rpx 10rpx;
  color: #6f584e !important;
  background: #fff9f5 !important;
  border: 1rpx solid #efd7c8;
  border-radius: 22rpx !important;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.15;
  text-align: center;
}

.order-count-grid b {
  display: block;
  overflow: hidden;
  max-width: 100%;
  color: #d84e34;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-panel {
  padding: 20rpx 24rpx 0;
  background: #f7eee5;
}

.order-search-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  height: 72rpx;
  margin: 0 0 18rpx;
  padding: 0 26rpx;
  color: #172033;
  background: #ffffff;
  border: 0;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 40rpx rgba(110, 68, 42, 0.08);
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1;
}

.order-search-row b {
  overflow: hidden;
  min-width: 0;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-search-row span {
  color: #d84e34;
  font-size: 30rpx;
}

.order-tabs {
  display: flex;
  flex-wrap: nowrap;
  gap: 16rpx;
  width: 100%;
  margin: 0 0 16rpx;
  padding: 0 0 4rpx;
  white-space: nowrap;
  box-sizing: border-box;
}

.order-tabs button,
.order-tabs :deep(uni-button) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  min-width: 104rpx;
  height: 58rpx;
  min-height: 58rpx;
  margin-right: 16rpx;
  padding: 0 24rpx;
  color: #6d554b !important;
  background: rgba(255, 255, 255, 0.88) !important;
  border: 1rpx solid #ead7cb;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1;
  box-shadow: none;
}

.order-tabs button.active,
.order-tabs :deep(uni-button.active) {
  color: #ffffff !important;
  background: #172033 !important;
  border-color: #172033;
  box-shadow: 0 16rpx 36rpx rgba(23, 32, 51, 0.18);
}

.order-list {
  display: grid;
  gap: 16rpx;
  padding: 0;
}

.order-card,
.order-empty {
  padding: 22rpx;
  background: #ffffff;
  border: 1rpx solid #efd8ca;
  border-radius: 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(105, 66, 41, 0.05);
  overflow: hidden;
}

.order-card-top {
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 16rpx;
  flex-wrap: nowrap;
}

.order-station {
  align-items: flex-start;
  flex-direction: column;
  gap: 8rpx;
  min-width: 0;
}

.order-station b {
  overflow: hidden;
  max-width: 410rpx;
  color: #142033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.2;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-no-copy,
.order-station :deep(uni-button) {
  justify-content: flex-start;
  min-height: 34rpx;
  padding: 0;
  color: #8a6b5f !important;
  background: transparent !important;
  border-radius: 0 !important;
  box-shadow: none;
  font-size: 26rpx;
  line-height: 1.25;
}

.order-status {
  min-height: 46rpx;
  padding: 0 16rpx;
  color: #9d5b00;
  background: #fff4d8;
  border: 1rpx solid #f1d28f;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 46rpx;
  white-space: nowrap;
  box-shadow: none;
}

.order-status.preparing {
  color: #14724a;
  background: #effaf4;
  border-color: #ccebdc;
}

.order-status.pickup {
  color: #3452a4;
  background: #eef3ff;
  border-color: #d1dcff;
}

.order-status.done {
  color: #172033;
  background: #f2f5fa;
  border-color: #dbe2ee;
}

.order-status.rejected {
  color: #b73518;
  background: #fff1ee;
  border-color: #f3c7bd;
}

.order-product-row {
  min-height: 112rpx;
  gap: 16rpx;
  margin: 16rpx 0;
  padding: 16rpx 0;
  background: transparent;
  border-top: 1rpx solid #f0ddd2;
  border-bottom: 1rpx solid #f0ddd2;
  border-radius: 0;
}

.order-product-preview {
  flex-direction: row-reverse;
  min-width: 116rpx;
}

.order-thumb {
  display: grid;
  width: 96rpx;
  height: 96rpx;
  margin-left: -54rpx;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  border: 4rpx solid #ffffff;
  border-radius: 24rpx;
  font-weight: 900;
  place-items: center;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.1);
  overflow: hidden;
}

.order-thumb-img {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 20rpx;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.order-product-main {
  min-width: 0;
  flex: 1;
}

.order-product-main h4,
.order-product-main p {
  overflow: hidden;
  margin: 0;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-product-main h4 {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.2;
}

.order-product-main p {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 25rpx;
  line-height: 1.35;
}

.order-arrow {
  color: #b07655;
  font-size: 36rpx;
  font-weight: 900;
}

.order-meta-line {
  justify-content: space-between;
  flex-wrap: nowrap;
  gap: 16rpx;
  margin-top: 0;
  color: #7b6258;
  font-size: 26rpx;
  line-height: 1.25;
}

.order-total {
  color: #172033;
  font-size: 26rpx;
  font-weight: 800;
  text-align: right;
  white-space: nowrap;
}

.order-total b {
  color: #d84e34;
  font-size: 30rpx;
  font-weight: 900;
}

.order-card-actions {
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
}

.pay-countdown {
  display: inline-flex;
  align-items: center;
  min-height: 48rpx;
  padding: 0 16rpx;
  color: #b06c00;
  background: #fff4d8;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
  white-space: nowrap;
}

.order-action-buttons {
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-left: auto;
}

.order-action-buttons button,
.order-action-buttons :deep(uni-button) {
  min-width: 0;
  height: 56rpx;
  min-height: 56rpx;
  padding: 0 28rpx;
  color: #c2412d !important;
  background: #ffffff !important;
  border: 1rpx solid #f1c9bb;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1;
  box-shadow: none;
}

.order-action-buttons button.primary,
.order-action-buttons :deep(uni-button.primary) {
  color: #ffffff !important;
  background: #d84e34 !important;
  border-color: #d84e34;
  box-shadow: 0 10rpx 24rpx rgba(216, 78, 52, 0.18);
}

.order-empty {
  display: grid;
  justify-items: center;
  gap: 12rpx;
  padding: 42rpx 24rpx;
  text-align: center;
}

.order-empty b {
  color: #172033;
  font-size: 32rpx;
}

.order-empty span {
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.45;
}

.order-empty button {
  min-height: 64rpx;
  margin-top: 8rpx;
  padding: 0 26rpx;
  font-size: 26rpx;
}
</style>
