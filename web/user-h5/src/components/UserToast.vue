<template>
  <view v-if="state.toast.visible" class="user-toast" :class="state.toast.type">
    <text>{{ state.toast.text }}</text>
  </view>
</template>

<script setup lang="ts">
import { useUserState } from '@/stores/userState';

const state = useUserState();
</script>
