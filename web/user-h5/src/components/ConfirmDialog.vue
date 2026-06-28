<template>
  <view v-if="visible" class="dialog-mask" @click="$emit('cancel')">
    <view class="confirm-dialog" @click.stop>
      <text class="dialog-title">{{ title }}</text>
      <text class="dialog-sub">{{ content }}</text>
      <view class="dialog-actions">
        <button class="plain" @click="$emit('cancel')">{{ cancelText || '取消' }}</button>
        <button :class="danger ? 'danger' : 'primary'" @click="$emit('confirm')">{{ confirmText || '确认' }}</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
defineProps<{
    visible: boolean;
    title: string;
    content: string;
    cancelText?: string;
    confirmText?: string;
    danger?: boolean;
}>();

defineEmits<{
    cancel: [];
    confirm: [];
}>();
</script>
