<template>
  <view class="service-page shop-page station-apply-page" data-m-page="stationApply">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>团长申请</b>
        <span>审核状态</span>
      </view>
    </view>

    <view class="service-list">
      <view class="mine-card">
        <h3>申请类型</h3>
        <view class="apply-type-grid">
          <view class="apply-type-card active">
            <b>团长身份</b>
            <span>已选择</span>
          </view>
          <view class="apply-type-card">
            <b>自提点服务</b>
            <span>可选择</span>
          </view>
        </view>
      </view>

      <view class="mine-card">
        <h3>团长申请信息</h3>
        <view class="apply-station-form">
          <label>
            姓名
            <input v-model="contactName" aria-label="姓名" />
          </label>
          <label>
            手机号
            <input v-model="contactMobile" aria-label="手机号" />
          </label>
          <label>
            备注
            <textarea v-model="applyReason" maxlength="120" aria-label="备注" />
          </label>
          <label>
            门店名称
            <input v-model="storeName" aria-label="门店名称" />
          </label>
          <label>
            地址
            <textarea v-model="storeAddress" class="compact-address" maxlength="120" aria-label="地址" />
          </label>
        </view>
        <view class="apply-locked-note">审核中，暂不可修改。</view>
      </view>

      <view class="mine-card">
        <h3>审核状态</h3>
        <view class="apply-status-list">
          <view class="apply-status-card">
            <b>审核中</b>
            <span>AP202606290001</span>
            <span>团长身份 · 已提交资料</span>
          </view>
          <view class="apply-status-card success">
            <b>已通过</b>
            <span>历史申请记录</span>
            <span>资料完整时会同步开通服务权限</span>
          </view>
          <view class="apply-status-card warning">
            <b>已驳回</b>
            <span>示例记录</span>
            <span>地址不清晰或手机号无效会被驳回</span>
          </view>
        </view>
      </view>

      <view class="mine-card">
        <h3>申请历史</h3>
        <view class="apply-history-list">
          <view class="apply-status-card">
            <b>审核中 · 团长身份</b>
            <span>AP202606290001｜2026-06-29 10:30</span>
            <span>资料已提交，等待平台审核。</span>
          </view>
          <view class="apply-status-card success">
            <b>已通过 · 自提点服务</b>
            <span>AP202606120018｜2026-06-12 15:20</span>
            <span>资料完整，已同步开通服务权限。</span>
          </view>
        </view>
      </view>

      <view class="mine-card">
        <h3>身份标识</h3>
        <view class="identity-badge-row">
          <view class="identity-badge">
            <b>团长身份</b>
            已开通
          </view>
          <view class="identity-badge">
            <b>自提点身份</b>
            已开通
          </view>
        </view>
      </view>

      <view class="mine-card">
        <h3>审核进度</h3>
        <view class="station-audit-steps">
          <span>1 提交申请</span>
          <span>2 平台审核</span>
          <span>3 结果通知</span>
        </view>
      </view>

      <view class="detail-action-row">
        <button class="plain" @click="goMine">返回</button>
        <button class="primary" @click="submitApply">提交团长申请</button>
      </view>
    </view>
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';

import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { navigateUser, showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();
const contactName = ref(state.user.nickname);
const contactMobile = ref(state.user.mobile);
const applyReason = ref('有固定营业时间，可承担社区团购自提和售后退货暂存。');
const storeName = ref('王府花园便民服务点');
const storeAddress = ref('长沙市芙蓉区车站北路王府花园 3 栋左侧架空负一楼');

function submitApply() {
    if (!contactName.value.trim() || !/^1\d{10}$/.test(contactMobile.value.trim()) || !storeName.value.trim() || !storeAddress.value.trim()) {
        showUserToast('请填写联系人、手机号、门店名称和地址', 'warn');
        return;
    }
    showUserToast('团长申请提交接口暂缺，已保留页面状态', 'warn');
}

function goMine() {
    navigateUser('/pages/mine/index', true);
}
</script>

<style lang="scss" scoped>
.station-apply-page {
  background: #f7f1ea;
}

.apply-station-form {
  display: grid;
  gap: 14rpx;
}

.apply-station-form label {
  display: grid;
  gap: 10rpx;
  min-width: 0;
  color: #6b5a50;
  font-size: 26rpx;
  font-weight: 900;
}

.apply-station-form input,
.apply-station-form textarea,
.apply-station-form :deep(uni-input),
.apply-station-form :deep(uni-textarea) {
  width: 100%;
  max-width: 100%;
  min-width: 0;
}

.apply-station-form textarea,
.apply-station-form :deep(uni-textarea) {
  min-height: 128rpx;
}

.apply-locked-note {
  width: fit-content;
  min-height: 36rpx;
  margin-top: 16rpx;
  padding: 0 12rpx;
  color: #e85d3f;
  background: #fff1e9;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 900;
  line-height: 36rpx;
}

.apply-history-list,
.identity-badge-row {
  display: grid;
  gap: 14rpx;
}

.identity-badge-row {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.station-audit-steps {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
}

.station-audit-steps span {
  display: grid;
  min-height: 72rpx;
  place-items: center;
  color: #975b31;
  background: #fff1da;
  border-radius: 18rpx;
  font-size: 25rpx;
  font-weight: 900;
  text-align: center;
}
</style>
