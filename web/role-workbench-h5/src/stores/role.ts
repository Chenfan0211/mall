import { computed, reactive } from 'vue';
import type { LoginResult } from '@/api/auth';
import { clearToken } from '@/utils/request';

export type RoleType = 'station' | 'supplier';
export type SubjectStatus = 'normal' | 'loading' | 'loadFailed' | 'noIdentity' | 'subjectDisabled' | 'noPermission';

export interface RoleItem {
    title: string;
    desc: string;
    page: string;
    tag?: string;
}

export interface RoleMetric {
    value: string;
    label: string;
    page: string;
    valueClass?: string;
}

export interface RoleProfile {
    roleName: string;
    title: string;
    entity: string;
    account: string;
    boundMobile: string;
    mobilePlain: string;
    password: string;
    subtitle: string;
    loginNote: string;
    subjectId?: number;
    leaderId?: number;
    supplierId?: number;
    status: SubjectStatus;
}

export interface RoleSession {
    accountId?: number;
    subjectType?: number;
    username: string;
    mobile: string;
    stationId?: number;
    leaderId?: number;
    supplierId?: number;
    authorities: string[];
}

interface EntityNameCache {
    station?: string;
    leader?: string;
    supplier?: string;
}

interface DataScopeDTO {
    scopeType?: number;
    scopeObjectId?: number;
}

const DATA_SCOPE_SUPPLIER = 3;
const DATA_SCOPE_STATION = 4;
const DATA_SCOPE_LEADER = 7;
const FINANCE_SUBJECT_SUPPLIER = 1;
const FINANCE_SUBJECT_STATION = 2;
const FINANCE_SUBJECT_LEADER = 3;
const COMMISSION_SUBJECT_STATION = 1;
const COMMISSION_SUBJECT_LEADER = 2;
const COMMISSION_SUBJECT_SUPPLIER = 3;
const MESSAGE_RECEIVER_SUPPLIER = 2;
const MESSAGE_RECEIVER_STATION = 4;
const MESSAGE_RECEIVER_LEADER = 5;

const defaultProfiles: Record<RoleType, RoleProfile> = {
    station: {
        roleName: '自提点角色',
        title: '团点协同工作台',
        entity: '当前自提点',
        account: 'test_station',
        boundMobile: '13800000012',
        mobilePlain: '13800000012',
        password: 'Test@123456',
        subtitle: '查看当前自提点订单、到货、退货、资金和休假记录',
        loginNote: '自提点账号登录后按账号数据范围读取自提点和团长数据。',
        status: 'normal'
    },
    supplier: {
        roleName: '供应商角色',
        title: '供应商协同工作台',
        entity: '当前供应商',
        account: 'test_supplier_h5',
        boundMobile: '13800000014',
        mobilePlain: '13800000014',
        password: 'Test@123456',
        subtitle: '查看当前供应商采购、送货和结算数据',
        loginNote: '供应商账号登录后按账号数据范围读取自身供应商数据。',
        status: 'normal'
    }
};

const state = reactive({
    roleType: (uni.getStorageSync('mall_role_h5_role') as RoleType) || 'station',
    token: (uni.getStorageSync('mall_role_h5_token') as string) || '',
    session: readSession(),
    entityNames: readEntityNames(),
    lastToast: ''
});

export const currentRole = computed<RoleType>(() => state.roleType);
export const currentSession = computed<RoleSession>(() => state.session);
export const isLoggedIn = computed(() => Boolean(state.token));
export const currentProfile = computed<RoleProfile>(() => {
    const base = defaultProfiles[state.roleType];
    const session = state.session;
    const subjectId = state.roleType === 'supplier' ? session.supplierId : (session.stationId || session.leaderId);
    return {
        ...base,
        entity: resolveEntityName(base, session),
        account: session.username || base.account,
        boundMobile: maskMobile(session.mobile || base.mobilePlain),
        mobilePlain: session.mobile || base.mobilePlain,
        subjectId,
        supplierId: session.supplierId,
        leaderId: session.leaderId
    };
});

