<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>仓库汇总</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">{{ title }}</text>
          <text class="role-detail-meta">{{ profile.entity }} · 该仓库下所有送货单商品汇总</text>
        </view>
      </view>
    </view>

    <view v-if="loading" class="empty-panel">
      <text class="title">正在加载送货汇总</text>
      <text class="subtle">正在同步仓库送货汇总...</text>
    </view>
    <view v-else-if="error" class="empty-panel">
      <text class="title">送货汇总加载失败</text>
      <text class="subtle">{{ error }}</text>
    </view>

    <view v-else class="role-main">
      <view class="role-detail-section">
        <text class="section-title">仓库汇总</text>
        <view class="role-total-box">
          <view>
            <text>送货单</text>
            <text>{{ deliveries.length }}</text>
          </view>
          <view>
            <text>合计数量</text>
            <text>{{ totalQty }}</text>
          </view>
          <view>
            <text>合计金额</text>
            <text>{{ totalAmount }}</text>
          </view>
        </view>
        <view v-if="!hasWarehouseFilter" class="role-status-note">
          <text>未指定仓库</text>
          <text>当前按所有仓库汇总展示，可从供应商到仓页进入指定仓库。</text>
        </view>
      </view>

      <view class="role-detail-section">
        <text class="section-title">商品汇总</text>
        <view class="role-product-list">
          <view v-for="item in productSummaries" :key="item.key" class="role-product-summary-card">
            <view class="role-product-summary-main">
              <view class="role-product-summary-img"><RoleProductThumb :label="item.title" /></view>
              <view>
                <text class="role-product-title">{{ item.title }}</text>
                <text class="role-product-desc">{{ item.spec }}</text>
              </view>
            </view>
            <view class="role-product-summary-foot">
              <view>
                <text>{{ item.deliveryCount }}</text>
                <text>送货单</text>
              </view>
              <view>
                <text>{{ item.qty }}</text>
                <text>合计数量</text>
              </view>
              <view>
                <text>{{ item.amount }}</text>
                <text>合计金额</text>
              </view>
            </view>
          </view>
          <view v-if="productSummaries.length === 0" class="empty-panel inline-empty">
            <text class="title">暂无商品汇总数据</text>
            <text class="subtle">当前仓库暂无采购商品明细</text>
          </view>
        </view>
      </view>

      <view class="role-detail-section">
        <text class="section-title">送货单</text>
        <view class="role-task-list">
          <view v-for="item in deliveries" :key="item.id" class="role-task">
            <view>
              <text>{{ item.deliveryNo || `送货单 #${item.id}` }}</text>
              <text>仓库 #{{ item.warehouseId || '-' }} · {{ item.deliveryDate || item.createTime || '-' }}</text>
            </view>
            <text class="role-status" :class="deliveryStatusClass(item.status)">{{ deliveryStatusText(item.status) }}</text>
          </view>
          <view v-if="deliveries.length === 0" class="empty-panel inline-empty">
            <text class="title">暂无送货单</text>
            <text class="subtle">当前仓库暂无送货数据</text>
          </view>
        </view>
      </view>
    </view>
    <RoleBottomNav active="orders" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { pageSupplierDeliveries, pageSupplierPurchaseItems, type SupplierDeliveryDTO, type SupplierPurchaseItemDTO } from '@/api/station';
import { currentProfile, currentRole, getRequiredRoleQuery, goPage, moneyText } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

