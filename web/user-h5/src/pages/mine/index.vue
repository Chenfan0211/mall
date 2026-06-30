<template>
  <view class="page mine-page shop-page" data-m-page="mine">
    <template v-if="state.authenticated">
      <view class="mine-hero">
        <view class="mine-hero-row">
          <view class="avatar-img" aria-label="微信头像">{{ state.user.avatarText }}</view>
          <view class="mine-profile">
            <h2>{{ state.user.nickname }}</h2>
            <view class="member-chip">会员ID {{ state.user.id }} <span>鲜选会员</span></view>
          </view>
        </view>
      </view>

      <view class="mine-vip-card">
        <view>
          <b>鲜选会员</b>
          <span>本月已下单 {{ completedOrderCount }} 次</span>
        </view>
      </view>

      <view class="mine-card">
        <h3>
          我的订单
          <button class="btn" @click="open('/pages/order/index')">全部订单 ›</button>
        </h3>
        <view class="order-icons">
          <view v-for="item in orderEntries" :key="item.text" @click="open(item.url)">
            <i :data-order-icon="item.key" v-html="item.icon"></i>
            <em v-if="item.count > 0" class="mine-entry-badge">{{ item.count }}</em>
            <span>{{ item.text }}</span>
          </view>
        </view>
      </view>

      <view class="mine-card">
        <h3>
          当前自提点
          <button class="btn" @click="stationSheetVisible = true">切换自提点 ›</button>
        </h3>
        <view class="station-card" @click="stationSheetVisible = true">
          <b>{{ state.station.name }}</b>
          <span class="station-addr">地址：{{ state.station.address }}</span>
          <span class="station-phone">电话：{{ state.station.mobile || '暂无' }}</span>
        </view>
      </view>

      <view class="mine-card mine-service-card">
        <h3>我的服务</h3>
        <view class="service-icons compact-service">
          <view v-for="item in serviceEntries" :key="item.text" @click="open(item.url)">
            <i v-html="item.icon"></i>
            <em v-if="item.count > 0" class="mine-entry-badge">{{ item.count }}</em>
            <span>{{ item.text }}</span>
          </view>
        </view>
      </view>

      <view class="mine-logout">
        <button type="button" @click="logout">退出登录</button>
      </view>
    </template>

    <template v-else>
      <view class="mine-login-hero">
        <view class="avatar-img">未</view>
        <view>
          <h2>登录后查看</h2>
          <p>订单、售后、收藏和消息需要先登录。</p>
        </view>
      </view>
      <view class="mine-login-card">
        <b>登录后可查看个人信息</b>
        <span>完成微信授权后查看订单、售后和收藏。</span>
        <button @click="open('/pages/login/index')">去登录</button>
        <button class="plain" @click="open('/pages/home/index')">先逛逛</button>
      </view>
      <view class="mine-card login-view-card">
        <h3>登录后可查看</h3>
        <view class="mine-asset-group locked">
          <view v-for="item in loginPreviewEntries" :key="item.text" @click="open(item.url)">
            <b>{{ item.text }}</b>
            <span>{{ item.desc }}</span>
          </view>
        </view>
      </view>
    </template>

    <StationSheet :visible="stationSheetVisible" @close="stationSheetVisible = false" />
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

import StationSheet from '@/components/StationSheet.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import {
    logoutUser,
    navigateUser,
    useUserState
} from '@/stores/userState';

const state = useUserState();
const stationSheetVisible = ref(false);
const completedOrderCount = computed(() => state.localOrders.filter((item) => [50, 60].includes(item.tradeStatus)).length);
const unreadMessageCount = computed(() => state.localMessages.filter((item) => !item.read).length);
const pendingPayCount = computed(() => state.localOrders.filter((item) => item.tradeStatus === 10).length);
const preparingCount = computed(() => state.localOrders.filter((item) => item.tradeStatus === 20 || item.tradeStatus === 30).length);
const finishedCount = computed(() => state.localOrders.filter((item) => item.tradeStatus === 60).length);
const pickupCount = computed(() => state.localOrders.filter((item) => item.tradeStatus === 50).length);
const afterSaleCount = computed(() => state.localOrders.flatMap((item) => item.items).filter((item) => item.afterSaleStatus > 0).length);

