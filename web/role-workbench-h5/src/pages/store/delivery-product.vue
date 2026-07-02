<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>配送商品详情</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">配送商品详情</text>
          <text class="role-detail-meta">{{ summaryText }}</text>
        </view>
        <text class="role-status" :class="summary?.statusClass || 'blue'">{{ summary?.status || '等待处理' }}</text>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载商品详情</text>
      <text class="subtle">正在同步订单商品明细...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">商品详情加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view v-if="summary" class="role-detail-section">
          <text class="section-title">商品信息</text>
          <view class="role-product-summary-main">
            <view class="role-product-summary-img"><RoleProductThumb :label="summary.title" /></view>
            <view>
              <text class="role-product-title">{{ summary.title }}</text>
              <text class="role-product-desc">{{ summary.spec }}</text>
            </view>
          </view>
          <view class="role-total-box">
            <view>
              <text>应到</text>
              <text>{{ summary.expected }}</text>
            </view>
            <view>
              <text>实到</text>
              <text>{{ summary.actual }}</text>
            </view>
            <view>
              <text>缺货</text>
              <text>{{ summary.lack }}</text>
            </view>
          </view>
          <view v-if="summary.lack > 0" class="role-short-tip">
            <text>缺货 {{ summary.lack }} 件</text>
            <button class="role-shortage-entry" @click="openShortage">去标记</button>
          </view>
        </view>

        <view v-else class="role-empty inline-empty">
          <text class="title">暂无商品详情</text>
          <text class="subtle">当前没有匹配的订单商品。</text>
        </view>

        <view class="role-detail-section">
          <view class="role-section-head">
            <view>
              <text class="section-title">用户购买明细</text>
              <text class="subtle">按订单商品维度展示用户和数量</text>
            </view>
          </view>
          <view class="role-user-breakdown">
            <view v-for="item in rows" :key="item.id" class="role-user-buy-row">
              <view>
                <text>{{ item.pickupName || '提货人未返回' }} {{ item.pickupMobile || '' }}</text>
                <text><text class="role-order-code-inline">{{ item.orderNoText }}</text> · 购买 {{ item.qty || 0 }} 件 · {{ formatDateText(item.expectedPickupDate || '-') }}</text>
              </view>
              <text>x{{ item.qty || 0 }}</text>
            </view>
            <view v-if="rows.length === 0" class="role-empty inline-empty">
              <text class="title">暂无用户明细</text>
              <text class="subtle">当前暂无符合条件的订单商品数据</text>
            </view>
          </view>
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
import { pageStationOrderItems, pageStationOrders, type StationOrderDTO, type StationOrderItemDTO } from '@/api/station';
import { getRequiredRoleQuery, goPage } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

interface DeliveryUserRow extends StationOrderItemDTO {
    pickupName: string;
    pickupMobile: string;
    orderNoText: string;
}

interface DeliverySummary {
    title: string;
    spec: string;
    expected: number;
    actual: number;
    lack: number;
    status: string;
    statusClass: string;
}

const key = ref('');
const loading = ref(false);
const error = ref('');
const rows = ref<DeliveryUserRow[]>([]);
const summary = ref<DeliverySummary | null>(null);
const returnQuery = ref('');

const summaryText = computed(() => {
    if (!summary.value) return '暂无商品配送数据';
    return `${rows.value.length} 位用户 · 实到 ${summary.value.actual}/${summary.value.expected}`;
});

onLoad((query) => {
    const routeQuery = {
        ...h5HashQuery(),
        ...normalizeQuery(query)
    };
    key.value = safeDecode(routeQuery.key || '');
    returnQuery.value = buildReturnQuery(routeQuery);
    load();
});

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const query = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 200 };
        const [orderPage, itemPage] = await Promise.all([
            pageStationOrders({ ...query, pageSize: 100 }),
            pageStationOrderItems(query)
        ]);
        const orders = orderPage.list || [];
        const items = itemPage.list || [];
        const filtered = key.value ? items.filter((item) => productKey(item) === key.value) : items;
        rows.value = filtered.map((item) => decorateRow(item, orders));
        summary.value = buildSummary(filtered);
    } catch (err) {
        error.value = friendlyErrorMessage(err, '商品详情加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function decorateRow(item: StationOrderItemDTO, orders: StationOrderDTO[]): DeliveryUserRow {
    const order = orders.find((row) => row.id === item.orderId || row.orderNo === item.orderNo);
    return {
        ...item,
        pickupName: order?.pickupName || '',
        pickupMobile: order?.pickupMobile || '',
        orderNoText: item.orderNo || order?.orderNo || `订单#${item.orderId || '-'}`
    };
}

function buildSummary(items: StationOrderItemDTO[]): DeliverySummary | null {
    if (items.length === 0) return null;
    const first = items[0];
    const expected = items.reduce((sum, item) => sum + Number(item.qty || 0), 0);
    const actual = items.reduce((sum, item) => sum + actualQty(item), 0);
    const status = deliveryProductStatus(expected, actual);
    return {
        title: first.productName || `SKU #${first.skuId || first.id}`,
        spec: first.skuName || '-',
        expected,
        actual,
        lack: Math.max(0, expected - actual),
        status,
        statusClass: roleStatusClass(status)
    };
}

function productKey(item: StationOrderItemDTO) {
    const title = item.productName || `SKU #${item.skuId || item.id}`;
    const spec = item.skuName || '-';
    return `${title}__${spec}`;
}

function actualQty(item: StationOrderItemDTO) {
    const pickedQty = Number(item.pickedQty || 0);
    if (pickedQty > 0) return pickedQty;
    if (Number(item.fulfillStatus || 0) >= 50) return Number(item.qty || 0);
    return 0;
}

function deliveryProductStatus(expected: number, actual: number) {
    if (expected <= 0) return '等待处理';
    if (actual >= expected) return '已到货';
    if (actual > 0) return '部分到货';
    return '待到货';
}

function roleStatusClass(status: string) {
    if (/已到货|已完成/.test(status)) return 'green';
    if (/部分到货|等待/.test(status)) return 'orange';
    if (/待到货/.test(status)) return 'blue';
    return 'blue';
}

function formatDateText(value?: string) {
    if (!value || value === '-') return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0];
}

