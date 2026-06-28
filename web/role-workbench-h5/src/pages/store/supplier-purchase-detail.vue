<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>采购单详情</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ purchase.no }}</text>
      <text class="subtle">{{ purchase.warehouse }} · {{ purchase.pickup }}</text>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">采购信息</text>
          <text class="subtle">供应商动作只影响自身采购/到仓流程</text>
        </view>
        <text class="status-pill" :class="purchase.statusClass">{{ purchase.status }}</text>
      </view>
      <view class="detail-row">
        <text>总数量</text>
        <text>{{ purchase.totalQty }} 件</text>
      </view>
      <view class="detail-row">
        <text>金额</text>
        <text>¥{{ purchase.amount.toFixed(2) }}</text>
      </view>
      <view class="detail-row">
        <text>申请时间</text>
        <text>{{ purchase.applyTime }}</text>
      </view>
      <view v-if="purchase.rejectReason" class="detail-row">
        <text>驳回原因</text>
        <text>{{ purchase.rejectReason }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">商品明细</text>
          <text class="subtle">{{ purchase.items.length }} 个 SKU</text>
        </view>
      </view>
      <view v-for="item in purchase.items" :key="item.id" class="product-line">
        <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
        <view class="card-main">
          <text class="card-title">{{ item.title }}</text>
          <text class="card-desc">{{ item.spec }}</text>
        </view>
        <view class="qty">
          <text>{{ item.qty }} 件</text>
          <text>¥{{ item.price }}</text>
        </view>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="confirmPurchase">确认供货</button>
      <button class="plain" @click="submitArrival">提交到仓</button>
      <button class="danger" @click="goShortage">缺货反馈</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { addOperation, confirmAction, currentProfile, findPurchase, showRoleToast } from '@/stores/role';

const no = ref('');
const profile = computed(() => currentProfile.value);
const purchase = computed(() => findPurchase(no.value));

onLoad((query) => {
    no.value = String(query?.no || '');
});

async function confirmPurchase() {
    const ok = await confirmAction(`确认 ${purchase.value.no} 可以供货？`, '确认供货');
    if (!ok) return;
    addOperation({
        no: purchase.value.no,
        type: '采购确认',
        title: `${purchase.value.no} 已确认供货`,
        time: '刚刚',
        status: '已提交待处理',
        next: '等待平台处理'
    });
    showRoleToast('采购确认接口暂未接通，已记录前端操作');
}

async function submitArrival() {
    const ok = await confirmAction(`确认提交 ${purchase.value.no} 到仓凭证？`, '提交到仓');
    if (ok) {
        showRoleToast('到仓凭证接口暂未接通，已保留交互');
    }
}

function goShortage() {
    const orderNo = purchase.value.orderNo || profile.value.orders[0]?.no || '';
    uni.navigateTo({ url: `/pages/store/shortage?no=${encodeURIComponent(orderNo)}` });
}
</script>

<style lang="scss" scoped>
.qty {
  text-align: right;
}

.qty text {
  display: block;
  white-space: nowrap;
}

.qty text:first-child {
  color: #2b241f;
  font-weight: 900;
}

.qty text:last-child {
  margin-top: 6rpx;
  color: #d94b34;
  font-size: 23rpx;
  font-weight: 900;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 23rpx;
}
</style>