export const stationQuickItems: RoleItem[] = [
    { title: '提货订单', desc: '当前自提点订单与商品明细', page: '/pages/orders/index' },
    { title: '门店作业', desc: '到货、退货和异常记录', page: '/pages/store/index' },
    { title: '资金佣金', desc: '账户、佣金、提现和流水', page: '/pages/mine/index' },
    { title: '消息中心', desc: '作业提醒、审核和资金通知', page: '/pages/messages/index' }
];

export const supplierQuickItems: RoleItem[] = [
    { title: '采购到货', desc: '采购单、送货单和到仓记录', page: '/pages/store/supplier-purchase' },
    { title: '结算提现', desc: '供应商结算和资金流水', page: '/pages/mine/index' },
    { title: '消息中心', desc: '作业提醒、审核和资金通知', page: '/pages/messages/index' }
];

export function setRoleType(roleType: RoleType) {
    state.roleType = roleType;
    uni.setStorageSync('mall_role_h5_role', roleType);
}

export function applyLoginResult(result: LoginResult, fallbackUsername: string) {
    const account = result.account || {};
    const scopes = result.dataScopes || [];
    const nextSession: RoleSession = {
        accountId: account.id,
        subjectType: account.subjectType,
        username: account.username || fallbackUsername,
        mobile: account.mobile || '',
        supplierId: findScope(scopes, DATA_SCOPE_SUPPLIER) || (account.subjectType === 2 ? account.subjectId : undefined),
        stationId: findScope(scopes, DATA_SCOPE_STATION) || (account.subjectType === 4 ? account.subjectId : undefined),
        leaderId: findScope(scopes, DATA_SCOPE_LEADER) || (account.subjectType === 5 ? account.subjectId : undefined),
        authorities: result.authorities || []
    };
    state.session = nextSession;
    uni.setStorageSync('mall_role_h5_session', JSON.stringify(nextSession));
}

export function setRoleToken(token: string) {
    state.token = token;
    uni.setStorageSync('mall_role_h5_token', token);
}

export function setCurrentEntityName(name?: string) {
    const text = String(name || '').trim();
    if (!text) return;
    if (state.roleType === 'supplier') {
        state.entityNames.supplier = text;
    } else if (state.session.stationId) {
        state.entityNames.station = text;
    } else if (state.session.leaderId) {
        state.entityNames.leader = text;
    }
    uni.setStorageSync('mall_role_h5_entity_names', JSON.stringify(state.entityNames));
}

export function getRoleQuery() {
    if (state.roleType === 'supplier') {
        return { supplierId: state.session.supplierId };
    }
    return { stationId: state.session.stationId, leaderId: state.session.leaderId };
}

export function getRequiredRoleQuery() {
    const query = getRoleQuery();
    if (state.roleType === 'supplier' && !query.supplierId) {
        throw new Error('当前账号未绑定供应商数据范围');
    }
    if (state.roleType === 'station' && !query.stationId && !query.leaderId) {
        throw new Error('当前账号未绑定自提点或团长数据范围');
    }
    return query;
}

export function getRequiredFinanceQuery() {
    if (state.roleType === 'supplier') {
        if (!state.session.supplierId) {
            throw new Error('当前账号未绑定供应商数据范围');
        }
        return { subjectType: FINANCE_SUBJECT_SUPPLIER, subjectId: state.session.supplierId };
    }
    if (state.session.stationId) {
        return { subjectType: FINANCE_SUBJECT_STATION, subjectId: state.session.stationId };
    }
    if (state.session.leaderId) {
        return { subjectType: FINANCE_SUBJECT_LEADER, subjectId: state.session.leaderId };
    }
    throw new Error('当前账号未绑定自提点或团长数据范围');
}

export function getRequiredCommissionQuery() {
    if (state.roleType === 'supplier') {
        if (!state.session.supplierId) {
            throw new Error('当前账号未绑定供应商数据范围');
        }
        return { subjectType: COMMISSION_SUBJECT_SUPPLIER, subjectId: state.session.supplierId };
    }
    if (state.session.stationId) {
        return {
            subjectType: COMMISSION_SUBJECT_STATION,
            subjectId: state.session.stationId,
            stationId: state.session.stationId
        };
    }
    if (state.session.leaderId) {
        return { subjectType: COMMISSION_SUBJECT_LEADER, subjectId: state.session.leaderId };
    }
    throw new Error('当前账号未绑定自提点或团长数据范围');
}

