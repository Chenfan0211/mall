<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '送货单详情' : '提货订单详情' }}</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <text class="role-detail-title">{{ title }}</text>
        <text class="role-status" :class="roleStatusClass(status)">{{ status }}</text>
      </view>
      <view class="role-detail-meta">
        <text>{{ stationText }}</text>
        <text>提货 / 到仓：{{ pickupDate }}</text>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载详情</text>
      <text class="subtle">正在同步订单详情...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">详情加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-main">
        <view class="role-detail-section">
          <text class="section-title">{{ roleType === 'supplier' ? '送货信息' : '订单信息' }}</text>
          <view class="role-order-info">
            <view v-for="row in detailRows" :key="row.label">
              <text>{{ row.label }}</text>
              <text :class="{ 'role-order-code': row.label.includes('编号') }">{{ row.value }}</text>
            </view>
          </view>
        </view>

        <view class="role-order-actions">
          <button
            v-for="action in detailActions"
            :key="action"
            class="role-action-btn"
            :class="action === 'back' ? 'soft' : 'soft'"
            @click="runDetailAction(action)"
          >
            {{ detailActionLabel(action) }}
          </button>
        </view>

        <view v-if="roleType === 'station'" class="role-detail-section">
          <view class="role-section-head">
            <view>
              <text class="section-title">商品明细</text>
              <text class="subtle">{{ items.length }} 个商品</text>
            </view>
          </view>
          <view class="role-detail-item-list">
            <view v-for="item in items" :key="item.id" class="role-detail-item">
              <view class="role-detail-img"><RoleProductThumb :label="item.productName || item.skuName" /></view>
              <view class="role-detail-item-main">
                <text class="role-status role-detail-item-status" :class="roleStatusClass(displayItemStatus(item))">{{ displayItemStatus(item) }}</text>
                <text class="role-detail-item-title">{{ item.productName || `SKU #${item.skuId || item.id}` }}</text>
                <text class="role-detail-item-desc">规格：{{ item.skuName || '-' }}</text>
                <view class="role-detail-amount">
                  <text>{{ moneyText(item.itemAmount || item.salePrice) }}</text>
                  <text>x{{ item.qty || 0 }}</text>
                </view>
              </view>
            </view>
            <view v-if="items.length === 0" class="role-empty inline-empty">
              <text class="title">暂无商品明细</text>
              <text class="subtle">当前订单暂无商品明细</text>
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
              <text>合计数量</text>
              <text>{{ totalQty }}</text>
            </view>
            <view>
              <text>合计金额</text>
              <text>{{ totalAmount }}</text>
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
import { getStationOrderDetail, pageStationOrderItems, type StationOrderDTO, type StationOrderItemDTO } from '@/api/station';
import { currentProfile, currentRole, getRequiredRoleQuery, goPage, moneyText, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

const roleType = computed(() => currentRole.value);
const profile = computed(() => currentProfile.value);
const id = ref<number>();
const loading = ref(false);
const error = ref('');
const order = ref<StationOrderDTO | null>(null);
const items = ref<StationOrderItemDTO[]>([]);
const returnMode = ref('order');
const returnKeyword = ref('');
const returnPickupDate = ref('');

const title = computed(() => order.value?.orderNo || (id.value ? `单据 #${id.value}` : '-'));
const status = computed(() => order.value ? stationOrderStatusText(order.value, items.value) : '-');
const pickupDate = computed(() => stationOrderPickupDate(order.value, items.value));
const stationText = computed(() => profile.value.entity || `自提点 #${order.value?.stationId || '-'}`);
const totalQty = computed(() => items.value.reduce((sum, item) => sum + Number(item.qty || 0), 0));
const totalAmount = computed(() => {
    const itemAmount = items.value.reduce((sum, item) => sum + Number(item.itemAmount || 0), 0);
    const fallback = Number(order.value?.payAmount || order.value?.totalAmount || 0);
    return moneyText((itemAmount || fallback).toFixed(2));
});
const pickupUser = computed(() => [order.value?.pickupName, order.value?.pickupMobile].filter(Boolean).join(' ') || '-');
const detailRows = computed(() => [
    { label: '订单状态', value: status.value },
    { label: '生成时间', value: formatDateTimeText(order.value?.createTime) },
    { label: '提货人', value: pickupUser.value }
]);
type DetailAction = 'notify' | 'call' | 'back';
const detailActions = computed<DetailAction[]>(() => {
    if (roleType.value === 'station' && status.value === '待提货') {
        return ['notify', 'call', 'back'];
    }
    return ['back'];
});

onLoad((query) => {
    returnMode.value = normalizeReturnMode(query?.returnMode);
    returnKeyword.value = queryText(query?.keyword);
    returnPickupDate.value = queryText(query?.pickupDate);
    const value = Number(query?.id);
    if (value) {
        id.value = value;
        load();
    } else {
        error.value = '缺少订单 ID';
    }
});

async function load() {
    if (!id.value) return;
    loading.value = true;
    error.value = '';
    try {
        if (roleType.value === 'supplier') {
            error.value = '当前供应商送货详情暂无数据';
            return;
        }
        const detail = await getStationOrderDetail(id.value);
        order.value = detail;
        const query = { ...getRequiredRoleQuery(), keyword: detail.orderNo || undefined, pageNum: 1, pageSize: 100 };
        const list = (await pageStationOrderItems(query)).list || [];
        items.value = list.filter((item) => item.orderId === id.value || (!!detail.orderNo && item.orderNo === detail.orderNo));
    } catch (err) {
        error.value = friendlyErrorMessage(err, '详情加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function back() {
    const query = new URLSearchParams();
    if (returnMode.value) {
        query.set('mode', returnMode.value);
    }
    if (returnKeyword.value) {
        query.set('keyword', returnKeyword.value);
    }
    if (returnPickupDate.value) {
        query.set('pickupDate', returnPickupDate.value);
    }
    const text = query.toString();
    uni.redirectTo({ url: `/pages/orders/index${text ? `?${text}` : ''}` });
}

function normalizeReturnMode(value?: string | number) {
    const mode = String(value || 'order');
    return ['order', 'summary', 'warehouse'].includes(mode) ? mode : 'order';
}

function queryText(value: unknown) {
    const raw = Array.isArray(value) ? String(value[0] || '') : String(value || '');
    return safeDecode(raw);
}

function safeDecode(value: string) {
    let text = value;
    for (let i = 0; i < 2; i += 1) {
        if (!/%[0-9A-Fa-f]{2}/.test(text)) break;
        try {
            const decoded = decodeURIComponent(text);
            if (decoded === text) break;
            text = decoded;
        } catch {
            break;
        }
    }
    return text;
}

function openNotifyPreview() {
    const orderNo = order.value?.orderNo;
    const query = orderNo ? `?orderNo=${encodeURIComponent(orderNo)}` : '';
    goPage(`/pages/store/notify-preview${query}`);
}

function callPickupUser() {
    const mobile = order.value?.pickupMobile || '';
    if (!mobile) {
        showRoleToast('当前订单没有手机号');
        return;
    }
    uni.makePhoneCall({
        phoneNumber: mobile,
        fail: () => showRoleToast(`请手动拨打 ${mobile}`)
    });
}

function detailActionLabel(action: DetailAction) {
    const map: Record<DetailAction, string> = {
        notify: '发送到货通知',
        call: '拨打电话',
        back: roleType.value === 'supplier' ? '返回到仓' : '返回提货订单'
    };
    return map[action];
}

function runDetailAction(action: DetailAction) {
    if (action === 'notify') {
        openNotifyPreview();
        return;
    }
    if (action === 'call') {
        callPickupUser();
        return;
    }
    back();
}

function stationOrderStatusText(row: StationOrderDTO, rowItems: StationOrderItemDTO[]) {
    const trade = Number(row.tradeStatus || 0);
    const statuses = [row.fulfillStatus, ...rowItems.map((item) => item.fulfillStatus)]
        .filter((item) => item !== undefined && item !== null)
        .map(Number)
        .filter((item) => Number.isFinite(item));
    const maxFulfill = statuses.length ? Math.max(...statuses) : 0;
    const afterSaleStatuses = rowItems.map((item) => Number(item.afterSaleStatus || 0)).filter((item) => item > 0);

    const tradeText = stationTradeStatusText(trade);
    if (tradeText === '已关闭' || tradeText === '已取消') return tradeText;
    if (afterSaleStatuses.some((item) => item >= 10 && item <= 30)) return '待售后确认';
    if (tradeText === '已完成') return '已完成';
    if (statuses.length > 0 && statuses.every((item) => item >= 70)) return '已完成';
    if (maxFulfill >= 60) return '待提货';
    if (maxFulfill >= 30 || tradeText === '已支付') return '待到货';
    return '未发货';
}

function stationItemStatusText(item: StationOrderItemDTO) {
    const value = Number(item.fulfillStatus);
    const afterSale = Number(item.afterSaleStatus || 0);
    const trade = Number(order.value?.tradeStatus || 0);
    const tradeText = stationTradeStatusText(trade);
    if (tradeText === '已取消') return '已取消';
    if (afterSale >= 10 && afterSale <= 30) return '待售后确认';
    if (value >= 70 || tradeText === '已完成') return '已完成';
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

function displayItemStatus(item: StationOrderItemDTO) {
    return stationItemStatusText(item);
}

function stationOrderPickupDate(row: StationOrderDTO | null, rowItems: StationOrderItemDTO[]) {
    const fromItem = rowItems.find((item) => item.expectedPickupDate)?.expectedPickupDate;
    return formatDateText(fromItem || row?.createTime || '-');
}

function formatDateText(value?: string) {
    if (!value || value === '-') return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0];
}

function formatDateTimeText(value?: string) {
    if (!value || value === '-') return '-';
    const text = String(value).trim();
    const match = text.match(/^(\d{4})-(\d{2})-(\d{2})[T\s](\d{2}):(\d{2}):(\d{2})/);
    if (match) {
        return `${match[1]}-${match[2]}-${match[3]} ${match[4]}:${match[5]}:${match[6]}`;
    }
    return text.replace('T', ' ');
}

function roleStatusClass(text: string) {
    if (/已完成|已自提|已发送|已到货|已通过|仓库已确认|已结算/.test(text)) return 'green';
    if (/待提货|待售后确认/.test(text)) return 'blue';
    if (/未发货|待到货|等待|处理中|已提交|待处理|待审核|待确认|今日需处理/.test(text)) return 'orange';
    if (/驳回|拒绝|失败|停用|不可|已取消|已关闭|已退款/.test(text)) return 'gray';
    return 'orange';
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
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-top: 16rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-detail-meta text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  position: relative;
  display: grid;
  grid-template-columns: 92rpx minmax(0, 1fr);
  gap: 18rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f1e1d6;
}

.role-detail-item:last-child {
  border-bottom: 0;
}

.role-detail-item-main {
  min-width: 0;
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
  min-height: 44rpx;
  padding-right: 150rpx;
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
  justify-content: space-between;
  gap: 16rpx;
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

.role-detail-item-status {
  position: absolute;
  top: 18rpx;
  right: 0;
  z-index: 1;
  max-width: 144rpx;
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

.role-action-btn.soft,
:deep(uni-button.role-action-btn.soft) {
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  align-items: center;
  gap: 16rpx;
  margin: -8rpx 0 24rpx;
  padding: 0 10rpx;
}

.role-order-actions .role-action-btn,
.role-order-actions :deep(uni-button.role-action-btn) {
  margin-left: 0;
  margin-right: 0;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}
</style>
