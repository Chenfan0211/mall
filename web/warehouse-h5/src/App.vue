<script lang="ts">
import './styles/app.scss';

import { clearWarehouseSession, hasWarehouseSession } from '@/stores/warehouse';

const LOGIN_ROUTE = 'pages/login/index';
const LOGIN_URL = `/${LOGIN_ROUTE}`;

function getCurrentRoute() {
    const pages = getCurrentPages();
    return pages.length ? pages[pages.length - 1].route || '' : '';
}

function ensureLoggedIn() {
    const route = getCurrentRoute();
    if (!route || route === LOGIN_ROUTE) {
        return;
    }
    if (!hasWarehouseSession()) {
        clearWarehouseSession();
        uni.reLaunch({ url: LOGIN_URL });
    }
}

export default {
    onLaunch() {
        setTimeout(ensureLoggedIn, 0);
        setTimeout(ensureLoggedIn, 120);
    },
    onShow() {
        setTimeout(ensureLoggedIn, 0);
    }
};
</script>

<template>
  <view></view>
</template>
