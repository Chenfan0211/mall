<template>
  <view class="page category-page shop-page" data-m-page="category">
    <view class="category-top">
      <view class="status-bar">
        <text>14:49</text>
        <text>▮▮▮ WiFi 80</text>
      </view>
      <view class="category-bar">
        <view class="category-station" @click="stationSheetVisible = true">
          <span>自提点 中轮 - {{ state.station.name }}</span>
          <em>当前类目 · {{ activeCategoryName }}</em>
        </view>
        <view class="shop-search" @click="openSearch">搜索</view>
      </view>
      <scroll-view class="horizontal-cats" scroll-x>
        <view
          v-for="item in topCategoryCards"
          :key="item.id"
          class="cat-item"
          :class="{ active: activeCategoryId === item.id }"
          @click="selectCategory(item.id)"
        >
          <view class="cat-img" :style="backgroundImageStyle(item.imageUrl)" />
          <span class="cat-name">{{ shortCategoryName(item.label) }}</span>
        </view>
      </scroll-view>
    </view>

    <view v-if="state.station.status !== 1" class="station-alert">
      <text>{{ state.station.abnormalReason || '当前自提点暂不可选，请重新选择自提点' }}</text>
      <button class="plain small" @click="stationSheetVisible = true">重新选择</button>
    </view>

    <view class="activity-head">
      <span>{{ activeCategoryName === '全部' ? '当前自提点' : activeCategoryName }}</span>
      <b>{{ activeActivityTitle }}</b>
      <span>活动商品列表</span>
    </view>

    <view class="category-layout">
      <scroll-view class="side-cats" scroll-y>
        <view
          v-for="item in categoryCards"
          :key="item.id"
          :class="{ active: activeCategoryId === item.id }"
          @click="selectCategory(item.id)"
        >
          {{ shortCategoryName(item.label) }}
        </view>
      </scroll-view>
      <view class="cat-main">
        <scroll-view class="sub-cat-strip" scroll-x>
          <button
            v-for="(item, index) in subCategoryCards"
            :key="item"
            class="sub-cat-chip"
            :class="{ active: activeSubIndex === index }"
            @click="activeSubIndex = index"
          >
            {{ item }}
          </button>
        </scroll-view>

        <view class="cat-products">
          <view
            v-for="item in sortedProducts"
            :key="item.publishSkuId"
            class="cat-product"
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
            <view class="cat-product-img" :style="backgroundImageStyle(item.mainImageUrl || fallbackProductImages[0])" />
            <view class="cat-product-info">
              <h4>{{ item.productName }}</h4>
              <view class="shop-product-spec">{{ productSpecText(item) }}</view>
              <view class="price-row">
                <span class="shop-price">¥{{ item.salePrice }}</span>
                <span v-if="productOldPrice(item)" class="old-price">¥{{ productOldPrice(item) }}</span>
              </view>
              <view class="shop-sale-meta">
                <span v-if="Number(item.soldQty || 0) > 0" class="sold-count">已售{{ item.soldQty }}</span>
                <span class="sale-stock">
                  <b>{{ isSoldout(item) ? '已售罄' : `剩余${item.availableQty}` }}</b>
                </span>
              </view>
              <view class="shop-product-tag-row">
                <span class="shop-product-date">提货日期 {{ productPickupText(item.deliveryDate) }}</span>
              </view>
            </view>
            <button
              class="round-add"
              :disabled="isSoldout(item)"
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
            v-if="sortedProducts.length === 0"
            title="当前类目暂无商品"
            sub="空分类入口已在首页隐藏，可切换其他类目查看。"
            icon="类"
            button-text="返回首页"
            @action="goHome"
          />
        </view>
      </view>
    </view>

    <StationSheet :visible="stationSheetVisible" @close="stationSheetVisible = false" />
    <UserToast />
    <UserTabBar active="category" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import StationSheet from '@/components/StationSheet.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageCategories, pageProducts, type UserCategoryDTO, type UserProductCardDTO } from '@/api/user';
import {
    addProductToCart,
    navigateUser,
    showUserToast,
    toggleFavorite,
    toggleNotice,
    useUserState
} from '@/stores/userState';
import {
    fallbackCategories,
    fallbackProductImages,
    fallbackProducts,
    isSoldout,
    productOldPrice,
    productPickupText,
    productSpecText,
    normalizeCategoryName,
    withProductImages
} from '@/utils/userFallbackData';

