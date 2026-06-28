<template>
  <view class="page">
    <view class="role-header">
      <view class="status-bar">
        <text>14:49</text>
        <text>{{ roleType === 'supplier' ? '供应商作业' : '门店作业' }}</text>
      </view>
      <text class="subtle">{{ profile.entity }}</text>
      <text class="title">{{ roleType === 'supplier' ? '采购、到仓和补充供货' : '到货、退货和异常处理' }}</text>
      <text class="subtle">{{ roleType === 'supplier' ? '只处理当前供应商 SKU 和送货单' : '当前自提点数据隔离，操作记录可追溯' }}</text>
    </view>

    <template v-if="roleType === 'station'">
      <view class="section">
        <view class="section-head">
          <view>
            <text class="title">今日到货商品</text>
            <text class="subtle">{{ delivery.label }}</text>
          </view>
          <button class="plain mini" @click="open('/pages/store/notify-preview')">到货通知</button>
        </view>
        <view class="card-list">
          <view v-for="item in delivery.goods" :key="item.id" class="product-card" @click="open(`/pages/store/delivery-detail?id=${item.id}`)">
            <view class="product-line">
              <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
              <view class="card-main">
                <text class="card-title">{{ item.title }}</text>
                <text class="card-desc">{{ item.spec }} · 影响 {{ item.users.length }} 位用户</text>
              </view>
              <view class="arrival-count">
                <text>{{ item.actual }}/{{ item.expected }}</text>
                <text>实到/应到</text>
              </view>
            </view>
            <view class="action-row">
              <button class="primary" @click.stop="confirmArrival(item.title)">确认到货</button>
              <button class="danger" @click.stop="open(`/pages/store/shortage?id=${item.id}`)">缺货标记</button>
            </view>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="section-head">
          <view>
            <text class="title">退货交接</text>
            <text class="subtle">按售后单 / 商品维度跟踪司机取回和返仓结果</text>
          </view>
        </view>
        <view class="card-list">
          <view v-for="item in delivery.returns" :key="item.id" class="product-card" @click="open(`/pages/store/return-detail?id=${item.id}`)">
            <view class="card-head">
              <view class="card-main">
                <text class="card-title">{{ item.title }}</text>
                <text class="card-desc">{{ item.spec }} · {{ item.users.length }} 位用户</text>
              </view>
              <text class="status-pill orange">{{ item.status }}</text>
            </view>
            <button class="plain row-action" @click.stop="handover(item.title)">确认交接</button>
          </view>
        </view>
      </view>
    </template>

    <template v-else>
      <view class="section">
        <view class="section-head">
          <view>
            <text class="title">采购 / 到仓</text>
            <text class="subtle">供应商只处理自身采购单和到仓凭证</text>
          </view>
          <button class="plain mini" @click="open('/pages/store/supplier-purchase')">查看全部</button>
        </view>
        <view class="card-list">
          <view v-for="item in profile.purchases" :key="item.no" class="record-card" @click="open(`/pages/store/supplier-purchase-detail?no=${item.no}`)">
            <view class="card-head">
              <view class="card-main">
                <text class="card-title">{{ item.no }}</text>
                <text class="card-desc">{{ item.warehouse }} · {{ item.pickup }}</text>
              </view>
              <text class="status-pill" :class="item.statusClass">{{ item.status }}</text>
            </view>
            <view class="purchase-foot">
              <text>{{ item.totalQty }} 件</text>
              <text class="money">¥{{ item.amount.toFixed(2) }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="section-head">
          <view>
            <text class="title">补充供货申请</text>
            <text class="subtle">选择自身 SKU 后提交，等待平台处理</text>
          </view>
        </view>
        <view class="card-list">
          <view v-for="item in profile.salesProducts.slice(0, 3)" :key="item.id" class="product-card">
            <view class="product-line">
              <view class="product-thumb">{{ item.title.slice(0, 1) }}</view>
              <view class="card-main">
                <text class="card-title">{{ item.title }}</text>
                <text class="card-desc">{{ item.spec }} · {{ item.status }}</text>
              </view>
              <button class="plain add-btn" @click="supplement(item.title)">补货</button>
            </view>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { addOperation, confirmAction, currentProfile, currentRole, goPage, showRoleToast } from '@/stores/role';

const profile = computed(() => currentProfile.value);
const roleType = computed(() => currentRole.value);
const delivery = computed(() => profile.value.deliveryRecords[0] || { label: '暂无配送日', goods: [], returns: [] });

function open(url: string) {
    goPage(url);
}

async function confirmArrival(title: string) {
    const ok = await confirmAction(`确认 ${title} 已到货？`, '确认到货');
    if (!ok) return;
    addOperation({
        no: `ARR${Date.now()}`,
        type: '到货确认',
        title: `${title} 已确认到货`,
        time: '刚刚',
        status: '已完成',
        next: '可发送到货通知'
    });
    showRoleToast('到货确认接口暂未接通，已记录前端操作');
}

async function handover(title: string) {
    const ok = await confirmAction(`确认 ${title} 已交接给司机？`, '退货交接');
    if (ok) {
        showRoleToast('退货交接接口暂未接通，已保留交互');
    }
}

async function supplement(title: string) {
    const ok = await confirmAction(`确认提交 ${title} 的补充供货申请？`, '补充供货');
    if (ok) {
        addOperation({
            no: `PR${Date.now()}`,
            type: '补充供货申请',
            title: `${title} 补充供货申请`,
            time: '刚刚',
            status: '已提交待处理',
            next: '等待平台处理'
        });
        showRoleToast('补充供货接口暂未接通，已记录前端操作');
    }
}
</script>

<style lang="scss" scoped>
.mini {
  min-height: 58rpx;
  padding: 0 18rpx;
  font-size: 23rpx;
}

.arrival-count {
  text-align: right;
}

.arrival-count text:first-child {
  display: block;
  color: #d94b34;
  font-weight: 900;
}

.arrival-count text:last-child {
  color: #8f6c58;
  font-size: 22rpx;
}

.action-row {
  margin-top: 16rpx;
}

.action-row button {
  flex: 1;
  min-height: 62rpx;
  font-size: 24rpx;
}

.row-action {
  width: 100%;
  margin-top: 18rpx;
}

.purchase-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 18rpx;
  color: #8f6c58;
  font-size: 24rpx;
}

.add-btn {
  min-width: 98rpx;
  min-height: 58rpx;
  padding: 0 18rpx;
  font-size: 23rpx;
}
</style>
