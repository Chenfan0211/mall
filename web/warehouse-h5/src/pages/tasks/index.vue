<template>
  <view class="page">
    <view class="section">
      <text class="title">任务</text>
      <text class="subtle">不同角色看到的任务不同</text>
    </view>
    <view class="section">
      <view v-for="group in groups" :key="group.name" class="group">
        <text class="title">{{ group.name }}</text>
        <view v-for="item in group.items" :key="item.name" class="row">
          <text>{{ item.name }}</text>
          <text class="pill">{{ item.status }}</text>
        </view>
        <text v-if="group.items.length === 0" class="subtle">暂无任务</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageDeliveryOrders, pageInboundOrders, pagePickTasks, pagePutawayTasks } from '@/api/wms';

interface TaskRow {
    name: string;
    status: string;
}

interface TaskGroup {
    name: string;
    items: TaskRow[];
}

const groups = ref<TaskGroup[]>([
    { name: '收货员', items: [] },
    { name: '上架员', items: [] },
    { name: '拣货员', items: [] },
    { name: '司机', items: [] }
]);

async function loadData() {
    const [inboundPage, putawayPage, pickPage, deliveryPage] = await Promise.all([
        pageInboundOrders({ pageNum: 1, pageSize: 5 }),
        pagePutawayTasks({ pageNum: 1, pageSize: 5 }),
        pagePickTasks({ pageNum: 1, pageSize: 5 }),
        pageDeliveryOrders({ pageNum: 1, pageSize: 5 })
    ]);
    groups.value = [
        {
            name: '收货员',
            items: (inboundPage.list || []).map((item) => ({ name: item.inboundNo, status: `状态 ${item.status}` }))
        },
        {
            name: '上架员',
            items: (putawayPage.list || []).map((item) => ({ name: item.putawayNo, status: `状态 ${item.status}` }))
        },
        {
            name: '拣货员',
            items: (pickPage.list || []).map((item) => ({ name: item.pickNo, status: `状态 ${item.status}` }))
        },
        {
            name: '司机',
            items: (deliveryPage.list || []).map((item) => ({ name: item.deliveryNo, status: `状态 ${item.status}` }))
        }
    ];
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.group + .group {
  margin-top: 28rpx;
  padding-top: 28rpx;
  border-top: 1rpx solid #d9e9e3;
}

.row {
  display: flex;
  justify-content: space-between;
  padding: 18rpx 0;
}
</style>
