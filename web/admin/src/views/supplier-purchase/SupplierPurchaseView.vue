<template>
  <section class="page-panel">
    <h1 class="page-title">供应商/采购</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="采购单" name="purchase">
        <el-table :data="purchases" border>
          <el-table-column prop="purchaseNo" label="采购单" width="180" />
          <el-table-column prop="supplierId" label="供应商ID" width="120" />
          <el-table-column prop="warehouseId" label="采购仓" width="120" />
          <el-table-column prop="deliveryDate" label="送货日期" width="140" />
          <el-table-column prop="purchaseStatus" label="采购状态" width="120" />
          <el-table-column prop="auditStatus" label="审核状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="送货单" name="delivery">
        <el-table :data="deliveries" border>
          <el-table-column prop="deliveryNo" label="送货单" width="180" />
          <el-table-column prop="supplierId" label="供应商ID" width="120" />
          <el-table-column prop="purchaseId" label="采购单ID" width="120" />
          <el-table-column prop="warehouseId" label="到仓" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="到货追踪" name="arrival">
        <el-table :data="purchases" border>
          <el-table-column prop="purchaseNo" label="采购单" width="180" />
          <el-table-column prop="expectedArrivalTime" label="预计到货" />
          <el-table-column prop="deliveryDate" label="送货日期" width="140" />
          <el-table-column prop="purchaseStatus" label="采购状态" width="120" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
    pagePurchases,
    pageSupplierDeliveries,
    type DeliveryOrderDTO,
    type PurchaseOrderDTO
} from '@/api/dashboard';

const activeTab = ref('purchase');
const purchases = ref<PurchaseOrderDTO[]>([]);
const deliveries = ref<DeliveryOrderDTO[]>([]);

async function loadData() {
    const [purchasePage, deliveryPage] = await Promise.all([
        pagePurchases({ pageNum: 1, pageSize: 20 }),
        pageSupplierDeliveries({ pageNum: 1, pageSize: 20 })
    ]);
    purchases.value = purchasePage.list || [];
    deliveries.value = deliveryPage.list || [];
}

onMounted(() => {
    void loadData();
});
</script>
