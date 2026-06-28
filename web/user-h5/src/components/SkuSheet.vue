<template>
  <view v-if="visible" class="sheet-mask" @click="$emit('close')">
    <view class="sheet-panel" @click.stop>
      <view class="sheet-head">
        <view class="sheet-thumb">鲜</view>
        <view>
          <text class="sheet-title">{{ title || '选择规格' }}</text>
          <text class="sheet-sub">选择规格后加入购物车</text>
        </view>
        <button class="sheet-close" @click="$emit('close')">×</button>
      </view>
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
          <text>{{ item.availableQty <= 0 ? '已售罄' : `¥${item.salePrice} · 剩余${item.availableQty}` }}</text>
        </button>
      </view>
      <button class="sheet-submit" :disabled="!selectedId" @click="$emit('confirm')">加入购物车</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { UserProductSkuDTO } from '@/api/user';

defineProps<{
    visible: boolean;
    title?: string;
    skus: UserProductSkuDTO[];
    selectedId?: number;
}>();

defineEmits<{
    close: [];
    select: [id: number];
    confirm: [];
}>();
</script>
