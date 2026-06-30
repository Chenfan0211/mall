<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>消息中心</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">消息中心</text>
        </view>
        <text class="role-status blue">{{ rows.length }} 条</text>
      </view>
    </view>

    <view class="role-main">
      <view class="role-order-actions top-actions">
        <button class="role-filter-clear" @click="load">刷新消息</button>
      </view>

      <view v-if="loading" class="role-empty">
        <text class="title">正在加载消息</text>
        <text class="subtle">正在同步最新消息...</text>
      </view>
      <view v-else-if="error" class="role-empty">
        <text class="title">消息加载失败</text>
        <text class="subtle">{{ error }}</text>
      </view>

      <template v-else>
        <view v-for="group in groups" :key="group.title" class="role-detail-section">
          <view class="role-section-head">
            <view>
              <text class="title">{{ group.title }}</text>
              <text class="subtle">{{ group.rows.length }} 条</text>
            </view>
          </view>
          <view class="role-leave-list">
            <view v-for="item in group.rows" :key="item.id" class="role-leave-card" :class="{ unread: item.readStatus !== 1 }">
              <view class="role-leave-head">
                <text>{{ item.title || item.msgNo || `消息 #${item.id}` }}</text>
                <text class="role-status" :class="messageStatusClass(item)">{{ item.readStatus === 1 ? '已读' : '未读' }}</text>
              </view>
              <text class="role-leave-copy">通知时间：{{ item.createTime || '-' }}</text>
              <text class="role-leave-copy">通知类型：{{ group.title }} · {{ item.bizType || '-' }}</text>
              <text class="role-leave-copy">内容：{{ item.content || item.bizNo || '-' }}</text>
            </view>
            <view v-if="group.rows.length === 0" class="role-empty inline-empty">
              <text class="title">暂无该类消息</text>
            </view>
          </view>
        </view>
      </template>
    </view>
    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { pageMessages, type StationMessageDTO } from '@/api/station';
import { getRequiredMessageQuery } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const loading = ref(false);
const error = ref('');
const rows = ref<StationMessageDTO[]>([]);
const groups = computed(() => {
    const names = ['作业提醒', '审核结果', '异常反馈', '资金通知'];
    return names.map((title) => ({
        title,
        rows: rows.value.filter((item) => messageGroup(item) === title)
    }));
});

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        rows.value = (await pageMessages({ ...getRequiredMessageQuery(), pageNum: 1, pageSize: 50 })).list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '消息加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function messageGroup(item: StationMessageDTO) {
    const text = [item.bizType, item.title, item.content].join(' ');
    if (/finance|withdraw|settlement|资金|提现|结算|佣金/.test(text)) return '资金通知';
    if (/audit|审核|审批|休假/.test(text)) return '审核结果';
    if (/exception|异常|失败|缺货/.test(text)) return '异常反馈';
    return '作业提醒';
}

function messageStatusClass(item: StationMessageDTO) {
    return item.readStatus === 1 ? 'green' : 'orange';
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

.role-leave-card.unread {
  background: #fff4ed;
  border-color: #efc8b7;
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

.top-actions,
.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-bottom: 18rpx;
}

.role-filter-clear,
:deep(uni-button.role-filter-clear),
.role-action-btn,
:deep(uni-button.role-action-btn) {
  min-height: 58rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-filter-clear,
:deep(uni-button.role-filter-clear) {
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
}

.role-action-btn.primary,
:deep(uni-button.role-action-btn.primary) {
  color: #ffffff;
  background: #e85d3f;
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

.inline-empty {
  margin: 0;
}
</style>
