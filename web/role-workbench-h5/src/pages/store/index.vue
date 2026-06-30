<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '供应商商品' : '门店作业' }}</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="role-work-title-main">{{ roleType === 'supplier' ? '供应商商品' : '自提点履约' }}</text>
          <text class="role-work-sub">{{ roleType === 'supplier' ? '选择自身 SKU 创建采购单' : '配送商品、退货和缺货查询' }}</text>
        </view>
        <text class="role-work-entity">{{ profile.entity }}</text>
      </view>
    </view>

    <view class="role-main">
      <view v-if="loading" class="role-empty">
        <text class="title">正在加载作业数据</text>
        <text class="subtle">正在同步配送、退货和缺货数据...</text>
      </view>
      <view v-else-if="error" class="role-empty">
        <text class="title">作业数据加载失败</text>
        <text class="subtle">{{ error }}</text>
        <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
      </view>

      <template v-else>
        <template v-if="roleType === 'supplier'">
          <view class="role-sales-tools">
            <view class="role-sales-tool-row single">
              <view class="role-sales-search">
                <text>🔍</text>
                <input v-model="keyword" placeholder="搜索商品名称" />
              </view>
            </view>
            <view class="role-sales-sort">
              <view class="role-sales-segment">
                <text>排序</text>
                <button :class="{ active: supplierSortKey === 'sold' }" @click="supplierSortKey = 'sold'">销量</button>
                <button :class="{ active: supplierSortKey === 'price' }" @click="supplierSortKey = 'price'">价格</button>
              </view>
              <view class="role-sales-segment">
                <text>顺序</text>
                <button :class="{ active: supplierSortOrder === 'desc' }" @click="supplierSortOrder = 'desc'">降序</button>
                <button :class="{ active: supplierSortOrder === 'asc' }" @click="supplierSortOrder = 'asc'">升序</button>
              </view>
            </view>
          </view>

          <view class="role-section">
            <view class="role-section-head">
              <view>
                <text class="title">供应商商品</text>
                <text class="subtle">选择自身 SKU 创建采购单</text>
              </view>
            </view>

            <view class="role-product-send-list">
              <view v-for="item in filteredSupplierProducts" :key="item.key" class="role-product-send-card" :class="{ selected: supplierDraftQty(item.key) > 0 }">
                <view class="role-sales-main">
                  <view class="role-sales-img"><RoleProductThumb :label="item.title" /></view>
                  <view>
                    <view class="role-sales-title">
                      <text class="role-sales-name">{{ item.title }}</text>
                      <text class="role-sales-tag">{{ item.status }}</text>
                    </view>
                    <text class="role-sales-desc">{{ item.spec }}</text>
                  </view>
                  <button class="role-product-select" @click.stop="toggleSupplierProduct(item)">
                    {{ supplierDraftQty(item.key) > 0 ? '已选' : '选择' }}
                  </button>
                </view>
                <view class="role-sales-metrics">
                  <view>
                    <text>{{ item.purchaseQty }}</text>
                    <text>销量</text>
                  </view>
                  <view>
                    <text>¥{{ item.price }}</text>
                    <text>销售价</text>
                  </view>
                  <view>
                    <text>¥{{ item.amount }}</text>
                    <text>销售额</text>
                  </view>
                </view>
                <view class="role-product-send-form">
                  <label>
                    <text>送货数量</text>
                    <input
                      type="number"
                      :value="supplierDraftQty(item.key) || ''"
                      placeholder="0"
                      @input="changeSupplierDraftQty(item, $event)"
                    />
                  </label>
                  <button class="role-action-btn soft" @click.stop="toggleSupplierProduct(item)">
                    {{ supplierDraftQty(item.key) > 0 ? '取消' : '加入' }}
                  </button>
                </view>
              </view>
              <view v-if="filteredSupplierProducts.length === 0" class="role-empty inline-empty">
                <text class="title">当前条件暂无在售商品数据</text>
              </view>
            </view>

            <view class="role-product-submit-bar">
              <view>
                <text class="role-product-submit-title">已选 {{ supplierDraftSummary.count }} 种 / {{ supplierDraftSummary.qty }} 件</text>
                <text>预估供货金额 ¥{{ supplierDraftSummary.amount }}，请确认商品和数量后提交供货申请。</text>
              </view>
              <button class="role-action-btn primary" :disabled="supplierDraftSummary.qty <= 0" @click="submitSupplierDraft">提交供货申请</button>
            </view>
          </view>
        </template>

        <template v-else>
        <view class="role-delivery-tools">
          <view class="role-delivery-tabs">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              :class="{ active: activeTab === tab.value }"
              @click="activeTab = tab.value"
            >
              {{ tab.label }}
            </button>
          </view>

          <view class="role-delivery-query-row">
            <view class="role-order-search role-delivery-search">
              <text>🔍</text>
              <input v-model="keyword" :placeholder="placeholderText" />
            </view>
            <view v-if="roleType === 'station'" class="role-delivery-date">
              <picker mode="selector" :range="dateOptions" :value="activeDateIndex" @change="changeDate">
                <view class="role-date-trigger">
                  <text class="role-delivery-date-icon">日</text>
                  <text>{{ activeDateLabel }}</text>
                </view>
              </picker>
            </view>
            <button v-else class="role-filter-clear" @click="clearKeyword">清空</button>
          </view>

          <view v-if="roleType === 'station' && activeTab === 'arrival'" class="role-delivery-toggle">
            <button :class="{ active: !onlyShortageDelivery }" @click="onlyShortageDelivery = false">全部商品</button>
            <button :class="{ active: onlyShortageDelivery }" @click="onlyShortageDelivery = true">只看缺货</button>
          </view>
        </view>

        <view class="role-delivery-stats role-delivery-stats-compact">
          <view v-for="item in stats" :key="item.label" class="role-delivery-stat">
            <b>{{ item.value }}</b>
            <text>{{ item.label }}</text>
          </view>
        </view>

          <view v-if="activeTab === 'arrival'" class="role-arrival-actions">
            <button class="role-shortage-entry" @click="open('/pages/store/notify-preview')">发送到货通知</button>
            <button class="role-shortage-entry" @click="openStationShortage()">标记异常</button>
          </view>
          <view v-if="activeTab === 'arrival' && firstConfirmableStation" class="role-arrival-confirm-strip">
            <text>到站记录 #{{ firstConfirmableStation.id }} 待确认</text>
            <button class="role-arrival-confirm-btn" @click="confirmArrival(firstConfirmableStation.id)">确认到货</button>
          </view>

          <view class="role-section">
            <view class="role-section-head">
              <view>
                <text class="title">{{ stationPanelTitle }}</text>
                <text class="subtle">{{ stationPanelSubTitle }}</text>
              </view>
            </view>

            <view v-if="activeTab === 'arrival'" class="role-delivery-list">
              <view
                v-for="item in filteredDeliveryProducts"
                :key="item.key"
                class="role-delivery-product"
                :class="{ short: item.lack > 0 }"
                @click="openDeliveryProduct(item)"
              >
                <view class="role-delivery-product-main">
                  <view class="role-delivery-img"><RoleProductThumb :label="item.title" /></view>
                  <view>
                    <view class="role-delivery-title-row">
                      <text class="role-delivery-title">{{ item.title }}</text>
                      <text class="role-status" :class="item.statusClass">{{ item.status }}</text>
                    </view>
                    <text class="role-delivery-desc">{{ item.spec }}</text>
                    <view class="role-delivery-counts">
                      <view>
                        <text>应到</text>
                        <text>{{ item.expected }}</text>
                      </view>
                      <view>
                        <text>实到</text>
                        <text>{{ item.actual }}</text>
                      </view>
                      <view>
                        <text>用户</text>
                        <text>{{ item.userCount }}</text>
                      </view>
                    </view>
                  </view>
                </view>
                <view v-if="item.lack > 0" class="role-short-tip product-short-tip">
                  <text>缺货 {{ item.lack }} 件，实到 {{ item.actual }} / 应到 {{ item.expected }}</text>
                  <button class="role-shortage-entry" @click.stop="openStationShortage(item)">去标记</button>
                </view>
                <view class="role-order-actions">
                  <button class="role-action-btn soft" @click.stop="openDeliveryProduct(item)">查看用户</button>
                  <button v-if="item.firstStationId" class="role-action-btn soft" @click.stop="open(`/pages/store/delivery-detail?id=${item.firstStationId}`)">到站记录</button>
                </view>
              </view>
              <view v-if="filteredDeliveryProducts.length === 0" class="role-empty inline-empty">
                <text class="title">暂无配送商品</text>
                <text class="subtle">当前暂无匹配的订单商品数据</text>
              </view>
            </view>

            <view v-else-if="activeTab === 'returns'" class="role-delivery-list">
              <view
                v-for="item in filteredReturns"
                :key="item.id"
                class="role-return-card"
                @click="open(`/pages/store/return-detail?id=${item.id}`)"
              >
                <view class="role-return-main">
                  <view class="role-delivery-img role-return-img" aria-hidden="true">
                    <svg viewBox="0 0 36 36" fill="none">
                      <path d="M10.5 11.5h12.8c3.6 0 6.5 2.9 6.5 6.5s-2.9 6.5-6.5 6.5H12" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M14 7.8 8.5 13.3 14 18.8" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M8.8 13.3h14" stroke="currentColor" stroke-width="2.6" stroke-linecap="round"/>
                      <path d="M13 24.5h8.2" stroke="currentColor" stroke-width="2.6" stroke-linecap="round"/>
                    </svg>
                  </view>
                  <view>
                    <text class="role-delivery-title">{{ item.returnNo || `退货 #${item.id}` }}</text>
                    <text class="role-delivery-desc">配送单 #{{ item.deliveryId || '-' }} · 仓库 #{{ item.warehouseId || '-' }}</text>
                    <view class="role-return-order">配送单 #{{ item.deliveryId || '-' }} · SKU #{{ item.skuId || '-' }}</view>
                    <view class="role-return-meta">
                      <text class="role-return-state" :class="roleStatusClass(returnStatusText(item.status))">{{ returnStatusText(item.status) }}</text>
                      <text v-if="returnHasMismatch(item)" class="role-return-state orange">数量不一致</text>
                      <text>数量 {{ item.returnQty || 0 }}</text>
                    </view>
                    <view class="role-return-qtys">
                      <view>
                        <text>退货</text>
                        <text>{{ item.returnQty || 0 }}</text>
                      </view>
                      <view>
                        <text>取回</text>
                        <text>{{ returnPickupQty(item) }}</text>
                      </view>
                      <view>
                        <text>返仓</text>
                        <text>{{ returnWarehouseQty(item) }}</text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
              <view v-if="filteredReturns.length === 0" class="role-empty inline-empty">
                <text class="title">暂无退货交接</text>
                <text class="subtle">当前暂无匹配的退货数据</text>
              </view>
            </view>

          </view>
        </template>
      </template>
    </view>

    <RoleBottomNav active="delivery" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import {
    confirmStationArrival,
    pageDeliveryStations,
    pageExceptions,
    pageReturnHandovers,
    pageStationOrderItems,
    pageStationOrders,
    pageSupplierDeliveries,
    pageSupplierPurchaseItems,
    pageSupplierPurchases,
    type DeliveryStationDTO,
    type ReturnHandoverDTO,
    type StationExceptionDTO,
    type StationOrderDTO,
    type StationOrderItemDTO,
    type SupplierDeliveryDTO,
    type SupplierPurchaseItemDTO,
    type SupplierPurchaseDTO
} from '@/api/station';
import { confirmAction, currentProfile, currentRole, getRequiredRoleQuery, goPage, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

type StationTab = 'arrival' | 'returns' | 'exception';
type SupplierTab = 'purchase' | 'delivery';
type ActiveTab = StationTab | SupplierTab;
type SupplierSortKey = 'sold' | 'price';
type SupplierSortOrder = 'desc' | 'asc';

interface SupplierProductRow {
    key: string;
    title: string;
    spec: string;
    initial: string;
    status: string;
    purchaseQty: number;
    amountValue: number;
    priceValue: number;
    amount: string;
    price: string;
    purchaseCount: number;
    warehouseCount: number;
    latestTime: string;
    searchText: string;
}

interface DeliveryProductRow {
    key: string;
    title: string;
    spec: string;
    initial: string;
    expected: number;
    actual: number;
    lack: number;
    userCount: number;
    status: string;
    statusClass: string;
    firstStationId?: number;
    searchText: string;
}

const roleType = computed(() => currentRole.value);
const profile = computed(() => currentProfile.value);
const activeTab = ref<ActiveTab>('arrival');
const keyword = ref('');
const activeDate = ref('all');
const onlyShortageDelivery = ref(false);
const supplierSortKey = ref<SupplierSortKey>('sold');
const supplierSortOrder = ref<SupplierSortOrder>('desc');
const supplierDraft = ref<Record<string, number>>({});
const loading = ref(false);
const error = ref('');
const deliveryStations = ref<DeliveryStationDTO[]>([]);
const returns = ref<ReturnHandoverDTO[]>([]);
const exceptions = ref<StationExceptionDTO[]>([]);
const stationOrders = ref<StationOrderDTO[]>([]);
const stationItems = ref<StationOrderItemDTO[]>([]);
const purchases = ref<SupplierPurchaseDTO[]>([]);
const supplierItems = ref<SupplierPurchaseItemDTO[]>([]);
const supplierDeliveries = ref<SupplierDeliveryDTO[]>([]);

const tabs = computed(() => roleType.value === 'supplier'
    ? [
        { label: '采购单', value: 'purchase' as ActiveTab },
        { label: '送货单', value: 'delivery' as ActiveTab }
    ]
    : [
        { label: '配送商品', value: 'arrival' as ActiveTab },
        { label: '退货数据', value: 'returns' as ActiveTab }
    ]);

const placeholderText = computed(() => {
    if (roleType.value === 'supplier') {
        return activeTab.value === 'purchase' ? '搜索采购单号、仓库、日期' : '搜索送货单号、仓库、日期';
    }
    if (activeTab.value === 'arrival') return '搜索商品、规格、用户、订单号';
    if (activeTab.value === 'returns') return '搜索退货单、配送单、仓库、备注';
    return '搜索异常标题、编号、描述';
});

const dateOptions = computed(() => {
    const source = activeTab.value === 'returns'
        ? returns.value.map((item) => item.confirmTime || item.createTime)
        : stationItems.value.map((item) => item.expectedPickupDate);
    const days = Array.from(new Set(source.map(formatDateOption).filter((item) => item && item !== '-')));
    return ['全部日期', ...days.sort((a, b) => b.localeCompare(a, 'zh-CN'))];
});

const activeDateIndex = computed(() => {
    if (activeDate.value === 'all') return 0;
    const index = dateOptions.value.indexOf(activeDate.value);
    return index >= 0 ? index : 0;
});

const activeDateLabel = computed(() => dateOptions.value[activeDateIndex.value] || '全部日期');

const filteredDeliveryStations = computed(() => {
    const query = normalize(keyword.value);
    return deliveryStations.value.filter((item) => {
        if (!matchActiveDate(item.arriveTime || item.createTime)) return false;
        if (!query) return true;
        return normalize([
            item.id,
            item.deliveryId,
            item.deliverySort,
            deliveryStatusText(item.status),
            item.arriveTime,
            item.createTime
        ].join(' ')).includes(query);
    });
});

const firstConfirmableStation = computed(() => filteredDeliveryStations.value.find(canConfirmArrival));

const deliveryProducts = computed<DeliveryProductRow[]>(() => {
    const stationHint = firstConfirmableStation.value || filteredDeliveryStations.value[0];
    const stationId = stationHint?.id;
    const map = new Map<string, {
        key: string;
        title: string;
        spec: string;
        expected: number;
        actual: number;
        orders: Set<string>;
        users: Set<string>;
        searchParts: string[];
    }>();

    stationItems.value.forEach((item) => {
        if (!matchActiveDate(item.expectedPickupDate)) return;
        const title = item.productName || `SKU #${item.skuId || item.id}`;
        const spec = item.skuName || '-';
        const key = `${title}__${spec}`;
        const row = map.get(key) || {
            key,
            title,
            spec,
            expected: 0,
            actual: 0,
            orders: new Set<string>(),
            users: new Set<string>(),
            searchParts: []
        };
        const qty = Number(item.qty || 0);
        row.expected += qty;
        row.actual += deliveryActualQty(item);
        const order = stationOrders.value.find((rowOrder) => rowOrder.id === item.orderId || rowOrder.orderNo === item.orderNo);
        const orderNo = item.orderNo || order?.orderNo || (item.orderId ? `订单#${item.orderId}` : '');
        if (orderNo) row.orders.add(orderNo);
        if (order?.pickupName) row.users.add(order.pickupName);
        if (order?.pickupMobile) row.users.add(order.pickupMobile);
        row.searchParts.push([
            title,
            spec,
            orderNo,
            order?.pickupName,
            order?.pickupMobile,
            item.expectedPickupDate,
            stationItemStatusText(item)
        ].join(' '));
        map.set(key, row);
    });

    return Array.from(map.values()).map((item) => {
        const lack = Math.max(0, item.expected - item.actual);
        const status = deliveryProductStatus(item.expected, item.actual);
        return {
            key: item.key,
            title: item.title,
            spec: item.spec,
            initial: item.title.slice(0, 1) || '商',
            expected: item.expected,
            actual: item.actual,
            lack,
            userCount: item.users.size || item.orders.size,
            status,
            statusClass: roleStatusClass(status),
            firstStationId: stationId,
            searchText: [item.title, item.spec, ...item.searchParts].join(' ').toLowerCase()
        };
    });
});

const filteredDeliveryProducts = computed(() => {
    const query = normalize(keyword.value);
    return deliveryProducts.value.filter((item) => {
        if (onlyShortageDelivery.value && item.lack <= 0) return false;
        if (!query) return true;
        return item.searchText.includes(query);
    });
});

const filteredReturns = computed(() => {
    const query = normalize(keyword.value);
    return returns.value.filter((item) => {
        if (!matchActiveDate(item.confirmTime || item.createTime)) return false;
        if (!query) return true;
        return normalize([
            item.returnNo,
            item.deliveryId,
            item.warehouseId,
            item.returnQty,
            returnStatusText(item.status),
            item.remark,
            item.createTime
        ].join(' ')).includes(query);
    });
});

const filteredExceptions = computed(() => {
    const query = normalize(keyword.value);
    return exceptions.value.filter((item) => {
        if (!query) return true;
        return normalize([
            item.exceptionNo,
            item.title,
            item.description,
            item.sourceModule,
            exceptionStatusText(item.status),
            item.createTime
        ].join(' ')).includes(query);
    });
});

const filteredPurchases = computed(() => {
    const query = normalize(keyword.value);
    return purchases.value.filter((item) => {
        if (!query) return true;
        return normalize([
            item.purchaseNo,
            item.warehouseId,
            item.deliveryDate,
            item.expectedArrivalTime,
            item.createReason,
            supplierPurchaseStatusText(item),
            supplierAuditStatusText(item.auditStatus)
        ].join(' ')).includes(query);
    });
});

const filteredSupplierDeliveries = computed(() => {
    const query = normalize(keyword.value);
    return supplierDeliveries.value.filter((item) => {
        if (!query) return true;
        return normalize([
            item.deliveryNo,
            item.warehouseId,
            item.deliveryDate,
            item.createTime,
            supplierDeliveryStatusText(item.status)
        ].join(' ')).includes(query);
    });
});

const supplierProducts = computed<SupplierProductRow[]>(() => {
    const map = new Map<string, {
        key: string;
        title: string;
        spec: string;
        purchaseQty: number;
        amountValue: number;
        prices: number[];
        purchases: Set<string>;
        warehouses: Set<string>;
        latestTime: string;
        status: string;
        searchText: string[];
    }>();

    supplierItems.value.forEach((item) => {
        const title = item.productName || `商品 #${item.productId || item.id}`;
        const spec = item.skuName || (item.skuId ? `SKU #${item.skuId}` : '-');
        const key = `${item.productId || title}-${item.skuId || spec}`;
        const purchaseQty = Number(item.purchaseQty || 0);
        const price = Number(item.purchasePrice || 0);
        const amount = Number(item.amount || 0) || purchaseQty * price;
        const current = map.get(key) || {
            key,
            title,
            spec,
            purchaseQty: 0,
            amountValue: 0,
            prices: [],
            purchases: new Set<string>(),
            warehouses: new Set<string>(),
            latestTime: '-',
            status: supplierItemStatusText(undefined),
            searchText: []
        };
        current.purchaseQty += purchaseQty;
        current.amountValue += amount;
        if (price > 0) current.prices.push(price);
        if (item.purchaseId) current.purchases.add(String(item.purchaseId));
        if (item.warehouseId) current.warehouses.add(String(item.warehouseId));
        if (isLaterTime(item.createTime, current.latestTime)) current.latestTime = formatDateTime(item.createTime);
        current.status = supplierItemStatusText(item.status);
        current.searchText.push([
            title,
            spec,
            item.purchaseId,
            item.productId,
            item.skuId,
            item.warehouseId,
            item.createTime,
            current.status
        ].join(' '));
        map.set(key, current);
    });

    return Array.from(map.values()).map((item) => {
        const priceValue = item.prices.length
            ? item.prices.reduce((sum, value) => sum + value, 0) / item.prices.length
            : (item.purchaseQty ? item.amountValue / item.purchaseQty : 0);
        return {
            key: item.key,
            title: item.title,
            spec: item.spec,
            initial: item.title.slice(0, 1) || '商',
            status: item.status,
            purchaseQty: item.purchaseQty,
            amountValue: item.amountValue,
            priceValue,
            amount: formatAmount(item.amountValue),
            price: formatAmount(priceValue),
            purchaseCount: item.purchases.size,
            warehouseCount: item.warehouses.size,
            latestTime: item.latestTime,
            searchText: item.searchText.join(' ').toLowerCase()
        };
    });
});

const filteredSupplierProducts = computed(() => {
    const query = normalize(keyword.value);
    const rows = supplierProducts.value.filter((item) => !query || item.searchText.includes(query));
    const direction = supplierSortOrder.value === 'desc' ? -1 : 1;
    const key = supplierSortKey.value;
    return [...rows].sort((a, b) => {
        const left = key === 'sold' ? a.purchaseQty : a.priceValue;
        const right = key === 'sold' ? b.purchaseQty : b.priceValue;
        return (left - right) * direction;
    });
});

const supplierProductStats = computed(() => {
    const rows = filteredSupplierProducts.value;
    const qty = rows.reduce((sum, item) => sum + item.purchaseQty, 0);
    const amount = rows.reduce((sum, item) => sum + item.amountValue, 0);
    const avgPrice = qty ? amount / qty : 0;
    return {
        count: rows.length,
        qty,
        avgPrice: formatAmount(avgPrice),
        amount: formatAmount(amount)
    };
});

const supplierDraftSummary = computed(() => {
    return supplierProducts.value.reduce((summary, item) => {
        const qty = supplierDraftQty(item.key);
        if (qty <= 0) return summary;
        summary.count += 1;
        summary.qty += qty;
        summary.amountValue += qty * item.priceValue;
        return summary;
    }, {
        count: 0,
        qty: 0,
        amountValue: 0,
        get amount() {
            return formatAmount(this.amountValue);
        }
    });
});

const stats = computed(() => {
    if (roleType.value === 'supplier') {
        if (activeTab.value === 'purchase') {
            return [
                { label: '采购单', value: filteredPurchases.value.length },
                { label: '待处理', value: filteredPurchases.value.filter((item) => ![60, 70].includes(Number(item.purchaseStatus || 0))).length },
                { label: '仓库数', value: new Set(filteredPurchases.value.map((item) => item.warehouseId).filter(Boolean)).size }
            ];
        }
        return [
            { label: '送货单', value: filteredSupplierDeliveries.value.length },
            { label: '处理中', value: filteredSupplierDeliveries.value.filter((item) => ![60, 70].includes(Number(item.status || 0))).length },
            { label: '仓库数', value: new Set(filteredSupplierDeliveries.value.map((item) => item.warehouseId).filter(Boolean)).size }
        ];
    }
    if (activeTab.value === 'returns') {
        return [
            { label: '退货', value: filteredReturns.value.reduce((sum, item) => sum + Number(item.returnQty || 0), 0) },
            { label: '交接记录', value: filteredReturns.value.length },
            { label: '待确认', value: filteredReturns.value.filter((item) => Number(item.status || 0) !== 20).length }
        ];
    }
    const expected = filteredDeliveryProducts.value.reduce((sum, item) => sum + item.expected, 0);
    const actual = filteredDeliveryProducts.value.reduce((sum, item) => sum + item.actual, 0);
    return [
        { label: '配送商品', value: filteredDeliveryProducts.value.length },
        { label: '实到 / 应到', value: `${actual}/${expected}` },
        { label: '缺货数量', value: filteredDeliveryProducts.value.reduce((sum, item) => sum + item.lack, 0) }
    ];
});

const stationPanelTitle = computed(() => {
    if (activeTab.value === 'returns') return '退货数据';
    return '配送商品';
});

const stationPanelSubTitle = computed(() => {
    if (activeTab.value === 'returns') return `${filteredReturns.value.length} 条订单商品`;
    return '点击商品查看用户数量';
});

watch(roleType, (value) => {
    activeTab.value = value === 'supplier' ? 'purchase' : 'arrival';
    keyword.value = '';
    activeDate.value = 'all';
    onlyShortageDelivery.value = false;
}, { immediate: true });

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const query = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 50 };
        if (roleType.value === 'supplier') {
            const [purchasePage, itemPage, deliveryPage] = await Promise.all([
                pageSupplierPurchases(query),
                pageSupplierPurchaseItems(query),
                pageSupplierDeliveries(query)
            ]);
            purchases.value = purchasePage.list || [];
            supplierItems.value = itemPage.list || [];
            supplierDeliveries.value = deliveryPage.list || [];
        } else {
            const [stationPage, returnPage, exceptionPage, orderPage, itemPage] = await Promise.all([
                pageDeliveryStations(query),
                pageReturnHandovers(query),
                pageExceptions(query),
                pageStationOrders({ ...query, pageSize: 100 }),
                pageStationOrderItems({ ...query, pageSize: 200 })
            ]);
            deliveryStations.value = stationPage.list || [];
            returns.value = returnPage.list || [];
            exceptions.value = exceptionPage.list || [];
            stationOrders.value = orderPage.list || [];
            stationItems.value = itemPage.list || [];
        }
    } catch (err) {
        error.value = friendlyErrorMessage(err, '作业数据加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function open(url: string) {
    goPage(url);
}

function supplierDraftQty(key: string) {
    return Math.max(0, Math.floor(Number(supplierDraft.value[key] || 0)));
}

function toggleSupplierProduct(item: SupplierProductRow) {
    const qty = supplierDraftQty(item.key);
    if (qty > 0) {
        setSupplierDraftQty(item.key, 0);
        return;
    }
    setSupplierDraftQty(item.key, Math.max(1, item.purchaseQty || 1));
}

function changeSupplierDraftQty(item: SupplierProductRow, event: { detail?: { value?: string | number } }) {
    setSupplierDraftQty(item.key, Number(event.detail?.value || 0));
}

function setSupplierDraftQty(key: string, qty: number) {
    const nextQty = Math.max(0, Math.floor(Number(qty || 0)));
    supplierDraft.value = {
        ...supplierDraft.value,
        [key]: nextQty
    };
}

async function submitSupplierDraft() {
    if (supplierDraftSummary.value.qty <= 0) {
        showRoleToast('请先选择商品和数量');
        return;
    }
    const ok = await confirmAction(`确认提交 ${supplierDraftSummary.value.qty} 件供货申请？`, '供货申请');
    if (!ok) return;
    showRoleToast('供货申请提交通道暂未开放，暂不提交数据');
}

function clearKeyword() {
    keyword.value = '';
    activeDate.value = 'all';
    onlyShortageDelivery.value = false;
}

function normalize(value: string) {
    return String(value || '').trim().toLowerCase();
}

function formatDateTime(value?: string) {
    if (!value) return '-';
    return String(value).replace(/:\d{2}$/, '').replace('T', ' ');
}

function isLaterTime(value?: string, current?: string) {
    if (!value) return false;
    if (!current || current === '-') return true;
    return String(value).localeCompare(String(current), 'zh-CN') > 0;
}

function formatAmount(value: number) {
    return Number(value || 0).toFixed(2);
}

function supplierItemStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待处理',
        20: '待采购',
        30: '采购中',
        40: '待送货',
        50: '已送货',
        60: '已完成',
        70: '已取消'
    };
    return map[Number(status || 0)] || '在售';
}

