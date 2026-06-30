<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '在售商品' : '服务工具' }}</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="role-work-title-main">{{ roleType === 'supplier' ? '在售商品' : '自提点服务' }}</text>
        </view>
        <text class="role-work-entity">{{ profile.entity }}</text>
      </view>
    </view>

    <view class="role-main">
      <template v-if="roleType === 'supplier'">
        <view class="role-sales-tools">
          <view class="role-sales-tool-row single">
            <view class="role-sales-search">
              <text>🔍</text>
              <input v-model="keyword" placeholder="搜索商品、规格、SKU" />
            </view>
          </view>
          <view class="role-sales-sort">
            <view class="role-sales-segment">
              <text>排序</text>
              <button :class="{ active: sortKey === 'sold' }" @click="sortKey = 'sold'">销量</button>
              <button :class="{ active: sortKey === 'price' }" @click="sortKey = 'price'">价格</button>
            </view>
            <view class="role-sales-segment">
              <text>方向</text>
              <button :class="{ active: sortOrder === 'desc' }" @click="sortOrder = 'desc'">降序</button>
              <button :class="{ active: sortOrder === 'asc' }" @click="sortOrder = 'asc'">升序</button>
            </view>
          </view>
        </view>

        <view v-if="loading" class="role-empty">
          <text class="title">正在加载在售商品</text>
          <text class="subtle">正在同步供应商商品销售数据...</text>
        </view>
        <view v-else-if="error" class="role-empty">
          <text class="title">在售商品加载失败</text>
          <text class="subtle">{{ error }}</text>
          <button class="role-action-btn primary retry-btn" @click="loadSupplierProducts">重新加载</button>
        </view>
        <template v-else>
          <view class="role-delivery-stats">
            <view class="role-delivery-stat">
              <text>{{ productStats.count }}</text>
              <text>在售商品</text>
            </view>
            <view class="role-delivery-stat">
              <text>{{ productStats.qty }}</text>
              <text>销售数量</text>
            </view>
            <view class="role-delivery-stat">
              <text>¥{{ productStats.avgPrice }}</text>
              <text>均价</text>
            </view>
          </view>

          <view class="role-detail-section">
            <view class="role-section-head">
              <view>
                <text class="section-title">商品销售数据</text>
                <text class="subtle">合计 ¥{{ productStats.amount }}</text>
              </view>
            </view>
            <view class="role-sales-list">
              <view v-for="item in filteredProducts" :key="item.key" class="role-sales-card">
                <view class="role-sales-main readonly">
                  <view class="role-sales-img"><RoleProductThumb :label="item.title" /></view>
                  <view>
                    <view class="role-sales-title">
                      <text class="role-sales-name">{{ item.title }}</text>
                      <text class="role-sales-tag">{{ item.status }}</text>
                    </view>
                    <text class="role-sales-desc">{{ item.spec }}</text>
                  </view>
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
              </view>
              <view v-if="filteredProducts.length === 0" class="role-empty inline-empty">
                <text class="title">当前条件暂无在售商品数据</text>
              </view>
            </view>
          </view>
        </template>
      </template>

      <view v-else class="role-detail-section">
        <view class="role-section-head">
          <view>
            <text class="section-title">服务管理</text>
          </view>
        </view>
        <view class="role-service-grid">
          <button v-for="item in services" :key="item.title" class="role-service-card" @click="open(item.page)">
            <text class="role-service-title">{{ item.title }}</text>
          </button>
        </view>
      </view>
    </view>
    <RoleBottomNav active="delivery" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { pageSupplierPurchaseItems, type SupplierPurchaseItemDTO } from '@/api/station';
import { currentProfile, currentRole, getRequiredRoleQuery, goPage, type RoleItem } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';
import RoleProductThumb from '@/components/RoleProductThumb.vue';

type SortKey = 'sold' | 'price';
type SortOrder = 'desc' | 'asc';

interface ProductRow {
    key: string;
    title: string;
    spec: string;
    status: string;
    purchaseQty: number;
    amountValue: number;
    priceValue: number;
    amount: string;
    price: string;
    searchText: string;
}

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const services = computed<RoleItem[]>(() => [
    { title: '收退货协同', page: '/pages/store/index' },
    { title: '休假设置', page: '/pages/leave/apply' },
    { title: '客户联络', page: '/pages/orders/index' },
    { title: '团点公告', page: '/pages/store/notify-preview' }
]);
const keyword = ref('');
const sortKey = ref<SortKey>('sold');
const sortOrder = ref<SortOrder>('desc');
const loading = ref(false);
const error = ref('');
const supplierItems = ref<SupplierPurchaseItemDTO[]>([]);

const products = computed<ProductRow[]>(() => {
    const map = new Map<string, {
        key: string;
        title: string;
        spec: string;
        status: string;
        purchaseQty: number;
        amountValue: number;
        prices: number[];
        searchText: string[];
    }>();

    supplierItems.value.forEach((item) => {
        const title = item.productName || `商品 #${item.productId || item.id}`;
        const spec = item.skuName || (item.skuId ? `SKU #${item.skuId}` : '-');
        const key = `${item.productId || title}-${item.skuId || spec}`;
        const qty = Number(item.purchaseQty || item.applyQty || item.receivedQty || 0);
        const price = Number(item.purchasePrice || 0);
        const amount = Number(item.amount || 0) || qty * price;
        const row = map.get(key) || {
            key,
            title,
            spec,
            status: supplierItemStatusText(undefined),
            purchaseQty: 0,
            amountValue: 0,
            prices: [],
            searchText: []
        };
        row.purchaseQty += qty;
        row.amountValue += amount;
        if (price > 0) row.prices.push(price);
        row.status = supplierItemStatusText(item.status);
        row.searchText.push([title, spec, item.productId, item.skuId, item.purchaseNo, item.warehouseId, row.status].join(' '));
        map.set(key, row);
    });

    return Array.from(map.values()).map((item) => {
        const priceValue = item.prices.length
            ? item.prices.reduce((sum, value) => sum + value, 0) / item.prices.length
            : (item.purchaseQty ? item.amountValue / item.purchaseQty : 0);
        return {
            key: item.key,
            title: item.title,
            spec: item.spec,
            status: item.status,
            purchaseQty: item.purchaseQty,
            amountValue: item.amountValue,
            priceValue,
            amount: formatAmount(item.amountValue),
            price: formatAmount(priceValue),
            searchText: item.searchText.join(' ').toLowerCase()
        };
    });
});