const state = useUserState();
const stationSheetVisible = ref(false);
const categories = ref<UserCategoryDTO[]>([]);
const products = ref<UserProductCardDTO[]>([]);
const activeCategoryId = ref(0);
const activeSubIndex = ref(0);
let loadVersion = 0;

const subCategoryMap: Record<string, string[]> = {
    全部: ['精选推荐', '今日上新', '销量优先', '次日达'],
    水果: ['柑橘橙柚', '莓果葡萄', '苹果梨桃', '礼盒装'],
    蔬菜百: ['叶菜', '茄果', '菌菇', '净菜'],
    海鲜: ['鱼虾蟹贝', '冷冻海味', '即烹食材'],
    熟食: ['卤味', '凉菜', '加热即食'],
    肉禽蛋: ['鸡鸭禽肉', '猪牛羊', '蛋品'],
    零食: ['坚果', '饼干糕点', '饮品'],
    早餐: ['面包吐司', '牛奶豆浆', '速食早餐'],
    乳品: ['低温奶', '酸奶', '奶酪'],
    粮油: ['米面杂粮', '食用油', '调味']
};
const fallbackCategoryProductIds: Record<string, number[]> = {
    水果: [610001, 610002, 610005, 610009],
    蔬菜百: [610005, 610006],
    肉禽蛋: [610003, 610008],
    早餐: [610003, 610004],
    乳品: [610004],
    粮油: [610007, 610010]
};

const categoryCards = computed(() => {
    const allCategory = fallbackCategories[0];
    const rows = categories.value.filter((item) => item.categoryName !== allCategory.label).slice(0, 9).map((item, index) => ({
        id: item.id,
        categoryName: item.categoryName,
        label: normalizeCategoryName(item.categoryName),
        imageUrl: fallbackCategories[(index + 1) % fallbackCategories.length].imageUrl
    }));
    const existingNames = new Set([allCategory.label, ...rows.map((item) => item.label)]);
    const filled = [
        allCategory,
        ...rows,
        ...fallbackCategories.slice(1).filter((item) => !existingNames.has(item.label) && !rows.some((row) => row.id === item.id))
    ];
    return filled.slice(0, 10);
});
const topCategoryCards = computed(() => categoryCards.value.filter((item) => item.id !== 0).slice(0, 5));
const activeCategoryName = computed(() => categoryCards.value.find((item) => item.id === activeCategoryId.value)?.label || '全部');
const activeActivityTitle = computed(() => {
    if (activeCategoryName.value === '全部') {
        return '今日鲜选好物';
    }
    return `${activeCategoryName.value}精选`;
});
const subCategoryCards = computed(() => subCategoryMap[activeCategoryName.value] || subCategoryMap['全部']);
const sortedProducts = computed(() => [...products.value].sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0)));

function readInitialCategoryId() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    return Number(current.options?.categoryId || 0);
}

function shortCategoryName(value: string) {
    return value.length > 4 ? value.slice(0, 4) : value;
}

function fallbackProductsForCategory(categoryName: string) {
    if (!categoryName || categoryName === '全部') {
        return withProductImages(fallbackProducts);
    }
    const productIds = fallbackCategoryProductIds[categoryName] || [];
    if (productIds.length > 0) {
        return withProductImages(fallbackProducts.filter((item) => productIds.includes(item.productId)));
    }
    return withProductImages(
        fallbackProducts.filter((item) => `${item.productName}${item.skuName}${item.saleSpecText || ''}`.includes(categoryName))
    );
}

async function loadCategories() {
    try {
        const page = await pageCategories({ pageNum: 1, pageSize: 10, status: 1 }, { silent: true });
        categories.value = page?.list?.length ? page.list : [];
    } catch {
        categories.value = [];
        console.info('用户端分类接口不可用，已展示本地兜底分类');
    }
    if (!categoryCards.value.some((item) => item.id === activeCategoryId.value)) {
        activeCategoryId.value = categoryCards.value[0]?.id || 0;
    }
}

