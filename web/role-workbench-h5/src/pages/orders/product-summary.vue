<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>商品订单用户</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">商品订单用户</text>
          <text class="role-detail-meta">{{ summaryText }}</text>
        </view>
        <text class="role-status blue">{{ rows.length }} 条</text>
      </view>
    </view>

    <view v-if="loading" class="empty-panel">
      <text class="title">正在加载商品汇总</text>
      <text class="subtle">正在同步商品订单用户...</text>
    </view>
    <view v-else-if="error" class="empty-panel">
      <text class="title">商品汇总加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view v-if="summary" class="role-product-summary-card detail-summary-card">
          <text class="section-title">商品汇总</text>
          <view class="role-product-summary-main">
            <view class="role-product-summary-img"><RoleProductThumb :label="summary.title" /></view>
            <view>
              <text class="role-product-title">{{ summary.title }}</text>
              <text class="role-product-desc">{{ summary.spec }}</text>
            </view>
          </view>
          <view class="role-product-summary-foot">
            <view>
              <text>{{ summary.orders.size }}</text>
              <text>提货订单</text>
            </view>
            <view>
              <text>{{ summary.qty }}</text>
              <text>合计数量</text>
            </view>
            <view>
              <text>¥{{ summary.amount }}</text>
              <text>合计金额</text>
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <text class="section-title">订单和用户信息</text>
          <view class="role-product-user-list">
            <view v-for="item in rows" :key="item.id" class="role-product-user-card">
              <view class="role-product-user-head">
                <text>{{ item.displayOrderNo }}</text>
                <text class="role-status" :class="item.statusClass">{{ item.status }}</text>
              </view>
              <view class="role-product-user-main">
                <view>
                  <text>提货人</text>
                  <text>{{ item.pickupName || '提货人未返回' }}</text>
                </view>
                <view>
                  <text>手机号</text>
                  <text>{{ item.pickupMobile || '-' }}</text>
                </view>
                <view>
                  <text>规格</text>
                  <text>{{ item.skuName || '-' }}</text>
                </view>
              </view>
              <view class="role-product-user-total">
                <view>
                  <text>{{ item.qty || 0 }}</text>
                  <text>数量</text>
                </view>
                <view>
                  <text>{{ moneyText(formatMoneyValue(item.displayAmount)) }}</text>
                  <text>金额</text>
                </view>
              </view>
            </view>
            <view v-if="rows.length === 0" class="empty-panel">
              <text class="title">暂无匹配商品</text>
              <text class="subtle">当前暂无符合条件的数据</text>
            </view>
          </view>
        </view>
      </view>
    </template>

    <RoleBottomNav active="orders" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { pageStationOrderItems, pageStationOrders, type StationOrderDTO, type StationOrderItemDTO } from '@/api/station';
import { getRequiredRoleQuery, goPage, moneyText } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

interface DisplayItem extends StationOrderItemDTO {
    displayOrderNo: string;
    displayAmount: number;
    pickupName: string;
    pickupMobile: string;
    orderCreateTime: string;
    status: string;
    statusClass: string;
}

interface SummaryRow {
    title: string;
    spec: string;
    qty: number;
    amount: string;
    orders: Set<string>;
}

const key = ref('');
const loading = ref(false);
const error = ref('');
const rows = ref<DisplayItem[]>([]);
const summary = ref<SummaryRow | null>(null);
const summaryText = computed(() => {
    if (!summary.value) return '暂无商品汇总数据';
    return `${summary.value.orders.size} 张提货订单 · ${summary.value.qty} 件`;
});

