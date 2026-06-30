<template>
  <view class="order-detail-page shop-page" data-m-page="afterSaleApply">
    <view class="order-detail-hero">
      <view class="order-hero-img" :style="backgroundImageStyle(heroImage)" />
      <view class="order-hero-mask"></view>
      <view class="order-detail-nav">
        <button class="order-detail-back" @click="goOrder">‹</button>
        <b>申请售后</b>
        <span>退款/退货</span>
      </view>
      <view class="order-detail-status">
        <span class="order-status">售后</span>
        <h2>提交售后申请</h2>
        <p>确认商品后填写售后资料。</p>
      </view>
    </view>

    <view class="order-detail-body">
      <view class="after-sale-expire-card">
        <b>售后期校验</b>
        自提后 24 小时内可申请售后，提交后等待运营审核处理。
      </view>

      <view v-if="order" class="order-info-card">
        <h3>选择售后商品</h3>
        <view class="after-sale-rule-tip after-sale-process-tip">按订单商品明细申请售后，选择商品后填写数量、原因、图片凭证和备注。</view>
        <view class="after-sale-select-list">
          <view
            v-for="item in order.items"
            :key="item.id"
            class="after-sale-select-row"
            :class="{ active: selectedItemIds.includes(item.id) }"
            @click="toggleItem(item.id)"
          >
            <view class="thumb" :style="backgroundImageStyle(item.image)" />
            <view>
              <b>{{ item.productName }}</b>
              <span>{{ item.skuName }}<br />可售后 {{ item.refundableQty }} 件</span>
            </view>
            <button>{{ selectedItemIds.includes(item.id) ? '已选' : '选择' }}</button>
          </view>
        </view>
      </view>

      <view v-else class="order-info-card">
        <h3>暂无可申请售后的订单</h3>
        <p>请从订单列表选择可售后的商品。</p>
      </view>

      <view v-if="selectedItems.length" class="order-info-card">
        <h3>商品信息</h3>
        <view v-for="item in selectedItems" :key="item.id" class="detail-product">
          <view class="order-thumb" :style="backgroundImageStyle(item.image)" />
          <view class="order-product-main">
            <view class="order-product-date-row">预计提货日：<span>{{ item.expectedPickupDate || '待定' }}</span></view>
            <h4>{{ item.productName }}</h4>
            <p>{{ item.skuName }}</p>
            <view class="detail-price-line">
              <span class="order-product-price-qty"><b>¥{{ lineAmount(item) }}</b><em>x{{ item.qty }}</em></span>
            </view>
          </view>
        </view>
      </view>

      <view class="order-info-card after-sale-card after-sale-apply-focus">
        <h3>申请售后 <span>必填信息</span></h3>
        <view class="after-sale-summary-grid">
          <view>
            <b>{{ selectedQty }}</b>
            <span>申请数量</span>
          </view>
          <view>
            <b>¥{{ expectedRefundAmount }}</b>
            <span>预计退款</span>
          </view>
          <view>
            <b>最多6张</b>
            <span>图片凭证</span>
          </view>
        </view>

        <view class="after-sale-form">
          <label>
            售后类型
            <view class="after-sale-type-grid">
              <button
                v-for="item in typeTabs"
                :key="item.value"
                type="button"
                class="after-sale-type-card"
                :class="{ active: afterSaleType === item.value }"
                @click="afterSaleType = item.value"
              >
                <b>{{ item.label }}</b>
                <span>{{ item.desc }}</span>
              </button>
            </view>
          </label>

          <label>
            申请数量
            <view class="after-sale-qty-control">
              <button type="button" @click="decreaseQty">-</button>
              <input v-model="applyQty" type="number" />
              <button type="button" @click="increaseQty">+</button>
              <span>最多{{ maxRefundableQty }}件，累计不超购买数量</span>
            </view>
          </label>

          <view class="after-sale-amount-card">
            <b>退款金额</b>
            <strong>¥{{ refundAmount || expectedRefundAmount }}</strong>
            <span>{{ normalizedApplyQty }} 件商品预计退款</span>
          </view>

          <label>
            售后原因
            <view class="reason-grid">
              <button
                v-for="item in reasonOptions"
                :key="item"
                type="button"
                :class="{ active: reasonType === item }"
                @click="reasonType = item"
              >
                {{ item }}
              </button>
            </view>
          </label>

          <label>
            退款金额
            <input v-model="refundAmount" type="digit" placeholder="请输入退款金额" />
          </label>

          <label>
            图片凭证（最多6张）
            <view class="order-review-images after-sale-proof-grid">
              <button
                v-for="item in proofSamples"
                :key="item"
                type="button"
                :style="backgroundImageStyle(item)"
                disabled
              >
                <span>示例</span>
              </button>
              <button type="button" class="upload-btn" @click="showUploadTip">上传</button>
              <span class="review-upload-tip">请上传清晰图片凭证。</span>
            </view>
          </label>

          <label>
            售后备注（200字以内）
            <textarea v-model="reason" maxlength="200" placeholder="请填写少件、破损、变质、错发等情况，200 字以内" />
          </label>
        </view>
      </view>

      <view class="after-sale-progress-card">
        <b>售后进度</b>
        <view class="after-sale-progress-list">
          <span v-for="node in progressNodes" :key="node" :class="{ active: node === '审核中' }">{{ node }}</span>
        </view>
      </view>

      <view class="refund-breakdown-card">
        <b>退款金额明细</b>
        <view class="refund-breakdown-grid">
          <span>
            商品金额
            <em>¥{{ expectedRefundAmount }}</em>
          </span>
          <span>
            实退金额
            <em>¥{{ refundAmount || expectedRefundAmount }}</em>
          </span>
        </view>
      </view>

      <view class="order-info-card return-address-card">
        <h3>退货地址</h3>
        <view class="detail-field">
          <span>退货自提点</span>
          <b>{{ order?.stationName || state.station.name }}</b>
        </view>
        <view class="detail-field">
          <span>退货地址</span>
          <b>{{ stationAddress }}</b>
        </view>
      </view>

    </view>

    <view class="detail-action-row">
      <button @click="goOrder">返回订单列表</button>
      <button class="primary" :disabled="!canSubmit" @click="submitApply">提交申请</button>
    </view>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { findLocalOrder, navigateUser, showUserToast, type LocalOrder, type LocalOrderItem, useUserState } from '@/stores/userState';