const filteredProducts = computed(() => {
    const query = keyword.value.trim().toLowerCase();
    const rows = products.value.filter((item) => !query || item.searchText.includes(query));
    const direction = sortOrder.value === 'desc' ? -1 : 1;
    return [...rows].sort((left, right) => {
        const leftValue = sortKey.value === 'sold' ? left.purchaseQty : left.priceValue;
        const rightValue = sortKey.value === 'sold' ? right.purchaseQty : right.priceValue;
        return (leftValue - rightValue) * direction;
    });
});

const productStats = computed(() => {
    const qty = filteredProducts.value.reduce((sum, item) => sum + item.purchaseQty, 0);
    const amount = filteredProducts.value.reduce((sum, item) => sum + item.amountValue, 0);
    return {
        count: filteredProducts.value.length,
        qty,
        avgPrice: formatAmount(qty ? amount / qty : 0),
        amount: formatAmount(amount)
    };
});

onShow(() => {
    if (roleType.value === 'supplier') {
        loadSupplierProducts();
    }
});

async function loadSupplierProducts() {
    loading.value = true;
    error.value = '';
    try {
        supplierItems.value = (await pageSupplierPurchaseItems({ ...getRequiredRoleQuery(), pageNum: 1, pageSize: 100 })).list || [];
    } catch (err) {
        error.value = friendlyErrorMessage(err, '在售商品加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

function open(url: string) {
    goPage(url);
}

function supplierItemStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '在售',
        20: '已确认',
        30: '已收货',
        40: '已完成',
        50: '已取消'
    };
    return map[Number(status || 0)] || '在售';
}

function formatAmount(value: number) {
    return Number.isFinite(value) ? value.toFixed(2) : '0.00';
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
  color: #2b241f;
  font-size: 32rpx;
  font-weight: 900;
}

.role-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.role-service-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.role-service-card,
uni-button.role-service-card {
  display: grid;
  align-content: start;
  min-height: 156rpx;
  padding: 22rpx;
  color: #2d241f;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 26rpx;
  text-align: left;
}

.role-service-title {
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
}

.role-sales-tools {
  display: grid;
  gap: 18rpx;
  margin-bottom: 24rpx;
}

.role-sales-tool-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 230rpx;
  gap: 16rpx;
  align-items: center;
}

.role-sales-tool-row.single {
  grid-template-columns: minmax(0, 1fr);
}

.role-sales-search {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: center;
  gap: 12rpx;
  min-height: 80rpx;
  padding: 0 24rpx;
  background: #fffdfb;
  border: 1rpx solid #efc8b7;
  border-radius: 999rpx;
  box-shadow: 0 16rpx 36rpx rgba(60, 33, 16, 0.05);
}

.role-sales-search text {
  color: #d94b34;
  font-size: 24rpx;
  font-weight: 900;
}

.role-sales-search input {
  min-height: 76rpx;
  padding: 0;
  background: transparent;
  border: 0;
}

.role-sales-sort {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
}

.role-sales-segment {
  display: grid;
  grid-template-columns: auto repeat(2, minmax(0, 1fr));
  gap: 8rpx;
  align-items: center;
  min-height: 66rpx;
  padding: 8rpx;
  background: #fff8f2;
  border: 1rpx solid #f1d8ca;
  border-radius: 999rpx;
}

.role-sales-segment > text {
  padding-left: 12rpx;
  color: #8d6a57;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-sales-segment button,
.role-sales-segment uni-button,
.role-sales-segment :deep(uni-button) {
  min-height: 48rpx;
  padding: 0 12rpx;
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
  background: #e85d3f;
}

.role-delivery-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.role-delivery-stat {
  padding: 22rpx 16rpx;
  color: #8b6b57;
  background: #fff8f3;
  border: 1rpx solid #f3d9c9;
  border-radius: 28rpx;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-delivery-stat text {
  display: block;
}

.role-delivery-stat text:first-child {
  overflow: hidden;
  color: #d94b34;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.2;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-sales-list {
  display: grid;
  gap: 20rpx;
}

.role-sales-card {
  padding: 22rpx;
  background: #fff8f3;
  border: 1rpx solid #f3d9c9;
  border-radius: 32rpx;
}

.role-sales-main {
  display: grid;
  grid-template-columns: 124rpx minmax(0, 1fr) auto;
  gap: 20rpx;
  align-items: center;
}

.role-sales-main.readonly {
  grid-template-columns: 124rpx minmax(0, 1fr);
}

.role-sales-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 124rpx;
  height: 124rpx;
  color: #d94b34;
  background: linear-gradient(135deg, #fff3ea 0%, #fffaf6 100%);
  border: 1rpx solid #f2d6c4;
  border-radius: 28rpx;
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

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}

@media screen and (max-width: 360px) {
  .role-sales-sort,
  .role-sales-main,
  .role-sales-main.readonly {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