export function getRequiredMessageQuery() {
    if (state.roleType === 'supplier') {
        if (!state.session.supplierId) {
            throw new Error('当前账号未绑定供应商数据范围');
        }
        return { receiverType: MESSAGE_RECEIVER_SUPPLIER, receiverId: state.session.supplierId };
    }
    if (state.session.stationId) {
        return { receiverType: MESSAGE_RECEIVER_STATION, receiverId: state.session.stationId };
    }
    if (state.session.leaderId) {
        return { receiverType: MESSAGE_RECEIVER_LEADER, receiverId: state.session.leaderId };
    }
    throw new Error('当前账号未绑定自提点或团长数据范围');
}

export function moneyText(value: string | number | undefined) {
    const amount = value === undefined || value === null || value === '' ? '0.00' : String(value);
    return `¥${amount}`;
}

export function goPage(url: string) {
    const tabPages = ['/pages/workbench/index', '/pages/orders/index', '/pages/store/index', '/pages/mine/index'];
    if (tabPages.includes(url)) {
        uni.redirectTo({ url });
        return;
    }
    uni.navigateTo({ url });
}

export function showRoleToast(title: string) {
    state.lastToast = title;
    uni.showToast({ title, icon: 'none' });
}

export function confirmAction(content: string, title = '确认操作') {
    return new Promise<boolean>((resolve) => {
        uni.showModal({
            title,
            content,
            confirmText: '确认',
            cancelText: '取消',
            success(result) {
                resolve(Boolean(result.confirm));
            },
            fail() {
                resolve(false);
            }
        });
    });
}

export async function refreshCurrentEntityName(fetcher: {
    getStationDetail: (id: number) => Promise<{ stationName?: string; contactName?: string }>;
    getLeaderDetail: (id: number) => Promise<{ leaderName?: string }>;
}) {
    if (state.roleType !== 'station') {
        return;
    }
    try {
        if (state.session.stationId) {
            const station = await fetcher.getStationDetail(Number(state.session.stationId));
            setCurrentEntityName(station.stationName || station.contactName);
            return;
        }
        if (state.session.leaderId) {
            const leader = await fetcher.getLeaderDetail(Number(state.session.leaderId));
            setCurrentEntityName(leader.leaderName);
        }
    } catch {
        // Keep the existing fallback name when the identity detail request is unavailable.
    }
}

export function logoutRole() {
    state.token = '';
    state.session = emptySession();
    state.entityNames = {};
    clearToken();
    uni.removeStorageSync('mall_role_h5_session');
    uni.removeStorageSync('mall_role_h5_entity_names');
}

function findScope(scopes: DataScopeDTO[], scopeType: number) {
    return scopes.find((item) => item.scopeType === scopeType)?.scopeObjectId;
}

function readSession(): RoleSession {
    const raw = uni.getStorageSync('mall_role_h5_session') as string;
    if (!raw) {
        return emptySession();
    }
    try {
        return { ...emptySession(), ...JSON.parse(raw) };
    } catch {
        return emptySession();
    }
}

function readEntityNames(): EntityNameCache {
    const raw = uni.getStorageSync('mall_role_h5_entity_names') as string;
    if (!raw) {
        return {};
    }
    try {
        return JSON.parse(raw) || {};
    } catch {
        return {};
    }
}

function emptySession(): RoleSession {
    return {
        username: '',
        mobile: '',
        authorities: []
    };
}

function resolveEntityName(base: RoleProfile, session: RoleSession) {
    if (state.roleType === 'supplier' && session.supplierId) {
        return state.entityNames.supplier || `供应商 #${session.supplierId}`;
    }
    if (state.roleType === 'station' && session.stationId) {
        return state.entityNames.station || `自提点 #${session.stationId}`;
    }
    if (state.roleType === 'station' && session.leaderId) {
        return state.entityNames.leader || `团长 #${session.leaderId}`;
    }
    return base.entity;
}

function maskMobile(mobile: string) {
    if (!mobile || mobile.length < 7) {
        return mobile || '-';
    }
    return `${mobile.slice(0, 3)}****${mobile.slice(-4)}`;
}
