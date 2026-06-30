<template>
  <view class="page detail-page shop-page" data-m-page="detail">
    <view class="detail-hero">
      <view class="detail-carousel-track">
        <view class="detail-slide" :style="backgroundImageStyle(detailImage)" />
      </view>
      <button class="detail-back" @click="goBack">‹</button>
      <button class="price-alert-btn" :class="{ subscribed: state.notices.has(productId) }" @click="noticeMe">
        {{ state.notices.has(productId) ? '已订阅到货' : '到货提醒' }}
      </button>
      <view class="detail-hero-title">
        <b>{{ productTitle }}</b>
        <span>{{ productDesc }}</span>
      </view>
      <view class="detail-dots"><span></span><span></span><span></span></view>
    </view>

    <view class="detail-price">
      <view>
        <span class="detail-price-main">¥{{ selectedSku?.salePrice || '0.00' }}</span>
      </view>
      <view class="pickup-info">
        <b>{{ selectedSku && selectedSku.availableQty <= 0 ? '已售罄' : `剩余${selectedSku?.availableQty ?? 0}份` }}</b>
        <span class="pickup-date">预计提货日 {{ productPickupText(selectedSku?.deliveryDate) }}</span>
      </view>
    </view>

    <view class="detail-card product-main-card">
      <h2>{{ productTitle }}</h2>
      <p>{{ productDesc }}</p>
      <view class="supplier-line">商品供应商 <span>平台优选供应商</span></view>
      <view class="detail-meta-grid">
        <span><b>产地</b>产地直采</span>
        <span><b>购买人数</b><em>{{ selectedSkuSoldQty }}</em>人</span>
      </view>
      <button class="detail-rank-card" @click="openRank">
        <em>榜单</em>
        <span>自提点热卖榜</span>
        <b>第 3 名</b>
      </button>
    </view>

    <view class="detail-card">
      <h3>商品规格</h3>
      <view class="sku-grid">
        <view
          v-for="item in detail?.skus || []"
          :key="item.publishSkuId"
          class="sku-option"
          :class="{ disabled: item.availableQty <= 0, active: selectedPublishSkuId === item.publishSkuId }"
          @click="selectSku(item.publishSkuId, item.availableQty)"
        >
          <b>{{ item.skuName }}</b>
          <small>{{ item.availableQty <= 0 ? '已售罄' : `¥${item.salePrice} · 剩余${item.availableQty}` }}</small>
        </view>
      </view>
    </view>

    <view class="detail-card detail-review-section">
      <view class="detail-review-title">
        <h3>买家评价（{{ detail?.commentCount || 0 }}）</h3>
        <button @click="openReviews">全部评价 ›</button>
      </view>
      <view class="detail-review-summary">
        <view><strong>98<small>.6%</small></strong><span>好评率</span></view>
        <view><b>{{ selectedSkuSoldQty * 7 }}</b><em>近期已售</em></view>
        <view><b>48</b><em>近期回购用户</em></view>
      </view>
      <view class="detail-review-tags">
        <button>新鲜 <em>42</em></button>
        <button>划算 <em>31</em></button>
        <button>提货方便 <em>26</em></button>
      </view>
      <view class="detail-review-card preview">
        <view>
          <view class="detail-review-head">
            <span class="detail-review-user">
              <span class="detail-review-user-main">
                <span class="detail-review-user-line">
                  <span>评价记录</span>
                  <span class="detail-review-rebuy">已回购 3 次</span>
                </span>
                <span><span class="detail-stars">★★★★★</span><span class="detail-review-satisfy"> 非常满意</span></span>
              </span>
            </span>
          </view>
          <p>包装完整，提货点取货很快，商品到手状态不错。</p>
        </view>
        <button class="detail-review-preview-img" @click="previewReviewImage" aria-label="查看评价图片">
          <view class="detail-review-img" :style="backgroundImageStyle(reviewPreviewImage)" />
          <span>3</span>
        </button>
      </view>
    </view>

    <view class="detail-card detail-buyer-record-card">
      <h3>购买记录</h3>
      <view class="buyer-list">
        <view v-for="(item, index) in buyerRecords" :key="item.time" class="buyer-row">
          <view class="buyer-avatar">单</view>
          <view><b>购买记录 {{ index + 1 }}</b>{{ item.time }} · {{ state.station.name }} · {{ selectedSku?.skuName }}</view>
          <em>x{{ item.qty }}</em>
        </view>
      </view>
    </view>

    <view class="detail-card recommend-section">
      <h3>同类推荐 <span>当前自提点可买</span></h3>
      <view class="recommend-grid">
        <view v-for="item in recommendProducts" :key="item.publishSkuId" class="recommend-card" @click="openProduct(item.productId)">
          <view class="recommend-img" :style="backgroundImageStyle(item.mainImageUrl)" />
          <b>{{ item.productName }}</b>
          <p>{{ productSpecText(item) }}</p>
          <view class="recommend-sale-meta">
            <span v-if="Number(item.soldQty || 0) > 0">已售{{ item.soldQty }}</span>
            <span><b>{{ item.availableQty <= 0 ? '已售罄' : `剩余${item.availableQty}` }}</b></span>
          </view>
          <view class="recommend-tag-row">
            <span class="recommend-date">提货日期 {{ productPickupText(item.deliveryDate) }}</span>
          </view>
          <view class="recommend-bottom">
            <view class="recommend-price-stack">
              <span>¥{{ item.salePrice }}</span>
              <em v-if="productOldPrice(item)">¥{{ productOldPrice(item) }}</em>
            </view>
            <button @click.stop="handleRecommendAdd(item)">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M3.5 4.5h2.4L8 15.7h9.7l2.2-7.8H7.2"></path>
                <path d="M12 11h6"></path>
                <path d="M15 8v6"></path>
              </svg>
            </button>
          </view>
        </view>
      </view>
    </view>

    <view class="detail-bottom">
      <button class="cart-action" :disabled="!selectedSku || selectedSku.availableQty <= 0" @click="addCart">
        {{ selectedSku && selectedSku.availableQty <= 0 ? '已售罄' : '加入购物车' }}
      </button>
      <button v-if="selectedSku && selectedSku.availableQty <= 0" class="red" @click="noticeMe">到货提醒</button>
      <button v-else class="red" :disabled="!selectedSku" @click="buyNow">立即购买</button>
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import UserToast from '@/components/UserToast.vue';
import { getProductDetail, type UserProductCardDTO, type UserProductDetailDTO } from '@/api/user';
import {
    addProductToCart,
    navigateUser,
    showUserToast,
    toggleNotice,
    useUserState
} from '@/stores/userState';
import {
    buildFallbackDetail,
    fallbackProductImages,
    fallbackProducts,
    productPickupText,
    productSpecText
} from '@/utils/userFallbackData';
import { currentRouteQuery } from '@/utils/routeQuery';

