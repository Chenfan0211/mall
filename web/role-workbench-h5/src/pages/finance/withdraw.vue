<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>申请提现</text>
      </view>
      <text class="subtle">{{ profile.wallet.label }}</text>
      <text class="title">{{ profile.wallet.amount }}</text>
      <text class="subtle">最低提现 ¥{{ profile.withdrawMin }}，手续费 ¥{{ profile.withdrawFee }}</text>
    </view>

    <view class="section">
      <view class="form-row">
        <text>提现金额</text>
        <input v-model="amount" type="digit" placeholder="请输入提现金额" />
      </view>
      <view class="form-row">
        <text>收款账户</text>
        <input :value="profile.accountSummary" disabled />
      </view>
      <view class="form-row">
        <text>备注</text>
        <textarea v-model="remark" placeholder="可填写提现说明" />
      </view>
      <text class="form-tip">提交后进入财务审核，审核通过后打款；待审核期间可在提现记录中查看。</text>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="submit">提交提现</button>
      <button class="soft" @click="back">返回</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { addOperation, confirmAction, currentProfile, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const amount = ref('');
const remark = ref('');

async function submit() {
    const value = Number(amount.value);
    if (!value || value < Number(profile.value.withdrawMin)) {
        showRoleToast(`提现金额不能低于 ¥${profile.value.withdrawMin}`);
        return;
    }
    const ok = await confirmAction(`确认申请提现 ¥${value.toFixed(2)}？`, '申请提现');
    if (!ok) return;
    addOperation({
        no: `WD${Date.now()}`,
        type: '提现申请',
        title: `申请提现 ¥${value.toFixed(2)}${remark.value ? `，${remark.value}` : ''}`,
        time: '刚刚',
        status: '待审核',
        next: '等待财务审核'
    });
    showRoleToast('提现接口暂未接通，已记录前端操作');
    back();
}

function back() {
    uni.navigateBack({ delta: 1 });
}
</script>

<style lang="scss" scoped>
.form-row {
  margin-bottom: 20rpx;
}

.form-row > text {
  display: block;
  margin-bottom: 10rpx;
  color: #5f493d;
  font-size: 24rpx;
  font-weight: 800;
}

.form-tip {
  display: block;
  color: #8f6c58;
  font-size: 23rpx;
  line-height: 1.6;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
