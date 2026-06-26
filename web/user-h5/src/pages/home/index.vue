<template>
  <view class="page shop-page">
    <view class="shop-header">
      <view class="shop-header-row">
        <view class="logo-mark">
          <text>社区鲜到</text>
          <text class="logo-sub">GROUP BUY</text>
        </view>
        <view class="shop-search" @click="goSearch">
          <text>⌕</text>
          <text>搜索果蔬、粮油、牛奶</text>
        </view>
        <view class="mini-pill" @click="goOrder">订单</view>
      </view>
      <view class="station-bar" @click="goEntry">
        <view class="station-main">
          <text>{{ stationName }}</text>
          <text>预计提货日：{{ deliveryDate }}</text>
        </view>
        <button>切换</button>
      </view>
    </view>

    <view class="cat-grid">
      <view v-for="item in categoryCards" :key="item.id" class="cat-item" @click="goCategory">
        <view class="cat-img">{{ item.shortName }}</view>
        <text>{{ item.categoryName }}</text>
      </view>
    </view>

    <view class="hero-banner">
      <view class="banner-text">
        <text class="banner-kicker">今日团期</text>
        <text class="banner-title">基地直采鲜货</text>
        <text>按当前自提点展示可售库存</text>
      </view>
      <view class="banner-dots">
        <text class="active"></text>
        <text></text>
        <text></text>
      </view>
    </view>

    <view class="promo-grid">
      <view class="promo-card">
        <text class="promo-title">次日达 <text>热销</text></text>
        <view class="promo-img fruit">鲜</view>
      </view>
      <view class="promo-card">
        <text class="promo-title">家庭装 <text>精选</text></text>
        <view class="promo-img grain">惠</view>
      </view>
    </view>

    <view class="summary">
      <view v-for="item in metrics" :key="item.label" class="section metric">
        <text class="title">{{ item.value }}</text>
        <text class="subtle">{{ item.label }}</text>
      </view>
    </view>

    <view class="product-list">
      <view class="list-head">
        <text>今日推荐</text>
        <text>团期商品</text>
      </view>
      <view v-for="item in products" :key="item.publishSkuId" class="shop-product" @click="openProduct(item.productId)">
        <view class="shop-product-media">
          <image v-if="item.mainImageUrl" class="shop-product-img" :src="item.mainImageUrl" mode="aspectFill" />
          <view v-else class="shop-product-img fallback">鲜</view>
          <text class="shop-product-date">{{ item.deliveryDate || '待定提货' }}</text>
        </view>
        <view class="shop-product-info">
          <text class="product-name">{{ item.productName }}</text>
          <text class="shop-product-spec">{{ item.skuName || item.saleSpecText || '默认规格' }}</text>
          <text class="shop-desc">库存 {{ item.availableQty }} · 限购 {{ item.limitQty }}</text>
          <view class="shop-sale-meta">
            <text>已售 {{ item.soldQty || 0 }}</text>
            <text>剩余 {{ item.availableQty }}</text>
          </view>
          <view class="price-row">
            <text class="shop-price">¥{{ item.salePrice }}</text>
            <button @click.stop="openProduct(item.productId)">加购</button>
          </view>
        </view>
      </view>
      <view v-if="products.length === 0" class="section empty-card">
        <text class="title">暂无可售商品</text>
        <text class="subtle">当前自提点还没有上架团期商品。</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { getHomeSummary, pageCategories, pageProducts, type UserCategoryDTO, type UserProductCardDTO } from '@/api/user';

const stationName = ref('默认自提点');
const deliveryDate = ref('待加载');
const categories = ref<UserCategoryDTO[]>([]);
const metrics = ref([
    { label: '待提货', value: 0 },
    { label: '待付款', value: 0 },
    { label: '在售商品', value: 0 },
    { label: '未读消息', value: 0 }
]);
const products = ref<UserProductCardDTO[]>([]);

const fallbackCategories = [
    { id: 1, categoryName: '新鲜水果', shortName: '果' },
    { id: 2, categoryName: '时令蔬菜', shortName: '蔬' },
    { id: 3, categoryName: '肉禽蛋奶', shortName: '鲜' },
    { id: 4, categoryName: '米面粮油', shortName: '粮' },
    { id: 5, categoryName: '早餐烘焙', shortName: '早' }
];

