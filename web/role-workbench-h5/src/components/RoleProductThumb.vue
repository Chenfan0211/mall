<template>
  <view class="role-product-thumb" aria-hidden="true">
    <image :src="displaySrc" mode="aspectFill" @error="useFallback = true" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';

type ThumbVariant = 'product' | 'delivery' | 'return' | 'service';

const props = defineProps<{
  label?: string;
  src?: string;
  variant?: ThumbVariant;
}>();

const useFallback = ref(false);
const defaultImages: Record<ThumbVariant, string> = {
  product: '/static/role-images/default-product.svg',
  delivery: '/static/role-images/default-delivery.svg',
  return: '/static/role-images/default-return.svg',
  service: '/static/role-images/default-service.svg'
};

const fallbackSrc = computed(() => defaultImages[props.variant || 'product']);
const displaySrc = computed(() => {
  const image = String(props.src || '').trim();
  return image && !useFallback.value ? image : fallbackSrc.value;
});

watch(() => props.src, () => {
  useFallback.value = false;
});
</script>

<style scoped>
.role-product-thumb {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: currentColor;
  overflow: hidden;
}

.role-product-thumb image {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
