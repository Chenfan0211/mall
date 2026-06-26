<template>
  <section class="page-panel">
    <h1 class="page-title">库存流水</h1>
    <el-table :data="rows" border>
      <el-table-column prop="bizNo" label="来源单号" width="180" />
      <el-table-column prop="skuId" label="SKU" width="120" />
      <el-table-column prop="changeType" label="类型" width="120" />
      <el-table-column prop="changeQty" label="数量" width="100" />
      <el-table-column prop="operatorAccountId" label="操作人" width="120" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageInventoryLogs, type InventoryLogDTO } from '@/api/wms';

const rows = ref<InventoryLogDTO[]>([]);

async function loadData() {
    const page = await pageInventoryLogs({ pageNum: 1, pageSize: 20 });
    rows.value = page.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
