<template>
  <view class="page">
    <view class="section">
      <text class="title">团长申请</text>
      <text class="subtle">当前后端已支持自提点查询，提交申请接口待补。</text>
    </view>
    <view v-for="item in stations" :key="item.id" class="section station-row">
      <view>
        <text class="title">{{ item.stationName }}</text>
        <text class="subtle">{{ item.address }}</text>
      </view>
      <text class="pill">状态 {{ item.status }}</text>
    </view>
    <view v-if="stations.length === 0" class="section">
      <text class="subtle">暂无可申请自提点</text>
    </view>
    <view class="section">
      <text class="title">申请资料</text>
      <input v-model="contactName" placeholder="联系人姓名" />
      <input v-model="contactMobile" class="field" placeholder="联系人手机号" />
      <textarea v-model="applyReason" class="field" maxlength="120" placeholder="请输入申请说明，例如社区位置、可服务户数" />
      <button class="primary submit-btn" @click="submitApply">提交申请</button>
    </view>
    <UserToast />
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import UserToast from '@/components/UserToast.vue';
import { pageStations, type UserStationDTO } from '@/api/user';
import { showUserToast, useUserState } from '@/stores/userState';

const state = useUserState();
const stations = ref<UserStationDTO[]>([]);
const contactName = ref(state.user.nickname);
const contactMobile = ref(state.user.mobile);
const applyReason = ref('');

async function loadStations() {
    try {
        const page = await pageStations({ pageNum: 1, pageSize: 20, cityId: state.city.id });
        stations.value = page.list || [];
    } catch (error) {
        showUserToast('自提点查询接口暂不可用', 'warn');
    }
}

function submitApply() {
    if (!contactName.value.trim() || !/^1\d{10}$/.test(contactMobile.value.trim())) {
        showUserToast('请填写联系人和正确手机号', 'warn');
        return;
    }
    showUserToast('团长申请提交接口暂缺，已保留页面状态', 'warn');
}

onMounted(() => {
    void loadStations();
});
</script>

<style lang="scss" scoped>
.station-row {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.field {
  margin-top: 14rpx;
}

.submit-btn {
  width: 100%;
  margin-top: 18rpx;
}
</style>
