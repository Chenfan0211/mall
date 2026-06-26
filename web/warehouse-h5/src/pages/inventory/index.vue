<template>
  <view class="page">
    <view class="section">
      <text class="title">库存</text>
      <text class="subtle">SKU、批次、库位和锁定数量</text>
    </view>
    <view v-for="item in rows" :key="item.id" class="section inventory-row">
      <view>
        <text class="title">{{ item.skuId }}</text>
        <text class="subtle">{{ item.batchNo }} · {{ item.locationId }}</text>
      </view>
      <view class="counts">
        <text>在库 {{ item.inStockQty }}</text>
        <text>锁定 {{ item.lockedQty }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageInventories, type InventoryDTO } from '@/api/wms';

const rows = ref<InventoryDTO[]>([]);

async function loadData() {
    const page = await pageInventories({ pageNum: 1, pageSize: 20 });
    rows.value = page.list;
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.inventory-row {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.counts {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
}
</style>
