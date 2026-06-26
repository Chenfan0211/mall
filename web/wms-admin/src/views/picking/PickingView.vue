<template>
  <section class="page-panel">
    <h1 class="page-title">拣货复检</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="波次" name="waves">
        <el-table :data="waves" border>
          <el-table-column prop="waveNo" label="波次号" width="180" />
          <el-table-column prop="warehouseId" label="仓库ID" width="100" />
          <el-table-column prop="lineId" label="线路ID" width="100" />
          <el-table-column prop="deliveryDate" label="配送日期" width="140" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="拣货任务" name="picks">
        <el-table :data="picks" border>
          <el-table-column prop="pickNo" label="拣货单号" width="180" />
          <el-table-column prop="waveId" label="波次ID" width="100" />
          <el-table-column prop="warehouseId" label="仓库ID" width="100" />
          <el-table-column prop="lineId" label="线路ID" width="100" />
          <el-table-column prop="requiredQty" label="应拣数" width="100" />
          <el-table-column prop="actualQty" label="实拣数" width="100" />
          <el-table-column prop="shortageQty" label="缺货数" width="100" />
          <el-table-column prop="assignAccountId" label="作业人" width="110" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pagePickTasks, pageWaveBatches, type PickTaskDTO, type WaveBatchDTO } from '@/api/wms';

const activeTab = ref('waves');
const waves = ref<WaveBatchDTO[]>([]);
const picks = ref<PickTaskDTO[]>([]);

async function loadData() {
    const [wavePage, pickPage] = await Promise.all([
        pageWaveBatches({ pageNum: 1, pageSize: 20 }),
        pagePickTasks({ pageNum: 1, pageSize: 20 })
    ]);
    waves.value = wavePage.list || [];
    picks.value = pickPage.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
