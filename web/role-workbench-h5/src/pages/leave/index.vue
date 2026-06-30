<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>休假记录</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="backMine">‹</button>
        <view>
          <text class="role-detail-title">休假列表</text>
        </view>
        <text class="role-status blue">{{ rows.length }} 条</text>
      </view>
    </view>

    <view class="role-main">
      <view v-if="loading" class="role-empty">
        <text class="title">正在加载休假记录</text>
        <text class="subtle">正在同步休假申请...</text>
      </view>
      <view v-else-if="error" class="role-empty">
        <text class="title">休假记录加载失败</text>
        <text class="subtle">{{ error }}</text>
        <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
      </view>

      <view v-else class="role-detail-section">
        <view class="role-section-head">
          <view>
            <text class="title">休假记录</text>
            <text class="subtle">{{ rows.length }} 条</text>
          </view>
        </view>
        <view class="role-leave-list">
          <view v-for="item in rows" :key="item.id" class="role-leave-card">
            <view class="role-leave-head">
              <text>{{ item.leaveNo || `休假 #${item.id}` }}</text>
              <text class="role-status" :class="leaveStatusClass(item)">{{ leaveStatusText(item) }}</text>
            </view>
            <text class="role-leave-copy">休假时间：{{ leaveRangeText(item) }} · {{ leaveDaysText(item) }}</text>
            <text class="role-leave-copy">申请原因：{{ item.reason || '-' }}</text>
            <text class="role-leave-copy">申请时间：{{ item.createTime || '-' }}</text>
            <text class="role-leave-copy">审核时间：{{ item.auditTime || '-' }}</text>
            <view v-if="item.auditRemark" class="role-leave-reject">
              <text>审核备注：{{ item.auditRemark }}</text>
            </view>
            <view v-if="canCancel(item)" class="role-order-actions">
              <button class="role-action-btn soft" @click="cancel(item.id)">撤回申请</button>
            </view>
          </view>
          <view v-if="rows.length === 0" class="role-empty inline-empty">
            <text class="title">暂无休假记录</text>
            <text class="subtle">当前暂无休假记录</text>
          </view>
        </view>
      </view>
    </view>
    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { cancelLeave, pageLeaves, type StationLeaveDTO } from '@/api/station';
import { confirmAction, getRequiredRoleQuery, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const loading = ref(false);
const error = ref('');
const rows = ref<StationLeaveDTO[]>([]);

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        rows.value = (await pageLeaves({ ...getRequiredRoleQuery(), pageNum: 1, pageSize: 50 })).list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '休假记录加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

async function cancel(id: number) {
    const ok = await confirmAction(`确认撤回休假申请 #${id}？`, '撤回休假');
    if (!ok) return;
    try {
        await cancelLeave(id);
        showRoleToast('撤回成功');
        load();
    } catch (err) {
        showRoleToast(friendlyErrorMessage(err, '撤回失败'));
    }
}

function leaveStatusText(item: StationLeaveDTO) {
    const audit = Number(item.auditStatus || 0);
    const status = Number(item.status || 0);
    if (status === 70) return '已撤销';
    const map: Record<number, string> = {
        10: '待审核',
        20: '已通过',
        30: '已拒绝',
        40: '已关闭',
        50: '已过期'
    };
    return map[audit] || map[status] || '待审核';
}

function leaveStatusClass(item: StationLeaveDTO) {
    const text = leaveStatusText(item);
    if (/已通过/.test(text)) return 'green';
    if (/待审核/.test(text)) return 'orange';
    if (/已拒绝|已撤销|已关闭|已过期/.test(text)) return 'gray';
    return 'blue';
}

function canCancel(item: StationLeaveDTO) {
    return leaveStatusText(item) === '待审核';
}

function leaveRangeText(item: StationLeaveDTO) {
    return `${formatDateText(item.startTime)} 至 ${formatDateText(item.endTime)}`;
}

function leaveDaysText(item: StationLeaveDTO) {
    const days = countLeaveDays(item.startTime, item.endTime);
    return days ? `${days} 天` : '- 天';
}

function countLeaveDays(start?: string, end?: string) {
    const startDate = parseDateOnly(start);
    const endDate = parseDateOnly(end);
    if (!startDate || !endDate) return 0;
    const diff = endDate.getTime() - startDate.getTime();
    if (diff < 0) return 0;
    return Math.floor(diff / 86400000) + 1;
}

function parseDateOnly(value?: string) {
    const match = String(value || '').match(/(\d{4})-(\d{2})-(\d{2})/);
    if (!match) return null;
    return new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]));
}

function formatDateText(value?: string) {
    if (!value) return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[1]}-${match[2]}-${match[3]}` : String(value);
}

function backMine() {
    uni.redirectTo({ url: '/pages/mine/index' });
}
</script>

<style lang="scss" scoped>
.role-detail-headline {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr) auto;
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
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.role-leave-list {
  display: grid;
  gap: 20rpx;
}

.role-leave-card {
  padding: 24rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 28rpx;
}

.role-leave-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 14rpx;
}

.role-leave-head > text:first-child {
  min-width: 0;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-leave-copy {
  display: block;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.6;
}

.role-leave-reject {
  margin-top: 14rpx;
  padding: 16rpx 18rpx;
  color: #bd4f36;
  background: #ffffff;
  border: 1rpx solid #f3d0c2;
  border-radius: 20rpx;
  font-size: 24rpx;
  line-height: 1.5;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-top: 18rpx;
}

.role-action-btn,
:deep(uni-button.role-action-btn) {
  min-height: 58rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-action-btn.primary,
:deep(uni-button.role-action-btn.primary) {
  color: #ffffff;
  background: #e85d3f;
}

.role-action-btn.soft,
:deep(uni-button.role-action-btn.soft) {
  color: #b85a2f;
  background: #fff3ea;
}

.role-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 48rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-status.orange {
  color: #d66323;
  background: #fff0e4;
}

.role-status.green {
  color: #17885f;
  background: #edf9f0;
}

.role-status.blue {
  color: #2b6cb0;
  background: #eef6ff;
}

.role-status.gray {
  color: #78665b;
  background: #f4f1ee;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}

</style>
