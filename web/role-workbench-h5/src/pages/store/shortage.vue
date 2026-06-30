<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '缺货标记' : '标记异常' }}</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">{{ roleType === 'supplier' ? '缺货标记' : '标记异常' }}</text>
          <text class="role-detail-meta">{{ activeOrderTitle }}</text>
        </view>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">{{ roleType === 'supplier' ? '正在加载送货单' : '正在加载异常范围' }}</text>
      <text class="subtle">正在同步可标记商品...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">{{ roleType === 'supplier' ? '缺货数据加载失败' : '异常范围加载失败' }}</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <view v-else-if="roleType === 'station'" class="role-main">
      <view v-if="!stationSummary" class="role-empty">
        <text class="title">暂无可标记商品</text>
        <text class="subtle">当前没有可上报异常的配送商品，可返回门店作业刷新后重试。</text>
      </view>

      <template v-else>
        <view class="role-detail-section">
          <text class="section-title">异常范围</text>
          <view class="role-total-box">
            <view>
              <text>应到</text>
              <text>{{ stationSummary.expected }}</text>
            </view>
            <view>
              <text>实到</text>
              <text>{{ stationSummary.actual }}</text>
            </view>
            <view>
              <text>可标记</text>
              <text>{{ stationAvailableQty }}/{{ stationSummary.lack }}</text>
            </view>
          </view>
          <view class="role-leave-form station-exception-form">
            <view class="role-form-row">
              <text>异常类型</text>
              <picker mode="selector" :range="stationExceptionTypes" :value="stationExceptionTypeIndex" @change="changeStationExceptionType">
                <view class="role-picker-trigger">{{ stationExceptionType }}</view>
              </picker>
            </view>
            <view class="role-form-row">
              <text>处理期望</text>
              <textarea v-model="stationReason" placeholder="请填写期望运营如何处理，不直接退款或补货" />
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <text class="section-title">商品信息</text>
          <view class="role-supplier-shortage-main">
            <view class="role-product-summary-img"><RoleProductThumb :label="stationSummary.title" /></view>
            <view>
              <text class="role-shortage-title">{{ stationSummary.title }}</text>
              <text class="role-shortage-desc">{{ stationSummary.spec }}</text>
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <view class="role-section-head">
            <view>
              <text class="section-title">用户购买明细</text>
              <text class="subtle">按订单商品维度填写异常数量</text>
            </view>
          </view>
          <view class="role-user-breakdown">
            <view v-for="item in stationRows" :key="item.id" class="role-user-buy-row" :class="{ 'shortage-marked': stationShortageMap[item.id] > 0 }">
              <view>
                <text>{{ item.pickupName || '提货人未返回' }} {{ item.pickupMobile || '' }}</text>
                <text>
                  {{ item.orderNoText }} · 购买 {{ item.qty || 0 }} 件 · {{ formatDateText(item.expectedPickupDate || '-') }}
                  <text v-if="stationShortageMap[item.id]" class="role-shortage-badge">已填写异常 {{ stationShortageMap[item.id] }} 件</text>
                </text>
              </view>
              <view class="role-shortage-edit">
                <input v-model="stationShortageInputs[item.id]" type="number" :placeholder="`0-${item.qty || 0}`" />
                <button class="role-shortage-btn" @click="markStation(item)">标记</button>
              </view>
            </view>
            <view v-if="stationRows.length === 0" class="role-empty inline-empty">
              <text class="title">暂无用户明细</text>
              <text class="subtle">当前暂无符合条件的订单商品数据</text>
            </view>
          </view>
        </view>

        <view class="role-order-actions">
          <button class="role-action-btn danger" @click="submitStationException">提交异常</button>
          <button class="role-action-btn soft" @click="back">返回</button>
        </view>
      </template>
    </view>

    <view v-else class="role-main">
      <view v-if="!activePurchase" class="role-empty">
        <text class="title">暂无送货单</text>
        <text class="subtle">当前暂无可标记缺货的采购或送货数据</text>
      </view>

      <template v-else>
        <view class="role-detail-section">
          <text class="section-title">送货单缺货</text>
          <view class="role-total-box">
            <view>
              <text>合计数量</text>
              <text>{{ totalQty }}</text>
            </view>
            <view>
              <text>已标记</text>
              <text>{{ markedQty }}</text>
            </view>
            <view>
              <text>可标记</text>
              <text>{{ availableQty }}</text>
            </view>
          </view>
        </view>

        <view class="role-detail-section">
          <view class="role-section-head">
            <view>
              <text class="section-title">商品明细</text>
              <text class="subtle">填写缺货数量用于核对送货差异</text>
            </view>
          </view>
          <view class="role-supplier-shortage-list">
            <view v-for="item in rows" :key="item.id" class="role-supplier-shortage-card">
              <view class="role-supplier-shortage-main">
                <view class="role-product-summary-img"><RoleProductThumb :label="item.productName || item.skuName" /></view>
                <view>
                  <text class="role-shortage-title">{{ item.title }}</text>
                  <text class="role-shortage-desc">{{ item.spec }}</text>
                </view>
              </view>
              <view class="role-supplier-shortage-foot">
                <view>
                  <text>送货数量</text>
                  <text>{{ item.qty }}</text>
                  <text v-if="shortageMap[item.id]" class="role-shortage-badge">已填写 {{ shortageMap[item.id] }} 件</text>
                </view>
                <view class="role-shortage-edit">
                  <input
                    v-model="shortageInputs[item.id]"
                    type="number"
                    :placeholder="`0-${item.qty}`"
                  />
                  <button class="role-shortage-btn" @click="mark(item)">标记</button>
                </view>
              </view>
            </view>
            <view v-if="rows.length === 0" class="role-empty inline-empty">
              <text class="title">暂无商品明细</text>
              <text class="subtle">当前暂无采购商品数据</text>
            </view>
          </view>
        </view>

        <view class="role-order-actions">
          <button class="role-action-btn danger" @click="submit">提交缺货</button>
          <button class="role-action-btn soft" @click="back">返回</button>
        </view>
      </template>
    </view>
    <RoleBottomNav :active="roleType === 'supplier' ? 'orders' : 'delivery'" />
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import {
    pageStationOrderItems,
    pageStationOrders,
    pageSupplierPurchaseItems,
    pageSupplierPurchases,
    type StationOrderDTO,
    type StationOrderItemDTO,
    type SupplierPurchaseDTO,
    type SupplierPurchaseItemDTO
} from '@/api/station';
import { currentRole, getRequiredRoleQuery, goPage, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

interface ShortageRow {
    id: number;
    title: string;
    spec: string;
    initial: string;
    qty: number;
}

interface StationShortageRow extends StationOrderItemDTO {
    pickupName: string;
    pickupMobile: string;
    orderNoText: string;
}

interface StationShortageSummary {
    title: string;
    spec: string;
    initial: string;
    expected: number;
    actual: number;
    lack: number;
}

const purchaseId = ref<number>();
const stationKey = ref('');
const loading = ref(false);
const error = ref('');
const purchases = ref<SupplierPurchaseDTO[]>([]);
const items = ref<SupplierPurchaseItemDTO[]>([]);
const stationReason = ref('');
const shortageInputs = reactive<Record<number, string>>({});
const shortageMap = reactive<Record<number, number>>({});
const stationShortageInputs = reactive<Record<number, string>>({});
const stationShortageMap = reactive<Record<number, number>>({});
const stationOrders = ref<StationOrderDTO[]>([]);
const stationItems = ref<StationOrderItemDTO[]>([]);
const stationExceptionTypes = ['少件', '错件', '破损', '变质'];
const stationExceptionTypeIndex = ref(0);
const roleType = computed(() => currentRole.value);

const activePurchase = computed(() => {
    if (purchaseId.value) {
        return purchases.value.find((item) => item.id === purchaseId.value) || null;
    }
    return purchases.value[0] || null;
});

const activeOrderTitle = computed(() => {
    if (roleType.value === 'station') return stationSummary.value ? `${stationSummary.value.title} · ${stationSummary.value.spec}` : '门店作业 · 少件 / 错件 / 破损';
    return activePurchase.value?.purchaseNo || (activePurchase.value?.id ? `采购单 #${activePurchase.value.id}` : '未找到送货单');
});

const rows = computed<ShortageRow[]>(() => items.value.map((item) => {
    const title = item.productName || `商品 #${item.productId || item.id}`;
    return {
        id: item.id,
        title,
        spec: item.skuName || (item.skuId ? `SKU #${item.skuId}` : '-'),
        initial: title.slice(0, 1) || '商',
        qty: Number(item.purchaseQty || item.applyQty || 0)
    };
}));

const totalQty = computed(() => rows.value.reduce((sum, item) => sum + item.qty, 0));
const markedQty = computed(() => Object.values(shortageMap).reduce((sum, value) => sum + Number(value || 0), 0));
const availableQty = computed(() => Math.max(0, totalQty.value - markedQty.value));
const stationExceptionType = computed(() => stationExceptionTypes[stationExceptionTypeIndex.value] || stationExceptionTypes[0]);
const stationRows = computed<StationShortageRow[]>(() => {
    const source = stationKey.value
        ? stationItems.value.filter((item) => productKey(item) === stationKey.value)
        : stationItems.value;
    return source.map((item) => decorateStationRow(item, stationOrders.value));
});
const stationSummary = computed<StationShortageSummary | null>(() => buildStationSummary(stationRows.value));
const stationMarkedQty = computed(() => Object.values(stationShortageMap).reduce((sum, value) => sum + Number(value || 0), 0));
const stationAvailableQty = computed(() => Math.max(0, (stationSummary.value?.lack || 0) - stationMarkedQty.value));

onLoad((query) => {
    const value = Number(query?.purchaseId || query?.id);
    if (value) purchaseId.value = value;
    stationKey.value = decodeURIComponent(String(query?.key || ''));
});

onShow(load);

async function load() {
    if (roleType.value === 'station') {
        loading.value = true;
        error.value = '';
        try {
            const query = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 200 };
            const [orderPage, itemPage] = await Promise.all([
                pageStationOrders({ ...query, pageSize: 100 }),
                pageStationOrderItems(query)
            ]);
            stationOrders.value = orderPage.list || [];
            stationItems.value = itemPage.list || [];
        } catch (err) {
            error.value = friendlyErrorMessage(err, '异常商品加载失败，请稍后重试');
        } finally {
            loading.value = false;
        }
        return;
    }
    loading.value = true;
    error.value = '';
    try {
        const query = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 50 };
        const purchasePage = await pageSupplierPurchases(query);
        purchases.value = purchasePage.list || [];
        const target = activePurchase.value;
        if (!target) {
            items.value = [];
            return;
        }
        const itemQuery = { ...query, purchaseId: target.id, pageSize: 100 };
        items.value = (await pageSupplierPurchaseItems(itemQuery)).list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '缺货数据加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function submitStationException() {
    showRoleToast('异常提交通道暂未开放，未提交数据');
}

