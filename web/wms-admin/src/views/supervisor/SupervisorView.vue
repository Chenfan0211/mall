<template>
  <section class="supervisor">
    <div v-for="item in metrics" :key="item.label" class="metric-card">
      <span>{{ item.label }}</span>
      <strong>{{ item.value }}</strong>
      <small>{{ item.tip }}</small>
    </div>
    <div class="page-panel supervisor__wide">
      <h2 class="page-title">今日仓内异常</h2>
      <el-table :data="exceptions" border>
        <el-table-column prop="bizNo" label="单号" width="180" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="reason" label="原因" />
      </el-table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
    pageDeliveryOrders,
    pageInboundOrders,
    pageInventoryLogs,
    pagePickTasks,
    pagePutawayTasks,
    type InventoryLogDTO
} from '@/api/wms';

interface MetricCard {
    label: string;
    value: string;
    tip: string;
}

interface ExceptionRow {
    bizNo: string;
    type: string;
    status: string;
    reason: string;
}

const metrics = ref<MetricCard[]>([
    { label: '待收货', value: '0', tip: '加载中' },
    { label: '待上架', value: '0', tip: '加载中' },
    { label: '待拣货', value: '0', tip: '加载中' },
    { label: '配送中', value: '0', tip: '加载中' }
]);
const exceptions = ref<ExceptionRow[]>([]);

async function loadData() {
    const [inboundPage, putawayPage, pickPage, deliveryPage, logsPage] = await Promise.all([
        pageInboundOrders({ pageNum: 1, pageSize: 1 }),
        pagePutawayTasks({ pageNum: 1, pageSize: 1 }),
        pagePickTasks({ pageNum: 1, pageSize: 1 }),
        pageDeliveryOrders({ pageNum: 1, pageSize: 1 }),
        pageInventoryLogs({ pageNum: 1, pageSize: 5 })
    ]);
    metrics.value = [
        { label: '待收货', value: String(inboundPage.total ?? 0), tip: '入库单' },
        { label: '待上架', value: String(putawayPage.total ?? 0), tip: '上架任务' },
        { label: '待拣货', value: String(pickPage.total ?? 0), tip: '拣货任务' },
        { label: '配送中', value: String(deliveryPage.total ?? 0), tip: '配送任务' }
    ];
    exceptions.value = ((logsPage.list || []) as InventoryLogDTO[]).slice(0, 5).map((item) => ({
        bizNo: item.bizNo,
        type: String(item.changeType),
        status: String(item.changeType),
        reason: item.remark || '无'
    }));
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.supervisor {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  padding: 16px;
  background: var(--wms-panel);
  border: 1px solid var(--wms-border);
  border-top: 4px solid var(--wms-primary);
  border-radius: 8px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.04);
}

.metric-card span,
.metric-card small {
  display: block;
  color: #667985;
}

.metric-card strong {
  display: block;
  margin: 10px 0 8px;
  color: var(--wms-primary);
  font-size: 28px;
}

.metric-card:nth-child(2) {
  border-top-color: var(--wms-green);
}

.metric-card:nth-child(2) strong {
  color: var(--wms-green);
}

.metric-card:nth-child(3) {
  border-top-color: var(--wms-warning);
}

.metric-card:nth-child(3) strong {
  color: var(--wms-warning);
}

.metric-card:nth-child(4) {
  border-top-color: var(--wms-danger);
}

.metric-card:nth-child(4) strong {
  color: var(--wms-danger);
}

.supervisor__wide {
  grid-column: 1 / -1;
}
</style>
