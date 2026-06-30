<template>
  <view class="service-page shop-page" data-m-page="afterSale">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>售后记录</b>
        <span>{{ afterSales.length }}单</span>
      </view>
    </view>

    <view class="service-list">
      <view class="service-filter-strip">
        <text
          v-for="item in statusTabs"
          :key="item.label"
          class="pill"
          :class="{ active: status === item.value }"
          @click="selectStatus(item.value)"
        >
          {{ item.label }}
        </text>
      </view>

      <view
        v-for="item in afterSales"
        :key="item.id"
        class="after-sale-record service-card"
        @click="openDetail(item.id)"
      >
        <view class="after-sale-top">
          <view>
            <b>{{ item.afterSaleNo }}</b>
            <span>订单 {{ item.orderNo }} · 退款 ¥{{ item.refundAmount }}</span>
          </view>
          <text class="pill">{{ statusLabel(item.status) }}</text>
        </view>
        <view class="after-sale-meta">
          <span>{{ item.applyReason || '售后原因待同步' }}</span>
          <span>{{ item.createTime || '创建时间待同步' }}</span>
        </view>
        <view class="receiver-actions">
          <button class="service-action" @click.stop="openDetail(item.id)">详情</button>
          <button v-if="item.status === 10" class="service-action danger" @click.stop="askCancel(item.id)">撤销申请</button>
        </view>
      </view>

      <EmptyActionCard
        v-if="afterSales.length === 0"
        title="暂无售后记录"
        sub="可在已完成订单中申请售后。"
        icon="售"
        button-text="看订单"
        @action="goOrder"
      />
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
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { pageAfterSales, type UserAfterSaleDTO } from '@/api/user';
import { navigateUser, showUserToast, statusText, useUserState } from '@/stores/userState';

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
const statusTabs = [
    { label: '全部', value: undefined },
    { label: '待审核', value: 10 },
    { label: '退货中', value: 20 },
    { label: '退款中', value: 30 },
    { label: '已完成', value: 40 },
    { label: '驳回', value: 60 }
];
const cancelDialog = reactive({
    visible: false,
    id: 0
});

async function loadAfterSales() {
    try {
        const page = await pageAfterSales(
            {
                pageNum: 1,
                pageSize: 20,
                userId: state.user.id,
                status: status.value
            },
            { silent: true }
        );
        if (page?.list?.length) {
            afterSales.value = page.list;
        }
    } catch {
        console.info('用户端售后接口不可用，已展示本地售后状态');
    }
}

function selectStatus(value?: number) {
    status.value = value;
    void loadAfterSales();
}

function openDetail(id: number) {
    navigateUser(`/pages/aftersale/detail?id=${id}`);
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

function goMine() {
    navigateUser('/pages/mine/index', true);
}

function goOrder() {
    navigateUser('/pages/order/index', true);
}

onMounted(() => {
    void loadAfterSales();
});
</script>

<style lang="scss" scoped>
.after-sale-record {
  display: grid;
  gap: 16rpx;
  padding: 22rpx;
}

.after-sale-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.after-sale-top view {
  min-width: 0;
}

.after-sale-top b,
.after-sale-top span,
.after-sale-meta span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.after-sale-top b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.after-sale-top span,
.after-sale-meta span {
  margin-top: 6rpx;
  color: #8c6a58;
  font-size: 25rpx;
}

.after-sale-meta {
  display: grid;
  gap: 6rpx;
  padding: 16rpx;
  background: #fffaf6;
  border-radius: 18rpx;
}
</style>
