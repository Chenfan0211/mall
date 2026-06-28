<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>休假申请</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">提交休假</text>
      <text class="subtle">提交后等待后台审核，审核通过后休假日期不可选点。</text>
    </view>

    <view class="section">
      <view class="form-row">
        <text>开始日期</text>
        <input v-model="form.startDate" placeholder="例如 2026-06-18" />
      </view>
      <view class="form-row">
        <text>结束日期</text>
        <input v-model="form.endDate" placeholder="例如 2026-06-20" />
      </view>
      <view class="form-row">
        <text>休假原因</text>
        <textarea v-model="form.reason" placeholder="请填写无人值守、设备检修等原因" />
      </view>
      <view class="impact-card">
        <text class="title">提交影响</text>
        <text class="subtle">休假期间将暂停新订单选择该自提点；已有订单仍需完成履约。后台审核通过前不立即生效。</text>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="submit">提交申请</button>
      <button class="soft" @click="back">返回</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue';
import { addOperation, confirmAction, currentProfile, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const form = reactive({
    startDate: '2026-06-18',
    endDate: '2026-06-20',
    reason: ''
});

async function submit() {
    if (!form.startDate || !form.endDate || !form.reason.trim()) {
        showRoleToast('请完整填写休假信息');
        return;
    }
    const ok = await confirmAction('休假会影响用户选点和未支付订单继续支付，确认提交？', '提交休假申请');
    if (!ok) return;
    addOperation({
        no: `LV${Date.now()}`,
        type: '休假申请',
        title: `${form.startDate} 至 ${form.endDate} 休假`,
        time: '刚刚',
        status: '待审核',
        next: '等待后台审核'
    });
    showRoleToast('休假接口暂未接通，已记录前端操作');
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

.impact-card {
  padding: 22rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 24rpx;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
