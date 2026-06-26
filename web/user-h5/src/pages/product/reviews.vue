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

const filters = [
    { label: '全部评价', score: undefined },
    { label: '5 分', score: 5 },
    { label: '低评分', score: 2 }
];
const productId = ref<number | undefined>();
const score = ref<number | undefined>();
const reviews = ref<UserCommentDTO[]>([]);

async function loadReviews() {
    const page = await pageComments({
        pageNum: 1,
        pageSize: 20,
        productId: productId.value,
        score: score.value,
        stationId: 720001
    });
    reviews.value = page.list || [];
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
