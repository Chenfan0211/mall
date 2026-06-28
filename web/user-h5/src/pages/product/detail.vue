<template>
  <view class="page detail-page">
    <view class="detail-hero">
      <image v-if="detail?.product?.mainImageUrl" class="detail-image" :src="detail.product.mainImageUrl" mode="aspectFill" />
      <view v-else class="detail-image fallback">商品图</view>
      <button class="detail-back" @click="goBack">‹</button>
      <button class="detail-share" @click="shareProduct">分享</button>
    </view>

    <view class="detail-card product-card">
      <view class="price-line">
        <text class="detail-price">¥{{ selectedSku?.salePrice || '0.00' }}</text>
        <text class="delivery-pill">{{ selectedSku?.deliveryDate || '待定提货' }}</text>
      </view>
      <text class="detail-title">{{ detail?.product?.productName || '商品详情' }}</text>
      <text class="detail-sub">当前自提点可买 · 库存 {{ selectedSku?.availableQty ?? 0 }} · 限购 {{ selectedSku?.limitQty ?? 0 }}</text>
      <view class="detail-actions-row">
        <button class="plain small" @click="favoriteProduct">{{ favoriteText }}</button>
        <button class="plain small" @click="sharePoster">生成海报</button>
      </view>
    </view>

    <view class="detail-card">
      <view class="card-title-row">
        <view>
          <text class="title">选择规格</text>
          <text class="subtle">下单前会再次校验库存</text>
        </view>
      </view>
      <view class="sku-grid">
        <view
          v-for="item in detail?.skus || []"
          :key="item.publishSkuId"
          class="sku-option"
          :class="{ disabled: item.availableQty <= 0, active: selectedPublishSkuId === item.publishSkuId }"
          @click="selectSku(item.publishSkuId, item.availableQty)"
        >
          <text>{{ item.skuName }}</text>
          <text>库存 {{ item.availableQty }}</text>
        </view>
      </view>
    </view>

    <view class="detail-card review-entry" @click="openReviews">
      <view>
        <text class="title">买家评价</text>
        <text class="subtle">{{ detail?.commentCount || 0 }} 条真实评价</text>
      </view>
      <text class="pill">查看</text>
    </view>

    <view class="detail-card">
      <text class="title">履约说明</text>
      <view class="service-grid">
        <view>
          <text>次日达</text>
          <text>按团期到货</text>
        </view>
        <view>
          <text>到店提</text>
          <text>自提点核销</text>
        </view>
        <view>
          <text>售后保障</text>
          <text>支持部分退款</text>
        </view>
      </view>
    </view>

    <view v-if="selectedSku && selectedSku.availableQty <= 0" class="soldout-bar">
      <text>当前规格已售罄</text>
      <button class="plain small" @click="noticeMe">到货提醒</button>
    </view>

    <view class="detail-bottom">
      <button class="cart-action" @click="goCart">购物车</button>
      <button class="buy-action" :disabled="!selectedSku || selectedSku.availableQty <= 0" @click="addCart">加入购物车</button>
      <button class="buy-now-action" :disabled="!selectedSku || selectedSku.availableQty <= 0" @click="buyNow">立即购买</button>
    </view>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { getProductDetail, type UserProductDetailDTO } from '@/api/user';
import {
    addProductToCart,
    navigateUser,
    showUserToast,
    toggleFavorite,
    toggleNotice,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const productId = ref(0);
const detail = ref<UserProductDetailDTO>();
const selectedPublishSkuId = ref<number | undefined>();
const selectedSku = computed(() => (detail.value?.skus || []).find((item) => item.publishSkuId === selectedPublishSkuId.value));
const favoriteText = computed(() => (state.favorites.has(productId.value) ? '已收藏' : '收藏'));

async function loadDetail() {
    if (!productId.value) {
        return;
    }
    try {
        detail.value = await getProductDetail(productId.value, { cityId: state.city.id, stationId: state.station.id });
        selectedPublishSkuId.value = detail.value.skus?.[0]?.publishSkuId;
        if (detail.value.favoriteFlag === 1) {
            state.favorites.add(productId.value);
        }
    } catch (error) {
        showUserToast('商品详情接口暂不可用，请从首页重新进入', 'warn');
    }
}

function selectSku(id: number, availableQty: number) {
    if (availableQty <= 0) {
        return;
    }
    selectedPublishSkuId.value = id;
}

async function addCart() {
    if (!selectedSku.value || !detail.value?.product || selectedSku.value.availableQty <= 0) {
        return;
    }
    if (!state.authenticated) {
        state.afterLoginUrl = `/pages/product/detail?id=${productId.value}`;
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(selectedSku.value, detail.value.product.productName, detail.value.product.id);
}

function buyNow() {
    addCart();
    if (state.authenticated) {
        navigateUser('/pages/checkout/index');
    }
}

function favoriteProduct() {
    if (!productId.value) {
        return;
    }
    toggleFavorite(productId.value);
}

function noticeMe() {
    if (!productId.value) {
        return;
    }
    toggleNotice(productId.value);
}

function shareProduct() {
    showUserToast('已打开微信分享卡片，只展示商品价和自提点信息');
}

function sharePoster() {
    showUserToast('分享海报生成中，当前为前端演示状态', 'loading');
}

function openReviews() {
    uni.navigateTo({ url: `/pages/product/reviews?productId=${productId.value}` });
}

function goCart() {
    navigateUser('/pages/cart/index');
}

function goBack() {
    uni.navigateBack();
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    productId.value = Number(current.options?.id || current.options?.productId || 0);
    void loadDetail();
});
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  padding: 0 0 150rpx;
  background: #f7f1ea;
}

