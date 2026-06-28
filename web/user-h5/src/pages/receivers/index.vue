<template>
  <view class="page no-tab receivers-page">
    <view class="section">
      <text class="title">提货人管理</text>
      <text class="subtle">结算页默认使用当前默认提货人。</text>
    </view>

    <view v-for="item in state.receivers" :key="item.id" class="section receiver-row">
      <view>
        <text>{{ item.name }}</text>
        <text class="subtle">{{ item.mobile }} · {{ item.defaultFlag ? '默认提货人' : '备用提货人' }}</text>
      </view>
      <view class="receiver-actions">
        <button v-if="!item.defaultFlag" class="plain small" @click="makeDefault(item.id)">设默认</button>
        <button class="plain small" @click="askRemove(item.id)">删除</button>
      </view>
    </view>

    <view class="section">
      <text class="title">新增提货人</text>
      <input v-model="name" placeholder="提货人姓名" />
      <input v-model="mobile" class="mobile-input" placeholder="手机号" />
      <button class="primary add-btn" @click="saveReceiver">保存提货人</button>
    </view>

    <ConfirmDialog
      :visible="removeDialog.visible"
      title="删除提货人"
      content="确认删除该提货人？默认提货人被删除后会自动选择下一个。"
      confirm-text="删除"
      danger
      @cancel="removeDialog.visible = false"
      @confirm="removeCurrent"
    />
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import UserToast from '@/components/UserToast.vue';
import { addReceiver, removeReceiver, setDefaultReceiver, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();
const name = ref('');
const mobile = ref('');
const removeDialog = reactive({
    visible: false,
    id: 0
});

function saveReceiver() {
    if (!name.value.trim() || !/^1\d{10}$/.test(mobile.value.trim())) {
        showUserToast('请填写正确的提货人姓名和手机号', 'warn');
        return;
    }
    addReceiver(state, name.value.trim(), mobile.value.trim());
    name.value = '';
    mobile.value = '';
    showUserToast('提货人已保存');
}

function makeDefault(id: number) {
    setDefaultReceiver(state, id);
    showUserToast('默认提货人已更新');
}

function askRemove(id: number) {
    removeDialog.id = id;
    removeDialog.visible = true;
}

function removeCurrent() {
    removeReceiver(state, removeDialog.id);
    removeDialog.visible = false;
    showUserToast('提货人已删除');
}
</script>

<style lang="scss" scoped>
.receiver-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
}

.receiver-row > view:first-child {
  min-width: 0;
}

.receiver-row > view:first-child > text:first-child {
  display: block;
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
}

.receiver-actions {
  display: flex;
  gap: 10rpx;
}

.mobile-input {
  margin-top: 14rpx;
}

.add-btn {
  width: 100%;
  margin-top: 18rpx;
}
</style>
