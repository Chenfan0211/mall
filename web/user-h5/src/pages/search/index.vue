<template>
  <view class="search-page shop-page user-search-page" data-m-page="search">
    <view class="user-search-head">
      <view class="order-nav">
        <button class="order-back" @click="goBack">‹</button>
        <b>搜索</b>
        <span>商品</span>
      </view>
      <label class="user-search-box">
        <span>⌕</span>
        <input
          v-model="keyword"
          class="user-search-input"
          placeholder="当前搜索：输入“果冻橙”展示联想词"
          confirm-type="search"
          @confirm="runSearch"
        />
      </label>
    </view>

    <view class="search-section">
      <h3>
        历史搜索
        <button @click="clearHistory">一键清空</button>
      </h3>
      <view class="search-chip-row">
        <button v-for="item in state.searchHistory" :key="item" @click="selectWord(item)">
          {{ item }}<i @click.stop="deleteHistory(item)">×</i>
        </button>
        <EmptyActionCard
          v-if="state.searchHistory.length === 0"
          title="暂无搜索历史"
          sub="搜索后会按最近搜索时间展示，支持单条删除和一键清空。"
          icon="搜"
          button-text="去首页逛逛"
          @action="goHome"
        />
      </view>
    </view>

    <view class="search-section">
      <h3>热门搜索 <span>实时推荐</span></h3>
      <view class="search-chip-row">
        <button v-for="item in hotWords" :key="item" @click="selectWord(item)">{{ item }}</button>
      </view>
    </view>

    <view class="search-section">
      <h3>联想词 <span>推荐词</span></h3>
      <view class="search-suggest-list">
        <button v-for="item in suggestions" :key="item" @click="selectWord(item)">
          {{ item }}<span>搜索</span>
        </button>
      </view>
    </view>

    <view class="search-section" data-search-result-section>
      <h3>{{ resultTitle }} <span>当前自提点</span></h3>
      <view class="product-sort-card">
        <b>排序</b>
        <view>
          <button v-for="item in sortTabs" :key="item" :class="{ active: activeSort === item }" @click="activeSort = item">
            {{ item }}
          </button>
        </view>
      </view>
      <view class="product-list">
        <view
          v-for="item in sortedResults"
          :key="item.publishSkuId"
          class="shop-product"
          :class="{ soldout: isSoldout(item) }"
          @click="openDetail(item.productId)"
        >
          <button
            class="product-fav-btn"
            :class="{ active: state.favorites.has(item.productId) }"
            @click.stop="toggleFavorite(item.productId)"
          >
            {{ state.favorites.has(item.productId) ? '已藏' : '收藏' }}
          </button>
          <view class="shop-product-media">
            <view class="shop-product-img" :style="backgroundImageStyle(item.mainImageUrl || fallbackProductImages[0])" />
          </view>
          <view class="shop-product-info">
            <h4>{{ item.productName }}</h4>
            <view class="shop-product-spec">{{ productSpecText(item) }}</view>
            <view class="price-row">
              <span class="shop-price">¥{{ item.salePrice }}</span>
              <span v-if="productOldPrice(item)" class="old-price">¥{{ productOldPrice(item) }}</span>
            </view>
            <view class="shop-sale-meta">
              <span class="sold-count">已售{{ item.soldQty || 0 }}</span>
              <span class="sale-stock"><b>{{ isSoldout(item) ? '已售罄' : `剩余${item.availableQty}` }}</b></span>
            </view>
          </view>
          <view class="shop-product-tag-row">
            <span class="shop-product-date">提货日期 {{ productPickupText(item.deliveryDate) }}</span>
          </view>
          <button
            class="round-add"
            :aria-label="isSoldout(item) ? '已售罄' : '加入购物车'"
            @click.stop="isSoldout(item) ? toggleNotice(item.productId) : handleAddProduct(item)"
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="8.8" cy="20" r="1.5"></circle>
              <circle cx="17.8" cy="20" r="1.5"></circle>
              <path d="M3.5 4.5h2.4L8 15.7h9.7l2.2-7.8H7.2"></path>
              <path d="M15.8 4.2v6.4"></path>
              <path d="M12.6 7.4H19"></path>
            </svg>
          </button>
        </view>
        <EmptyActionCard
          v-if="sortedResults.length === 0"
          title="暂无搜索结果"
          sub="没有找到可购买商品，先看看热销推荐。"
          icon="搜"
          button-text="返回首页"
          @action="goHome"
        />
      </view>
    </view>

    <view class="search-section recommend-section">
      <h3>无结果推荐 <span>搜索“进口榴莲”</span></h3>
      <view class="search-status-card">暂无匹配商品，先看看热销推荐。</view>
      <view class="search-chip-row">
        <button @click="selectWord('进口榴莲')">搜索进口榴莲</button>
      </view>
      <view class="recommend-grid">
        <view v-for="item in recommendedProducts" :key="item.publishSkuId" class="recommend-card" @click="openDetail(item.productId)">
          <view class="recommend-img" :style="backgroundImageStyle(item.mainImageUrl || fallbackProductImages[0])" />
          <b>{{ item.productName }}</b>
          <p>{{ productSpecText(item) }}</p>
          <view class="recommend-bottom">
            <span>¥{{ item.salePrice }}</span>
            <button @click.stop="handleAddProduct(item)">
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
import {
    addProductToCart,
    navigateUser,
    rememberSearchKeyword,
    showUserToast,
    toggleFavorite,
    toggleNotice,
    useUserState
} from '@/stores/userState';
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
const keyword = ref('');
const activeSort = ref('匹配度');
const results = ref<UserProductCardDTO[]>([]);
const hotWords = ['蓝莓', '苹果', '早餐组合', '有机蔬菜', '牛奶'];
const suggestions = ['果冻橙 5斤装', '果冻橙 次日自提', '果冻橙 当前自提点可买', '果冻橙 榜单商品'];
const sortTabs = ['匹配度', '销量', '上新', '价格'];
const resultTitle = computed(() => (keyword.value ? `“${keyword.value}”搜索结果` : '搜索结果'));
const sortedResults = computed(() => {
    const list = [...results.value];
    if (activeSort.value === '价格') {
        list.sort((a, b) => Number(a.salePrice || 0) - Number(b.salePrice || 0));
    }
    if (activeSort.value === '销量') {
        list.sort((a, b) => Number(b.soldQty || 0) - Number(a.soldQty || 0));
    }
    if (activeSort.value === '上新') {
        list.sort((a, b) => Number(b.publishSkuId || 0) - Number(a.publishSkuId || 0));
    }
    return list.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0));
});
const recommendedProducts = computed(() => fallbackProducts.slice(0, 4));

