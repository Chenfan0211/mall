<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '送货单详情' : '提货订单详情' }}</text>
      </view>
      <view class="detail-headline">
        <button class="ghost back-btn" @click="back">‹</button>
        <view>
          <text class="title">{{ order.no }}</text>
          <text class="subtle">{{ order.pickup }}</text>
        </view>
        <text class="status-pill" :class="order.statusClass">{{ order.status }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">订单信息</text>
          <text class="subtle">{{ roleType === 'supplier' ? '只展示供应商履约字段' : '包含提货用户和备注' }}</text>
        </view>
      </view>
      <view class="detail-row">
        <text>订单状态</text>
        <text>{{ order.status }}</text>
      </view>
      <view class="detail-row">
        <text>合计数量</text>
        <text>{{ order.totalQty }} 件</text>
      </view>
      <view class="detail-row">
        <text>合计金额</text>
        <text>¥{{ order.amount }}</text>
      </view>
      <view class="detail-row">
        <text>生成时间</text>
        <text>{{ order.time }}</text>
      </view>
      <view class="detail-row">
        <text>{{ roleType === 'supplier' ? '送达仓库' : '自提点' }}</text>
        <text>{{ roleType === 'supplier' ? order.warehouse || order.station : order.station }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">商品明细</text>
          <text class="subtle">{{ order.items.length }} 个商品</text>
        </view>
      </view>
      <view v-for="item in order.items" :key="item.id" class="product-line">
        <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
        <view class="card-main">
          <text class="card-title">{{ item.title }}</text>
          <text class="card-desc">{{ item.spec }}{{ item.shortageQty ? ` · 已标记缺货 ${item.shortageQty} 件` : '' }}</text>
        </view>
        <view class="qty">
          <text>¥{{ item.price }}</text>
          <text>x{{ item.qty }}</text>
        </view>
      </view>
    </view>

    <view v-if="roleType === 'station'" class="section">
      <view class="section-head">
        <view>
          <text class="title">用户信息</text>
          <text class="subtle">仅自提点处理提货时展示</text>
        </view>
      </view>
      <view class="detail-row">
        <text>提货人</text>
        <text>{{ order.userName }} {{ order.userMobile }}</text>
      </view>
      <view class="detail-row">
        <text>备注</text>
        <text>{{ order.userRemark || '无备注' }}</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">{{ roleType === 'station' ? '提货处理规则' : '履约处理规则' }}</text>
          <text class="subtle">{{ roleType === 'station' ? '少件错件走异常，不展示提货码' : '提交到仓后等待仓库确认' }}</text>
        </view>
      </view>
      <text class="rule-copy">
        {{ roleType === 'station'
          ? '自提点交付商品属于线下行为，当前阶段暂不做交付凭证。用户到店少件或错件时，可当场标记异常，由运营处理补货或售后。'
          : '供应商只能查看自身送货单、仓库、SKU、数量和结算相关数据，不展示 C 端用户姓名、手机号和备注。' }}
      </text>
    </view>

    <view class="fixed-bottom">
      <button v-if="roleType === 'station'" class="primary" @click="verify">确认提货</button>
      <button v-if="roleType === 'station'" class="plain" @click="notify">发送通知</button>
      <button v-if="roleType === 'station'" class="soft" @click="contact">联系用户</button>
      <button v-if="roleType === 'supplier'" class="primary" @click="submitArrival">提交到仓</button>
      <button v-if="roleType === 'supplier'" class="danger" @click="goShortage">缺货标记</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { addOperation, confirmAction, currentRole, findOrder, showRoleToast } from '@/stores/role';

const roleType = computed(() => currentRole.value);
const orderNo = ref('');
const order = computed(() => findOrder(orderNo.value));

onLoad((query) => {
    orderNo.value = String(query?.no || '');
});

function back() {
    uni.navigateBack({ delta: 1 });
}

async function verify() {
    const ok = await confirmAction(`确认 ${order.value.userName || '用户'} 已提货？`, '确认提货');
    if (!ok) return;
    addOperation({
        no: `PK${Date.now()}`,
        type: '提货核销',
        title: `${order.value.no} 已确认提货`,
        time: '刚刚',
        status: '已完成',
        next: '订单进入已提货记录'
    });
    showRoleToast('提货核销接口暂未接通，已记录前端操作');
}

async function notify() {
    const ok = await confirmAction(`确认向 ${order.value.userName || '用户'} 发送到货通知？`, '发送通知');
    if (ok) {
        showRoleToast('到货通知接口暂未接通，已保留交互');
    }
}

function contact() {
    showRoleToast(order.value.userMobile ? `已复制手机号 ${order.value.userMobile}` : '当前订单没有手机号');
}

async function submitArrival() {
    const ok = await confirmAction(`确认提交 ${order.value.no} 到仓凭证？`, '提交到仓');
    if (ok) {
        showRoleToast('到仓凭证接口暂未接通，已保留交互');
    }
}

function goShortage() {
    uni.navigateTo({ url: `/pages/store/shortage?no=${encodeURIComponent(order.value.no)}` });
}
</script>

<style lang="scss" scoped>
.detail-headline {
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
}

.back-btn {
  width: 64rpx;
  min-width: 64rpx;
  min-height: 64rpx;
  padding: 0;
  font-size: 44rpx;
}

.qty {
  text-align: right;
}

.qty text {
  display: block;
  white-space: nowrap;
}

.qty text:first-child {
  color: #d94b34;
  font-weight: 900;
}

.qty text:last-child {
  margin-top: 6rpx;
  color: #8f6c58;
  font-size: 23rpx;
}

.rule-copy {
  display: block;
  color: #8f6c58;
  font-size: 24rpx;
  line-height: 1.7;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
