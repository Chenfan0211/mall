<template>
  <view class="page cart-page shop-page" data-m-page="cart">
    <view class="cart-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>▮▮▮ WiFi 80</text>
      </view>
      <view class="cart-title-row">
        <view>
          <b>购物车</b>
          <span>{{ state.authenticated ? '确认本次团期商品' : '登录后查看购物车' }}</span>
        </view>
        <view class="mini-pill">••• ◎</view>
      </view>
    </view>

    <view v-if="!state.authenticated" class="cart-login-guard">
      <view class="empty-action-card" data-empty-icon="车">
        <b>登录后查看购物车</b>
        <span>购物车和结算需要先登录。</span>
        <button @click="goLogin">去登录</button>
      </view>
    </view>

    <view class="cart-page-list" :class="{ preview: !state.authenticated }">
      <view class="cart-summary-card cart-station-card">
        <view class="cart-station-main">
          <b>自提点 {{ state.station.name }}</b>
          <span>当前自提点商品</span>
        </view>
        <button class="cart-station-switch" type="button" @click="goStation">切换自提点</button>
      </view>

        <view v-if="validCartItems.length > 0" class="cart-bulk-actions">
          <span>删除当前可选商品。</span>
          <button type="button" @click="askClearValid">清空购物车</button>
        </view>

        <view
          v-for="item in validCartItems"
          :key="item.id"
          class="cart-swipe-wrap"
          :class="{ 'swipe-open': swipedItemId === item.id }"
          @touchstart="startSwipe"
          @touchend="endSwipe($event, item.id)"
          @mousedown="startSwipe"
          @mouseup="endSwipe($event, item.id)"
        >
          <button class="cart-swipe-delete" @click="askDelete(item.id)">删除</button>
          <view class="cart-item" :class="{ invalid: !item.valid }">
            <button class="cart-inline-delete" @click="askDelete(item.id)">删除</button>
            <view class="check-dot" :class="{ off: !item.selected || !item.valid }" @click="toggleItem(item.id)">
              {{ item.selected && item.valid ? '✓' : '' }}
            </view>
            <view class="cart-img" :style="backgroundImageStyle(item.image || fallbackProductImages[0])" @click="openProduct(item.productId)" />
            <view class="cart-item-info">
              <b @click="openProduct(item.productId)">{{ item.title }}</b>
              <view class="shop-product-spec cart-product-spec">{{ item.spec || '默认规格' }}</view>
              <view class="shop-product-tag-row">
                <span class="shop-product-date">提货日期 {{ pickupDateText(item.pickupDate) }}</span>
              </view>
              <view class="shop-sale-meta">
                <span>{{ item.valid ? '可结算' : item.invalidReason || '已失效' }}</span>
                <span>{{ pickupDateText(item.pickupDate) }}</span>
              </view>
              <view class="price-row">
                <span class="shop-price">¥{{ item.price }}</span>
                <span v-if="item.priceChanged" class="cart-price-change-note">价格已更新</span>
              </view>
            </view>
            <view class="cart-actions">
              <view class="qty">
                <button :disabled="!item.valid" @click="changeQty(item.id, item.qty - 1)">−</button>
                <span>{{ item.qty }}</span>
                <button :disabled="!item.valid" @click="changeQty(item.id, item.qty + 1)">+</button>
              </view>
            </view>
            <view v-if="!item.valid" class="cart-invalid-note">{{ item.invalidReason || '商品不可结算' }}</view>
          </view>
        </view>

        <view v-if="invalidItems.length > 0" class="cart-invalid-group" data-cart-invalid-group>
          <view class="cart-invalid-group-head">
            <view>
              <b>失效商品</b>
              <span>以下商品暂不可结算。</span>
            </view>
            <button type="button" @click="clearInvalid">清理失效商品</button>
          </view>
          <view
            v-for="item in invalidItems"
            :key="item.id"
            class="cart-swipe-wrap"
          >
            <view class="cart-item invalid">
              <view class="cart-img" :style="backgroundImageStyle(item.image || fallbackProductImages[0])" @click="openProduct(item.productId)" />
              <view class="cart-item-info">
                <b @click="openProduct(item.productId)">{{ item.title }}</b>
                <view class="shop-product-spec cart-product-spec">{{ item.spec || '默认规格' }}</view>
                <view class="price-row">
                  <span class="shop-price">¥{{ item.price }}</span>
                </view>
              </view>
              <view class="cart-actions">
                <view class="qty">
                  <button disabled>−</button>
                  <span>{{ item.qty }}</span>
                  <button disabled>+</button>
                </view>
              </view>
              <view class="cart-invalid-note">{{ item.invalidReason || '商品已失效' }}，不可结算</view>
            </view>
          </view>
        </view>

        <view v-if="recommendProducts.length > 0" class="recommend-section">
          <h3>购物车推荐<span>凑单热卖</span></h3>
          <view class="recommend-grid">
            <view
              v-for="item in recommendProducts"
              :key="item.publishSkuId"
              class="recommend-card"
              @click="openProduct(item.productId)"
            >
              <view class="recommend-img" :style="backgroundImageStyle(item.mainImageUrl || fallbackProductImages[0])" />
              <b>{{ item.productName }}</b>
              <p>{{ productSpecText(item) }}</p>
              <view class="recommend-bottom">
                <view class="recommend-price-stack">
                  <span>¥{{ item.salePrice }}</span>
                </view>
                <button type="button" aria-label="加入购物车" @click.stop="addRecommend(item)">
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
          </view>
        </view>

      <EmptyActionCard
        v-if="cartItemsForView.length === 0"
        title="购物车为空"
        sub="去首页挑选今天可提的商品。"
        icon="车"
        button-text="去首页"
        @action="goHome"
      />
    </view>

    <view class="cart-settle" :class="{ preview: !state.authenticated }">
      <button class="settle-btn select-all" data-cart-select-all @click="toggleAll">全选</button>
      <view class="cart-total-box">
        <span class="cart-total-label">合计金额</span>
        <b><span class="price">¥{{ totalAmount }}</span></b>
        <em>已选数量：{{ selectedItems.length }}</em>
      </view>
      <button class="settle-btn" data-cart-checkout :disabled="selectedItems.length === 0" @click="checkout">去结算</button>
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
    <ConfirmDialog
      :visible="clearDialogVisible"
      title="确认清空购物车？"
      content="将删除当前可选商品。已下单订单不受影响。"
      confirm-text="确认清空"
      danger
      @cancel="clearDialogVisible = false"
      @confirm="confirmClearValid"
    />
    <UserToast />
    <UserTabBar active="cart" />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageCartItems, type UserCartItemDTO, type UserProductCardDTO } from '@/api/user';
