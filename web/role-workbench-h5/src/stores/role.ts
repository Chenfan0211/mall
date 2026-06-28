import { computed, reactive } from 'vue';

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
}

export interface RoleProduct {
    id: string;
    title: string;
    spec: string;
    qty: number;
    price: string;
    status?: string;
    shortageQty?: number;
}

export interface RoleOrder {
    no: string;
    status: string;
    statusClass: string;
    time: string;
    amount: string;
    totalQty: number;
    station: string;
    pickup: string;
    userName?: string;
    userMobile?: string;
    userRemark?: string;
    warehouse?: string;
    items: RoleProduct[];
}

export interface DeliveryRecord {
    date: string;
    label: string;
    goods: Array<RoleProduct & { expected: number; actual: number; users: Array<{ name: string; mobile: string; qty: number; orderNo: string }> }>;
    returns: Array<RoleProduct & { status: string; users: Array<{ name: string; mobile: string; qty: number; orderNo: string; pickupQty?: number; warehouseQty?: number }> }>;
}

export interface WithdrawRecord {
    no: string;
    amount: string;
    status: string;
    statusClass: string;
    applyTime: string;
    auditTime: string;
    account: string;
    rejectReason?: string;
}

export interface LeaveRecord {
    no: string;
    range: string;
    days: number;
    reason: string;
    status: string;
    statusClass: string;
    applyTime: string;
    auditTime: string;
    rejectReason?: string;
}

export interface RoleMessage {
    group: string;
    title: string;
    time: string;
    status: string;
    readStatus?: number;
}

export interface OperationRecord {
    no: string;
    type: string;
    title: string;
    time: string;
    status: string;
    next: string;
}

export interface PurchaseRecord {
    no: string;
    warehouse: string;
    pickup: string;
    totalQty: number;
    amount: number;
    status: string;
    statusClass: string;
    applyTime: string;
    auditTime?: string;
    rejectReason?: string;
    orderNo?: string;
    items: RoleProduct[];
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
    subjectId: number;
    leaderId?: number;
    status: SubjectStatus;
    kpis: RoleMetric[];
    tasks: RoleItem[];
    quicks: RoleItem[];
    wallet: {
        amount: string;
        label: string;
        stats: Array<{ value: string; label: string }>;
    };
    accountSummary: string;
    withdrawMin: string;
    withdrawFee: string;
    services: RoleItem[];
    orders: RoleOrder[];
    deliveryRecords: DeliveryRecord[];
    withdrawRequests: WithdrawRecord[];
    leaveRequests: LeaveRecord[];
    messages: RoleMessage[];
    operations: OperationRecord[];
    purchases: PurchaseRecord[];
    salesProducts: RoleProduct[];
}