function formatDateOption(value?: string) {
    if (!value) return '-';
    const match = String(value).match(/(\d{4})-(\d{2})-(\d{2})/);
    return match ? `${match[2]}月${match[3]}日` : String(value).split(' ')[0] || '-';
}

function matchActiveDate(value?: string) {
    return activeDate.value === 'all' || formatDateOption(value) === activeDate.value;
}

function changeDate(event: { detail?: { value?: number | string } }) {
    const index = Number(event.detail?.value || 0);
    activeDate.value = index <= 0 ? 'all' : dateOptions.value[index] || 'all';
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

function deliveryActualQty(item: StationOrderItemDTO) {
    const fulfillStatus = Number(item.fulfillStatus || 0);
    const pickedQty = Number(item.pickedQty || 0);
    if (pickedQty > 0) return pickedQty;
    if (fulfillStatus >= 50) return Number(item.qty || 0);
    return 0;
}

function deliveryProductStatus(expected: number, actual: number) {
    if (expected <= 0) return '等待处理';
    if (actual >= expected) return '已到货';
    if (actual > 0) return '部分到货';
    return '待到货';
}

function stationItemStatusText(item: StationOrderItemDTO) {
    const value = Number(item.fulfillStatus || 0);
    const afterSale = Number(item.afterSaleStatus || 0);
    if (afterSale >= 10 && afterSale <= 30) return '待售后确认';
    if (value >= 70) return '已完成';
    if (value >= 60) return '待提货';
    if (value >= 50) return '待到货';
    return '未发货';
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

function returnHasMismatch(item: ReturnHandoverDTO) {
    const qty = Number(item.returnQty || 0);
    return returnPickupQty(item) !== qty || returnWarehouseQty(item) !== returnPickupQty(item);
}

function exceptionStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待处理',
        20: '处理中',
        30: '已处理',
        40: '已关闭',
        50: '待确认',
        60: '已完成',
        70: '已取消'
    };
    return map[Number(status || 0)] || '等待处理';
}