const orderIconSvg = {
    unpaid: '<svg viewBox="0 0 24 24"><rect x="4.5" y="6" width="15" height="11.5" rx="2.2"></rect><path d="M4.5 10h15"></path><path d="M8 15h3"></path></svg>',
    paid: '<svg viewBox="0 0 24 24"><path d="M5 7.5h14v10H5z"></path><path d="M8 7.5V5h8v2.5"></path><path d="M8 12h8"></path></svg>',
    done: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="7"></circle><path d="m8.7 12.2 2.1 2.1 4.7-5"></path></svg>',
    pickup: '<svg viewBox="0 0 24 24"><path d="M5 10.5 12 5l7 5.5V19H5z"></path><path d="M9 19v-5h6v5"></path></svg>',
    refund: '<svg viewBox="0 0 24 24"><path d="M7 7h8.5a4.5 4.5 0 0 1 0 9H9"></path><path d="M7 7l3-3"></path><path d="M7 7l3 3"></path></svg>'
};
const serviceIconSvg = {
    favorites: '<svg viewBox="0 0 24 24"><path d="M12 20s-7-4.2-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.8-7 10-7 10Z"></path></svg>',
    history: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="7"></circle><path d="M12 8v4.5l3 1.8"></path></svg>',
    notice: '<svg viewBox="0 0 24 24"><path d="M6.5 10a5.5 5.5 0 0 1 11 0v3.5l1.5 2H5l1.5-2Z"></path><path d="M10 18a2 2 0 0 0 4 0"></path></svg>',
    messages: '<svg viewBox="0 0 24 24"><path d="M5 6.5h14v10H8l-3 3Z"></path><path d="M8 10h8"></path><path d="M8 13h5"></path></svg>',
    reviews: '<svg viewBox="0 0 24 24"><path d="m12 4 2.2 4.5 5 .7-3.6 3.5.8 5-4.4-2.4-4.4 2.4.8-5L4.8 9.2l5-.7Z"></path></svg>',
    receivers: '<svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="3.2"></circle><path d="M5.8 19a6.2 6.2 0 0 1 12.4 0"></path></svg>',
    station: '<svg viewBox="0 0 24 24"><path d="M5 10.5 12 5l7 5.5V19H5z"></path><path d="M8 12h8"></path></svg>',
    settings: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="2.8"></circle><path d="M19 12a7 7 0 0 0-.1-1.2l2-1.6-2-3.5-2.5 1a7 7 0 0 0-2.1-1.2L14 3h-4l-.4 2.5a7 7 0 0 0-2.1 1.2l-2.5-1-2 3.5 2 1.6A7 7 0 0 0 5 12c0 .4 0 .8.1 1.2l-2 1.6 2 3.5 2.5-1c.6.5 1.3.9 2.1 1.2L10 21h4l.4-2.5c.8-.3 1.5-.7 2.1-1.2l2.5 1 2-3.5-2-1.6c.1-.4.1-.8.1-1.2Z"></path></svg>'
};

const orderEntries = computed(() => [
    { key: 'unpaid', text: '待付款', count: pendingPayCount.value, icon: orderIconSvg.unpaid, url: '/pages/order/index?status=unpaid' },
    { key: 'paid', text: '备货中', count: preparingCount.value, icon: orderIconSvg.paid, url: '/pages/order/index?status=preparing' },
    { key: 'done', text: '已完成', count: finishedCount.value, icon: orderIconSvg.done, url: '/pages/order/index?status=done' },
    { key: 'pickup', text: '待提货', count: pickupCount.value, icon: orderIconSvg.pickup, url: '/pages/order/index?status=pickup' },
    { key: 'refund', text: '售后', count: afterSaleCount.value, icon: orderIconSvg.refund, url: '/pages/order/index?status=aftersale' }
]);
const serviceEntries = computed(() => [
    { text: '我的收藏', count: state.favorites.size, icon: serviceIconSvg.favorites, url: '/pages/favorite/index' },
    { text: '浏览历史', count: 0, icon: serviceIconSvg.history, url: '/pages/history/index' },
    { text: '商品通知', count: state.notices.size, icon: serviceIconSvg.notice, url: '/pages/notice/index' },
    { text: '站内消息', count: unreadMessageCount.value, icon: serviceIconSvg.messages, url: '/pages/message/index' },
    { text: '我的评价', count: state.localReviews.length, icon: serviceIconSvg.reviews, url: '/pages/reviews/mine' },
    { text: '提货人', count: state.receivers.length, icon: serviceIconSvg.receivers, url: '/pages/receivers/index' },
    { text: '团长申请', count: 0, icon: serviceIconSvg.station, url: '/pages/station/apply' },
    { text: '设置', count: 0, icon: serviceIconSvg.settings, url: '/pages/settings/index' }
]);
const loginPreviewEntries = [
    { text: '站内消息', desc: '查看未读消息', url: '/pages/message/index' },
    { text: '我的收藏', desc: '查看收藏商品', url: '/pages/favorite/index' },
    { text: '浏览历史', desc: '查看最近浏览', url: '/pages/history/index' },
    { text: '商品通知', desc: '查看到货提醒', url: '/pages/notice/index' }
];