const state = useUserState();
const order = ref<LocalOrder>();
const selectedItemIds = ref<number[]>([]);
const afterSaleType = ref(1);
const applyQty = ref('1');
const refundAmount = ref('');
const reason = ref('');
const reasonType = ref('少件/缺货');
const typeTabs = [
    { label: '退货退款', value: 2, desc: '提交后等待审核处理' },
    { label: '仅退款', value: 1, desc: '少件缺货可直接退款' }
];
const reasonOptions = ['少件/缺货', '破损', '变质', '错发', '其他'];
const progressNodes = ['审核中', '待退货', '退款中', '退款完成', '退款失败', '已驳回', '已撤销', '已关闭'];
const fallbackOrderImage = '/static/user-home/shop-detail.jpg';
const proofSamples = ['/static/user-home/shop-detail.jpg', '/static/user-home/shop-list.jpg', '/static/user-home/shop-category.jpg'];
const selectedItems = computed(() => order.value?.items.filter((item) => selectedItemIds.value.includes(item.id)) || []);
const heroImage = computed(() => selectedItems.value[0]?.image || order.value?.items[0]?.image || fallbackOrderImage);
const maxRefundableQty = computed(() => selectedItems.value.reduce((sum, item) => sum + Number(item.refundableQty || item.qty || 0), 0));
const normalizedApplyQty = computed(() => {
    const value = Math.max(1, Math.floor(Number(applyQty.value || 1)));
    const max = Math.max(1, maxRefundableQty.value || 1);
    return Math.min(value, max);
});
const selectedQty = computed(() => selectedItems.value.reduce((sum, item) => sum + Number(item.qty || 1), 0));
const expectedRefundAmount = computed(() => {
    const first = selectedItems.value[0];
    const unitPrice = Number(first?.salePrice || 0);
    return (unitPrice * normalizedApplyQty.value).toFixed(2);
});
const stationAddress = computed(() => state.stations.find((item) => item.id === order.value?.stationId)?.address || state.station.address);
const canSubmit = computed(() => selectedItemIds.value.length > 0 && Number(refundAmount.value || expectedRefundAmount.value) > 0 && !!reasonType.value);

function loadOrder() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    const id = Number(current.options?.orderId || current.options?.id || 0);
    order.value = findLocalOrder(id);
    const first = order.value?.items[0];
    if (first) {
        selectedItemIds.value = [first.id];
        refundAmount.value = first.salePrice;
        applyQty.value = String(Math.max(1, Number(first.refundableQty || first.qty || 1)));
    }
}