const state = useUserState();
const productId = ref(0);
const detail = ref<UserProductDetailDTO>();
const selectedPublishSkuId = ref<number | undefined>();
const selectedSku = computed(() => (detail.value?.skus || []).find((item) => item.publishSkuId === selectedPublishSkuId.value));
const productTitle = computed(() => detail.value?.product?.productName || '商品详情');
const productDesc = computed(() => selectedSku.value?.saleSpecText || selectedSku.value?.skuName || '基地直采 / 当前自提点可买 / 次日到团点');
const detailImage = computed(() => detail.value?.product?.mainImageUrl || fallbackProductImages[0]);
const reviewPreviewImage = computed(() => fallbackProductImages[1] || detailImage.value);
const selectedSkuSoldQty = computed(() => {
    const product = fallbackProducts.find((item) => item.publishSkuId === selectedPublishSkuId.value || item.productId === productId.value);
    return Number(product?.soldQty || 86);
});
const recommendProducts = computed(() => fallbackProducts.filter((item) => item.productId !== productId.value).slice(0, 4));
const buyerRecords = [
    { time: '刚刚下单', qty: 1 },
    { time: '12 分钟前', qty: 2 },
    { time: '28 分钟前', qty: 1 }
];

async function loadDetail() {
    if (!productId.value) {
        detail.value = buildFallbackDetail(610001);
        selectedPublishSkuId.value = detail.value.skus?.[0]?.publishSkuId;
        return;
    }
    try {
        detail.value = await getProductDetail(productId.value, { cityId: state.city.id, stationId: state.station.id }, { silent: true });
        if (!detail.value?.product || !detail.value.skus?.length) {
            detail.value = buildFallbackDetail(productId.value);
        }
        selectedPublishSkuId.value = detail.value.skus?.find((item) => item.availableQty > 0)?.publishSkuId || detail.value.skus?.[0]?.publishSkuId;
        if (detail.value.favoriteFlag === 1) {
            state.favorites.add(productId.value);
        }
    } catch {
        detail.value = buildFallbackDetail(productId.value);
        selectedPublishSkuId.value = detail.value.skus?.find((item) => item.availableQty > 0)?.publishSkuId || detail.value.skus?.[0]?.publishSkuId;
        console.info('用户端商品详情接口不可用，已展示本地兜底详情');
    }
}

