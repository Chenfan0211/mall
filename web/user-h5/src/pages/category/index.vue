<template>
  <view class="page category-page shop-page" data-m-page="category">
    <view class="category-top">
      <view class="status-bar">
        <text>14:49</text>
        <text>WiFi 80</text>
      </view>
      <view class="category-bar">
        <view class="category-station" @click="stationSheetVisible = true">
          <span>自提点 中轮 - {{ state.station.name }}</span>
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
        <view v-if="subCategoryCards.length > 0" class="sub-cat-strip">
          <button
            v-for="item in subCategoryCards"
            :key="item.id"
            class="sub-cat-chip"
            :class="{ active: activeSubCategoryId === item.id }"
            @click="selectSubCategory(item.id)"
          >
            {{ item.label }}
          </button>
        </view>

        <view class="sort-strip">
          <button
            v-for="item in sortOptions"
            :key="item.field"
            class="sort-chip"
            :class="{ active: activeSortField === item.field }"
            @click="selectSort(item.field)"
          >
            <text>{{ item.label }}</text>
            <text v-if="item.sortable" class="sort-arrows">
              <text :class="{ active: activeSortField === item.field && activeSortOrder === 'asc' }">↑</text>
              <text :class="{ active: activeSortField === item.field && activeSortOrder === 'desc' }">↓</text>
            </text>
          </button>
        </view>

        <view class="cat-products">
          <view
            v-for="item in products"
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
            <view class="product-action">
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
          </view>

          <EmptyActionCard
            v-if="products.length === 0"
            title="当前类目暂无商品"
            sub="可切换其他类目查看。"
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
    fallbackProductImages,
    isSoldout,
    productOldPrice,
    productPickupText,
    productSpecText,
    normalizeCategoryName,
    withProductImages
} from '@/utils/userFallbackData';

type SortField = 'new' | 'sales' | 'price';
type SortOrder = 'asc' | 'desc';

const state = useUserState();
const stationSheetVisible = ref(false);
const categories = ref<UserCategoryDTO[]>([]);
const products = ref<UserProductCardDTO[]>([]);
const activeCategoryId = ref(0);
const activeSubCategoryId = ref(0);
const activeSortField = ref<SortField>('new');
const activeSortOrder = ref<SortOrder>('desc');
let loadVersion = 0;

const sortOptions = [
    { field: 'new', label: '今日上新', sortable: false },
    { field: 'sales', label: '销量', sortable: true },
    { field: 'price', label: '价格', sortable: true }
] as const;

const categoryCards = computed(() => {
    const allCategory = {
        id: 0,
        categoryName: '全部',
        label: '全部',
        imageUrl: fallbackProductImages[0],
        children: [] as UserCategoryDTO[]
    };
    const rows = categories.value.slice(0, 20).map((item) => ({
        id: item.id,
        categoryName: item.categoryName,
        label: normalizeCategoryName(item.categoryName),
        imageUrl: item.imageUrl || fallbackProductImages[0],
        children: item.children || []
    }));
    return [allCategory, ...rows];
});
const topCategoryCards = computed(() => categoryCards.value.filter((item) => item.id !== 0).slice(0, 8));
const activeCategory = computed(() => categoryCards.value.find((item) => item.id === activeCategoryId.value));
const subCategoryCards = computed(() => {
    if (!activeCategoryId.value || !activeCategory.value?.children?.length) {
        return [];
    }
    return [
        { id: 0, label: '全部' },
        ...activeCategory.value.children.map((item) => ({
            id: item.id,
            label: normalizeCategoryName(item.categoryName)
        }))
    ];
});
const queryCategoryId = computed(() => activeSubCategoryId.value || activeCategoryId.value || undefined);

function readInitialCategoryId() {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    return Number(current.options?.categoryId || 0);
}

function shortCategoryName(value: string) {
    return value.length > 4 ? value.slice(0, 4) : value;
}

