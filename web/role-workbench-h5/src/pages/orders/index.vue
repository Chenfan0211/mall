<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ profile.entity }}</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="role-work-title-main">{{ roleType === 'supplier' ? '供应商到仓' : '提货订单' }}</text>
          <text class="role-work-sub">{{ roleType === 'supplier' ? '只看仓库、SKU、数量、到仓时间和单据状态' : '默认当前提货日，可按订单或商品汇总查询' }}</text>
        </view>
        <text class="role-work-entity">{{ profile.entity }}</text>
      </view>
    </view>

    <view class="role-query-tabs">
      <button :class="{ active: mode === 'order' }" @click="mode = 'order'">
        {{ roleType === 'supplier' ? '送货单维度' : '提货订单' }}
      </button>
      <button :class="{ active: mode === secondaryMode }" @click="setSecondaryMode">
        {{ secondaryLabel }}
      </button>
    </view>

    <view class="role-order-query-row" :class="{ 'single': roleType !== 'station' }">
      <view class="role-order-search">
        <text>🔍</text>
        <input v-model="keyword" :placeholder="placeholderText" />
      </view>
      <view v-if="roleType === 'station'" class="role-order-date">
        <picker mode="selector" :range="dateOptions" :value="activeDateIndex" @change="changeDate">
          <view class="role-date-trigger">
            <text>{{ activeDateLabel }}</text>
          </view>
        </picker>
      </view>
    </view>

    <view v-if="loading" class="empty-panel">
      <text class="title">正在加载订单</text>
      <text class="subtle">正在同步提货订单...</text>
    </view>
    <view v-else-if="error" class="empty-panel">
      <text class="title">订单加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view v-if="mode === 'order'" class="role-order-list">
        <view v-for="order in filteredOrders" :key="order.id" class="role-order-card">
          <view class="role-order-head">
            <view>
              <text class="role-order-no">{{ order.no }}</text>
              <text>{{ order.location }}</text>
            </view>
            <text class="role-status" :class="order.statusClass">{{ order.status }}</text>
          </view>
          <view class="role-order-summary">
            <view>
              <text>{{ order.qty }}</text>
              <text>合计数量</text>
            </view>
            <view>
              <text>¥{{ order.amount }}</text>
              <text>合计金额</text>
            </view>
          </view>
          <view v-if="roleType === 'station'" class="role-order-info">
            <view>
              <text>提货人</text>
              <text>{{ order.primary }}</text>
            </view>
            <view>
              <text>手机号</text>
              <text>{{ order.mobile || '-' }}</text>
            </view>
            <view>
              <text>自提点</text>
              <text>{{ order.location }}</text>
            </view>
            <view>
              <text>提货日</text>
              <text>{{ order.pickupDate }}</text>
            </view>
          </view>
          <view class="role-order-actions">
            <button class="role-action-btn primary" @click.stop="openDetail(order.id)">查看详情</button>
            <button v-if="roleType === 'station'" class="role-action-btn soft" @click.stop="callUser(order)">拨打电话</button>
            <button v-if="roleType === 'station'" class="role-action-btn soft" @click.stop="copyMobile(order)">复制手机号</button>
            <button v-if="roleType === 'station'" class="role-action-btn soft" @click.stop="openNotifyPreview(order)">发送到货通知</button>
          </view>
        </view>
        <view v-if="filteredOrders.length === 0" class="empty-panel">
          <text class="title">{{ roleType === 'station' ? '暂无匹配的提货订单' : '暂无匹配的送货单' }}</text>
        </view>
      </view>

      <view v-else-if="roleType === 'supplier'" class="role-order-list">
        <view v-for="item in filteredWarehouses" :key="item.key" class="role-order-card role-warehouse-card">
          <view class="role-order-head">
            <view>
              <text class="role-order-no">{{ item.title }}</text>
              <text>{{ item.deliveryCount }} 张送货单 · {{ item.productCount }} 个商品</text>
            </view>
            <text class="role-status" :class="item.statusClass">{{ item.status }}</text>
          </view>
          <view class="role-order-summary role-warehouse-summary">
            <view>
              <text>{{ item.deliveryCount }}</text>
              <text>送货单</text>
            </view>
            <view>
              <text>{{ item.qty }}</text>
              <text>合计数量</text>
            </view>
            <view>
              <text>¥{{ item.amount }}</text>
              <text>合计金额</text>
            </view>
          </view>
          <view class="role-status-note">
            <text class="role-order-need">{{ item.actionNeed }}</text>
            <text>当前状态：{{ item.status }}</text>
            <text>{{ item.nextText }}</text>
          </view>
          <view class="role-order-actions">
            <button class="role-action-btn primary" @click.stop="openWarehouse(item)">查看商品汇总</button>
          </view>
        </view>
        <view v-if="filteredWarehouses.length === 0" class="empty-panel">
          <text class="title">暂无仓库汇总</text>
          <text class="subtle">当前供应商暂无可汇总的仓库送货数据</text>
        </view>
      </view>

      <view v-else class="role-order-list">
        <view v-for="item in filteredSummaries" :key="item.key" class="role-product-summary-card">
          <view class="role-product-summary-main">
            <view class="role-product-summary-img"><RoleProductThumb :label="item.title" /></view>
            <view>
              <text class="role-product-title">{{ item.title }}</text>
              <text class="role-product-desc">{{ item.spec }}</text>
            </view>
          </view>
          <view class="role-product-summary-foot">
            <view>
              <text>{{ item.orders }}</text>
              <text>提货订单</text>
            </view>
            <view>
              <text>{{ item.qty }}</text>
              <text>合计数量</text>
            </view>
            <view>
              <text>¥{{ item.amount }}</text>
              <text>合计金额</text>
            </view>
          </view>
          <view class="role-order-actions">
            <button class="role-action-btn primary" @click.stop="openSummary(item.key)">查看订单用户</button>
          </view>
        </view>
        <view v-if="filteredSummaries.length === 0" class="empty-panel">
          <text class="title">暂无商品汇总</text>
        </view>
      </view>
    </template>

    <RoleBottomNav active="orders" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { pageStationOrderItems, pageStationOrders, pageSupplierDeliveries, pageSupplierPurchaseItems, type StationOrderDTO, type StationOrderItemDTO, type SupplierDeliveryDTO, type SupplierPurchaseItemDTO } from '@/api/station';
