<template>
  <view v-if="visible" class="sheet-mask" @click="$emit('close')">
    <view class="sheet-panel station-sheet" @click.stop>
      <view class="sheet-head">
        <view>
          <text class="sheet-title">选择自提点</text>
          <text class="sheet-sub">切换后会刷新商品和购物车状态</text>
        </view>
        <button class="sheet-close" @click="$emit('close')">×</button>
      </view>
      <input v-model="keyword" class="station-search-input" placeholder="搜索自提点名称或地址" />
      <view class="station-list">
        <button
          v-for="item in filteredStations"
          :key="item.id"
          class="station-option"
          :class="{ active: item.id === state.station.id, disabled: item.status !== 1 }"
          @click="select(item)"
        >
          <view>
            <text>{{ item.name }}</text>
            <text>{{ item.address }}</text>
            <text>{{ item.mobile }} · {{ item.distance || '附近' }}</text>
            <text v-if="item.status !== 1">{{ item.abnormalReason || '当前自提点不可选' }}</text>
          </view>
          <text>{{ item.id === state.station.id ? '当前' : item.status === 1 ? '选择' : '休假' }}</text>
        </button>
      </view>
      <ConfirmDialog
        :visible="confirmVisible"
        title="切换自提点"
        :content="confirmContent"
        confirm-text="确认切换"
        @cancel="confirmVisible = false"
        @confirm="confirmSwitch"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import { type StationState, switchCurrentStation, useUserState } from '@/stores/userState';

defineProps<{
    visible: boolean;
}>();

const emit = defineEmits<{
    close: [];
}>();

const state = useUserState();
const keyword = ref('');
const pendingStation = ref<StationState>();
const confirmVisible = ref(false);
const filteredStations = computed(() => {
    const value = keyword.value.trim();
    if (!value) {
        return state.stations;
    }
    return state.stations.filter((item) => `${item.name}${item.address}`.includes(value));
});
const confirmContent = computed(() => {
    const next = pendingStation.value;
    if (!next) {
        return '确认切换自提点？';
    }
    return `当前购物车已有商品，将切换到 ${next.name}。确认后将清空购物车。`;
});

function select(item: StationState) {
    if (item.status !== 1) {
        switchCurrentStation(item);
        return;
    }
    const shouldConfirm = state.station.id !== item.id && state.cartItems.some((row) => row.valid && row.qty > 0);
    if (shouldConfirm) {
        pendingStation.value = item;
        confirmVisible.value = true;
        return;
    }
    const result = switchCurrentStation(item);
    if (result.switched) {
        emit('close');
    }
}

function confirmSwitch() {
    if (!pendingStation.value) {
        return;
    }
    const result = switchCurrentStation(pendingStation.value);
    confirmVisible.value = false;
    pendingStation.value = undefined;
    if (result.switched) {
        emit('close');
    }
}
</script>
