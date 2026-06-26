<template>
  <section class="page-panel">
    <h1 class="page-title">交易履约</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="订单中心" name="orders">
        <el-table :data="orders" border>
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="stationId" label="自提点" width="120" />
          <el-table-column prop="tradeStatus" label="交易状态" width="120" />
          <el-table-column prop="fulfillStatus" label="履约状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="履约跟踪" name="tracking">
        <el-table :data="tracks" border>
          <el-table-column prop="trackNo" label="跟踪单号" width="180" />
          <el-table-column prop="orderId" label="订单ID" width="100" />
          <el-table-column prop="nodeName" label="节点" width="140" />
          <el-table-column prop="bizNo" label="业务单号" width="180" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="remark" label="备注" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="售后中心" name="aftersale">
        <el-table :data="afterSales" border>
          <el-table-column prop="afterSaleNo" label="售后单号" width="180" />
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="stationId" label="自提点" width="120" />
          <el-table-column prop="afterSaleType" label="类型" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="refundAmount" label="退款金额" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="缺货处理" name="shortage">
        <el-table :data="shortageTodos" border>
          <el-table-column prop="todoNo" label="待办单号" width="180" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="priority" label="优先级" width="100" />
          <el-table-column prop="processRoleCode" label="处理角色" width="140" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="异常中心" name="exceptions">
        <el-table :data="exceptions" border>
          <el-table-column prop="exceptionNo" label="异常单号" width="180" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="sourceModule" label="来源模块" width="120" />
          <el-table-column prop="priority" label="优先级" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
    pageAfterSales,
    pageFulfillmentTracks,
    pageOperationExceptions,
    pageOperationOrders,
    pageOperationTodos,
    type AfterSaleDTO,
    type ExceptionRecordDTO,
    type FulfillmentTrackDTO,
    type OrderDTO,
    type TodoDTO
} from '@/api/dashboard';

const activeTab = ref('orders');
const orders = ref<OrderDTO[]>([]);
const tracks = ref<FulfillmentTrackDTO[]>([]);
const afterSales = ref<AfterSaleDTO[]>([]);
const shortageTodos = ref<TodoDTO[]>([]);
const exceptions = ref<ExceptionRecordDTO[]>([]);

async function loadData() {
    const [orderPage, trackPage, afterSalePage, todoPage, exceptionPage] = await Promise.all([
        pageOperationOrders({ pageNum: 1, pageSize: 20 }),
        pageFulfillmentTracks({ pageNum: 1, pageSize: 20 }),
        pageAfterSales({ pageNum: 1, pageSize: 20 }),
        pageOperationTodos({ pageNum: 1, pageSize: 20, sourceBizType: 'SHORTAGE' }),
        pageOperationExceptions({ pageNum: 1, pageSize: 20 })
    ]);
    orders.value = orderPage.list || [];
    tracks.value = trackPage.list || [];
    afterSales.value = afterSalePage.list || [];
    shortageTodos.value = todoPage.list || [];
    exceptions.value = exceptionPage.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
