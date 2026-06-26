<template>
  <view class="page">
    <view class="section">
      <text class="title">售后详情</text>
      <text class="subtle">{{ detail?.afterSaleNo || '-' }} · 状态 {{ detail?.status || '-' }}</text>
    </view>
    <view class="section">
      <view class="row">
        <text>订单号</text>
        <text class="subtle">{{ detail?.orderNo || '-' }}</text>
      </view>
      <view class="row">
        <text>退款金额</text>
        <text class="subtle">¥{{ detail?.refundAmount || '0.00' }}</text>
      </view>
      <view class="row">
        <text>申请原因</text>
        <text class="subtle">{{ detail?.applyReason || '-' }}</text>
      </view>
    </view>
    <view class="section">
      <text class="title">售后商品</text>
      <view v-for="item in items" :key="item.id" class="row">
        <text>SKU {{ item.skuId }}</text>
        <text class="subtle">申请 {{ item.applyQty }} / 通过 {{ item.approvedQty }}</text>
      </view>
    </view>
    <view class="section">
      <text class="title">退货记录</text>
      <view v-for="item in returns" :key="item.id" class="row">
        <text>{{ item.returnNo }}</text>
        <text class="subtle">数量 {{ item.returnQty }} · 状态 {{ item.returnStatus }}</text>
      </view>
      <text v-if="returns.length === 0" class="subtle">暂无退货记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
    getAfterSale,
    listAfterSaleItems,
    listReturnRecords,
    type UserAfterSaleDTO,
    type UserAfterSaleItemDTO,
    type UserReturnRecordDTO
} from '@/api/user';

const afterSaleId = ref(0);
const detail = ref<UserAfterSaleDTO>();
const items = ref<UserAfterSaleItemDTO[]>([]);
const returns = ref<UserReturnRecordDTO[]>([]);

async function loadDetail() {
    if (!afterSaleId.value) {
        return;
    }
    detail.value = await getAfterSale(afterSaleId.value, { userId: 900001 });
    items.value = await listAfterSaleItems(afterSaleId.value, { userId: 900001 });
    returns.value = await listReturnRecords(afterSaleId.value, { userId: 900001 });
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    afterSaleId.value = Number(current.options?.id || 0);
    void loadDetail();
});
</script>

<style lang="scss" scoped>
.row {
  display: flex;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}
</style>
