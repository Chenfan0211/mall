<template>
  <view class="page warehouse-page">
    <view class="warehouse-work-head hero">
      <view class="warehouse-brand-row">
        <view class="warehouse-brand">
          <text>仓配工作台</text>
          <text>WMS MOBILE</text>
        </view>
        <text class="warehouse-badge">仓库主管</text>
      </view>
      <view class="warehouse-work-title">
        <view>
          <text class="title">广州中心仓</text>
          <text class="subtle">收货、上架、拣货、配送按仓库隔离</text>
        </view>
        <text class="warehouse-chip">实时</text>
      </view>
    </view>

    <view class="warehouse-main">
      <view class="warehouse-kpis">
        <view v-for="item in metrics" :key="item.label" class="metric warehouse-kpi">
          <text class="title">{{ item.value }}</text>
          <text class="subtle">{{ item.label }}</text>
        </view>
      </view>

      <view class="section warehouse-section">
        <view class="warehouse-section-head">
          <view>
            <text class="title">仓内作业</text>
            <text class="subtle">按仓库和库区隔离任务</text>
          </view>
          <text class="pill">今日</text>
        </view>
        <view class="warehouse-quick-grid">
          <button v-for="item in shortcuts" :key="item.text" class="warehouse-quick-btn" @click="open(item.url)">
            <text>{{ item.text }}</text>
            <text>{{ item.desc }}</text>
          </button>
        </view>
      </view>

      <view class="section warehouse-section">
        <view class="warehouse-section-head">
          <view>
            <text class="title">重点任务</text>
            <text class="subtle">收货、拣货、配送进度</text>
          </view>
        </view>
        <view class="warehouse-task-list">
          <view v-for="item in shortcuts" :key="item.tip" class="warehouse-task" @click="open(item.url)">
            <view>
              <text class="task-title">{{ item.text }}</text>
              <text class="task-tip">{{ item.desc }}</text>
            </view>
            <text class="pill">{{ item.tip }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageInventories, pageInboundOrders } from '@/api/wms';

const metrics = ref([
    { label: '待收货', value: 0 },
    { label: '待上架', value: 0 },
    { label: '待拣货', value: 0 }
]);

const shortcuts = [
    { text: '收货任务', desc: '采购到仓、质检、上架', tip: '差异 0', url: '/pages/tasks/index' },
    { text: '库存查看', desc: 'SKU、库位、批次库存', tip: '实时', url: '/pages/inventory/index' },
    { text: '我的角色', desc: '当前身份和授权仓库', tip: '主管', url: '/pages/mine/index' },
    { text: '角色任务', desc: '收货、拣货、装车视角', tip: '切换', url: '/pages/roles/index' }
];

async function loadData() {
    const [inboundPage, inventoryPage] = await Promise.all([
        pageInboundOrders({ pageNum: 1, pageSize: 20 }),
        pageInventories({ pageNum: 1, pageSize: 20 })
    ]);
    metrics.value = [
        { label: '待收货', value: inboundPage.total || 0 },
        { label: '待上架', value: inventoryPage.total || 0 },
        { label: '待拣货', value: inventoryPage.total || 0 }
    ];
}

function open(url: string) {
    if (url === '/pages/tasks/index' || url === '/pages/inventory/index' || url === '/pages/mine/index') {
        uni.switchTab({ url });
        return;
    }
    uni.navigateTo({ url });
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.warehouse-page {
  padding: 0 0 164rpx;
}

.warehouse-work-head {
  min-height: 240rpx;
  padding: 16rpx 28rpx 28rpx;
}

.warehouse-brand-row {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 14rpx;
}

.warehouse-brand {
  display: grid;
  gap: 8rpx;
  min-width: 0;
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.15;
}

.warehouse-brand text:last-child {
  font-size: 22rpx;
  opacity: 0.9;
}

.warehouse-badge,
.warehouse-chip {
  display: inline-flex;
  align-items: center;
  min-height: 56rpx;
  padding: 0 22rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 800;
  white-space: nowrap;
}

.warehouse-work-title {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 30rpx;
}

.warehouse-work-title .title {
  color: #ffffff;
  font-size: 44rpx;
}

.warehouse-work-title .subtle {
  color: rgba(255, 255, 255, 0.9);
}

.warehouse-main {
  padding: 24rpx;
}

.warehouse-kpis {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18rpx;
  margin-bottom: 24rpx;
}

.warehouse-kpi {
  margin: 0;
  min-width: 0;
}

.warehouse-section {
  padding: 28rpx;
}

.warehouse-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.warehouse-quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}

.warehouse-quick-btn {
  display: grid;
  gap: 8rpx;
  align-content: start;
  min-height: 128rpx;
  padding: 24rpx;
  color: #245a54 !important;
  background: linear-gradient(135deg, #edf8f4 0%, #fbfdf8 100%) !important;
  border-radius: 24rpx !important;
  box-shadow: inset 0 0 0 1rpx #d3e7df;
  text-align: left;
}

.warehouse-quick-btn text:first-child {
  color: #173b3a;
  font-size: 28rpx;
  font-weight: 900;
}

.warehouse-quick-btn text:last-child {
  color: #628078;
  font-size: 24rpx;
  line-height: 1.45;
}

.warehouse-task-list {
  display: grid;
  gap: 20rpx;
}

.warehouse-task {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 24rpx;
  background: #f7fbf8;
  border: 1rpx solid #d9e9e3;
  border-radius: 24rpx;
}

.task-title,
.task-tip {
  display: block;
}

.task-title {
  color: #173b3a;
  font-size: 28rpx;
  font-weight: 900;
}

.task-tip {
  margin-top: 8rpx;
  color: #628078;
  font-size: 24rpx;
  line-height: 1.45;
}
</style>
