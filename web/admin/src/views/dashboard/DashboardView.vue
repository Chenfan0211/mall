<template>
  <section class="dashboard">
    <div v-for="item in metrics" :key="item.label" class="metric-card">
      <span>{{ item.label }}</span>
      <strong>{{ item.value }}</strong>
      <small>{{ item.tip }}</small>
    </div>
    <div class="page-panel dashboard__wide">
      <h2 class="page-title">待处理事项</h2>
      <el-table :data="todos" border>
        <el-table-column prop="type" label="类型" width="160" />
        <el-table-column prop="title" label="事项" />
        <el-table-column prop="level" label="等级" width="120" />
        <el-table-column prop="owner" label="处理角色" width="160" />
      </el-table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getOperationDashboardSummary, pageOperationTodos } from '@/api/dashboard';

interface MetricCard {
    label: string;
    value: string;
    tip: string;
}

interface TodoRow {
    type: string;
    title: string;
    level: string;
    owner: string;
}

const metrics = ref<MetricCard[]>([
    { label: '待办', value: '0', tip: '加载中' },
    { label: '异常', value: '0', tip: '加载中' },
    { label: '高优先级', value: '0', tip: '加载中' },
    { label: '超时', value: '0', tip: '加载中' }
]);

const todos = ref<TodoRow[]>([]);

function formatCount(value: number | undefined): string {
    return String(value ?? 0);
}

function formatLevel(priority: number | undefined): string {
    if (priority === 0) {
        return 'P0';
    }
    if (priority === 1) {
        return 'P1';
    }
    if (priority === 2) {
        return 'P2';
    }
    return `P${priority ?? 3}`;
}

async function loadData() {
    try {
        const summary = await getOperationDashboardSummary();
        metrics.value = [
            {
                label: '待办',
                value: formatCount(summary.pendingTodoCount),
                tip: `处理中 ${formatCount(summary.processingTodoCount)}`
            },
            {
                label: '异常',
                value: formatCount(summary.pendingExceptionCount),
                tip: `处理中 ${formatCount(summary.processingExceptionCount)}`
            },
            {
                label: '高优先级',
                value: formatCount(summary.highPriorityTodoCount),
                tip: `异常高优先级 ${formatCount(summary.highPriorityExceptionCount)}`
            },
            {
                label: '超时',
                value: formatCount(summary.timeoutTodoCount),
                tip: `已闭环 ${formatCount(summary.closedExceptionCount)}`
            }
        ];
    } catch {
        metrics.value = [
            { label: '待办', value: '0', tip: '加载失败' },
            { label: '异常', value: '0', tip: '加载失败' },
            { label: '高优先级', value: '0', tip: '加载失败' },
            { label: '超时', value: '0', tip: '加载失败' }
        ];
    }

    try {
        const page = await pageOperationTodos({ pageNum: 1, pageSize: 10 });
        todos.value = page.list.map((item) => ({
            type: item.sourceBizType || '待办',
            title: item.title || item.todoNo,
            level: formatLevel(item.priority),
            owner: item.processRoleCode || '未分配'
        }));
    } catch {
        todos.value = [];
    }
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.dashboard {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  padding: 16px;
  background: var(--mall-panel);
  border: 1px solid var(--mall-border);
  border-top: 4px solid var(--mall-primary);
  border-radius: 8px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.04);
}

.metric-card span,
.metric-card small {
  display: block;
  color: #6b7a90;
}

.metric-card strong {
  display: block;
  margin: 10px 0 8px;
  color: var(--mall-primary);
  font-size: 28px;
}

.metric-card:nth-child(2) {
  border-top-color: var(--mall-green);
}

.metric-card:nth-child(2) strong {
  color: var(--mall-green);
}

.metric-card:nth-child(3) {
  border-top-color: var(--mall-yellow);
}

.metric-card:nth-child(3) strong {
  color: var(--mall-yellow);
}

.metric-card:nth-child(4) {
  border-top-color: var(--mall-red);
}

.metric-card:nth-child(4) strong {
  color: var(--mall-red);
}

.dashboard__wide {
  grid-column: 1 / -1;
}
</style>
