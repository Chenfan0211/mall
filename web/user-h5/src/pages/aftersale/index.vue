<template>
  <view class="page no-tab aftersale-page">
    <view class="section">
      <text class="title">售后记录</text>
      <text class="subtle">支持仅退款、退货退款和部分退款</text>
      <view class="tag-row">
        <text v-for="item in statusTabs" :key="item.label" class="pill" :class="{ active: status === item.value }" @click="selectStatus(item.value)">
          {{ item.label }}
        </text>
      </view>
    </view>
    <view v-for="item in afterSales" :key="item.id" class="section status-row">
      <view class="status-head" @click="openDetail(item.id)">
        <view>
          <text>{{ item.afterSaleNo }}</text>
          <text class="subtle">订单 {{ item.orderNo }} · 退款 ¥{{ item.refundAmount }}</text>
        </view>
        <text class="pill">{{ statusLabel(item.status) }}</text>
      </view>
      <view class="action-row">
        <button class="plain small" @click="openDetail(item.id)">详情</button>
        <button v-if="item.status === 10" class="plain small" @click="askCancel(item.id)">撤销申请</button>
      </view>
    </view>
    <view v-if="afterSales.length === 0" class="section">
      <text class="subtle">暂无售后记录</text>
    </view>
    <ConfirmDialog
      :visible="cancelDialog.visible"
      title="确认撤销售后"
      content="撤销后售后单保留为已撤销，仍在售后期内可重新申请。"
      confirm-text="撤销"
      danger
      @cancel="cancelDialog.visible = false"
      @confirm="confirmCancel"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserToast from '@/components/UserToast.vue';
import { pageAfterSales, type UserAfterSaleDTO } from '@/api/user';
import { showUserToast, statusText, useUserState } from '@/stores/userState';

const statusTabs = [
    { label: '全部', value: undefined },
    { label: '待审核', value: 10 },
    { label: '退货中', value: 20 },
    { label: '退款中', value: 30 },
    { label: '已完成', value: 40 },
    { label: '驳回', value: 60 }
];
const state = useUserState();
const status = ref<number | undefined>();
const afterSales = ref<UserAfterSaleDTO[]>([
    {
        id: 1,
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
        responsibilityType: 1,
        createTime: '2026-06-26 20:15:00'
    }
]);
const cancelDialog = reactive({
    visible: false,
    id: 0
});

async function loadAfterSales() {
    try {
        const page = await pageAfterSales({
            pageNum: 1,
            pageSize: 20,
            userId: state.user.id,
            status: status.value
        });
        if (page.list?.length) {
            afterSales.value = page.list;
        }
    } catch (error) {
        showUserToast('售后接口暂不可用，已展示本地售后状态', 'warn');
    }
}

function selectStatus(value?: number) {
    status.value = value;
    void loadAfterSales();
}

function openDetail(id: number) {
    uni.navigateTo({ url: `/pages/aftersale/detail?id=${id}` });
}

function statusLabel(value: number) {
    return statusText('afterSale', value);
}

function askCancel(id: number) {
    cancelDialog.id = id;
    cancelDialog.visible = true;
}

function confirmCancel() {
    cancelDialog.visible = false;
    showUserToast('撤销售后接口暂缺，已保留页面状态', 'warn');
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

.status-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18rpx;
}

.status-head > view {
  min-width: 0;
}

.status-head > view > text:first-child {
  display: block;
  overflow: hidden;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-row {
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
  margin-top: 18rpx;
}

.active {
  color: #d94b34;
  background: #fff2e9;
}
</style>
