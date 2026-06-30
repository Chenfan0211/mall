<template>
  <view class="page phone-shell login-page">
    <view class="warehouse-top login-hero">
      <view class="status-row">
        <text>14:49</text>
        <text>WiFi 80</text>
      </view>
      <view class="login-hero-title">
        <text class="eyebrow">WMS MOBILE</text>
        <text class="title">仓配移动端</text>
        <text class="subtle">{{ selectedRole.loginNote }}</text>
      </view>
    </view>

    <view class="login-main">
      <view class="section login-panel role-panel" :class="{ open: rolePanelOpen }">
        <view class="login-role-collapsed">
          <text class="section-title">选择岗位</text>
          <button class="login-current-role" @click="toggleRolePanel">
            <text>{{ selectedRole.name }}</text>
            <text class="login-current-arrow">{{ rolePanelOpen ? '⌃' : '⌄' }}</text>
          </button>
        </view>
        <view v-if="rolePanelOpen" class="login-role-grid">
          <button
            v-for="role in roles"
            :key="role.key"
            class="login-role-option"
            :class="{ active: form.role === role.key }"
            @click="selectRole(role.key)"
          >
            <svg class="login-role-icon" viewBox="0 0 24 24" aria-hidden="true">
              <template v-if="role.key === 'receiver'">
                <path d="M21 8.5 12 3 3 8.5l9 5.5 9-5.5Z" />
                <path d="M3 8.5v7L12 21l9-5.5v-7" />
                <path d="M8 12.5V17" />
              </template>
              <template v-else-if="role.key === 'buyer'">
                <path d="M6 4h12v16H6z" />
                <path d="M9 8h6" />
                <path d="M9 12h6" />
                <path d="M9 16h3" />
                <path d="m16 15 1.5 1.5L21 13" />
              </template>
              <template v-else-if="role.key === 'picker'">
                <path d="M9 11V6a3 3 0 0 1 6 0v5" />
                <path d="M5 10h14l-1.2 10H6.2L5 10Z" />
                <path d="M9 15h6" />
              </template>
              <template v-else-if="role.key === 'recheck'">
                <path d="M5 7h10v10H5z" />
                <path d="M15 10h3l2 3v4h-5" />
                <path d="M7 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" />
                <path d="M17 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z" />
                <path d="m7.5 11.5 1.2 1.2 2.3-2.5" />
              </template>
              <template v-else-if="role.key === 'driver'">
                <path d="M5 16h14l-1.3-5.2A3 3 0 0 0 14.8 8H9.2a3 3 0 0 0-2.9 2.8L5 16Z" />
                <path d="M7 16v2" />
                <path d="M17 16v2" />
                <path d="M8 12h8" />
                <circle cx="8" cy="18" r="1.8" />
                <circle cx="16" cy="18" r="1.8" />
              </template>
              <template v-else>
                <path d="M4 6h16" />
                <path d="M6 6v14h12V6" />
                <path d="M9 10h6" />
                <path d="M9 14h6" />
                <path d="M10 3h4l1 3H9l1-3Z" />
              </template>
            </svg>
            <text class="login-role-name">{{ role.name }}</text>
          </button>
        </view>
      </view>

      <view class="section login-panel login-card">
        <view class="login-panel-head">
          <view>
            <text class="section-title">账号登录</text>
            <text class="section-sub">{{ selectedRole.entity }}</text>
          </view>
        </view>
        <view class="form-row">
          <text>账号</text>
          <input v-model="form.username" placeholder="账号" />
        </view>
        <view class="form-row">
          <text>密码</text>
          <input v-model="form.password" password placeholder="密码" />
        </view>
        <button class="primary login-btn" @click="submit">登录进入工作台</button>
        <button class="plain forgot-btn" @click="forgotPassword">忘记密码</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { passwordLogin } from '@/api/auth';
import { clearWarehouseSession, getRoleProfile, roleProfiles, setCurrentRole, setDemoToken, setWarehouseSession, type WarehouseRole } from '@/stores/warehouse';

const roles = roleProfiles;
const roleAccounts: Record<WarehouseRole, string> = {
    receiver: 'test_wms_receiver',
    buyer: 'test_wms_buyer',
    picker: 'test_wms_picker',
    recheck: 'test_wms_loader',
    driver: 'test_wms_driver',
    manager: 'test_wms_manager'
};
const roleLoginFallbacks: Partial<Record<WarehouseRole, Array<{ username: string; portalCode: string }>>> = {
    buyer: [
        { username: 'test_wms_receiver', portalCode: 'warehouse-h5' }
    ],
    manager: [
        { username: 'test_wms_supervisor', portalCode: 'wms-admin' }
    ]
};
const form = reactive({
    username: roleAccounts.receiver,
    password: 'Test@123456',
    role: 'receiver' as WarehouseRole
});
const rolePanelOpen = ref(true);
const submitting = ref(false);

const selectedRole = computed(() => getRoleProfile(form.role));

function selectRole(role: WarehouseRole) {
    form.role = role;
    form.username = roleAccounts[role];
    rolePanelOpen.value = false;
}

function toggleRolePanel() {
    rolePanelOpen.value = !rolePanelOpen.value;
}

async function submit() {
    if (submitting.value) return;
    setCurrentRole(form.role);
    submitting.value = true;
    try {
        const session = await loginWithFallbacks();
        setWarehouseSession(session.token, {
            source: session.fallback ? 'fallback' : 'api',
            loginUsername: session.username,
            loginPortalCode: session.portalCode,
            selectedRole: form.role
        });
        uni.showToast({
            title: '登录成功',
            icon: 'none'
        });
    } catch (error) {
        console.debug('[warehouse-h5] 登录接口失败，切换演示数据', error);
        setDemoToken();
        uni.showToast({ title: '登录成功', icon: 'none' });
    } finally {
        submitting.value = false;
    }
    uni.switchTab({ url: '/pages/workbench/index' });
}

async function loginWithFallbacks(): Promise<{ token: string; username: string; portalCode: string; fallback: boolean }> {
    const attempts = [
        { username: form.username, portalCode: 'warehouse-h5' },
        ...(roleLoginFallbacks[form.role] || [])
    ];
    let lastError: unknown;
    for (const attempt of attempts) {
        try {
            const result = await passwordLogin({
                username: attempt.username,
                password: form.password,
                portalCode: attempt.portalCode
            }, { silent: true });
            const token = result.accessToken || result.token;
            if (token) {
                return {
                    token,
                    username: attempt.username,
                    portalCode: attempt.portalCode,
                    fallback: attempt.username !== form.username || attempt.portalCode !== 'warehouse-h5'
                };
            }
            lastError = new Error(`${attempt.username} 登录接口未返回 token`);
        } catch (error) {
            lastError = error;
        }
    }
    throw lastError || new Error('登录失败');
}

function forgotPassword() {
    uni.showToast({ title: '请联系仓库主管重置密码', icon: 'none' });
}

onShow(() => {
    clearWarehouseSession();
});
</script>
