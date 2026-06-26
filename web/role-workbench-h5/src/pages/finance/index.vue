<template>
  <view class="page">
    <view class="section hero">
      <text class="subtle">可提现佣金</text>
      <text class="amount">¥{{ availableAmount }}</text>
      <text class="subtle">提现需后台财务审核</text>
    </view>
    <view class="section">
      <view v-for="item in records" :key="item.id" class="row">
        <text>{{ item.withdrawNo }}</text>
        <text>¥{{ item.applyAmount }}</text>
      </view>
    </view>
    <button class="primary">申请提现</button>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageFinanceAccounts, pageWithdraws, type WithdrawApplyDTO } from '@/api/station';

const availableAmount = ref('0.00');
const records = ref<WithdrawApplyDTO[]>([]);

async function loadData() {
    const accounts = await pageFinanceAccounts({ pageNum: 1, pageSize: 1, subjectId: 720001 });
    availableAmount.value = accounts.list[0]?.availableAmount || '0.00';
    const withdraws = await pageWithdraws({ pageNum: 1, pageSize: 20, subjectId: 720001 });
    records.value = withdraws.list;
}

onMounted(() => {
    void loadData();
});
</script>

<style lang="scss" scoped>
.hero {
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
  border: 0;
  box-shadow: 0 16rpx 32rpx rgba(217, 75, 52, 0.22);
}

.hero .subtle {
  color: #ffffff;
}

.amount {
  display: block;
  margin: 12rpx 0;
  font-size: 48rpx;
  font-weight: 700;
}

.row {
  display: flex;
  justify-content: space-between;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f0dfd6;
}

.primary {
  color: #ffffff;
  background: #d94b34;
  border-radius: 20rpx;
}
</style>
