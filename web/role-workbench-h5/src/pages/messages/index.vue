<template>
  <view class="page">
    <view v-for="item in messages" :key="item.id" class="section">
      <text class="title">{{ item.title }}</text>
      <text class="subtle">{{ item.readStatus === 1 ? '已读' : '未读' }} · {{ item.bizType }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageMessages, type StationMessageDTO } from '@/api/station';

const messages = ref<StationMessageDTO[]>([]);

async function loadData() {
    const page = await pageMessages({ pageNum: 1, pageSize: 20, receiverId: 720001 });
    messages.value = page.list;
}

onMounted(() => {
    void loadData();
});
</script>
