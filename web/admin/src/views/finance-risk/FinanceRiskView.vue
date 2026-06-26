<template>
  <section class="page-panel">
    <h1 class="page-title">财务风控</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="提现审核" name="withdraw">
        <el-table :data="withdrawals" border>
          <el-table-column prop="withdrawNo" label="提现单号" width="180" />
          <el-table-column prop="subjectId" label="主体ID" width="120" />
          <el-table-column prop="withdrawAmount" label="金额" width="120" />
          <el-table-column prop="auditStatus" label="审核状态" width="120" />
          <el-table-column prop="payStatus" label="打款状态" width="120" />
          <el-table-column prop="auditRemark" label="风控提示" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="资金流水" name="flow">
        <el-table :data="flows" border>
          <el-table-column prop="flowNo" label="流水号" width="180" />
          <el-table-column prop="accountId" label="账户ID" width="120" />
          <el-table-column prop="amount" label="金额" width="120" />
          <el-table-column prop="flowType" label="类型" width="100" />
          <el-table-column prop="bizType" label="业务类型" width="140" />
          <el-table-column prop="bizNo" label="业务单号" width="180" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageFinanceFlows, pageWithdraws, type FinanceAccountFlowDTO, type WithdrawApplyDTO } from '@/api/dashboard';

const activeTab = ref('withdraw');
const withdrawals = ref<WithdrawApplyDTO[]>([]);
const flows = ref<FinanceAccountFlowDTO[]>([]);

async function loadData() {
    const [withdrawPage, flowPage] = await Promise.all([
        pageWithdraws({ pageNum: 1, pageSize: 20 }),
        pageFinanceFlows({ pageNum: 1, pageSize: 20 })
    ]);
    withdrawals.value = withdrawPage.list || [];
    flows.value = flowPage.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
