import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import AdminLayout from '@/layouts/AdminLayout.vue';
import LoginView from '@/views/login/LoginView.vue';
import DashboardView from '@/views/dashboard/DashboardView.vue';
import ProductSaleView from '@/views/product-sale/ProductSaleView.vue';
import FulfillmentView from '@/views/fulfillment/FulfillmentView.vue';
import StationLeaderView from '@/views/station-leader/StationLeaderView.vue';
import SupplierPurchaseView from '@/views/supplier-purchase/SupplierPurchaseView.vue';
import FinanceRiskView from '@/views/finance-risk/FinanceRiskView.vue';
import UserMarketingView from '@/views/user-marketing/UserMarketingView.vue';
import ReportSystemView from '@/views/report-system/ReportSystemView.vue';

export const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        meta: { public: true }
    },
    {
        path: '/',
        component: AdminLayout,
        redirect: '/dashboard',
        children: [
            { path: 'dashboard', name: 'dashboard', component: DashboardView, meta: { title: '运营驾驶舱' } },
            { path: 'product-sale', name: 'productSale', component: ProductSaleView, meta: { title: '商品与团期' } },
            { path: 'fulfillment', name: 'fulfillment', component: FulfillmentView, meta: { title: '交易履约' } },
            { path: 'station-leader', name: 'stationLeader', component: StationLeaderView, meta: { title: '自提点/团长' } },
            {
                path: 'supplier-purchase',
                name: 'supplierPurchase',
                component: SupplierPurchaseView,
                meta: { title: '供应商/采购' }
            },
            { path: 'finance-risk', name: 'financeRisk', component: FinanceRiskView, meta: { title: '财务风控' } },
            { path: 'user-marketing', name: 'userMarketing', component: UserMarketingView, meta: { title: '用户与营销' } },
            { path: 'report-system', name: 'reportSystem', component: ReportSystemView, meta: { title: '报表与系统' } }
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

    const token = localStorage.getItem('mall_admin_token');
    if (!token) {
        return '/login';
    }

    return true;
});
