<template>
  <view class="empty-action-card">
    <view class="empty-icon">{{ icon }}</view>
    <text class="empty-title">{{ title }}</text>
    <text class="empty-sub">{{ sub }}</text>
    <button v-if="buttonText" class="plain small" @click="$emit('action')">{{ buttonText }}</button>
  </view>
</template>

<script setup lang="ts">
defineProps<{
    title: string;
    sub: string;
    icon?: string;
    buttonText?: string;
}>();

defineEmits<{
    action: [];
}>();
</script>
