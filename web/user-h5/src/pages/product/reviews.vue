<template>
  <view class="page">
    <view class="section">
      <text class="title">买家评价</text>
      <view class="tag-row">
        <text v-for="item in filters" :key="item.label" class="pill" :class="{ active: score === item.score }" @click="selectScore(item.score)">
          {{ item.label }}
        </text>
      </view>
    </view>
    <view v-for="item in reviews" :key="item.id" class="section">
      <text class="title">{{ item.score }} 分</text>
      <text class="subtle">用户 {{ item.userId }} · {{ item.content }}</text>
    </view>
    <view v-if="reviews.length === 0" class="section">
      <text class="subtle">暂无评价</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageComments, type UserCommentDTO } from '@/api/user';
import { showUserToast, useUserState } from '@/stores/userState';

const filters = [
    { label: '全部评价', score: undefined },
    { label: '5 分', score: 5 },
    { label: '低评分', score: 2 }
];
const productId = ref<number | undefined>();
const score = ref<number | undefined>();
const reviews = ref<UserCommentDTO[]>([]);
const state = useUserState();

async function loadReviews() {
    try {
        const page = await pageComments({
            pageNum: 1,
            pageSize: 20,
            productId: productId.value,
            score: score.value,
            stationId: state.station.id
        });
        reviews.value = page.list || [];
    } catch (error) {
        reviews.value = state.localReviews
            .filter((item) => !productId.value || item.productId === productId.value)
            .filter((item) => !score.value || (score.value === 2 ? item.score <= 2 : item.score === score.value))
            .map((item) => ({
                id: item.id,
                commentNo: `LOCAL${item.id}`,
                userId: state.user.id,
                productId: item.productId,
                skuId: 0,
                stationId: state.station.id,
                score: item.score,
                content: item.content,
                status: item.status,
                createTime: item.createTime
            }));
        showUserToast('评价接口暂不可用，已展示本地评价状态', 'warn');
    }
}

function selectScore(value?: number) {
    score.value = value;
    void loadReviews();
}

onMounted(() => {
    const pages = getCurrentPages();
    const current = pages[pages.length - 1] as { options?: Record<string, string> };
    productId.value = Number(current.options?.productId || 0) || undefined;
    void loadReviews();
});
</script>

<style lang="scss" scoped>
.tag-row {
  display: flex;
  gap: 12rpx;
  margin-top: 18rpx;
}

.active {
  color: #d94b34;
  background: #fff2e9;
}
</style>