function changeStationExceptionType(event: { detail?: { value?: number | string } }) {
    const index = Number(event.detail?.value || 0);
    stationExceptionTypeIndex.value = Number.isFinite(index) ? index : 0;
}

function markStation(item: StationShortageRow) {
    const value = Math.floor(Number(stationShortageInputs[item.id] || 0));
    const limit = Number(item.qty || 0);
    if (!Number.isFinite(value) || value < 0) {
        showRoleToast('请输入有效异常数量');
        return;
    }
    if (value > limit) {
        showRoleToast('异常数量不能大于购买数量');
        return;
    }
    stationShortageMap[item.id] = value;
    showRoleToast(value > 0 ? `已标记异常 ${value} 件` : '已清空异常数量');
}

function mark(item: ShortageRow) {
    const value = Math.floor(Number(shortageInputs[item.id] || 0));
    if (!Number.isFinite(value) || value < 0) {
        showRoleToast('请输入有效缺货数量');
        return;
    }
    if (value > item.qty) {
        showRoleToast('缺货数量不能大于送货数量');
        return;
    }
    shortageMap[item.id] = value;
    showRoleToast(value > 0 ? `已标记缺货 ${value} 件` : '已清空缺货数量');
}

function submit() {
    showRoleToast('缺货提交通道暂未开放，未提交数据');
}

