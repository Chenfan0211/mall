<template>
  <view class="page">
    <view class="section">
      <image v-if="detail?.product?.mainImageUrl" class="image-box" :src="detail.product.mainImageUrl" mode="aspectFill" />
      <view v-else class="image-box">商品图</view>
      <text class="title">{{ detail?.product?.productName || '商品详情' }}</text>
      <text class="price">¥{{ selectedSku?.salePrice || '0.00' }}</text>
      <text class="subtle">预计提货日：{{ selectedSku?.deliveryDate || '-' }} · 当前自提点可买</text>
    </view>
    <view class="section">
      <text class="title">规格</text>
      <text
        v-for="item in detail?.skus || []"
        :key="item.publishSkuId"
        class="pill"
        :class="{ danger: item.availableQty <= 0, active: selectedPublishSkuId === item.publishSkuId }"
        @click="selectedPublishSkuId = item.publishSkuId"
      >
        {{ item.skuName }} / 库存 {{ item.availableQty }}
      </text>
    </view>
    <view class="section link-row" @click="openReviews">
      <text>买家评价</text>
      <text class="subtle">{{ detail?.commentCount || 0 }} 条评价</text>
    </view>
    <button class="primary" @click="addCart">加入购物车</button>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { addCartItem, getProductDetail, type UserProductDetailDTO } from '@/api/user';

const productId = ref(0);
const detail = ref<UserProductDetailDTO>();
const selectedPublishSkuId = ref<number | undefined>();
const selectedSku = computed(() => (detail.value?.skus || []).find((item) => item.publishSkuId === selectedPublishSkuId.value));

async function loadDetail() {
    if (!productId.value) {
        return;
    }
    detail.value = await getProductDetail(productId.value, { cityId: 440100, stationId: 720001 });
    selectedPublishSkuId.value = detail.value.skus?.[0]?.publishSkuId;
}

async function addCart() {
    if (!selectedSku.value || !detail.value?.product) {
        return;
    }
    await addCartItem({
        userId: 900001,
        cityId: 440100,
        stationId: 720001,
        periodId: selectedSku.value.periodId,
        productId: detail.value.product.id,
        skuId: selectedSku.value.skuId,
        qty: 1
    });
    uni.switchTab({ url: '/pages/cart/index' });
}

function openReviews() {
    uni.navigateTo({ url: `/pages/product/reviews?productId=${productId.value}` });
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    productId.value = Number(current.options?.id || current.options?.productId || 0);
    void loadDetail();
});
</script>

<style lang="scss" scoped>
.image-box {
  display: grid;
  width: 100%;
  height: 320rpx;
  margin-bottom: 24rpx;
  place-items: center;
  background: #fff2e9;
  border-radius: 24rpx;
}

.price {
  display: block;
  margin: 16rpx 0;
  color: #d94b34;
  font-size: 38rpx;
  font-weight: 700;
}

.danger {
  margin-left: 12rpx;
  color: #d94b34;
  background: #fff1f0;
}

.active {
  color: #d94b34;
  background: #fff2e9;
}

.primary {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}

.link-row {
  display: flex;
  justify-content: space-between;
}
</style>
