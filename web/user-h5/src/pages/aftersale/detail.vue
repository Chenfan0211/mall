<template>
  <view class="order-detail-page shop-page" data-m-page="afterSaleDetail">
    <view class="order-detail-hero">
      <view class="order-hero-img" :style="backgroundImageStyle(heroImage)" />
      <view class="order-hero-mask"></view>
      <view class="order-detail-nav">
        <button class="order-detail-back" @click="goMessages">‹</button>
        <b>售后详情</b>
        <span>{{ topActionStatus }}</span>
      </view>
      <view class="order-detail-status">
        <span class="order-status">{{ statusLabel }}</span>
        <h2>{{ heroTitle }}</h2>
        <p>{{ heroDesc }}</p>
      </view>
    </view>

    <view class="order-detail-body">
      <view class="order-info-card">
        <h3>售后信息</h3>
        <view class="detail-field">
          <span>订单号</span>
          <b>{{ detail?.orderNo || '-' }}</b>
        </view>
        <view class="detail-field">
          <span>退款金额</span>
          <b>¥{{ detail?.refundAmount || '0.00' }}</b>
        </view>
        <view class="detail-field">
          <span>申请原因</span>
          <b>{{ detail?.applyReason || '-' }}</b>
        </view>
      </view>

      <view class="order-info-card">
        <h3>售后单信息</h3>
        <view class="detail-field">
          <span>售后类型</span>
          <b>{{ afterSaleTypeText }}</b>
        </view>
        <view class="detail-field">
          <span>处理状态</span>
          <b>{{ heroTitle }}</b>
        </view>
        <view class="detail-field">
          <span>申请退款金额</span>
          <b>¥{{ detail?.refundAmount || '0.00' }}</b>
        </view>
        <view class="detail-field">
          <span>售后备注</span>
          <b>{{ detail?.applyReason || '等待运营审核' }}</b>
        </view>
      </view>

      <view class="order-info-card">
        <h3>售后商品</h3>
        <view v-for="item in items" :key="item.id" class="detail-product">
          <view class="order-thumb" :style="backgroundImageStyle(afterSaleProduct(item).image)" />
          <view class="order-product-main">
            <view class="order-product-date-row">SKU：<span>{{ afterSaleProduct(item).skuName }}</span></view>
            <h4>{{ afterSaleProduct(item).productName }}</h4>
            <p>申请 {{ item.applyQty }} 件 / 通过 {{ item.approvedQty }} 件</p>
            <view class="detail-price-line">
              <span class="order-product-price-qty"><b>¥{{ item.refundAmount }}</b></span>
            </view>
          </view>
        </view>
      </view>

      <view class="after-sale-progress-card">
        <b>售后进度</b>
        <view class="after-sale-progress-list">
          <span v-for="node in progressNodes" :key="node" :class="{ active: isProgressActive(node) }">{{ node }}</span>
        </view>
      </view>

      <view class="order-info-card return-address-card">
        <h3>退货地址</h3>
        <view class="detail-field">
          <span>退货自提点</span>
          <b>{{ stationName }}</b>
        </view>
        <view class="detail-field">
          <span>退货地址</span>
          <b>{{ stationAddress }}</b>
        </view>
      </view>

      <view class="refund-breakdown-card">
        <b>退款金额明细</b>
        <view class="refund-breakdown-grid">
          <span>
            商品金额
            <em>¥{{ goodsAmount }}</em>
          </span>
          <span>
            实退金额
            <em>¥{{ detail?.refundAmount || goodsAmount }}</em>
          </span>
        </view>
      </view>

      <view v-if="returns.length" class="order-info-card">
        <h3>退货记录</h3>
        <view v-for="item in returns" :key="item.id" class="timeline-row">
          <view class="dot"></view>
          <view>
            <b>{{ item.returnNo }}</b>
            <span>数量 {{ item.returnQty }} · 状态 {{ item.returnStatus }}</span>
            <em>{{ item.createTime || '时间待同步' }}</em>
          </view>
        </view>
      </view>

      <view class="order-info-card">
        <h3>售后进度</h3>
        <view class="timeline-row">
          <view class="dot"></view>
          <view>
            <b>提交申请</b>
            <span>{{ detail?.applyReason || '等待运营审核' }}</span>
            <em>{{ detail?.createTime || '2026-06-26 20:15:00' }}</em>
          </view>
        </view>
      </view>
    </view>

    <view class="detail-action-row detail-floating-actions">
      <button @click="goMessages">返回消息</button>
      <button @click="goOrder">查看原订单</button>
      <button v-if="Number(detail?.status || 10) === 10" class="primary" @click="cancelVisible = true">撤销申请</button>
      <button v-else disabled>{{ bottomReadonlyText }}</button>
    </view>

    <ConfirmDialog
      :visible="cancelVisible"
      title="确认撤销售后"
      content="撤销后售后单状态保留为已撤销，不会删除历史记录。"
      confirm-text="撤销"
      danger
      @cancel="cancelVisible = false"
      @confirm="cancelAfterSale"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserToast from '@/components/UserToast.vue';
