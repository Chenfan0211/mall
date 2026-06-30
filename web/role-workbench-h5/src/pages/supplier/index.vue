<template>
  <view class="page"></view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { currentRole } from '@/stores/role';

onMounted(() => {
    const url = currentRole.value === 'supplier'
        ? '/pages/workbench/index'
        : '/pages/status/index?status=noPermission';
    uni.redirectTo({ url });
});
</script>

<style lang="scss" scoped></style>