onLoad((query) => {
    key.value = safeDecode(String(query?.key || ''));
    load();
});

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const baseQuery = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 100 };
        const [orderPage, itemPage] = await Promise.all([pageStationOrders(baseQuery), pageStationOrderItems(baseQuery)]);
        const orders = orderPage.list || [];
        const list = itemPage.list || [];
        const filtered = key.value
            ? list.filter((item) => productSummaryKey(item) === key.value)
            : list;
        rows.value = filtered.map((item) => decorateItem(item, orders));
        summary.value = buildSummary(filtered);
    } catch (err) {
        error.value = friendlyErrorMessage(err, '商品汇总加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function decorateItem(item: StationOrderItemDTO, orders: StationOrderDTO[]): DisplayItem {
    const order = orders.find((row) => row.id === item.orderId || row.orderNo === item.orderNo);
    const status = stationItemStatusText(item, order);
    return {
        ...item,
        displayOrderNo: item.orderNo || order?.orderNo || `订单#${item.orderId || '-'}`,
        displayAmount: orderItemAmount(item),
        pickupName: order?.pickupName || '',
        pickupMobile: order?.pickupMobile || '',
        orderCreateTime: order?.createTime || '',
        status,
        statusClass: roleStatusClass(status)
    };
}

function buildSummary(items: StationOrderItemDTO[]): SummaryRow | null {
    if (items.length === 0) return null;
    const first = items[0];
    const amount = items.reduce((sum, item) => sum + Number(item.itemAmount || 0), 0);
    const orders = new Set<string>();
    items.forEach((item) => {
        const orderKey = item.orderNo || (item.orderId ? `订单#${item.orderId}` : '');
        if (orderKey) orders.add(orderKey);
    });
    return {
        title: first.productName || `SKU #${first.skuId || first.id}`,
        spec: first.skuName || '-',
        qty: items.reduce((sum, item) => sum + Number(item.qty || 0), 0),
        amount: (amount || items.reduce((sum, item) => sum + orderItemAmount(item), 0)).toFixed(2),
        orders
    };
}

function productSummaryKey(item: StationOrderItemDTO) {
    const title = item.productName || `SKU #${item.skuId || item.id}`;
    const spec = item.skuName || '-';
    return `${title}__${spec}`;
}

function safeDecode(value: string) {
    try {
        return decodeURIComponent(value);
    } catch {
        return value;
    }
}

function orderItemAmount(item: StationOrderItemDTO) {
    const directAmount = Number(item.itemAmount || 0);
    if (directAmount) return directAmount;
    return Number(item.salePrice || 0) * Number(item.qty || 0);
}

function formatMoneyValue(value: string | number | undefined) {
    const amount = Number(value || 0);
    return Number.isFinite(amount) ? amount.toFixed(2) : String(value || '0.00');
}

function stationItemStatusText(item: StationOrderItemDTO, order?: StationOrderDTO) {
    const value = Number(item.fulfillStatus);
    const afterSale = Number(item.afterSaleStatus || 0);
    const trade = Number(order?.tradeStatus || 0);
    const tradeText = stationTradeStatusText(trade);
    if (tradeText === '已关闭' || tradeText === '已取消') return tradeText;
    if (afterSale >= 10 && afterSale <= 30) return '待售后确认';
    if (tradeText === '已完成' || value >= 70) return '已完成';
    if (value >= 60) return '待提货';
    if (value >= 30) return '待到货';
    return '未发货';
}

function stationTradeStatusText(status: number) {
    const map: Record<number, string> = {
        10: '待付款',
        20: '已支付',
        30: '已关闭',
        40: '已取消',
        50: '已完成'
    };
    return map[status] || '';
}

function roleStatusClass(status: string) {
    if (/已完成|已自提|已发送|已到账|已通过|已结算/.test(status)) return 'green';
    if (/待提货|待售后确认/.test(status)) return 'blue';
    if (/未发货|待到货|等待|处理中|已提交|待处理|待审核|待确认|今日需处理/.test(status)) return 'orange';
    if (/驳回|拒绝|失败|停用|不可|已取消|已关闭|已退款/.test(status)) return 'gray';
    return 'orange';
}

function formatDateText(value?: string) {
    if (!value || value === '-') return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0];
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

.role-detail-meta {
  display: block;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
}

.role-detail-section {
  margin-bottom: 24rpx;
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.detail-summary-card {
  margin-bottom: 24rpx;
  padding: 24rpx;
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
  padding: 14rpx;
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

.role-product-user-list {
  display: grid;
  gap: 20rpx;
}

.role-product-user-card {
  padding: 24rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 28rpx;
}

.role-product-user-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-bottom: 18rpx;
}

.role-product-user-head > text:first-child {
  min-width: 0;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-product-user-main {
  display: grid;
  gap: 12rpx;
  padding: 20rpx 0;
  border-top: 1rpx solid #f1e1d6;
  border-bottom: 1rpx solid #f1e1d6;
}

.role-product-user-main view {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.role-product-user-main text:first-child {
  color: #3a2c24;
  font-size: 24rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-product-user-main text:last-child {
  min-width: 0;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
  text-align: right;
}

.role-product-user-total {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 18rpx;
}

.role-product-user-total view {
  padding: 14rpx 10rpx;
  color: #8b6a57;
  background: #fffdfb;
  border-radius: 20rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-product-user-total text:first-child {
  display: block;
  margin-bottom: 4rpx;
  overflow: hidden;
  color: #d94b34;
  font-size: 24rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
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
</style>