import {
    getAfterSale,
    listAfterSaleItems,
    listReturnRecords,
    type UserAfterSaleDTO,
    type UserAfterSaleItemDTO,
    type UserReturnRecordDTO
} from '@/api/user';
import { navigateUser, showUserToast, statusText, useUserState, type LocalOrderItem } from '@/stores/userState';
import { findFallbackProduct } from '@/utils/userFallbackData';

const state = useUserState();
const afterSaleId = ref(0);
const detail = ref<UserAfterSaleDTO>();
const items = ref<UserAfterSaleItemDTO[]>([]);
const returns = ref<UserReturnRecordDTO[]>([]);
const cancelVisible = ref(false);
const statusLabel = computed(() => statusText('afterSale', detail.value?.status));
const progressNodes = ['审核中', '待退货', '退款中', '退款完成', '退款失败', '已驳回', '已撤销', '已关闭'];
const topActionStatus = computed(() => {
    if ([30, 40].includes(Number(detail.value?.status))) {
        return '只读';
    }
    if ([50, 60].includes(Number(detail.value?.status))) {
        return '已关闭';
    }
    return '处理中';
});
const heroTitle = computed(() => {
    const value = Number(detail.value?.status || 10);
    if (value === 10) {
        return '售后申请审核中';
    }
    if (value === 20) {
        return '售后处理中';
    }
    if (value === 30) {
        return '退款完成';
    }
    if (value === 40) {
        return '售后已驳回';
    }
    if (value === 50) {
        return '售后申请已撤销';
    }
    return '售后处理进度';
});
const heroDesc = computed(() => {
    if (Number(detail.value?.status) === 50) {
        return `撤销原因：${detail.value?.applyReason || '用户主动撤销'}`;
    }
    return detail.value?.applyReason || '查看售后进度和退款结果。';
});
const afterSaleTypeText = computed(() => (Number(detail.value?.afterSaleType || 1) === 2 ? '退货退款' : '仅退款'));
const bottomReadonlyText = computed(() => ([30, 40, 50, 60].includes(Number(detail.value?.status)) ? '只读查看' : '等待处理'));
const currentProgress = computed(() => {
    const value = Number(detail.value?.status || 10);
    if (value === 10) {
        return '审核中';
    }
    if (value === 20) {
        return returns.value.length ? '退款中' : '待退货';
    }
    if (value === 30) {
        return '退款完成';
    }
    if (value === 40) {
        return '已驳回';
    }
    if (value === 50) {
        return '已撤销';
    }
    return '已关闭';
});
const goodsAmount = computed(() => {
    const total = items.value.reduce((sum, item) => sum + Number(item.refundAmount || 0), 0);
    return (total || Number(detail.value?.refundAmount || 0)).toFixed(2);
});
const stationName = computed(() => state.stations.find((item) => item.id === detail.value?.stationId)?.name || state.station.name);
const stationAddress = computed(() => state.stations.find((item) => item.id === detail.value?.stationId)?.address || state.station.address);
const fallbackOrderImage = '/static/user-home/shop-detail.jpg';
const heroImage = computed(() => (items.value[0] ? afterSaleProduct(items.value[0]).image : fallbackOrderImage));

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackOrderImage}")`
    };
}

function afterSaleProduct(item: UserAfterSaleItemDTO) {
    const localItem = state.localOrders
        .flatMap((order) => order.items)
        .find((orderItem: LocalOrderItem) => orderItem.id === item.orderItemId);
    if (localItem) {
        const fallback = findFallbackProduct(localItem.productId);
        return {
            productName: localItem.productName,
            skuName: localItem.skuName,
            image: localItem.image || fallback.mainImageUrl
        };
    }
    const fallback = findFallbackProduct(item.skuId);
    return {
        productName: fallback.productName,
        skuName: fallback.skuName,
        image: fallback.mainImageUrl
    };
}

