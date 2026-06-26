<template>
  <section class="page-panel">
    <h1 class="page-title">商品与团期</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="商品中心" name="products">
        <el-table :data="products" border>
          <el-table-column prop="productNo" label="商品编号" width="180" />
          <el-table-column prop="productName" label="商品" />
          <el-table-column prop="supplierId" label="供应商" width="120" />
          <el-table-column prop="auditStatus" label="审核状态" width="120" />
          <el-table-column prop="saleStatus" label="销售状态" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="团期管理" name="periods">
        <el-table :data="periods" border>
          <el-table-column prop="periodNo" label="团期编号" width="180" />
          <el-table-column prop="periodName" label="团期名称" />
          <el-table-column prop="cityId" label="城市" width="100" />
          <el-table-column prop="warehouseId" label="采购仓" width="120" />
          <el-table-column prop="deliveryDate" label="配送日期" width="140" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="类目中心" name="categories">
        <el-table :data="categories" border>
          <el-table-column prop="categoryCode" label="类目编码" width="160" />
          <el-table-column prop="categoryName" label="类目名称" />
          <el-table-column prop="categoryType" label="类型" width="100" />
          <el-table-column prop="sortNo" label="排序" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="榜单标签" name="rankTags">
        <el-table :data="products" border>
          <el-table-column prop="productName" label="榜单商品" />
          <el-table-column prop="productNo" label="商品编号" width="180" />
          <el-table-column prop="supplierId" label="供应商" width="120" />
          <el-table-column prop="saleStatus" label="销售状态" width="120" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
    pageCategories,
    pageOperationProducts,
    pagePublishPeriods,
    type CategoryDTO,
    type ProductDTO,
    type PublishPeriodDTO
} from '@/api/dashboard';

const activeTab = ref('products');
const products = ref<ProductDTO[]>([]);
const periods = ref<PublishPeriodDTO[]>([]);
const categories = ref<CategoryDTO[]>([]);

async function loadData() {
    const [productPage, periodPage, categoryList] = await Promise.all([
        pageOperationProducts({ pageNum: 1, pageSize: 20 }),
        pagePublishPeriods({ pageNum: 1, pageSize: 20 }),
        pageCategories({ pageNum: 1, pageSize: 20 })
    ]);
    products.value = productPage.list || [];
    periods.value = periodPage.list || [];
    categories.value = categoryList || [];
}

onMounted(() => {
    void loadData();
});
</script>
