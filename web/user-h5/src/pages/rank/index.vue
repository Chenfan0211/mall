<template>
  <view class="rank-page shop-page" data-m-page="rank">
    <view class="rank-hero">
      <view class="rank-nav">
        <button @click="goHome">‹</button>
        <b>好物榜单</b>
        <span>更多榜单</span>
      </view>
      <h2>{{ activeRank.name }}</h2>
      <p>{{ activeRank.rule }}</p>
    </view>

    <scroll-view class="rank-tabs" scroll-x :show-scrollbar="false">
      <button
        v-for="item in rankTabs"
        :key="item.key"
        :class="{ active: activeRankKey === item.key }"
        @click="activeRankKey = item.key"
      >
        {{ item.short }}
      </button>
    </scroll-view>

    <view class="product-sort-card">
      <b>排序</b>
      <view>
        <button v-for="item in sortTabs" :key="item" :class="{ active: activeSort === item }" @click="activeSort = item">
          {{ item }}
        </button>
      </view>
    </view>

    <view class="rank-list">
      <view v-for="(item, index) in sortedProducts" :key="item.publishSkuId" class="rank-product" @click="openDetail(item.productId)">
        <button
          class="product-fav-btn"
          :class="{ active: state.favorites.has(item.productId) }"
          @click.stop="toggleFavorite(item.productId)"
        >
          {{ state.favorites.has(item.productId) ? '已藏' : '收藏' }}
        </button>
        <span class="rank-no">#{{ index + 1 }}</span>
        <view class="rank-product-img" :style="backgroundImageStyle(item.mainImageUrl || fallbackProductImages[0])" />
        <view class="rank-product-body">
          <h4>{{ item.productName }}</h4>
          <p>{{ productSpecText(item) }}</p>
          <span class="rank-hot">近7日销量{{ formatRankSales(item.soldQty) }}份</span>
          <view class="shop-sale-meta">
            <span class="sold-count">已售{{ item.soldQty || 0 }}</span>
            <span class="sale-stock"><b>{{ isSoldout(item) ? '已售罄' : `剩余${item.availableQty}` }}</b></span>
          </view>
          <view class="shop-product-tag-row">
            <span>提货日期 {{ productPickupText(item.deliveryDate) }}</span>
          </view>
          <view class="rank-product-foot">
            <view class="rank-price-stack">
              <b>¥{{ item.salePrice }}</b>
              <span v-if="productOldPrice(item)" class="old-price">¥{{ productOldPrice(item) }}</span>
            </view>
            <button @click.stop="isSoldout(item) ? toggleNotice(item.productId) : addProduct(item)">
              {{ isSoldout(item) ? '到货提醒' : '立即加购' }}
            </button>
          </view>
        </view>
      </view>

      <EmptyActionCard
        v-if="sortedProducts.length === 0"
        title="暂无榜单"
        sub="当前自提点还没有足够销量数据。"
        icon="榜"
        button-text="返回首页"
        @action="goHome"
      />
    </view>

    <UserToast />
    <UserTabBar active="home" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageProducts, type UserProductCardDTO } from '@/api/user';
import { addProductToCart, navigateUser, showUserToast, toggleFavorite, toggleNotice, useUserState } from '@/stores/userState';
import {
    fallbackProductImages,
    fallbackProducts,
    isSoldout,
    productOldPrice,
    productPickupText,
    productSpecText,
    withProductImages
} from '@/utils/userFallbackData';

const state = useUserState();
const products = ref<UserProductCardDTO[]>([]);
const activeRankKey = ref('fresh');
const activeSort = ref('推荐');
const sortTabs = ['推荐', '销量', '上新', '价格'];
const rankTabs = [
    { key: 'fresh', short: '鲜果榜', name: '自提点热卖鲜果榜', rule: '按当前自提点近 7 日销量与可售库存综合排序' },
    { key: 'breakfast', short: '早餐榜', name: '早餐烘焙人气榜', rule: '优先展示次日可提的早餐和乳品组合' },
    { key: 'family', short: '家庭榜', name: '家庭囤货必买榜', rule: '覆盖粮油、蛋奶、蔬菜等高频复购商品' }
];