function fallbackSearchRows() {
    const value = keyword.value.trim();
    if (/榴莲|进口榴莲/.test(value)) {
        return [];
    }
    if (/吐司|早餐|面包/.test(value)) {
        return fallbackProducts.filter((item) => /早餐|吐司|牛奶/.test(`${item.productName}${item.saleSpecText}`));
    }
    if (!value) {
        return fallbackProducts.slice(0, 6);
    }
    const matched = fallbackProducts.filter((item) => `${item.productName}${item.skuName}${item.saleSpecText}`.includes(value));
    return matched.length ? matched : fallbackProducts.slice(0, 4);
}

async function runSearch() {
    rememberSearchKeyword(keyword.value);
    try {
        const page = await pageProducts(
            {
                pageNum: 1,
                pageSize: 20,
                keyword: keyword.value,
                userId: state.user.id,
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        results.value = page?.list?.length ? withProductImages(page.list) : fallbackSearchRows();
    } catch {
        results.value = fallbackSearchRows();
        console.info('用户端搜索接口不可用，已展示本地兜底商品');
    }
}

function selectWord(value: string) {
    keyword.value = value;
    void runSearch();
}

function clearHistory() {
    state.searchHistory = [];
    showUserToast('搜索历史已清空');
}

function deleteHistory(value: string) {
    state.searchHistory = state.searchHistory.filter((item) => item !== value);
}

function handleAddProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = '/pages/search/index';
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goBack() {
    uni.navigateBack({
        fail() {
            navigateUser('/pages/home/index', true);
        }
    });
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

onMounted(() => {
    void runSearch();
});
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  padding: 0 0 172rpx;
  background: #f7f1ea;
}

.user-search-head {
  position: sticky;
  top: 0;
  z-index: 4;
  padding: 24rpx 28rpx 22rpx;
  color: #172033;
  background: #fffaf6;
  border-bottom: 1rpx solid #f0dfd6;
  box-shadow: none;
}

.order-nav,
.product-sort-card,
.product-sort-card view,
.search-chip-row,
.shop-sale-meta,
.shop-product-tag-row,
.recommend-bottom {
  display: flex;
  align-items: center;
}

.order-nav {
  display: grid;
  grid-template-columns: 70rpx minmax(0, 1fr) 70rpx;
  gap: 12rpx;
  height: 64rpx;
  align-items: center;
}

.order-nav b {
  justify-self: center;
  color: #172033;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1;
}

.order-nav span {
  justify-self: end;
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1;
}

.order-back {
  justify-self: start;
  width: 58rpx;
  min-width: 58rpx;
  height: 58rpx;
  min-height: 58rpx;
  padding: 0;
  color: #172033 !important;
  background: #fff4ec !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 42rpx;
  line-height: 50rpx;
}

.user-search-box {
  display: flex;
  align-items: center;
  gap: 14rpx;
  height: 72rpx;
  margin-top: 14rpx;
  padding: 0 22rpx;
  color: #7b5f51;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  font-size: 26rpx;
  font-weight: 800;
  box-shadow: none;
}

.user-search-box span {
  flex: 0 0 auto;
  color: #c2412d;
  font-size: 28rpx;
  font-weight: 900;
}

.user-search-input {
  flex: 1;
  min-width: 0;
  min-height: 68rpx;
  padding: 0;
  color: #172033;
  background: transparent;
  border: 0;
  border-radius: 0;
  font-size: 26rpx;
  font-weight: 800;
}

.search-section {
  margin: 18rpx 24rpx;
  padding: 22rpx 24rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 26rpx rgba(126, 76, 49, 0.06);
}

.search-section h3 {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 0 14rpx;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.search-section h3 span {
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 700;
}

.search-section h3 button {
  min-height: 42rpx;
  padding: 0;
  color: #c2412d !important;
  background: transparent !important;
  border: 0;
  font-size: 25rpx;
  box-shadow: none;
}

.search-chip-row {
  flex-wrap: wrap;
  gap: 12rpx;
}

.search-chip-row button,
.search-chip-row :deep(uni-button) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  min-height: 50rpx;
  padding: 0 18rpx;
  color: #7b5f51 !important;
  background: #fff7f1 !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx !important;
  font-size: 26rpx;
  box-shadow: none;
}

.search-chip-row i,
.search-chip-row :deep(i) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32rpx;
  height: 32rpx;
  color: #c2412d;
  background: rgba(232, 93, 63, 0.12);
  border-radius: 50%;
  font-style: normal;
  line-height: 1;
}

.search-suggest-list {
  display: grid;
  gap: 10rpx;
}

.search-suggest-list button,
.search-suggest-list :deep(uni-button) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 62rpx;
  padding: 0 18rpx;
  color: #172033 !important;
  background: #fffaf6 !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx !important;
  font-size: 26rpx;
  box-shadow: none;
}

