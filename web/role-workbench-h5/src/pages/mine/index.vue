<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ profile.entity }}</text>
      </view>
      <view class="role-work-title">
        <view>
          <text class="role-work-title-main">{{ roleType === 'supplier' ? '供应商我的' : '自提点我的' }}</text>
          <text class="role-work-sub">{{ roleType === 'supplier' ? '货款、扣回和提现' : '佣金、扣减和提现' }}</text>
        </view>
        <text class="role-work-entity">{{ profile.entity }}</text>
      </view>
    </view>

    <view v-if="loading" class="role-empty">
      <text class="title">正在加载我的数据</text>
      <text class="subtle">正在同步最新身份和账户信息...</text>
    </view>
    <view v-else-if="error" class="role-empty">
      <text class="title">我的数据加载失败</text>
      <text class="subtle">{{ error }}</text>
      <button class="role-wallet-action primary retry-btn" @click="load">重新加载</button>
    </view>

    <template v-else>
      <view class="role-wallet">
        <text>{{ walletLabel }}</text>
        <text class="role-wallet-strong">{{ balanceText }}</text>
        <view class="role-wallet-actions" :class="{ three: roleType === 'supplier' }">
          <button class="role-wallet-action primary" @click="open('/pages/finance/withdraw')">申请提现</button>
          <button class="role-wallet-action soft" @click="open(roleType === 'supplier' ? '/pages/finance/list?tab=settlement' : '/pages/finance/list?tab=commission')">
            {{ roleType === 'supplier' ? '查看货款明细' : '查看佣金明细' }}
          </button>
          <button v-if="roleType === 'supplier'" class="role-wallet-action soft" @click="open('/pages/finance/list?tab=withdraw')">提现记录</button>
        </view>
        <view class="role-money-grid">
          <button v-for="item in stats" :key="item.label" @click="open(item.page)">
            <text class="role-money-value">{{ item.value }}</text>
            <text>{{ item.label }}</text>
          </button>
        </view>
      </view>

      <view class="role-main role-finance-main">
        <view class="role-leave-entry-panel">
          <view class="role-mine-entry-grid">
            <button v-for="item in mineEntries" :key="item.title" class="role-mine-entry" @click="open(item.page)">
              <view class="role-mine-entry-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path v-for="path in item.paths" :key="path" :d="path" />
                </svg>
              </view>
              <text class="role-mine-entry-title">{{ item.title }}</text>
            </button>
          </view>
        </view>

        <view class="role-finance-spacer"></view>

        <view class="role-logout-card">
          <button class="role-logout-btn" @click="logout">退出登录</button>
        </view>
      </view>
    </template>

    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import {
    pageCommissions,
    pageFinanceAccounts,
    pageFinanceFlows,
    pageSupplierSettlements,
    pageWithdraws,
    type CommissionDetailDTO,
    type StationFinanceAccountDTO,
    type StationFinanceFlowDTO,
    type SupplierSettlementDTO,
    type WithdrawApplyDTO
} from '@/api/station';
import {
    confirmAction,
    currentProfile,
    currentRole,
    getRequiredCommissionQuery,
    getRequiredFinanceQuery,
    getRequiredRoleQuery,
    goPage,
    logoutRole,
    moneyText
} from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const loading = ref(false);
const error = ref('');
const accounts = ref<StationFinanceAccountDTO[]>([]);
const flows = ref<StationFinanceFlowDTO[]>([]);
const commissions = ref<CommissionDetailDTO[]>([]);
const withdraws = ref<WithdrawApplyDTO[]>([]);
const settlements = ref<SupplierSettlementDTO[]>([]);

const walletLabel = computed(() => (roleType.value === 'supplier' ? '供应商可结算货款' : '可用佣金余额'));

const balanceText = computed(() => {
    if (roleType.value === 'supplier') {
        const total = settlements.value.reduce((sum, item) => sum + Number(item.payableAmount || 0), 0);
        return moneyText(total.toFixed(2));
    }
    return moneyText(accounts.value[0]?.availableAmount);
});

