<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>到货通知预览</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">发送前确认影响范围</text>
      <text class="subtle">避免重复打扰用户，同一提货日再次发送前必须展示本预览。</text>
    </view>

    <view class="section">
      <view class="summary-box">
        <view>
          <text>{{ userCount }}</text>
          <text>影响用户</text>
        </view>
        <view>
          <text>{{ delivery.goods.length }}</text>
          <text>商品种类</text>
        </view>
        <view>
          <text>{{ goodsCount }}</text>
          <text>实到数量</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">通知确认</text>
          <text class="subtle">公众号模板消息 + 站内消息</text>
        </view>
        <text class="status-pill orange">待通知</text>
      </view>
      <view class="detail-row">
        <text>提货日</text>
        <text>{{ delivery.label }}</text>
      </view>
      <view class="detail-row">
        <text>上次发送</text>
        <text>2026-06-10 15:42</text>
      </view>
      <view class="detail-row">
        <text>发送限制</text>
        <text>再次发送前必须展示本预览</text>
      </view>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">商品范围</text>
          <text class="subtle">按实到商品生成通知</text>
        </view>
      </view>
      <view v-for="item in delivery.goods" :key="item.id" class="product-line">
        <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
        <view class="card-main">
          <text class="card-title">{{ item.title }}</text>
          <text class="card-desc">{{ item.spec }} · {{ item.users.length }} 位用户</text>
        </view>
        <text class="money">{{ item.actual }} 件</text>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="primary" @click="send">确认发送</button>
      <button class="soft" @click="back">返回作业</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { addOperation, confirmAction, currentProfile, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const delivery = computed(() => profile.value.deliveryRecords[0] || { label: '暂无配送日', goods: [], returns: [] });
const userCount = computed(() => new Set(delivery.value.goods.flatMap((item) => item.users.map((user) => user.orderNo))).size);
const goodsCount = computed(() => delivery.value.goods.reduce((sum, item) => sum + Number(item.actual || 0), 0));

async function send() {
    const ok = await confirmAction(`确认给 ${userCount.value} 位用户发送到货通知？`, '发送到货通知');
    if (!ok) return;
    addOperation({
        no: `NT${Date.now()}`,
        type: '到货通知',
        title: `${delivery.value.label} 到货通知`,
        time: '刚刚',
        status: '已发送',
        next: '用户可到店提货'
    });
    showRoleToast('到货通知接口暂未接通，已记录前端操作');
}

function back() {
    uni.switchTab({ url: '/pages/store/index' });
}
</script>

<style lang="scss" scoped>
.summary-box {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.summary-box view {
  padding: 18rpx;
  background: #fff7f1;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  text-align: center;
}

.summary-box text:first-child {
  display: block;
  color: #d94b34;
  font-size: 34rpx;
  font-weight: 900;
}

.summary-box text:last-child {
  color: #8f6c58;
  font-size: 22rpx;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
