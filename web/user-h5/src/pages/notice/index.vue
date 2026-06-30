<template>
  <view class="service-page shop-page" data-m-page="productNotices">
    <view class="service-head">
      <view class="order-nav">
        <button class="order-back" @click="goMine">‹</button>
        <b>商品通知</b>
        <span>{{ noticeProducts.length }}个</span>
      </view>
    </view>

    <view class="service-list">
      <view
        v-for="item in noticeProducts"
        :key="item.productId"
        class="service-product-row"
        @click="openDetail(item.productId)"
      >
        <view class="service-product-thumb" :style="serviceProductThumbStyle(item.mainImageUrl)" />
        <view class="service-product-main">
          <b>{{ item.productName }}</b>
          <span>到货提醒已开启<br />{{ state.station.name }} 有货后通知你</span>
          <em>{{ productSpecText(item) }}</em>
        </view>
        <button class="service-action danger" @click.stop="cancelNotice(item.productId)">删除</button>
      </view>

      <view class="service-product-row invalid">
        <view class="service-product-thumb" :style="serviceProductThumbStyle(shopImageAssets.list)" />
        <view class="service-product-main">
          <b>云南蓝莓 125g*4盒</b>
          <span>当前商品已售罄<br />可在商品详情页开启提醒</span>
          <em>售罄样例</em>
        </view>
        <button class="service-action used" disabled>已售罄</button>
      </view>

      <EmptyActionCard
        v-if="noticeProducts.length === 0"
        title="暂无商品通知"
        sub="订阅到货提醒后会显示在这里。"
        icon="铃"
        button-text="去首页逛逛"
        @action="goHome"
      />
    </view>
    <UserToast />
    <UserTabBar active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

import EmptyActionCard from '@/components/EmptyActionCard.vue';
import UserTabBar from '@/components/UserTabBar.vue';
import UserToast from '@/components/UserToast.vue';
import { navigateUser, showUserToast, toggleNotice, useUserState } from '@/stores/userState';
import { fallbackProducts, productSpecText, shopImageAssets } from '@/utils/userFallbackData';

const state = useUserState();
const noticeProducts = computed(() => {
    const ids = Array.from(state.notices);
    if (ids.length === 0) {
        return [];
    }
    return ids.map((id) => fallbackProducts.find((item) => item.productId === id) || fallbackProducts[0]);
});

function cancelNotice(productId: number) {
    toggleNotice(productId);
    showUserToast('已取消到货提醒');
}

function serviceProductThumbStyle(url?: string) {
    return {
        backgroundImage: `url("${url || shopImageAssets.detail}")`
    };
}

function openDetail(productId: number) {
    navigateUser(`/pages/product/detail?id=${productId}`);
}

function goMine() {
    navigateUser('/pages/mine/index', true);
}

function goHome() {
    navigateUser('/pages/home/index', true);
}
</script>
