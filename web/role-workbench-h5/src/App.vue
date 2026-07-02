<script setup lang="ts">
import './styles/app.scss';
import { getLeaderDetail, getStationDetail } from '@/api/station';
import { refreshCurrentEntityName } from '@/stores/role';

refreshCurrentEntityName({ getStationDetail, getLeaderDetail });
</script>

<template>
  <view />
</template>