import {
    addProductToCart,
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
import {
    buildFallbackCartItems,
    fallbackProductImages,
    fallbackProducts,
    productPickupText,
    productSpecText
} from '@/utils/userFallbackData';

const state = useUserState();
const deleteDialog = reactive({
    visible: false,
    id: 0
});
const clearDialogVisible = ref(false);
const touchStartX = ref(0);
const swipedItemId = ref(0);
const previewCartItems = buildFallbackCartItems();
const cartItemsForView = computed(() => (state.authenticated ? state.cartItems : previewCartItems));
const validCartItems = computed(() => cartItemsForView.value.filter((item) => item.valid));
const invalidItems = computed(() => state.authenticated ? invalidCartItems(state) : cartItemsForView.value.filter((item) => !item.valid));
const selectedItems = computed(() => state.authenticated ? selectedCartItems(state) : validCartItems.value.filter((item) => item.selected));
const allSelected = computed(() => validCartItems.value.length > 0 && validCartItems.value.every((item) => item.selected));
const totalAmount = computed(() => {
    if (state.authenticated) {
        return cartTotal(state);
    }
    return selectedItems.value.reduce((sum, item) => sum + Number(item.price || 0) * item.qty, 0).toFixed(2);
});
const recommendProducts = computed(() => {
    const existingIds = new Set(cartItemsForView.value.map((item) => item.productId));
    return fallbackProducts.filter((item) => !existingIds.has(item.productId) && Number(item.availableQty || 0) > 0).slice(0, 4);
});
const settleTip = computed(() => {
    if (cartItemsForView.value.length === 0) {
        return '添加商品后可结算';
    }
    if (selectedItems.value.length === 0) {
        return '请选择需要结算的商品';
    }
    return invalidItems.value.length > 0 ? '已过滤失效商品，提交后锁定库存' : '提交后锁定库存';
});

function requireLoginForCartAction() {
    if (state.authenticated) {
        return false;
    }
    state.afterLoginUrl = '/pages/cart/index';
    showUserToast('请先登录，登录后才能操作购物车', 'warn');
    return true;
}

async function loadData() {
    if (!state.authenticated) {
        return;
    }
    applyFallbackCart();
    try {
        const page = await pageCartItems(
            {
                pageNum: 1,
                pageSize: 20,
                userId: state.user.id,
                cityId: state.city.id,
                stationId: state.station.id
            },
            { silent: true }
        );
        if (page.list?.length) {
            syncCartFromApi((page.list || []) as UserCartItemDTO[]);
        }
    } catch {
        console.info('用户端购物车接口不可用，已展示本地购物车兜底数据');
    }
}

function applyFallbackCart() {
    if (state.cartItems.length > 0) {
        return;
    }
    state.cartItems = buildFallbackCartItems();
}

function checkout() {
    if (requireLoginForCartAction()) {
        return;
    }
    if (selectedItems.value.length === 0) {
        showUserToast('请选择需要结算的商品', 'warn');
        return;
    }
    navigateUser('/pages/checkout/index');
}

function toggleItem(id: number) {
    if (requireLoginForCartAction()) {
        return;
    }
    toggleCartItem(state, id);
}

function toggleAll() {
    if (requireLoginForCartAction()) {
        return;
    }
    toggleSelectAllCartItems(state, !allSelected.value);
}

function changeQty(id: number, qty: number) {
    if (requireLoginForCartAction()) {
        return;
    }
    const result = removeCartItemOrAsk(state, id, qty);
    if (result.requiresConfirm) {
        askDelete(id);
    }
}

function pointerX(event: TouchEvent | MouseEvent) {
    return 'changedTouches' in event ? event.changedTouches[0]?.clientX || 0 : event.clientX;
}

function startSwipe(event: TouchEvent | MouseEvent) {
    touchStartX.value = pointerX(event);
}

function endSwipe(event: TouchEvent | MouseEvent, id: number) {
    const endX = pointerX(event) || touchStartX.value;
    const diff = endX - touchStartX.value;
    if (diff < -36) {
        swipedItemId.value = id;
        return;
    }
    if (diff > 24) {
        swipedItemId.value = 0;
    }
}

function askDelete(id: number) {
    if (requireLoginForCartAction()) {
        return;
    }
    deleteDialog.id = id;
    deleteDialog.visible = true;
}

function confirmDelete() {
    removeCartItem(state, deleteDialog.id);
    swipedItemId.value = 0;
    deleteDialog.visible = false;
    showUserToast('商品已删除');
}

function askClearValid() {
    if (requireLoginForCartAction()) {
        return;
    }
    if (validCartItems.value.length === 0) {
        showUserToast('暂无可清空商品', 'warn');
        return;
    }
    clearDialogVisible.value = true;
}

function confirmClearValid() {
    state.cartItems = state.cartItems.filter((item) => !item.valid);
    clearDialogVisible.value = false;
    showUserToast('已清空可选商品');
}

function clearInvalid() {
    if (requireLoginForCartAction()) {
        return;
    }
    const count = clearInvalidCartItems(state);
    showUserToast(count > 0 ? '已清理失效商品' : '暂无失效商品');
}

function openProduct(productId?: number) {
    if (!productId) {
        return;
    }
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goLogin() {
    state.afterLoginUrl = '/pages/cart/index';
    navigateUser('/pages/login/index');
}

function goHome() {
    navigateUser('/pages/home/index', true);
}

function goStation() {
    navigateUser('/pages/entry/index');
}

function backgroundImageStyle(url?: string) {
    return url ? { backgroundImage: `url(${url})` } : {};
}

function pickupDateText(value?: string) {
    return productPickupText(value || state.station.deliveryTime);
}

function addRecommend(item: UserProductCardDTO) {
    if (requireLoginForCartAction()) {
        return;
    }
    addProductToCart(item, item.productName, item.productId);
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: 100vh;
  padding: 0 0 calc(238rpx + env(safe-area-inset-bottom));
  background: #f7f1ea;
}

.cart-head {
  min-height: 204rpx;
  padding: 0 32rpx 28rpx;
  color: #172033;
  background: #ffffff;
  border-bottom: 0;
  box-shadow: 0 2rpx 0 rgba(126, 76, 49, 0.08);
}

.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 68rpx;
  color: #172033;
  font-size: 30rpx;
  font-weight: 800;
}

.cart-title-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20rpx;
  min-height: 88rpx;
  margin-top: 12rpx;
}