function back() {
    if (roleType.value === 'supplier') {
        goPage('/pages/orders/index');
        return;
    }
    goPage('/pages/store/index');
}

function decorateStationRow(item: StationOrderItemDTO, orders: StationOrderDTO[]): StationShortageRow {
    const order = orders.find((row) => row.id === item.orderId || row.orderNo === item.orderNo);
    return {
        ...item,
        pickupName: order?.pickupName || '',
        pickupMobile: order?.pickupMobile || '',
        orderNoText: item.orderNo || order?.orderNo || `订单#${item.orderId || '-'}`
    };
}

function buildStationSummary(items: StationShortageRow[]): StationShortageSummary | null {
    if (items.length === 0) return null;
    const first = items[0];
    const expected = items.reduce((sum, item) => sum + Number(item.qty || 0), 0);
    const actual = items.reduce((sum, item) => sum + actualQty(item), 0);
    return {
        title: first.productName || `SKU #${first.skuId || first.id}`,
        spec: first.skuName || '-',
        initial: (first.productName || '商').slice(0, 1),
        expected,
        actual,
        lack: Math.max(0, expected - actual)
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

function formatDateText(value?: string) {
    if (!value || value === '-') return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0];
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
  overflow: hidden;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
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

.role-supplier-shortage-list {
  display: grid;
  gap: 20rpx;
}

.role-leave-form {
  display: grid;
  gap: 20rpx;
}

.role-form-row > text {
  display: block;
  margin-bottom: 10rpx;
  color: #5f493d;
  font-size: 24rpx;
  font-weight: 800;
}

.role-form-row input,
.role-form-row textarea,
.role-picker-trigger {
  width: 100%;
  min-height: 82rpx;
  box-sizing: border-box;
  padding: 0 22rpx;
  color: #2b241f;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 24rpx;
  font-size: 26rpx;
}

.role-picker-trigger {
  display: flex;
  align-items: center;
  font-weight: 800;
}

.role-form-row textarea {
  min-height: 180rpx;
  padding-top: 20rpx;
}

.station-exception-form {
  margin-top: 20rpx;
}

.role-supplier-shortage-card {
  padding: 24rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 28rpx;
}

.role-supplier-shortage-main {
  display: grid;
  grid-template-columns: 96rpx minmax(0, 1fr);
  gap: 18rpx;
  align-items: center;
}

.role-product-summary-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border: 1rpx solid #f2d6c4;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: 900;
}

.role-shortage-title {
  display: block;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-shortage-desc {
  display: block;
  margin-top: 8rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-supplier-shortage-foot {
  display: grid;
  gap: 18rpx;
  margin-top: 20rpx;
  padding-top: 18rpx;
  border-top: 1rpx solid #f1e1d6;
}

.role-supplier-shortage-foot > view:first-child {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
  color: #8b6a57;
  font-size: 24rpx;
}

.role-supplier-shortage-foot > view:first-child text:nth-child(2) {
  color: #2d241f;
  font-weight: 900;
}

.role-shortage-badge {
  padding: 6rpx 14rpx;
  color: #b42318;
  background: #fff0ec;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
}

.role-shortage-edit {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 132rpx;
  gap: 14rpx;
}

.role-shortage-edit input {
  min-height: 70rpx;
  padding: 0 22rpx;
  color: #2b241f;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 22rpx;
  font-size: 26rpx;
}

.role-shortage-btn,
:deep(uni-button.role-shortage-btn) {
  min-height: 70rpx;
  color: #ffffff;
  background: #b9362a;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-user-breakdown {
  display: grid;
  gap: 16rpx;
}

.role-user-buy-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 16rpx;
  padding: 20rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
}

.role-user-buy-row.shortage-marked {
  background: #fff7f1;
  border-color: #efc8b7;
}

.role-user-buy-row > view:first-child > text:first-child {
  display: block;
  color: #2d241f;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-user-buy-row > view:first-child > text:nth-child(2) {
  display: block;
  margin-top: 6rpx;
  color: #8b6a57;
  font-size: 23rpx;
  line-height: 1.45;
}

.role-user-buy-row .role-shortage-edit {
  grid-template-columns: minmax(0, 1fr) 132rpx;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.role-action-btn,
:deep(uni-button.role-action-btn) {
  flex: 1;
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

.role-action-btn.danger,
:deep(uni-button.role-action-btn.danger) {
  color: #ffffff;
  background: #b9362a;
}

.role-action-btn.soft,
:deep(uni-button.role-action-btn.soft) {
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
}

.role-action-btn[disabled],
:deep(uni-button.role-action-btn[disabled]) {
  color: #9a8a80;
  background: #f4f1ee;
  border: 1rpx solid #e8d8cf;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}
</style>