const stats = computed(() => {
    if (roleType.value === 'supplier') {
        return [
            { value: String(settlements.value.length), label: '结算单', page: '/pages/finance/list?tab=settlement' },
            { value: String(withdraws.value.length), label: '提现记录', page: '/pages/finance/list?tab=withdraw' },
            { value: moneyText(settlements.value.reduce((sum, item) => sum + Number(item.payableAmount || 0), 0).toFixed(2)), label: '应付合计', page: '/pages/finance/list?tab=settlement' }
        ];
    }
    return [
        { value: moneyText(accounts.value[0]?.frozenAmount), label: '冻结金额', page: '/pages/finance/list?tab=withdraw' },
        { value: String(commissions.value.length), label: '佣金明细', page: '/pages/finance/list?tab=commission' },
        { value: String(flows.value.length), label: '账户流水', page: '/pages/finance/list?tab=detail' }
    ];
});

const mineEntries = computed(() => {
    const common = [
        {
            title: '操作记录',
            page: '/pages/records/index',
            paths: [
                'M6 3.8h12v16.4H6z',
                'M8.5 7.5h7',
                'M8.5 12h7',
                'M8.5 16.5h4.5'
            ]
        },
        {
            title: '消息中心',
            page: '/pages/messages/index',
            paths: [
                'M5 6.5h14v9.2H9l-4 3V6.5Z',
                'M8.5 10h7',
                'M8.5 13h5'
            ]
        }
    ];
    if (roleType.value !== 'station') return common;
    return [
        {
            title: '发起休假',
            page: '/pages/leave/apply',
            paths: [
                'M7 3v3',
                'M17 3v3',
                'M4.8 9.2h14.4',
                'M6.5 5h11A2.5 2.5 0 0 1 20 7.5v10A2.5 2.5 0 0 1 17.5 20h-11A2.5 2.5 0 0 1 4 17.5v-10A2.5 2.5 0 0 1 6.5 5Z',
                'm9 14 2 2 4-5'
            ]
        },
        {
            title: '休假列表',
            page: '/pages/leave/index',
            paths: [
                'M8 7h8',
                'M8 12h8',
                'M8 17h5',
                'M6.5 3.8h11A2.2 2.2 0 0 1 19.7 6v12a2.2 2.2 0 0 1-2.2 2.2h-11A2.2 2.2 0 0 1 4.3 18V6a2.2 2.2 0 0 1 2.2-2.2Z'
            ]
        },
        ...common
    ];
});

onShow(load);

