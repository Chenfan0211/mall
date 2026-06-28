<template>
  <view class="page inventory-page">
    <view class="warehouse-header">
      <text class="subtle">库存查询</text>
      <text class="title">SKU、批次、库位和锁定数量</text>
    </view>

    <view class="inventory-list">
      <view v-for="item in rows" :key="item.id" class="inventory-card">
        <view class="inventory-head">
          <view>
            <text class="sku-title">SKU {{ item.skuId }}</text>
            <text class="subtle">{{ item.batchNo }} · 库位 {{ item.locationId }}</text>
          </view>
          <text class="pill">仓 {{ item.warehouseId }}</text>
        </view>
        <view class="inventory-counts">
          <view>
            <text>{{ item.inStockQty }}</text>
            <text>在库</text>
          </view>
          <view>
            <text>{{ item.lockedQty }}</text>
            <text>锁定</text>
          </view>
          <view>
            <text>{{ item.availableQty }}</text>
            <text>可用</text>
          </view>
          <view>
            <text>{{ item.frozenQty }}</text>
            <text>冻结</text>
          </view>
        </view>
      </view>
      <view v-if="rows.length === 0" class="empty-panel">
        <text class="title">暂无库存</text>
        <text class="subtle">当前仓库还没有库存记录。</text>
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
    rows.value = page.list || [];
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.inventory-list {
  display: grid;
  gap: 18rpx;
}

.inventory-card {
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.97);
  border: 1rpx solid #d9e9e3;
  border-radius: 30rpx;
  box-shadow: 0 12rpx 40rpx rgba(17, 85, 76, 0.07);
}

.inventory-head {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.sku-title {
  display: block;
  color: #173b3a;
  font-size: 30rpx;
  font-weight: 900;
}

.inventory-counts {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10rpx;
  margin-top: 20rpx;
}

.inventory-counts view {
  min-height: 94rpx;
  padding: 14rpx 8rpx;
  background: #f7fbf8;
  border: 1rpx solid #d9e9e3;
  border-radius: 18rpx;
  text-align: center;
}

.inventory-counts text {
  display: block;
}

.inventory-counts text:first-child {
  color: #117b6d;
  font-size: 30rpx;
  font-weight: 900;
}

.inventory-counts text:last-child {
  margin-top: 8rpx;
  color: #628078;
  font-size: 21rpx;
}
</style>
