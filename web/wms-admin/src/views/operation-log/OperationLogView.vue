<template>
  <section class="page-panel">
    <h1 class="page-title">操作日志</h1>
    <el-table :data="rows" border>
      <el-table-column prop="logNo" label="日志号" width="180" />
      <el-table-column prop="roleCode" label="角色" width="120" />
      <el-table-column prop="moduleCode" label="模块" width="120" />
      <el-table-column prop="actionCode" label="动作" width="120" />
      <el-table-column prop="resultStatus" label="结果" width="100" />
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageOperationLogs, type WmsOperationLogDTO } from '@/api/wms';

const rows = ref<WmsOperationLogDTO[]>([]);

async function loadData() {
    const page = await pageOperationLogs({ pageNum: 1, pageSize: 20 });
    rows.value = page.list;
}

onMounted(() => {
    void loadData();
});
</script>
