<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>申请提现</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">申请提现</text>
        </view>
      </view>
    </view>

    <view class="role-main">
      <view class="role-withdraw-card">
        <view class="role-withdraw-amount-box">
          <text class="role-withdraw-label">提现金额</text>
          <input class="role-withdraw-input" v-model="amount" type="digit" placeholder="请输入提现金额" />
        </view>

        <view class="role-wallet-actions three">
          <button class="role-wallet-action primary" :disabled="loading" @click="submit">{{ loading ? '提交中' : '提交申请' }}</button>
          <button class="role-wallet-action soft" @click="openWithdrawList">提现记录</button>
          <button class="role-wallet-action soft" @click="back">返回我的</button>
        </view>
      </view>
    </view>
    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { applyWithdraw, pageFinanceAccounts, type StationFinanceAccountDTO } from '@/api/station';
import { confirmAction, getRequiredFinanceQuery, goPage, moneyText, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const account = ref<StationFinanceAccountDTO | null>(null);
const accountError = ref('');
const amount = ref('');
const loading = ref(false);

const accountText = computed(() => {
    if (account.value) {
        return `${account.value.accountNo || `账户 #${account.value.id}`} · 可用 ${account.value.availableAmount || '0.00'}`;
    }
    return accountError.value || '暂无可提现资金账户';
});

onShow(loadAccount);

async function loadAccount() {
    accountError.value = '';
    account.value = null;
    try {
        const page = await pageFinanceAccounts({ ...getRequiredFinanceQuery(), pageNum: 1, pageSize: 1 });
        account.value = page.list?.[0] || null;
        if (!account.value) {
            accountError.value = '暂无可提现资金账户';
        }
    } catch (err) {
        accountError.value = friendlyErrorMessage(err, '资金账户加载失败');
    }
}

async function submit() {
    const value = Number(amount.value);
    if (!value || value <= 0) {
        showRoleToast('请输入有效提现金额');
        return;
    }
    if (!account.value?.id) {
        showRoleToast(accountError.value || '请先加载资金账户');
        return;
    }
    const ok = await confirmAction(`确认申请提现 ¥${value.toFixed(2)}？`, '申请提现');
    if (!ok) return;
    loading.value = true;
    try {
        await applyWithdraw({
            accountId: account.value.id,
            withdrawAmount: value.toFixed(2),
            receiveAccountName: account.value.accountNo || '默认收款账户',
            receiveAccountNo: account.value.accountNo || String(account.value.id)
        });
        showRoleToast('提现申请已提交');
        back();
    } catch (err) {
        showRoleToast(friendlyErrorMessage(err, '提现申请失败'));
    } finally {
        loading.value = false;
    }
}

function openWithdrawList() {
    goPage('/pages/finance/list?tab=withdraw');
}

function back() {
    uni.redirectTo({ url: '/pages/mine/index' });
}
</script>

<style lang="scss" scoped>
.role-detail-headline {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr);
  gap: 16rpx;
  align-items: center;
  margin-top: 18rpx;
}

.role-detail-back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  min-height: 64rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.24);
  border-radius: 999rpx;
  font-size: 44rpx;
  font-weight: 900;
}

.role-detail-title {
  display: block;
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.25;
}

.role-withdraw-card {
  display: grid;
  gap: 24rpx;
  margin-bottom: 24rpx;
  padding: 32rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.role-withdraw-amount-box {
  display: grid;
  gap: 24rpx;
}

.role-withdraw-label {
  display: block;
  color: #8b6b57;
  font-size: 24rpx;
  font-weight: 800;
}

.role-withdraw-input {
  width: 100%;
  min-height: 104rpx;
  padding: 0 28rpx;
  color: #3b2f28;
  background: #fff8f3;
  border: 1rpx solid #f0d8ca;
  border-radius: 32rpx;
  font-size: 44rpx;
  font-weight: 900;
}

.role-withdraw-hint {
  margin-top: -10rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.6;
}

.role-wallet-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.role-wallet-actions.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.role-wallet-action,
:deep(uni-button.role-wallet-action) {
  min-height: 76rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  font-size: 23rpx;
  font-weight: 900;
}

.role-wallet-action.primary,
:deep(uni-button.role-wallet-action.primary) {
  color: #ffffff;
  background: #e85d3f;
}

.role-wallet-action.soft,
:deep(uni-button.role-wallet-action.soft) {
  color: #b85a2f;
  background: #ffffff;
  box-shadow: inset 0 0 0 1rpx #f0d6c5;
}
</style>
