<template>
  <view class="page cart-page">
    <view class="cart-head">
      <view>
        <text class="cart-kicker">购物车</text>
        <text class="cart-title">确认本次团期商品</text>
      </view>
      <text class="cart-count">{{ state.cartItems.length }} 件</text>
    </view>

    <view class="cart-tools">
      <view class="cart-select" @click="toggleAll">
        <view class="check-dot" :class="{ checked: allSelected }"></view>
        <text>全选</text>
      </view>
      <button v-if="invalidItems.length > 0" class="plain small" @click="clearInvalid">清理失效商品</button>
    </view>

    <view class="cart-list">
      <view v-for="item in state.cartItems" :key="item.id" class="cart-card" :class="{ invalid: !item.valid }">
        <view class="check-dot" :class="{ checked: item.selected && item.valid }" @click="toggleItem(item.id)"></view>
        <image v-if="item.image" class="cart-img" :src="item.image" mode="aspectFill" @click="openProduct(item.productId)" />
        <view v-else class="cart-img fallback">鲜</view>
        <view class="cart-info">
          <text class="cart-name" @click="openProduct(item.productId)">{{ item.title }}</text>
          <text class="cart-spec">{{ item.spec || '默认规格' }}</text>
          <view class="cart-tags">
            <text>{{ item.valid ? '可结算' : item.invalidReason || '已失效' }}</text>
            <text>{{ item.pickupDate || state.station.deliveryTime || '提货日待定' }}</text>
          </view>
          <view class="cart-price-line">
            <text class="cart-price">¥{{ item.price }}</text>
            <view class="qty-stepper">
              <button class="qty-btn" :disabled="!item.valid" @click="changeQty(item.id, item.qty - 1)">-</button>
              <text>{{ item.qty }}</text>
              <button class="qty-btn" :disabled="!item.valid" @click="changeQty(item.id, item.qty + 1)">+</button>
            </view>
          </view>
          <view class="cart-action-row">
            <button class="plain small" @click="askDelete(item.id)">删除</button>
          </view>
        </view>
      </view>

      <EmptyActionCard
        v-if="state.cartItems.length === 0"
        title="购物车为空"
        sub="去首页挑选今天可提的商品。"
        icon="车"
        button-text="去首页"
        @action="goHome"
      />
    </view>

    <view class="cart-settle">
      <view>
        <text class="settle-label">合计</text>
        <text class="settle-price">¥{{ totalAmount }}</text>
        <text class="settle-tip">{{ settleTip }}</text>
      </view>
      <button class="settle-btn" :disabled="selectedItems.length === 0" @click="checkout">去结算</button>
    </view>

    <ConfirmDialog
      :visible="deleteDialog.visible"
      title="删除商品"
      content="确认从购物车删除该商品？删除后可重新加入。"
      confirm-text="删除"
      danger
      @cancel="deleteDialog.visible = false"
      @confirm="confirmDelete"
    />
    <UserToast />
    <UserTabBar active="cart" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageCartItems, type UserCartItemDTO } from '@/api/user';