function open(url: string) {
    navigateUser(url, url === '/pages/home/index');
}

function logout() {
    logoutUser();
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  padding: 0 0 172rpx;
  background: #f7f1ea;
  overflow-y: auto;
  scrollbar-width: none;
}

.mine-page::-webkit-scrollbar {
  display: none;
}

.mine-hero {
  position: relative;
  min-height: 384rpx;
  padding: 108rpx 40rpx 104rpx;
  overflow: hidden;
  color: #ffffff;
  background:
    linear-gradient(90deg, rgba(20, 24, 31, 0.78), rgba(183, 75, 44, 0.58)),
    url("/static/user-home/login-market.svg");
  background-size: cover;
  background-position: center;
}

.mine-hero::after {
  position: absolute;
  inset: auto 0 0;
  height: 108rpx;
  pointer-events: none;
  content: "";
  background: linear-gradient(180deg, transparent, #f7f1ea);
}

.mine-hero-row {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 32rpx;
  min-width: 0;
}

.avatar-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 144rpx;
  min-width: 144rpx;
  height: 144rpx;
  color: #c2412d;
  background: #fff7ef;
  border-radius: 40rpx;
  box-shadow: inset 0 0 0 1rpx rgba(255, 255, 255, 0.6), 0 20rpx 48rpx rgba(0, 0, 0, 0.18);
  font-size: 58rpx;
  font-weight: 500;
  line-height: 1;
}

.mine-profile {
  min-width: 0;
}

