<template>
  <section class="page-panel">
    <h1 class="page-title">配送线路</h1>
    <el-table :data="rows" border>
      <el-table-column prop="deliveryNo" label="配送单号" width="180" />
      <el-table-column prop="warehouseId" label="仓库" width="120" />
      <el-table-column prop="lineId" label="线路" width="120" />
      <el-table-column prop="driverAccountId" label="司机" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageDeliveryOrders, type DeliveryOrderDTO } from '@/api/wms';

const rows = ref<DeliveryOrderDTO[]>([]);

async function loadData() {
    const page = await pageDeliveryOrders({ pageNum: 1, pageSize: 20 });
    rows.value = page.list;
}

onMounted(() => {
    void loadData();
});
</script>