function toggleItem(id: number) {
    selectedItemIds.value = selectedItemIds.value.includes(id)
        ? selectedItemIds.value.filter((item) => item !== id)
        : [...selectedItemIds.value, id];
    const nextFirst = order.value?.items.find((item) => selectedItemIds.value.includes(item.id));
    if (nextFirst) {
        applyQty.value = String(Math.max(1, Number(nextFirst.refundableQty || nextFirst.qty || 1)));
        refundAmount.value = expectedRefundAmount.value;
    }
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackOrderImage}")`
    };
}

function lineAmount(item: LocalOrderItem) {
    return (Number(item.salePrice || 0) * Number(item.qty || 1)).toFixed(2);
}

function decreaseQty() {
    applyQty.value = String(Math.max(1, normalizedApplyQty.value - 1));
    refundAmount.value = expectedRefundAmount.value;
}

function increaseQty() {
    applyQty.value = String(Math.min(Math.max(1, maxRefundableQty.value), normalizedApplyQty.value + 1));
    refundAmount.value = expectedRefundAmount.value;
}

function showUploadTip() {
    showUserToast('图片上传接口暂缺，当前展示凭证占位', 'warn');
}

function submitApply() {
    if (!canSubmit.value) {
        showUserToast('请选择商品和售后原因', 'warn');
        return;
    }
    showUserToast('售后申请接口暂缺，页面已保留申请状态', 'warn');
    setTimeout(() => {
        navigateUser('/pages/aftersale/index', true);
    }, 600);
}

function goOrder() {
    navigateUser('/pages/order/index', true);
}

onMounted(loadOrder);
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
.after-sale-select-row,
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
  font-size: 48rpx;
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
  padding: 24rpx 24rpx 0;
}

.order-info-card {
  margin-bottom: 0;
  padding: 30rpx;
  border-radius: 44rpx;
  box-shadow: 0 32rpx 60rpx rgba(126, 76, 49, 0.09);
}

.order-info-card h3 {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 0 16rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
}

.order-info-card h3 span {
  min-height: 38rpx;
  padding: 0 14rpx;
  color: #e85d3f;
  background: #fff1e9;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 38rpx;
}

.order-info-card p {
  margin: 0;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.45;
}

.after-sale-expire-card,
.after-sale-rule-tip,
.after-sale-progress-card,
.refund-breakdown-card {
  color: #8c6a58;
  background: #ffffff;
  border: 1rpx solid #f1d8cb;
  border-radius: 28rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.08);
}

.after-sale-expire-card {
  padding: 22rpx 24rpx;
  font-size: 26rpx;
  line-height: 1.45;
}

.after-sale-expire-card b {
  display: block;
  margin-bottom: 6rpx;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.after-sale-rule-tip {
  margin-bottom: 14rpx;
  padding: 18rpx 20rpx;
  color: #8a4a2c;
  background: #fff8f3;
  font-size: 26rpx;
  line-height: 1.45;
}

.after-sale-process-tip {
  margin-top: -4rpx;
}

.after-sale-select-list {
  display: grid;
  gap: 14rpx;
}

.after-sale-select-row {
  display: grid;
  grid-template-columns: 124rpx minmax(0, 1fr) auto;
  gap: 20rpx;
  padding: 20rpx;
  background: #fffaf6;
  border: 1rpx solid #f2dfd4;
  border-radius: 28rpx;
}

.after-sale-select-row.active {
  border-color: #f07855;
  background: #fff3ec;
}

.thumb {
  display: grid;
  width: 124rpx;
  min-width: 124rpx;
  height: 124rpx;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7, #f05a37);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border-radius: 18rpx;
  font-weight: 900;
  place-items: center;
}

.after-sale-select-row view:nth-child(2) {
  min-width: 0;
  flex: 1;
}

.after-sale-select-row b,
.after-sale-select-row span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
}

.after-sale-select-row b {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
  white-space: nowrap;
}

.after-sale-select-row span {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 25rpx;
  line-height: 1.4;
}

.after-sale-select-row button {
  min-width: 88rpx;
  min-height: 52rpx;
  padding: 0 16rpx;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  font-size: 25rpx;
  box-shadow: none;
}

.detail-product {
  align-items: flex-start;
  gap: 16rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f2dfd4;
}

.detail-product:last-child {
  border-bottom: 0;
}

.order-thumb {
  display: grid;
  width: 104rpx;
  min-width: 104rpx;
  height: 104rpx;
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
  gap: 12rpx;
  margin-top: 12rpx;
}

