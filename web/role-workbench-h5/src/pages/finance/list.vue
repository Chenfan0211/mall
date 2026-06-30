<template>
  <view class="page">
    <view class="role-work-head">
      <view class="status-bar">
        <text>14:49</text>
        <text>财务明细</text>
      </view>
      <view class="role-detail-headline">
        <button class="role-detail-back" @click="back">‹</button>
        <view>
          <text class="role-detail-title">{{ pageTitle }}</text>
        </view>
        <text class="role-status blue">{{ rows.length }} 条</text>
      </view>
    </view>

    <view class="role-main">
      <view v-if="loading" class="role-empty">
        <text class="title">正在加载财务明细</text>
        <text class="subtle">正在同步财务明细...</text>
      </view>
      <view v-else-if="error" class="role-empty">
        <text class="title">财务明细加载失败</text>
        <text class="subtle">{{ error }}</text>
        <button class="role-action-btn primary retry-btn" @click="load">重新加载</button>
      </view>

      <view v-else class="role-detail-section">
        <view class="role-section-head">
          <view>
            <text class="title">{{ pageTitle }}</text>
            <text class="subtle">{{ rows.length }} 条</text>
          </view>
        </view>
        <view class="role-finance-list" :class="{ 'withdraw-list': tab === 'withdraw' }">
          <view v-for="item in rows" :key="item.no" :class="tab === 'withdraw' ? 'role-leave-card' : 'role-finance-line'">
            <template v-if="tab === 'withdraw'">
              <view class="role-leave-head">
                <text>{{ item.no }}</text>
                <text class="role-status" :class="roleStatusClass(item.status)">{{ item.status }}</text>
              </view>
              <view class="role-withdraw-record-body">
                <text>提现金额：{{ item.amount }}</text>
                <text>{{ item.extra }}</text>
                <text>{{ item.desc }}</text>
              </view>
              <view v-if="item.auditRemark" class="role-leave-reject">
                <text>拒绝原因：{{ item.auditRemark }}</text>
              </view>
              <view v-if="item.canCancel" class="role-order-actions">
                <button class="role-action-btn soft" @click="cancelWithdrawRecord(item.id)">撤回申请</button>
              </view>
            </template>
            <template v-else>
              <view class="role-finance-main">
                <view class="role-finance-line-title">
                  <text>{{ item.no }}</text>
                  <text class="role-status" :class="roleStatusClass(item.status)">{{ item.status }}</text>
                </view>
                <text>{{ item.desc }}</text>
                <text v-if="item.extra">{{ item.extra }}</text>
                <view v-if="item.auditRemark" class="role-finance-reject">
                  <text>拒绝原因：{{ item.auditRemark }}</text>
                </view>
              </view>
              <view class="role-finance-side">
                <text class="role-finance-amount">{{ item.amount }}</text>
                <text class="role-finance-action done">只读</text>
              </view>
            </template>
          </view>
          <view v-if="rows.length === 0" class="role-empty inline-empty">
            <text class="title">暂无明细</text>
            <text class="subtle">当前分类暂无数据</text>
          </view>
        </view>
      </view>
    </view>
    <RoleBottomNav active="mine" />
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { cancelWithdraw, pageCommissions, pageFinanceFlows, pageSupplierSettlements, pageWithdraws, type CommissionDetailDTO, type StationFinanceFlowDTO, type SupplierSettlementDTO, type WithdrawApplyDTO } from '@/api/station';
import { confirmAction, currentRole, getRequiredCommissionQuery, getRequiredFinanceQuery, getRequiredRoleQuery, moneyText, showRoleToast } from '@/stores/role';
import { friendlyErrorMessage } from '@/utils/request';
import RoleBottomNav from '@/components/RoleBottomNav.vue';

const roleType = computed(() => currentRole.value);
type FinanceTab = 'settlement' | 'commission' | 'withdraw' | 'detail';
interface FinanceRow {
    id?: number;
    no: string;
    desc: string;
    extra?: string;
    auditRemark?: string;
    status: string;
    amount: string;
    canCancel?: boolean;
}

const tab = ref<FinanceTab>('detail');
const validTabs: FinanceTab[] = ['settlement', 'commission', 'withdraw', 'detail'];
const loading = ref(false);
const error = ref('');
const withdraws = ref<WithdrawApplyDTO[]>([]);
const flows = ref<StationFinanceFlowDTO[]>([]);
const commissions = ref<CommissionDetailDTO[]>([]);
const settlements = ref<SupplierSettlementDTO[]>([]);

const pageTitle = computed(() => {
    if (roleType.value === 'supplier') {
        const map: Record<FinanceTab, string> = {
            settlement: '已结算货款',
            commission: '货款明细',
            withdraw: '提现申请记录',
            detail: '货款明细'
        };
        return map[tab.value] || '货款明细';
    }
    const map: Record<FinanceTab, string> = {
        settlement: '结算记录',
        commission: '佣金明细',
        withdraw: '提现申请记录',
        detail: '账户流水'
    };
    return map[tab.value] || '金额明细';
});

