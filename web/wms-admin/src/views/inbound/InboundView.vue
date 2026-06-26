<template>
  <section class="page-panel">
    <h1 class="page-title">收货入库</h1>
    <el-table :data="rows" border>
      <el-table-column prop="inboundNo" label="入库单号" width="180" />
      <el-table-column prop="sourceNo" label="来源单号" width="160" />
      <el-table-column prop="supplierId" label="供应商" width="120" />
      <el-table-column prop="warehouseId" label="仓库" width="120" />
      <el-table-column prop="status" label="状态" width="120" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageInboundOrders, type InboundOrderDTO } from '@/api/wms';

const rows = ref<InboundOrderDTO[]>([]);

async function loadData() {
    const page = await pageInboundOrders({ pageNum: 1, pageSize: 20 });
    rows.value = page.list;
}

onMounted(() => {
    void loadData();
});
</script>
