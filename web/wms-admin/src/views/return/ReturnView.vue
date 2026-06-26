<template>
  <section class="page-panel">
    <h1 class="page-title">回仓处理</h1>
    <el-table :data="rows" border>
      <el-table-column prop="returnNo" label="回仓单号" width="180" />
      <el-table-column prop="deliveryId" label="配送ID" width="100" />
      <el-table-column prop="stationId" label="自提点ID" width="110" />
      <el-table-column prop="skuId" label="SKU ID" width="100" />
      <el-table-column prop="warehouseId" label="仓库ID" width="100" />
      <el-table-column prop="returnQty" label="退货数" width="100" />
      <el-table-column prop="reasonType" label="原因类型" width="110" />
      <el-table-column prop="driverAccountId" label="司机账号" width="110" />
      <el-table-column prop="supervisorAccountId" label="主管账号" width="110" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageReturnHandovers, type ReturnHandoverDTO } from '@/api/wms';

const rows = ref<ReturnHandoverDTO[]>([]);

async function loadData() {
    const page = await pageReturnHandovers({ pageNum: 1, pageSize: 20 });
    rows.value = page.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
