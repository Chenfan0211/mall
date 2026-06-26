<template>
  <section class="page-panel">
    <h1 class="page-title">自提点/团长</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="自提点管理" name="stations">
        <el-table :data="stations" border>
          <el-table-column prop="stationName" label="自提点" />
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="cityId" label="城市ID" width="120" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="status" label="状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="休假管理" name="leave">
        <el-table :data="leaves" border>
          <el-table-column prop="leaveNo" label="休假单号" width="180" />
          <el-table-column prop="stationId" label="自提点ID" width="120" />
          <el-table-column prop="leaderId" label="团长ID" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="团长管理" name="leaders">
        <el-table :data="leaders" border>
          <el-table-column prop="leaderNo" label="团长编号" width="180" />
          <el-table-column prop="leaderName" label="团长" />
          <el-table-column prop="stationId" label="自提点ID" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="佣金数据" name="commission">
        <el-table :data="accounts" border>
          <el-table-column prop="accountNo" label="账户号" width="180" />
          <el-table-column prop="subjectId" label="主体ID" width="120" />
          <el-table-column prop="availableAmount" label="可用金额" width="120" />
          <el-table-column prop="frozenAmount" label="冻结金额" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
    pageLeaders,
    pageFinanceAccounts,
    pageStationLeaves,
    pageStations,
    type FinanceAccountDTO,
    type LeaderDTO,
    type StationDTO,
    type StationLeaveDTO
} from '@/api/dashboard';

const activeTab = ref('stations');
const stations = ref<StationDTO[]>([]);
const leaves = ref<StationLeaveDTO[]>([]);
const leaders = ref<LeaderDTO[]>([]);
const accounts = ref<FinanceAccountDTO[]>([]);

async function loadData() {
    const [stationPage, leavePage, leaderPage, accountPage] = await Promise.all([
        pageStations({ pageNum: 1, pageSize: 20 }),
        pageStationLeaves({ pageNum: 1, pageSize: 20 }),
        pageLeaders({ pageNum: 1, pageSize: 20 }),
        pageFinanceAccounts({ pageNum: 1, pageSize: 20, subjectType: 2 })
    ]);
    stations.value = stationPage.list || [];
    leaves.value = leavePage.list || [];
    leaders.value = leaderPage.list || [];
    accounts.value = accountPage.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