function selectSku(id: number, availableQty: number) {
    if (availableQty <= 0) {
        showUserToast('该规格已售罄，可订阅到货提醒', 'warn');
        return;
    }
    selectedPublishSkuId.value = id;
}

function requireAuth() {
    if (state.authenticated) {
        return true;
    }
    state.afterLoginUrl = `/pages/product/detail?id=${productId.value}`;
    showUserToast('请先登录，登录后可加入购物车', 'warn');
    navigateUser('/pages/login/index');
    return false;
}

function skuToProduct(): UserProductCardDTO | undefined {
    if (!selectedSku.value || !detail.value?.product) {
        return undefined;
    }
    return {
        publishSkuId: selectedSku.value.publishSkuId,
        periodId: selectedSku.value.periodId,
        productId: detail.value.product.id,
        skuId: selectedSku.value.skuId,
        productName: detail.value.product.productName,
        skuName: selectedSku.value.skuName,
        mainImageUrl: detail.value.product.mainImageUrl,
        saleSpecText: selectedSku.value.saleSpecText,
        salePrice: selectedSku.value.salePrice,
        limitQty: selectedSku.value.limitQty,
        minBuyQty: selectedSku.value.minBuyQty,
        plannedStockQty: selectedSku.value.availableQty + selectedSkuSoldQty.value,
        soldQty: selectedSkuSoldQty.value,
        lockedQty: 0,
        availableQty: selectedSku.value.availableQty,
        deliveryDate: selectedSku.value.deliveryDate
    };
}

function addCart() {
    if (!requireAuth()) {
        return;
    }
    const product = skuToProduct();
    if (!product || product.availableQty <= 0) {
        return;
    }
    addProductToCart(product);
}

function buyNow() {
    addCart();
    if (state.authenticated) {
        navigateUser('/pages/checkout/index');
    }
}

function handleRecommendAdd(item: UserProductCardDTO) {
    if (!requireAuth()) {
        return;
    }
    addProductToCart(item);
}

function productOldPrice(item: UserProductCardDTO) {
    const value = Number(item.salePrice || 0);
    if (!Number.isFinite(value) || value <= 0) {
        return '';
    }
    return (value * 1.25).toFixed(2);
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

function noticeMe() {
    if (!productId.value) {
        return;
    }
    toggleNotice(productId.value);
}

function previewReviewImage() {
    uni.previewImage({
        urls: [reviewPreviewImage.value],
        current: reviewPreviewImage.value
    });
}

function openReviews() {
    const realProductId = detail.value?.product?.id || productId.value;
    uni.navigateTo({ url: `/pages/product/reviews?productId=${realProductId}` });
}

function openRank() {
    navigateUser('/pages/rank/index');
}

function openProduct(id: number) {
    navigateUser(`/pages/product/detail?id=${id}`, true);
}

function goBack() {
    uni.navigateBack({
        fail() {
            navigateUser('/pages/home/index', true);
        }
    });
}

onMounted(() => {
    const query = currentRouteQuery();
    productId.value = Number(query.id || query.productId || 610001);
    void loadDetail();
});
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  padding: 0 0 160rpx;
  overflow-x: hidden;
  background: #f7f1ea;
}