const rows = computed<FinanceRow[]>(() => {
    if (tab.value === 'withdraw') {
        return withdraws.value.map((item) => ({
            id: item.id,
            no: item.withdrawNo || `提现 #${item.id}`,
            desc: `申请时间：${item.createTime || '-'}`,
            extra: `收款账户：默认收款账户 · 审核时间：${item.auditTime || (withdrawStatusText(item) === '待审核' ? '后台审核中' : '-')}`,
            auditRemark: withdrawStatusText(item) === '已拒绝' ? item.auditRemark : '',
            status: withdrawStatusText(item),
            amount: moneyText(item.applyAmount || item.withdrawAmount),
            canCancel: canCancelWithdraw(item)
        }));
    }
    if (tab.value === 'commission') {
        return commissions.value.map((item) => ({
            no: item.commissionNo || `佣金 #${item.id}`,
            desc: `生成时间：${item.createTime || '-'}`,
            extra: `主体：${item.subjectId || '-'} · 类型：${item.subjectType || '-'}`,
            status: commissionStatusText(item.status),
            amount: moneyText(item.amount)
        }));
    }
    if (tab.value === 'settlement') {
        return settlements.value.map((item) => ({
            no: item.settlementNo || `结算 #${item.id}`,
            desc: `结算周期：${periodText(item.periodStartDate, item.periodEndDate)} · 生成时间：${item.createTime || '-'}`,
            extra: `采购金额：${moneyText(item.purchaseAmount)} · 差异金额：${moneyText(item.diffAmount)} · 供应商：${item.supplierId || '-'}`,
            status: settlementStatusText(item.status),
            amount: moneyText(item.payableAmount)
        }));
    }
    return flows.value.map((item) => ({
        no: item.flowNo || `流水 #${item.id}`,
        desc: `业务：${item.bizType || '-'} · ${item.bizNo || '-'}`,
        extra: `发生时间：${item.createTime || '-'}`,
        status: flowTypeText(item.flowType),
        amount: moneyText(item.amount)
    }));
});

onLoad((query) => {
    tab.value = normalizeTab(query?.tab);
    load();
});

function normalizeTab(value: unknown): FinanceTab {
    const text = String(value || 'detail');
    return validTabs.includes(text as FinanceTab) ? text as FinanceTab : 'detail';
}

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
            commissions.value = [];
        } else {
            commissions.value = await safePageRequest(() => pageCommissions({ ...getRequiredCommissionQuery(), pageNum: 1, pageSize: 50 }));
            settlements.value = [];
        }
    } catch (err) {
        error.value = friendlyErrorMessage(err, '财务明细加载失败，请稍后重试');
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

function withdrawStatusText(item: WithdrawApplyDTO) {
    const audit = Number(item.auditStatus || 0);
    const pay = Number(item.payStatus || 0);
    const status = Number(item.status || 0);
    if (pay === 30 || pay === 40 || status === 40) return '打款成功';
    if (pay === 20 || status === 30) return '打款中';
    if (pay === 50 || audit === 30 || status === 50) return '已拒绝';
    if (audit === 20 || status === 20) return '已审核';
    if (status === 70) return '已撤销';
    return '待审核';
}

function commissionStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待结算',
        20: '待审核',
        30: '已入账',
        40: '已扣回',
        50: '已关闭',
        60: '已完成'
    };
    return map[Number(status || 0)] || '待结算';
}

function settlementStatusText(status?: number) {
    const map: Record<number, string> = {
        10: '待结算',
        20: '待审核',
        30: '已结算',
        40: '已关闭',
        50: '已驳回',
        60: '已完成'
    };
    return map[Number(status || 0)] || '待结算';
}

function periodText(start?: string, end?: string) {
    if (!start && !end) return '-';
    return `${shortDate(start) || '-'} 至 ${shortDate(end) || '-'}`;
}

function shortDate(value?: string) {
    if (!value) return '';
    return String(value).split(' ')[0];
}

function flowTypeText(type?: number) {
    const map: Record<number, string> = {
        1: '佣金入账',
        2: '退款扣回',
        3: '提现冻结',
        4: '提现成功',
        5: '提现驳回解冻',
        6: '押金释放',
        7: '提现撤回解冻'
    };
    return map[Number(type || 0)] || '账户流水';
}

function roleStatusClass(status: string) {
    if (/成功|已入账|已结算|已完成|已审核|释放/.test(status)) return 'green';
    if (/待|打款中|冻结|扣回/.test(status)) return 'orange';
    if (/拒绝|驳回|撤销|关闭/.test(status)) return 'gray';
    return 'blue';
}

