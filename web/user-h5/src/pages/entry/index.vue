<template>
  <view class="page no-tab entry-page entry-select-page">
    <view class="entry-select-shell">
      <view class="entry-welcome">
        <text class="entry-welcome-title">选择城市和自提点</text>
      </view>

      <view class="entry-select-card">
        <view class="entry-city-panel">
          <view class="entry-section-head">
            <view>
              <h3>选择城市</h3>
              <p>定位失败时可手动切换已开通城市</p>
            </view>
            <em>必选</em>
          </view>
          <view class="city-scroll">
            <view class="city-chip-row entry-city-row">
              <button class="city-chip locating" disabled>定位当前城市</button>
              <button
                v-for="city in state.cities"
                :key="city.id"
                class="city-chip"
                :class="{ active: city.id === state.city.id }"
                @click="selectCity(city)"
              >
                {{ city.name }}
              </button>
            </view>
          </view>
        </view>

        <view class="entry-station-panel">
          <view class="entry-section-head">
            <view>
              <h3>选择自提点</h3>
              <p>{{ loadingStations ? '正在获取附近自提点' : `${availableCount} 个营业中自提点` }}</p>
            </view>
            <em>下一步</em>
          </view>
          <view v-if="loadingStations" class="station-loading">正在加载附近自提点...</view>
          <view v-else-if="cityStations.length === 0" class="station-loading">当前城市暂无自提点</view>
          <view v-else class="entry-station-list">
            <button
              v-for="item in cityStations"
              :key="item.id"
              class="entry-station"
              :class="{ active: item.id === state.station.id, abnormal: item.status !== 1 }"
              @click="select(item)"
            >
              <span>
                <b>{{ item.name }}</b>
                <span>{{ item.address }}</span>
                <span>{{ item.distance || '附近' }} · 提货 {{ item.deliveryTime || item.businessHours }}</span>
                <span v-if="item.status !== 1">{{ item.abnormalReason }}</span>
              </span>
              <em>{{ item.id === state.station.id ? '当前' : item.status === 1 ? '可选' : '暂不可选' }}</em>
            </button>
          </view>
        </view>
      </view>
    </view>

    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import UserToast from '@/components/UserToast.vue';
