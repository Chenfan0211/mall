<template>
  <view class="page category-page">
    <view class="category-top">
      <view class="status-line">
        <text>{{ state.station.name }}</text>
        <text>当前类目 · {{ activeCategoryName }}</text>
      </view>
      <view class="shop-search" @click="openSearch">搜索</view>
      <scroll-view class="horizontal-cats" scroll-x>
        <view
          v-for="item in categoryCards"
          :key="item.id"
          class="cat-item"
          :class="{ active: activeCategoryId === item.id }"
          @click="selectCategory(item.id)"
        >
          <view class="cat-img">{{ item.categoryName.slice(0, 1) }}</view>
          <text>{{ item.categoryName }}</text>
        </view>
      </scroll-view>
    </view>

    <view class="sort-card">
      <view class="chip-row">
        <text v-for="item in sortTabs" :key="item" class="pill" :class="{ active: activeSort === item }" @click="activeSort = item">
          {{ item }}
        </text>
      </view>
    </view>

    <view class="product-list">
      <ProductCard
        v-for="item in sortedProducts"
        :key="item.publishSkuId"
        :product="item"
        :favorite="state.favorites.has(item.productId)"
        @open="openDetail(item.productId)"
        @favorite="toggleFavorite(item.productId)"
        @notice="toggleNotice(item.productId)"
        @add="handleAddProduct(item)"
      />
      <EmptyActionCard
        v-if="sortedProducts.length === 0"
        title="当前类目暂无商品"
        sub="空分类入口已在首页隐藏，可切换其他类目查看。"
        icon="类"
        button-text="返回首页"
        @action="goHome"
      />
    </view>

    <UserToast />
    <UserTabBar active="category" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import ProductCard from '@/components/ProductCard.vue';
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

const state = useUserState();
const categories = ref<UserCategoryDTO[]>([]);
const products = ref<UserProductCardDTO[]>([]);
const activeCategoryId = ref<number | undefined>();
const activeSort = ref('推荐');
const sortTabs = ['推荐', '销量', '上新', '价格'];

const categoryCards = computed(() => {
    if (categories.value.length > 0) {
        return categories.value;
    }
    return [
        { id: 1, categoryName: '全部', status: 1 },
        { id: 2, categoryName: '水果', status: 1 },
        { id: 3, categoryName: '蔬菜', status: 1 },
        { id: 4, categoryName: '肉蛋奶', status: 1 },
        { id: 5, categoryName: '早餐', status: 1 }
    ] as UserCategoryDTO[];
});
const activeCategoryName = computed(() => categoryCards.value.find((item) => item.id === activeCategoryId.value)?.categoryName || '全部');
const sortedProducts = computed(() => {
    const list = [...products.value];
    if (activeSort.value === '价格') {
        list.sort((a, b) => Number(a.salePrice || 0) - Number(b.salePrice || 0));
    }
    if (activeSort.value === '销量') {
        list.sort((a, b) => Number(b.soldQty || 0) - Number(a.soldQty || 0));
    }
    return list.sort((a, b) => Number(b.availableQty > 0) - Number(a.availableQty > 0));
});

async function loadCategories() {
    try {
        const page = await pageCategories({ pageNum: 1, pageSize: 20, status: 1 });
        categories.value = page.list || [];
        activeCategoryId.value = categories.value[0]?.id || 1;
    } catch (error) {
        activeCategoryId.value = 1;
    }
}

async function loadProducts() {
    try {
        const page = await pageProducts({
            pageNum: 1,
            pageSize: 20,
            categoryId: activeCategoryId.value,
            userId: state.user.id,
            cityId: state.city.id,
            stationId: state.station.id
        });
        products.value = page.list || [];
    } catch (error) {
        showUserToast('分类商品加载失败', 'warn');
    }
}

async function selectCategory(id: number) {
    activeCategoryId.value = id;
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
    uni.switchTab({ url: '/pages/home/index' });
}

onMounted(async () => {
    await loadCategories();
    await loadProducts();
});
</script>

<style lang="scss" scoped>
.category-page {
  padding-left: 0;
  padding-right: 0;
}

.category-top {
  padding: 38rpx 20rpx 18rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #f28a42 100%);
}

.status-line {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.status-line text:first-child {
  overflow: hidden;
  font-size: 30rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-line text:last-child {
  color: rgba(255, 255, 255, 0.88);
  font-size: 24rpx;
}

.shop-search {
  display: flex;
  align-items: center;
  height: 70rpx;
  padding: 0 22rpx;
  color: #7b5f51;
  background: #ffffff;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
}

.horizontal-cats {
  margin-top: 20rpx;
  white-space: nowrap;
}

.cat-item {
  display: inline-grid;
  justify-items: center;
  width: 112rpx;
  margin-right: 12rpx;
  gap: 8rpx;
  color: rgba(255, 255, 255, 0.88);
  font-size: 21rpx;
  font-weight: 800;
  text-align: center;
}

.cat-item.active {
  color: #ffffff;
}

.cat-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 82rpx;
  height: 82rpx;
  color: #7c1d12;
  background: #ffffff;
  border-radius: 24rpx;
  font-size: 30rpx;
  font-weight: 900;
}

.cat-item.active .cat-img {
  color: #ffffff;
  background: #172033;
}

.cat-item text {
  overflow: hidden;
  width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sort-card {
  margin: 18rpx 20rpx;
  padding: 18rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
}

.product-list {
  margin: 0 20rpx;
}
</style>