const categoryCards = computed(() => {
    const rows = categories.value.slice(0, 5).map((item) => ({
        id: item.id,
        categoryName: item.categoryName,
        shortName: item.categoryName.slice(0, 1)
    }));
    return rows.length > 0 ? rows : fallbackCategories;
});

async function loadData() {
    const [home, categoryPage, productPage] = await Promise.all([
        getHomeSummary({
            userId: 740001,
            cityId: 440100,
            stationId: 720001
        }),
        pageCategories({ pageNum: 1, pageSize: 10, status: 1 }),
        pageProducts({
            pageNum: 1,
            pageSize: 6,
            userId: 740001,
            cityId: 440100,
            stationId: 720001
        })
    ]);
    stationName.value = `自提点 ${home.stationId || 720001}`;
    categories.value = categoryPage.list || [];
    products.value = productPage.list || [];
    deliveryDate.value = products.value[0]?.deliveryDate || '待定';
    metrics.value = [
        { label: '待提货', value: home.waitPickupOrderCount || 0 },
        { label: '待付款', value: home.waitPayOrderCount || 0 },
        { label: '在售商品', value: home.onlineProductCount || 0 },
        { label: '未读消息', value: home.unreadMessageCount || 0 }
    ];
}

function openProduct(productId: number) {
    uni.navigateTo({ url: `/pages/product/detail?id=${productId}` });
}

function goEntry() {
    uni.navigateTo({ url: '/pages/entry/index' });
}

function goSearch() {
    uni.navigateTo({ url: '/pages/search/index' });
}

function goCategory() {
    uni.switchTab({ url: '/pages/category/index' });
}

