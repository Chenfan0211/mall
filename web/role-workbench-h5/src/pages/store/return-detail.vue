<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>退货详情</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">{{ record?.returnNo || `退货记录 #${id || '-'}` }}</text>
          <text class="role-detail-meta">{{ profile.entity }}</text>
        </view>
        <text class="role-status" :class="roleStatusClass(statusText)">{{ statusText }}</text>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载退货详情</text>
      <text class="subtle">正在同步退货交接记录...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">退货详情加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view v-if="record" class="role-detail-section">
          <text class="section-title">订单和用户信息</text>
          <view class="role-return-users">
            <view class="role-return-user">
              <view>
                <text>{{ stationText }}</text>
                <text>{{ record.returnNo || `退货 #${record.id}` }} · {{ formatDateTime(record.confirmTime || record.createTime) }}</text>
              </view>
              <text>x{{ record.returnQty || 0 }}</text>
            </view>
          </view>
        </view>

        <view v-if="record" class="role-detail-section">
          <text class="section-title">退货商品</text>
          <view class="role-product-summary-main">
            <view class="role-product-summary-img">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M7 7h10l2 3v9H5v-9l2-3Z" />
                <path d="M9 7V5h6v2" />
                <path d="m9 14 2.2 2.2L15.8 12" />
              </svg>
            </view>
            <view>
              <text class="role-product-title">{{ record.returnNo || `退货 #${record.id}` }}</text>
              <text class="role-product-desc">SKU #{{ record.skuId || '-' }} · 仓库 #{{ record.warehouseId || '-' }}</text>
            </view>
          </view>
          <view class="role-total-box">
            <view>
              <text>退货数量</text>
              <text>{{ record.returnQty || 0 }}</text>
            </view>
            <view>
              <text>司机取货</text>
              <text>{{ pickupQty }}</text>
            </view>
            <view>
              <text>返仓数量</text>
              <text>{{ warehouseQty }}</text>
            </view>
          </view>
          <view v-if="hasMismatch" class="role-return-alert">退货数量、司机取回数量、返仓数量不一致，请核对司机交接记录</view>
          <view class="role-order-info detail-info">
            <view>
              <text>配送单</text>
              <text>#{{ record.deliveryId || '-' }}</text>
            </view>
            <view>
              <text>仓库</text>
              <text>#{{ record.warehouseId || '-' }}</text>
            </view>
            <view>
              <text>退货原因</text>
              <text>{{ reasonText(record.reasonType) }}</text>
            </view>
            <view>
              <text>确认时间</text>
              <text>{{ formatDateTime(record.confirmTime) }}</text>
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
          <text class="title">未找到退货记录</text>
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
import { pageReturnHandovers, type ReturnHandoverDTO } from '@/api/station';
import { currentProfile, getRequiredRoleQuery, goPage } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const profile = computed(() => currentProfile.value);
const id = ref<number>();
const loading = ref(false);
const error = ref('');
const record = ref<ReturnHandoverDTO | null>(null);

const statusText = computed(() => record.value ? returnStatusText(record.value.status) : '等待处理');
const stationText = computed(() => profile.value.entity || `自提点 #${record.value?.stationId || '-'}`);
const pickupQty = computed(() => record.value ? returnPickupQty(record.value) : 0);
const warehouseQty = computed(() => record.value ? returnWarehouseQty(record.value) : 0);
const hasMismatch = computed(() => {
    if (!record.value) return false;
    return Number(record.value.returnQty || 0) !== pickupQty.value || pickupQty.value !== warehouseQty.value;
});
const actionLine = computed(() => {
    if (!record.value) {
        return { need: '暂无可处理记录', next: '返回门店作业重新选择退货记录。' };
    }
    if ([20, 40].includes(Number(record.value.status || 0))) {
        return { need: '无需我处理', next: '该退货交接已进入后续节点，可继续查看记录。' };
    }
    if ([30].includes(Number(record.value.status || 0))) {
        return { need: '等待主管处理', next: '请按后台退货处理结果继续跟进，不在角色端伪造结果。' };
    }
    return { need: '待回仓确认', next: '等待仓库确认退货交接结果。' };
});

onLoad((query) => {
    const value = Number(query?.id);
    if (value) {
        id.value = value;
        load();
    } else {
        error.value = '缺少退货记录 ID';
    }
});

async function load() {
    if (!id.value) return;
    loading.value = true;
    error.value = '';
    try {
        const page = await pageReturnHandovers({ ...getRequiredRoleQuery(), pageNum: 1, pageSize: 100 });
        record.value = (page.list || []).find((item) => item.id === id.value) || null;
    } catch (err) {
        error.value = friendlyErrorMessage(err, '退货详情加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function returnStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待回仓确认',
        20: '已回仓确认',
        30: '待主管处理',
        40: '已关闭'
    };
    return map[Number(status || 0)] || '等待处理';
}

function returnPickupQty(item: ReturnHandoverDTO) {
    const qty = Number(item.returnQty || 0);
    const status = Number(item.status || 0);
    if (status >= 20 || item.confirmTime) return qty;
    return 0;
}

function returnWarehouseQty(item: ReturnHandoverDTO) {
    const qty = Number(item.returnQty || 0);
    const status = Number(item.status || 0);
    if (status >= 20 || item.confirmTime) return qty;
    return 0;
}

function reasonText(reason?: number) {
    const map: Record<number, string> = {
        10: '用户退货',
        20: '商品破损',
        30: '少件错件',
        40: '质量问题',
        50: '其他'
    };
    return map[Number(reason || 0)] || (reason ? `类型 ${reason}` : '-');
}

function roleStatusClass(status: string) {
    if (/已回仓|已关闭|已完成/.test(status)) return 'green';
    if (/待回仓|待主管|等待|待处理/.test(status)) return 'orange';
    if (/无法|失败|取消|驳回|拒绝/.test(status)) return 'gray';
    return 'blue';
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

.role-product-summary-main {
  display: grid;
  grid-template-columns: 108rpx minmax(0, 1fr);
  gap: 20rpx;
  align-items: center;
}

.role-product-summary-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 108rpx;
  height: 108rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border-radius: 26rpx;
  font-size: 36rpx;
  font-weight: 900;
}

.role-product-summary-img svg {
  width: 48rpx;
  height: 48rpx;
  fill: none;
}

.role-product-summary-img path {
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.role-product-title {
  display: block;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-product-desc {
  display: block;
  margin-top: 8rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-total-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 22rpx;
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

.role-return-alert {
  margin-top: 20rpx;
  padding: 18rpx 20rpx;
  color: #b42318;
  background: #fff0ec;
  border: 1rpx solid #ffd1c7;
  border-radius: 22rpx;
  font-size: 24rpx;
  font-weight: 800;
  line-height: 1.45;
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

.role-return-users {
  display: grid;
  gap: 14rpx;
}

.role-return-user {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 20rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
}

.role-return-user view {
  min-width: 0;
}

.role-return-user view text:first-child {
  display: block;
  overflow: hidden;
  color: #2d241f;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-return-user view text:last-child {
  display: block;
  margin-top: 8rpx;
  overflow: hidden;
  color: #8b6a57;
  font-size: 23rpx;
  line-height: 1.45;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-return-user > text {
  flex: 0 0 auto;
  color: #d94b34;
  font-size: 28rpx;
  font-weight: 900;
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
