<template>
  <section class="page-panel">
    <h1 class="page-title">上架作业</h1>
    <el-table :data="rows" border>
      <el-table-column prop="putawayNo" label="上架单号" width="180" />
      <el-table-column prop="inboundId" label="入库单ID" width="110" />
      <el-table-column prop="warehouseId" label="仓库ID" width="100" />
      <el-table-column prop="skuId" label="SKU ID" width="100" />
      <el-table-column prop="targetLocationId" label="目标库位" width="110" />
      <el-table-column prop="putawayQty" label="应上架数" width="110" />
      <el-table-column prop="actualQty" label="实上架数" width="110" />
      <el-table-column prop="assignAccountId" label="作业人" width="110" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pagePutawayTasks, type PutawayTaskDTO } from '@/api/wms';

const rows = ref<PutawayTaskDTO[]>([]);

async function loadData() {
    const page = await pagePutawayTasks({ pageNum: 1, pageSize: 20 });
    rows.value = page.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