.order-product-price-qty b {
  color: #f20d0d;
  font-size: 28rpx;
}

.order-product-price-qty em {
  margin-left: 8rpx;
  color: #8c6a58;
  font-size: 25rpx;
  font-style: normal;
}

.after-sale-summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-bottom: 20rpx;
}

.after-sale-summary-grid view {
  display: grid;
  gap: 8rpx;
  min-width: 0;
  padding: 18rpx 10rpx;
  text-align: center;
  background: #fff8f3;
  border: 1rpx solid #f0dfd6;
  border-radius: 22rpx;
}

.after-sale-summary-grid b {
  overflow: hidden;
  color: #e85d3f;
  font-size: 30rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.after-sale-summary-grid span {
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
}

.after-sale-form {
  display: grid;
  gap: 18rpx;
}

.after-sale-form label {
  display: grid;
  gap: 12rpx;
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.after-sale-form input,
.after-sale-form textarea {
  background: #fffaf6;
}

.after-sale-type-grid,
.reason-grid {
  display: grid;
  gap: 12rpx;
}

.after-sale-type-card {
  display: grid;
  justify-content: start;
  min-height: 92rpx;
  padding: 18rpx 20rpx;
  color: #172033 !important;
  background: #ffffff !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 22rpx;
  box-shadow: none;
  text-align: left;
}

.after-sale-type-card.active {
  color: #c2412d !important;
  background: #fff1e9 !important;
  border-color: #e85d3f;
}

.after-sale-type-card b,
.after-sale-type-card span {
  display: block;
}

.after-sale-type-card b {
  font-size: 26rpx;
}

.after-sale-type-card span {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 25rpx;
}

.after-sale-qty-control {
  display: grid;
  grid-template-columns: 64rpx 112rpx 64rpx minmax(0, 1fr);
  gap: 12rpx;
  align-items: center;
}

.after-sale-qty-control button {
  min-width: 64rpx;
  min-height: 64rpx;
  padding: 0;
  color: #e85d3f !important;
  background: #fff5f1 !important;
  border: 1rpx solid #f2d6c4;
  box-shadow: none;
}

.after-sale-qty-control input {
  min-height: 64rpx;
  padding: 0;
  text-align: center;
}

.after-sale-qty-control span {
  min-width: 0;
  color: #8c6a58;
  font-size: 25rpx;
  font-weight: 800;
  line-height: 1.35;
}

.after-sale-amount-card {
  display: grid;
  gap: 8rpx;
  padding: 20rpx;
  background: #fff8f3;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
}

.after-sale-amount-card b,
.after-sale-amount-card span {
  color: #8c6a58;
  font-size: 26rpx;
}

.after-sale-amount-card strong {
  color: #f20d0d;
  font-size: 42rpx;
  font-weight: 900;
}

.reason-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.reason-grid button {
  min-height: 58rpx;
  padding: 0 12rpx;
  color: #7b5f51 !important;
  background: #ffffff !important;
  border: 1rpx solid #f0dfd6;
  box-shadow: none;
  font-size: 26rpx;
}

.reason-grid button.active {
  color: #ffffff !important;
  background: #e85d3f !important;
  border-color: #e85d3f;
}

.after-sale-proof-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
}

.after-sale-proof-grid button {
  min-height: 112rpx;
  padding: 0;
  color: #8a4a2c !important;
  background: linear-gradient(135deg, #fff4c7, #fff1e9) !important;
  background-position: center !important;
  background-repeat: no-repeat !important;
  background-size: cover !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  box-shadow: none;
  font-size: 25rpx;
}

.after-sale-proof-grid button span {
  padding: 4rpx 10rpx;
  color: #ffffff;
  background: rgba(23, 32, 51, 0.56);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
}

.after-sale-proof-grid .upload-btn {
  color: #e85d3f !important;
  background: #ffffff !important;
}

.review-upload-tip {
  grid-column: 1 / -1;
  color: #8c6a58;
  font-size: 25rpx;
  font-weight: 800;
}

.detail-field {
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 16rpx;
}

.detail-field span {
  flex: 0 0 auto;
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.detail-field input {
  text-align: right;
}

.detail-field b {
  min-width: 0;
  color: #172033;
  font-size: 25rpx;
  font-weight: 900;
  text-align: right;
}

.after-sale-progress-card,
.refund-breakdown-card {
  display: grid;
  gap: 18rpx;
  padding: 30rpx;
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
</style>