import { currentProfile, currentRole, getRequiredRoleQuery, goPage, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

interface DisplayOrder {
    id: number;
    no: string;
    time: string;
    pickupDate: string;
    status: string;
    statusClass: string;
    primary: string;
    mobile: string;
    location: string;
    qty: number;
    amount: string;
    searchText: string;
    raw: StationOrderDTO | SupplierDeliveryDTO;
}

interface ProductSummary {
    key: string;
    title: string;
    spec: string;
    qty: number;
    amount: string;
    orders: number;
    searchText: string;
}

interface WarehouseSummary {
    key: string;
    warehouseId?: number;
    title: string;
    deliveryCount: number;
    productCount: number;
    qty: number;
    amount: string;
    status: string;
    statusClass: string;
    actionNeed: string;
    nextText: string;
    searchText: string;
}

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const mode = ref<'order' | 'summary' | 'warehouse'>('order');
const keyword = ref('');
const activeDate = ref('');
const loading = ref(false);
const error = ref('');
const stationOrders = ref<StationOrderDTO[]>([]);
const stationItems = ref<StationOrderItemDTO[]>([]);
const supplierDeliveries = ref<SupplierDeliveryDTO[]>([]);
const supplierItems = ref<SupplierPurchaseItemDTO[]>([]);

const secondaryMode = computed(() => roleType.value === 'supplier' ? 'warehouse' : 'summary');
const secondaryLabel = computed(() => roleType.value === 'supplier' ? '仓库维度' : '商品汇总');

const orders = computed<DisplayOrder[]>(() => {
    if (roleType.value === 'supplier') {
        return supplierDeliveries.value.map((item) => ({
            id: item.id,
            no: item.deliveryNo || `送货单#${item.id}`,
            time: item.createTime || item.deliveryDate || '-',
            status: supplierDeliveryStatusText(item.status),
            pickupDate: item.deliveryDate || '-',
            statusClass: roleStatusClass(supplierDeliveryStatusText(item.status)),
            primary: item.warehouseId ? `仓库 #${item.warehouseId}` : '-',
            mobile: '',
            location: item.warehouseId ? `仓库 #${item.warehouseId}` : '-',
            qty: 0,
            amount: '0.00',
            searchText: [
                item.deliveryNo,
                item.warehouseId,
                item.deliveryDate,
                item.createTime,
                supplierDeliveryStatusText(item.status)
            ].join(' ').toLowerCase(),
            raw: item
        }));
    }
    return stationOrders.value.map((item) => {
        const orderItems = stationItems.value.filter((row) => row.orderId === item.id || (!!item.orderNo && row.orderNo === item.orderNo));
        const status = stationOrderStatusText(item, orderItems);
        const pickupDate = stationOrderPickupDate(item, orderItems);
        const productText = orderItems.map((row) => [row.productName, row.skuName, row.orderItemNo].join(' ')).join(' ');
        return {
            id: item.id,
            no: item.orderNo || `订单#${item.id}`,
            time: item.createTime || '-',
            pickupDate,
            status,
            statusClass: roleStatusClass(status),
            primary: item.pickupName || '-',
            mobile: item.pickupMobile || '',
            location: profile.value.entity || `自提点 #${item.stationId || '-'}`,
            qty: orderQty(orderItems),
            amount: orderAmount(item, orderItems),
            searchText: [
                item.orderNo,
                item.pickupName,
                item.pickupMobile,
                profile.value.entity,
                status,
                pickupDate,
                productText
            ].join(' ').toLowerCase(),
            raw: item
        };
    });
});

const dateOptions = computed(() => {
    const days = Array.from(new Set(orders.value.map((item) => item.pickupDate).filter((item) => item && item !== '-')));
    return ['全部提货日', ...days.sort((a, b) => b.localeCompare(a, 'zh-CN'))];
});

const defaultPickupDate = computed(() => dateOptions.value[1] || 'all');

const selectedPickupDate = computed(() => {
    if (roleType.value !== 'station') return 'all';
    if (!activeDate.value) return defaultPickupDate.value;
    if (activeDate.value === 'all') return 'all';
    return dateOptions.value.includes(activeDate.value) ? activeDate.value : defaultPickupDate.value;
});

const activeDateIndex = computed(() => {
    if (selectedPickupDate.value === 'all') return 0;
    const index = dateOptions.value.indexOf(selectedPickupDate.value);
    return index >= 0 ? index : 0;
});

const activeDateLabel = computed(() => dateOptions.value[activeDateIndex.value] || '全部提货日');

const placeholderText = computed(() => {
    if (roleType.value === 'supplier') return mode.value === 'warehouse' ? '搜索仓库、商品、送货单号' : '搜索送货单号、仓库、状态';
    return mode.value === 'summary' ? '搜索商品名称、规格、提货订单号、用户' : '搜索提货订单号、用户、手机号、商品';
});

const filteredOrders = computed(() => {
    const query = keyword.value.trim().toLowerCase();
    return dateFilteredOrders.value.filter((order) => {
        if (!query) return true;
        return order.searchText.includes(query);
    });
});

const dateFilteredOrders = computed(() => orders.value.filter((order) => {
    return roleType.value !== 'station' || selectedPickupDate.value === 'all' || order.pickupDate === selectedPickupDate.value;
}));

const summaries = computed<ProductSummary[]>(() => {
    if (roleType.value === 'supplier') {
        return [];
    }
    const allowedOrders = new Set(dateFilteredOrders.value.map((item) => item.no));
    const map = new Map<string, { key: string; title: string; spec: string; qty: number; amount: number; orders: Set<string>; users: Set<string> }>();
    stationItems.value.forEach((item) => {
        const order = stationOrders.value.find((rowOrder) => rowOrder.id === item.orderId || rowOrder.orderNo === item.orderNo);
        if (selectedPickupDate.value !== 'all' && stationItemPickupDate(item, order) !== selectedPickupDate.value) return;
        if (item.orderNo && allowedOrders.size && !allowedOrders.has(item.orderNo)) return;
        const displayOrderNo = item.orderNo || order?.orderNo || (item.orderId ? `订单#${item.orderId}` : '');
        if (allowedOrders.size && displayOrderNo && !allowedOrders.has(displayOrderNo)) return;
        const title = item.productName || `SKU #${item.skuId || item.id}`;
        const spec = item.skuName || '-';
        const key = productSummaryKey(title, spec);
        const row = map.get(key) || { key, title, spec, qty: 0, amount: 0, orders: new Set<string>(), users: new Set<string>() };
        row.qty += Number(item.qty || 0);
        row.amount += orderItemAmount(item);
        if (displayOrderNo) row.orders.add(displayOrderNo);
        if (order?.pickupName) row.users.add(order.pickupName);
        if (order?.pickupMobile) row.users.add(order.pickupMobile);
        map.set(key, row);
    });
    return Array.from(map.values()).map((item) => ({
        ...item,
        amount: item.amount.toFixed(2),
        orders: item.orders.size,
        searchText: [item.title, item.spec, ...Array.from(item.orders), ...Array.from(item.users)].join(' ').toLowerCase()
    }));
});

const filteredSummaries = computed(() => {
    const query = keyword.value.trim().toLowerCase();
    if (!query) return summaries.value;
    return summaries.value.filter((item) => item.searchText.includes(query));
});

const warehouses = computed<WarehouseSummary[]>(() => {
    const map = new Map<string, {
        key: string;
        warehouseId?: number;
        title: string;
        deliveries: SupplierDeliveryDTO[];
        productKeys: Set<string>;
        qty: number;
        amount: number;
        searchParts: string[];
    }>();

    supplierDeliveries.value.forEach((delivery) => {
        const key = warehouseSummaryKey(delivery.warehouseId);
        const row = map.get(key) || createWarehouseSummarySeed(delivery.warehouseId);
        row.deliveries.push(delivery);
        row.searchParts.push([
            delivery.deliveryNo,
            delivery.warehouseId,
            delivery.deliveryDate,
            delivery.createTime,
            supplierDeliveryStatusText(delivery.status)
        ].join(' '));
        map.set(key, row);
    });

    supplierItems.value.forEach((item) => {
        const key = warehouseSummaryKey(item.warehouseId);
        const row = map.get(key) || createWarehouseSummarySeed(item.warehouseId);
        const title = item.productName || `商品 #${item.productId || item.id}`;
        const spec = item.skuName || (item.skuId ? `SKU #${item.skuId}` : '-');
        row.productKeys.add(`${title}__${spec}`);
        row.qty += supplierItemQty(item);
        row.amount += supplierItemAmount(item);
        row.searchParts.push([
            title,
            spec,
            item.purchaseNo,
            item.purchaseId,
            item.warehouseId,
            supplierItemStatusText(item.status)
        ].join(' '));
        map.set(key, row);
    });

    return Array.from(map.values()).map((item) => {
        const status = warehouseStatusText(item.deliveries);
        const action = warehouseActionState(status, item.productKeys.size);
        return {
            key: item.key,
            warehouseId: item.warehouseId,
            title: item.title,
            deliveryCount: item.deliveries.length,
            productCount: item.productKeys.size,
            qty: item.qty,
            amount: formatMoneyValue(item.amount),
            status,
            statusClass: roleStatusClass(status),
            actionNeed: action.need,
            nextText: action.next,
            searchText: [item.title, ...item.searchParts].join(' ').toLowerCase()
        };
    });
});

const filteredWarehouses = computed(() => {
    const query = keyword.value.trim().toLowerCase();
    if (!query) return warehouses.value;
    return warehouses.value.filter((item) => item.searchText.includes(query));
});

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const query = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 100 };
        if (roleType.value === 'supplier') {
            const [deliveryPage, itemPage] = await Promise.all([
                pageSupplierDeliveries(query),
                pageSupplierPurchaseItems(query)
            ]);
            supplierDeliveries.value = deliveryPage.list || [];
            supplierItems.value = itemPage.list || [];
        } else {
            const [orderPage, itemPage] = await Promise.all([pageStationOrders(query), pageStationOrderItems(query)]);
            stationOrders.value = orderPage.list || [];
            stationItems.value = itemPage.list || [];
        }
    } catch (err) {
        error.value = friendlyErrorMessage(err, '订单加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function openDetail(id: number) {
    goPage(`/pages/orders/detail?id=${id}`);
}

function openSummary(key: string) {
    goPage(`/pages/orders/product-summary?key=${encodeURIComponent(key)}`);
}

function openWarehouse(item: WarehouseSummary) {
    const query = item.warehouseId ? `?warehouseId=${encodeURIComponent(String(item.warehouseId))}` : '';
    goPage(`/pages/store/warehouse-detail${query}`);
}

function setSecondaryMode() {
    mode.value = secondaryMode.value;
}

function changeDate(event: { detail?: { value?: number | string } }) {
    const index = Number(event.detail?.value || 0);
    activeDate.value = index <= 0 ? 'all' : dateOptions.value[index] || 'all';
}

function openNotifyPreview(order?: DisplayOrder) {
    const query = order?.no ? `?orderNo=${encodeURIComponent(order.no)}` : '';
    goPage(`/pages/store/notify-preview${query}`);
}

function copyMobile(order: DisplayOrder) {
    if (!order.mobile) {
        showRoleToast('当前订单没有手机号');
        return;
    }
    if (copyTextByInput(order.mobile)) {
        showRoleToast(`已复制手机号 ${order.mobile}`);
        return;
    }
    if (typeof navigator !== 'undefined' && navigator.clipboard?.writeText) {
        navigator.clipboard.writeText(order.mobile)
            .then(() => showRoleToast(`已复制手机号 ${order.mobile}`))
            .catch(() => copyTextByUni(order.mobile));
        return;
    }
    copyTextByUni(order.mobile);
}

function copyTextByUni(value: string) {
    uni.setClipboardData({
        data: value,
        success: () => showRoleToast(`已复制手机号 ${value}`),
        fail: () => showRoleToast(`请手动复制 ${value}`)
    });
}

function copyTextByInput(value: string) {
    if (typeof document === 'undefined' || !document.body) return false;
    const input = document.createElement('textarea');
    input.value = value;
    input.readOnly = true;
    input.style.position = 'fixed';
    input.style.left = '-9999px';
    input.style.top = '0';
    input.style.opacity = '0';
    document.body.appendChild(input);
    input.focus();
    input.select();
    input.setSelectionRange(0, value.length);
    try {
        return document.execCommand('copy');
    } catch {
        return false;
    } finally {
        document.body.removeChild(input);
    }
}

function callUser(order: DisplayOrder) {
    if (!order.mobile) {
        showRoleToast('当前订单没有手机号');
        return;
    }
    uni.makePhoneCall({
        phoneNumber: order.mobile,
        fail: () => showRoleToast(`请手动拨打 ${order.mobile}`)
    });
}

function orderQty(items: StationOrderItemDTO[]) {
    return items.reduce((sum, item) => sum + Number(item.qty || 0), 0);
}

function orderAmount(order: StationOrderDTO, items: StationOrderItemDTO[]) {
    const itemAmount = items.reduce((sum, item) => sum + orderItemAmount(item), 0);
    return formatMoneyValue(itemAmount || order.payAmount || order.totalAmount);
}

function orderItemAmount(item: StationOrderItemDTO) {
    const directAmount = Number(item.itemAmount || 0);
    if (directAmount) return directAmount;
    return Number(item.salePrice || 0) * Number(item.qty || 0);
}

function supplierItemQty(item: SupplierPurchaseItemDTO) {
    return Number(item.purchaseQty || item.applyQty || item.receivedQty || 0);
}

function supplierItemAmount(item: SupplierPurchaseItemDTO) {
    const directAmount = Number(item.amount || 0);
    if (directAmount) return directAmount;
    return Number(item.purchasePrice || 0) * supplierItemQty(item);
}

function formatMoneyValue(value: string | number | undefined) {
    const amount = Number(value || 0);
    return Number.isFinite(amount) ? amount.toFixed(2) : String(value || '0.00');
}

function warehouseSummaryKey(warehouseId?: number) {
    return warehouseId ? `warehouse_${warehouseId}` : 'warehouse_unknown';
}

function createWarehouseSummarySeed(warehouseId?: number) {
    return {
        key: warehouseSummaryKey(warehouseId),
        warehouseId,
        title: warehouseId ? `仓库 #${warehouseId}` : '未返回仓库',
        deliveries: [] as SupplierDeliveryDTO[],
        productKeys: new Set<string>(),
        qty: 0,
        amount: 0,
        searchParts: [] as string[]
    };
}

function warehouseStatusText(deliveries: SupplierDeliveryDTO[]) {
    if (deliveries.length === 0) return '商品汇总';
    const statuses = deliveries.map((item) => supplierDeliveryStatusText(item.status));
    if (statuses.some((item) => /等待仓库确认|送货中|待送货|等待处理/.test(item))) return '处理中';
    if (statuses.every((item) => /已取消/.test(item))) return '已取消';
    if (statuses.some((item) => /仓库已确认|已完成/.test(item))) return '已完成';
    return statuses[0] || '等待处理';
}

function warehouseActionState(status: string, productCount: number) {
    if (productCount === 0) return { need: '暂无商品明细', next: '当前仓库仅有送货单记录，可刷新后查看商品汇总。' };
    if (/已完成/.test(status)) return { need: '无需我处理', next: '可查看该仓库商品汇总和送货单记录。' };
    if (/已取消/.test(status)) return { need: '无需我处理', next: '送货单已取消，可查看历史记录。' };
    return { need: '按当前单据处理', next: '查看仓库商品汇总，并跟进入仓确认进度。' };
}

function supplierItemStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待处理',
        20: '已确认',
        30: '已收货',
        40: '已完成',
        50: '已取消'
    };
    return map[Number(status || 0)] || '等待处理';
}