const stationProfile: RoleProfile = {
    roleName: '自提点角色',
    title: '团点协同工作台',
    entity: '越秀东湖店',
    account: 'station_yx',
    boundMobile: '138****2918',
    mobilePlain: '13800002918',
    password: '888888',
    subtitle: '查看本团点提货订单、用户信息、门店作业、佣金与休假安排；自提点继承团长全部能力。',
    loginNote: '团点入口登录后先判断团长 / 自提点身份；同时具备时默认进入自提点工作台。',
    subjectId: 720001,
    leaderId: 730001,
    status: 'normal',
    kpis: [
        { value: '186', label: '今日提货订单', page: '/pages/orders/index' },
        { value: '42', label: '待收货商品', page: '/pages/store/index' },
        { value: '¥320.50', label: '今日佣金', page: '/pages/finance/index' }
    ],
    tasks: [
        { title: '待到货', desc: '广州 A 线司机预计 16:00 抵达，涉及蓝莓、橙子和牛角包。', page: '/pages/store/index', tag: '到货' },
        { title: '待通知', desc: '到货后先预览影响用户和商品，再发送到货通知。', page: '/pages/store/notify-preview', tag: '通知' },
        { title: '待提货', desc: '今日提货订单默认按提货日筛选，优先处理已通知待提货用户。', page: '/pages/orders/index', tag: '核销' },
        { title: '待退货', desc: '按售后单 / 订单商品维度跟踪司机取回和返仓结果。', page: '/pages/store/return-detail', tag: '退货' }
    ],
    quicks: [
        { title: '提货订单', desc: '用户订单、今日提货日、联系方式', page: '/pages/orders/index' },
        { title: '门店作业', desc: '到货通知、退货处理、异常提交', page: '/pages/store/index' },
        { title: '操作记录', desc: '休假、提现、异常、通知提交记录', page: '/pages/records/index' },
        { title: '消息中心', desc: '作业提醒、审核结果、资金通知', page: '/pages/messages/index' }
    ],
    wallet: {
        amount: '¥1,286.00',
        label: '可提现佣金',
        stats: [
            { value: '¥320.00', label: '审核中金额' },
            { value: '¥126.00', label: '售后扣回' },
            { value: '¥500.00', label: '不可提现押金' }
        ]
    },
    accountSummary: '招商银行(尾号 3921)，后台已核验',
    withdrawMin: '100.00',
    withdrawFee: '0.00',
    services: [
        { title: '收退货协同', desc: '确认用户退货商品与数量，回传售后状态。', page: '/pages/store/return-detail' },
        { title: '休假设置', desc: '关闭可售日期，避免用户下单到休假团点。', page: '/pages/leave/index' },
        { title: '客户联络', desc: '查看用户姓名、电话、今日提货数据和提货备注。', page: '/pages/orders/index' },
        { title: '团点公告', desc: '维护提货提醒、停车说明和到货通知。', page: '/pages/store/notify-preview' }
    ],
    orders: [
        {
            status: '未发货',
            statusClass: 'orange',
            no: 'DL20260610001',
            time: '2026-06-09 10:42:12',
            amount: '151.40',
            totalQty: 4,
            station: '越秀东湖店',
            pickup: '06月10日 16:00-20:00',
            userName: '张晓琳',
            userMobile: '138****2918',
            userRemark: '放南门货架，孩子 18 点后代取。',
            items: [
                { id: 'orange', title: '四川爱媛果冻橙 5斤装', spec: '5斤装 / 单果70-80mm', qty: 1, price: '62.80' },
                { id: 'blueberry', title: '云南蓝莓 125g*4盒', spec: '125g*4盒 / 冷藏到店', qty: 2, price: '44.30' },
                { id: 'croissant', title: '早餐牛角包组合', spec: '6枚装 / 当日烘焙', qty: 1, price: '0.00' }
            ]
        },
        {
            status: '待售后确认',
            statusClass: 'blue',
            no: 'DL20260610002',
            time: '2026-06-09 11:18:45',
            amount: '119.60',
            totalQty: 3,
            station: '越秀东湖店',
            pickup: '06月10日 18:00-21:00',
            userName: '陈一宁',
            userMobile: '139****6402',
            userRemark: '蓝莓箱有轻微破损，需现场核对。',
            items: [
                { id: 'blueberry', title: '云南蓝莓 125g*4盒', spec: '125g*4盒 / 冷藏到店', qty: 1, price: '88.60' },
                { id: 'plum', title: '新疆西梅干 500g', spec: '500g / 袋装', qty: 2, price: '15.50' }
            ]
        },
        {
            status: '已通知待提货',
            statusClass: 'green',
            no: 'DL20260610003',
            time: '2026-06-09 12:06:20',
            amount: '238.80',
            totalQty: 6,
            station: '越秀东湖店',
            pickup: '06月10日 17:00-20:30',
            userName: '王敏',
            userMobile: '136****5172',
            userRemark: '老人代取，请电话提醒。',
            items: [
                { id: 'mango', title: '海南贵妃芒 5斤装', spec: '5斤装 / 自然熟果', qty: 2, price: '59.90' },
                { id: 'apple', title: '烟台红富士苹果 6枚', spec: '精选大果 / 6枚装', qty: 2, price: '39.50' },
                { id: 'grain', title: '坚果燕麦礼盒', spec: '1.2kg / 礼盒装', qty: 2, price: '20.00' }
            ]
        }
    ],
    deliveryRecords: [
        {
            date: '2026-06-10',
            label: '06月10日 今日',
            goods: [
                {
                    id: 'blueberry',
                    title: '云南蓝莓 125g*4盒',
                    spec: '125g*4盒 / 冷藏到店',
                    qty: 38,
                    price: '44.30',
                    expected: 42,
                    actual: 38,
                    users: [
                        { name: '张晓琳', mobile: '138****2918', qty: 2, orderNo: 'DL20260610001' },
                        { name: '陈一宁', mobile: '139****6402', qty: 1, orderNo: 'DL20260610002' },
                        { name: '王敏', mobile: '136****5172', qty: 3, orderNo: 'DL20260610003' }
                    ]
                },
                {
                    id: 'orange',
                    title: '四川爱媛果冻橙 5斤装',
                    spec: '5斤装 / 单果70-80mm',
                    qty: 36,
                    price: '62.80',
                    expected: 36,
                    actual: 36,
                    users: [
                        { name: '张晓琳', mobile: '138****2918', qty: 1, orderNo: 'DL20260610001' },
                        { name: '周嘉', mobile: '135****6109', qty: 2, orderNo: 'DL20260610008' }
                    ]
                }
            ],
            returns: [
                {
                    id: 'return-blueberry',
                    title: '云南蓝莓 125g*4盒',
                    spec: '箱体轻微破损 / 现场待核对',
                    qty: 2,
                    price: '44.30',
                    status: '待司机取回',
                    users: [
                        { name: '陈一宁', mobile: '139****6402', qty: 1, orderNo: 'DL20260610002' },
                        { name: '王敏', mobile: '136****5172', qty: 1, orderNo: 'DL20260610003' }
                    ]
                }
            ]
        }
    ],
    withdrawRequests: [
        { no: 'WD20260610001', amount: '¥500.00', status: '待审核', statusClass: 'orange', applyTime: '2026-06-10 19:20', auditTime: '后台审核中', account: '招商银行(尾号 3921)' },
        { no: 'WD20260530007', amount: '¥1,200.00', status: '已到账', statusClass: 'green', applyTime: '2026-05-30 10:20', auditTime: '2026-05-31 09:30', account: '招商银行(尾号 3921)' },
        { no: 'WD20260515003', amount: '¥900.00', status: '已拒绝', statusClass: 'gray', applyTime: '2026-05-15 10:16', auditTime: '2026-05-15 18:42', account: '招商银行(尾号 3921)', rejectReason: '申请金额低于当时可提现佣金，请调整后重新提交。' }
    ],
    leaveRequests: [
        { no: 'LV20260610001', range: '2026-06-18 至 2026-06-20', days: 3, reason: '店主外出，期间无人值守自提点。', status: '待审核', statusClass: 'orange', applyTime: '2026-06-10 09:30', auditTime: '后台审核中' },
        { no: 'LV20260602003', range: '2026-06-06 至 2026-06-06', days: 1, reason: '门店设备检修，暂停提货一天。', status: '已通过', statusClass: 'green', applyTime: '2026-06-02 14:12', auditTime: '2026-06-03 10:08' },
        { no: 'LV20260528002', range: '2026-06-01 至 2026-06-03', days: 3, reason: '临时休整。', status: '已拒绝', statusClass: 'gray', applyTime: '2026-05-28 18:40', auditTime: '2026-05-29 11:20', rejectReason: '该日期已有较多用户选择该自提点提货，请先完成履约后再申请。' }
    ],
    messages: [
        { group: '作业提醒', title: '今日 16:00 到货后请先发送到货通知', time: '2026-06-10 14:20', status: '今日需处理', readStatus: 0 },
        { group: '审核结果', title: '休假申请 LV20260602003 已通过', time: '2026-06-03 10:08', status: '已完成', readStatus: 1 },
        { group: '资金通知', title: '提现 WD20260530007 已到账', time: '2026-05-31 09:30', status: '已完成', readStatus: 1 }
    ],
    operations: [
        { no: 'NT20260610001', type: '到货通知', title: '06月10日蓝莓/橙子到货通知', time: '2026-06-10 15:42', status: '已发送', next: '用户可到店提货' },
        { no: 'LV20260610001', type: '休假申请', title: '2026-06-18 至 2026-06-20休假', time: '2026-06-10 09:30', status: '待审核', next: '等待后台审核' },
        { no: 'WD20260610001', type: '提现申请', title: '申请提现 ¥500.00', time: '2026-06-10 19:20', status: '待审核', next: '等待财务审核' }
    ],
    purchases: [],
    salesProducts: []
};