import { pageStations } from '@/api/user';
import {
    type CityState,
    type StationState,
    locateCurrentCity,
    navigateUser,
    resetStationsToFallback,
    showUserToast,
    switchCurrentCity,
    switchStation,
    syncStationsFromApi,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const loadingStations = ref(false);
const cityStations = computed(() => state.stations.filter((item) => item.cityId === state.city.id));
const availableCount = computed(() => cityStations.value.filter((item) => item.status === 1).length);

onMounted(async () => {
    await loadStations();
    locateCurrentCity();
});

function selectCity(city: CityState) {
    switchCurrentCity(city);
}

function select(item: StationState) {
    const result = switchStation(state, item);
    if (item.status !== 1) {
        showUserToast(result.message, 'warn');
        return;
    }
    navigateUser('/pages/home/index', true);
}

async function loadStations() {
    loadingStations.value = true;
    try {
        const page = await pageStations({ pageNum: 1, pageSize: 100 }, { silent: true });
        const result = syncStationsFromApi(state, page?.list || []);
        if (result.fallback) {
            resetStationsToFallback(state);
            console.info(result.message);
        }
    } catch (error) {
        resetStationsToFallback(state);
        console.info('自提点接口不可用，已展示本地兜底数据');
    } finally {
        loadingStations.value = false;
    }
}
</script>

<style lang="scss" scoped>
.entry-page {
  padding: 0 20rpx 56rpx;
  overflow-x: hidden;
  background: linear-gradient(180deg, #fff7f1 0%, #f7f1ea 100%);
}

.entry-select-shell {
  display: grid;
  gap: 18rpx;
  min-width: 0;
  margin: 0;
}

.entry-welcome {
  min-height: 176rpx;
  margin: 0 -20rpx;
  padding: 72rpx 32rpx 28rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #e85d3f 0%, #f39a4f 100%);
  border-radius: 0 0 44rpx 44rpx;
  box-shadow: 0 28rpx 56rpx rgba(232, 93, 63, 0.18);
}

.entry-welcome-title {
  display: block;
  margin-top: 0;
  color: #ffffff;
  font-size: 46rpx;
  font-weight: 900;
  line-height: 1.22;
}

.entry-select-card {
  display: grid;
  gap: 24rpx;
  min-width: 0;
  margin: 0;
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.98);
  border: 1rpx solid #f0dfd6;
  border-radius: 36rpx;
  box-shadow: 0 24rpx 48rpx rgba(111, 62, 31, 0.08);
}

.entry-city-panel,
.entry-station-panel {
  min-width: 0;
}

.entry-section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.entry-section-head h3,
.entry-section-head p {
  margin: 0;
}

.entry-section-head h3 {
  color: #172033;
  font-size: 34rpx;
  line-height: 1.25;
}

.entry-section-head p {
  margin-top: 8rpx;
  color: #8f6b59;
  font-size: 26rpx;
  line-height: 1.4;
}

.entry-section-head em {
  flex: 0 0 auto;
  min-height: 48rpx;
  padding: 0 16rpx;
  color: #e85d3f;
  background: #fff1e9;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-style: normal;
  font-weight: 900;
  line-height: 48rpx;
  white-space: nowrap;
}

.city-scroll {
  display: block;
  min-width: 0;
  max-width: 100%;
  width: 100%;
  overflow: visible;
  white-space: normal;
}

.city-chip-row {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 14rpx 12rpx;
  width: 100%;
  min-width: 100%;
  padding-right: 0;
  padding-bottom: 4rpx;
}

.city-chip {
  flex: 0 1 auto;
  max-width: 100%;
  min-width: 96rpx;
  min-height: 56rpx;
  padding: 0 22rpx;
  overflow: hidden;
  color: #8c6a58 !important;
  background: #fff7f2 !important;
  border: 1rpx solid #f0dfd6;
  border-radius: 999rpx;
  box-shadow: none;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.city-chip.active {
  color: #ffffff !important;
  background: #d94b34 !important;
  border-color: #d94b34;
}

.station-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 180rpx;
  color: #8c6a58;
  background: #fffaf6;
  border: 1rpx dashed #eadbd3;
  border-radius: 22rpx;
  font-size: 26rpx;
  font-weight: 800;
}

.entry-station-list {
  display: grid;
  gap: 16rpx;
  min-width: 0;
  max-height: 620rpx;
  overflow-y: auto;
  overflow-x: hidden;
}

.entry-station {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 20rpx;
  align-items: center;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  min-height: 150rpx;
  padding: 20rpx;
  color: #172033 !important;
  background: #fffdfb !important;
  border: 1rpx solid #eadbd3;
  border-radius: 18rpx;
  text-align: left;
  box-shadow: 0 4rpx 14rpx rgba(126, 76, 49, 0.05);
}

.entry-station > span {
  min-width: 0;
}

.entry-station.active {
  border-color: #f07855;
  background: #fff4ed !important;
  box-shadow: 0 6rpx 16rpx rgba(217, 75, 52, 0.1);
}

.entry-station.abnormal {
  color: #8d817a !important;
  background: #faf6f2 !important;
  border-color: #eaded6;
  box-shadow: none;
  opacity: 0.88;
}

.entry-station b,
.entry-station span {
  display: block;
}

.entry-station b {
  overflow: hidden;
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1.25;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entry-station span span {
  margin-top: 8rpx;
  overflow: hidden;
  color: #7b5f51;
  font-size: 26rpx;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entry-station em {
  flex: 0 0 auto;
  min-width: 82rpx;
  min-height: 44rpx;
  padding: 0 14rpx;
  color: #c2412d;
  background: #fff4ee;
  border: 1rpx solid #f5d6c7;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-style: normal;
  font-weight: 900;
  line-height: 44rpx;
  text-align: center;
  white-space: nowrap;
}

.entry-station.abnormal em {
  color: #96887e;
  background: #eee7e2;
  border-color: #e2d7d0;
}

.entry-select-page .city-scroll {
  overflow: visible;
  white-space: normal;
}

.entry-select-page .city-chip-row.entry-city-row {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 10rpx 8rpx;
  width: 100%;
  min-width: 0;
  margin: 0;
  padding: 0 0 4rpx;
  overflow: visible;
  white-space: normal;
}

.entry-select-page .city-chip-row.entry-city-row .city-chip {
  flex: 0 0 auto;
  width: auto;
  min-width: 0;
  min-height: 56rpx;
  margin: 0;
  padding: 0 18rpx;
}

.entry-select-page .city-chip-row.entry-city-row .city-chip.locating {
  min-width: auto;
  padding: 0 20rpx;
}
</style>