function supplierPurchaseStatusText(item: SupplierPurchaseDTO) {
    const audit = Number(item.auditStatus || 0);
    const status = Number(item.purchaseStatus || 0);
    if ([30, 40, 50, 60].includes(status)) return '已完成';
    if ([30, 40, 50, 60].includes(audit)) return '已完成';
    if ([3, 70].includes(status) || audit === 30) return '待我补充';
    if ([20, 25].includes(status)) return '待提交到仓';
    return '已提交待处理';
}

function supplierAuditStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待审核',
        20: '已通过',
        30: '已拒绝',
        40: '已完成'
    };
    return map[Number(status || 0)] || '待审核';
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
    return map[Number(status || 0)] || '待送货';
}

function roleStatusClass(status: string) {
    if (/已完成|已到达|已到货|已回仓|已处理|已关闭|已通过|仓库已确认/.test(status)) return 'green';
    if (/部分到货|待回仓|待主管|待确认|处理中|待处理|已提交待处理|待审核/.test(status)) return 'orange';
    if (/待到货|待配送|待送货|送货中|等待仓库确认|待提交到仓|采购|审核/.test(status)) return 'blue';
    if (/无法|失败|取消|驳回|拒绝/.test(status)) return 'gray';
    return 'blue';
}

function openDeliveryProduct(item: DeliveryProductRow) {
    open(`/pages/store/delivery-product?key=${encodeURIComponent(item.key)}`);
}