const supplierProfile: RoleProfile = {
    roleName: '供应商角色',
    title: '供应协同工作台',
    entity: '华南果业',
    account: 'supplier_hn',
    boundMobile: '139****6402',
    mobilePlain: '13900006402',
    password: '888888',
    subtitle: '查看本供应商采购、到仓、货款结算与本人操作记录；不展示 C 端用户信息。',
    loginNote: '供应商入口登录后只判断供应商主体；停用或合作终止时只允许查看历史记录。',
    subjectId: 610001,
    status: 'normal',
    kpis: [
        { value: '4', label: '待确认采购', page: '/pages/store/supplier-purchase' },
        { value: '3', label: '待提交到仓', page: '/pages/orders/index' },
        { value: '¥2,860', label: '可提现货款', page: '/pages/finance/index' }
    ],
    tasks: [
        { title: '待我确认采购', desc: '采购单展示是否需要供应商动作，不展示采购、仓库、财务内部流转。', page: '/pages/store/supplier-purchase', tag: '采购' },
        { title: '待提交到仓', desc: '上传到仓凭证后等待仓库确认，不由供应商确认入库。', page: '/pages/orders/index', tag: '到仓' }
    ],
    quicks: [
        { title: '采购/到仓', desc: '采购列表、到仓凭证和提交结果', page: '/pages/store/supplier-purchase' },
        { title: '补充供货申请', desc: '选择自身 SKU 后提交供货申请', page: '/pages/store/index' },
        { title: '货款结算', desc: '可提现货款、结算记录和驳回原因', page: '/pages/finance/index' },
        { title: '操作记录', desc: '采购申请、到仓凭证、提现记录', page: '/pages/records/index' },
        { title: '消息中心', desc: '作业提醒、审核结果、资金通知', page: '/pages/messages/index' }
    ],
    wallet: {
        amount: '¥2,860.00',
        label: '可提现货款',
        stats: [
            { value: '¥1,860.00', label: '审核中货款' },
            { value: '¥52,480.00', label: '已结算货款' },
            { value: '¥126.00', label: '售后扣回' }
        ]
    },
    accountSummary: '华南果业对公账户(尾号 6208)，后台已核验',
    withdrawMin: '200.00',
    withdrawFee: '0.00',
    services: [
        { title: '商品表现', desc: '查看商品图片、名称、规格、销量和发布状态。', page: '/pages/service/index' },
        { title: '售后复核', desc: '按订单商品和批次核对售后原因。', page: '/pages/finance/list?tab=afterSale' },
        { title: '货款结算', desc: '按采购单、到仓凭证、入库结果和结算状态追踪。', page: '/pages/finance/index' },
        { title: '送货资料', desc: '提交送货信息、查看到仓要求和预约记录。', page: '/pages/store/supplier-purchase' }
    ],
    orders: [
        {
            status: '送货中',
            statusClass: 'blue',
            no: 'SH20260610001',
            time: '2026-06-09 09:26:18',
            amount: '3,458.00',
            totalQty: 17,
            station: '广州中心仓',
            warehouse: '广州中心仓',
            pickup: '06月10日 08:00 到仓',
            items: [
                { id: 'blueberry-box', title: '云南蓝莓 125g*24盒', spec: '整箱 / 冷藏 / 24盒', qty: 5, price: '1286.00' },
                { id: 'orange', title: '四川爱媛果冻橙 5斤装', spec: '5斤装 / 单果70-80mm', qty: 8, price: '156.00' },
                { id: 'apple', title: '烟台红富士苹果 6枚', spec: '精选大果 / 6枚装', qty: 4, price: '245.00' }
            ]
        },
        {
            status: '待结算',
            statusClass: 'green',
            no: 'SH20260610002',
            time: '2026-06-09 13:05:39',
            amount: '2,340.00',
            totalQty: 8,
            station: '深圳中心仓',
            warehouse: '深圳中心仓',
            pickup: '06月11日 09:00 到仓',
            items: [
                { id: 'plum-box', title: '新疆西梅干 500g', spec: '500g / 24袋 / 箱', qty: 8, price: '292.50', shortageQty: 1 },
                { id: 'grain', title: '坚果燕麦礼盒', spec: '1.2kg / 礼盒装', qty: 2, price: '0.00' }
            ]
        }
    ],
    deliveryRecords: [],
    withdrawRequests: [
        { no: 'WD20260610008', amount: '¥1,000.00', status: '待审核', statusClass: 'orange', applyTime: '2026-06-10 15:20', auditTime: '后台审核中', account: '华南果业对公账户' },
        { no: 'WD20260607004', amount: '¥1,860.00', status: '已到账', statusClass: 'green', applyTime: '2026-06-07 11:12', auditTime: '2026-06-08 10:30', account: '华南果业对公账户' },
        { no: 'WD20260602002', amount: '¥2,000.00', status: '已拒绝', statusClass: 'gray', applyTime: '2026-06-02 16:42', auditTime: '2026-06-03 09:18', account: '华南果业对公账户', rejectReason: '申请金额超过当前已审核可提现货款，请调整金额后重新提交。' }
    ],
    leaveRequests: [],
    messages: [
        { group: '作业提醒', title: 'SH20260610001 待提交到仓凭证', time: '2026-06-10 10:20', status: '今日需处理', readStatus: 0 },
        { group: '审核结果', title: 'PR20260609022 已提交待处理', time: '2026-06-09 15:08', status: '等待处理', readStatus: 0 },
        { group: '资金通知', title: '货款提现 WD20260607004 已到账', time: '2026-06-08 10:30', status: '已完成', readStatus: 1 }
    ],
    operations: [
        { no: 'PR20260610031', type: '补充供货申请', title: '广州中心仓补充供货 18 件', time: '2026-06-10 14:49', status: '已提交待处理', next: '等待平台处理' },
        { no: 'ARR20260610001', type: '到仓凭证', title: 'SH20260610001 到仓凭证提交', time: '2026-06-10 10:26', status: '等待仓库确认', next: '仓库确认后进入入库结果' },
        { no: 'WD20260610008', type: '提现申请', title: '申请提现 ¥1,000.00', time: '2026-06-10 15:20', status: '待审核', next: '等待财务审核' }
    ],
    purchases: [
        {
            no: 'PR20260610031',
            warehouse: '广州中心仓',
            pickup: '06月10日 15:00 到仓',
            totalQty: 18,
            amount: 6820,
            status: '已提交待处理',
            statusClass: 'orange',
            applyTime: '2026-06-10 14:49',
            items: [
                { id: 'blueberry-box', title: '云南蓝莓 125g*24盒', spec: '整箱 / 冷藏 / 24盒', qty: 4, price: '1286.00' },
                { id: 'orange', title: '四川爱媛果冻橙 5斤装', spec: '5斤装 / 单果70-80mm', qty: 14, price: '119.70' }
            ]
        },
        {
            no: 'PR20260609022',
            warehouse: '深圳中心仓',
            pickup: '06月11日 09:00 到仓',
            totalQty: 10,
            amount: 2340,
            status: '待提交到仓',
            statusClass: 'blue',
            applyTime: '2026-06-09 13:20',
            auditTime: '2026-06-09 15:08',
            orderNo: 'SH20260610002',
            items: [
                { id: 'plum-box', title: '新疆西梅干 500g', spec: '500g / 24袋 / 箱', qty: 8, price: '292.50' },
                { id: 'grain', title: '坚果燕麦礼盒', spec: '1.2kg / 礼盒装', qty: 2, price: '0.00' }
            ]
        },
        {
            no: 'PR20260608016',
            warehouse: '广州中心仓',
            pickup: '06月09日 08:30 到仓',
            totalQty: 6,
            amount: 1770,
            status: '待我补充',
            statusClass: 'gray',
            applyTime: '2026-06-08 10:12',
            auditTime: '2026-06-08 11:06',
            rejectReason: '到仓时间与采购排期冲突，请调整到仓日期后重新提交。',
            items: [
                { id: 'croissant', title: '早餐牛角包组合', spec: '6枚装 / 当日烘焙', qty: 6, price: '295.00' }
            ]
        }
    ],
    salesProducts: [
        { id: 'mango', title: '海南贵妃芒 5斤装', spec: '5斤装 / 自然熟果', qty: 126, price: '59.90', status: '在售' },
        { id: 'apple', title: '烟台红富士苹果 6枚', spec: '精选大果 / 6枚装', qty: 98, price: '39.50', status: '在售' },
        { id: 'blueberry-box', title: '云南蓝莓 125g*24盒', spec: '整箱 / 冷藏 / 24盒', qty: 72, price: '1286.00', status: '在售' },
        { id: 'grain', title: '坚果燕麦礼盒', spec: '1.2kg / 礼盒装', qty: 31, price: '56.50', status: '下架待补货' }
    ]
};

