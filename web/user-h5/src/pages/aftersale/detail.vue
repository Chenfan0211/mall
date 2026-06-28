<template>
  <view class="page no-tab aftersale-detail-page">
    <view class="section">
      <text class="title">售后详情</text>
      <text class="subtle">{{ detail?.afterSaleNo || '-' }} · {{ statusLabel }}</text>
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

    <view class="fixed-actions">
      <button class="plain" @click="contactStation">联系自提点</button>
      <button v-if="detail?.status === 10" class="danger" @click="cancelVisible = true">撤销申请</button>
      <button v-else class="plain" @click="goOrder">查看订单</button>
    </view>

    <ConfirmDialog
      :visible="cancelVisible"
      title="确认撤销售后"
      content="撤销后售后单状态保留为已撤销，不会删除历史记录。"
      confirm-text="撤销"
      danger
      @cancel="cancelVisible = false"
      @confirm="cancelAfterSale"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserToast from '@/components/UserToast.vue';
import {
    getAfterSale,
    listAfterSaleItems,
    listReturnRecords,
    type UserAfterSaleDTO,
    type UserAfterSaleItemDTO,
    type UserReturnRecordDTO
} from '@/api/user';
import { showUserToast, statusText, useUserState } from '@/stores/userState';

const state = useUserState();
const afterSaleId = ref(0);
const detail = ref<UserAfterSaleDTO>();
const items = ref<UserAfterSaleItemDTO[]>([]);
const returns = ref<UserReturnRecordDTO[]>([]);
const cancelVisible = ref(false);
const statusLabel = computed(() => statusText('afterSale', detail.value?.status));

async function loadDetail() {
    if (!afterSaleId.value) {
        return;
    }
    try {
        detail.value = await getAfterSale(afterSaleId.value, { userId: state.user.id });
        items.value = await listAfterSaleItems(afterSaleId.value, { userId: state.user.id });
        returns.value = await listReturnRecords(afterSaleId.value, { userId: state.user.id });
    } catch (error) {
        detail.value = {
            id: afterSaleId.value,
            afterSaleNo: 'SH202606270001',
            orderId: 880004,
            orderNo: 'DD202606250009',
            userId: state.user.id,
            cityId: state.city.id,
            stationId: state.station.id,
            afterSaleType: 1,
            status: 10,
            applyReason: '商品破损',
            refundAmount: '19.90',
            responsibilityType: 1
        };
        items.value = [
            {
                id: 1,
                afterSaleId: afterSaleId.value,
                orderItemId: 990005,
                skuId: 610005,
                supplierId: 1,
                applyQty: 1,
                approvedQty: 0,
                refundAmount: '19.90'
            }
        ];
        returns.value = [];
        showUserToast('售后详情接口暂不可用，已展示本地状态', 'warn');
    }
}

function contactStation() {
    showUserToast('已复制自提点联系方式，可在真实小程序唤起拨号');
}

function cancelAfterSale() {
    cancelVisible.value = false;
    showUserToast('撤销售后接口暂缺，已保留页面状态', 'warn');
}

function goOrder() {
    if (detail.value?.orderId) {
        uni.navigateTo({ url: `/pages/order/detail?id=${detail.value.orderId}` });
    }
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    afterSaleId.value = Number(current.options?.id || 0);
    void loadDetail();
});
</script>

<style lang="scss" scoped>
.aftersale-detail-page {
  padding-bottom: 160rpx;
}

.row {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.row:last-child {
  border-bottom: 0;
}

.row text:first-child {
  color: #172033;
  font-weight: 900;
}

.row text:last-child {
  max-width: 58%;
  text-align: right;
}

.fixed-actions {
  position: fixed;
  left: 50%;
  bottom: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  width: min(100vw, 390px);
  padding: 18rpx 24rpx 28rpx;
  background: rgba(255, 255, 255, 0.97);
  border-top: 1rpx solid #f0dfd6;
  transform: translateX(-50%);
}
</style>
