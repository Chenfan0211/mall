<template>
  <view class="page">
    <view v-for="item in records" :key="item.key" class="section">
      <text class="title">{{ item.title }}</text>
      <text class="subtle">{{ item.status }} · {{ item.time || '-' }}</text>
    </view>
    <view v-if="records.length === 0" class="section">
      <text class="subtle">暂无记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { pageLeaves, pageMessages, pageWithdraws } from '@/api/station';

interface RecordRow {
    key: string;
    title: string;
    status: string;
    time?: string;
}

const records = ref<RecordRow[]>([]);

async function loadData() {
    const [messagePage, withdrawPage, leavePage] = await Promise.all([
        pageMessages({ pageNum: 1, pageSize: 5, stationId: 720001 }),
        pageWithdraws({ pageNum: 1, pageSize: 5, stationId: 720001 }),
        pageLeaves({ pageNum: 1, pageSize: 5, stationId: 720001 })
    ]);
    records.value = [
        ...(messagePage.list || []).map((item) => ({
            key: `message-${item.id}`,
            title: item.title,
            status: `消息状态 ${item.readStatus}`,
            time: item.createTime
        })),
        ...(withdrawPage.list || []).map((item) => ({
            key: `withdraw-${item.id}`,
            title: `提现申请 ${item.withdrawNo}`,
            status: `状态 ${item.status}`,
            time: undefined
        })),
        ...(leavePage.list || []).map((item) => ({
            key: `leave-${item.id}`,
            title: `休假申请 ${item.leaveNo}`,
            status: `状态 ${item.status}`,
            time: undefined
        }))
    ];
}

onMounted(() => {
    void loadData();
});
</script>