async function loadCategories() {
    try {
        const page = await pageCategories(
            {
                pageNum: 1,
                pageSize: 50,
                status: 1,
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        categories.value = page?.list?.length ? page.list : [];
    } catch (error) {
        categories.value = [];
        console.info('用户端分类接口不可用，分类页展示空状态', error);
    }
    if (!categoryCards.value.some((item) => item.id === activeCategoryId.value)) {
        activeCategoryId.value = 0;
        activeSubCategoryId.value = 0;
    } else if (activeSubCategoryId.value && !subCategoryCards.value.some((item) => item.id === activeSubCategoryId.value)) {
        activeSubCategoryId.value = 0;
    }
}

async function loadProducts() {
    const currentLoad = ++loadVersion;
    try {
        const page = await pageProducts(
            {
                pageNum: 1,
                pageSize: 20,
                categoryId: queryCategoryId.value,
                userId: state.user.id,
                cityId: state.city.id,
                stationId: state.station.id,
                sortField: activeSortField.value,
                sortOrder: activeSortOrder.value
            },
            { silent: true }
        );
        if (currentLoad !== loadVersion) {
            return;
        }
        products.value = page?.list?.length ? withProductImages(page.list) : [];
    } catch (error) {
        if (currentLoad !== loadVersion) {
            return;
        }
        products.value = [];
        console.info('用户端分类商品接口不可用，分类页展示空状态', error);
    }
}

async function selectCategory(id: number) {
    if (activeCategoryId.value === id) {
        return;
    }
    activeCategoryId.value = id;
    activeSubCategoryId.value = 0;
    await loadProducts();
}

async function selectSubCategory(id: number) {
    if (activeSubCategoryId.value === id) {
        return;
    }
    activeSubCategoryId.value = id;
    await loadProducts();
}

async function selectSort(field: SortField) {
    if (field === 'new') {
        activeSortField.value = 'new';
        activeSortOrder.value = 'desc';
    } else if (activeSortField.value === field) {
        activeSortOrder.value = activeSortOrder.value === 'desc' ? 'asc' : 'desc';
    } else {
        activeSortField.value = field;
        activeSortOrder.value = 'desc';
    }
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
    async ([cityId, stationId], [previousCityId, previousStationId]) => {
        if (cityId !== previousCityId || stationId !== previousStationId) {
            await loadCategories();
            await loadProducts();
        }
    }
);
</script>

<style lang="scss" scoped>
.category-page {
  min-height: 100vh;
  padding: 0 0 calc(132rpx + env(safe-area-inset-bottom));
  background: #f6efe7;
}

.category-top {
  min-height: 300rpx;
  padding: 0 24rpx 20rpx;
  color: #7a2718;
  background: linear-gradient(180deg, #d94b34 0%, #fff0e4 100%);
}

.status-bar {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 4rpx 12rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  font-weight: 700;
}

.category-bar {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.category-station {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  min-height: 72rpx;
  padding: 14rpx 20rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 18rpx;
  box-shadow: 0 12rpx 28rpx rgba(132, 52, 28, 0.12);
}

.category-station span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-station span {
  color: #6d2b1f;
  font-size: 28rpx;
  font-weight: 900;
}

.shop-search {
  width: 118rpx;
  min-height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7a321d;
  background: #fff8ef;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
}

.horizontal-cats {
  white-space: nowrap;
}

.horizontal-cats .cat-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 112rpx;
  min-height: 126rpx;
  margin-right: 26rpx;
  color: #8b4a2a;
}

.horizontal-cats .cat-item.active {
  color: #d94b34;
}

.cat-img {
  width: 92rpx;
  height: 92rpx;
  background-color: #fff8ef;
  background-position: center;
  background-size: cover;
  border: 4rpx solid rgba(255, 255, 255, 0.86);
  border-radius: 28rpx;
  box-shadow: 0 12rpx 24rpx rgba(122, 43, 24, 0.12);
}

.cat-name {
  margin-top: 8rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.station-alert {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin: 18rpx 24rpx;
  padding: 18rpx 20rpx;
  color: #9b4d1f;
  background: #fff7e8;
  border: 1rpx solid #f2d7b7;
  border-radius: 18rpx;
  font-size: 25rpx;
  font-weight: 700;
}

.station-alert text {
  flex: 1;
}

.station-alert button {
  min-width: 132rpx;
  min-height: 54rpx;
  padding: 0 18rpx;
  color: #fff !important;
  background: #e85d3f !important;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.category-layout {
  display: grid;
  grid-template-columns: 148rpx minmax(0, 1fr);
  min-height: calc(100vh - 300rpx);
}

.side-cats {
  min-height: 100%;
  background: #fff8ef;
}

.side-cats view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 96rpx;
  padding: 22rpx 14rpx;
  color: #8c6d51;
  border-left: 6rpx solid transparent;
  font-size: 26rpx;
  font-weight: 900;
  text-align: center;
}

.side-cats view.active {
  color: #d94b34;
  background: #fff;
  border-left-color: #e85d3f;
}

.cat-main {
  min-width: 0;
}

.sub-cat-strip {
  display: flex;
  align-items: center;
  gap: 10rpx;
  min-height: 78rpx;
  padding: 12rpx 16rpx 8rpx;
  overflow-x: auto;
  background: #fff;
  border-bottom: 1rpx solid #f3e3d4;
}

.sub-cat-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  min-height: 52rpx;
  padding: 0 20rpx;
  color: #8c6d51 !important;
  background: #fbf4ec !important;
  border: 0;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-weight: 900;
}

.sub-cat-chip.active {
  color: #fff !important;
  background: #e85d3f !important;
}

.sort-strip {
  display: flex;
  align-items: center;
  gap: 12rpx;
  min-height: 88rpx;
  padding: 14rpx 16rpx;
  overflow-x: auto;
  background: #fff;
}

.sort-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  min-width: 118rpx;
  min-height: 58rpx;
  padding: 0 20rpx;
  color: #8c6d51 !important;
  background: #fbf4ec !important;
  border: 0;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
}

.sort-chip.active {
  color: #fff !important;
  background: #e85d3f !important;
}

.sort-arrows {
  display: inline-flex;
  flex-direction: column;
  gap: 0;
  font-size: 18rpx;
  line-height: 1;
  opacity: 0.72;
}

.sort-arrows .active {
  opacity: 1;
  font-weight: 900;
}

.cat-products {
  padding: 0 8rpx calc(148rpx + env(safe-area-inset-bottom)) 10rpx;
}

.cat-product {
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr) 44px;
  gap: 10px;
  align-items: center;
  min-height: 120px;
  margin-top: 16rpx;
  padding: 10px 10px 10px 0;
  overflow: hidden;
  background: #fff;
  border-radius: 18rpx;
  box-shadow: 0 12rpx 30rpx rgba(94, 49, 28, 0.08);
}

