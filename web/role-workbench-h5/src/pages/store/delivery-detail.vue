<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>到货详情</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">配送记录 #{{ id || '-' }}</text>
          <text class="role-detail-meta">{{ profile.entity }}</text>
        </view>
        <text class="role-status" :class="roleStatusClass(statusText)">{{ statusText }}</text>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载到货详情</text>
      <text class="subtle">正在同步到站记录...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">到货详情加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view class="role-status-note">
          <text>{{ actionLine.need }}</text>
          <text>{{ actionLine.next }}</text>
        </view>

        <view v-if="record" class="role-detail-section">
          <text class="section-title">到站信息</text>
          <view class="role-total-box">
            <view>
              <text>配送单</text>
              <text>#{{ record.deliveryId || '-' }}</text>
            </view>
            <view>
              <text>顺序</text>
              <text>{{ record.deliverySort || '-' }}</text>
            </view>
            <view>
              <text>状态</text>
              <text>{{ statusText }}</text>
            </view>
          </view>
          <view class="role-order-info detail-info">
            <view>
              <text>记录编号</text>
              <text>#{{ record.id }}</text>
            </view>
            <view>
              <text>自提点</text>
              <text>{{ stationText }}</text>
            </view>
            <view>
              <text>到达时间</text>
              <text>{{ formatDateTime(record.arriveTime) }}</text>
            </view>
            <view>
              <text>完成时间</text>
              <text>{{ formatDateTime(record.completeTime) }}</text>
            </view>
            <view>
              <text>创建时间</text>
              <text>{{ formatDateTime(record.createTime) }}</text>
            </view>
            <view>
              <text>备注</text>
              <text>{{ record.remark || '-' }}</text>
            </view>
          </view>
        </view>

        <view v-else class="role-empty inline-empty">
          <text class="title">未找到到站记录</text>
          <text class="subtle">当前记录暂不可查看，请返回门店作业重新选择。</text>
        </view>

        <view class="role-order-actions">
          <button class="role-action-btn soft" @click="back">返回门店作业</button>
        </view>
      </view>
    </template>
    <RoleBottomNav active="delivery" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { pageDeliveryStations, type DeliveryStationDTO } from '@/api/station';
import { currentProfile, getRequiredRoleQuery, goPage } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const profile = computed(() => currentProfile.value);
const id = ref<number>();
const loading = ref(false);
const error = ref('');
const record = ref<DeliveryStationDTO | null>(null);

const statusText = computed(() => record.value ? deliveryStatusText(record.value.status) : '等待处理');
const stationText = computed(() => profile.value.entity || `自提点 #${record.value?.stationId || '-'}`);
const actionLine = computed(() => {
    if (!record.value) {
        return { need: '暂无可处理记录', next: '返回门店作业重新选择到站记录。' };
    }
    if ([30].includes(Number(record.value.status || 0))) {
        return { need: '无需我处理', next: '该配送记录已完成，可继续查看作业记录。' };
    }
    if ([40].includes(Number(record.value.status || 0))) {
        return { need: '需关注异常', next: '该配送记录无法送达，请按门店作业异常流程跟进。' };
    }
    if ([20].includes(Number(record.value.status || 0))) {
        return { need: '等待后续处理', next: '司机已到站，可从门店作业入口确认到货或发送通知。' };
    }
    return { need: '等待配送到站', next: '配送到站后再确认到货并预览通知范围。' };
});

onLoad((query) => {
    const value = Number(query?.id);
    if (value) {
        id.value = value;
        load();
    } else {
        error.value = '缺少到站记录 ID';
    }
});

async function load() {
    if (!id.value) return;
    loading.value = true;
    error.value = '';
    try {
        const page = await pageDeliveryStations({ ...getRequiredRoleQuery(), pageNum: 1, pageSize: 100 });
        record.value = (page.list || []).find((item) => item.id === id.value) || null;
    } catch (err) {
        error.value = friendlyErrorMessage(err, '到货详情加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function deliveryStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待配送',
        20: '已到达',
        30: '已完成',
        40: '无法送达'
    };
    return map[Number(status || 0)] || '等待处理';
}

function roleStatusClass(status: string) {
    if (/已完成|已到达/.test(status)) return 'green';
    if (/待配送|等待/.test(status)) return 'blue';
    if (/无法|失败|取消/.test(status)) return 'gray';
    return 'orange';
}

function formatDateTime(value?: string) {
    if (!value) return '-';
    return String(value).replace(/:\d{2}$/, '').replace('T', ' ');
}

function back() {
    goPage('/pages/store/index');
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
  overflow: hidden;
  max-width: 420rpx;
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 900;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-detail-meta {
  display: block;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-main {
  padding-top: 0;
}

.role-status-note,
.role-detail-section {
  margin-bottom: 24rpx;
}

.role-status-note {
  padding: 20rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
  font-size: 24rpx;
  line-height: 1.55;
}

.role-status-note text:first-child {
  display: block;
  margin-bottom: 8rpx;
  color: #3a2c24;
  font-weight: 900;
}

.role-detail-section {
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.section-title {
  display: block;
  margin-bottom: 20rpx;
  color: #2b241f;
  font-size: 32rpx;
  font-weight: 900;
}

.role-total-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
}

.role-total-box view {
  padding: 16rpx 12rpx;
  color: #8b6a57;
  background: #fff8f2;
  border-radius: 22rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-total-box text {
  display: block;
}

.role-total-box text:last-child {
  margin-top: 8rpx;
  color: #d94b34;
  font-size: 28rpx;
  font-weight: 900;
}

.role-order-info {
  display: grid;
  gap: 14rpx;
}

.detail-info {
  margin-top: 22rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f1e1d6;
}

.role-order-info view {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.role-order-info text:first-child {
  color: #3a2c24;
  font-size: 24rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-order-info text:last-child {
  min-width: 0;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
  text-align: right;
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

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.role-action-btn,
:deep(uni-button.role-action-btn) {
  min-height: 66rpx;
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
  border: 1rpx solid #f0d6c5;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0 0 24rpx;
}
</style>
