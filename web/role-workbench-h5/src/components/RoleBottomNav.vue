<template>
  <view class="role-bottom">
    <button
      v-for="item in navItems"
      :key="item.key"
      :class="{ active: active === item.key }"
      @click="open(item.url)"
    >
      <svg class="role-bottom-icon" viewBox="0 0 24 24" aria-hidden="true">
        <template v-for="path in item.paths" :key="path.d || `${path.cx}-${path.cy}-${path.r}`">
          <path v-if="path.d" :d="path.d" />
          <circle v-else :cx="path.cx" :cy="path.cy" :r="path.r" />
        </template>
      </svg>
      <text>{{ item.label }}</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { currentRole, goPage } from '@/stores/role';

type NavKey = 'home' | 'orders' | 'delivery' | 'mine';
type IconName = 'home' | 'orders' | 'supplierOrders' | 'delivery' | 'products' | 'mine';
type IconPath = {
    d: string;
    cx?: string;
    cy?: string;
    r?: string;
};

withDefaults(defineProps<{
    active?: NavKey;
}>(), {
    active: undefined
});

const icons: Record<IconName, IconPath[]> = {
    home: [
        { d: 'M4 11.5 12 5l8 6.5' },
        { d: 'M6.5 10.5V20h11v-9.5' },
        { d: 'M10 20v-5h4v5' }
    ],
    orders: [
        { d: 'M7 4h10l2 3v13H5V7l2-3Z' },
        { d: 'M8 9h8' },
        { d: 'M8 13h8' },
        { d: 'M8 17h5' }
    ],
    supplierOrders: [
        { d: 'M4 7h11v9H4z' },
        { d: 'M15 10h3.5l2.5 3v3h-6' },
        { d: 'M7 7V5h5v2' },
        { d: '', cx: '8', cy: '18', r: '1.8' },
        { d: '', cx: '18', cy: '18', r: '1.8' }
    ],
    delivery: [
        { d: 'M4 7h10v9H4z' },
        { d: 'M14 10h3.5l2.5 3v3h-6' },
        { d: '', cx: '8', cy: '18', r: '1.8' },
        { d: '', cx: '17', cy: '18', r: '1.8' }
    ],
    products: [
        { d: 'M12 4 20 8.5v7L12 20 4 15.5v-7L12 4Z' },
        { d: 'M4.5 8.5 12 13l7.5-4.5' },
        { d: 'M12 13v7' }
    ],
    mine: [
        { d: '', cx: '12', cy: '8', r: '3.2' },
        { d: 'M5.5 20c1.2-4 4-6 6.5-6s5.3 2 6.5 6' }
    ]
};

const navItems = computed(() => {
    const isSupplier = currentRole.value === 'supplier';
    return [
        { key: 'home', label: '首页', paths: icons.home, url: '/pages/workbench/index' },
        { key: 'orders', label: isSupplier ? '到仓' : '提货订单', paths: isSupplier ? icons.supplierOrders : icons.orders, url: '/pages/orders/index' },
        { key: 'delivery', label: isSupplier ? '商品' : '门店作业', paths: isSupplier ? icons.products : icons.delivery, url: '/pages/store/index' },
        { key: 'mine', label: '我的', paths: icons.mine, url: '/pages/mine/index' }
    ] as const;
});

function open(url: string) {
    goPage(url);
}
</script>
