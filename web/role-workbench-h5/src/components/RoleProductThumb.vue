<template>
  <view class="role-product-thumb" :class="{ 'has-label': thumbLabel }" aria-hidden="true">
    <text v-if="thumbLabel">{{ thumbLabel }}</text>
    <svg v-else viewBox="0 0 48 48" fill="none">
      <rect x="8" y="11" width="32" height="28" rx="7" />
      <path d="M16 11c0-4.4 3.6-8 8-8s8 3.6 8 8" />
      <path d="M15.5 28.5c3.8-6.6 7.2-8.5 10.2-5.6 2 2 3.3 2.9 4.8 1.8 1.1-.8 2.3-2.2 3.7-4.2" />
      <path d="M15 34h18" />
      <circle cx="17" cy="20" r="2.4" />
    </svg>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  label?: string;
}>();

const thumbLabel = computed(() => {
  const value = String(props.label || '').trim();
  if (!value) return '';
  const char = Array.from(value).find((item) => /[A-Za-z0-9\u4e00-\u9fff]/u.test(item)) || value[0];
  return /[A-Za-z]/.test(char) ? char.toUpperCase() : char;
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
}

.role-product-thumb.has-label {
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1;
}

.role-product-thumb svg {
  width: 58%;
  height: 58%;
}

.role-product-thumb rect,
.role-product-thumb path,
.role-product-thumb circle {
  stroke: currentColor;
  stroke-width: 2.8;
  stroke-linecap: round;
  stroke-linejoin: round;
  vector-effect: non-scaling-stroke;
}
</style>