interface ProductSummary {
    key: string;
    title: string;
    spec: string;
    qty: number;
    amountValue: number;
    amount: string;
    deliveryCount: number;
}

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const warehouseId = ref<number>();
const loading = ref(false);
const error = ref('');
const rows = ref<SupplierDeliveryDTO[]>([]);
const items = ref<SupplierPurchaseItemDTO[]>([]);
const hasWarehouseFilter = computed(() => Boolean(warehouseId.value));
const title = computed(() => warehouseId.value ? `仓库 #${warehouseId.value}` : '仓库商品汇总');
const deliveries = computed(() => {
    if (!warehouseId.value) return rows.value;
    return rows.value.filter((item) => Number(item.warehouseId || 0) === warehouseId.value);
});
const warehouseItems = computed(() => {
    if (!warehouseId.value) return items.value;
    return items.value.filter((item) => Number(item.warehouseId || 0) === warehouseId.value);
});
const productSummaries = computed<ProductSummary[]>(() => {
    const deliveryCount = deliveries.value.length;
    const map = new Map<string, ProductSummary>();
    warehouseItems.value.forEach((item) => {
        const title = item.productName || `商品 #${item.productId || item.id}`;
        const spec = item.skuName || (item.skuId ? `SKU #${item.skuId}` : '-');
        const key = `${title}__${spec}`;
        const current = map.get(key) || {
            key,
            title,
            spec,
            qty: 0,
            amountValue: 0,
            amount: moneyText('0.00'),
            deliveryCount
        };
        current.qty += supplierItemQty(item);
        current.amountValue += supplierItemAmount(item);
        current.amount = moneyText(current.amountValue.toFixed(2));
        map.set(key, current);
    });
    return Array.from(map.values());
});
const totalQty = computed(() => productSummaries.value.reduce((sum, item) => sum + item.qty, 0));
const totalAmount = computed(() => moneyText(productSummaries.value.reduce((sum, item) => sum + item.amountValue, 0).toFixed(2)));

onLoad((query) => {
    const value = Number(query?.warehouseId);
    if (value) {
        warehouseId.value = value;
    }
});

onShow(load);

async function load() {
    if (roleType.value !== 'supplier') {
        uni.redirectTo({ url: '/pages/status/index?status=noPermission' });
        return;
    }
    loading.value = true;
    error.value = '';
    try {
        const baseQuery = { ...getRequiredRoleQuery(), ...(warehouseId.value ? { warehouseId: warehouseId.value } : {}), pageNum: 1, pageSize: 100 };
        const [deliveryPage, itemPage] = await Promise.all([
            pageSupplierDeliveries(baseQuery),
            pageSupplierPurchaseItems(baseQuery)
        ]);
        rows.value = deliveryPage.list || [];
        items.value = itemPage.list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '送货汇总加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function deliveryStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待送货',
        20: '送货中',
        30: '等待仓库确认',
        40: '仓库已确认',
        50: '已完成',
        60: '已取消'
    };
    return map[Number(status || 0)] || '等待处理';
}

function supplierItemQty(item: SupplierPurchaseItemDTO) {
    return Number(item.purchaseQty || item.applyQty || item.receivedQty || 0);
}

function supplierItemAmount(item: SupplierPurchaseItemDTO) {
    const directAmount = Number(item.amount || 0);
    if (directAmount) return directAmount;
    return Number(item.purchasePrice || 0) * supplierItemQty(item);
}

function deliveryStatusClass(status?: number) {
    const text = deliveryStatusText(status);
    if (/仓库已确认|已完成/.test(text)) return 'green';
    if (/送货中|等待仓库确认|等待处理/.test(text)) return 'orange';
    if (/已取消/.test(text)) return 'gray';
    return 'blue';
}

function back() {
    goPage('/pages/orders/index');
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

.role-total-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.role-total-box view {
  padding: 18rpx 12rpx;
  background: #fff8f2;
  border-radius: 20rpx;
  text-align: center;
}

.role-total-box text:first-child {
  display: block;
  color: #8b6a57;
  font-size: 22rpx;
}

.role-total-box text:last-child {
  display: block;
  margin-top: 6rpx;
  color: #d94b34;
  font-size: 30rpx;
  font-weight: 900;
}

.role-status-note {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-status-note text:first-child {
  display: block;
  margin-bottom: 6rpx;
  color: #3a2c24;
  font-weight: 900;
}

.role-product-list {
  display: grid;
  gap: 18rpx;
}

.role-product-summary-card {
  padding: 24rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 28rpx;
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

.role-product-summary-foot {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 20rpx;
}

.role-product-summary-foot view {
  padding: 14rpx 12rpx;
  color: #8b6a57;
  background: #fff8f2;
  border-radius: 20rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-product-summary-foot text:first-child {
  display: block;
  margin-bottom: 4rpx;
  color: #d94b34;
  font-size: 26rpx;
  font-weight: 900;
}

.role-task-list {
  display: grid;
  gap: 16rpx;
}

.role-task,
uni-button.role-task {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 112rpx;
  padding: 20rpx;
  color: #2d241f;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
  text-align: left;
}

.role-task view {
  min-width: 0;
}

.role-task view text:first-child {
  display: block;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
}

.role-task view text:last-child {
  display: block;
  margin-top: 8rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
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

.inline-empty {
  margin: 0;
}
</style>
