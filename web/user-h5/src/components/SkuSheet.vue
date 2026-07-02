<template>
  <view v-if="visible" class="sku-sheet-mask" @click="$emit('close')">
    <view class="sku-sheet-panel" @click.stop>
      <view class="sku-sheet-head">
        <view class="sku-sheet-thumb" :style="backgroundImageStyle(imageUrl)" />
        <view class="sku-sheet-title-block">
          <text class="sku-sheet-title">{{ title || '选择规格' }}</text>
          <text class="sku-sheet-sub">选择规格后加入购物车</text>
        </view>
        <button class="sku-sheet-close" @click="$emit('close')">×</button>
      </view>

      <view class="sku-sheet-label">规格</view>
      <view class="sku-options">
        <button
          v-for="item in skus"
          :key="item.publishSkuId"
          class="sku-sheet-option"
          :class="{ active: item.publishSkuId === selectedId, disabled: item.availableQty <= 0 }"
          :disabled="item.availableQty <= 0"
          @click="$emit('select', item.publishSkuId)"
        >
          <text>{{ item.skuName }}</text>
          <text>{{ item.availableQty <= 0 ? '已售罄' : `¥${item.salePrice}` }}</text>
        </button>
      </view>

      <view class="sku-sheet-footer">
        <text class="sku-sheet-price">¥{{ selectedPrice }}</text>
        <view class="sku-qty-stepper">
          <button :disabled="quantity <= 1" @click="$emit('decrease')">−</button>
          <text>{{ quantity }}</text>
          <button :disabled="!canIncrease" @click="$emit('increase')">＋</button>
        </view>
      </view>

      <button class="sheet-submit" :disabled="!selectedId" @click="$emit('confirm')">加入购物车</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

import { fallbackProductImages } from '@/utils/userFallbackData';
import type { UserProductSkuDTO } from '@/api/user';

const props = defineProps<{
    visible: boolean;
    title?: string;
    imageUrl?: string;
    skus: UserProductSkuDTO[];
    selectedId?: number;
    quantity: number;
}>();

defineEmits<{
    close: [];
    select: [id: number];
    decrease: [];
    increase: [];
    confirm: [];
}>();

const selectedSku = computed(() => props.skus.find((item) => item.publishSkuId === props.selectedId));
const selectedPrice = computed(() => (Number(selectedSku.value?.salePrice || 0) || 0).toFixed(2));
const canIncrease = computed(() => {
    const sku = selectedSku.value;
    if (!sku) {
        return false;
    }
    const maxQty = Math.min(Number(sku.limitQty || 99), Number(sku.availableQty || 0));
    return props.quantity < maxQty;
});

function backgroundImageStyle(url?: string) {
    return {
        backgroundImage: `url("${url || fallbackProductImages[0]}")`
    };
}
</script>

<style lang="scss" scoped>
.sku-sheet-mask {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 0 12rpx 16rpx;
  background: rgba(15, 23, 42, 0.58);
}

.sku-sheet-panel {
  width: min(100%, 390px);
  padding: 28rpx 26rpx 26rpx;
  background: #ffffff;
  border-radius: 28rpx 28rpx 0 0;
  box-shadow: 0 -18rpx 42rpx rgba(15, 23, 42, 0.18);
}

.sku-sheet-head {
  display: grid;
  grid-template-columns: 112rpx minmax(0, 1fr) 56rpx;
  gap: 18rpx;
  align-items: start;
}

.sku-sheet-thumb {
  width: 112rpx;
  height: 112rpx;
  background-color: #f7f1ea;
  background-position: center;
  background-size: cover;
  border-radius: 8rpx;
}

.sku-sheet-title-block {
  min-width: 0;
}

.sku-sheet-title,
.sku-sheet-sub {
  display: block;
}

.sku-sheet-title {
  overflow: hidden;
  color: #071a36;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sku-sheet-sub {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 24rpx;
  line-height: 1.35;
}

.sku-sheet-close {
  display: grid;
  width: 56rpx;
  min-width: 56rpx;
  height: 56rpx;
  min-height: 56rpx;
  margin: 0;
  padding: 0;
  color: #777777 !important;
  background: transparent !important;
  border: 0;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 42rpx;
  font-weight: 900;
  line-height: 1;
  place-items: center;
}

.sku-sheet-close::after,
.sku-sheet-option::after,
.sku-qty-stepper button::after,
.sheet-submit::after {
  display: none;
}

.sku-sheet-label {
  margin-top: 28rpx;
  color: #172033;
  font-size: 27rpx;
  font-weight: 900;
}

.sku-options {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.sku-sheet-option {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  min-height: 58rpx;
  padding: 0 22rpx;
  color: #071a36 !important;
  background: #fff7f1 !important;
  border: 1rpx solid #ff6a4e;
  border-radius: 8rpx;
  box-shadow: none;
  font-size: 25rpx;
  font-weight: 900;
  text-align: left;
}

.sku-sheet-option.active {
  background: #fff0e9 !important;
  border-color: #e85d3f;
}

.sku-sheet-option.disabled {
  color: #9ca3af !important;
  background: #f3f4f6 !important;
  border-color: #e5e7eb;
}

.sku-sheet-option text:last-child {
  color: #e60012;
  font-size: 24rpx;
}

.sku-sheet-option.disabled text:last-child {
  color: #8c96a6;
}

.sku-sheet-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 26rpx;
}

.sku-sheet-price {
  color: #e60012;
  font-size: 28rpx;
  font-weight: 950;
}

.sku-qty-stepper {
  display: inline-flex;
  align-items: center;
  gap: 18rpx;
}

.sku-qty-stepper button {
  display: grid;
  width: 52rpx;
  min-width: 52rpx;
  height: 52rpx;
  min-height: 52rpx;
  margin: 0;
  padding: 0;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1;
  place-items: center;
}

.sku-qty-stepper button:disabled {
  color: #667085 !important;
  background: #f2f4f7 !important;
  border: 1rpx solid #d0d5dd;
}

.sku-qty-stepper text {
  min-width: 28rpx;
  color: #071a36;
  font-size: 30rpx;
  font-weight: 900;
  text-align: center;
}

.sheet-submit {
  width: 100%;
  min-height: 82rpx;
  margin-top: 18rpx;
  color: #ffffff !important;
  background: #e85d3f !important;
  border: 0;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 29rpx;
  font-weight: 900;
}

.sheet-submit:disabled {
  opacity: 0.5;
}
</style>
