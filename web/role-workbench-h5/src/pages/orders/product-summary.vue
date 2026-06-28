<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '商品送货单汇总' : '商品订单用户' }}</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ summary.title || '商品汇总' }}</text>
      <text class="subtle">{{ summary.orders }} 张{{ roleType === 'supplier' ? '送货单' : '提货订单' }} · {{ summary.qty }} 件</text>
    </view>

    <view class="section">
      <view class="product-line">
        <view class="product-thumb">{{ summary.title.slice(0, 1) || '商' }}</view>
        <view class="card-main">
          <text class="card-title">{{ summary.title }}</text>
          <text class="card-desc">{{ summary.spec }}</text>
        </view>
        <view class="summary-count">
          <text>{{ summary.qty }}</text>
          <text>件</text>
        </view>
      </view>
      <view class="summary-foot">
        <view>
          <text>{{ summary.orders }}</text>
          <text>{{ roleType === 'supplier' ? '送货单' : '订单' }}</text>
        </view>
        <view>
          <text>{{ summary.qty }}</text>
          <text>数量</text>
        </view>
        <view>
          <text>¥{{ summary.amount }}</text>
          <text>金额</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">{{ roleType === 'supplier' ? '送货单和仓库信息' : '订单和用户信息' }}</text>
          <text class="subtle">{{ roleType === 'supplier' ? '供应商视角不展示用户个人信息' : '按提货订单列出用户和备注' }}</text>
        </view>
      </view>
      <view class="card-list">
        <view v-for="line in lines" :key="line.no" class="record-card" @click="openDetail(line.no)">
          <view class="card-head">
            <view class="card-main">
              <text class="card-title">{{ line.no }}</text>
              <text class="card-desc">{{ roleType === 'supplier' ? line.warehouse || line.station : `${line.userName} ${line.userMobile}` }}</text>
            </view>
            <text class="status-pill" :class="line.statusClass">{{ line.status }}</text>
          </view>
          <text class="line-copy">
            {{ roleType === 'supplier'
              ? `到仓时间：${line.pickup}；生成时间：${line.time}`
              : `提货日：${line.pickup}；备注：${line.userRemark || '无'}` }}
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { currentProfile, currentRole, type RoleOrder } from '@/stores/role';

const key = ref('');
const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);

onLoad((query) => {
    key.value = decodeURIComponent(String(query?.key || ''));
});

const lines = computed<RoleOrder[]>(() =>
    profile.value.orders.filter((order) => order.items.some((item) => `${item.title}-${item.spec}` === key.value))
);

const summary = computed(() => {
    let title = '';
    let spec = '';
    let qty = 0;
    let amount = 0;
    lines.value.forEach((order) => {
        order.items.forEach((item) => {
            if (`${item.title}-${item.spec}` === key.value) {
                title = item.title;
                spec = item.spec;
                qty += Number(item.qty || 0);
                amount += Number(item.qty || 0) * Number(item.price || 0);
            }
        });
    });
    return {
        title,
        spec,
        qty,
        amount: amount.toFixed(2),
        orders: lines.value.length
    };
});

function openDetail(no: string) {
    uni.navigateTo({ url: `/pages/orders/detail?no=${encodeURIComponent(no)}` });
}
</script>

<style lang="scss" scoped>
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
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 20rpx;
}

.summary-foot view {
  padding: 16rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx;
}

.summary-foot text {
  display: block;
  text-align: center;
}

.summary-foot text:first-child {
  color: #d94b34;
  font-weight: 900;
}

.summary-foot text:last-child {
  margin-top: 6rpx;
  color: #8f6c58;
  font-size: 22rpx;
}

.line-copy {
  display: block;
  margin-top: 16rpx;
  color: #8f6c58;
  font-size: 24rpx;
  line-height: 1.6;
}
</style>