function goOrder() {
    uni.switchTab({ url: '/pages/order/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.shop-page {
  padding: 0 0 150rpx;
}

.shop-header {
  padding: 12rpx 24rpx 20rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
}

.shop-header-row {
  display: grid;
  grid-template-columns: 184rpx minmax(0, 1fr) 112rpx;
  gap: 18rpx;
  align-items: center;
}

.logo-mark {
  display: grid;
  gap: 4rpx;
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.05;
}

.logo-sub {
  font-size: 20rpx;
  font-weight: 800;
  opacity: 0.9;
}

.shop-search {
  display: flex;
  align-items: center;
  gap: 12rpx;
  height: 76rpx;
  padding: 0 24rpx;
  color: #7b5f51;
  background: #ffffff;
  border-radius: 40rpx;
  font-size: 26rpx;
}

.mini-pill {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 68rpx;
  color: #ffffff;
  background: rgba(38, 48, 67, 0.22);
  border-radius: 36rpx;
  font-size: 24rpx;
  font-weight: 800;
}

.station-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  min-height: 104rpx;
  margin-top: 20rpx;
  padding: 16rpx 16rpx 16rpx 28rpx;
  color: #1f2937;
  background: #ffffff;
  border-radius: 32rpx;
  box-shadow: 0 6rpx 0 rgba(0, 0, 0, 0.04);
}

.station-main {
  min-width: 0;
  display: grid;
  gap: 6rpx;
}

.station-main text:first-child {
  overflow: hidden;
  font-size: 28rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-main text:last-child {
  color: #8b6a57;
  font-size: 22rpx;
  font-weight: 800;
}

.station-bar button {
  min-height: 64rpx;
  padding: 0 24rpx;
  color: #c2412d !important;
  background: #ffffff !important;
  border: 1rpx solid #e85d3f;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 24rpx 12rpx;
  margin: 20rpx 24rpx;
  padding: 28rpx 16rpx 24rpx;
  background: #ffffff;
  border-radius: 28rpx;
}

.cat-item {
  display: grid;
  justify-items: center;
  gap: 12rpx;
  min-width: 0;
  color: #263043;
  font-size: 22rpx;
  font-weight: 800;
  line-height: 1.2;
  text-align: center;
}

.cat-item > text {
  overflow: hidden;
  max-width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cat-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7 0%, #ffcf55 42%, #f05a37 100%);
  border-radius: 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(38, 48, 67, 0.12);
  font-size: 30rpx;
  font-weight: 900;
}

.hero-banner {
  position: relative;
  overflow: hidden;
  height: 308rpx;
  margin: 20rpx 24rpx;
  background:
    linear-gradient(90deg, rgba(23, 32, 51, 0.72), rgba(23, 32, 51, 0.18) 55%, rgba(217, 75, 52, 0.18)),
    linear-gradient(135deg, #263043 0%, #d94b34 58%, #f7b36c 100%);
  border-radius: 28rpx;
  box-shadow: 0 20rpx 44rpx rgba(38, 48, 67, 0.16);
}

.banner-text {
  position: absolute;
  left: 32rpx;
  top: 36rpx;
  z-index: 1;
  display: grid;
  gap: 8rpx;
  color: #ffffff;
  text-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.35);
}

.banner-kicker {
  width: fit-content;
  padding: 6rpx 16rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 800;
}

.banner-title {
  font-size: 52rpx;
  font-weight: 900;
  line-height: 1.12;
}

.banner-dots {
  position: absolute;
  left: 32rpx;
  bottom: 24rpx;
  z-index: 1;
  display: flex;
  gap: 10rpx;
}

.banner-dots text {
  width: 14rpx;
  height: 14rpx;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 999rpx;
}

.banner-dots .active {
  width: 44rpx;
  background: #ffffff;
}

.promo-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin: 20rpx 24rpx;
}

.promo-card {
  overflow: hidden;
  min-height: 280rpx;
  padding: 18rpx;
  background: #ffffff;
  border-radius: 20rpx;
}

.promo-title {
  display: flex;
  justify-content: space-between;
  color: #172033;
  font-size: 32rpx;
  font-weight: 900;
}

.promo-title text {
  color: #e85d3f;
  font-size: 24rpx;
}

.promo-img {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 180rpx;
  margin-top: 16rpx;
  color: #ffffff;
  border-radius: 16rpx;
  font-size: 46rpx;
  font-weight: 900;
}

.promo-img.fruit {
  background: linear-gradient(135deg, #ffcf55, #f05a37);
}

.promo-img.grain {
  background: linear-gradient(135deg, #f7b36c, #a36d14);
}

.summary {
  margin: 0 24rpx 20rpx;
}

.product-list {
  margin: 16rpx 20rpx 0;
}

.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
  padding: 0 4rpx;
}

.list-head text:first-child {
  color: #172033;
  font-size: 34rpx;
  font-weight: 900;
}

.list-head text:last-child {
  color: #9a7868;
  font-size: 22rpx;
  font-weight: 800;
}

.shop-product {
  position: relative;
  display: grid;
  grid-template-columns: 220rpx minmax(0, 1fr);
  gap: 20rpx;
  align-items: start;
  margin-bottom: 16rpx;
  padding: 18rpx;
  background: #ffffff;
  border: 1rpx solid #edf1ed;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.shop-product-media {
  display: grid;
  align-content: start;
  gap: 12rpx;
}

.shop-product-img {
  width: 192rpx;
  height: 192rpx;
  border-radius: 16rpx;
  background-size: cover;
  background-position: center;
}

.shop-product-img.fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7 0%, #ffcf55 42%, #f05a37 100%);
  font-size: 48rpx;
  font-weight: 900;
}

.shop-product-date {
  width: fit-content;
  max-width: 220rpx;
  min-height: 44rpx;
  padding: 0 16rpx;
  color: #6f5736;
  background: #fbfaf6;
  border: 1rpx solid #ece6da;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 800;
  line-height: 44rpx;
  white-space: nowrap;
}

.shop-product-info {
  min-width: 0;
}

.product-name {
  display: -webkit-box;
  overflow: hidden;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.3;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec,
.shop-desc {
  display: block;
  overflow: hidden;
  color: #8c6a58;
  font-size: 24rpx;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-product-spec {
  margin-top: 8rpx;
}

.shop-sale-meta {
  display: flex;
  gap: 10rpx;
  margin-top: 12rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.shop-sale-meta text {
  min-height: 36rpx;
  padding: 0 12rpx;
  background: #fff8f3;
  border: 1rpx solid #f4e2d8;
  border-radius: 999rpx;
  line-height: 36rpx;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  margin-top: 16rpx;
}

.shop-price {
  color: #e60012;
  font-size: 40rpx;
  font-weight: 900;
}

.price-row button {
  min-width: 88rpx;
  min-height: 56rpx;
  padding: 0 18rpx;
  font-size: 24rpx;
}

.empty-card {
  margin: 0;
}
</style>