.detail-hero {
  position: relative;
  height: 520rpx;
  overflow: hidden;
  background: #172033;
}

.detail-image {
  display: grid;
  width: 100%;
  height: 100%;
  place-items: center;
  color: #ffffff;
  background:
    linear-gradient(90deg, rgba(23, 32, 51, 0.58), rgba(23, 32, 51, 0.08)),
    linear-gradient(135deg, #263043 0%, #d94b34 58%, #f7b36c 100%);
  font-size: 46rpx;
  font-weight: 900;
}

.detail-back {
  position: absolute;
  left: 22rpx;
  top: 46rpx;
  width: 72rpx;
  min-width: 72rpx;
  height: 72rpx;
  min-height: 72rpx;
  padding: 0;
  color: #172033 !important;
  background: rgba(255, 255, 255, 0.92) !important;
  border-radius: 50% !important;
  font-size: 58rpx;
  line-height: 60rpx;
  box-shadow: 0 8rpx 18rpx rgba(15, 23, 42, 0.18);
}

.detail-share {
  position: absolute;
  right: 22rpx;
  top: 46rpx;
  min-width: 96rpx;
  min-height: 62rpx;
  padding: 0 20rpx;
  color: #172033 !important;
  background: rgba(255, 255, 255, 0.92) !important;
  box-shadow: 0 8rpx 18rpx rgba(15, 23, 42, 0.18);
  font-size: 24rpx;
}

.detail-card {
  margin: 18rpx 20rpx 0;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(126, 76, 49, 0.08);
}

.product-card {
  position: relative;
  z-index: 1;
  margin-top: -40rpx;
}

.price-line,
.review-entry,
.card-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.detail-price {
  color: #d94b34;
  font-size: 48rpx;
  font-weight: 900;
}

.delivery-pill {
  min-height: 44rpx;
  padding: 0 16rpx;
  color: #914b12;
  background: #fff1da;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  line-height: 44rpx;
}

.detail-title {
  display: block;
  margin-top: 14rpx;
  color: #172033;
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1.22;
}

.detail-sub {
  display: block;
  margin-top: 12rpx;
  color: #8c6a58;
  font-size: 24rpx;
  line-height: 1.45;
}

.detail-actions-row {
  display: flex;
  gap: 12rpx;
  margin-top: 18rpx;
}

.sku-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.sku-option {
  min-height: 92rpx;
  padding: 16rpx;
  color: #7b5f51;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx;
}

.sku-option.active {
  color: #c2412d;
  background: #fff1e9;
  border-color: #e85d3f;
}

.sku-option.disabled {
  opacity: 0.56;
}

.sku-option text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sku-option text:first-child {
  font-size: 24rpx;
  font-weight: 900;
}

.sku-option text:last-child {
  margin-top: 8rpx;
  font-size: 22rpx;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 18rpx;
}

.service-grid view {
  min-height: 96rpx;
  padding: 14rpx 10rpx;
  background: #fff7f1;
  border: 1rpx solid #f4e2d8;
  border-radius: 18rpx;
}

.service-grid text {
  display: block;
  text-align: center;
}

.service-grid text:first-child {
  color: #172033;
  font-size: 24rpx;
  font-weight: 900;
}

.service-grid text:last-child {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 20rpx;
}

.detail-bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  display: grid;
  grid-template-columns: 150rpx minmax(0, 1fr) minmax(0, 1fr);
  gap: 0;
  min-height: 132rpx;
  padding: 18rpx 24rpx 28rpx;
  background: rgba(255, 255, 255, 0.97);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
}

.cart-action,
.buy-action,
.buy-now-action {
  min-height: 82rpx;
  font-size: 28rpx;
}

.cart-action {
  color: #c2412d !important;
  background: #fff2e9 !important;
  border: 1rpx solid #f2d6c4;
  border-radius: 999rpx 0 0 999rpx !important;
  box-shadow: none;
}

.buy-action {
  border-radius: 0 !important;
}

.buy-now-action {
  background: #172033 !important;
  border-radius: 0 999rpx 999rpx 0 !important;
}

.soldout-bar {
  position: fixed;
  left: 50%;
  right: auto;
  bottom: 132rpx;
  z-index: 9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  width: min(100vw, 390px);
  padding: 14rpx 24rpx;
  color: #8c6a58;
  background: rgba(255, 250, 246, 0.98);
  border-top: 1rpx solid #f0dfd6;
  transform: translateX(-50%);
  font-size: 24rpx;
  font-weight: 900;
}
</style>