.detail-hero {
  position: relative;
  height: 504rpx;
  overflow: hidden;
  background: #fff3ea;
}

.detail-carousel-track,
.detail-slide {
  width: 100%;
  height: 100%;
}

.detail-slide {
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.detail-hero::after {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(180deg, transparent 45%, rgba(7, 34, 25, 0.74));
  content: "";
}

.detail-back,
.price-alert-btn {
  position: absolute;
  top: 52rpx;
  z-index: 2;
  min-height: 62rpx;
  color: #172033 !important;
  background: rgba(255, 255, 255, 0.94) !important;
  box-shadow: 0 8rpx 18rpx rgba(15, 23, 42, 0.18);
}

.detail-back {
  left: 22rpx;
  width: 68rpx;
  min-width: 68rpx;
  padding: 0;
  border-radius: 50% !important;
  font-size: 54rpx;
  line-height: 56rpx;
}

.price-alert-btn {
  right: 22rpx;
  padding: 0 22rpx;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  font-weight: 900;
}

.price-alert-btn.subscribed {
  color: #e85d3f !important;
}

.detail-hero-title {
  position: absolute;
  left: 28rpx;
  right: 28rpx;
  bottom: 32rpx;
  z-index: 2;
  color: #ffffff;
}

.detail-hero-title b,
.detail-hero-title span {
  display: block;
}

.detail-hero-title b {
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.15;
}

.detail-hero-title span {
  margin-top: 8rpx;
  font-size: 26rpx;
  line-height: 1.45;
  opacity: 0.92;
}

.detail-dots {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 14rpx;
  z-index: 2;
  display: flex;
  justify-content: center;
  gap: 8rpx;
}

.detail-dots span {
  width: 14rpx;
  height: 14rpx;
  background: rgba(255, 255, 255, 0.52);
  border-radius: 999rpx;
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.2);
}

.detail-dots span:first-child {
  width: 36rpx;
  background: #ffffff;
}

.detail-price {
  display: grid;
  grid-template-columns: 264rpx minmax(0, 1fr);
  align-items: end;
  gap: 24rpx;
  margin: 0;
  padding: 24rpx 36rpx 28rpx;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 0 0 36rpx 36rpx;
  box-shadow: none;
}

.detail-price-main {
  color: #ffffff;
  font-size: 60rpx;
  font-weight: 900;
  line-height: 1;
}

.pickup-info {
  display: grid;
  justify-items: end;
  gap: 10rpx;
  color: #ffffff;
  text-align: right;
}

.pickup-info b {
  color: #ffffff;
  font-size: 28rpx;
}

.pickup-date {
  display: inline-flex;
  align-items: center;
  padding: 8rpx 16rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 800;
  white-space: nowrap;
}

.detail-card {
  margin: 20rpx 24rpx 0;
  padding: 24rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 24rpx rgba(126, 76, 49, 0.07);
}

.detail-card h2,
.detail-card h3 {
  margin: 0;
  color: #172033;
  font-weight: 900;
}

.detail-card h2 {
  font-size: 36rpx;
  line-height: 1.22;
}

.detail-card h3 {
  font-size: 30rpx;
}

.detail-card p {
  margin: 12rpx 0 0;
  color: #8c6a58;
  font-size: 26rpx;
  line-height: 1.5;
}

.supplier-line,
.detail-meta-grid,
.detail-rank-card,
.sku-grid,
.detail-review-title,
.detail-review-summary,
.detail-review-tags,
.detail-review-head,
.buyer-row,
.recommend-bottom,
.detail-bottom {
  display: flex;
  align-items: center;
}

.supplier-line {
  justify-content: space-between;
  margin-top: 18rpx;
  padding: 14rpx 16rpx;
  color: #8c6a58;
  background: #fff8f2;
  border-radius: 18rpx;
  font-size: 26rpx;
}

.supplier-line span {
  color: #172033;
  font-weight: 900;
}

.detail-meta-grid {
  gap: 12rpx;
  margin-top: 14rpx;
}

.detail-meta-grid span {
  flex: 1;
  padding: 16rpx;
  color: #8c6a58;
  background: #fff7f1;
  border-radius: 18rpx;
  font-size: 25rpx;
}

