<template>
  <view class="service-page shop-page" data-m-page="receivers">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>提货人信息</b>
        <span>{{ state.receivers.length }}条</span>
      </view>
    </view>

    <view class="service-list">
      <view class="receiver-add-card">
        <view>
          <b>新增提货人</b>
          <span>填写姓名和手机号。</span>
        </view>
        <button class="service-action" @click="formVisible = !formVisible">{{ formVisible ? '收起' : '新增' }}</button>
      </view>

      <view v-if="formVisible" class="service-form-card service-card">
        <h3>{{ editingId ? '编辑提货人' : '新增提货人' }}</h3>
        <input v-model="name" placeholder="提货人姓名" />
        <input v-model="mobile" placeholder="手机号" />
        <button class="primary" @click="saveReceiver">{{ editingId ? '保存修改' : '保存提货人' }}</button>
      </view>

      <view
        v-for="item in sortedReceivers"
        :key="item.id"
        class="receiver-row"
        :class="{ default: item.defaultFlag }"
      >
        <view>
          <b>{{ item.name }} {{ item.mobile }}</b>
          <span>手机号 {{ item.mobile }}<br />{{ item.defaultFlag ? '结算页默认使用' : '备用提货人' }}</span>
          <view class="receiver-tags">
            <em v-if="item.defaultFlag">默认</em>
          </view>
        </view>
        <view class="receiver-actions">
          <button class="service-action" :class="{ used: item.defaultFlag }" @click="makeDefault(item.id)">
            {{ item.defaultFlag ? '默认' : '设默认' }}
          </button>
          <button class="service-action" @click="editReceiver(item.id)">编辑</button>
          <button class="service-action danger" @click="askRemove(item.id)">删除</button>
        </view>
      </view>

      <EmptyActionCard
        v-if="state.receivers.length === 0"
        title="暂无提货人信息"
        sub="下单前会用授权手机号生成默认提货人，也可手动新增。"
        icon="人"
        button-text="返回我的"
        @action="goMine"
      />
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
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { addReceiver, navigateUser, removeReceiver, setDefaultReceiver, showUserToast, updateReceiver, useUserState } from '@/stores/userState';

const state = useUserState();
const name = ref('');
const mobile = ref('');
const formVisible = ref(false);
const editingId = ref(0);
const removeDialog = reactive({
    visible: false,
    id: 0
});
const sortedReceivers = computed(() => [...state.receivers].sort((left, right) => Number(right.defaultFlag) - Number(left.defaultFlag)));

function saveReceiver() {
    if (!name.value.trim() || !/^1\d{10}$/.test(mobile.value.trim())) {
        showUserToast('请填写正确的提货人姓名和手机号', 'warn');
        return;
    }
    const wasEditing = Boolean(editingId.value);
    if (editingId.value) {
        updateReceiver(state, editingId.value, name.value.trim(), mobile.value.trim());
    } else {
        addReceiver(state, name.value.trim(), mobile.value.trim());
    }
    name.value = '';
    mobile.value = '';
    editingId.value = 0;
    formVisible.value = false;
    showUserToast(wasEditing ? '提货人已更新' : '提货人已保存');
}

function makeDefault(id: number) {
    setDefaultReceiver(state, id);
    showUserToast('默认提货人已更新');
}

function editReceiver(id: number) {
    const receiver = state.receivers.find((item) => item.id === id);
    if (!receiver) {
        return;
    }
    editingId.value = id;
    name.value = receiver.name;
    mobile.value = receiver.mobile;
    formVisible.value = true;
    showUserToast('已带入提货人信息，可修改后保存');
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

function goMine() {
    navigateUser('/pages/mine/index', true);
}
</script>
