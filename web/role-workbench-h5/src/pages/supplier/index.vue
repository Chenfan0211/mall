<template>
  <view class="page">
    <view class="section hero">
      <text class="title">供应商工作台</text>
      <text class="subtle">展示当前供应商采购、到仓、商品和货款。</text>
    </view>
    <view v-for="item in tasks" :key="item.title" class="section task-row">
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
import { getSupplierWorkbenchSummary } from '@/api/station';

interface TaskRow {
    title: string;
    tip: string;
    status: string;
}

const tasks = ref<TaskRow[]>([]);

async function loadData() {
    const summary = await getSupplierWorkbenchSummary({ supplierId: 610001 });
    tasks.value = [
        { title: '采购确认', tip: `待审核 ${summary.waitAuditPurchaseCount}`, status: `${summary.waitDeliveryPurchaseCount} 待发货` },
        { title: '到仓凭证', tip: `配送中 ${summary.deliveringOrderCount}`, status: `${summary.arrivedOrderCount} 已到仓` },
        { title: '货款结算', tip: `采购 ${summary.purchaseQty}`, status: `实收 ${summary.receivedQty}` },
        { title: '差异处理', tip: `差异数量 ${summary.diffQty}`, status: summary.diffQty > 0 ? '需处理' : '正常' }
    ];
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.hero {
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
  border: 0;
  box-shadow: 0 16rpx 32rpx rgba(217, 75, 52, 0.22);
}

.hero .title,
.hero .subtle {
  color: #ffffff;
}

.task-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