.mine-profile h2 {
  margin: 0;
  overflow: hidden;
  color: #ffffff;
  font-size: 56rpx;
  font-weight: 900;
  line-height: 1.1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-chip {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  max-width: 420rpx;
  min-height: 44rpx;
  margin-top: 20rpx;
  padding: 10rpx 20rpx;
  overflow: hidden;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 800;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-chip span {
  color: #ffffff;
  font-weight: 900;
}

.mine-vip-card {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 136rpx;
  margin: -80rpx 36rpx 24rpx;
  padding: 28rpx 32rpx;
  background: #fff8f2;
  border-radius: 28rpx;
  box-shadow: 0 18rpx 44rpx rgba(126, 76, 49, 0.12);
}

.mine-vip-card b,
.mine-vip-card span {
  display: block;
}

.mine-vip-card b {
  color: #172033;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 1;
}

.mine-vip-card span {
  margin-top: 16rpx;
  color: #a65a2d;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1.25;
}

.mine-card {
  margin: 20rpx 32rpx;
  padding: 32rpx 28rpx;
  background: #ffffff;
  border: 1rpx solid rgba(126, 76, 49, 0.08);
  border-radius: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(126, 76, 49, 0.07);
}

.mine-card h3 {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin: 0 0 28rpx;
  color: #172033;
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.15;
}

.mine-card h3 .btn {
  min-height: 68rpx;
  padding: 0 24rpx;
  color: #d84d38 !important;
  background: #ffffff !important;
  border: 1rpx solid #f1bfb1;
  border-radius: 999rpx;
  box-shadow: none;
  font-size: 26rpx;
  font-weight: 900;
  white-space: nowrap;
}

.order-icons,
.service-icons {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 16rpx;
  text-align: center;
}

.service-icons.compact-service {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 28rpx 24rpx;
}

.order-icons > view,
.service-icons > view {
  position: relative;
  display: grid;
  justify-items: center;
  gap: 10rpx;
  min-width: 0;
  color: #172033;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 1.15;
  white-space: nowrap;
}

.order-icons span,
.service-icons span {
  overflow: hidden;
  max-width: 100%;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-icons i,
.service-icons i {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 76rpx;
  height: 76rpx;
  color: #d84d38;
  background: #fff6f1;
  border: 1rpx solid #f2cbbf;
  border-radius: 26rpx;
  font-style: normal;
}

.order-icons i :deep(svg),
.service-icons i :deep(svg) {
  width: 48rpx;
  height: 48rpx;
  fill: none;
  stroke: currentColor;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 2;
}

.mine-entry-badge {
  position: absolute;
  top: -8rpx;
  left: calc(50% + 18rpx);
  z-index: 2;
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  color: #ffffff;
  background: #b7301c;
  border-radius: 999rpx;
  font-size: 25rpx;
  font-style: normal;
  font-weight: 900;
  line-height: 36rpx;
}

.station-card {
  min-height: 232rpx;
  padding: 28rpx;
  color: #172033;
  background: #fffaf6;
  border: 1rpx solid #efd6ca;
  border-radius: 24rpx;
  line-height: 1.55;
  box-shadow: none;
}

.station-card b,
.station-card span {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.station-card b {
  margin-bottom: 20rpx;
  font-size: 32rpx;
  font-weight: 900;
  line-height: 1.2;
}

.station-card span {
  margin-top: 10rpx;
  color: #6d5145;
  font-size: 26rpx;
  font-weight: 800;
  line-height: 1.35;
  white-space: normal;
}

.station-card .station-addr {
  padding-bottom: 12rpx;
  border-bottom: 1rpx solid rgba(239, 214, 202, 0.78);
}

.station-card .station-phone {
  margin-top: 16rpx;
  color: #7d5340;
  white-space: nowrap;
}

.mine-logout {
  margin: 20rpx 32rpx 36rpx;
  padding-bottom: 144rpx;
}

.mine-logout button {
  width: 100%;
  min-height: 88rpx;
  color: #c2412d !important;
  background: #ffffff !important;
  border: 1rpx solid #f2d6c4;
  box-shadow: none;
}

.mine-login-hero {
  position: relative;
  display: grid;
  grid-template-columns: 144rpx minmax(0, 1fr);
  gap: 32rpx;
  align-items: center;
  min-height: 384rpx;
  padding: 108rpx 40rpx 104rpx;
  overflow: hidden;
  color: #ffffff;
  background:
    linear-gradient(90deg, rgba(20, 24, 31, 0.78), rgba(183, 75, 44, 0.58)),
    url("/static/user-home/login-market.svg") center / cover;
}

.mine-login-hero::after {
  position: absolute;
  inset: auto 0 0;
  height: 108rpx;
  pointer-events: none;
  content: "";
  background: linear-gradient(180deg, transparent, #f7f1ea);
}

.mine-login-hero > view {
  position: relative;
  z-index: 1;
}

.mine-login-hero h2 {
  margin: 0 0 12rpx;
  color: #ffffff;
  font-size: 48rpx;
  line-height: 1.15;
}

.mine-login-hero p {
  margin: 0;
  color: rgba(255, 255, 255, 0.86);
  font-size: 26rpx;
  line-height: 1.5;
}

.mine-login-card {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 16rpx;
  margin: -80rpx 32rpx 20rpx;
  padding: 28rpx;
  background: #ffffff;
  border: 1rpx solid rgba(126, 76, 49, 0.08);
  border-radius: 32rpx;
  box-shadow: 0 18rpx 44rpx rgba(126, 76, 49, 0.12);
}

.mine-login-card b {
  color: #172033;
  font-size: 34rpx;
}

.mine-login-card span {
  color: #7b4a32;
  font-size: 26rpx;
  line-height: 1.5;
}

.mine-login-card button {
  min-height: 80rpx;
  color: #ffffff !important;
  background: linear-gradient(135deg, #e85d3f, #f28a42) !important;
  border-radius: 999rpx;
  font-weight: 900;
}

.mine-login-card button.plain {
  color: #c2412d !important;
  background: #fff0e8 !important;
  border: 1rpx solid #f2d6c4;
}

.login-view-card {
  margin-top: 18rpx;
}

.mine-asset-group {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.mine-asset-group > view {
  min-height: 116rpx;
  padding: 20rpx;
  background: #fffaf6;
  border: 1rpx solid #f0dfd6;
  border-radius: 22rpx;
}

.mine-asset-group.locked > view {
  background: #f4f7f4;
  opacity: 0.92;
}

.mine-asset-group b,
.mine-asset-group span {
  display: block;
}

.mine-asset-group b {
  color: #172033;
  font-size: 27rpx;
  font-weight: 900;
}

.mine-asset-group span {
  margin-top: 8rpx;
  color: #8c6a58;
  font-size: 25rpx;
  line-height: 1.38;
}
</style>
