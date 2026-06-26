<template>
  <view class="page">
    <view class="section">
      <text class="title">自提点申请</text>
      <text class="subtle">当前后端已支持自提点查询，提交申请接口待补。</text>
    </view>
    <view v-for="item in stations" :key="item.id" class="section station-row">
      <view>
        <text class="title">{{ item.stationName }}</text>
        <text class="subtle">{{ item.address }}</text>
      </view>
      <text class="pill">状态 {{ item.status }}</text>
    </view>
    <view v-if="stations.length === 0" class="section">
      <text class="subtle">暂无可申请自提点</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageStations, type UserStationDTO } from '@/api/user';

const stations = ref<UserStationDTO[]>([]);

async function loadStations() {
    const page = await pageStations({ pageNum: 1, pageSize: 20, cityId: 440100 });
    stations.value = page.list || [];
}

onMounted(() => {
    void loadStations();
});
</script>

<style lang="scss" scoped>
.station-row {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}
</style>