const state = reactive({
    roleType: (uni.getStorageSync('mall_role_h5_role') as RoleType) || 'station',
    token: (uni.getStorageSync('mall_role_h5_token') as string) || '',
    account: '',
    lastToast: '',
    profiles: {
        station: stationProfile,
        supplier: supplierProfile
    }
});

export const currentRole = computed<RoleType>(() => state.roleType);
export const currentProfile = computed<RoleProfile>(() => state.profiles[state.roleType]);

export function setRoleType(roleType: RoleType) {
    state.roleType = roleType;
    uni.setStorageSync('mall_role_h5_role', roleType);
}

export function setRoleToken(token: string) {
    state.token = token;
    uni.setStorageSync('mall_role_h5_token', token);
}

export function setRoleAccount(account: string) {
    state.account = account;
    uni.setStorageSync('mall_role_h5_account', account);
}

export function getRoleQuery() {
    const profile = currentProfile.value;
    if (state.roleType === 'supplier') {
        return { roleType: state.roleType, supplierId: profile.subjectId };
    }
    return { roleType: state.roleType, stationId: profile.subjectId, leaderId: profile.leaderId };
}

export function findOrder(no?: string) {
    const orders = currentProfile.value.orders;
    return orders.find((item) => item.no === no) || orders[0];
}

export function findPurchase(no?: string) {
    const purchases = currentProfile.value.purchases;
    return purchases.find((item) => item.no === no) || purchases[0];
}

export function statusClass(status: string) {
    if (/完成|到账|通过|已发送|正常|待结算/.test(status)) {
        return 'green';
    }
    if (/送货|等待|确认|售后/.test(status)) {
        return 'blue';
    }
    if (/拒绝|停用|异常|缺货|失败/.test(status)) {
        return 'gray';
    }
    return 'orange';
}

export function goPage(url: string) {
    const tabPages = ['/pages/workbench/index', '/pages/orders/index', '/pages/store/index', '/pages/finance/index', '/pages/mine/index'];
    if (tabPages.includes(url)) {
        uni.switchTab({ url });
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

export function addOperation(record: OperationRecord) {
    currentProfile.value.operations.unshift(record);
}

export function markMessagesRead(group?: string) {
    currentProfile.value.messages.forEach((item) => {
        if (!group || item.group === group) {
            item.readStatus = 1;
        }
    });
}

export function logoutRole() {
    state.token = '';
    state.account = '';
    uni.removeStorageSync('mall_role_h5_token');
    uni.removeStorageSync('mall_role_h5_account');
}