.cart-title-row b,
.cart-title-row span {
  display: block;
}

.cart-title-row > view:first-child {
  min-width: 0;
  text-align: left;
}

.cart-title-row b {
  color: #172033;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.2;
}

.cart-title-row span {
  display: block;
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
}

.mini-pill {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 128rpx;
  min-width: 128rpx;
  height: 68rpx;
  color: #ffffff;
  background: #d7d8dd;
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 900;
  overflow: hidden;
}

.cart-login-guard {
  padding: 24rpx 24rpx 8rpx;
}

.cart-login-guard .empty-action-card {
  display: grid;
  gap: 16rpx;
  padding: 32rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 28rpx;
  box-shadow: 0 12rpx 24rpx rgba(23, 32, 51, 0.08);
}

.cart-login-guard b {
  color: #172033;
  font-size: 34rpx;
}

.cart-login-guard span {
  color: #8c6a58;
  font-size: 26rpx;
}

.cart-page-list {
  padding: 16rpx 12rpx calc(236rpx + env(safe-area-inset-bottom));
  overflow-y: auto;
}

.cart-page-list.preview {
  padding-top: 6rpx;
}

.cart-page-list.preview .cart-bulk-actions button,
.cart-page-list.preview .cart-invalid-group-head button,
.cart-settle.preview .settle-btn {
  opacity: 0.92;
}

