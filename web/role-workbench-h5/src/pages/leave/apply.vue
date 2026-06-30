<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>休假申请</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">发起休假</text>
        </view>
      </view>
    </view>

    <view class="role-main">
      <view class="role-detail-section">
        <text class="section-title">休假申请</text>
        <view class="role-leave-form">
          <view class="role-form-row">
            <text>开始日期</text>
            <picker mode="date" :value="startDate" @change="changeStartDate">
              <view class="role-date-input" :class="{ placeholder: !startDate }">
                <text>{{ startDate || '请选择开始日期' }}</text>
              </view>
            </picker>
          </view>
          <view class="role-form-row">
            <text>结束日期</text>
            <picker mode="date" :value="endDate" @change="changeEndDate">
              <view class="role-date-input" :class="{ placeholder: !endDate }">
                <text>{{ endDate || '请选择结束日期' }}</text>
              </view>
            </picker>
          </view>
          <view class="role-form-row">
            <text>休假原因</text>
            <textarea v-model="reason" placeholder="请输入休假原因" />
          </view>
          <view class="role-wallet-actions">
            <button class="role-wallet-action primary" :disabled="loading" @click="submit">{{ loading ? '提交中' : '提交申请' }}</button>
            <button class="role-wallet-action soft" @click="back">返回我的</button>
          </view>
        </view>
      </view>
    </view>
    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { applyLeave } from '@/api/station';
import { confirmAction, getRequiredRoleQuery, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const startDate = ref('');
const endDate = ref('');
const reason = ref('');
const loading = ref(false);

async function submit() {
    if (!startDate.value || !endDate.value || !reason.value.trim()) {
        showRoleToast('请填写完整休假信息');
        return;
    }
    if (startDate.value > endDate.value) {
        showRoleToast('结束日期不能早于开始日期');
        return;
    }
    const ok = await confirmAction('确认提交休假申请？', '休假申请');
    if (!ok) return;
    loading.value = true;
    try {
        const query = getRequiredRoleQuery();
        await applyLeave({
            stationId: query.stationId,
            leaderId: query.leaderId,
            startTime: `${startDate.value} 00:00:00`,
            endTime: `${endDate.value} 23:59:59`,
            reason: reason.value
        });
        showRoleToast('休假申请已提交');
        back();
    } catch (err) {
        showRoleToast(friendlyErrorMessage(err, '休假申请失败'));
    } finally {
        loading.value = false;
    }
}

function changeStartDate(event: { detail?: { value?: string } }) {
    startDate.value = event.detail?.value || '';
}

function changeEndDate(event: { detail?: { value?: string } }) {
    endDate.value = event.detail?.value || '';
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

.role-detail-section {
  margin-bottom: 24rpx;
  padding: 32rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.section-title {
  display: block;
  margin-bottom: 24rpx;
  color: #2d241f;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-leave-form {
  display: grid;
  gap: 24rpx;
}

.role-form-row {
  display: grid;
  gap: 12rpx;
}

.role-form-row > text {
  color: #8b6a57;
  font-size: 24rpx;
  font-weight: 800;
}

.role-form-row input,
.role-form-row textarea {
  width: 100%;
  min-height: 88rpx;
  padding: 0 24rpx;
  color: #332821;
  background: #fff8f2;
  border: 1rpx solid #f0d8ca;
  border-radius: 28rpx;
  font-size: 26rpx;
}

.role-date-input {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 88rpx;
  padding: 0 24rpx;
  color: #332821;
  background: #fff8f2;
  border: 1rpx solid #f0d8ca;
  border-radius: 28rpx;
  font-size: 26rpx;
  font-weight: 800;
}

.role-date-input.placeholder {
  color: #a58b7b;
  font-weight: 500;
}

.role-form-row textarea {
  min-height: 172rpx;
  padding-top: 22rpx;
  line-height: 1.55;
}

.role-wallet-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.role-wallet-action,
:deep(uni-button.role-wallet-action) {
  min-height: 76rpx;
  padding: 0 22rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
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
