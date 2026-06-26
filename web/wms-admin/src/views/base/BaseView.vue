<template>
  <section class="page-panel">
    <h1 class="page-title">基础数据</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="仓库" name="warehouse">
        <el-table :data="warehouses" border>
          <el-table-column prop="warehouseCode" label="仓库编码" width="160" />
          <el-table-column prop="warehouseName" label="仓库名称" />
          <el-table-column prop="cityId" label="城市" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="库区" name="zone">
        <el-table :data="zones" border>
          <el-table-column prop="zoneCode" label="库区编码" width="160" />
          <el-table-column prop="zoneName" label="库区名称" />
          <el-table-column prop="warehouseId" label="仓库" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="库位" name="location">
        <el-table :data="locations" border>
          <el-table-column prop="locationCode" label="库位编码" width="160" />
          <el-table-column prop="locationName" label="库位名称" />
          <el-table-column prop="zoneId" label="库区" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="线路" name="route">
        <el-table :data="lines" border>
          <el-table-column prop="lineCode" label="线路编码" width="160" />
          <el-table-column prop="lineName" label="线路名称" />
          <el-table-column prop="defaultDriverAccountId" label="默认司机" width="140" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="司机档案" name="driver">
        <el-table :data="drivers" border>
          <el-table-column prop="driverNo" label="司机编号" width="160" />
          <el-table-column prop="driverName" label="司机姓名" />
          <el-table-column prop="driverMobile" label="手机号" width="160" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageWarehouses, pageZones, pageLocations, pageLines, pageDrivers, type DriverDTO, type LineDTO, type LocationDTO, type WarehouseDTO, type ZoneDTO } from '@/api/wms';

const activeTab = ref('warehouse');
const warehouses = ref<WarehouseDTO[]>([]);
const zones = ref<ZoneDTO[]>([]);
const locations = ref<LocationDTO[]>([]);
const lines = ref<LineDTO[]>([]);
const drivers = ref<DriverDTO[]>([]);

async function loadData() {
    const [warehousePage, zonePage, locationPage, linePage, driverPage] = await Promise.all([
        pageWarehouses({ pageNum: 1, pageSize: 20 }),
        pageZones({ pageNum: 1, pageSize: 20 }),
        pageLocations({ pageNum: 1, pageSize: 20 }),
        pageLines({ pageNum: 1, pageSize: 20 }),
        pageDrivers({ pageNum: 1, pageSize: 20 })
    ]);
    warehouses.value = warehousePage.list;
    zones.value = zonePage.list;
    locations.value = locationPage.list;
    lines.value = linePage.list;
    drivers.value = driverPage.list;
}

onMounted(() => {
    void loadData();
});
</script>
