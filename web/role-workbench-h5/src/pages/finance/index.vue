<template>
  <view class="page">
    <view class="finance-hero">
      <text class="subtle">{{ profile.wallet.label }}</text>
      <text class="amount">{{ profile.wallet.amount }}</text>
      <text class="subtle">{{ roleType === 'supplier' ? '货款提现需财务审核，到账后进入结算记录。' : '佣金提现需后台财务审核，审核通过后打款。' }}</text>
    </view>

    <view class="two-grid wallet-grid">
      <view v-for="item in profile.wallet.stats" :key="item.label" class="section wallet-stat">
        <text class="money">{{ item.value }}</text>
        <text class="subtle">{{ item.label }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">账户信息</text>
          <text class="subtle">{{ profile.accountSummary }}</text>
        </view>
        <text class="status-pill green">已核验</text>
      </view>
      <view class="detail-row">
        <text>最低提现</text>
        <text>¥{{ profile.withdrawMin }}</text>
      </view>
      <view class="detail-row">
        <text>手续费</text>
        <text>¥{{ profile.withdrawFee }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">提现记录</text>
          <text class="subtle">展示当前主体申请状态</text>
        </view>
        <button class="plain mini" @click="open('/pages/finance/list?tab=withdraw')">全部</button>
      </view>
      <view class="card-list">
        <view v-for="item in profile.withdrawRequests.slice(0, 3)" :key="item.no" class="finance-card">
          <view class="card-head">
            <view class="card-main">
              <text class="card-title">{{ item.no }}</text>
              <text class="card-desc">{{ item.applyTime }} · {{ item.account }}</text>
            </view>
            <text class="status-pill" :class="item.statusClass">{{ item.status }}</text>
          </view>
          <view class="finance-foot">
            <text class="money">{{ item.amount }}</text>
            <button v-if="item.status === '待审核'" class="soft" @click="cancelWithdraw(item.no)">撤销</button>
          </view>
        </view>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="open('/pages/finance/withdraw')">申请提现</button>
      <button class="plain" @click="open('/pages/finance/list?tab=pending')">结算明细</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { confirmAction, currentProfile, currentRole, goPage, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);

function open(url: string) {
    goPage(url);
}

async function cancelWithdraw(no: string) {
    const ok = await confirmAction(`确认撤销提现申请 ${no}？`, '撤销提现');
    if (ok) {
        showRoleToast('撤销提现接口暂未接通，已保留交互');
    }
}
</script>

<style lang="scss" scoped>
.finance-hero {
  position: relative;
  overflow: hidden;
  margin-bottom: 20rpx;
  padding: 38rpx 28rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #d94b34 0%, #ef7a37 62%, #f7b36c 100%);
  border-radius: 30rpx;
  box-shadow: 0 24rpx 52rpx rgba(217, 75, 52, 0.18);
}

.finance-hero .subtle {
  color: #ffffff;
}

.amount {
  display: block;
  margin: 14rpx 0;
  color: #ffffff;
  font-size: 58rpx;
  font-weight: 900;
}

.wallet-grid {
  margin-bottom: 20rpx;
}

.wallet-stat {
  margin-bottom: 0;
  text-align: center;
}

.wallet-stat .money {
  display: block;
  font-size: 32rpx;
}

.mini {
  min-height: 58rpx;
  padding: 0 18rpx;
  font-size: 23rpx;
}

.finance-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16rpx;
}

.finance-foot button {
  min-width: 120rpx;
  min-height: 56rpx;
  font-size: 23rpx;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
