<template>
  <view v-if="visible" class="sheet-mask station-mask show" @click="$emit('close')">
    <view class="sheet-panel station-sheet station-panel" @click.stop>
      <view class="sheet-head station-panel-head">
        <view>
          <h3>选择自提点</h3>
          <span>{{ activeCityName }} · {{ availableCount }} 个可选</span>
        </view>
        <button class="sheet-close" @click="$emit('close')">×</button>
      </view>
      <label class="station-search">
        <input v-model="keyword" class="station-search-input" placeholder="搜索自提点名称或地址" />
      </label>
      <view class="entry-city-row station-city-row">
        <view class="station-city-chip-row">
          <view class="station-city-chip locating">定位当前城市</view>
          <view
            v-for="item in state.cities"
            :key="item.id"
            class="station-city-chip"
            :class="{ active: item.id === sheetCityId }"
            role="button"
            @click="selectCity(item)"
          >
            {{ item.name }}
          </view>
        </view>
      </view>
      <view class="station-list station-option-list">
        <button
          v-for="item in filteredStations"
          :key="item.id"
          class="station-option"
          :class="{ active: item.id === state.station.id, disabled: item.status !== 1, abnormal: item.status !== 1 }"
          :aria-disabled="item.status !== 1"
          @click="select(item)"
        >
          <view>
            <b>{{ item.name }}</b>
            <span>{{ item.address }}</span>
            <span class="station-option-meta">{{ item.mobile || '电话待完善' }} · {{ item.distance || '附近' }} · 提货 {{ item.deliveryTime || '待定' }}</span>
            <span class="station-option-hours">营业 {{ item.businessHours || '营业时间待定' }}</span>
            <span v-if="item.status !== 1" class="station-option-reason">{{ item.abnormalReason || '当前自提点不可选' }}</span>
          </view>
          <em>{{ item.id === state.station.id ? '当前' : item.status === 1 ? '选择' : '休假' }}</em>
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
import { computed, ref, watch } from 'vue';

import ConfirmDialog from '@/components/ConfirmDialog.vue';
import { type CityState, type StationState, showUserToast, switchCurrentStation, useUserState } from '@/stores/userState';

const props = defineProps<{
    visible: boolean;
}>();

const emit = defineEmits<{
    close: [];
}>();

const state = useUserState();
const keyword = ref('');
const sheetCityId = ref(state.city.id);
const pendingStation = ref<StationState>();
const confirmVisible = ref(false);
const activeCityName = computed(() => state.cities.find((item) => item.id === sheetCityId.value)?.name || state.city.name);
const cityStations = computed(() => state.stations.filter((item) => item.cityId === sheetCityId.value));
const availableCount = computed(() => cityStations.value.filter((item) => item.status === 1).length);
const filteredStations = computed(() => {
    const value = keyword.value.trim();
    if (!value) {
        return cityStations.value;
    }
    return cityStations.value.filter((item) => `${item.name}${item.address}`.includes(value));
});
const confirmContent = computed(() => {
    const next = pendingStation.value;
    if (!next) {
        return '确认切换自提点？';
    }
    return `当前购物车已有商品，将切换到 ${next.name}。确认后将清空购物车。`;
});

function selectCity(item: CityState) {
    keyword.value = '';
    sheetCityId.value = item.id;
}

function select(item: StationState) {
    if (item.status !== 1) {
        showUserToast(item.abnormalReason || '当前自提点休假或停用，暂不可选', 'warn');
        return;
    }
    if (state.station.id === item.id) {
        emit('close');
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

watch(
    () => props.visible,
    (visible) => {
        if (visible) {
            sheetCityId.value = state.city.id;
            keyword.value = '';
        }
    }
);
</script>

<style lang="scss" scoped>
.station-mask {
  position: fixed;
  inset: 0;
  z-index: 70;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: rgba(15, 23, 42, 0.42);
}

.station-panel {
  width: min(100%, 390px);
  max-height: 620px;
  padding: 28rpx 28rpx calc(36rpx + env(safe-area-inset-bottom));
  overflow: hidden;
  background: #fffaf6;
  border-radius: 44rpx 44rpx 0 0;
  box-shadow: 0 -36rpx 84rpx rgba(15, 23, 42, 0.22);
}

.station-panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24rpx;
  margin-bottom: 20rpx;
}

.station-panel-head h3 {
  margin: 0;
  color: #172033;
  font-size: 40rpx;
  font-weight: 900;
  line-height: 1.18;
}

.station-panel-head span {
  display: block;
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 26rpx;
  font-weight: 800;
}

.sheet-close {
  flex: 0 0 auto;
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  padding: 0;
  color: #7b5f51 !important;
  background: #f3e6dc !important;
  border: 0;
  border-radius: 50% !important;
  box-shadow: none;
  font-size: 38rpx;
  font-weight: 700;
  line-height: 1;
}

.station-search {
  display: block;
  margin-bottom: 18rpx;
}

.station-search-input {
  height: 76rpx;
  min-height: 76rpx;
  padding: 0 28rpx;
  color: #172033;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 20rpx;
  font-size: 26rpx;
  font-weight: 800;
}

.station-city-row {
  display: block;
  margin: 0 -28rpx 20rpx;
  padding: 0 28rpx;
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  scrollbar-width: none;
}

.station-city-row::-webkit-scrollbar {
  display: none;
}

.station-city-chip-row {
  display: inline-flex;
  gap: 12rpx;
  min-width: max-content;
}

.station-city-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  min-width: 118rpx;
  min-height: 58rpx;
  margin-right: 16rpx;
  padding: 0 24rpx;
  color: #7b5f51 !important;
  background: #ffffff !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx !important;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
}