async function loadDetail() {
    if (!afterSaleId.value) {
        return;
    }
    try {
        detail.value = await getAfterSale(afterSaleId.value, { userId: state.user.id }, { silent: true });
        items.value = await listAfterSaleItems(afterSaleId.value, { userId: state.user.id }, { silent: true });
        returns.value = await listReturnRecords(afterSaleId.value, { userId: state.user.id }, { silent: true });
    } catch {
        detail.value = {
            id: afterSaleId.value,
            afterSaleNo: 'SH202606270001',
            orderId: 880004,
            orderNo: 'DD202606250009',
            userId: state.user.id,
            cityId: state.city.id,
            stationId: state.station.id,
            afterSaleType: 1,
            status: 10,
            applyReason: '商品破损',
            refundAmount: '19.90',
            responsibilityType: 1,
            createTime: '2026-06-26 20:15:00'
        };
        items.value = [
            {
                id: 1,
                afterSaleId: afterSaleId.value,
                orderItemId: 990005,
                skuId: 610005,
                supplierId: 1,
                applyQty: 1,
                approvedQty: 0,
                refundAmount: '19.90'
            }
        ];
        returns.value = [];
        console.info('用户端售后详情接口不可用，已展示本地状态');
    }
}

function cancelAfterSale() {
    cancelVisible.value = false;
    showUserToast('撤销售后接口暂缺，已保留页面状态', 'warn');
}

function goOrder() {
    if (detail.value?.orderId) {
        navigateUser(`/pages/order/detail?id=${detail.value.orderId}`);
    }
}

function goMessages() {
    navigateUser('/pages/message/index', true);
}

function isProgressActive(node: string) {
    const current = currentProgress.value;
    const currentIndex = progressNodes.indexOf(current);
    const terminal = ['退款失败', '已驳回', '已撤销', '已关闭'];
    if (terminal.includes(current)) {
        return node === '审核中' || node === current;
    }
    return progressNodes.indexOf(node) <= currentIndex;
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    afterSaleId.value = Number(current.options?.id || 0);
    void loadDetail();
});
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 100vh;
  padding-bottom: 236rpx;
  background: #f7f1ea;
}

.order-detail-hero {
  position: relative;
  min-height: 472rpx;
  overflow: hidden;
  color: #ffffff;
  background: #172033;
  border-radius: 0;
}