import {
    cartTotal,
    clearInvalidCartItems,
    invalidCartItems,
    navigateUser,
    removeCartItem,
    removeCartItemOrAsk,
    selectedCartItems,
    showUserToast,
    syncCartFromApi,
    toggleCartItem,
    toggleSelectAllCartItems,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const deleteDialog = reactive({
    visible: false,
    id: 0
});
const selectedItems = computed(() => selectedCartItems(state));
const invalidItems = computed(() => invalidCartItems(state));
const allSelected = computed(() => state.cartItems.some((item) => item.valid) && state.cartItems.filter((item) => item.valid).every((item) => item.selected));
const totalAmount = computed(() => cartTotal(state));
const settleTip = computed(() => {
    if (state.cartItems.length === 0) {
        return '添加商品后可结算';
    }
    if (selectedItems.value.length === 0) {
        return '请选择需要结算的商品';
    }
    return invalidItems.value.length > 0 ? '已过滤失效商品，提交后锁定库存' : '提交后锁定库存';
});

async function loadData() {
    try {
        const page = await pageCartItems({
            pageNum: 1,
            pageSize: 20,
            userId: state.user.id,
            cityId: state.city.id,
            stationId: state.station.id
        });
        syncCartFromApi((page.list || []) as UserCartItemDTO[]);
    } catch (error) {
        showUserToast('购物车接口暂不可用，已保留本地购物车状态', 'warn');
    }
}

function checkout() {
    if (selectedItems.value.length === 0) {
        showUserToast('请选择需要结算的商品', 'warn');
        return;
    }
    navigateUser('/pages/checkout/index');
}

function toggleItem(id: number) {
    toggleCartItem(state, id);
}

function toggleAll() {
    toggleSelectAllCartItems(state, !allSelected.value);
}

function changeQty(id: number, qty: number) {
    const result = removeCartItemOrAsk(state, id, qty);
    if (result.requiresConfirm) {
        askDelete(id);
    }
}

function askDelete(id: number) {
    deleteDialog.id = id;
    deleteDialog.visible = true;
}

function confirmDelete() {
    removeCartItem(state, deleteDialog.id);
    deleteDialog.visible = false;
    showUserToast('商品已删除');
}

function clearInvalid() {
    const count = clearInvalidCartItems(state);
    showUserToast(count > 0 ? `已清理 ${count} 件失效商品` : '暂无失效商品');
}

function openProduct(productId?: number) {
    if (!productId) {
        return;
    }
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goHome() {
    uni.switchTab({ url: '/pages/home/index' });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: 100vh;
  padding: 0 0 238rpx;
  background:
    radial-gradient(circle at 86% 8%, rgba(255, 214, 172, 0.85), transparent 24%),
    linear-gradient(180deg, #d94b34 0%, #ef7a37 186rpx, #f7f1ea 188rpx, #f7f1ea 100%);
}

.cart-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20rpx;
  padding: 46rpx 28rpx 26rpx;
  color: #ffffff;
}

.cart-kicker,
.cart-title,
.cart-count {
  display: block;
}

.cart-kicker {
  color: rgba(255, 255, 255, 0.84);
  font-size: 22rpx;
  font-weight: 900;
}

.cart-title {
  margin-top: 8rpx;
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.12;
}

.cart-count {
  min-height: 56rpx;
  padding: 0 22rpx;
  color: #c2412d;
  background: #ffffff;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
  line-height: 56rpx;
  box-shadow: 0 12rpx 28rpx rgba(121, 74, 43, 0.16);
}

.cart-list {
  display: grid;
  gap: 16rpx;
  padding: 0 20rpx;
}

.cart-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin: 0 20rpx 16rpx;
  padding: 16rpx 18rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 22rpx;
}

.cart-select {
  display: flex;
  align-items: center;
  gap: 12rpx;
  color: #172033;
  font-size: 24rpx;
  font-weight: 900;
}

.cart-card {
  display: grid;
  grid-template-columns: 38rpx 132rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
  min-height: 172rpx;
  padding: 16rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
  box-shadow: 0 10rpx 24rpx rgba(126, 76, 49, 0.08);
}

.cart-card.invalid {
  opacity: 0.68;
  background: #fffaf7;
}

.check-dot {
  width: 34rpx;
  height: 34rpx;
  border: 2rpx solid #e2cfc5;
  border-radius: 50%;
  background: #ffffff;
}

.check-dot.checked {
  border-color: #e85d3f;
  background: radial-gradient(circle at center, #ffffff 0 28%, #e85d3f 30% 100%);
}

.cart-img {
  display: grid;
  width: 132rpx;
  height: 132rpx;
  place-items: center;
  color: #7c1d12;
  background: linear-gradient(135deg, #fff4c7 0%, #ffcf55 42%, #f05a37 100%);
  border-radius: 18rpx;
  font-size: 38rpx;
  font-weight: 900;
  box-shadow: 0 8rpx 18rpx rgba(23, 32, 51, 0.12);
}

.cart-info {
  min-width: 0;
}

.cart-name,
.cart-spec {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cart-name {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.25;
  white-space: nowrap;
}

.cart-spec {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
  white-space: nowrap;
}

.cart-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 10rpx;
}

.cart-tags text {
  min-height: 32rpx;
  padding: 0 10rpx;
  color: #b73518;
  background: #fff1e9;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 800;
  line-height: 32rpx;
}

.cart-card.invalid .cart-tags text {
  color: #8a6c62;
  background: #f3e6dc;
}

.cart-price-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  margin-top: 12rpx;
}

.cart-price {
  color: #d94b34;
  font-size: 32rpx;
  font-weight: 900;
}

.qty-stepper {
  display: grid;
  grid-template-columns: 48rpx 52rpx 48rpx;
  align-items: center;
  height: 48rpx;
  overflow: hidden;
  color: #172033;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  text-align: center;
}

.qty-btn {
  min-width: 48rpx;
  min-height: 48rpx;
  padding: 0;
  color: #c2412d !important;
  background: transparent !important;
  box-shadow: none;
  font-size: 28rpx;
}

.cart-action-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 12rpx;
}

.cart-action-row button {
  min-height: 48rpx;
  padding: 0 18rpx;
  font-size: 22rpx;
}

.cart-settle {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 216rpx;
  gap: 18rpx;
  align-items: center;
  min-height: 136rpx;
  padding: 18rpx 24rpx 28rpx;
  background: rgba(255, 255, 255, 0.97);
  border-top: 1rpx solid #f0dfd6;
  box-shadow: 0 -14rpx 30rpx rgba(23, 32, 51, 0.12);
}

.settle-label,
.settle-tip {
  display: block;
}

.settle-label {
  color: #172033;
  font-size: 24rpx;
  font-weight: 900;
}

.settle-price {
  margin-left: 8rpx;
  color: #d94b34;
  font-size: 34rpx;
  font-weight: 900;
}

.settle-tip {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 22rpx;
}

.settle-btn {
  min-height: 82rpx;
  font-size: 28rpx;
}

.empty-panel {
  padding: 40rpx 24rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
}
</style>