.cart-summary-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  min-height: 104rpx;
  margin: 0 20rpx 16rpx;
  padding: 20rpx 20rpx 20rpx 24rpx;
  background: #fffaf5;
  border: 1rpx solid rgba(232, 93, 63, 0.16);
  border-radius: 24rpx;
  box-shadow: 0 10rpx 28rpx rgba(126, 76, 49, 0.05);
}

.cart-station-main {
  display: grid;
  gap: 6rpx;
  min-width: 0;
}

.cart-station-main b,
.cart-station-main span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-station-main b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 950;
}

.cart-station-main span {
  color: #8a6a5c;
  font-size: 25rpx;
  font-weight: 800;
}

.cart-station-switch {
  flex: 0 0 auto;
  min-width: 172rpx;
  min-height: 60rpx;
  padding: 0 24rpx;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 999rpx !important;
  box-shadow: 0 14rpx 28rpx rgba(232, 93, 63, 0.18);
  font-size: 26rpx;
  font-weight: 950;
}

.cart-bulk-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-height: 48rpx;
  margin: 0 20rpx 12rpx;
}

.cart-bulk-actions span {
  display: none;
}

.cart-bulk-actions button {
  min-height: 48rpx;
  padding: 0 16rpx;
  color: #9b6f5e !important;
  background: transparent !important;
  border: 0;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 25rpx;
  font-weight: 800;
}

.cart-swipe-wrap {
  position: relative;
  margin: 16rpx 20rpx;
  overflow: hidden;
  border-radius: 20rpx;
  touch-action: pan-y;
}

.cart-swipe-delete {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 0;
  width: 136rpx;
  min-width: 136rpx;
  min-height: 100%;
  padding: 0;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 20rpx 0 0 20rpx !important;
  box-shadow: none;
  font-size: 30rpx;
  font-weight: 900;
  pointer-events: auto;
}

.cart-item {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 60rpx 132rpx minmax(0, 1fr) 148rpx;
  gap: 16rpx;
  align-items: center;
  min-height: 214rpx;
  padding: 20rpx 16rpx;
  background: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 8rpx 20rpx rgba(126, 76, 49, 0.08);
  transition: transform 0.18s ease;
}

.cart-swipe-wrap.swipe-open .cart-item {
  transform: translateX(136rpx);
}

.cart-item.invalid {
  background: #fffaf7;
  opacity: 0.72;
}

.cart-invalid-group .cart-item.invalid {
  grid-template-columns: 128rpx minmax(0, 1fr) 148rpx;
  min-height: 188rpx;
  padding: 18rpx;
  filter: grayscale(0.08);
}

.cart-invalid-group .cart-item.invalid .cart-img {
  grid-column: 1;
  filter: grayscale(0.55) saturate(0.65);
}

.cart-invalid-group .cart-item.invalid .cart-item-info {
  grid-column: 2;
}

.cart-invalid-group .cart-item.invalid .cart-actions {
  grid-column: 3;
}

.cart-inline-delete {
  display: none;
  pointer-events: none;
}

.check-dot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
  color: #ffffff;
  background: #e85d3f;
  border: 1rpx solid #e85d3f;
  border-radius: 50%;
  font-size: 28rpx;
  font-weight: 900;
}