function productSummaryKey(title: string, spec: string) {
    return `${title}__${spec}`;
}

function stationOrderPickupDate(order: StationOrderDTO, items: StationOrderItemDTO[]) {
    const fromItem = items.find((item) => item.expectedPickupDate)?.expectedPickupDate;
    return formatDateText(fromItem || order.createTime || '-');
}

function stationItemPickupDate(item: StationOrderItemDTO, order?: StationOrderDTO) {
    return formatDateText(item.expectedPickupDate || order?.createTime || '-');
}

function formatDateText(value?: string) {
    if (!value || value === '-') return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0];
}

function stationOrderStatusText(order: StationOrderDTO, items: StationOrderItemDTO[]) {
    const trade = Number(order.tradeStatus || 0);
    const pay = Number(order.payStatus || 0);
    const statuses = [order.fulfillStatus, ...items.map((item) => item.fulfillStatus)]
        .filter((item) => item !== undefined && item !== null)
        .map(Number)
        .filter((item) => Number.isFinite(item));
    const maxFulfill = statuses.length ? Math.max(...statuses) : 0;
    const afterSaleStatuses = items.map((item) => Number(item.afterSaleStatus || 0)).filter((item) => item > 0);

    const tradeText = stationTradeStatusText(trade);
    if (tradeText === '已关闭' || tradeText === '已取消') return tradeText;
    if (afterSaleStatuses.some((item) => item >= 10 && item <= 30)) return '待售后确认';
    if (tradeText === '已完成') return '已完成';
    if (statuses.length > 0 && statuses.every((item) => item >= 70)) return '已完成';
    if (maxFulfill >= 60) return '待提货';
    if (maxFulfill >= 30 || pay >= 30 || tradeText === '已支付') return '待到货';
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

function supplierDeliveryStatusText(status?: number) {
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

function roleStatusClass(status: string) {
    if (/已完成|已自提|已发送|已到账|已通过|仓库已确认|已结算/.test(status)) return 'green';
    if (/待提货|待售后确认/.test(status)) return 'blue';
    if (/未发货|待到货|待配送|等待|处理中|已提交|待处理|待审核|待确认|今日需处理/.test(status)) return 'orange';
    if (/驳回|拒绝|失败|停用|不可|已取消|已关闭|已退款/.test(status)) return 'gray';
    return 'orange';
}

</script>

<style lang="scss" scoped>
.role-work-title {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 14rpx;
}

.role-work-title-main {
  display: block;
  color: #ffffff;
  font-size: 42rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-work-sub {
  display: block;
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-query-tabs {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0;
  width: 100%;
  min-height: 86rpx;
  margin: -4rpx 0 14rpx;
  padding: 8rpx;
  background: rgba(255, 248, 241, 0.98);
  border: 1rpx solid rgba(245, 193, 163, 0.9);
  border-radius: 34rpx;
  box-shadow: 0 10rpx 24rpx rgba(113, 69, 37, 0.06), inset 0 1rpx 0 rgba(255, 255, 255, 0.95);
}

.role-query-tabs button,
.role-query-tabs uni-button {
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  width: 100%;
  min-width: 0;
  min-height: 70rpx;
  margin: 0;
  padding: 0 18rpx;
  color: #54372a;
  background: transparent;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1;
  white-space: nowrap;
  box-shadow: none;
}

.role-query-tabs button::after,
.role-query-tabs uni-button::after {
  border: 0;
}

.role-query-tabs button.active,
.role-query-tabs uni-button.active {
  color: #ffffff;
  background: linear-gradient(135deg, #e85a3c 0%, #f27643 100%);
  box-shadow: 0 12rpx 24rpx rgba(232, 90, 60, 0.24), inset 0 1rpx 0 rgba(255, 255, 255, 0.24);
}

.role-order-query-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 224rpx;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 16rpx;
}

.role-order-query-row.single {
  grid-template-columns: minmax(0, 1fr);
}

.role-order-search,
.role-order-date {
  min-height: 78rpx;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(60, 33, 16, 0.05), inset 0 1rpx 0 rgba(255, 255, 255, 0.85);
}

.role-order-search {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: center;
  gap: 12rpx;
  padding: 0 24rpx;
}

.role-order-search text {
  color: #d94b34;
  font-size: 24rpx;
  font-weight: 900;
}

.role-order-search input {
  min-height: 76rpx;
  padding: 0;
  background: transparent;
  border: 0;
}

.role-date-trigger {
  position: relative;
  display: flex;
  align-items: center;
  min-height: 78rpx;
  padding: 0 60rpx 0 24rpx;
  color: #2f241f;
  font-size: 24rpx;
  font-weight: 900;
}

.role-date-trigger::before {
  content: "";
  position: absolute;
  right: 30rpx;
  top: calc(50% - 2rpx);
  width: 20rpx;
  height: 4rpx;
  border-radius: 999rpx;
  background: #df613f;
}

.role-date-trigger::after {
  content: "";
  position: absolute;
  right: 24rpx;
  top: 50%;
  width: 30rpx;
  height: 30rpx;
  border: 3rpx solid #df613f;
  border-radius: 10rpx;
  transform: translateY(-50%);
}

.role-filter-clear,
uni-button.role-filter-clear {
  min-height: 58rpx;
  padding: 0 24rpx;
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-order-list {
  display: grid;
  gap: 24rpx;
}

.role-order-card,
.role-product-summary-card {
  padding: 24rpx;
  border-radius: 32rpx;
}

.role-order-head,
.role-order-actions,
.role-order-info view {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.role-order-head {
  margin-bottom: 20rpx;
}

.role-order-no {
  display: block;
  margin-bottom: 8rpx;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.3;
}

.role-order-head text:last-child:not(.role-status) {
  color: #8b6a57;
  font-size: 24rpx;
}

.role-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44rpx;
  padding: 0 16rpx;
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

.role-order-summary,
.role-product-summary-foot {
  display: grid;
  gap: 12rpx;
}

.role-order-summary {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin: 12rpx 0 10rpx;
}

.role-product-summary-foot {
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-top: 20rpx;
}

.role-order-summary view,
.role-product-summary-foot view {
  padding: 14rpx;
  color: #8b6a57;
  background: #fff8f2;
  border-radius: 22rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-order-summary text:first-child {
  display: block;
  margin-bottom: 4rpx;
  color: #3a2c24;
  font-size: 28rpx;
  font-weight: 900;
}

.role-product-summary-foot text:first-child {
  display: block;
  margin-bottom: 4rpx;
  color: #d94b34;
  font-size: 28rpx;
  font-weight: 900;
}

.role-status-note {
  margin: 0 0 18rpx;
  padding: 16rpx 18rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 22rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-status-note text {
  display: block;
}

.role-status-note text:not(:last-child) {
  margin-bottom: 6rpx;
}

.role-status-note text:first-child,
.role-order-need {
  color: #3a2c24;
  font-weight: 900;
}

.role-order-info {
  display: grid;
  gap: 12rpx;
  padding: 18rpx 0;
  border-top: 1rpx solid #f1e1d6;
  border-bottom: 1rpx solid #f1e1d6;
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

.role-order-actions {
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 18rpx;
}

.role-action-btn,
uni-button.role-action-btn {
  flex: 0 1 auto;
  min-height: 60rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
  line-height: 60rpx;
  white-space: nowrap;
}

.role-action-btn.primary,
uni-button.role-action-btn.primary {
  min-width: 150rpx;
}

.role-action-btn.primary,
uni-button.role-action-btn.primary {
  color: #ffffff;
  background: #e85d3f;
}

.role-action-btn.soft,
uni-button.role-action-btn.soft {
  color: #b85a2f;
  background: #fff3ea;
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

.role-product-summary-foot text:first-child {
  font-size: 26rpx;
}

.retry-btn {
  margin-top: 18rpx;
}

@media screen and (max-width: 360px) {
  .role-order-query-row {
    grid-template-columns: minmax(0, 1fr);
  }

  .role-order-actions {
    gap: 12rpx;
  }

  .role-action-btn {
    padding: 0 18rpx;
  }
}
</style>
