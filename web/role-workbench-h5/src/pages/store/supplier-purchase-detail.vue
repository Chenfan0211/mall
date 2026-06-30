<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>采购单商品数据</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">采购单商品数据</text>
          <text class="role-detail-meta">{{ profile.entity }}</text>
        </view>
        <text class="role-status" :class="roleStatusClass(purchaseStatusText)">{{ purchaseStatusText }}</text>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载采购单商品数据</text>
      <text class="subtle">正在同步采购明细...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">采购单商品数据加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view class="role-status-note">
          <text>{{ actionState.need }}</text>
          <text>{{ actionState.next }}</text>
        </view>

        <view class="role-detail-section">
          <text class="section-title">采购单汇总</text>
          <view class="role-order-info">
            <view v-for="row in detailRows" :key="row.label">
              <text>{{ row.label }}</text>
              <text>{{ row.value }}</text>
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <view class="role-section-head">
            <view>
              <text class="section-title">商品数据</text>
              <text class="subtle">{{ items.length }} 个 SKU</text>
            </view>
          </view>
          <view class="role-detail-item-list">
            <view v-for="item in items" :key="item.id" class="role-detail-item">
              <view class="role-detail-img"><RoleProductThumb :label="item.productName || item.skuName" /></view>
              <view>
                <text class="role-detail-item-title">{{ item.productName || `商品 #${item.productId || '-'}` }}</text>
                <text class="role-detail-item-desc">规格：{{ item.skuName || `SKU #${item.skuId || '-'}` }}</text>
                <view class="role-detail-amount">
                  <text>{{ moneyText(item.amount || item.purchasePrice) }}</text>
                  <text>x{{ item.purchaseQty || 0 }}</text>
                </view>
              </view>
            </view>
            <view v-if="items.length === 0" class="role-empty inline-empty">
              <text class="title">暂无商品明细</text>
              <text class="subtle">当前暂无采购明细</text>
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <text class="section-title">合计</text>
          <view class="role-total-box">
            <view>
              <text>商品种类</text>
              <text>{{ items.length }}</text>
            </view>
            <view>
              <text>采购数量</text>
              <text>{{ totalQty }}</text>
            </view>
            <view>
              <text>采购金额</text>
              <text>{{ totalAmount }}</text>
            </view>
          </view>
        </view>
        <view class="role-order-actions">
          <button class="role-action-btn soft" @click="back">返回采购列表</button>
        </view>
      </view>
    </template>
    <RoleBottomNav active="orders" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getSupplierPurchaseDetail, pageSupplierPurchaseItems, type SupplierPurchaseDTO, type SupplierPurchaseItemDTO } from '@/api/station';
import { currentProfile, currentRole, getRequiredRoleQuery, goPage, moneyText } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const id = ref<number>();
const loading = ref(false);
const error = ref('');
const purchase = ref<SupplierPurchaseDTO | null>(null);
const items = ref<SupplierPurchaseItemDTO[]>([]);

const purchaseStatusText = computed(() => purchase.value ? purchaseDisplayStatus(purchase.value) : '-');
const actionState = computed(() => purchaseActionState(purchaseStatusText.value));
const totalQty = computed(() => items.value.reduce((sum, item) => sum + Number(item.purchaseQty || 0), 0));
const totalAmount = computed(() => moneyText(items.value.reduce((sum, item) => sum + Number(item.amount || 0), 0).toFixed(2)));
const detailRows = computed(() => [
    { label: '采购编号', value: purchase.value?.purchaseNo || '-' },
    { label: '仓库', value: purchase.value?.warehouseId ? `#${purchase.value.warehouseId}` : '-' },
    { label: '配送日期', value: purchase.value?.deliveryDate || '-' },
    { label: '预计到仓', value: purchase.value?.expectedArrivalTime || '-' },
    { label: '审核状态', value: auditStatusText(purchase.value?.auditStatus) },
    { label: '创建时间', value: purchase.value?.createTime || '-' },
    { label: '原因', value: purchase.value?.createReason || '-' }
]);

onLoad((query) => {
    if (roleType.value !== 'supplier') {
        uni.redirectTo({ url: '/pages/status/index?status=noPermission' });
        return;
    }
    const value = Number(query?.id);
    if (value) {
        id.value = value;
        load();
    } else {
        error.value = '缺少采购单 ID';
    }
});

async function load() {
    if (roleType.value !== 'supplier') return;
    if (!id.value) return;
    loading.value = true;
    error.value = '';
    try {
        purchase.value = await getSupplierPurchaseDetail(id.value);
        items.value = (await pageSupplierPurchaseItems({ ...getRequiredRoleQuery(), purchaseId: id.value, pageNum: 1, pageSize: 100 })).list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '采购详情加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function purchaseDisplayStatus(item: SupplierPurchaseDTO) {
    const audit = Number(item.auditStatus || 0);
    const status = Number(item.purchaseStatus || 0);
    if ([30, 40, 50, 60].includes(status)) return '已完成';
    if ([30, 40, 50, 60].includes(audit)) return '已完成';
    if ([3, 70].includes(status) || audit === 30) return '待我补充';
    if ([20, 25].includes(status)) return '待提交到仓';
    return '已提交待处理';
}

function auditStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待审核',
        20: '已通过',
        30: '已拒绝',
        40: '已完成'
    };
    return map[Number(status || 0)] || '待审核';
}

function roleStatusClass(status: string) {
    if (status === '已完成') return 'green';
    if (status === '待提交到仓') return 'blue';
    if (status === '待我补充') return 'gray';
    return 'orange';
}

function purchaseActionState(status: string) {
    if (status === '已完成') return { need: '无需我处理', next: '采购单已完成，可查看采购商品和到仓记录。' };
    if (status === '待提交到仓') return { need: '需要我处理', next: '按采购要求准备到仓凭证，后续由仓库确认入库。' };
    if (status === '待我补充') return { need: '需要我处理', next: '采购申请被驳回或异常，按后台要求补充后重新提交。' };
    return { need: '无需我处理', next: '采购申请已提交，等待平台处理。' };
}

function back() {
    goPage('/pages/store/supplier-purchase');
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

.role-status-note {
  margin-bottom: 24rpx;
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
  margin-bottom: 24rpx;
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

.role-section-head .section-title {
  margin-bottom: 6rpx;
}

.role-order-info {
  display: grid;
  gap: 14rpx;
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

.role-detail-item-list {
  display: grid;
  gap: 18rpx;
}

.role-detail-item {
  display: grid;
  grid-template-columns: 92rpx minmax(0, 1fr);
  gap: 18rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f1e1d6;
}

.role-detail-item:last-child {
  border-bottom: 0;
}

.role-detail-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 92rpx;
  height: 92rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border: 1rpx solid #f2d6c4;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: 900;
}

.role-detail-item-title {
  display: block;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-detail-item-desc {
  display: block;
  margin-top: 6rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-detail-amount {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 12rpx;
}

.role-detail-amount text:first-child {
  color: #d94b34;
  font-size: 28rpx;
  font-weight: 900;
}

.role-detail-amount text:nth-child(2) {
  color: #8f6c58;
  font-size: 24rpx;
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

.role-total-box text:first-child,
.role-total-box text:last-child {
  display: block;
}

.role-total-box text:last-child {
  margin-top: 8rpx;
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

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
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
  margin: 0;
}
</style>