.order-hero-img,
.order-hero-mask {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.order-hero-img {
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.order-hero-mask {
  background:
    linear-gradient(180deg, rgba(23, 32, 51, 0.62), rgba(217, 75, 52, 0.82)),
    radial-gradient(circle at 90% 15%, rgba(255, 210, 165, 0.45), transparent 26%);
}

.order-detail-nav,
.order-detail-status,
.detail-field,
.detail-product,
.detail-price-line,
.timeline-row,
.detail-action-row {
  display: flex;
  align-items: center;
}

.order-detail-nav {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 84rpx minmax(0, 1fr) minmax(148rpx, auto);
  justify-content: initial;
  gap: 16rpx;
  height: auto;
  padding: 88rpx 28rpx 0;
}

.order-detail-back {
  width: 60rpx;
  min-width: 60rpx;
  height: 60rpx;
  min-height: 60rpx;
  padding: 0;
  color: #d94b34 !important;
  background: rgba(255, 255, 255, 0.9) !important;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 46rpx;
  line-height: 50rpx;
}

.order-detail-nav b {
  min-width: 0;
  text-align: center;
  font-size: 34rpx;
  font-weight: 900;
}

.order-detail-nav span {
  justify-self: end;
  max-width: 208rpx;
  padding: 8rpx 16rpx;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  opacity: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-detail-status {
  position: relative;
  left: auto;
  right: auto;
  bottom: auto;
  z-index: 1;
  align-items: flex-start;
  flex-direction: column;
  padding: 48rpx 36rpx 40rpx;
}

.order-status {
  min-height: 42rpx;
  padding: 0 16rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.28);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
  line-height: 42rpx;
}

.order-detail-status h2 {
  margin: 18rpx 0 0;
  font-size: 42rpx;
  font-weight: 900;
  line-height: 1.12;
}

.order-detail-status p {
  margin: 10rpx 0 0;
  font-size: 26rpx;
  line-height: 1.45;
  opacity: 0.9;
}

.order-detail-body {
  display: grid;
  gap: 24rpx;
  padding: 24rpx 24rpx 28rpx;
}

.order-info-card {
  margin-bottom: 0;
  padding: 30rpx;
  border-radius: 44rpx;
  box-shadow: 0 32rpx 60rpx rgba(126, 76, 49, 0.09);
}

.order-info-card h3 {
  margin: 0 0 12rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.order-info-card p {
  margin: 0;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.45;
}

.detail-field {
  justify-content: space-between;
  gap: 18rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.detail-field:last-child {
  border-bottom: 0;
}

.detail-field span {
  color: #8c6a58;
  font-size: 26rpx;
}

.detail-field b {
  min-width: 0;
  color: #172033;
  font-size: 25rpx;
  font-weight: 900;
  text-align: right;
}

.detail-product {
  align-items: flex-start;
  gap: 16rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.order-thumb {
  display: grid;
  width: 104rpx;
  min-width: 104rpx;
  height: 104rpx;
  overflow: hidden;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border-radius: 18rpx;
  font-weight: 900;
  place-items: center;
}

.order-product-main {
  min-width: 0;
  flex: 1;
}

.order-product-date-row {
  color: #8c6a58;
  font-size: 26rpx;
}

.order-product-date-row span {
  color: #e85d3f;
  font-weight: 900;
}

.order-product-main h4,
.order-product-main p {
  overflow: hidden;
  margin: 8rpx 0 0;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-product-main h4 {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.order-product-main p {
  color: #8c6a58;
  font-size: 26rpx;
}

.detail-price-line {
  justify-content: space-between;
  margin-top: 12rpx;
}

.order-product-price-qty b {
  color: #f20d0d;
  font-size: 28rpx;
}

.timeline-row {
  align-items: flex-start;
  gap: 14rpx;
  padding-top: 18rpx;
}

.dot {
  width: 18rpx;
  min-width: 18rpx;
  height: 18rpx;
  margin-top: 8rpx;
  background: #e85d3f;
  border-radius: 50%;
}

.timeline-row b,
.timeline-row span,
.timeline-row em {
  display: block;
}

.timeline-row b {
  color: #172033;
  font-size: 26rpx;
}

.timeline-row span,
.timeline-row em {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 25rpx;
  font-style: normal;
}

.after-sale-progress-card,
.refund-breakdown-card {
  display: grid;
  gap: 18rpx;
  padding: 30rpx;
  color: #172033;
  background: #ffffff;
  border: 1rpx solid #f1d8cb;
  border-radius: 36rpx;
  box-shadow: 0 24rpx 48rpx rgba(126, 76, 49, 0.08);
}

.after-sale-progress-card > b,
.refund-breakdown-card > b {
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.after-sale-progress-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.after-sale-progress-list span {
  min-height: 48rpx;
  padding: 0 18rpx;
  color: #8c6a58;
  background: #fff8f3;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
  line-height: 48rpx;
}

.after-sale-progress-list span.active {
  color: #ffffff;
  background: #e85d3f;
  border-color: #e85d3f;
}

.refund-breakdown-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.refund-breakdown-grid span {
  display: grid;
  gap: 8rpx;
  min-width: 0;
  padding: 18rpx;
  color: #8c6a58;
  background: #fff8f3;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  font-size: 25rpx;
  font-weight: 800;
}

.refund-breakdown-grid em {
  overflow: hidden;
  color: #f20d0d;
  font-size: 30rpx;
  font-style: normal;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-action-row {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  gap: 16rpx;
  width: 100%;
  max-width: 390px;
  min-height: 132rpx;
  margin: 0 auto;
  padding: 16rpx 24rpx 28rpx;
  background: rgba(255, 250, 247, 0.98);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
  backdrop-filter: blur(10rpx);
}

.detail-action-row button {
  flex: 1;
  min-height: 88rpx;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  border-radius: 28rpx !important;
  font-size: 26rpx;
  box-shadow: none;
  font-weight: 900;
}

.detail-action-row button.primary {
  color: #ffffff !important;
  background: #e85d3f !important;
}

.detail-action-row button[disabled] {
  color: #9a8a80 !important;
  background: #f1e9e4 !important;
  border-color: #e4d7cf;
}
</style>
