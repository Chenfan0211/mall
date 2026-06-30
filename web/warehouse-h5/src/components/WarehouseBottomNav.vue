<template>
  <view class="warehouse-bottom" :class="`items-${navItems.length}`">
    <button
      v-for="item in navItems"
      :key="item.key"
      class="warehouse-nav-btn"
      :class="{ active: active === item.key }"
      @click="go(item.key)"
    >
      <view class="warehouse-nav-icon">
        <svg v-if="item.icon === 'home'" viewBox="0 0 24 24" fill="none">
          <path d="M3 11.5 12 4l9 7.5" />
          <path d="M5 10.5V20h14v-9.5" />
          <path d="M9 20v-6h6v6" />
        </svg>
        <svg v-else-if="item.icon === 'tasks'" viewBox="0 0 24 24" fill="none">
          <path d="M7 3h10l3 3v15H7a3 3 0 0 1-3-3V6a3 3 0 0 1 3-3Z" />
          <path d="M16 3v4h4" />
          <path d="M8 12h8" />
          <path d="M8 16h5" />
        </svg>
        <svg v-else-if="item.icon === 'stock'" viewBox="0 0 24 24" fill="none">
          <path d="M4 7h16" />
          <path d="M4 12h16" />
          <path d="M4 17h16" />
          <path d="M6 4h12a2 2 0 0 1 2 2v14H4V6a2 2 0 0 1 2-2Z" />
          <path d="M8 9h3" />
          <path d="M8 14h3" />
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none">
          <path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z" />
          <path d="M4 21a8 8 0 0 1 16 0" />
        </svg>
      </view>
      <text>{{ item.label }}</text>
    </button>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { getCurrentRole, type WarehouseRole } from '@/stores/warehouse';

type NavKey = 'home' | 'tasks' | 'stock' | 'profile';

const DRIVER_TAB_KEY = 'mall_warehouse_h5_driver_tab';

const props = withDefaults(defineProps<{
    active: NavKey;
    role?: WarehouseRole;
}>(), {
    role: undefined
});

const currentRole = computed(() => props.role || getCurrentRole());

const navItems = computed<Array<{ key: NavKey; label: string; icon: 'home' | 'tasks' | 'stock' | 'profile' }>>(() => {
    const role = currentRole.value;
    const taskLabel = role === 'driver'
        ? '配送单'
        : role === 'buyer'
            ? '采购单'
            : role === 'receiver'
                ? '收货单'
                : '任务';
    const items: Array<{ key: NavKey; label: string; icon: 'home' | 'tasks' | 'stock' | 'profile' }> = [
        { key: 'home', label: '工作台', icon: 'home' },
        { key: 'tasks', label: taskLabel, icon: 'tasks' }
    ];
    if (role === 'driver') {
        items.push({ key: 'stock', label: '退货单', icon: 'stock' });
    } else if (role === 'manager') {
        items.push({ key: 'stock', label: '库存', icon: 'stock' });
    }
    items.push({ key: 'profile', label: role === 'driver' ? '作业统计' : '我的', icon: 'profile' });
    return items;
});

function go(key: NavKey) {
    const role = currentRole.value;
    if (role === 'driver' && (key === 'tasks' || key === 'stock')) {
        const tab = key === 'stock' ? 'return' : 'delivery';
        uni.setStorageSync(DRIVER_TAB_KEY, tab);
        uni.$emit('warehouse:driverTaskTab', tab);
        uni.switchTab({ url: '/pages/tasks/index' });
        return;
    }
    if (key === props.active) return;
    if (key === 'home') {
        uni.switchTab({ url: '/pages/workbench/index' });
        return;
    }
    if (key === 'tasks') {
        uni.switchTab({ url: '/pages/tasks/index' });
        return;
    }
    if (key === 'stock') {
        uni.switchTab({ url: '/pages/inventory/index' });
        return;
    }
    uni.switchTab({ url: '/pages/mine/index' });
}
</script>
