<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>操作记录</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">操作记录</text>
        </view>
        <text class="role-status blue">{{ filteredRecords.length }} 条</text>
      </view>
    </view>

    <view class="role-main">
      <view class="role-order-actions top-actions">
        <view class="role-operation-search">
          <text>🔍</text>
          <input v-model="keyword" placeholder="搜索编号、类型、内容或状态" />
        </view>
        <button class="role-filter-clear" @click="clearFilter">清空筛选</button>
        <button class="role-filter-clear" @click="load">刷新记录</button>
      </view>

      <view v-if="loading" class="role-empty">
        <text class="title">正在加载操作记录</text>
        <text class="subtle">正在同步操作记录...</text>
      </view>
      <view v-else-if="error" class="role-empty">
        <text class="title">操作记录加载失败</text>
        <text class="subtle">{{ error }}</text>
        <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
      </view>

      <view v-else class="role-detail-section">
        <view class="role-section-head">
          <view>
            <text class="title">本人提交记录</text>
            <text class="subtle">{{ profile.entity }} · {{ filteredRecords.length }} 条</text>
          </view>
        </view>
        <view class="role-leave-list">
          <view v-for="item in filteredRecords" :key="item.key" class="role-leave-card">
            <view class="role-leave-head">
              <text>{{ item.no }}</text>
              <text class="role-status" :class="item.statusClass">{{ item.status }}</text>
            </view>
            <text class="role-leave-copy">类型：{{ item.type }}</text>
            <text class="role-leave-copy">内容：{{ item.title }}</text>
            <text class="role-leave-copy">提交时间：{{ item.time }}</text>
          </view>
        </view>
        <view v-if="filteredRecords.length === 0" class="role-empty inline-empty">
          <text class="title">暂无提交记录</text>
        </view>
      </view>
    </view>
    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { pageLeaves, pageMessages, pageWithdraws, type StationLeaveDTO, type StationMessageDTO, type WithdrawApplyDTO } from '@/api/station';
import { currentProfile, getRequiredFinanceQuery, getRequiredMessageQuery, getRequiredRoleQuery } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const profile = computed(() => currentProfile.value);
interface OperationRecord {
    key: string;
    no: string;
    type: string;
    title: string;
    time: string;
    status: string;
    statusClass: string;
}

const loading = ref(false);
const error = ref('');
const keyword = ref('');
const records = ref<OperationRecord[]>([]);
const filteredRecords = computed(() => {
    const query = keyword.value.trim().toLowerCase();
    if (!query) return records.value;
    return records.value.filter((item) => [
        item.no,
        item.type,
        item.title,
        item.time,
        item.status
    ].join(' ').toLowerCase().includes(query));
});

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const [leavePage, withdrawPage, messagePage] = await Promise.all([
            pageLeaves({ ...getRequiredRoleQuery(), pageNum: 1, pageSize: 30 }).catch(() => ({ list: [] as StationLeaveDTO[] })),
            pageWithdraws({ ...getRequiredFinanceQuery(), pageNum: 1, pageSize: 30 }).catch(() => ({ list: [] as WithdrawApplyDTO[] })),
            pageMessages({ ...getRequiredMessageQuery(), pageNum: 1, pageSize: 30 }).catch(() => ({ list: [] as StationMessageDTO[] }))
        ]);
        records.value = [
            ...(leavePage.list || []).map(mapLeaveRecord),
            ...(withdrawPage.list || []).map(mapWithdrawRecord),
            ...(messagePage.list || []).map(mapMessageRecord)
        ].sort((a, b) => b.time.localeCompare(a.time, 'zh-CN')).slice(0, 50);
    } catch (err) {
        error.value = friendlyErrorMessage(err, '操作记录加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function mapLeaveRecord(item: StationLeaveDTO): OperationRecord {
    const status = leaveStatusText(item);
    return {
        key: `leave-${item.id}`,
        no: item.leaveNo || `休假 #${item.id}`,
        type: '休假申请',
        title: `${item.startTime || '-'} 至 ${item.endTime || '-'} · ${item.reason || '未填写原因'}`,
        time: item.createTime || item.startTime || '-',
        status,
        statusClass: statusClass(status)
    };
}

function mapWithdrawRecord(item: WithdrawApplyDTO): OperationRecord {
    const status = withdrawStatusText(item);
    return {
        key: `withdraw-${item.id}`,
        no: item.withdrawNo || `提现 #${item.id}`,
        type: '提现申请',
        title: `提现金额 ${item.applyAmount || item.withdrawAmount || '0.00'}`,
        time: item.createTime || '-',
        status,
        statusClass: statusClass(status)
    };
}

function mapMessageRecord(item: StationMessageDTO): OperationRecord {
    const status = item.readStatus === 1 ? '已读' : '未读';
    return {
        key: `message-${item.id}`,
        no: item.msgNo || `消息 #${item.id}`,
        type: item.bizType || '消息通知',
        title: item.title || item.content || item.bizNo || '通知记录',
        time: item.createTime || '-',
        status,
        statusClass: statusClass(status)
    };
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

function withdrawStatusText(item: WithdrawApplyDTO) {
    const audit = Number(item.auditStatus || 0);
    const pay = Number(item.payStatus || 0);
    const status = Number(item.status || 0);
    if (pay === 30 || pay === 40 || status === 40) return '打款成功';
    if (pay === 20 || status === 30) return '打款中';
    if (pay === 50 || audit === 30 || status === 50) return '已拒绝';
    if (audit === 20 || status === 20) return '已审核';
    if (status === 70) return '已撤销';
    return '待审核';
}

function statusClass(status: string) {
    if (/已通过|成功|已读|已审核/.test(status)) return 'green';
    if (/待|打款中|未读/.test(status)) return 'orange';
    if (/拒绝|撤销|关闭|过期/.test(status)) return 'gray';
    return 'blue';
}

function clearFilter() {
    keyword.value = '';
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

.top-actions,
.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-bottom: 18rpx;
}

.role-operation-search {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 12rpx;
  align-items: center;
  width: 100%;
  min-height: 80rpx;
  padding: 0 24rpx;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(60, 33, 16, 0.05), inset 0 1rpx 0 rgba(255, 255, 255, 0.85);
}

.role-operation-search text {
  color: #d94b34;
  font-size: 24rpx;
  font-weight: 900;
}

.role-operation-search input {
  min-height: 76rpx;
  padding: 0;
  background: transparent;
  border: 0;
}

.role-filter-clear,
:deep(uni-button.role-filter-clear) {
  min-height: 58rpx;
  padding: 0 24rpx;
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.inline-empty {
  margin: 0;
}

.role-leave-list {
  display: grid;
  gap: 20rpx;
}

.role-leave-card {
  padding: 24rpx;
  background: #fff8f2;
  border: 1rpx solid #f1dfd2;
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
  overflow: hidden;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-leave-copy {
  display: block;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.6;
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
</style>