function openShortage() {
    const params = [
        key.value ? `key=${encodeURIComponent(key.value)}` : '',
        returnQuery.value
    ].filter(Boolean).join('&');
    const query = params ? `?${params}` : '';
    goPage(`/pages/store/shortage${query}`);
}

function back() {
    goPage(`/pages/store/index${returnQuery.value ? `?${returnQuery.value}` : ''}`);
}

function buildReturnQuery(query?: Record<string, unknown>) {
    return ['tab', 'keyword', 'date', 'onlyShortage']
        .map((key) => {
            const value = Array.isArray(query?.[key]) ? query?.[key][0] : query?.[key];
            const text = safeDecode(String(value ?? ''));
            return text ? `${key}=${encodeURIComponent(text)}` : '';
        })
        .filter(Boolean)
        .join('&');
}

function normalizeQuery(query?: Record<string, unknown>) {
    const result: Record<string, string> = {};
    Object.entries(query || {}).forEach(([key, value]) => {
        result[key] = safeDecode(Array.isArray(value) ? String(value[0] || '') : String(value ?? ''));
    });
    return result;
}

function h5HashQuery() {
    const result: Record<string, string> = {};
    const hash = typeof location === 'undefined' ? '' : location.hash || '';
    const queryText = hash.includes('?') ? hash.slice(hash.indexOf('?') + 1) : '';
    if (!queryText) return result;
    new URLSearchParams(queryText).forEach((value, key) => {
        result[key] = safeDecode(value);
    });
    return result;
}

function safeDecode(value: string) {
    let text = value;
    for (let i = 0; i < 2; i += 1) {
        if (!/%[0-9A-Fa-f]{2}/.test(text)) break;
        try {
            text = decodeURIComponent(text);
        } catch (err) {
            break;
        }
    }
    return text;
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
  max-width: 390rpx;
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

.role-detail-section,
.role-short-tip {
  margin-bottom: 24rpx;
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
  margin-bottom: 18rpx;
  color: #2d241f;
  font-size: 30rpx;
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
  border: 1rpx solid #f2d6c4;
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

.role-total-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 20rpx;
}

.role-total-box view {
  padding: 16rpx 12rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f3ddcf;
  border-radius: 22rpx;
  text-align: center;
}

.role-total-box text:first-child {
  display: block;
  margin-bottom: 6rpx;
  font-size: 22rpx;
}

.role-total-box text:last-child {
  display: block;
  color: #d94b34;
  font-size: 30rpx;
  font-weight: 900;
}

.role-short-tip {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
  padding: 18rpx 20rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-shortage-entry,
uni-button.role-shortage-entry {
  min-height: 56rpx;
  padding: 0 18rpx;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-user-breakdown {
  display: grid;
  gap: 16rpx;
}

.role-user-buy-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f1e1d6;
}

.role-user-buy-row:last-child {
  border-bottom: 0;
}

.role-user-buy-row text:first-child {
  display: block;
  color: #2d241f;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-user-buy-row text:nth-child(2) {
  display: block;
  margin-top: 6rpx;
  color: #8b6a57;
  font-size: 23rpx;
  line-height: 1.45;
}

.role-user-buy-row > text {
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

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  align-items: center;
}

.role-action-btn,
uni-button.role-action-btn {
  min-height: 58rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-action-btn.primary {
  color: #ffffff;
  background: #e85d3f;
}

.role-action-btn.soft {
  color: #b85a2f;
  background: #fff3ea;
}

.inline-empty {
  margin: 0;
}

.retry-btn {
  margin-top: 18rpx;
}
</style>