const activeRank = computed(() => rankTabs.find((item) => item.key === activeRankKey.value) || rankTabs[0]);
const sortedProducts = computed(() => {
    const list = [...products.value];
    if (activeRankKey.value === 'breakfast') {
        list.sort((left, right) => Number(/早餐|牛奶|吐司/.test(`${right.productName}${right.saleSpecText}`)) - Number(/早餐|牛奶|吐司/.test(`${left.productName}${left.saleSpecText}`)));
    }
    if (activeRankKey.value === 'family') {
        list.sort((left, right) => Number(/米|油|蛋|菜/.test(`${right.productName}${right.saleSpecText}`)) - Number(/米|油|蛋|菜/.test(`${left.productName}${left.saleSpecText}`)));
    }
    if (activeSort.value === '销量' || activeSort.value === '推荐') {
        list.sort((left, right) => Number(right.soldQty || 0) - Number(left.soldQty || 0));
    }
    if (activeSort.value === '上新') {
        list.sort((left, right) => Number(right.publishSkuId || 0) - Number(left.publishSkuId || 0));
    }
    if (activeSort.value === '价格') {
        list.sort((left, right) => Number(left.salePrice || 0) - Number(right.salePrice || 0));
    }
    return list.sort((left, right) => Number(right.availableQty > 0) - Number(left.availableQty > 0));
});

async function loadData() {
    try {
        const page = await pageProducts(
            { pageNum: 1, pageSize: 20, cityId: state.city.id, stationId: state.station.id },
            { silent: true }
        );
        products.value = page?.list?.length ? withProductImages(page.list) : fallbackProducts;
    } catch {
        products.value = fallbackProducts;
        console.info('用户端榜单接口不可用，已展示本地兜底商品');
    }
}

function addProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = '/pages/rank/index';
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function formatRankSales(value?: number) {
    const num = Number(value || 0) * 7;
    return num >= 10000 ? `${(num / 10000).toFixed(1)}万+` : `${num}+`;
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.rank-page {
  min-height: 100vh;
  padding: 0 0 184rpx;
  color: #172033;
  background: #2b1604;
}

.rank-hero {
  min-height: 420rpx;
  padding: 88rpx 28rpx 36rpx;
  color: #ffffff;
  background:
    radial-gradient(circle at 80% 26%, rgba(245, 194, 94, 0.36), transparent 28%),
    linear-gradient(160deg, #2b1604 0%, #6b2b12 48%, #e32135 100%);
}

.rank-nav,
.product-sort-card,
.product-sort-card view,
.shop-sale-meta,
.shop-product-tag-row,
.rank-product-foot {
  display: flex;
  align-items: center;
}

.rank-nav {
  display: grid;
  grid-template-columns: 84rpx minmax(0, 1fr) 140rpx;
  gap: 16rpx;
  height: 68rpx;
  margin-bottom: 44rpx;
}

.rank-nav button {
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  color: #ffffff !important;
  background: rgba(255, 255, 255, 0.14) !important;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 56rpx;
  line-height: 58rpx;
}

.rank-nav b {
  font-size: 36rpx;
  font-weight: 900;
  text-align: center;
}

.rank-nav span {
  font-size: 26rpx;
  line-height: 68rpx;
  text-align: right;
  opacity: 0.9;
}

.rank-hero h2 {
  margin: 0 0 16rpx;
  color: #ffffff;
  font-size: 58rpx;
  font-weight: 900;
  line-height: 1.15;
  text-align: center;
}

.rank-hero p {
  max-width: 660rpx;
  margin: 0;
  color: rgba(255, 255, 255, 0.82);
  font-size: 26rpx;
  line-height: 1.45;
  text-align: center;
}

.rank-tabs {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: -4rpx;
  padding: 24rpx;
  white-space: nowrap;
}

.rank-tabs button {
  display: inline-flex;
  flex: 0 0 auto;
  min-width: 0;
  min-height: 68rpx;
  margin-right: 16rpx;
  padding: 0 24rpx;
  color: #ffffff !important;
  background: rgba(255, 255, 255, 0.12) !important;
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 999rpx !important;
  font-size: 26rpx;
  font-weight: 900;
  box-shadow: none;
  line-height: 68rpx;
}

.rank-tabs button.active {
  color: #d82236 !important;
  background: #ffffff !important;
  border-color: #ffffff;
}

.product-sort-card {
  justify-content: space-between;
  gap: 16rpx;
  margin: 0 24rpx 20rpx;
  padding: 14rpx 16rpx;
  background: rgba(255, 255, 255, 0.94);
  border: 0;
  border-radius: 20rpx;
}

.product-sort-card b {
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.product-sort-card view {
  gap: 8rpx;
}

.product-sort-card button {
  min-width: 72rpx;
  min-height: 48rpx;
  padding: 0 14rpx;
  color: #8c6a58 !important;
  background: transparent !important;
  border-radius: 999rpx !important;
  font-size: 25rpx;
  box-shadow: none;
}

.product-sort-card button.active {
  color: #ffffff !important;
  background: #e85d3f !important;
}

.rank-list {
  display: grid;
  gap: 20rpx;
  padding: 0 24rpx 24rpx;
}

.rank-product {
  position: relative;
  display: grid;
  grid-template-columns: 216rpx minmax(0, 1fr);
  gap: 24rpx;
  min-height: 216rpx;
  padding: 20rpx;
  background: #ffffff;
  border: 0;
  border-radius: 32rpx;
  box-shadow: 0 24rpx 48rpx rgba(0, 0, 0, 0.15);
}

.rank-no {
  position: absolute;
  left: 28rpx;
  top: 28rpx;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 64rpx;
  height: 60rpx;
  color: #5b3511;
  background: #f4bd3c;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.18);
  font-size: 32rpx;
  font-weight: 900;
  line-height: 60rpx;
}

.rank-product-img {
  width: 216rpx;
  min-width: 216rpx;
  height: 216rpx;
  background: #fff4eb;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border-radius: 28rpx;
}

.product-fav-btn {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  z-index: 2;
  min-width: 82rpx;
  min-height: 42rpx;
  padding: 0 16rpx;
  color: #a06c52 !important;
  background: rgba(255, 250, 246, 0.92) !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  box-shadow: none;
}

.product-fav-btn.active {
  color: #e85d3f !important;
  border-color: #f2c9b7;
}

.rank-product-body {
  min-width: 0;
  padding-right: 0;
}

.rank-product h4 {
  display: -webkit-box;
  overflow: hidden;
  margin: 0 82rpx 10rpx 0;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.25;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.rank-product p {
  overflow: hidden;
  margin: 0 0 14rpx;
  color: #7b5f51;
  font-size: 26rpx;
  line-height: 1.45;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rank-hot {
  display: inline-flex;
  min-height: 44rpx;
  padding: 0 14rpx;
  color: #9a5a08;
  background: #fff6d7;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 44rpx;
}

.shop-sale-meta {
  gap: 8rpx;
  margin-top: 8rpx;
}

.shop-sale-meta span {
  min-height: 36rpx;
  padding: 0 10rpx;
  color: #8c6a58;
  background: #fff6ef;
  border-radius: 999rpx;
  font-size: 26rpx;
  line-height: 36rpx;
}

.shop-sale-meta b {
  color: #e85d3f;
}

.shop-product-tag-row {
  margin-top: 8rpx;
}

.shop-product-tag-row span {
  overflow: hidden;
  max-width: 230rpx;
  min-height: 36rpx;
  padding: 0 10rpx;
  color: #975b31;
  background: #fff1da;
  border-radius: 999rpx;
  font-size: 26rpx;
  line-height: 36rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rank-product-foot {
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 18rpx;
}

.rank-price-stack b {
  color: #d82236;
  font-size: 44rpx;
  font-weight: 900;
}

.old-price {
  margin-left: 8rpx;
  color: #b99c8a;
  font-size: 26rpx;
  text-decoration: line-through;
}

.rank-product-foot button {
  min-width: 144rpx;
  min-height: 64rpx;
  padding: 0 24rpx;
  color: #ffffff !important;
  background: #e51c36 !important;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  font-weight: 900;
  box-shadow: none;
}
</style>