.check-dot.off {
  color: transparent;
  background: #ffffff;
  border-color: #cfcfcf;
}

.cart-img {
  display: grid;
  width: 132rpx;
  height: 132rpx;
  place-items: center;
  color: #ffffff;
  background: linear-gradient(135deg, #fff4c7 0%, #ffcf55 42%, #f05a37 100%);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  border-radius: 16rpx;
  font-size: 38rpx;
  font-weight: 900;
}

.cart-item-info {
  min-width: 0;
  padding-right: 10rpx;
}

.cart-item-info b {
  display: -webkit-box;
  margin-bottom: 4rpx;
  overflow: hidden;
  color: #172033;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1.25;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  overflow: hidden;
  color: #66716a;
  font-size: 25rpx;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-sale-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6rpx;
  margin-top: 8rpx;
}

.shop-sale-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 40rpx;
  padding: 4rpx 10rpx;
  color: #526257;
  background: #f8faf7;
  border: 1rpx solid #e8eee8;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  white-space: nowrap;
}

.shop-product-tag-row {
  display: flex;
  align-items: center;
  margin-top: 6rpx;
}

.shop-product-date {
  display: inline-flex;
  align-items: center;
  max-width: 100%;
  min-height: 40rpx;
  padding: 4rpx 12rpx;
  overflow: hidden;
  color: #6f5736;
  background: #fbfaf6;
  border: 1rpx solid #ece6da;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-row {
  margin-top: 8rpx;
}

.shop-price {
  color: #e60012;
  font-size: 44rpx;
  font-weight: 900;
}

.cart-actions {
  display: grid;
  justify-items: end;
}

.qty {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10rpx;
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
}

.qty button {
  width: 48rpx;
  min-width: 48rpx;
  height: 48rpx;
  min-height: 48rpx;
  padding: 0;
  color: #c2412d !important;
  background: #ffffff !important;
  border: 1rpx solid #dddddd;
  border-radius: 50%;
  box-shadow: none;
  font-size: 30rpx;
}

.cart-invalid-note {
  grid-column: 3 / -1;
  color: #9a6758;
  font-size: 25rpx;
  font-weight: 800;
}

.cart-invalid-group {
  margin: 20rpx 0 0;
  padding: 20rpx 0 0;
  background: rgba(255, 250, 246, 0.86);
  border: 1rpx solid rgba(126, 76, 49, 0.08);
  border-radius: 24rpx;
}

.cart-invalid-group-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 0 20rpx 8rpx;
}

.cart-invalid-group-head b,
.cart-invalid-group-head span {
  display: block;
}

.cart-invalid-group-head b {
  color: #7b5b4c;
  font-size: 26rpx;
  font-weight: 950;
}

.cart-invalid-group-head span {
  margin-top: 4rpx;
  color: #9b7c6d;
  font-size: 26rpx;
  font-weight: 800;
}

.cart-invalid-group-head button {
  min-height: 48rpx;
  padding: 0 16rpx;
  color: #8a6a5c !important;
  background: #f0e4dc !important;
  border: 0;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 25rpx;
  font-weight: 900;
}

.cart-price-change-note {
  display: inline-flex;
  align-items: center;
  max-width: 176rpx;
  min-height: 40rpx;
  margin-left: 8rpx;
  padding: 4rpx 12rpx;
  overflow: hidden;
  color: #a66210;
  background: #fff1dc;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-section {
  margin: 24rpx 0 8rpx;
  padding: 20rpx;
  background: #ffffff;
  border: 1rpx solid rgba(232, 93, 63, 0.12);
  border-radius: 24rpx;
  box-shadow: 0 14rpx 32rpx rgba(126, 76, 49, 0.07);
}

.recommend-section h3 {
  margin: 0 0 18rpx;
  color: #172033;
  font-size: 32rpx;
  font-weight: 950;
  line-height: 1.2;
}

