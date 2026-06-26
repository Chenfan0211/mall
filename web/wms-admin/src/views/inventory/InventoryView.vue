<template>
  <section class="page-panel">
    <h1 class="page-title">库存管理</h1>
    <el-table :data="rows" border>
      <el-table-column prop="skuId" label="SKU" width="120" />
      <el-table-column prop="batchNo" label="批次" width="160" />
      <el-table-column prop="locationId" label="库位" width="120" />
      <el-table-column prop="inStockQty" label="在库数量" width="120" />
      <el-table-column prop="lockedQty" label="锁定数量" width="120" />
    </el-table>
  </section>
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