.cat-product-img {
  width: 92px;
  height: 100px;
  margin-left: 8px;
  background-color: #f6efe7;
  background-position: center;
  background-size: cover;
  border-radius: 8px;
}

.cat-product-info {
  display: grid;
  align-content: center;
  gap: 4px;
  min-height: 100px;
  min-width: 0;
  overflow: hidden;
}

.cat-product h4 {
  display: -webkit-box;
  max-width: 100%;
  margin: 0;
  overflow: hidden;
  color: #2c221a;
  font-size: 15px;
  font-weight: 900;
  line-height: 1.28;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.shop-product-spec {
  max-width: 100%;
  margin: 0;
  overflow: hidden;
  color: #8e7a67;
  font-size: 14px;
  line-height: 1.3;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  display: flex;
  align-items: baseline;
  flex-wrap: nowrap;
  gap: 5px;
  min-height: 25px;
  margin: 0;
}

.shop-price {
  color: #e85d3f;
  font-size: 23px;
  font-weight: 1000;
}

.old-price {
  color: #c4aa95;
  font-size: 13px;
  text-decoration: line-through;
}

.shop-sale-meta {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 3px;
  min-height: 22px;
  max-width: 100%;
  margin: 0;
  overflow: hidden;
  font-size: 14px;
}

.shop-sale-meta span,
.shop-sale-meta b {
  display: inline-flex;
  align-items: center;
  min-height: 22px;
  max-width: 96px;
  padding: 2px 5px;
  overflow: hidden;
  color: #9b4d1f;
  background: #fff2e8;
  border-radius: 4px;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-sale-meta .sale-stock {
  padding: 0;
  background: transparent;
}

.shop-product-tag-row,
.product-fav-btn {
  display: none;
}

.product-action {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
}

.round-add {
  width: 34px;
  min-width: 34px;
  height: 34px;
  min-height: 34px;
  padding: 0;
  color: #fff !important;
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
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.6;
}
</style>
