<template>
  <view class="page tasks-page">
    <view class="warehouse-header">
      <text class="subtle">角色任务</text>
      <text class="title">收货、上架、拣货、配送</text>
      <view class="task-tabs">
        <text class="active">今日任务</text>
        <text>异常优先</text>
        <text>按角色</text>
      </view>
    </view>

    <view class="task-groups">
      <view v-for="group in groups" :key="group.name" class="task-group-card">
        <view class="group-head">
          <view>
            <text class="title">{{ group.name }}</text>
            <text class="subtle">共 {{ group.items.length }} 条任务</text>
          </view>
          <text class="pill">{{ group.items.length > 0 ? '待处理' : '暂无' }}</text>
        </view>
        <view v-for="item in group.items" :key="item.name" class="warehouse-task-row">
          <view>
            <text class="task-no">{{ item.name }}</text>
            <text class="task-sub">按仓库和角色权限隔离</text>
          </view>
          <text class="warehouse-status">{{ item.status }}</text>
        </view>
        <view v-if="group.items.length === 0" class="empty-line">暂无任务</view>
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
.task-tabs {
  display: flex;
  gap: 10rpx;
  margin-top: 22rpx;
  overflow-x: auto;
}

.task-tabs text {
  flex: 0 0 auto;
  min-height: 52rpx;
  padding: 0 18rpx;
  color: rgba(255, 255, 255, 0.82);
  background: rgba(255, 255, 255, 0.12);
  border: 1rpx solid rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 23rpx;
  font-weight: 900;
  line-height: 52rpx;
}

.task-tabs text.active {
  color: #117b6d;
  background: #ffffff;
}

.task-groups {
  display: grid;
  gap: 18rpx;
}

.task-group-card {
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.97);
  border: 1rpx solid #d9e9e3;
  border-radius: 30rpx;
  box-shadow: 0 12rpx 40rpx rgba(17, 85, 76, 0.07);
}

.group-head,
.warehouse-task-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.group-head {
  align-items: flex-start;
  margin-bottom: 14rpx;
}

.warehouse-task-row {
  align-items: center;
  padding: 20rpx 0;
  border-top: 1rpx solid #d9e9e3;
}

.task-no,
.task-sub {
  display: block;
}

.task-no {
  color: #173b3a;
  font-size: 27rpx;
  font-weight: 900;
}

.task-sub {
  margin-top: 8rpx;
  color: #628078;
  font-size: 23rpx;
}

.warehouse-status {
  min-height: 44rpx;
  padding: 0 16rpx;
  color: #117b6d;
  background: #e7f6ef;
  border: 1rpx solid #cce8dc;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  line-height: 44rpx;
  white-space: nowrap;
}

.empty-line {
  padding-top: 12rpx;
  color: #628078;
  font-size: 24rpx;
}
</style>