.search-suggest-list span,
.search-suggest-list :deep(span) {
  color: #c2412d;
  font-size: 25rpx;
  font-weight: 900;
}

.search-status-card {
  width: 100%;
  padding: 22rpx;
  color: #7b5f51;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx;
  font-size: 26rpx;
}

.product-sort-card {
  justify-content: space-between;
  gap: 14rpx;
  margin-bottom: 12rpx;
  padding: 12rpx 14rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx;
}

.product-sort-card b {
  color: #172033;
  font-size: 26rpx;
}

.product-sort-card view {
  gap: 8rpx;
}

.product-sort-card button,
.product-sort-card :deep(uni-button) {
  min-width: 72rpx;
  min-height: 48rpx;
  padding: 0 14rpx;
  color: #8c6a58 !important;
  background: transparent !important;
  border-radius: 999rpx !important;
  font-size: 25rpx;
  box-shadow: none;
}

.product-sort-card button.active,
.product-sort-card :deep(uni-button.active) {
  color: #ffffff !important;
  background: #e85d3f !important;
}

.product-list {
  display: grid;
  gap: 0;
}

.shop-product {
  position: relative;
  display: grid;
  grid-template-columns: 196rpx minmax(0, 1fr);
  gap: 18rpx;
  min-height: 264rpx;
  padding: 20rpx 84rpx 42rpx 0;
  background: #ffffff;
  border: 0;
  border-bottom: 1rpx solid #eeeeee;
  border-radius: 0;
  box-shadow: none;
}

