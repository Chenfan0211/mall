import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import WmsLayout from '@/layouts/WmsLayout.vue';
import LoginView from '@/views/login/LoginView.vue';
import SupervisorView from '@/views/supervisor/SupervisorView.vue';
import BaseView from '@/views/base/BaseView.vue';
import InboundView from '@/views/inbound/InboundView.vue';
import PutawayView from '@/views/putaway/PutawayView.vue';
import InventoryView from '@/views/inventory/InventoryView.vue';
import OutboundView from '@/views/outbound/OutboundView.vue';
import PickingView from '@/views/picking/PickingView.vue';
import LoadingView from '@/views/loading/LoadingView.vue';
import DeliveryView from '@/views/delivery/DeliveryView.vue';
import ReturnView from '@/views/return/ReturnView.vue';
import StockFlowView from '@/views/stock-flow/StockFlowView.vue';
import OperationLogView from '@/views/operation-log/OperationLogView.vue';

export const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        meta: { public: true }
    },
    {
        path: '/',
        component: WmsLayout,
        redirect: '/supervisor',
        children: [
            { path: 'supervisor', name: 'supervisor', component: SupervisorView, meta: { title: '主管工作台' } },
            { path: 'base', name: 'base', component: BaseView, meta: { title: '基础数据' } },
            { path: 'inbound', name: 'inbound', component: InboundView, meta: { title: '收货入库' } },
            { path: 'putaway', name: 'putaway', component: PutawayView, meta: { title: '上架作业' } },
            { path: 'inventory', name: 'inventory', component: InventoryView, meta: { title: '库存管理' } },
            { path: 'outbound', name: 'outbound', component: OutboundView, meta: { title: '出库单据' } },
            { path: 'picking', name: 'picking', component: PickingView, meta: { title: '拣货复检' } },
            { path: 'loading', name: 'loading', component: LoadingView, meta: { title: '装车出库' } },
            { path: 'delivery', name: 'delivery', component: DeliveryView, meta: { title: '配送线路' } },
            { path: 'return', name: 'return', component: ReturnView, meta: { title: '回仓处理' } },
            { path: 'stock-flow', name: 'stockFlow', component: StockFlowView, meta: { title: '库存流水' } },
            { path: 'operation-log', name: 'operationLog', component: OperationLogView, meta: { title: '操作日志' } }
        ]
    }
];

export const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

router.beforeEach((to) => {
    if (to.meta.public) {
        return true;
    }

    const token = localStorage.getItem('mall_wms_admin_token');
    if (!token) {
        return '/login';
    }

    return true;
});
