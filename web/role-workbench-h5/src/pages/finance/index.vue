<template>
  <view class="page"></view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';

onMounted(() => {
    uni.redirectTo({ url: '/pages/mine/index' });
});
</script>

<style lang="scss" scoped></style>
