<template>
  <view class="product-card" :class="{ soldout }" @click="$emit('open')">
    <button class="product-fav" :class="{ active: favorite }" @click.stop="$emit('favorite')">
      {{ favorite ? '已藏' : '收藏' }}
    </button>
    <view class="product-media">
      <image v-if="product.mainImageUrl" class="product-img" :src="product.mainImageUrl" mode="aspectFill" />
      <view v-else class="product-img fallback">鲜</view>
      <text class="pickup-pill">提货 {{ pickupText }}</text>
    </view>
    <view class="product-info">
      <text class="product-name">{{ product.productName }}</text>
      <text class="product-spec">{{ product.skuName || product.saleSpecText || '默认规格' }}</text>
      <view class="product-meta">
        <text>已售 {{ product.soldQty || 0 }}</text>
        <text :class="{ danger: soldout }">{{ soldout ? '已售罄' : `剩余 ${product.availableQty}` }}</text>
      </view>
      <view class="product-bottom">
        <view>
          <text class="product-price">¥{{ product.salePrice }}</text>
        </view>
        <button v-if="soldout" class="plain add-btn" @click.stop="$emit('notice')">到货提醒</button>
        <button v-else class="add-btn" @click.stop="$emit('add')">+</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

import type { UserProductCardDTO } from '@/api/user';

const props = defineProps<{
    product: UserProductCardDTO;
    favorite?: boolean;
}>();

defineEmits<{
    open: [];
    add: [];
    notice: [];
    favorite: [];
}>();

const soldout = computed(() => Number(props.product.availableQty || 0) <= 0);
const pickupText = computed(() => props.product.deliveryDate || '待定');
</script>