.shop-product.soldout {
  opacity: 0.72;
}

.shop-product-media {
  position: relative;
  width: 196rpx;
  height: 196rpx;
  overflow: hidden;
  background-color: #f7f1ea;
  border-radius: 16rpx;
}

.shop-product-img {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.shop-product.soldout .shop-product-media::after {
  position: absolute;
  inset: 0;
  display: grid;
  color: #ffffff;
  background: rgba(23, 32, 51, 0.46);
  content: "已售罄";
  font-size: 26rpx;
  font-weight: 900;
  place-items: center;
}

.product-fav-btn {
  position: absolute;
  top: 16rpx;
  right: 0;
  z-index: 2;
  min-width: 82rpx;
  min-height: 42rpx;
  padding: 0 16rpx;
  color: #1f8a70 !important;
  background: #ffffff !important;
  border: 1rpx solid rgba(31, 138, 112, 0.24);
  border-radius: 999rpx !important;
  font-size: 26rpx;
  box-shadow: none;
}

.product-fav-btn.active {
  color: #ffffff !important;
  background: #1f8a70 !important;
  border-color: #1f8a70;
}

.shop-product-info {
  min-width: 0;
  padding-right: 0;
}

.shop-product-info h4 {
  display: -webkit-box;
  overflow: hidden;
  margin: 0 0 8rpx;
  color: #172033;
  font-size: 27rpx;
  font-weight: 900;
  line-height: 1.25;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  overflow: hidden;
  color: #66716a;
  font-size: 25rpx;
  line-height: 1.25;
  text-overflow: clip;
  white-space: normal;
}

.price-row {
  margin-top: 12rpx;
}

.shop-price {
  color: #e60012;
  font-size: 36rpx;
  font-weight: 900;
}

.old-price {
  margin-left: 10rpx;
  color: #999999;
  font-size: 25rpx;
  text-decoration: line-through;
}

.shop-sale-meta {
  gap: 10rpx;
  margin-top: 10rpx;
}

.shop-sale-meta span {
  min-height: 40rpx;
  padding: 4rpx 10rpx;
  color: #5e6a60;
  background: #f8faf7;
  border: 1rpx solid #e8eee8;
  border-radius: 999rpx;
  font-size: 26rpx;
  line-height: 1.25;
}

.shop-sale-meta b {
  color: #526257;
}

.shop-product-tag-row {
  grid-column: 2 / 3;
  position: static;
  gap: 10rpx;
  min-width: 0;
  margin-top: 8rpx;
  padding-right: 54rpx;
}

.shop-product-date {
  overflow: hidden;
  max-width: 172rpx;
  min-height: 42rpx;
  padding: 0 12rpx;
  color: #6f5736;
  background: #fbfaf6;
  border: 1rpx solid #ece6da;
  border-radius: 999rpx;
  font-size: 26rpx;
  line-height: 42rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.round-add {
  position: absolute;
  right: 4rpx;
  bottom: 24rpx;
  display: grid;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  background: #e85d3f !important;
  border-radius: 50% !important;
  box-shadow: 0 12rpx 32rpx rgba(232, 93, 63, 0.34);
  place-items: center;
}

.round-add svg {
  width: 38rpx;
  height: 38rpx;
  fill: none;
  stroke: #ffffff;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.2;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.recommend-card {
  min-width: 0;
  padding: 14rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
}

.recommend-img {
  width: 100%;
  height: 136rpx;
  background-size: cover;
  background-position: center;
  border-radius: 16rpx;
  background-color: #fff4eb;
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
  font-weight: 900;
}

.recommend-card p {
  margin: 6rpx 0 0;
  color: #8c6a58;
  font-size: 26rpx;
}

.recommend-bottom {
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 10rpx;
}

.recommend-bottom span {
  color: #e60012;
  font-size: 30rpx;
  font-weight: 900;
}

.recommend-bottom button {
  display: grid;
  width: 54rpx;
  min-width: 54rpx;
  height: 54rpx;
  min-height: 54rpx;
  padding: 0;
  background: #e85d3f !important;
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

.search-section[data-search-result-section] {
  margin: 18rpx 12rpx;
  padding: 18rpx 12rpx;
  border-radius: 14rpx;
}

.search-section[data-search-result-section] .product-list {
  gap: 12rpx;
  padding: 0;
}

.search-section[data-search-result-section] .shop-product {
  grid-template-columns: 192rpx minmax(0, 1fr);
  grid-template-rows: auto auto;
  column-gap: 20rpx;
  row-gap: 10rpx;
  min-height: 248rpx;
  padding: 20rpx 104rpx 22rpx 20rpx;
  overflow: hidden;
  background: #ffffff;
  border: 1rpx solid #edf1ed;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(23, 36, 32, 0.05);
}

.search-section[data-search-result-section] .shop-product-media {
  grid-row: 1 / 2;
  width: 192rpx;
  height: 192rpx;
  border-radius: 16rpx;
}

.search-section[data-search-result-section] .shop-product-img {
  width: 192rpx;
  height: 192rpx;
  border-radius: 16rpx;
}

.search-section[data-search-result-section] .product-fav-btn {
  top: 18rpx;
  right: 18rpx;
  min-width: 64rpx;
  min-height: 44rpx;
  padding: 0 12rpx;
  color: #7b5f51 !important;
  background: rgba(255, 255, 255, 0.88) !important;
  border-color: rgba(126, 76, 49, 0.14);
  font-size: 25rpx;
}

.search-section[data-search-result-section] .shop-product-info h4 {
  margin: 0 0 6rpx;
  font-size: 28rpx;
  line-height: 1.28;
}

.search-section[data-search-result-section] .shop-product-spec {
  margin: 0 0 8rpx;
  overflow: hidden;
  font-size: 25rpx;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.search-section[data-search-result-section] .price-row {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 8rpx;
  margin: 0;
}

.search-section[data-search-result-section] .shop-price {
  font-size: 40rpx;
  line-height: 1;
}

.search-section[data-search-result-section] .old-price {
  margin-left: 0;
  font-size: 26rpx;
}

.search-section[data-search-result-section] .shop-sale-meta {
  display: flex;
  flex-wrap: nowrap;
  gap: 10rpx;
  max-width: 286rpx;
  margin-top: 8rpx;
  overflow: hidden;
}

.search-section[data-search-result-section] .shop-sale-meta span,
.search-section[data-search-result-section] .shop-sale-meta .sale-stock b {
  min-height: 40rpx;
  padding: 4rpx 12rpx;
  color: #a94b2a;
  background: #fff2e8;
  border: 0;
  font-size: 25rpx;
  font-weight: 900;
  line-height: 1.2;
}

.search-section[data-search-result-section] .shop-product-tag-row {
  grid-column: 2 / 3;
  grid-row: 2 / 3;
  display: flex;
  gap: 8rpx;
  min-height: 40rpx;
  margin: 0;
  padding-right: 0;
}

.search-section[data-search-result-section] .shop-product-date {
  max-width: 100%;
  min-height: 44rpx;
  height: auto;
  padding: 4rpx 12rpx;
  border: 0;
  border-radius: 12rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.2;
}

.search-section[data-search-result-section] .round-add {
  right: 20rpx;
  bottom: 24rpx;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
}

.search-section[data-search-result-section] .round-add svg {
  width: 44rpx;
  height: 44rpx;
}
</style>