.detail-meta-grid b {
  display: block;
  margin-bottom: 6rpx;
  color: #172033;
  font-size: 26rpx;
}

.detail-meta-grid em {
  color: #e85d3f;
  font-style: normal;
  font-weight: 900;
}

.detail-rank-card {
  justify-content: space-between;
  gap: 12rpx;
  width: 100%;
  min-height: 70rpx;
  margin-top: 16rpx;
  padding: 0 18rpx;
  color: #172033 !important;
  background: #fff1da !important;
  border: 0;
  border-radius: 18rpx !important;
  box-shadow: none;
}

.detail-rank-card em {
  color: #e85d3f;
  font-style: normal;
  font-weight: 900;
}

.detail-rank-card span {
  flex: 1;
  text-align: left;
}

.sku-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 18rpx;
}

.sku-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  min-height: 88rpx;
  padding: 14rpx 12rpx;
  color: #7b5f51;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  text-align: center;
}

.sku-option.active {
  color: #c2412d;
  background: #fff1e9;
  border-color: #e85d3f;
  box-shadow: 0 16rpx 36rpx rgba(232, 93, 63, 0.12);
}

.sku-option.disabled {
  background: #f4f5f7;
  border-color: #e4e7ec;
  opacity: 0.56;
}

.sku-option b,
.sku-option small {
  display: block;
  overflow: hidden;
  width: 100%;
  text-overflow: ellipsis;
}

.sku-option b {
  font-size: 25rpx;
  line-height: 1.2;
  white-space: normal;
  word-break: break-word;
}

.sku-option small {
  font-size: 26rpx;
  line-height: 1.15;
  white-space: nowrap;
}

.detail-review-title {
  justify-content: space-between;
}

.detail-review-title button {
  min-height: 48rpx;
  padding: 0;
  color: #e85d3f !important;
  background: transparent !important;
  font-size: 26rpx;
  box-shadow: none;
}

.detail-review-summary {
  gap: 12rpx;
  margin-top: 18rpx;
}

.detail-review-summary view {
  flex: 1;
  padding: 16rpx 8rpx;
  background: #fff7f1;
  border-radius: 18rpx;
  text-align: center;
}

.detail-review-summary strong,
.detail-review-summary b {
  display: block;
  color: #e85d3f;
  font-size: 30rpx;
  font-weight: 900;
}

.detail-review-summary small,
.detail-review-summary span,
.detail-review-summary em {
  color: #8c6a58;
  font-size: 26rpx;
  font-style: normal;
}

.detail-review-tags {
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 16rpx;
}

.detail-review-tags button {
  min-height: 48rpx;
  padding: 0 14rpx;
  color: #744b39 !important;
  background: #fff5ef !important;
  border: 1rpx solid #f2dfd4;
  border-radius: 999rpx !important;
  font-size: 25rpx;
  box-shadow: none;
}

.detail-review-card {
  display: flex;
  align-items: stretch;
  gap: 14rpx;
  margin-top: 16rpx;
  padding: 16rpx;
  background: #fffaf6;
  border-radius: 18rpx;
}

.detail-review-card > view {
  flex: 1;
  min-width: 0;
}