async function loadProducts() {
    const currentLoad = ++loadVersion;
    try {
        const page = await pageProducts(
            {
                pageNum: 1,
                pageSize: 20,
                categoryId: activeCategoryId.value || undefined,
                userId: state.user.id,
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        if (currentLoad !== loadVersion) {
            return;
        }
        products.value = page?.list?.length ? withProductImages(page.list) : fallbackProductsForCategory(activeCategoryName.value);
    } catch {
        if (currentLoad !== loadVersion) {
            return;
        }
        products.value = fallbackProductsForCategory(activeCategoryName.value);
        console.info('用户端分类商品接口不可用，已展示本地兜底商品');
    }
}

async function selectCategory(id: number) {
    activeCategoryId.value = id;
    activeSubIndex.value = 0;
    await loadProducts();
}

function handleAddProduct(item: UserProductCardDTO) {
    if (!state.authenticated) {
        state.afterLoginUrl = '/pages/category/index';
        showUserToast('请先登录，登录后可加入购物车', 'warn');
        navigateUser('/pages/login/index');
        return;
    }
    addProductToCart(item);
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function openSearch() {
    navigateUser('/pages/search/index');
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}

onMounted(async () => {
    activeCategoryId.value = readInitialCategoryId();
    await loadCategories();
    await loadProducts();
});

watch(
    () => [state.city.id, state.station.id],
    ([cityId, stationId], [previousCityId, previousStationId]) => {
        if (cityId !== previousCityId || stationId !== previousStationId) {
            void loadProducts();
        }
    }
);
</script>

<style lang="scss" scoped>
.category-page {
  min-height: 100vh;
  padding: 0 0 152rpx;
  background: #f6efe7;
}

.category-top {
  min-height: 312rpx;
  padding: 0 24rpx 20rpx;
  color: #7a2718;
  background: linear-gradient(180deg, #d94b34 0%, #fff0e4 100%);
}

.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 68rpx;
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 800;
}

.category-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 170px;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 20rpx;
  color: #8a2418;
  font-weight: 900;
}

.category-station {
  display: grid;
  gap: 4rpx;
  min-width: 0;
}

.category-station::before {
  display: none;
}

.category-station span,
.category-station em {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-station span {
  color: #6f2414;
  font-size: 28rpx;
  line-height: 1.25;
  text-shadow: none;
}

.category-station em {
  display: block;
  color: #9a5f47;
  font-size: 25rpx;
  font-style: normal;
  font-weight: 800;
}

.shop-search {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
  height: 76rpx;
  color: #172033;
  background: #ffffff;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  box-shadow: none;
}

.horizontal-cats {
  display: block;
  overflow: hidden;
  padding: 0 4rpx;
  white-space: nowrap;
}

.horizontal-cats :deep(.uni-scroll-view),
.horizontal-cats :deep(.uni-scroll-view-content),
.sub-cat-strip :deep(.uni-scroll-view),
.sub-cat-strip :deep(.uni-scroll-view-content) {
  scrollbar-width: none;
}

.horizontal-cats :deep(.uni-scroll-view::-webkit-scrollbar),
.horizontal-cats :deep(.uni-scroll-view-content::-webkit-scrollbar),
.sub-cat-strip :deep(.uni-scroll-view::-webkit-scrollbar),
.sub-cat-strip :deep(.uni-scroll-view-content::-webkit-scrollbar) {
  display: none;
}

.horizontal-cats .cat-item {
  display: inline-grid;
  width: 132rpx;
  min-height: 144rpx;
  margin-right: 28rpx;
  justify-items: center;
  align-content: start;
  gap: 8rpx;
  color: #091832;
  font-size: 26rpx;
  font-weight: 900;
  text-align: center;
  vertical-align: top;
}

.horizontal-cats .cat-item.active {
  color: #d94b34;
}

.cat-img {
  width: 96rpx;
  height: 96rpx;
  background-size: cover;
  background-position: center;
  border-radius: 26rpx;
  background-color: #ffffff;
  box-shadow: 0 14rpx 26rpx rgba(126, 76, 49, 0.15);
}

.horizontal-cats .cat-item.active .cat-img {
  outline: 4rpx solid #e85d3f;
  outline-offset: 4rpx;
}

.cat-name {
  overflow: hidden;
  width: 100%;
  text-overflow: ellipsis;
  white-space: normal;
  line-height: 1.18;
}

.station-alert {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  margin: 18rpx 20rpx;
  padding: 18rpx;
  color: #b42318;
  background: #fff1e9;
  border: 1rpx solid #ffd2c2;
  border-radius: 22rpx;
  font-size: 26rpx;
}

.activity-head {
  position: relative;
  display: grid;
  gap: 6rpx;
  min-height: 236rpx;
  margin: 20rpx 24rpx 0;
  padding: 32rpx;
  overflow: hidden;
  color: #ffffff;
  background:
    linear-gradient(90deg, rgba(15, 23, 42, 0.72), rgba(15, 23, 42, 0.16)),
    url("/static/user-home/shop-category.jpg") center / cover;
  border-radius: 26rpx;
  box-shadow: 0 16rpx 36rpx rgba(126, 76, 49, 0.1);
}

.activity-head b,
.activity-head span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-head b {
  color: #ffffff;
  font-size: 54rpx;
  font-weight: 900;
  line-height: 1.08;
}

.activity-head span {
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1.2;
}

.category-layout {
  display: grid;
  grid-template-columns: 184rpx minmax(0, 1fr);
  gap: 20rpx;
  min-height: 860rpx;
  background: #ffffff;
}

.side-cats {
  height: auto;
  min-height: 860rpx;
  padding-bottom: 156rpx;
  background: #f3f3f3;
}

.side-cats view {
  overflow: hidden;
  display: flex;
  align-items: center;
  min-height: 96rpx;
  padding: 24rpx 16rpx;
  color: #3a3a3a;
  border-left: 8rpx solid transparent;
  font-size: 26rpx;
  line-height: 1.25;
  text-overflow: clip;
  white-space: normal;
}

.side-cats view.active {
  color: #d94b34;
  background: #ffffff;
  border-left-color: #e85d3f;
  font-weight: 900;
}

.cat-main {
  min-width: 0;
  padding: 0 0 156rpx;
  background: #ffffff;
}

.sub-cat-strip {
  display: block;
  height: 104rpx;
  padding: 16rpx 20rpx;
  margin-bottom: 0;
  overflow: hidden;
  white-space: nowrap;
  border-bottom: 1rpx solid #f0e6df;
}

.sub-cat-chip {
  display: inline-flex;
  width: auto;
  min-width: 0;
  min-height: 58rpx;
  margin-right: 16rpx;
  padding: 0 24rpx;
  color: #263043 !important;
  background: #f7f7f8 !important;
  border: 0;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.sub-cat-chip.active {
  color: #d94b34 !important;
  background: #fff0e8 !important;
  border-color: #e85d3f;
}

.cat-products {
  display: grid;
  padding: 0 16rpx 188rpx 20rpx;
  background: #ffffff;
}

.cat-product {
  position: relative;
  display: grid;
  grid-template-columns: 200rpx minmax(0, 1fr);
  gap: 20rpx;
  align-items: start;
  min-height: 232rpx;
  padding: 24rpx 96rpx 24rpx 0;
  background: #ffffff;
  border-bottom: 1rpx solid #f0e6df;
}

.cat-product.soldout {
  opacity: 0.68;
}

.cat-product-img {
  width: 200rpx;
  height: 184rpx;
  background-size: cover;
  background-position: center;
  border-radius: 20rpx;
  background-color: #f7f1ea;
}

.cat-product.soldout .cat-product-img {
  filter: grayscale(1) saturate(0.45) brightness(0.92);
}

.cat-product-info {
  min-width: 0;
  padding-right: 0;
}

.cat-product h4 {
  display: -webkit-box;
  margin: 0 0 6rpx;
  overflow: hidden;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.25;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  margin-bottom: 6rpx;
  overflow: hidden;
  color: #66716a;
  font-size: 25rpx;
  line-height: 1.25;
  text-overflow: clip;
  white-space: normal;
}

.price-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8rpx;
  min-width: 0;
  white-space: nowrap;
}

.shop-price {
  color: #e60012;
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1;
}

.old-price {
  color: #999999;
  font-size: 25rpx;
  text-decoration: line-through;
}

.shop-sale-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6rpx;
  margin-top: 8rpx;
  color: #5e6a60;
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.25;
}

.shop-sale-meta span,
.shop-sale-meta b {
  display: inline-flex;
  align-items: center;
  min-height: 32rpx;
  padding: 2rpx 8rpx;
  white-space: nowrap;
  background: #f8faf7;
  border: 1rpx solid #e8eee8;
  border-radius: 999rpx;
}

.shop-sale-meta .sale-stock {
  padding: 0;
  background: transparent;
  border: 0;
}

.shop-sale-meta .sale-stock b {
  color: #526257;
  font-weight: 800;
}

.shop-product-tag-row {
  display: flex;
  min-width: 0;
  margin-top: 8rpx;
}

.shop-product-date {
  display: inline-flex;
  align-items: center;
  min-height: 42rpx;
  max-width: 172rpx;
  padding: 0 12rpx;
  overflow: hidden;
  color: #6f5736;
  background: #fbfaf6;
  border: 1rpx solid #ece6da;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.product-fav-btn {
  position: absolute;
  top: 18rpx;
  right: 0;
  z-index: 3;
  min-width: 68rpx;
  min-height: 46rpx;
  padding: 0 12rpx;
  color: #1f8a70 !important;
  background: #ffffff !important;
  border: 1rpx solid rgba(31, 138, 112, 0.24);
  border-radius: 999rpx;
  box-shadow: 0 8rpx 24rpx rgba(23, 36, 32, 0.08);
  font-size: 25rpx;
  font-weight: 900;
}

.product-fav-btn.active {
  color: #ffffff !important;
  background: #1f8a70 !important;
  border-color: #1f8a70;
}

.round-add {
  position: absolute;
  right: 4rpx;
  bottom: 26rpx;
  z-index: 2;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 50%;
  box-shadow: 0 12rpx 32rpx rgba(232, 93, 63, 0.34);
}

.cat-product.soldout .round-add,
.round-add[disabled] {
  background: #c8c0ba !important;
  box-shadow: none;
}

.round-add svg {
  width: 44rpx;
  height: 44rpx;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.6;
}

.category-page {
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
}

.category-top {
  min-height: 300rpx;
}

.category-bar {
  margin-bottom: 18rpx;
}

.horizontal-cats .cat-item {
  min-height: 126rpx;
  margin-right: 34rpx;
}

.cat-img {
  width: 92rpx;
  height: 92rpx;
}

.activity-head {
  min-height: 188rpx;
  margin-bottom: 16rpx;
  padding: 30rpx;
}

.activity-head b {
  font-size: 46rpx;
}

.category-layout {
  grid-template-columns: 148rpx minmax(0, 1fr);
}

.side-cats view {
  min-height: 96rpx;
  padding: 22rpx 14rpx;
}

.sub-cat-strip {
  height: 88rpx;
  padding: 14rpx 16rpx;
}

.sub-cat-chip {
  min-height: 56rpx;
  margin-right: 14rpx;
  padding: 0 22rpx;
}

.cat-products {
  padding: 0 8rpx calc(148rpx + env(safe-area-inset-bottom)) 10rpx;
}

.cat-product {
  grid-template-columns: 82px minmax(0, 1fr);
  gap: 9px;
  align-items: center;
  height: 106px;
  min-height: 106px;
  padding: 8px 38px 8px 0;
  overflow: hidden;
}

.cat-product-img {
  width: 82px;
  height: 90px;
  border-radius: 8px;
}

.cat-product-info {
  display: grid;
  align-content: center;
  gap: 2px;
  height: 90px;
  min-width: 0;
  overflow: hidden;
}

.cat-product h4 {
  margin: 0;
  max-width: 100%;
  font-size: 15px;
  line-height: 1.18;
  -webkit-line-clamp: 1;
}

.shop-product-spec {
  margin: 0;
  max-width: 100%;
  font-size: 14px;
  line-height: 1.22;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  flex-wrap: nowrap;
  gap: 5px;
  min-height: 25px;
  margin: 0;
}

.shop-price {
  font-size: 23px;
}

.old-price {
  font-size: 13px;
}

.shop-sale-meta {
  flex-wrap: nowrap;
  gap: 3px;
  height: auto;
  min-height: 24px;
  max-width: 100%;
  margin: 0;
  overflow: hidden;
  font-size: 14px;
}

.shop-sale-meta span,
.shop-sale-meta b {
  height: auto;
  min-height: 22px;
  padding: 2px 5px;
  max-width: 86px;
  overflow: hidden;
  color: #9b4d1f;
  background: #fff2e8;
  border: 0;
  border-radius: 4px;
  text-overflow: ellipsis;
}

.cat-product .shop-product-tag-row,
.cat-product .product-fav-btn {
  display: none;
}

.round-add {
  top: 50%;
  right: 2px;
  bottom: auto;
  width: 34px;
  min-width: 34px;
  height: 34px;
  min-height: 34px;
  transform: translateY(-50%);
}

.round-add svg {
  width: 18px;
  height: 18px;
}
</style>