.recommend-section h3 span {
  margin-left: 12rpx;
  color: #8a6a5c;
  font-size: 26rpx;
  font-weight: 800;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.recommend-card {
  min-width: 0;
  padding: 14rpx;
  background: #fffaf7;
  border: 1rpx solid #f2ded5;
  border-radius: 20rpx;
}

.recommend-img {
  width: 100%;
  height: 152rpx;
  background-color: #f7f1ea;
  background-position: center;
  background-size: cover;
  border-radius: 16rpx;
}

.recommend-card b {
  display: -webkit-box;
  margin: 14rpx 0 6rpx;
  overflow: hidden;
  color: #172033;
  font-size: 26rpx;
  font-weight: 950;
  line-height: 1.24;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.recommend-card p {
  margin: 0 0 12rpx;
  overflow: hidden;
  color: #8a6a5c;
  font-size: 25rpx;
  font-weight: 800;
  line-height: 1.2;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.recommend-price-stack span {
  color: #e60012;
  font-size: 34rpx;
  font-weight: 950;
  line-height: 1;
}

.recommend-bottom button {
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  min-width: 56rpx;
  height: 56rpx;
  min-height: 56rpx;
  padding: 0;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 50% !important;
  box-shadow: 0 10rpx 20rpx rgba(232, 93, 63, 0.2);
}

.recommend-bottom svg {
  width: 34rpx;
  height: 34rpx;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2.6;
}

.cart-settle {
  position: fixed;
  left: 0;
  right: 0;
  bottom: calc(120rpx + env(safe-area-inset-bottom));
  z-index: 45;
  display: grid;
  grid-template-columns: 132rpx minmax(0, 1fr) 214rpx;
  align-items: center;
  gap: 12rpx;
  width: 100%;
  max-width: 390px;
  min-height: 112rpx;
  margin: 0 auto;
  padding: 12rpx 16rpx;
  background: #ffffff;
  border-top: 1rpx solid #e5dfd8;
  box-shadow: none;
}

.settle-btn {
  min-height: 76rpx;
  padding: 0 16rpx;
  color: #ffffff !important;
  background: #e85d3f !important;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 28rpx;
  font-weight: 900;
}

.settle-btn.select-all {
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  font-size: 26rpx;
}

.cart-total-box {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  grid-template-areas:
    "label amount"
    "count count";
  align-items: baseline;
  column-gap: 10rpx;
  row-gap: 6rpx;
  min-width: 0;
}

.cart-total-label {
  grid-area: label;
  display: inline;
  color: #172033;
  font-size: 26rpx;
  font-weight: 950;
  line-height: 1;
  white-space: nowrap;
}

.cart-total-box em {
  grid-area: count;
  display: block;
  overflow: hidden;
  color: #8c6a58;
  font-size: 26rpx;
  font-style: normal;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-total-box b {
  grid-area: amount;
  display: block;
  min-width: 0;
  overflow: hidden;
  color: #e60012;
  font-size: 30rpx;
  line-height: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-page-list {
  padding-left: 8rpx;
  padding-right: 8rpx;
}

.cart-swipe-wrap {
  margin: 12rpx 12rpx;
  border-radius: 18rpx;
}

.cart-item {
  grid-template-columns: 42rpx 112rpx minmax(0, 1fr) 100rpx;
  gap: 10rpx;
  min-height: 168rpx;
  padding: 14rpx 12rpx;
  border-radius: 18rpx;
}

.check-dot {
  width: 38rpx;
  height: 38rpx;
  font-size: 25rpx;
}

.cart-img {
  width: 112rpx;
  height: 112rpx;
  border-radius: 14rpx;
}

.cart-item-info {
  padding-right: 0;
}

.cart-item-info b {
  margin-bottom: 2rpx;
  font-size: 28rpx;
  line-height: 1.18;
  -webkit-line-clamp: 2;
}

.shop-product-spec {
  font-size: 26rpx;
}

.shop-product-tag-row {
  margin-top: 4rpx;
}

.shop-product-date {
  min-height: 40rpx;
  max-width: 236rpx;
  padding: 4rpx 10rpx;
  font-size: 25rpx;
}

.shop-sale-meta {
  gap: 4rpx;
  margin-top: 4rpx;
  overflow: hidden;
}

.shop-sale-meta span {
  min-height: 38rpx;
  max-width: 190rpx;
  padding: 4rpx 10rpx;
  overflow: hidden;
  font-size: 25rpx;
  text-overflow: ellipsis;
}

.price-row {
  margin-top: 4rpx;
}

.shop-price {
  font-size: 34rpx;
}

.qty {
  gap: 6rpx;
  font-size: 26rpx;
}

.qty button {
  width: 38rpx;
  min-width: 38rpx;
  height: 38rpx;
  min-height: 38rpx;
  font-size: 26rpx;
}

.cart-invalid-group .cart-item.invalid {
  grid-template-columns: 108rpx minmax(0, 1fr) 96rpx;
  min-height: 148rpx;
  padding: 14rpx;
}

.cart-invalid-note {
  font-size: 26rpx;
}
</style>
