<template>
  <view class="page">
    <view class="section">
      <text class="title">门店作业</text>
      <text class="subtle">到货通知、退货处理和异常提交。</text>
    </view>
    <view v-for="item in tasks" :key="item.title" class="section task">
      <view>
        <text class="title">{{ item.title }}</text>
        <text class="subtle">{{ item.tip }}</text>
      </view>
      <text class="pill">{{ item.status }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageDeliveryOrders, pageDeliveryStations, pageExceptions, pageReturnHandovers } from '@/api/station';

interface TaskRow {
    title: string;
    tip: string;
    status: string;
}

const tasks = ref<TaskRow[]>([]);

async function loadData() {
    const [deliveryPage, deliveryStationPage, returnPage, exceptionPage] = await Promise.all([
        pageDeliveryOrders({ pageNum: 1, pageSize: 1, stationId: 720001 }),
        pageDeliveryStations({ pageNum: 1, pageSize: 1, stationId: 720001 }),
        pageReturnHandovers({ pageNum: 1, pageSize: 1, stationId: 720001 }),
        pageExceptions({ pageNum: 1, pageSize: 1, stationId: 720001 })
    ]);
    tasks.value = [
        { title: '待到货商品', tip: '配送单', status: `${deliveryPage.total || 0} 单` },
        { title: '用户到货通知', tip: '站点签收', status: `${deliveryStationPage.total || 0} 条` },
        { title: '退货司机取回', tip: '回仓单', status: `${returnPage.total || 0} 单` },
        { title: '缺货异常', tip: '异常记录', status: `${exceptionPage.total || 0} 条` }
    ];
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.task {
  display: flex;
  justify-content: space-between;
}
</style>