function canCancelWithdraw(item: WithdrawApplyDTO) {
    return withdrawStatusText(item) === '待审核';
}

async function cancelWithdrawRecord(id?: number) {
    if (!id) {
        showRoleToast('缺少提现申请 ID');
        return;
    }
    const ok = await confirmAction('确认撤回当前提现申请？', '撤回提现');
    if (!ok) return;
    try {
        await cancelWithdraw(id);
        showRoleToast('提现申请已撤回');
        await load();
    } catch (err) {
        showRoleToast(friendlyErrorMessage(err, '撤回提现失败'));
    }
}

function back() {
    uni.redirectTo({ url: '/pages/mine/index' });
}
</script>

<style lang="scss" scoped>
.role-detail-headline {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 64rpx minmax(0, 1fr) auto;
  gap: 16rpx;
  align-items: center;
  margin-top: 18rpx;
}

.role-detail-back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  min-height: 64rpx;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.24);
  border-radius: 999rpx;
  font-size: 44rpx;
  font-weight: 900;
}

.role-detail-title {
  display: block;
  color: #ffffff;
  font-size: 38rpx;
  font-weight: 900;
  line-height: 1.25;
}

.role-detail-section {
  margin-bottom: 24rpx;
  padding: 28rpx;
  background: rgba(255, 255, 255, 0.96);
  border: 1rpx solid rgba(233, 217, 203, 0.86);
  border-radius: 32rpx;
  box-shadow: 0 20rpx 48rpx rgba(60, 33, 16, 0.08);
}

.role-finance-list {
  display: grid;
  gap: 20rpx;
}

.role-finance-list.withdraw-list {
  gap: 18rpx;
}

.role-leave-card {
  padding: 24rpx;
  background: #fff8f2;
  border: 1rpx solid #f1dfd2;
  border-radius: 28rpx;
}

.role-leave-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.role-leave-head > text:first-child {
  min-width: 0;
  overflow: hidden;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-withdraw-record-body {
  display: grid;
  gap: 6rpx;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.6;
}

.role-leave-reject {
  margin-top: 16rpx;
  padding: 16rpx 18rpx;
  color: #bd4f36;
  background: #ffffff;
  border: 1rpx solid #f3d0c2;
  border-radius: 20rpx;
  font-size: 24rpx;
  line-height: 1.5;
}

.role-finance-line {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 150rpx;
  gap: 20rpx;
  align-items: start;
  padding: 22rpx;
  background: #fff8f3;
  border: 1rpx solid #f4ddd0;
  border-radius: 32rpx;
}

.role-finance-main {
  min-width: 0;
}

.role-finance-line-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 10rpx;
}

.role-finance-line-title > text:first-child {
  min-width: 0;
  color: #2d241f;
  font-size: 28rpx;
  font-weight: 900;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.role-finance-main > text {
  display: block;
  color: #8b6a57;
  font-size: 24rpx;
  line-height: 1.55;
}

.role-finance-reject {
  margin-top: 14rpx;
  padding: 16rpx 18rpx;
  color: #bd4f36;
  background: #ffffff;
  border: 1rpx solid #f3d0c2;
  border-radius: 20rpx;
  font-size: 24rpx;
  line-height: 1.5;
}

.role-finance-side {
  display: grid;
  justify-items: end;
  gap: 12rpx;
}

.role-finance-amount {
  color: #d94b34;
  font-size: 30rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-finance-action.done {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 50rpx;
  padding: 0 22rpx;
  color: #8b6a57;
  background: #f3ede7;
  border-radius: 999rpx;
  box-shadow: inset 0 0 0 1rpx #ead8cc;
  font-size: 23rpx;
  font-weight: 900;
}

.role-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 48rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 900;
  white-space: nowrap;
}

.role-status.orange {
  color: #d66323;
  background: #fff0e4;
}

.role-status.green {
  color: #17885f;
  background: #edf9f0;
}

.role-status.blue {
  color: #2b6cb0;
  background: #eef6ff;
}

.role-status.gray {
  color: #78665b;
  background: #f4f1ee;
}

.role-action-btn,
:deep(uni-button.role-action-btn) {
  min-height: 58rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 900;
}

.role-action-btn.primary,
:deep(uni-button.role-action-btn.primary) {
  color: #ffffff;
  background: #e85d3f;
}

.role-action-btn.soft,
:deep(uni-button.role-action-btn.soft) {
  color: #b85a2f;
  background: #fff3ea;
}

.role-order-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 18rpx;
}

.retry-btn {
  margin-top: 18rpx;
}

.inline-empty {
  margin: 0;
}

@media screen and (max-width: 360px) {
  .role-finance-line {
    grid-template-columns: minmax(0, 1fr);
  }

  .role-finance-side {
    justify-items: start;
  }
}
</style>
