<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>缺货标记</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ roleType === 'supplier' ? order.no : goods.title }}</text>
      <text class="subtle">{{ roleType === 'supplier' ? '供应商按送货单商品标记缺货' : '自提点按到货商品标记缺货' }}</text>
    </view>

    <view class="section">
      <view class="section-head">
        <view>
          <text class="title">缺货数量</text>
          <text class="subtle">提交后进入异常记录，等待平台处理</text>
        </view>
      </view>
      <view v-if="roleType === 'station'" class="detail-row">
        <text>应到/实到</text>
        <text>{{ goods.expected }} / {{ goods.actual }}</text>
      </view>
      <view class="form-row">
        <text>缺货件数</text>
        <input v-model="qty" type="number" placeholder="请输入缺货件数" />
      </view>
      <view class="form-row">
        <text>缺货原因</text>
        <textarea v-model="reason" placeholder="请输入现场情况、破损或供应不足说明" />
      </view>
    </view>

    <view v-if="roleType === 'supplier'" class="section">
      <view class="section-head">
        <view>
          <text class="title">送货单商品</text>
          <text class="subtle">只展示当前供应商 SKU</text>
        </view>
      </view>
      <view v-for="item in order.items" :key="item.id" class="product-line">
        <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
        <view class="card-main">
          <text class="card-title">{{ item.title }}</text>
          <text class="card-desc">{{ item.spec }} · 送货 {{ item.qty }} 件</text>
        </view>
        <text class="status-pill gray">可标记</text>
      </view>
    </view>

    <view class="fixed-bottom">
      <button class="danger" @click="submit">提交缺货</button>
      <button class="soft" @click="back">返回</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { addOperation, confirmAction, currentProfile, currentRole, findOrder, showRoleToast } from '@/stores/role';

const id = ref('');
const no = ref('');
const qty = ref('');
const reason = ref('');
const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const delivery = computed(() => profile.value.deliveryRecords[0]);
const goods = computed(() => delivery.value?.goods.find((item) => item.id === id.value) || delivery.value?.goods[0] || { title: '到货商品', spec: '', expected: 0, actual: 0, users: [] });
const order = computed(() => findOrder(no.value));

onLoad((query) => {
    id.value = String(query?.id || '');
    no.value = String(query?.no || '');
});

async function submit() {
    if (!qty.value || Number(qty.value) <= 0) {
        showRoleToast('请输入有效缺货件数');
        return;
    }
    const ok = await confirmAction(`确认提交缺货 ${qty.value} 件？`, '提交缺货');
    if (!ok) return;
    addOperation({
        no: `EX${Date.now()}`,
        type: '缺货异常',
        title: `${roleType.value === 'supplier' ? order.value.no : goods.value.title} 缺货 ${qty.value} 件`,
        time: '刚刚',
        status: '已提交待处理',
        next: '等待平台处理'
    });
    showRoleToast('缺货接口暂未接通，已记录前端操作');
    back();
}

function back() {
    uni.navigateBack({ delta: 1 });
}
</script>

<style lang="scss" scoped>
.form-row {
  margin-bottom: 20rpx;
}

.form-row > text {
  display: block;
  margin-bottom: 10rpx;
  color: #5f493d;
  font-size: 24rpx;
  font-weight: 800;
}

.fixed-bottom button {
  flex: 1;
  min-height: 70rpx;
  font-size: 24rpx;
}
</style>
