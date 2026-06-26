<template>
  <view class="page">
    <view class="section">
      <text class="title">申请售后</text>
      <text class="subtle">支持仅退款、退货退款和部分退款</text>
      <view class="tag-row">
        <text v-for="item in statusTabs" :key="item.label" class="pill" :class="{ active: status === item.value }" @click="selectStatus(item.value)">
          {{ item.label }}
        </text>
      </view>
    </view>
    <view v-for="item in afterSales" :key="item.id" class="section status-row" @click="openDetail(item.id)">
      <text>{{ item.afterSaleNo }}</text>
      <text class="subtle">订单 {{ item.orderNo }} · 状态 {{ item.status }} · 退款 ¥{{ item.refundAmount }}</text>
    </view>
    <view v-if="afterSales.length === 0" class="section">
      <text class="subtle">暂无售后记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageAfterSales, type UserAfterSaleDTO } from '@/api/user';

const statusTabs = [
    { label: '全部', value: undefined },
    { label: '待审核', value: 1 },
    { label: '退货中', value: 2 },
    { label: '退款中', value: 3 },
    { label: '已完成', value: 4 },
    { label: '驳回', value: 5 }
];
const status = ref<number | undefined>();
const afterSales = ref<UserAfterSaleDTO[]>([]);

async function loadAfterSales() {
    const page = await pageAfterSales({
        pageNum: 1,
        pageSize: 20,
        userId: 900001,
        status: status.value
    });
    afterSales.value = page.list || [];
}

function selectStatus(value?: number) {
    status.value = value;
    void loadAfterSales();
}

function openDetail(id: number) {
    uni.navigateTo({ url: `/pages/aftersale/detail?id=${id}` });
}

onMounted(() => {
    void loadAfterSales();
});
</script>

<style lang="scss" scoped>
.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 18rpx;
}

.status-row {
  margin-bottom: 16rpx;
}

.active {
  color: #d94b34;
  background: #fff2e9;
}
</style>
