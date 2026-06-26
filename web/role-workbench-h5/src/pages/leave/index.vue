<template>
  <view class="page">
    <view class="section">
      <text class="title">休假申请</text>
      <text class="subtle">休假会影响选点和未支付订单继续支付</text>
    </view>
    <view v-for="item in leaves" :key="item.id" class="section row">
      <text>{{ item.leaveNo }}</text>
      <text class="pill">{{ item.status }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageLeaves, type StationLeaveDTO } from '@/api/station';

const leaves = ref<StationLeaveDTO[]>([]);

async function loadData() {
    const page = await pageLeaves({ pageNum: 1, pageSize: 20, stationId: 720001 });
    leaves.value = page.list;
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.row {
  display: flex;
  justify-content: space-between;
}
</style>