function openStationShortage(item?: DeliveryProductRow) {
    const target = item || filteredDeliveryProducts.value.find((row) => row.lack > 0) || filteredDeliveryProducts.value[0];
    const query = target ? `?key=${encodeURIComponent(target.key)}` : '';
    open(`/pages/store/shortage${query}`);
}

function canConfirmArrival(item: DeliveryStationDTO) {
    return ![30, 40].includes(Number(item.status || 0));
}

async function confirmArrival(id: number) {
    const ok = await confirmAction(`确认配送记录 #${id} 已到货？`, '确认到货');
    if (!ok) return;
    try {
        await confirmStationArrival(id, {});
        showRoleToast('到货确认成功');
        load();
    } catch (err) {
        showRoleToast(friendlyErrorMessage(err, '到货确认失败'));
    }
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
  margin-top: 16rpx;
}

.role-work-title-main {
  display: block;
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-work-sub {
  display: block;
  margin-top: 10rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-main {
  padding-top: 0;
}

.role-delivery-tools {
  margin-bottom: 20rpx;
}

.role-sales-tools {
  display: grid;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.role-sales-tool-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 256rpx;
  gap: 16rpx;
  align-items: center;
}

.role-sales-tool-row.single {
  grid-template-columns: minmax(0, 1fr);
}

.role-sales-search,
.role-sales-date {
  display: grid;
  align-items: center;
  min-height: 80rpx;
  color: #c96a42;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(60, 33, 16, 0.05), inset 0 1rpx 0 rgba(255, 255, 255, 0.85);
}

.role-sales-search {
  grid-template-columns: auto minmax(0, 1fr);
  gap: 12rpx;
  padding: 0 26rpx;
}

.role-sales-search text {
  color: #d94b34;
  font-size: 24rpx;
  font-weight: 900;
}

.role-sales-search input {
  min-width: 0;
  min-height: 76rpx;
  padding: 0;
  color: #352a24;
  background: transparent;
  border: 0;
  font-size: 26rpx;
  font-weight: 800;
}

.role-sales-sort {
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  gap: 14rpx;
  padding: 12rpx;
  background: rgba(255, 255, 255, 0.72);
  border: 1rpx solid #f1d8c9;
  border-radius: 40rpx;
  box-shadow: 0 16rpx 36rpx rgba(79, 45, 24, 0.06), inset 0 1rpx 0 rgba(255, 255, 255, 0.9);
}

.role-sales-segment {
  display: grid;
  grid-template-columns: 68rpx repeat(2, minmax(0, 1fr));
  gap: 6rpx;
  align-items: center;
  min-width: 0;
  padding: 6rpx;
  background: #fffaf6;
  border: 1rpx solid #f5e1d4;
  border-radius: 30rpx;
}

.role-sales-segment > text {
  color: #a7775e;
  font-size: 22rpx;
  font-weight: 900;
  text-align: center;
  white-space: nowrap;
}

.role-sales-segment button,
.role-sales-segment uni-button,
.role-sales-segment :deep(uni-button) {
  min-height: 58rpx;
  color: #8d6a57;
  background: transparent;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
}

.role-sales-segment button.active,
.role-sales-segment uni-button.active,
.role-sales-segment :deep(uni-button.active) {
  color: #ffffff;
  background: linear-gradient(135deg, #e85d3f 0%, #ef8143 100%);
  box-shadow: 0 14rpx 28rpx rgba(232, 93, 63, 0.2);
}

.role-delivery-tabs {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8rpx;
  margin-bottom: 16rpx;
  padding: 8rpx;
  background: rgba(255, 248, 242, 0.92);
  border: 1rpx solid #f1d8ca;
  border-radius: 40rpx;
  box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.95), 0 20rpx 44rpx rgba(81, 48, 27, 0.07);
}

.role-delivery-tabs button,
.role-delivery-tabs uni-button,
.role-delivery-tabs :deep(uni-button) {
  min-height: 64rpx;
  color: #8d6a57;
  background: transparent;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-delivery-tabs button.active,
.role-delivery-tabs uni-button.active,
.role-delivery-tabs :deep(uni-button.active) {
  color: #ffffff;
  background: linear-gradient(135deg, #e85d3f 0%, #f17b45 100%);
  box-shadow: 0 16rpx 32rpx rgba(232, 93, 63, 0.24), inset 0 1rpx 0 rgba(255, 255, 255, 0.24);
  transform: translateY(-2rpx);
}

.role-delivery-query-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 224rpx;
  gap: 14rpx;
  align-items: center;
}

.role-order-search,
.role-delivery-date {
  display: grid;
  align-items: center;
  min-height: 78rpx;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(60, 33, 16, 0.05), inset 0 1rpx 0 rgba(255, 255, 255, 0.85);
}

.role-order-search {
  grid-template-columns: auto minmax(0, 1fr);
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
  justify-content: flex-start;
  gap: 10rpx;
  min-height: 78rpx;
  padding: 0 54rpx 0 16rpx;
  color: #2f241f;
  font-size: 24rpx;
  font-weight: 900;
}

.role-date-trigger::before {
  content: "";
  position: absolute;
  right: 24rpx;
  top: calc(50% - 2rpx);
  width: 20rpx;
  height: 4rpx;
  border-radius: 999rpx;
  background: #df613f;
}

.role-date-trigger::after {
  content: "";
  position: absolute;
  right: 18rpx;
  top: 50%;
  width: 30rpx;
  height: 30rpx;
  border: 3rpx solid #df613f;
  border-radius: 10rpx;
  transform: translateY(-50%);
}

.role-delivery-date-icon {
  display: grid;
  flex: 0 0 auto;
  width: 42rpx;
  height: 42rpx;
  place-items: center;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 16rpx;
  box-shadow: 0 16rpx 28rpx rgba(232, 93, 63, 0.18);
  font-size: 22rpx;
  font-weight: 900;
  line-height: 1;
}

.role-delivery-toggle {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
  overflow-x: auto;
}

.role-delivery-toggle button,
.role-delivery-toggle uni-button,
.role-delivery-toggle :deep(uni-button) {
  flex: 0 0 auto;
  min-height: 58rpx;
  padding: 0 24rpx;
  color: #8d6a57;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-delivery-toggle button.active,
.role-delivery-toggle uni-button.active,
.role-delivery-toggle :deep(uni-button.active) {
  color: #ffffff;
  background: #e85d3f;
  border-color: #e85d3f;
}

.role-filter-clear,
uni-button.role-filter-clear,
:deep(uni-button.role-filter-clear) {
  min-height: 58rpx;
  padding: 0 24rpx;
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-delivery-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin: 0 0 20rpx;
}

.role-delivery-stat {
  padding: 16rpx 12rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f3ddcf;
  border-radius: 24rpx;
  text-align: center;
}

.role-delivery-stat b,
.role-delivery-stat text:first-child {
  display: block;
  margin-bottom: 6rpx;
  color: #d94b34;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-delivery-stat text:not(:first-child) {
  font-size: 22rpx;
  font-weight: 800;
}

.role-sales-list,
.role-product-send-list {
  display: grid;
  gap: 20rpx;
}

.role-product-send-card,
.role-sales-card {
  padding: 22rpx;
  background: #fff8f3;
  border: 1rpx solid #f3d9c9;
  border-radius: 32rpx;
}

.role-product-send-card.selected {
  background: #fff2ea;
  border-color: #e85d3f;
  box-shadow: 0 20rpx 44rpx rgba(232, 93, 63, 0.12);
}

.role-sales-main {
  display: grid;
  grid-template-columns: 108rpx minmax(0, 1fr) auto;
  gap: 20rpx;
  align-items: center;
}

.role-sales-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 108rpx;
  height: 108rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border: 1rpx solid #f2d6c4;
  border-radius: 26rpx;
  font-size: 42rpx;
  font-weight: 900;
}

.role-sales-title {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.role-sales-name {
  display: block;
  min-width: 0;
  color: #2f241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-sales-tag {
  flex: none;
  padding: 6rpx 14rpx;
  color: #d94b34;
  background: #fff0e8;
  border: 1rpx solid #f3c9b6;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 900;
}

.role-sales-desc {
  display: block;
  margin-top: 8rpx;
  color: #8b6b57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-product-select,
uni-button.role-product-select,
:deep(uni-button.role-product-select) {
  flex: none;
  min-height: 58rpx;
  padding: 0 24rpx;
  color: #b85a2f;
  background: #fff3ea;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-product-send-card.selected .role-product-select,
.role-product-send-card.selected :deep(uni-button.role-product-select) {
  color: #ffffff;
  background: #e85d3f;
}

.role-sales-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 20rpx;
}

.role-sales-metrics view {
  padding: 14rpx 12rpx;
  color: #8b6b57;
  background: #ffffff;
  border: 1rpx solid #f0d8ca;
  border-radius: 22rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-sales-metrics text:first-child {
  display: block;
  overflow: hidden;
  margin-bottom: 4rpx;
  color: #d94b34;
  font-size: 28rpx;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-product-send-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 20rpx;
  padding: 16rpx;
  color: #8b6b57;
  background: rgba(255, 255, 255, 0.82);
  border: 1rpx solid #f0d8ca;
  border-radius: 24rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-product-send-form label {
  display: grid;
  min-width: 0;
  gap: 8rpx;
}

.role-product-send-form label > text {
  color: #8b6a57;
  font-size: 22rpx;
  font-weight: 900;
}

.role-product-send-form input {
  width: 100%;
  min-height: 56rpx;
  padding: 0 14rpx;
  color: #2d241f;
  background: #fffaf6;
  border: 1rpx solid #f1d8ca;
  border-radius: 16rpx;
  font-size: 22rpx;
  font-weight: 900;
}

.role-product-submit-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 18rpx;
  align-items: center;
  margin-top: 22rpx;
  padding: 22rpx;
  color: #8b6b57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 28rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-product-submit-title {
  display: block;
  margin-bottom: 6rpx;
  color: #2d241f;
  font-size: 26rpx;
  font-weight: 900;
}

.role-short-tip {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 16rpx;
  padding: 18rpx 20rpx;
  color: #8b6a57;
  background: #fff8f2;
  border: 1rpx solid #f4ddd0;
  border-radius: 24rpx;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-short-tip-actions,
.role-arrival-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10rpx;
}

.role-arrival-actions {
  justify-content: flex-start;
  margin-bottom: 16rpx;
}

.role-arrival-confirm-strip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin: -4rpx 0 16rpx;
  padding: 14rpx 18rpx;
  color: #9a725d;
  background: rgba(255, 253, 251, 0.78);
  border: 1rpx dashed #efc8b7;
  border-radius: 22rpx;
  font-size: 22rpx;
  line-height: 1.45;
}

.role-arrival-confirm-strip text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-arrival-confirm-btn,
uni-button.role-arrival-confirm-btn,
:deep(uni-button.role-arrival-confirm-btn) {
  flex: 0 0 auto;
  min-height: 48rpx;
  padding: 0 18rpx;
  color: #b85a2f;
  background: #fff3ea;
  border: 1rpx solid #f0d6c5;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.product-short-tip {
  margin: 18rpx 0 0;
}

.role-shortage-entry,
uni-button.role-shortage-entry,
:deep(uni-button.role-shortage-entry) {
  min-height: 56rpx;
  padding: 0 18rpx;
  color: #ffffff;
  background: #e85d3f;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-delivery-list {
  display: grid;
  gap: 20rpx;
}

.role-delivery-product,
.role-return-card {
  padding: 24rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 32rpx;
}

.role-delivery-product.short {
  background: #fff7f1;
  border-color: #efc8b7;
}

.role-delivery-product-main,
.role-return-main {
  display: grid;
  grid-template-columns: 92rpx minmax(0, 1fr);
  gap: 18rpx;
  align-items: start;
}

.role-delivery-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14rpx;
}

.role-delivery-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 92rpx;
  height: 92rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border: 1rpx solid #f2d6c4;
  border-radius: 24rpx;
  font-size: 30rpx;
  font-weight: 900;
}

.role-return-img {
  color: #d94b34;
}

.role-return-img svg {
  width: 52rpx;
  height: 52rpx;
}

.role-return-img path {
  vector-effect: non-scaling-stroke;
}

.role-delivery-title {
  display: block;
  overflow: hidden;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-delivery-desc {
  display: block;
  margin-top: 8rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.45;
}

.role-delivery-counts,
.role-return-qtys {
  display: grid;
  gap: 12rpx;
  margin-top: 16rpx;
}

.role-delivery-counts {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.role-return-qtys {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.role-delivery-counts view,
.role-return-qtys view {
  padding: 12rpx;
  color: #8b6a57;
  background: #fffaf6;
  border-radius: 18rpx;
  font-size: 22rpx;
}

.role-delivery-counts text:first-child,
.role-return-qtys text:first-child {
  display: block;
  margin-bottom: 4rpx;
}

.role-delivery-counts text:last-child,
.role-return-qtys text:last-child {
  display: block;
  overflow: hidden;
  color: #2d241f;
  font-weight: 900;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-return-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  align-items: center;
  margin-top: 14rpx;
  color: #8b6a57;
  font-size: 22rpx;
}

.role-return-order {
  margin-top: 12rpx;
  color: #8b6a57;
  font-size: 22rpx;
  line-height: 1.45;
}

.role-return-state,
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

.role-return-state.orange,
.role-status.orange {
  color: #d66323;
  background: #fff0e4;
}

.role-return-state.green,
.role-status.green {
  color: #17885f;
  background: #edf9f0;
}

.role-return-state.blue,
.role-status.blue {
  color: #2b6cb0;
  background: #eef6ff;
}

.role-return-state.gray,
.role-status.gray {
  color: #78665b;
  background: #f4f1ee;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  align-items: center;
  margin-top: 18rpx;
}

.role-action-btn,
uni-button.role-action-btn,
:deep(uni-button.role-action-btn) {
  min-height: 58rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-action-btn.primary,
uni-button.role-action-btn.primary,
:deep(uni-button.role-action-btn.primary) {
  color: #ffffff;
  background: #e85d3f;
}

.role-action-btn.soft,
uni-button.role-action-btn.soft,
:deep(uni-button.role-action-btn.soft) {
  color: #b85a2f;
  background: #fff3ea;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}

@media screen and (max-width: 360px) {
  .role-delivery-tabs {
    grid-template-columns: minmax(0, 1fr);
  }

  .role-delivery-query-row,
  .role-sales-tool-row,
  .role-short-tip,
  .role-short-tip-actions,
  .role-sales-sort,
  .role-product-send-form,
  .role-product-submit-bar {
    grid-template-columns: minmax(0, 1fr);
  }

  .role-delivery-counts {
    grid-template-columns: minmax(0, 1fr);
  }

  .role-sales-main {
    grid-template-columns: 104rpx minmax(0, 1fr);
  }

  .role-product-select {
    grid-column: 1 / -1;
  }
}
</style>