.station-city-chip.active {
  color: #ffffff !important;
  background: #e85d3f !important;
  border-color: #e85d3f;
}

.station-city-chip.locating {
  color: #b58b73 !important;
  background: #f7efe9 !important;
}

.station-option-list {
  display: grid;
  gap: 16rpx;
  max-height: calc(80vh - 292rpx);
  overflow-y: auto;
  padding-right: 2rpx;
}

.station-option {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: start;
  gap: 18rpx;
  width: 100%;
  min-height: 0;
  padding: 20rpx 22rpx;
  color: #172033 !important;
  text-align: left;
  background: #ffffff !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 18rpx !important;
  box-shadow: none;
}

.station-option.active {
  background: #fff4ec !important;
  border-color: #e85d3f;
}

.station-option.abnormal {
  background: #f4f2ef !important;
  border-color: #e3ddd8;
  opacity: 0.78;
}

.station-option > view {
  min-width: 0;
}

.station-option b,
.station-option span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-option b {
  color: #172033;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.22;
}

.station-option span {
  margin-top: 7rpx;
  color: #7b5f51;
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.28;
}

.station-option .station-option-meta,
.station-option .station-option-hours {
  color: #8c6a58;
  font-size: 25rpx;
}

.station-option .station-option-reason {
  width: fit-content;
  max-width: 100%;
  padding: 5rpx 12rpx;
  color: #8b6b5b;
  background: #ebe5df;
  border-radius: 999rpx;
}

.station-option em {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72rpx;
  min-height: 42rpx;
  padding: 0 14rpx;
  color: #e85d3f;
  background: #fff1e9;
  border: 1rpx solid #f2d6c4;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-style: normal;
  font-weight: 900;
}

.station-option.abnormal em {
  color: #7b7069;
  background: #e9e4df;
  border-color: #ddd5cf;
}

.station-panel {
  max-height: 620px;
  padding: 28rpx 28rpx calc(36rpx + env(safe-area-inset-bottom));
  border-radius: 44rpx 44rpx 0 0;
}

.station-panel-head {
  margin-bottom: 20rpx;
}

.station-panel-head h3 {
  font-size: 40rpx;
}

.station-panel-head span {
  font-size: 26rpx;
}

.sheet-close {
  width: 68rpx;
  min-width: 68rpx;
  height: 68rpx;
  min-height: 68rpx;
  font-size: 38rpx;
}

.station-panel {
  position: relative;
}

.station-panel-head {
  padding-right: 84rpx;
}

.station-panel-head .sheet-close {
  position: absolute;
  top: 24rpx;
  right: 24rpx;
  z-index: 3;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin: 0;
}

.station-search {
  display: flex;
  align-items: center;
  height: 76rpx;
  margin-bottom: 20rpx;
  padding: 0 24rpx;
  background: #ffffff;
  border: 1rpx solid #f0dfd6;
  border-radius: 40rpx;
}

.station-search-input {
  height: 72rpx;
  min-height: 72rpx;
  padding: 0;
  background: transparent;
  border: 0;
  border-radius: 0;
  font-size: 26rpx;
}

.station-city-row {
  margin: 0 -28rpx 20rpx;
  padding: 0 28rpx;
}

.station-city-chip {
  min-width: 116rpx;
  min-height: 58rpx;
  margin-right: 16rpx;
  padding: 0 24rpx;
  border-radius: 999rpx !important;
  font-size: 26rpx;
}

.station-option-list {
  gap: 18rpx;
  max-height: calc(80vh - 294rpx);
}

.station-option {
  min-height: 0;
  padding: 22rpx 24rpx;
  border-color: #f0dfd6;
  border-radius: 28rpx !important;
}

.station-option.active {
  background: #fff1e9 !important;
  border-color: #e85d3f;
}

.station-option.abnormal {
  background: #fff7f1 !important;
  border-color: #efc8b7;
  opacity: 0.86;
}

.station-option b {
  margin-bottom: 8rpx;
  font-size: 30rpx;
}

.station-option span {
  margin-top: 0;
  font-size: 26rpx;
  line-height: 1.45;
}

.station-option .station-option-meta,
.station-option .station-option-hours {
  font-size: 25rpx;
}

.station-option em {
  align-self: start;
  min-width: 72rpx;
  min-height: 42rpx;
  color: #e85d3f;
  background: transparent;
  border: 0;
  font-size: 26rpx;
  white-space: nowrap;
}
</style>