async function load() {
    loading.value = true;
    error.value = '';
    try {
        const roleQuery = { ...getRequiredRoleQuery(), pageNum: 1, pageSize: 50 };
        const financeQuery = { ...getRequiredFinanceQuery(), pageNum: 1, pageSize: 50 };
        const [withdrawPage, flowPage] = await Promise.all([
            safePageRequest(() => pageWithdraws(financeQuery)),
            safePageRequest(() => pageFinanceFlows(financeQuery))
        ]);
        withdraws.value = withdrawPage;
        flows.value = flowPage;
        if (roleType.value === 'supplier') {
            settlements.value = await safePageRequest(() => pageSupplierSettlements(roleQuery));
            accounts.value = [];
            commissions.value = [];
        } else {
            const [accountPage, commissionPage] = await Promise.all([
                safePageRequest(() => pageFinanceAccounts(financeQuery)),
                safePageRequest(() => pageCommissions({ ...getRequiredCommissionQuery(), pageNum: 1, pageSize: 50 }))
            ]);
            accounts.value = accountPage;
            commissions.value = commissionPage;
            settlements.value = [];
        }
    } catch (err) {
        error.value = friendlyErrorMessage(err, '我的数据加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
}

async function safePageRequest<T>(loader: () => Promise<{ list?: T[] }>) {
    try {
        const page = await loader();
        return page.list || [];
    } catch {
        return [];
    }
}

function open(url: string) {
    goPage(url);
}

async function logout() {
    const ok = await confirmAction('确认退出当前角色工作台账号？', '退出登录');
    if (!ok) return;
    logoutRole();
    uni.reLaunch({ url: '/pages/login/index' });
}
</script>

<style lang="scss" scoped>
.role-work-title {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-top: 16rpx;
}

.role-work-title-main {
  display: block;
  color: #ffffff;
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.2;
}

.role-work-sub {
  display: block;
  margin-top: 10rpx;
  color: rgba(255, 255, 255, 0.92);
  font-size: 24rpx;
  line-height: 1.45;
}

.role-wallet {
  margin: 0 0 24rpx;
  padding: 32rpx;
  background: linear-gradient(135deg, #fff0e4 0%, #fff9f3 100%);
}

.role-wallet text {
  display: block;
  color: #8f6c58;
  font-size: 24rpx;
  line-height: 1.6;
}

.role-wallet-strong {
  display: block;
  margin-top: 16rpx;
  color: #ce532f;
  font-size: 60rpx;
  font-weight: 900;
  line-height: 1.15;
}

.role-wallet-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-top: 28rpx;
}

.role-wallet-actions.three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.role-wallet-action,
:deep(uni-button.role-wallet-action) {
  min-height: 72rpx;
  padding: 0 20rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-wallet-actions.three .role-wallet-action,
.role-wallet-actions.three :deep(uni-button.role-wallet-action) {
  padding: 0 14rpx;
  font-size: 23rpx;
}

.role-wallet-action.primary,
:deep(uni-button.role-wallet-action.primary) {
  color: #ffffff;
  background: #e85d3f;
  box-shadow: 0 20rpx 36rpx rgba(232, 93, 63, 0.22);
}

.role-wallet-action.soft,
:deep(uni-button.role-wallet-action.soft) {
  color: #b85a2f;
  background: #ffffff;
  box-shadow: inset 0 0 0 1rpx #f0d6c5;
}

.role-money-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20rpx;
  margin-top: 28rpx;
}

.role-money-grid button,
.role-money-grid :deep(uni-button) {
  min-height: 96rpx;
  padding: 18rpx 12rpx;
  color: #8b6a57;
  background: #ffffff;
  border-radius: 28rpx;
  box-shadow: inset 0 0 0 1rpx #f3ddd1;
  text-align: center;
}

.role-money-value {
  display: block;
  overflow: hidden;
  color: #433229;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.3;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-money-grid text:not(.role-money-value) {
  display: block;
  margin-top: 8rpx;
  color: #8d6a57;
  font-size: 22rpx;
  line-height: 1.35;
}

.role-finance-main {
  display: flex;
  flex-direction: column;
  min-height: 624rpx;
  padding-top: 0;
}

.role-finance-spacer {
  flex: 1;
  min-height: 88rpx;
}

.role-leave-entry-panel {
  margin: 0 0 24rpx;
  padding: 0;
  background: transparent;
  border: 0;
  box-shadow: none;
}

.role-mine-entry-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
  margin-bottom: 24rpx;
}

.role-mine-entry,
:deep(uni-button.role-mine-entry) {
  display: grid;
  grid-template-columns: 84rpx minmax(0, 1fr);
  gap: 18rpx;
  align-items: center;
  min-height: 124rpx;
  padding: 22rpx;
  color: #332821;
  background: linear-gradient(135deg, #fffaf5 0%, #fff1e6 100%);
  border: 0;
  border-radius: 32rpx;
  box-shadow: inset 0 0 0 1rpx #f3ddd1;
  text-align: left;
}

.role-mine-entry-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 84rpx;
  height: 84rpx;
  color: #ffffff;
  background: linear-gradient(135deg, #e85d3f 0%, #f39a4f 100%);
  border-radius: 30rpx;
  box-shadow: 0 20rpx 36rpx rgba(232, 93, 63, 0.22);
  font-size: 28rpx;
  font-weight: 900;
}

.role-mine-entry-icon svg {
  width: 48rpx;
  height: 48rpx;
  fill: none;
}

.role-mine-entry-icon path {
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.role-mine-entry-title {
  display: block;
  min-width: 0;
  color: #332821;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
}

.role-logout-card {
  margin-top: auto;
  padding: 24rpx 0 28rpx;
}

.role-logout-btn,
:deep(uni-button.role-logout-btn) {
  width: 100%;
  min-height: 88rpx;
  color: #bd4f36;
  background: #ffffff;
  border: 0;
  border-radius: 36rpx;
  box-shadow: inset 0 0 0 1rpx #f1d6c8;
  font-size: 28rpx;
  font-weight: 900;
}

.retry-btn {
  margin-top: 18rpx;
}
</style>