.detail-review-card p {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.detail-review-head {
  justify-content: space-between;
  gap: 12rpx;
  color: #8c6a58;
  font-size: 25rpx;
}

.detail-review-user {
  color: #172033;
  font-weight: 900;
}

.detail-review-user-main,
.detail-review-user-line {
  display: grid;
  gap: 4rpx;
}

.detail-review-user-line {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
}

.detail-review-rebuy,
.detail-review-satisfy {
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 700;
}

.detail-stars {
  color: #f5a524;
}

.detail-review-preview-img {
  position: relative;
  width: 118rpx;
  min-width: 118rpx;
  height: 118rpx;
  min-height: 118rpx;
  padding: 0;
  overflow: hidden;
  background: #fff3ea !important;
  border-radius: 16rpx !important;
  box-shadow: none;
}

.detail-review-img {
  display: block;
  width: 100%;
  height: 100%;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.detail-review-preview-img span {
  position: absolute;
  right: 8rpx;
  bottom: 8rpx;
  display: grid;
  min-width: 40rpx;
  height: 40rpx;
  padding: 0 8rpx;
  color: #ffffff;
  background: rgba(23, 32, 51, 0.62);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  place-items: center;
}

.buyer-list {
  display: grid;
  gap: 14rpx;
  margin-top: 18rpx;
}

.buyer-row {
  gap: 14rpx;
  padding: 14rpx;
  color: #8c6a58;
  background: #fffaf6;
  border-radius: 18rpx;
  font-size: 25rpx;
}

.buyer-avatar {
  display: grid;
  width: 52rpx;
  min-width: 52rpx;
  height: 52rpx;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 50%;
  font-weight: 900;
  place-items: center;
}

.buyer-row view:nth-child(2) {
  flex: 1;
  min-width: 0;
}

.buyer-row b {
  display: block;
  margin-bottom: 4rpx;
  color: #172033;
}

.buyer-row em {
  color: #e85d3f;
  font-style: normal;
  font-weight: 900;
}

.recommend-section h3 {
  display: flex;
  justify-content: space-between;
}

.recommend-section h3 span {
  color: #8c6a58;
  font-size: 25rpx;
  font-weight: 600;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 18rpx;
}

.recommend-card {
  min-width: 0;
  padding: 14rpx;
  background: #fffaf6;
  border: 1rpx solid #f1dfd4;
  border-radius: 20rpx;
}

.recommend-img {
  width: 100%;
  height: 136rpx;
  border-radius: 16rpx;
  background: #fff4eb;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.recommend-card b,
.recommend-card p {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-card b {
  margin-top: 12rpx;
  color: #172033;
  font-size: 25rpx;
}

.recommend-card p {
  margin: 6rpx 0 0;
  color: #8c6a58;
  font-size: 26rpx;
}

.recommend-sale-meta,
.recommend-tag-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 10rpx;
}

.recommend-sale-meta span,
.recommend-date {
  display: inline-flex;
  align-items: center;
  min-height: 40rpx;
  padding: 4rpx 12rpx;
  color: #4d7465;
  background: #f1faf4;
  border: 1rpx solid rgba(31, 138, 112, 0.18);
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 800;
  white-space: nowrap;
}

.recommend-sale-meta b {
  margin: 0;
  color: #1f8a70;
  font-size: inherit;
}

.recommend-date {
  max-width: 100%;
  color: #9d4b2f;
  background: #fff4eb;
  border-color: #f3d4c1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recommend-bottom {
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 10rpx;
}

.recommend-price-stack {
  min-width: 0;
}

.recommend-price-stack span {
  display: block;
  color: #f20d0d;
  font-size: 30rpx;
  font-weight: 900;
}

.recommend-price-stack em {
  display: block;
  margin-top: 2rpx;
  color: #b9a89e;
  font-size: 26rpx;
  font-style: normal;
  font-weight: 800;
  text-decoration: line-through;
}

.recommend-bottom button {
  display: grid;
  width: 54rpx;
  min-width: 54rpx;
  height: 54rpx;
  min-height: 54rpx;
  padding: 0;
  background: #f20d0d !important;
  border-radius: 50% !important;
  place-items: center;
}

.recommend-bottom svg {
  width: 30rpx;
  height: 30rpx;
  fill: none;
  stroke: #ffffff;
  stroke-width: 2.2;
}

.detail-bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  gap: 0;
  width: 100%;
  max-width: 390px;
  min-height: calc(144rpx + env(safe-area-inset-bottom));
  margin: 0 auto;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.98);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
}

.detail-bottom button {
  flex: 1;
  min-height: 100rpx;
  font-size: 34rpx;
  font-weight: 900;
}

.detail-bottom .cart-action {
  color: #ffffff !important;
  background: #e85d3f !important;
  border-radius: 999rpx 0 0 999rpx !important;
}

.detail-bottom .red {
  color: #ffffff !important;
  background: #f20d0d !important;
  border-radius: 0 999rpx 999rpx 0 !important;
}

.detail-bottom button[disabled] {
  opacity: 0.56;
}
</style>
