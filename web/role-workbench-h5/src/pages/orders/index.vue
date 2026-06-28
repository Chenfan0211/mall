<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ profile.entity }}</text>
      </view>
      <text class="subtle">{{ roleType === 'supplier' ? '供应商到仓' : '提货订单' }}</text>
      <text class="title">{{ roleType === 'supplier' ? '送货单与仓库汇总' : '当前自提点订单核销' }}</text>
      <text class="subtle">{{ roleType === 'supplier' ? '只看仓库、SKU、数量、到仓时间和单据状态' : '默认当前提货日，可按订单或商品汇总查询' }}</text>
    </view>

    <view class="tabs">
      <button :class="{ active: mode === 'order' }" @click="mode = 'order'">
        {{ roleType === 'supplier' ? '送货单维度' : '提货订单' }}
      </button>
      <button :class="{ active: mode === 'summary' }" @click="mode = 'summary'">
        {{ roleType === 'supplier' ? '仓库维度' : '商品汇总' }}
      </button>
    </view>

    <view class="search-box">
      <text>搜</text>
      <input v-model="keyword" :placeholder="roleType === 'supplier' ? '搜索送货单、仓库、SKU' : '搜索订单、用户、手机号、商品'" />
    </view>

    <view v-if="mode === 'order'" class="card-list">
      <view v-for="order in filteredOrders" :key="order.no" class="order-card" @click="openDetail(order.no)">
        <view class="card-head">
          <view class="card-main">
            <text class="card-title">{{ order.no }}</text>
            <text class="card-desc">{{ order.time }}</text>
          </view>
          <text class="status-pill" :class="order.statusClass">{{ order.status }}</text>
        </view>
        <view class="order-meta">
          <view>
            <text>{{ roleType === 'supplier' ? '仓库' : '提货人' }}</text>
            <text>{{ roleType === 'supplier' ? order.warehouse || order.station : order.userName }}</text>
          </view>
          <view>
            <text>{{ roleType === 'supplier' ? '到仓' : '提货日' }}</text>
            <text>{{ order.pickup }}</text>
          </view>
          <view>
            <text>数量</text>
            <text>{{ order.totalQty }} 件</text>
          </view>
        </view>
        <view class="action-row">
          <button class="plain" @click.stop="openDetail(order.no)">查看详情</button>
          <button v-if="roleType === 'station'" class="soft" @click.stop="contactUser(order.no)">联系用户</button>
          <button v-if="roleType === 'supplier'" class="soft" @click.stop="submitProof(order.no)">提交到仓</button>
        </view>
      </view>
      <view v-if="filteredOrders.length === 0" class="empty-panel">
        <text class="title">暂无匹配单据</text>
        <text class="subtle">可清空筛选后查看当前主体数据。</text>
      </view>
    </view>

    <view v-else class="card-list">
      <view v-for="item in summaries" :key="item.key" class="product-card" @click="openSummary(item.key)">
        <view class="product-line">
          <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
          <view class="card-main">
            <text class="card-title">{{ item.title }}</text>
            <text class="card-desc">{{ item.spec }}</text>
          </view>
          <view class="summary-count">
            <text>{{ item.qty }}</text>
            <text>件</text>
          </view>
        </view>
        <view class="summary-foot">
          <text>{{ item.orders }} 张{{ roleType === 'supplier' ? '送货单' : '提货订单' }}</text>
          <text class="money">¥{{ item.amount }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { confirmAction, currentProfile, currentRole, goPage, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const mode = ref<'order' | 'summary'>('order');
const keyword = ref('');

const filteredOrders = computed(() => {
    const query = keyword.value.trim().toLowerCase();
    if (!query) {
        return profile.value.orders;
    }
    return profile.value.orders.filter((order) => {
        const text = [
            order.no,
            order.status,
            order.station,
            order.warehouse,
            order.pickup,
            roleType.value === 'station' ? order.userName : '',
            roleType.value === 'station' ? order.userMobile : '',
            ...order.items.flatMap((item) => [item.title, item.spec])
        ].filter(Boolean).join(' ').toLowerCase();
        return text.includes(query);
    });
});

const summaries = computed(() => {
    const map = new Map<string, { key: string; title: string; spec: string; qty: number; amount: number; orders: number }>();
    filteredOrders.value.forEach((order) => {
        order.items.forEach((item) => {
            const key = `${item.title}-${item.spec}`;
            const row = map.get(key) || { key, title: item.title, spec: item.spec, qty: 0, amount: 0, orders: 0 };
            row.qty += Number(item.qty || 0);
            row.amount += Number(item.price || 0) * Number(item.qty || 0);
            row.orders += 1;
            map.set(key, row);
        });
    });
    return Array.from(map.values()).map((item) => ({ ...item, amount: item.amount.toFixed(2) }));
});

function openDetail(no: string) {
    goPage(`/pages/orders/detail?no=${encodeURIComponent(no)}`);
}

function openSummary(key: string) {
    goPage(`/pages/orders/product-summary?key=${encodeURIComponent(key)}`);
}

function contactUser(no: string) {
    const order = profile.value.orders.find((item) => item.no === no);
    showRoleToast(order?.userMobile ? `已复制手机号 ${order.userMobile}` : '当前订单没有可联系手机号');
}

async function submitProof(no: string) {
    const ok = await confirmAction(`确认提交 ${no} 的到仓凭证？`, '提交到仓');
    if (ok) {
        showRoleToast('到仓凭证接口暂未接通，页面交互已保留');
    }
}
</script>

<style lang="scss" scoped>
.order-meta {
  display: grid;
  grid-template-columns: 1fr 1.3fr 0.7fr;
  gap: 12rpx;
  margin: 20rpx 0;
}

.order-meta view {
  min-height: 86rpx;
  padding: 14rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx;
}

.order-meta text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-meta text:first-child {
  color: #8f6c58;
  font-size: 20rpx;
}

.order-meta text:last-child {
  margin-top: 8rpx;
  color: #2b241f;
  font-size: 23rpx;
  font-weight: 900;
}

.action-row button {
  flex: 1;
  min-height: 62rpx;
  font-size: 24rpx;
}

.summary-count {
  min-width: 78rpx;
  text-align: right;
}

.summary-count text:first-child {
  display: block;
  color: #d94b34;
  font-size: 34rpx;
  font-weight: 900;
}

.summary-count text:last-child {
  color: #8f6c58;
  font-size: 22rpx;
}

.summary-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 16rpx;
  color: #8f6c58;
  font-size: 24rpx;
}
</style>
