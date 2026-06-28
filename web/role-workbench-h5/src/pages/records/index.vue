<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>操作记录</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">本人提交记录</text>
      <text class="subtle">休假、提现、异常、通知、采购和到仓凭证只读追踪。</text>
    </view>

    <view class="filter-row">
      <button :class="{ active: type === 'all' }" @click="type = 'all'">全部</button>
      <button :class="{ active: type === 'wait' }" @click="type = 'wait'">待处理</button>
      <button :class="{ active: type === 'done' }" @click="type = 'done'">已完成</button>
    </view>

    <view class="card-list">
      <view v-for="item in rows" :key="item.no" class="record-card">
        <view class="card-head">
          <view class="card-main">
            <text class="card-title">{{ item.no }} · {{ item.type }}</text>
            <text class="card-desc">{{ item.time }}</text>
          </view>
          <text class="status-pill" :class="statusClass(item.status)">{{ item.status }}</text>
        </view>
        <text class="record-copy">内容：{{ item.title }}</text>
        <text class="record-copy">下一步：{{ item.next }}</text>
      </view>
      <view v-if="rows.length === 0" class="empty-panel">
        <text class="title">暂无操作记录</text>
        <text class="subtle">提交休假、提现、异常、通知、采购或到仓凭证后会在这里展示。</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { currentProfile, statusClass } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const type = ref<'all' | 'wait' | 'done'>('all');
const rows = computed(() => {
    if (type.value === 'wait') {
        return profile.value.operations.filter((item) => /待|等待|提交/.test(item.status));
    }
    if (type.value === 'done') {
        return profile.value.operations.filter((item) => /完成|发送|到账/.test(item.status));
    }
    return profile.value.operations;
});
</script>

<style lang="scss" scoped>
.filter-row {
  margin-bottom: 18rpx;
}

.filter-row button {
  border: 1rpx solid #f0dfd6;
}

.record-copy {
  display: block;
  margin-top: 14rpx;
  color: #8f6c58;
  font-size: 24rpx;
  line-height: 1.55;
}
</style>
