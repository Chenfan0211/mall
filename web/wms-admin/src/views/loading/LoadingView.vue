<template>
  <section class="page-panel">
    <h1 class="page-title">装车出库</h1>
    <el-tabs v-model="activeTab" class="tabs">
      <el-tab-pane label="装车单" name="loading">
        <el-table :data="loadings" border>
          <el-table-column prop="loadingNo" label="装车单号" width="180" />
          <el-table-column prop="waveId" label="波次ID" width="100" />
          <el-table-column prop="warehouseId" label="仓库ID" width="100" />
          <el-table-column prop="lineId" label="线路ID" width="100" />
          <el-table-column prop="driverAccountId" label="司机账号" width="110" />
          <el-table-column prop="requiredQty" label="应装数" width="100" />
          <el-table-column prop="actualQty" label="实装数" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="deliveryDate" label="配送日期" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="出库单" name="outbound">
        <el-table :data="outbounds" border>
          <el-table-column prop="outboundNo" label="出库单号" width="180" />
          <el-table-column prop="loadingId" label="装车ID" width="100" />
          <el-table-column prop="warehouseId" label="仓库ID" width="100" />
          <el-table-column prop="lineId" label="线路ID" width="100" />
          <el-table-column prop="outboundQty" label="出库数" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageLoadingOrders, pageOutboundOrders, type LoadingOrderDTO, type OutboundOrderDTO } from '@/api/wms';

const activeTab = ref('loading');
const loadings = ref<LoadingOrderDTO[]>([]);
const outbounds = ref<OutboundOrderDTO[]>([]);

async function loadData() {
    const [loadingPage, outboundPage] = await Promise.all([
        pageLoadingOrders({ pageNum: 1, pageSize: 20 }),
        pageOutboundOrders({ pageNum: 1, pageSize: 20 })
    ]);
    loadings.value = loadingPage.list || [];
    outbounds.value = outboundPage.list || [];
}

onMounted(() => {
    void loadData();
});
</script>

<style scoped>
.tabs {
  margin-top: 16px;
}
</style>
