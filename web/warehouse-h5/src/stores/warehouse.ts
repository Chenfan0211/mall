import {
    pageDeliveryOrders,
    pageInboundItems,
    pageInboundOrders,
    pageInventories,
    pagePickTasks,
    pagePutawayTasks,
    type DeliveryOrderDTO,
    type InboundItemDTO,
    type InboundOrderDTO,
    type InventoryDTO,
    type PageResult,
    type PickTaskDTO,
    type PutawayTaskDTO
} from '@/api/wms';
import { getToken, setToken } from '@/utils/request';

export type WarehouseRole = 'receiver' | 'buyer' | 'picker' | 'recheck' | 'driver' | 'manager';
export type LegacyWarehouseRole = WarehouseRole | 'loader' | 'supervisor';
export type StatusTone = 'green' | 'blue' | 'amber' | 'red' | 'gray';
export type DashboardSource = 'api' | 'mock';

export type TaskStatus =
    | '待处理'
    | '待接单'
    | '待收货'
    | '收货中'
    | '待确认收货'
    | '已收货'
    | '质检异常'
    | '草稿'
    | '待采购审核'
    | '待送货'
    | '已到仓'
    | '待采购确认'
    | '入库异常'
    | '待拣货'
    | '拣货中'
    | '待记录履约缺货'
    | '已拣货'
    | '待装车'
    | '已装车待出库'
    | '已出库'
    | '待发车'
    | '配送中'
    | '待回仓'
    | '已完成'
    | '已上架'
    | '提交结果待确认'
    | '已关闭';

export interface WarehouseKpi {
    label: string;
    value: string | number;
    target: 'tasks' | 'inventory' | 'profile' | 'manager';
}

export interface WarehouseQuickAction {
    label: string;
    desc: string;
    target: WarehouseActionTarget;
}

export type WarehouseActionTarget =
    | 'tasks'
    | 'inventory'
    | 'receiveArea'
    | 'receiveScan'
    | 'receiveQuality'
    | 'purchaseCreate'
    | 'purchaseReceipt'
    | 'pickWork'
    | 'pickShortage'
    | 'recheckWork'
    | 'loadingReceipt'
    | 'driverDelivery'
    | 'driverReturns'
    | 'driverHandover'
    | 'managerOverview'
    | 'managerPutaway'
    | 'managerDocs'
    | 'managerStockGoods'
    | 'managerInventory';

export interface RoleProfile {
    key: WarehouseRole;
    name: string;
    title: string;
    account: string;
    entity: string;
    warehouse: string;
    badge: string;
    subtitle: string;
    loginNote: string;
    kpis: WarehouseKpi[];
    quicks: WarehouseQuickAction[];
    todos: Array<{ title: string; desc: string; taskId?: string }>;
    permissions: string[];
    dataScope: string[];
}

export interface TaskItem {
    id: string;
    title: string;
    sku: string;
    spec: string;
    batch: string;
    location: string;
    barcode: string;
    planQty: number;
    actualQty: number;
    unit: string;
    temp?: string;
    status: string;
    imageTone: string;
    reason?: string;
    stationDemand?: StationDemand[];
}

export interface StationDemand {
    station: string;
    route: string;
    demand: number;
    picked: number;
    shortage: number;
    marked: number;
}

export interface WarehouseTask {
    id: string;
    role: WarehouseRole;
    title: string;
    type: string;
    sourceNo: string;
    currentNo: string;
    status: TaskStatus;
    statusTone: StatusTone;
    warehouse: string;
    area: string;
    location: string;
    route?: string;
    station?: string;
    due: string;
    date: string;
    priority: string;
    skuCount: number;
    plannedQty: number;
    actualQty: number;
    diffNote: string;
    primaryAction: string;
    secondaryActions: string[];
    items: TaskItem[];
    logs: string[];
    updatedAt: string;
    submitPending?: boolean;
    submitPendingAction?: string;
    receiptNo?: string;
    closedReason?: string;
    assignedTo?: string;
}

export interface InventoryRow {
    id: string;
    title: string;
    sku: string;
    batch: string;
    zone: string;
    location: string;
    inStock: number;
    locked: number;
    available: number;
    frozen: number;
    status: string;
    statusTone: StatusTone;
    imageTone: string;
}

export interface DriverGoods {
    id: string;
    title: string;
    sku: string;
    spec: string;
    expected: number;
    actual: number;
    unit: string;
}

export interface DriverStation {
    id: string;
    index: number;
    name: string;
    address: string;
    contact: string;
    distance: string;
    deliveryQty: number;
    returnQty: number;
    status: '待配送' | '已到达' | '已完成' | '无法送达' | '退货交接中' | '退货已交接';
    statusTone: StatusTone;
    abnormalNote?: string;
    goods: DriverGoods[];
    returns: DriverGoods[];
}

export interface WarehouseDriverOrder {
    id: string;
    routeName: string;
    status: '待发车' | '配送中' | '已完成' | '待回仓' | '已回仓';
    statusTone: StatusTone;
    warehouse: string;
    warehouseAddress: string;
    vehicle: string;
    contact: string;
    deliveryDate: string;
    deadline: string;
    stationCount: number;
    cargoCount: number;
    returnCount: number;
    distance: string;
    outboundNo: string;
    handoverNo: string;
    handoverStatus: string;
    stations: DriverStation[];
    logs: string[];
}

export interface WarehouseReturnOrder {
    id: string;
    routeName: string;
    station: string;
    status: '待返仓' | '待仓库确认' | '已完成';
    statusTone: StatusTone;
    returnQty: number;
    deliveryDate: string;
    contact: string;
    address: string;
    items: DriverGoods[];
}

export interface WarehouseManagerDocument {
    id: string;
    category: 'inbound' | 'outbound' | 'purchase';
    title: string;
    type: string;
    status: string;
    statusTone: StatusTone;
    owner: string;
    warehouse: string;
    sourceNo: string;
    qty: number;
    skuCount: number;
    operator: string;
    date: string;
    actions: string[];
}

export interface WorkMetric {
    label: string;
    value: string | number;
}

export interface ReceiverTodoSummary {
    label: '待接单' | '待扫码' | '待收货';
    value: number;
    desc: string;
    target: 'receiveArea' | 'receiveScan';
}

export interface WarehouseDashboard {
    source: DashboardSource;
    profile: RoleProfile;
    kpis: WarehouseKpi[];
    todos: RoleProfile['todos'];
    tasks: WarehouseTask[];
    driverOrders: WarehouseDriverOrder[];
    returnOrders: WarehouseReturnOrder[];
    operations: string[];
    managerStatusRows: Array<{ label: string; value: number | string }>;
    receiverTodoSummary: ReceiverTodoSummary[];
}

export interface WarehouseInventoryDashboard {
    source: DashboardSource;
    rows: InventoryRow[];
    operations: string[];
}

interface WarehouseSession {
    version?: number;
    loginAt?: number;
    source?: 'api' | 'fallback' | 'demo';
    loginUsername?: string;
    loginPortalCode?: string;
    selectedRole?: WarehouseRole;
}

const ROLE_KEY = 'mall_warehouse_h5_role';
const TOKEN_KEY = 'mall_warehouse_h5_token';
const SESSION_KEY = 'mall_warehouse_h5_session';
const TASK_STATE_KEY = 'mall_warehouse_h5_task_state_v2';
const DRIVER_STATE_KEY = 'mall_warehouse_h5_driver_state_v2';
const OPERATIONS_KEY = 'mall_warehouse_h5_operations';
const API_TASK_CACHE_KEY = 'mall_warehouse_h5_api_task_cache';
const API_DRIVER_CACHE_KEY = 'mall_warehouse_h5_api_driver_cache';
const PENDING_TASK_KEY = 'mall_warehouse_h5_pending_task';
const PENDING_ACTION_KEY = 'mall_warehouse_h5_pending_action';
const PENDING_TARGET_KEY = 'mall_warehouse_h5_pending_target';

const roleMap: Record<string, WarehouseRole> = {
    receiver: 'receiver',
    buyer: 'buyer',
    picker: 'picker',
    recheck: 'recheck',
    loader: 'recheck',
    driver: 'driver',
    manager: 'manager',
    supervisor: 'manager'
};

export const roleProfiles: RoleProfile[] = [
    {
        key: 'receiver',
        name: '收货员',
        title: '收货入库工作台',
        account: 'test_wms_receiver',
        entity: '广州中心仓 / 收货区',
        warehouse: '广州中心仓',
        badge: '收货区',
        subtitle: '采购入库、扫码库位、质检凭证、差异记录和入库回执。',
        loginNote: '广州中心仓 · 收货区',
        kpis: [
            { value: 3, label: '待接单', target: 'tasks' },
            { value: 4, label: '收货中', target: 'tasks' },
            { value: 2, label: '待确认', target: 'tasks' }
        ],
        quicks: [
            { label: '接单', desc: '选择库区后接收任务', target: 'receiveArea' },
            { label: '扫码收货', desc: '扫码库位和商品条码', target: 'receiveScan' },
            { label: '上传质检', desc: '质检结果和批次凭证', target: 'receiveQuality' },
            { label: '查看入库回执', desc: '回看实收和差异', target: 'purchaseReceipt' }
        ],
        todos: [
            { title: '3 单待接单', desc: '华南果业、海南供应链、湛江生鲜待收货员接单。', taskId: 'IN20260610031' },
            { title: '4 单待扫码库位', desc: '已接单任务可直接扫码库位，确认后录入实收数量和差异原因。', taskId: 'IN20260610035' },
            { title: '2 单待确认收货', desc: '质检凭证已上传，等待确认入库回执。', taskId: 'IN20260610032' }
        ],
        permissions: ['接单', '扫码库位', '修改库位', '上传质检', '差异原因', '确认收货', '查看入库回执'],
        dataScope: ['广州中心仓', '收货区', '质检与入库回执']
    },
    {
        key: 'buyer',
        name: '采购员',
        title: '采购协同工作台',
        account: 'test_wms_buyer',
        entity: '广州中心仓 / 采购组',
        warehouse: '广州中心仓',
        badge: '采购协同',
        subtitle: '采购单到仓状态、入库回执、差异数量和待采购确认事项。',
        loginNote: '广州中心仓 · 采购组',
        kpis: [
            { value: 4, label: '待确认', target: 'tasks' },
            { value: 3, label: '到仓中', target: 'tasks' },
            { value: 2, label: '差异单', target: 'tasks' }
        ],
        quicks: [
            { label: '新建采购单', desc: '按商品和供应商补货', target: 'purchaseCreate' },
            { label: '审核采购单', desc: '审核通过或驳回', target: 'tasks' },
            { label: '查看入库回执', desc: '只看入库结果', target: 'purchaseReceipt' },
            { label: '差异确认', desc: '临时到货和数量差异', target: 'tasks' }
        ],
        todos: [
            { title: '4 张采购单待审核', desc: '供应商创建、系统补货和采购员草稿需要处理。', taskId: 'PO20260610021' },
            { title: '3 项到仓跟踪', desc: '只查看预计到仓、实收到仓和入库结果，不展示金额。', taskId: 'PO20260610022' },
            { title: '2 项差异待确认', desc: '规格不符、临时到货和差异数量需采购确认。', taskId: 'PO20260610023' }
        ],
        permissions: ['新建采购单', '提交审核', '审核通过', '驳回', '查看入库回执', '差异确认'],
        dataScope: ['广州中心仓', '采购组', '到仓与差异确认']
    },
    {
        key: 'picker',
        name: '拣货员',
        title: '出库拣货工作台',
        account: 'test_wms_picker',
        entity: '广州中心仓 / A 生鲜区',
        warehouse: '广州中心仓',
        badge: '拣货区',
        subtitle: '系统分配、主管转派、扫描条码和履约缺货记录。',
        loginNote: '广州中心仓 · A 生鲜区',
        kpis: [
            { value: 2, label: '待拣货', target: 'tasks' },
            { value: 2, label: '拣货中', target: 'tasks' },
            { value: 2, label: '履约缺货', target: 'tasks' }
        ],
        quicks: [
            { label: '开始拣货', desc: '处理本人名下任务', target: 'pickWork' },
            { label: '扫描条码', desc: '扫码识别商品条码', target: 'pickWork' },
            { label: '记录履约缺货', desc: '同步自提点明细', target: 'pickShortage' },
            { label: '继续拣货', desc: '回到未完成任务', target: 'pickWork' }
        ],
        todos: [
            { title: '2 单待拣货', desc: '任务由系统分配或主管转派，员工不自由抢单。', taskId: 'PICK20260610081' },
            { title: '2 单正在拣货', desc: '扫描商品条码，可修改数量并保存部分进度。', taskId: 'PICK20260610082' },
            { title: '2 单履约缺货', desc: '缺货只记录履约结果，不走异常处理单。', taskId: 'PICK20260610084' }
        ],
        permissions: ['开始拣货', '扫描条码', '录入实拣数量', '记录履约缺货', '完成拣货'],
        dataScope: ['广州中心仓', 'A 生鲜区', '拣货与履约缺货']
    },
    {
        key: 'recheck',
        name: '装车员',
        title: '装车出库工作台',
        account: 'test_wms_loader',
        entity: '广州中心仓 / D 月台',
        warehouse: '广州中心仓',
        badge: 'D 月台',
        subtitle: '处理装车、装车回执和确认出库。',
        loginNote: '广州中心仓 · D 月台',
        kpis: [
            { value: 3, label: '待装车', target: 'tasks' },
            { value: 2, label: '已装车待出库', target: 'tasks' },
            { value: 2, label: '待确认出库', target: 'tasks' }
        ],
        quicks: [
            { label: '查看装车明细', desc: '按线路汇总商品', target: 'recheckWork' },
            { label: '装车', desc: '整单装车完成', target: 'recheckWork' },
            { label: '查看装车回执', desc: '司机交接依据', target: 'loadingReceipt' },
            { label: '确认出库', desc: '确认后扣减库存', target: 'recheckWork' }
        ],
        todos: [
            { title: '3 单待装车', desc: '拣货完成后形成装车商品明细。', taskId: 'LOAD20260610061' },
            { title: '2 单待确认出库', desc: '装车完成后由装车员确认出库。', taskId: 'LOAD20260610062' },
            { title: '1 单回执待查看', desc: '司机发车前以装车回执为交接依据。', taskId: 'LOAD20260610063' }
        ],
        permissions: ['查看装车明细', '装车', '查看装车回执', '确认出库'],
        dataScope: ['广州中心仓', 'D 月台', '装车与出库确认']
    },
    {
        key: 'driver',
        name: '司机',
        title: '司机配送工作台',
        account: 'test_wms_driver',
        entity: '广州 A 线 / 粤A-K3581',
        warehouse: '广州中心仓',
        badge: '广州A线',
        subtitle: '今日路线、自提点配送状态、退货带回和回仓交接。',
        loginNote: '广州 A 线 · 粤A-K3581',
        kpis: [
            { value: 2, label: '待发车', target: 'tasks' },
            { value: 1, label: '配送中', target: 'tasks' },
            { value: 2, label: '待回仓', target: 'tasks' }
        ],
        quicks: [
            { label: '确认出发', desc: '查看今日线路', target: 'driverDelivery' },
            { label: '到达自提点', desc: '记录到达状态', target: 'driverDelivery' },
            { label: '完成配送', desc: '完成门店交付', target: 'driverDelivery' },
            { label: '发起回仓交接', desc: '退货带回入仓', target: 'driverHandover' }
        ],
        todos: [
            { title: '2 条线路待发车', desc: '已确认出库后司机才能发车。', taskId: 'DLV20260611031' },
            { title: '6 个自提点待配送', desc: '到达后记录完成配送、无法送达或退货带回。', taskId: 'DLV20260611032' },
            { title: '2 单退货待回仓', desc: '退货带回后发起回仓交接。', taskId: 'DLV20260611032' }
        ],
        permissions: ['确认出发', '到达自提点', '完成配送', '无法送达', '退货带回', '回仓交接'],
        dataScope: ['广州 A 线', '自提点配送', '退货回仓']
    },
    {
        key: 'manager',
        name: '仓库主管',
        title: '仓库主管',
        account: 'test_wms_manager',
        entity: '广州中心仓 / 管理组',
        warehouse: '广州中心仓',
        badge: '主管',
        subtitle: '全仓任务、上架、转派、关闭任务、全员记录和库存查询。',
        loginNote: '广州中心仓 · 管理组',
        kpis: [
            { value: 5, label: '待收货', target: 'tasks' },
            { value: 3, label: '待上架', target: 'manager' },
            { value: 4, label: '待拣货', target: 'tasks' },
            { value: 3, label: '待装车', target: 'tasks' },
            { value: 2, label: '待回仓', target: 'tasks' },
            { value: 2, label: '待确认库存', target: 'inventory' }
        ],
        quicks: [
            { label: '任务总览', desc: '全员任务状态', target: 'managerOverview' },
            { label: '确认上架', desc: '填写库位数量', target: 'managerPutaway' },
            { label: '转派任务', desc: '转派给授权员工', target: 'managerOverview' },
            { label: '库存查询', desc: '批次和库位库存', target: 'managerStockGoods' },
            { label: '库存盘点', desc: '新增盘点单', target: 'managerInventory' },
            { label: '单据查询', desc: '入库/出库/采购单', target: 'managerDocs' }
        ],
        todos: [
            { title: '3 个批次待上架', desc: '收货完成后由主管填写库位和上架数量。', taskId: 'PUT20260610091' },
            { title: '4 个任务待转派', desc: '转派、关闭任务必须记录原因和操作人。', taskId: 'PICK20260610081' },
            { title: '2 项库存待确认', desc: '回仓和盘点差异进入待确认库存。', taskId: 'PUT20260610092' }
        ],
        permissions: ['任务总览', '上架任务处理', '填写库位数量', '确认上架', '转派', '关闭任务', '全员记录', '库存查询'],
        dataScope: ['广州中心仓', '全部库区', '任务与库存']
    }
];

export const roleTabs = roleProfiles.map((role) => ({ value: role.key, label: role.name }));

const tasksBase: WarehouseTask[] = [
    task('IN20260610031', 'receiver', '采购入库待接单', '采购入库', 'PUR20260610021', '待接单', 'amber', '华南果业 -> 广州中心仓', 'R-收货暂存区', 'R-01-02', '06月10日 08:30', '冷链优先', '接单', ['扫码收货', '上传质检', '确认收货', '查看入库回执'], [
        item('云南蓝莓 125g*24盒', 'SKU-FR-001', '冷藏整箱 / 24盒', 'B20260610-LM01', 'R-01-02', '693000100031', 45, 0, '箱', '2-6C', 'blue'),
        item('四川爱媛果冻橙 5斤装', 'SKU-FR-002', '5斤装 / 单果70-80mm', 'B20260610-AY02', 'R-01-03', '693000100032', 60, 0, '袋', '常温', 'orange'),
        item('海南金煌芒 4斤装', 'SKU-FR-003', '4斤装 / 6-8个', 'B20260610-MG01', 'R-02-01', '693000100033', 21, 0, '箱', '常温', 'yellow')
    ]),
    task('IN20260610032', 'receiver', '低温乳品待确认收货', '采购入库', 'PUR20260610022', '待确认收货', 'amber', '低温乳品仓 -> 广州中心仓', 'C-冷藏收货口', 'C-R-04', '06月10日 10:15', '冷藏抽检', '确认收货', ['扫码收货', '上传质检', '差异原因', '查看入库回执'], [
        item('希腊酸奶 6杯', 'SKU-DR-018', '冷藏 / 6杯组', 'B20260610-YG18', 'C-R-04', '693000100034', 72, 28, '组', '0-4C', 'teal'),
        item('黄油可颂 4只', 'SKU-BK-009', '烘焙冷藏 / 4只', 'B20260610-BK09', 'C-R-05', '693000100035', 36, 12, '袋', '2-8C', 'amber')
    ]),
    task('IN20260610035', 'receiver', '海南鲜果扫码收货', '供应商直送', 'PUR20260610035', '待收货', 'amber', '海南供应链 -> 广州中心仓', 'R-收货暂存区', 'R-02-06', '06月11日 09:10', '鲜果优先', '扫码收货', ['上传质检', '差异原因', '确认收货'], [
        item('海南贵妃芒 5斤装', 'SKU-FR-015', '自然熟果 / 5斤', 'B20260611-MG15', 'R-02-06', '693000100040', 64, 0, '箱', '常温', 'yellow'),
        item('山东烟台红富士苹果 6枚', 'SKU-FR-011', '精选大果 / 6枚', 'B20260611-AP11', 'R-02-07', '693000100041', 58, 0, '盒', '常温', 'red')
    ]),
    task('IN20260610033', 'receiver', '海鲜质检异常复核', '供应商直送', 'PUR20260610023', '质检异常', 'red', '海鲜供应商 -> 广州中心仓', 'F-冷冻收货口', 'F-R-01', '06月10日 11:40', '冷冻验温', '上传质检', ['差异原因', '确认收货'], [
        item('鲜活基围虾 500g', 'SKU-SF-006', '冷冻保鲜 / 500g', 'B20260610-SF06', 'F-R-01', '693000100036', 80, 0, '盒', '-18C', 'purple'),
        item('黑猪前腿肉 500g', 'SKU-MT-012', '冷鲜分割 / 500g', 'B20260610-MT12', 'F-R-02', '693000100037', 52, 0, '盒', '0-4C', 'rose')
    ]),
    task('PO20260610021', 'buyer', '预售采购单审核', '预售采购单', 'SALE20260609011', '待采购审核', 'amber', '华南果业 / 蓝莓+果冻橙', '采购组', '计划到仓 06月11日 08:30', '06月10日 17:00', '预售补货', '审核通过', ['驳回', '调整数量', '查看入库回执'], [
        item('云南蓝莓 125g*24盒', 'SKU-FR-001', '冷藏整箱 / 24盒', 'PO-B20260611-LM01', '广州中心仓 R-01', 'PO693000300021', 120, 0, '箱', '2-6C', 'blue'),
        item('四川爱媛果冻橙 5斤装', 'SKU-FR-002', '5斤装 / 单果70-80mm', 'PO-B20260611-AY02', '广州中心仓 R-02', 'PO693000300022', 90, 0, '袋', '常温', 'orange')
    ]),
    task('PO20260610022', 'buyer', '低温乳品到仓跟踪', '低库存补货', 'RULE20260610008', '待送货', 'blue', '低温乳品仓 / 酸奶+可颂', '采购组', '计划到仓 06月10日 15:30', '06月10日 12:00', '冷藏优先', '到仓跟踪', ['查看入库回执', '差异确认'], [
        item('希腊酸奶 6杯', 'SKU-DR-018', '冷藏 / 6杯组', 'PO-B20260610-YG18', 'C-冷藏收货口', 'PO693000300023', 86, 40, '组', '0-4C', 'teal')
    ]),
    task('PO20260610023', 'buyer', '海鲜入库异常协同', '供应商直采', 'SUP20260610003', '入库异常', 'red', '海鲜供应商 / 冷冻水产', '采购组', 'F-冷冻收货口', '06月10日 18:00', '质检异常协同', '差异确认', ['查看入库回执', '驳回'], [
        item('鲜活基围虾 500g', 'SKU-SF-006', '冷冻保鲜 / 500g', 'PO-B20260610-SF06', 'F-冷冻收货口', 'PO693000300025', 80, 0, '盒', '-18C', 'purple')
    ]),
    task('PO20260611024', 'buyer', '人工采购单草稿', '人工采购单', 'DRAFT20260611024', '草稿', 'amber', '海南供应链 / 芒果+苹果', '采购组', '计划到仓 06月12日 09:20', '06月11日 11:30', '人工创建', '提交审核', ['调整数量'], [
        item('海南贵妃芒 5斤装', 'SKU-FR-015', '自然熟果 / 5斤', 'PO-B20260612-MG15', '广州中心仓 R-02', 'PO693000300027', 64, 0, '箱', '常温', 'yellow')
    ]),
    task('PICK20260610081', 'picker', '广州 A 线线路拣货', '线路拣货', 'WAVE20260610011', '待拣货', 'amber', '广州 A 线 / 8 个自提点', 'A-生鲜拣货区', 'A-01-03 -> A-03-08', '06月10日 13:30', '系统分配', '开始拣货', ['扫描条码', '记录履约缺货', '完成拣货'], [
        item('云南蓝莓 125g*4盒', 'SKU-FR-001-S', '用户装 / 4盒', 'B20260610-LM01', 'A-01-03', '693000200081', 86, 0, '组', '2-6C', 'blue'),
        item('四川爱媛果冻橙 5斤装', 'SKU-FR-002', '用户订单', 'B20260610-AY02', 'A-02-04', '693000200082', 74, 0, '袋', '常温', 'orange'),
        item('有机蔬菜组合 3斤', 'SKU-VE-004', '净菜组合 / 3斤', 'B20260610-VE04', 'A-03-08', '693000200083', 63, 0, '份', '2-8C', 'green')
    ]),
    task('PICK20260610082', 'picker', '越秀东湖店冷藏拣货', '团点拣货', 'WAVE20260610012', '拣货中', 'blue', '越秀东湖店 / 42 单', 'C-冷藏拣货区', 'C-02-01 -> C-02-09', '06月10日 14:10', '冷藏优先', '继续拣货', ['扫描条码', '记录履约缺货', '完成拣货'], [
        item('希腊酸奶 6杯', 'SKU-DR-018', '冷藏 / 6杯组', 'B20260610-YG18', 'C-02-01', '693000200084', 39, 18, '组', '0-4C', 'teal'),
        item('黄油可颂 4只', 'SKU-BK-009', '烘焙冷藏 / 4只', 'B20260610-BK09', 'C-02-09', '693000200085', 28, 9, '袋', '2-8C', 'amber')
    ]),
    task('PICK20260610084', 'picker', '海珠江南店履约缺货', '冷藏波次拣货', 'WAVE20260610014', '待记录履约缺货', 'amber', '海珠江南店 / 36 单', 'C-冷藏拣货区', 'C-03-02 -> C-03-06', '06月10日 15:20', '履约缺货', '记录履约缺货', ['完成拣货'], [
        item('希腊酸奶 6杯', 'SKU-DR-018', '冷藏 / 6杯组', 'B20260610-YG19', 'C-03-02', '693000200088', 32, 29, '组', '0-4C', 'teal', [
            { station: '海珠江南店', route: '广州 A 线', demand: 12, picked: 10, shortage: 2, marked: 0 },
            { station: '昌岗中路店', route: '广州 A 线', demand: 8, picked: 7, shortage: 1, marked: 0 }
        ]),
        item('黄油可颂 4只', 'SKU-BK-009', '烘焙冷藏 / 4只', 'B20260610-BK10', 'C-03-04', '693000200094', 26, 21, '袋', '2-8C', 'amber', [
            { station: '赤岗社区店', route: '广州 A 线', demand: 7, picked: 5, shortage: 2, marked: 0 },
            { station: '江泰路店', route: '广州 A 线', demand: 10, picked: 9, shortage: 1, marked: 0 }
        ])
    ]),
    task('LOAD20260610061', 'recheck', '广州 A 线待装车', '线路装车', 'OUT20260610061', '待装车', 'amber', '广州 A 线 / 8 个自提点', 'D-装车月台', 'DOCK-D-01', '06月10日 16:30', '晚高峰前出库', '装车', ['查看装车明细', '查看装车回执', '确认出库'], [
        item('广州 A 线商品汇总', 'LINE-GZ-A', '8个自提点 / 9个SKU', 'WAVE20260610011', 'DOCK-D-01', 'LOAD20260610061', 286, 0, '件', '常温+冷藏', 'green')
    ]),
    task('LOAD20260610062', 'recheck', '广州 D 线待确认出库', '线路装车', 'OUT20260610062', '已装车待出库', 'blue', '广州 D 线 / 6 个自提点', 'D-装车月台', 'DOCK-D-02', '06月10日 17:10', '已装车', '确认出库', ['查看装车明细', '查看装车回执'], [
        item('广州 D 线商品汇总', 'LINE-GZ-D', '6个自提点 / 7个SKU', 'WAVE20260610012', 'DOCK-D-02', 'LOAD20260610062', 132, 132, '件', '常温', 'green')
    ]),
    task('LOAD20260610063', 'recheck', '深圳夜配线装车回执', '线路装车', 'OUT20260610063', '已出库', 'green', '深圳南山夜配线 / 5 个自提点', 'D-装车月台', 'DOCK-D-03', '06月10日 18:20', '司机已接收', '查看装车回执', ['查看装车明细'], [
        item('深圳夜配线商品汇总', 'LINE-SZ-N', '5个自提点 / 6个SKU', 'WAVE20260610013', 'DOCK-D-03', 'LOAD20260610063', 118, 118, '件', '常温', 'green')
    ]),
    task('PUT20260610091', 'manager', '冷藏区待上架', '上架任务', 'IN20260610032', '待处理', 'amber', '低温乳品仓 -> 广州中心仓', 'C-冷藏区', 'C-03-02', '06月10日 18:00', '收货完成', '确认上架', ['填写库位数量', '转派任务', '关闭任务'], [
        item('希腊酸奶 6杯', 'SKU-DR-018', '冷藏 / 6杯组', 'B20260610-YG18', 'C-03-02', '693000100034', 72, 28, '组', '0-4C', 'teal'),
        item('黄油可颂 4只', 'SKU-BK-009', '烘焙冷藏 / 4只', 'B20260610-BK09', 'C-03-04', '693000100035', 36, 12, '袋', '2-8C', 'amber')
    ]),
    task('PUT20260610092', 'manager', '退货回仓待确认库存', '回仓确认', 'RET20260610012', '待回仓', 'amber', '广州 A 线退货', 'RETURN-退货区', 'RETURN-01', '06月10日 20:00', '司机回仓', '回仓确认', ['确认上架', '关闭任务'], [
        item('门店退货商品汇总', 'RETURN-GOODS', '退货待质检', 'RET20260610012', 'RETURN-01', 'RET20260610012', 12, 0, '件', '常温', 'red')
    ])
];

export const inventoryRows: InventoryRow[] = [
    inventory('STOCK-001', '云南蓝莓 125g*24盒', 'SKU-FR-001', 'B20260610-LM01', '冷藏区', 'C-01-03', 318, 72, 246, 0, '冷藏充足', 'green', 'blue'),
    inventory('STOCK-002', '希腊酸奶 6杯', 'SKU-DR-018', 'B20260610-YG18', '冷藏区', 'C-02-01', 126, 34, 92, 0, '质检待复核', 'amber', 'teal'),
    inventory('STOCK-003', '鲜活基围虾 500g', 'SKU-SF-006', 'B20260610-SF06', '冷冻区', 'F-01-02', 80, 0, 0, 80, '待确认库存', 'red', 'purple'),
    inventory('STOCK-004', '高原鲜奶 250ml', 'SKU-MILK-250', 'B20260628-C02', '待上架区', 'C-R-02', 64, 0, 0, 0, '待上架', 'amber', 'teal'),
    inventory('STOCK-005', '五常大米 5kg', 'SKU-GR-021', 'B20260609-GR21', '常温区', 'B-03-06', 220, 24, 196, 0, '可售', 'green', 'amber')
];

export const driverOrdersBase: WarehouseDriverOrder[] = [
    {
        id: 'DLV20260611031',
        routeName: '广州 A 线鲜果配送',
        status: '待发车',
        statusTone: 'amber',
        warehouse: '广州中心仓',
        warehouseAddress: '广州市白云区中心仓',
        vehicle: '粤A-K3581',
        contact: '陈志强 138****2310',
        deliveryDate: '2026-06-11',
        deadline: '2026-06-11 22:00:00',
        stationCount: 3,
        cargoCount: 156,
        returnCount: 4,
        distance: '36.2km',
        outboundNo: 'OUT20260611031',
        handoverNo: 'LOAD20260610061',
        handoverStatus: '装车完成',
        logs: ['装车员已确认出库，司机可确认出发'],
        stations: [
            station('ST-A1', 1, '越秀东湖店', '广州市越秀区东湖路 128 号', '王店长 137****6402', '6.2km', 44, 1),
            station('ST-A2', 2, '珠江新城店', '广州市天河区花城大道 88 号', '赵店长 138****0901', '9.8km', 40, 1),
            station('ST-A3', 3, '天河体育西店', '广州市天河区体育西路 101 号', '李店长 138****3210', '13.4km', 28, 1)
        ]
    },
    {
        id: 'DLV20260611032',
        routeName: '广州 D 线常温配送',
        status: '配送中',
        statusTone: 'blue',
        warehouse: '广州中心仓',
        warehouseAddress: '广州市白云区中心仓',
        vehicle: '粤A-M7608',
        contact: '陈志强 138****2310',
        deliveryDate: '2026-06-11',
        deadline: '2026-06-11 22:00:00',
        stationCount: 3,
        cargoCount: 132,
        returnCount: 2,
        distance: '28.5km',
        outboundNo: 'OUT20260611032',
        handoverNo: 'LOAD20260610062',
        handoverStatus: '司机配送中',
        logs: ['司机已发车', '番禺市桥店已完成配送'],
        stations: [
            station('ST-D1', 1, '番禺市桥店', '广州市番禺区市桥街清河路 18 号', '刘店长 138****6670', '12.6km', 52, 0, '已完成', 'green'),
            station('ST-D2', 2, '海珠江南店', '广州市海珠区江南大道 90 号', '陈店长 139****2218', '15.9km', 38, 2, '无法送达', 'red', '自提点暂未营业'),
            station('ST-D3', 3, '江泰路店', '广州市海珠区江泰路 58 号', '唐店长 136****0123', '18.4km', 16, 0, '待配送', 'amber')
        ]
    }
];

export const returnOrdersBase: WarehouseReturnOrder[] = [
    {
        id: 'RET20260612041',
        routeName: '广州 A 线鲜果配送',
        station: '越秀东湖店',
        status: '待返仓',
        statusTone: 'amber',
        returnQty: 5,
        deliveryDate: '2026-06-12',
        contact: '王店长 137****6402',
        address: '广州市越秀区东湖路 128 号',
        items: goodsSet(5, 0)
    },
    {
        id: 'RET20260612042',
        routeName: '广州 D 线常温配送',
        station: '海珠江南店',
        status: '待仓库确认',
        statusTone: 'blue',
        returnQty: 2,
        deliveryDate: '2026-06-12',
        contact: '陈店长 139****2218',
        address: '广州市海珠区江南大道 90 号',
        items: goodsSet(2, 0)
    },
    {
        id: 'RET20260612046',
        routeName: '广州 C 线冷链配送',
        station: '珠江新城店',
        status: '已完成',
        statusTone: 'green',
        returnQty: 1,
        deliveryDate: '2026-06-12',
        contact: '赵店长 138****0901',
        address: '广州市天河区花城大道 88 号',
        items: goodsSet(1, 1)
    }
];

export const managerDocuments: WarehouseManagerDocument[] = [
    doc('IN-MGR-20251204001', 'inbound', '华南果业到仓入库', '采购入库单', '待入库', 'amber', '华南果业', 'PUR20251204001', 168, 3, ['入库']),
    doc('IN-MGR-20251204002', 'inbound', '湘潭总仓调拨入库', '调拨入库单', '待入库', 'amber', '采购组', 'TR20251204002', 160, 2, ['入库']),
    doc('OUT-MGR-20251204001', 'outbound', '广州 A 线出库', '线路出库单', '已出库', 'green', '装车组', 'LOAD20260610061', 286, 9, ['查看']),
    doc('PUR-MGR-20251204001', 'purchase', '低温乳品采购待确认', '采购协同单', '待采购确认', 'blue', '采购组', 'PO20260610022', 86, 2, ['查看'])
];

export const purchaseCatalog: TaskItem[] = [
    item('云南蓝莓 125g*24盒', 'SKU-FR-001', '冷藏整箱 / 24盒', 'PO-B20260611-LM01', '广州中心仓 R-01', '693000310001', 30, 0, '箱', '2-6C', 'blue'),
    item('四川爱媛果冻橙 5斤装', 'SKU-FR-002', '5斤装 / 单果70-80mm', 'PO-B20260611-AY02', '广州中心仓 R-02', '693000310002', 50, 0, '袋', '常温', 'orange'),
    item('海南贵妃芒 5斤装', 'SKU-FR-015', '自然熟果 / 5斤', 'PO-B20260612-MG15', '广州中心仓 R-02', '693000310003', 40, 0, '箱', '常温', 'yellow'),
    item('希腊酸奶 6杯', 'SKU-DR-018', '冷藏 / 6杯组', 'PO-B20260610-YG18', 'C-冷藏收货口', '693000310006', 50, 0, '组', '0-4C', 'teal')
];

export function normalizeRole(role: unknown): WarehouseRole {
    return roleMap[String(role || '')] || 'manager';
}

export function getCurrentRole(): WarehouseRole {
    const saved = normalizeRole(uni.getStorageSync(ROLE_KEY));
    uni.setStorageSync(ROLE_KEY, saved);
    return saved;
}

export function setCurrentRole(role: LegacyWarehouseRole) {
    uni.setStorageSync(ROLE_KEY, normalizeRole(role));
}

export function setDemoToken() {
    uni.setStorageSync(TOKEN_KEY, 'warehouse-demo-token');
    uni.setStorageSync(SESSION_KEY, { version: 2, loginAt: Date.now(), source: 'demo' });
}

export function setWarehouseSession(token: string, meta: Record<string, unknown> = {}) {
    setToken(token);
    uni.setStorageSync(SESSION_KEY, { version: 2, loginAt: Date.now(), source: 'api', ...meta });
}

export function getWarehouseSession(): WarehouseSession {
    const session = uni.getStorageSync(SESSION_KEY) as WarehouseSession;
    return session && typeof session === 'object' ? session : {};
}

export function hasWarehouseSession() {
    const token = uni.getStorageSync(TOKEN_KEY);
    const session = getWarehouseSession();
    return Boolean(token && session && session.loginAt);
}

export function logoutWarehouse() {
    clearWarehouseSession();
}

export function clearWarehouseSession() {
    uni.removeStorageSync(TOKEN_KEY);
    uni.removeStorageSync(ROLE_KEY);
    uni.removeStorageSync(SESSION_KEY);
    uni.removeStorageSync(PENDING_TASK_KEY);
    uni.removeStorageSync(PENDING_ACTION_KEY);
    uni.removeStorageSync(PENDING_TARGET_KEY);
    uni.removeStorageSync(API_TASK_CACHE_KEY);
    uni.removeStorageSync(API_DRIVER_CACHE_KEY);
}

export function getRoleProfile(role: WarehouseRole = getCurrentRole()) {
    return roleProfiles.find((item) => item.key === role) || roleProfiles[0];
}

export function canViewInventory(role: WarehouseRole) {
    return role === 'manager';
}

export function getAllTasks() {
    const states = taskState();
    return [...cloneTasks(), ...apiTaskCache()].map((task) => mergeTaskState(task, states[task.id]));
}

export function getVisibleTasks(role: WarehouseRole = getCurrentRole()) {
    const tasks = sortWarehouseTasks(getAllTasks());
    if (role === 'manager') return tasks;
    return tasks.filter((task) => task.role === role);
}

function getMockVisibleTasks(role: WarehouseRole) {
    const states = taskState();
    const tasks = cloneTasks().map((task) => mergeTaskState(task, states[task.id]));
    if (role === 'manager') return tasks;
    return tasks.filter((task) => task.role === role);
}

export function findTask(id: string) {
    return getAllTasks().find((task) => task.id === id || task.currentNo === id || task.sourceNo === id);
}

export function getFirstTaskForTarget(role: WarehouseRole, target: WarehouseActionTarget | string) {
    const tasks = getVisibleTasks(role);
    if (target === 'receiveArea') return tasks.find((task) => task.status === '待接单') || tasks[0];
    if (target === 'receiveScan' || target === 'receiveQuality') return tasks.find((task) => /待收货|收货中|待确认收货/.test(task.status)) || tasks[0];
    if (target === 'pickWork') return tasks.find((task) => /待拣货|拣货中/.test(task.status)) || tasks[0];
    if (target === 'pickShortage') return tasks.find((task) => /履约缺货/.test(task.status) || getTaskShortageQty(task) > 0) || tasks[0];
    if (target === 'recheckWork' || target === 'loadingReceipt') return tasks.find((task) => /待装车|已装车待出库|已出库/.test(task.status)) || tasks[0];
    if (target === 'managerPutaway') return tasks.find((task) => task.id.startsWith('PUT')) || tasks[0];
    return tasks[0];
}

export function sortWarehouseTasks(tasks: WarehouseTask[]) {
    return [...tasks].sort((a, b) => {
        const aWeight = taskSortWeight(a);
        const bWeight = taskSortWeight(b);
        if (aWeight !== bWeight) return aWeight - bWeight;
        if (a.statusTone !== b.statusTone) return toneSortWeight(a.statusTone) - toneSortWeight(b.statusTone);
        return String(b.updatedAt || b.due || b.date || '').localeCompare(String(a.updatedAt || a.due || a.date || ''));
    });
}

function taskSortWeight(task: WarehouseTask) {
    if (task.submitPending || /提交结果待确认/.test(task.status)) return 0;
    if (task.statusTone === 'red' || /异常|缺货|差异/.test(`${task.status}${task.diffNote}`)) return 1;
    if (/待确认|已装车待出库|待回仓/.test(task.status)) return 2;
    if (/中|收货中|拣货中|配送中/.test(task.status)) return 3;
    if (/待|草稿/.test(task.status)) return 4;
    if (/已完成|已收货|已上架|已拣货|已装车|已出库|已关闭/.test(task.status)) return 8;
    return 5;
}

function toneSortWeight(tone: StatusTone) {
    const map: Record<StatusTone, number> = { red: 0, amber: 1, blue: 2, green: 3, gray: 4 };
    return map[tone] ?? 9;
}

export function setPendingTarget(target: WarehouseActionTarget | string, taskId?: string) {
    uni.setStorageSync(PENDING_TARGET_KEY, target);
    if (taskId) uni.setStorageSync(PENDING_TASK_KEY, taskId);
}

export function consumePendingTarget() {
    const target = uni.getStorageSync(PENDING_TARGET_KEY) as string;
    const taskId = uni.getStorageSync(PENDING_TASK_KEY) as string;
    uni.removeStorageSync(PENDING_TARGET_KEY);
    uni.removeStorageSync(PENDING_TASK_KEY);
    return { target, taskId };
}

export function getRoleMetrics(role: WarehouseRole): WorkMetric[] {
    return designKpis(role, getVisibleTasks(role), getDriverOrders()).map((item) => ({ label: item.label, value: item.value }));
}

export function getOperations() {
    const saved = uni.getStorageSync(OPERATIONS_KEY) as string[];
    if (Array.isArray(saved) && saved.length) return saved;
    return ['09:18 系统生成收货任务', '10:02 波次锁定成功', '10:40 上架任务待处理'];
}

export function addOperation(text: string) {
    const next = [`${formatTime()} ${text}`, ...getOperations()].slice(0, 16);
    uni.setStorageSync(OPERATIONS_KEY, next);
}

export async function loadWarehouseDashboard(role: WarehouseRole = getCurrentRole()): Promise<WarehouseDashboard> {
    const mock = buildMockDashboard(role);
    if (!shouldUseWarehouseApi()) return mock;

    try {
        const data = await loadDashboardFromApi(role, mock);
        return data || mock;
    } catch (error) {
        console.debug('[warehouse-h5] WMS接口降级演示数据', error);
        return mock;
    }
}

export async function loadWarehouseInventory(): Promise<WarehouseInventoryDashboard> {
    if (!shouldUseWarehouseApi()) {
        return { source: 'mock', rows: inventoryRows, operations: getOperations().slice(0, 6) };
    }
    try {
        const stocks = await pageInventories({ pageNum: 1, pageSize: 30 }, { silent: true });
        if (!pageHasRows(stocks)) {
            return { source: 'mock', rows: inventoryRows, operations: getOperations().slice(0, 6) };
        }
        const rows = stocks.list.map((row, index) => inventoryFromApi(row, index));
        return {
            source: 'api',
            rows,
            operations: apiOperations('库存批次', stocks.total || rows.length, rows.map((row) => row.batch))
        };
    } catch (error) {
        console.debug('[warehouse-h5] 库存接口降级演示数据', error);
        return { source: 'mock', rows: inventoryRows, operations: getOperations().slice(0, 6) };
    }
}

function shouldUseWarehouseApi() {
    const token = getToken();
    return Boolean(token && token !== 'warehouse-demo-token');
}

function buildMockDashboard(role: WarehouseRole): WarehouseDashboard {
    const profile = getRoleProfile(role);
    const tasks = getMockVisibleTasks(role);
    const driverOrders = driverOrdersBase.map((order) => ({ ...order, stations: order.stations.map((stationRow) => ({ ...stationRow })), logs: [...order.logs] }));
    const returnOrders = getReturnOrders();
    return {
        source: 'mock',
        profile,
        kpis: designKpis(role, tasks, driverOrders, returnOrders),
        todos: buildTodosFromTasks(role, tasks, profile.todos),
        tasks,
        driverOrders,
        returnOrders,
        operations: getOperations().slice(0, 6),
        managerStatusRows: buildManagerStatusRowsFromTasks(getMockVisibleTasks('manager'), inventoryRows.length),
        receiverTodoSummary: buildReceiverTodoSummary(tasks)
    };
}

async function loadDashboardFromApi(role: WarehouseRole, mock: WarehouseDashboard): Promise<WarehouseDashboard | null> {
    const query = { pageNum: 1, pageSize: 10 };
    const options = { silent: true };

    if (role === 'receiver') {
        const inbound = await loadAllInboundOrders(options);
        if (!pageHasRows(inbound)) return null;
        const inboundItemsResult = await loadReceiverInboundItems(inbound.list, options);
        if (!inboundItemsResult.ok) return null;
        const receiverItems = inboundItemsResult.itemsMap;
        const inboundTotal = pageTotal(inbound);
        const tasks = sortWarehouseTasks(inbound.list.map((row, index) => inboundToTask(row, index, receiverItems.get(row.id) || [])));
        rememberApiTasks(tasks);
        return {
            ...mock,
            source: 'api',
            kpis: buildReceiverKpis(inboundTotal, inbound.list, receiverItems),
            todos: buildTodosFromTasks(role, tasks, mock.profile.todos),
            tasks,
            operations: apiOperations('入库单', inboundTotal, tasks.map((item) => item.currentNo)),
            receiverTodoSummary: buildReceiverTodoSummary(tasks)
        };
    }

    if (role === 'buyer') {
        const inbound = await pageInboundOrders(query, options);
        if (!pageHasRows(inbound)) return null;
        const tasks = sortWarehouseTasks(inbound.list.map((row, index) => inboundToBuyerTask(row, index)));
        rememberApiTasks(tasks);
        return {
            ...mock,
            source: 'api',
            kpis: [
                { value: tasks.filter((row) => /待送货|到仓|待采购确认|待确认/.test(row.status)).length || inbound.total || tasks.length, label: '到仓跟踪', target: 'tasks' },
                { value: tasks.filter((row) => /已到仓|已收货|待采购确认/.test(row.status)).length, label: '入库回执', target: 'tasks' },
                { value: countRiskTasks(tasks), label: '差异确认', target: 'tasks' }
            ],
            todos: buildTodosFromTasks(role, tasks, mock.profile.todos),
            tasks,
            operations: apiOperations('采购到仓', inbound.total || tasks.length, tasks.map((item) => item.currentNo))
        };
    }

    if (role === 'picker') {
        const picks = await pagePickTasks(query, options);
        if (!pageHasRows(picks)) return null;
        const tasks = sortWarehouseTasks(picks.list.map((row, index) => pickToTask(row, index)));
        rememberApiTasks(tasks);
        return {
            ...mock,
            source: 'api',
            kpis: [
                { value: picks.total || tasks.length, label: '拣货任务', target: 'tasks' },
                { value: sumTaskPlannedQty(tasks), label: '应拣件数', target: 'tasks' },
                { value: tasks.reduce((sum, item) => sum + Math.max(0, item.plannedQty - item.actualQty), 0), label: '缺货差异', target: 'tasks' }
            ],
            todos: buildTodosFromTasks(role, tasks, mock.profile.todos),
            tasks,
            operations: apiOperations('拣货任务', picks.total || tasks.length, tasks.map((item) => item.currentNo))
        };
    }

    if (role === 'recheck') {
        const deliveries = await pageDeliveryOrders(query, options);
        if (!pageHasRows(deliveries)) return null;
        const tasks = sortWarehouseTasks(deliveries.list.map((row, index) => deliveryToTask(row, index)));
        rememberApiTasks(tasks);
        return {
            ...mock,
            source: 'api',
            kpis: [
                { value: deliveries.total || tasks.length, label: '待装车单', target: 'tasks' },
                { value: sumTaskPlannedQty(tasks), label: '待装数量', target: 'tasks' },
                { value: tasks.filter((row) => /待确认出库|已装车待出库/.test(row.status)).length, label: '待确认出库', target: 'tasks' }
            ],
            todos: buildTodosFromTasks(role, tasks, mock.profile.todos),
            tasks,
            operations: apiOperations('装车单', deliveries.total || tasks.length, tasks.map((item) => item.currentNo))
        };
    }

    if (role === 'driver') {
        const deliveries = await pageDeliveryOrders(query, options);
        if (!pageHasRows(deliveries)) return null;
        const driverOrders = deliveries.list.map((row, index) => deliveryToDriverOrder(row, index, mock.driverOrders[index]));
        rememberApiDriverOrders(driverOrders);
        return {
            ...mock,
            source: 'api',
            kpis: [
                { value: deliveries.total || driverOrders.length, label: '今日线路', target: 'tasks' },
                { value: driverOrders.reduce((sum, item) => sum + item.cargoCount, 0), label: '配送件数', target: 'tasks' },
                { value: mock.returnOrders.reduce((sum, item) => sum + item.returnQty, 0), label: '待取退货', target: 'tasks' }
            ],
            todos: buildDriverTodos(driverOrders, mock.returnOrders),
            driverOrders,
            operations: apiOperations('配送单', deliveries.total || driverOrders.length, driverOrders.map((item) => item.id))
        };
    }

    if (role === 'manager') {
        const [inbound, putaway, picks, deliveries, stocks] = await Promise.all([
            pageInboundOrders(query, options),
            pagePutawayTasks(query, options),
            pagePickTasks(query, options),
            pageDeliveryOrders(query, options),
            pageInventories(query, options)
        ]);
        if (![inbound, putaway, picks, deliveries, stocks].some(pageHasRows)) return null;
        const inboundTasks = inbound.list.map((row, index) => inboundToTask(row, index));
        const putawayTasks = putaway.list.map((row, index) => putawayToTask(row, index));
        const pickTasks = picks.list.map((row, index) => pickToTask(row, index));
        const deliveryTasks = deliveries.list.map((row, index) => deliveryToTask(row, index));
        const tasks = sortWarehouseTasks([...inboundTasks, ...putawayTasks, ...pickTasks, ...deliveryTasks]);
        rememberApiTasks(tasks);
        return {
            ...mock,
            source: 'api',
            kpis: [
                { value: inbound.total || inboundTasks.length, label: '入库单', target: 'tasks' },
                { value: deliveries.total || deliveryTasks.length, label: '出库单', target: 'tasks' },
                { value: mock.profile.kpis[2]?.value || 0, label: '采购待处理', target: 'tasks' }
            ],
            todos: buildTodosFromTasks(role, tasks, mock.profile.todos),
            tasks,
            managerStatusRows: [
                { label: '待收货', value: inbound.total || inboundTasks.length },
                { label: '待上架', value: putaway.total || putawayTasks.length },
                { label: '待拣货', value: picks.total || pickTasks.length },
                { label: '待装车', value: deliveries.total || deliveryTasks.length },
                { label: '待回仓', value: mock.returnOrders.length },
                { label: '待确认库存', value: stocks.list.filter((row) => Number(row.frozenQty || 0) > 0 || Number(row.availableQty || 0) <= 0).length },
                { label: '已完成', value: tasks.filter((item) => /已/.test(item.status)).length }
            ],
            operations: apiOperations('主管汇总', tasks.length, tasks.map((item) => item.currentNo))
        };
    }

    return null;
}

function pageHasRows(page: { total?: number; list?: unknown[] }) {
    return Boolean((Number(page.total) || 0) > 0 || (Array.isArray(page.list) && page.list.length > 0));
}

function pageTotal(page: { total?: number; list?: unknown[] }) {
    const total = Number(page.total);
    return Number.isFinite(total) ? total : (page.list || []).length;
}

async function loadAllInboundOrders(options: { silent: boolean }) {
    return loadAllPages((pageNum, pageSize) => pageInboundOrders({ pageNum, pageSize }, options));
}

async function loadReceiverInboundItems(inboundOrders: InboundOrderDTO[], options: { silent: boolean }) {
    const allItemsResult = await loadAllInboundItems(options);
    if (allItemsResult.ok) {
        return {
            itemsMap: groupInboundItemsByOrder(allItemsResult.items, inboundOrders),
            ok: true
        };
    }

    const inboundItemsResult = await loadInboundItemsForOrders(inboundOrders, options);
    if (inboundItemsResult.failedCount > 0) {
        return { itemsMap: inboundItemsResult.itemsMap, ok: false };
    }
    return { itemsMap: inboundItemsResult.itemsMap, ok: true };
}

async function loadInboundItemsForOrders(inboundOrders: InboundOrderDTO[], options: { silent: boolean }) {
    const entries = await Promise.all(inboundOrders.map(async (order) => {
        try {
            const page = await pageInboundItems({ pageNum: 1, pageSize: 200, inboundId: order.id }, options);
            return { orderId: order.id, list: page.list || [], ok: true };
        } catch (error) {
            console.debug('[warehouse-h5] 入库明细接口降级估算数量', order.inboundNo || order.id, error);
            return { orderId: order.id, list: [] as InboundItemDTO[], ok: false };
        }
    }));
    return {
        itemsMap: new Map<number, InboundItemDTO[]>(entries.map((entry) => [entry.orderId, entry.list])),
        failedCount: entries.filter((entry) => !entry.ok).length
    };
}

async function loadAllInboundItems(options: { silent: boolean }) {
    try {
        const page = await loadAllPages((pageNum, pageSize) => pageInboundItems({ pageNum, pageSize }, options));
        return { items: page.list || [], ok: true };
    } catch (error) {
        console.debug('[warehouse-h5] 入库明细全量接口降级', error);
        return { items: [] as InboundItemDTO[], ok: false };
    }
}

async function loadAllPages<T>(loader: (pageNum: number, pageSize: number) => Promise<PageResult<T>>): Promise<PageResult<T>> {
    const pageSize = 200;
    const firstPage = await loader(1, pageSize);
    const firstList = firstPage.list || [];
    const total = pageTotal(firstPage);
    const pageCount = Math.ceil(total / pageSize);
    if (pageCount <= 1) {
        return { total, list: firstList };
    }

    const rest: T[] = [];
    for (let pageNum = 2; pageNum <= pageCount; pageNum += 1) {
        const page = await loader(pageNum, pageSize);
        rest.push(...(page.list || []));
    }
    return { total, list: [...firstList, ...rest] };
}

function groupInboundItemsByOrder(allItems: InboundItemDTO[], orders: InboundOrderDTO[]) {
    const itemsMap = new Map<number, InboundItemDTO[]>();
    const orderIds = new Set(orders.map((order) => order.id));
    allItems.forEach((item) => {
        if (!orderIds.has(item.inboundId)) return;
        const current = itemsMap.get(item.inboundId) || [];
        if (!current.some((row) => row.id === item.id)) {
            itemsMap.set(item.inboundId, [...current, item]);
        }
    });
    return itemsMap;
}

function buildReceiverKpis(total: number, orders: InboundOrderDTO[], itemsMap: Map<number, InboundItemDTO[]>): WarehouseKpi[] {
    const detailQty = Array.from(itemsMap.values()).flat().reduce((sum, row) => sum + Number(row.expectedQty || 0), 0);
    return [
        { value: total, label: '收货单据', target: 'tasks' },
        { value: detailQty, label: '商品件数', target: 'tasks' },
        { value: countReceiverRiskOrders(orders, itemsMap), label: '异常单', target: 'tasks' }
    ];
}

function countReceiverRiskOrders(orders: InboundOrderDTO[], itemsMap: Map<number, InboundItemDTO[]>) {
    return orders.filter((order) => {
        const status = inboundStatus(order.status);
        if (Number(order.status || 0) >= 50) return true;
        const rows = itemsMap.get(order.id) || [];
        if (/异常|质检/.test(status)) return true;
        if (rows.some((row) => Number(row.rejectedQty || 0) + Number(row.damagedQty || 0) > 0)) return true;
        if (/收货中|待确认收货/.test(status) && rows.some((row) => Number(row.expectedQty || 0) > Number(row.receivedQty || 0))) return true;
        return false;
    }).length;
}

function designKpis(role: WarehouseRole, tasks: WarehouseTask[], driverOrders: WarehouseDriverOrder[] = getDriverOrders(), returnOrders: WarehouseReturnOrder[] = getReturnOrders()): WarehouseKpi[] {
    if (role === 'receiver') {
        return [
            { value: tasks.length, label: '收货单据', target: 'tasks' },
            { value: sumTaskPlannedQty(tasks), label: '商品件数', target: 'tasks' },
            { value: countRiskTasks(tasks), label: '异常单', target: 'tasks' }
        ];
    }
    if (role === 'buyer') {
        return [
            { value: tasks.filter((row) => row.status === '待采购审核').length, label: '待采购审核', target: 'tasks' },
            { value: tasks.filter((row) => row.status === '草稿').length, label: '采购草稿', target: 'tasks' },
            { value: tasks.filter((row) => /驳回|关闭/.test(row.status + row.diffNote)).length || 2, label: '已驳回/已关闭', target: 'tasks' }
        ];
    }
    if (role === 'picker') {
        return [
            { value: tasks.length, label: '拣货任务', target: 'tasks' },
            { value: sumTaskPlannedQty(tasks), label: '应拣件数', target: 'tasks' },
            { value: tasks.reduce((sum, row) => sum + Math.max(0, row.plannedQty - row.actualQty), 0), label: '缺货差异', target: 'tasks' }
        ];
    }
    if (role === 'recheck') {
        return [
            { value: tasks.length, label: '待装车单', target: 'tasks' },
            { value: sumTaskPlannedQty(tasks), label: '待装数量', target: 'tasks' },
            { value: tasks.filter((row) => /待确认出库|已装车待出库/.test(row.status)).length, label: '待确认出库', target: 'tasks' }
        ];
    }
    if (role === 'driver') {
        return [
            { value: driverOrders.length, label: '今日线路', target: 'tasks' },
            { value: driverOrders.reduce((sum, row) => sum + row.cargoCount, 0), label: '配送件数', target: 'tasks' },
            { value: returnOrders.reduce((sum, row) => sum + row.returnQty, 0), label: '待取退货', target: 'tasks' }
        ];
    }
    return [
        { value: tasks.filter((row) => row.role === 'receiver' || row.type.includes('入库')).length, label: '入库单', target: 'tasks' },
        { value: tasks.filter((row) => row.role === 'picker' || row.role === 'recheck' || row.type.includes('出库')).length, label: '出库单', target: 'tasks' },
        { value: getVisibleTasks('buyer').filter((row) => /待|草稿|确认|审核/.test(row.status)).length, label: '采购待处理', target: 'tasks' }
    ];
}

function buildTodosFromTasks(role: WarehouseRole, tasks: WarehouseTask[], fallback: RoleProfile['todos']) {
    const visible = sortWarehouseTasks(tasks).filter((row) => !/已关闭|已完成|已收货|已上架|已拣货|已出库/.test(row.status)).slice(0, 3);
    if (!visible.length) return fallback;
    return visible.map((row) => ({
        title: `${row.currentNo} · ${row.status}`,
        desc: `${row.warehouse} / ${row.route || row.location}，${row.skuCount} SKU，${row.diffNote}`,
        taskId: row.id
    }));
}

function buildReceiverTodoSummary(tasks: WarehouseTask[]): ReceiverTodoSummary[] {
    return [
        {
            label: '待接单',
            value: tasks.filter((row) => row.status === '待接单').length,
            desc: '等待收货员接单',
            target: 'receiveArea'
        },
        {
            label: '待扫码',
            value: tasks.filter((row) => row.status === '待收货').length,
            desc: '待扫码库位/商品',
            target: 'receiveScan'
        },
        {
            label: '待收货',
            value: tasks.filter((row) => /收货中|待确认收货/.test(row.status)).length,
            desc: '收货中待确认',
            target: 'receiveScan'
        }
    ];
}

function buildDriverTodos(driverOrders: WarehouseDriverOrder[], returnOrders: WarehouseReturnOrder[]) {
    const orders = driverOrders.slice(0, 2).map((row) => ({
        title: `${row.id} · ${row.status}`,
        desc: `${row.routeName}，${row.stationCount} 个自提点，${row.cargoCount} 件待配送`,
        taskId: row.id
    }));
    const returns = returnOrders.length
        ? [{
            title: `${returnOrders.length} 单退货待回仓`,
            desc: `退货 ${returnOrders.reduce((sum, row) => sum + row.returnQty, 0)} 件，完成配送后发起回仓交接`,
            taskId: returnOrders[0].id
        }]
        : [];
    return [...orders, ...returns].slice(0, 3);
}

function buildManagerStatusRowsFromTasks(tasks: WarehouseTask[], stockCount = 0) {
    return [
        { label: '待收货', value: tasks.filter((row) => row.role === 'receiver' && /待接单|待收货|收货中|待确认收货/.test(row.status)).length },
        { label: '待上架', value: tasks.filter((row) => row.role === 'manager' && /上架|待处理/.test(row.type + row.status)).length },
        { label: '待拣货', value: tasks.filter((row) => row.role === 'picker' && /待拣货|拣货中/.test(row.status)).length },
        { label: '待装车', value: tasks.filter((row) => row.role === 'recheck' && /待装车|待确认出库|已装车待出库/.test(row.status)).length },
        { label: '待回仓', value: tasks.filter((row) => /回仓|退货|待回仓/.test(row.type + row.status)).length },
        { label: '待确认库存', value: stockCount || inventoryRows.filter((row) => /待确认|冻结|待上架/.test(row.status)).length },
        { label: '已完成', value: tasks.filter((row) => /已完成|已上架|已出库|已回仓|已收货|已拣货/.test(row.status)).length }
    ];
}

function sumTaskPlannedQty(tasks: WarehouseTask[]) {
    return tasks.reduce((sum, row) => sum + Number(row.plannedQty || 0), 0);
}

function countRiskTasks(tasks: WarehouseTask[]) {
    return tasks.filter((row) => row.statusTone === 'red' || /差异|异常|缺货|待确认/.test(row.diffNote + row.status)).length;
}

function apiOperations(label: string, count: number, ids: string[]) {
    const head = ids.slice(0, 3).map((id) => `${formatTime()} 已同步${label} ${id}`);
    return [`${formatTime()} 后端接口返回 ${count} 条${label}`, ...head, ...getOperations()].slice(0, 6);
}

function inboundToTask(row: InboundOrderDTO, index: number, inboundItems: InboundItemDTO[] = []): WarehouseTask {
    const status = inboundStatus(row.status);
    const primaryAction = status === '待接单'
        ? '接单'
        : status === '待确认收货'
            ? '确认收货'
            : status === '已收货'
                ? '查看入库回执'
                : '扫码收货';
    const secondaryActions = status === '已收货'
        ? ['查看入库回执']
        : ['扫码收货', '上传质检', '差异原因', '查看入库回执'];
    const detailItems = inboundItems.length
        ? inboundItems.map((detail, detailIndex) => item(
            `入库商品 ${detail.skuId || row.id}`,
            `SKU-IN-${detail.skuId || row.id}`,
            detail.diffReason || '后端入库明细',
            detail.batchNo || `B${row.id}-${detailIndex + 1}`,
            `R-0${(index % 4) + 1}-0${(detailIndex % 6) + 1}`,
            `69${String(detail.skuId || detail.id || row.id).padStart(10, '0')}`,
            Number(detail.expectedQty || 0),
            Number(detail.receivedQty || 0),
            '件',
            '常温',
            detailIndex % 2 === 0 ? 'teal' : 'green'
        ))
        : [
            item(`入库商品 ${row.id}`, `SKU-IN-${row.id}`, '后端列表字段补齐 / 待扫码', `B${row.id}`, `R-0${(index % 4) + 1}-0${(index % 6) + 1}`, `69${String(row.id).padStart(10, '0')}`, estimateQty(index, 48), status === '待确认收货' ? estimateQty(index, 36) : 0, '件', '常温', 'teal')
        ];
    return task(
        row.inboundNo || `IN-API-${row.id}`,
        'receiver',
        status === '待接单' ? '采购入库待接单' : '采购入库收货任务',
        '采购入库',
        row.sourceNo || `PUR-API-${row.id}`,
        status,
        statusTone(status),
        `供应商 ${row.supplierId || '-'} -> 广州中心仓`,
        'R-收货暂存区',
        `R-0${(index % 4) + 1}-0${(index % 6) + 1}`,
        '今日 18:00',
        index === 0 ? '冷链优先' : '按到仓顺序',
        primaryAction,
        secondaryActions,
        detailItems
    );
}

function inboundToBuyerTask(row: InboundOrderDTO, index: number): WarehouseTask {
    const plannedQty = estimateQty(index, 48);
    const hasArrived = Number(row.status || 0) >= 30;
    const actualQty = hasArrived ? Math.max(0, plannedQty - (index % 3 === 0 ? 12 : 0)) : 0;
    const hasDiff = plannedQty !== actualQty && actualQty > 0;
    const status = buyerInboundStatus(row.status, hasDiff);
    const primaryAction = status === '待采购确认'
        ? '差异确认'
        : /已到仓|已收货/.test(status)
            ? '查看入库回执'
            : '到仓跟踪';
    const secondaryActions = status === '待采购确认'
        ? ['查看入库回执', '差异确认']
        : ['到仓跟踪', '查看入库回执'];
    const taskRow = task(
        `PO-API-${row.id}`,
        'buyer',
        hasDiff ? '采购到仓差异确认' : '采购到仓跟踪',
        '采购到仓',
        row.sourceNo || row.inboundNo || `PUR-API-${row.id}`,
        status,
        statusTone(hasDiff ? '待采购确认' : status),
        `供应商 ${row.supplierId || '-'} / 广州中心仓`,
        '采购组',
        row.inboundNo || `IN-API-${row.id}`,
        '今日 18:00',
        hasDiff ? '差异待采购确认' : '只看入库结果',
        primaryAction,
        secondaryActions,
        [
            item(`采购到仓商品 ${row.id}`, `SKU-BUY-${row.id}`, '采购协同 / 价格信息不在 H5 展示', `PO-B${row.id}`, row.inboundNo || `IN-API-${row.id}`, `65${String(row.id).padStart(10, '0')}`, plannedQty, actualQty, '件', '常温', hasDiff ? 'rose' : 'teal')
        ]
    );
    return {
        ...taskRow,
        diffNote: hasDiff ? `计划 ${plannedQty}，实收到仓 ${actualQty}，差异 ${plannedQty - actualQty}，待采购确认` : `到仓/入库进度已同步，计划 ${plannedQty}，价格信息不在 H5 展示`
    };
}

function pickToTask(row: PickTaskDTO, index: number): WarehouseTask {
    const status = pickStatus(row.status);
    const plannedQty = Number(row.requiredQty || 0) || estimateQty(index, 64);
    const shortageQty = Number(row.shortageQty || 0);
    const actualQty = Number(row.actualQty || 0) || Math.max(0, plannedQty - shortageQty);
    const primaryAction = status === '已拣货' ? '查看记录' : status === '待拣货' ? '开始拣货' : '完成拣货';
    const secondaryActions = status === '已拣货' ? ['查看拣货记录'] : ['扫描条码', '记录履约缺货', '保存进度'];
    return task(
        row.pickNo || `PICK-API-${row.id}`,
        'picker',
        '社区团购拣货任务',
        '出库拣货',
        `OUT-API-${row.id}`,
        status,
        statusTone(shortageQty > 0 ? '履约缺货' : status),
        `线路 ${row.lineId || '-'}`,
        'A-生鲜拣货区',
        `A-0${(index % 5) + 1}-0${(index % 6) + 1}`,
        '今日 20:00',
        shortageQty > 0 ? '缺货优先处理' : '按波次拣货',
        primaryAction,
        secondaryActions,
        [
            item(`拣货商品 ${row.id}`, `SKU-PICK-${row.id}`, '按自提点汇总', `B-PICK-${row.id}`, `A-0${(index % 5) + 1}-0${(index % 6) + 1}`, `68${String(row.id).padStart(10, '0')}`, plannedQty, actualQty, '件', '常温', 'green')
        ]
    );
}

function putawayToTask(row: PutawayTaskDTO, index: number): WarehouseTask {
    const status = row.status >= 30 ? '已上架' : '待处理';
    const primaryAction = status === '已上架' ? '查看记录' : '确认上架';
    const secondaryActions = status === '已上架' ? ['查看上架记录'] : ['填写库位数量', '转派任务', '关闭任务'];
    return task(
        row.putawayNo || `PUT-API-${row.id}`,
        'manager',
        '收货完成待上架',
        '上架任务',
        `IN-API-${row.inboundId || row.id}`,
        status,
        statusTone(status),
        '广州中心仓',
        '待上架区',
        `C-0${(index % 3) + 1}-0${(index % 6) + 1}`,
        '今日 19:30',
        '待主管确认',
        primaryAction,
        secondaryActions,
        [
            item(`待上架商品 ${row.skuId || row.id}`, `SKU-PUT-${row.skuId || row.id}`, '批量上架', `B-PUT-${row.id}`, `C-0${(index % 3) + 1}-0${(index % 6) + 1}`, `67${String(row.id).padStart(10, '0')}`, Number(row.putawayQty || 0) || estimateQty(index, 32), Number(row.actualQty || 0), '件', '常温', 'amber')
        ]
    );
}

function deliveryToTask(row: DeliveryOrderDTO, index: number): WarehouseTask {
    const status = deliveryStatus(row.status);
    const taskStatus = status === '待发车' ? '待装车' : status === '配送中' ? '已出库' : '已完成';
    const primaryAction = taskStatus === '待装车' ? '装车' : '查看装车回执';
    const secondaryActions = taskStatus === '待装车' ? ['查看装车明细', '确认出库'] : ['查看装车明细', '查看装车回执'];
    return task(
        row.deliveryNo || `DLV-API-${row.id}`,
        'recheck',
        '配送出库装车任务',
        '出库配送',
        `OUT-API-${row.id}`,
        taskStatus,
        statusTone(status),
        `线路 ${row.lineId || '-'}`,
        'D-月台',
        `D-0${(index % 3) + 1}`,
        row.deliveryDate || '今日',
        '按线路装车',
        primaryAction,
        secondaryActions,
        [
            item(`配送商品 ${row.id}`, `SKU-DLV-${row.id}`, '线路汇总', `B-DLV-${row.id}`, `D-0${(index % 3) + 1}`, `66${String(row.id).padStart(10, '0')}`, estimateQty(index, 72), status === '待发车' ? 0 : estimateQty(index, 72), '件', '常温', 'blue')
        ]
    );
}

function deliveryToDriverOrder(row: DeliveryOrderDTO, index: number, fallback?: WarehouseDriverOrder): WarehouseDriverOrder {
    const status = deliveryStatus(row.status);
    const cargoCount = fallback?.cargoCount || estimateQty(index, 96);
    return {
        ...(fallback || driverOrdersBase[index % driverOrdersBase.length]),
        id: row.deliveryNo || `DLV-API-${row.id}`,
        routeName: `线路 ${row.lineId || index + 1} 配送单`,
        status,
        statusTone: statusTone(status),
        deliveryDate: row.deliveryDate || fallback?.deliveryDate || '2026-06-29',
        cargoCount,
        stationCount: fallback?.stationCount || 3,
        returnCount: fallback?.returnCount || 0,
        outboundNo: `OUT-API-${row.id}`,
        handoverNo: `LOAD-API-${row.id}`,
        handoverStatus: status === '待发车' ? '装车完成' : status,
        logs: [`${formatTime()} 后端接口同步配送单 ${row.deliveryNo || row.id}`, ...(fallback?.logs || [])].slice(0, 10)
    };
}

function inventoryFromApi(row: InventoryDTO, index: number): InventoryRow {
    const status = inventoryStatus(row);
    return inventory(
        String(row.id || `INV-API-${index}`),
        `库存商品 ${row.skuId || index + 1}`,
        `SKU-${row.skuId || row.id}`,
        row.batchNo || `BATCH-${row.id || index + 1}`,
        `库区 ${row.zoneId || '-'}`,
        `库位 ${row.locationId || '-'}`,
        Number(row.inStockQty || 0),
        Number(row.lockedQty || 0),
        Number(row.availableQty || 0),
        Number(row.frozenQty || 0),
        status,
        statusTone(status),
        index % 2 === 0 ? 'green' : 'blue'
    );
}

function inventoryStatus(row: InventoryDTO) {
    if (Number(row.frozenQty || 0) > 0) return '待确认库存';
    if (Number(row.availableQty || 0) <= 0 && Number(row.inStockQty || 0) > 0) return '待上架';
    if (Number(row.lockedQty || 0) > 0) return '已锁定';
    return '可售';
}

function inboundStatus(status?: number): TaskStatus {
    if (status === 30) return '待确认收货';
    if (status && status >= 40) return '已收货';
    if (status && status >= 20) return '收货中';
    return '待接单';
}

function buyerInboundStatus(status?: number, hasDiff = false): TaskStatus {
    if (hasDiff) return '待采购确认';
    if (status && status >= 30) return '已到仓';
    if (status && status >= 20) return '待送货';
    return '待送货';
}

function pickStatus(status?: number): TaskStatus {
    if (status && status >= 40) return '已拣货';
    if (status && status >= 20) return '拣货中';
    return '待拣货';
}

function deliveryStatus(status?: number): WarehouseDriverOrder['status'] {
    if (status && status >= 50) return '已完成';
    if (status && status >= 40) return '配送中';
    if (status && status >= 30) return '待回仓';
    return '待发车';
}

function estimateQty(index: number, base: number) {
    return base + index * 8;
}

export function applyTaskAction(task: WarehouseTask, action: string, options: { note?: string; actualQty?: number } = {}) {
    if (task.submitPending) {
        addOperation(`${task.currentNo} ${action}重复提交，回显首次提交结果`);
        return;
    }
    const nextStatus = nextStatusForAction(task, action);
    const actualQty = options.actualQty ?? (actionUpdatesQuantity(action) ? task.plannedQty : task.actualQty);
    const logs = [`${formatTime()} ${action}，${task.status} -> ${nextStatus}${options.note ? `，${options.note}` : ''}`, ...task.logs].slice(0, 12);
    setTaskState(task.id, {
        status: nextStatus,
        statusTone: toneForStatus(nextStatus),
        actualQty,
        primaryAction: nextPrimaryAction(task, action, nextStatus),
        logs,
        updatedAt: formatTime(),
        submitPending: false,
        submitPendingAction: ''
    });
    addOperation(`${task.currentNo} ${action}`);
}

export function markTaskPending(task: WarehouseTask, action: string) {
    const logs = [`${formatTime()} ${action}提交超时，结果待确认`, ...task.logs].slice(0, 12);
    setTaskState(task.id, {
        status: '提交结果待确认',
        statusTone: 'red',
        submitPending: true,
        submitPendingAction: action,
        logs,
        updatedAt: formatTime()
    });
    addOperation(`${task.currentNo} 进入提交结果待确认`);
}

export function updateTaskItemQty(task: WarehouseTask, itemId: string, qty: number) {
    const nextItems = task.items.map((item) => item.id === itemId ? { ...item, actualQty: clampQty(qty, item.planQty), status: clampQty(qty, item.planQty) > 0 ? '处理中' : item.status } : item);
    setTaskState(task.id, {
        items: nextItems,
        actualQty: nextItems.reduce((sum, item) => sum + Number(item.actualQty || 0), 0),
        logs: [`${formatTime()} 更新 ${itemId} 实际数量`, ...task.logs].slice(0, 12),
        updatedAt: formatTime()
    } as Partial<WarehouseTask>);
}

export function updateTaskItemLocation(task: WarehouseTask, itemId: string, location: string) {
    const nextItems = task.items.map((item) => item.id === itemId ? { ...item, location, status: '已扫码' } : item);
    setTaskState(task.id, {
        items: nextItems,
        logs: [`${formatTime()} 修改库位 ${itemId} -> ${location}`, ...task.logs].slice(0, 12),
        updatedAt: formatTime()
    } as Partial<WarehouseTask>);
}

export function markShortage(task: WarehouseTask, itemId: string, station: string, qty: number) {
    const nextItems = task.items.map((item) => {
        if (item.id !== itemId || !item.stationDemand) return item;
        return {
            ...item,
            stationDemand: item.stationDemand.map((row) => row.station === station ? { ...row, marked: clampQty(qty, row.shortage) } : row)
        };
    });
    setTaskState(task.id, {
        items: nextItems,
        logs: [`${formatTime()} 记录履约缺货 ${station}`, ...task.logs].slice(0, 12),
        updatedAt: formatTime()
    } as Partial<WarehouseTask>);
}

export function getTaskShortageQty(task: WarehouseTask) {
    return task.items.reduce((sum, item) => sum + Math.max(0, item.planQty - item.actualQty), 0);
}

export function actionIsCritical(action: string) {
    return ['确认收货', '确认上架', '完成拣货', '装车', '确认出库', '回仓确认', '关闭任务', '发起回仓交接', '完成配送'].includes(action);
}

export function nextStatusForAction(task: WarehouseTask, action: string): TaskStatus {
    if (action === '接单') return '收货中';
    if (action === '扫码收货') return '收货中';
    if (action === '上传质检' || action === '差异原因') return '待确认收货';
    if (action === '确认收货') return '已收货';
    if (action === '审核通过' || action === '提交审核') return '待送货';
    if (action === '驳回' || action === '关闭任务') return '已关闭';
    if (action === '差异确认') return '待采购确认';
    if (action === '开始拣货' || action === '继续拣货' || action === '扫描条码') return '拣货中';
    if (action === '记录履约缺货') return '待记录履约缺货';
    if (action === '完成拣货') return '已拣货';
    if (action === '装车') return '已装车待出库';
    if (action === '确认出库') return '已出库';
    if (action === '确认上架' || action === '填写库位数量') return '已上架';
    if (action === '转派任务') return '待处理';
    if (action === '回仓确认') return '已完成';
    return task.status;
}

export function nextPrimaryAction(task: WarehouseTask, action: string, status: TaskStatus) {
    if (action === '接单') return '扫码收货';
    if (action === '扫码收货') return '上传质检';
    if (action === '上传质检' || action === '差异原因') return '确认收货';
    if (action === '审核通过') return '到仓跟踪';
    if (action === '提交审核') return '审核通过';
    if (action === '开始拣货' || action === '继续拣货' || action === '扫描条码') return '完成拣货';
    if (action === '记录履约缺货') return '完成拣货';
    if (action === '装车') return '确认出库';
    if (action === '确认出库') return '查看装车回执';
    if (action === '填写库位数量') return '确认上架';
    if (doneStatus(status)) return '查看记录';
    return task.primaryAction;
}

export function getDriverOrders() {
    const state = driverState();
    const cached = apiDriverOrderCache();
    return [...cached, ...driverOrdersBase.filter((order) => !cached.some((apiOrder) => apiOrder.id === order.id))].map((order) => ({
        ...order,
        ...(state.orders?.[order.id] || {}),
        stations: order.stations.map((station) => ({ ...station, ...(state.stations?.[`${order.id}:${station.id}`] || {}) })),
        logs: [...((state.orders?.[order.id] || {}).logs || order.logs)]
    }));
}

export function findDriverOrder(id: string) {
    return getDriverOrders().find((order) => order.id === id);
}

export function applyDriverAction(orderId: string, action: string, stationId?: string) {
    const order = findDriverOrder(orderId);
    if (!order) return;
    const state = driverState();
    const orders = { ...(state.orders || {}) };
    const stations = { ...(state.stations || {}) };
    let nextOrder: Partial<WarehouseDriverOrder> = { ...(orders[orderId] || {}) };
    let log = action;
    if (action === '确认出发') {
        nextOrder.status = '配送中';
        nextOrder.statusTone = 'blue';
    } else if (action === '发起回仓交接') {
        nextOrder.status = '待回仓';
        nextOrder.statusTone = 'amber';
    } else if (stationId) {
        const key = `${orderId}:${stationId}`;
        const patch = { ...(stations[key] || {}) } as Partial<DriverStation>;
        if (action === '到达自提点') {
            patch.status = '已到达';
            patch.statusTone = 'blue';
        } else if (action === '完成配送') {
            patch.status = '已完成';
            patch.statusTone = 'green';
        } else if (action === '无法送达') {
            patch.status = '无法送达';
            patch.statusTone = 'red';
            patch.abnormalNote = '现场无法交付，已记录原因';
        } else if (action === '退货交接') {
            patch.status = '退货已交接';
            patch.statusTone = 'green';
        }
        stations[key] = patch;
        log = `${action} ${stationId}`;
    }
    const mergedStations = order.stations.map((station) => ({ ...station, ...(stations[`${orderId}:${station.id}`] || {}) }));
    if (mergedStations.length && mergedStations.every((station) => ['已完成', '无法送达', '退货已交接'].includes(station.status))) {
        nextOrder.status = '已完成';
        nextOrder.statusTone = 'green';
    }
    nextOrder.logs = [`${formatTime()} ${log}`, ...order.logs].slice(0, 10);
    orders[orderId] = nextOrder;
    uni.setStorageSync(DRIVER_STATE_KEY, { orders, stations });
    addOperation(`${orderId} ${action}`);
}

export function getReturnOrders() {
    return returnOrdersBase.map((item) => ({ ...item, items: item.items.map((row) => ({ ...row })) }));
}

export function formatTime() {
    const now = new Date();
    return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
}

export function statusTone(status: string): StatusTone {
    if (/已完成|已收货|已上架|已出库|可售|充足/.test(status)) return 'green';
    if (/配送中|收货中|拣货中|审核中|待仓库确认|待送货/.test(status)) return 'blue';
    if (/异常|无法|失败|冻结/.test(status)) return 'red';
    if (/已关闭|关闭/.test(status)) return 'gray';
    return 'amber';
}

function task(id: string, role: WarehouseRole, title: string, type: string, sourceNo: string, status: TaskStatus, tone: StatusTone, route: string, area: string, location: string, due: string, priority: string, primaryAction: string, secondaryActions: string[], items: TaskItem[]): WarehouseTask {
    const plannedQty = items.reduce((sum, row) => sum + row.planQty, 0);
    const actualQty = items.reduce((sum, row) => sum + row.actualQty, 0);
    return {
        id,
        role,
        title,
        type,
        sourceNo,
        currentNo: id,
        status,
        statusTone: tone,
        warehouse: '广州中心仓',
        area,
        location,
        route,
        due,
        date: '2026-06-10',
        priority,
        skuCount: items.length,
        plannedQty,
        actualQty,
        diffNote: plannedQty === actualQty ? '无差异' : `计划 ${plannedQty}，实际 ${actualQty}，差异 ${Math.max(0, plannedQty - actualQty)}`,
        primaryAction,
        secondaryActions,
        items,
        logs: [`系统生成任务 ${id}，来源 ${sourceNo}`],
        updatedAt: '10:30'
    };
}

function item(title: string, sku: string, spec: string, batch: string, location: string, barcode: string, planQty: number, actualQty: number, unit: string, temp: string, imageTone: string, stationDemand?: StationDemand[]): TaskItem {
    return {
        id: `${sku}-${batch}`.replace(/[^A-Za-z0-9-]/g, ''),
        title,
        sku,
        spec,
        batch,
        location,
        barcode,
        planQty,
        actualQty,
        unit,
        temp,
        status: actualQty > 0 ? '处理中' : '待处理',
        imageTone,
        stationDemand
    };
}

function inventory(id: string, title: string, sku: string, batch: string, zone: string, location: string, inStock: number, locked: number, available: number, frozen: number, status: string, statusToneValue: StatusTone, imageTone: string): InventoryRow {
    return { id, title, sku, batch, zone, location, inStock, locked, available, frozen, status, statusTone: statusToneValue, imageTone };
}

function station(id: string, index: number, name: string, address: string, contact: string, distance: string, deliveryQty: number, returnQty: number, status: DriverStation['status'] = '待配送', statusToneValue: StatusTone = 'amber', abnormalNote = ''): DriverStation {
    return {
        id,
        index,
        name,
        address,
        contact,
        distance,
        deliveryQty,
        returnQty,
        status,
        statusTone: statusToneValue,
        abnormalNote,
        goods: goodsSet(deliveryQty, Math.round(deliveryQty * 0.7)),
        returns: goodsSet(returnQty, 0)
    };
}

function goodsSet(expected: number, actual: number): DriverGoods[] {
    return [
        { id: 'G-1', title: '云南蓝莓 125g*4盒', sku: 'SKU-FR-001-S', spec: '用户装 / 4盒', expected, actual, unit: '组' },
        { id: 'G-2', title: '四川爱媛果冻橙 5斤装', sku: 'SKU-FR-002', spec: '5斤装', expected: Math.max(0, Math.floor(expected / 2)), actual: Math.max(0, Math.floor(actual / 2)), unit: '袋' }
    ];
}

function doc(id: string, category: WarehouseManagerDocument['category'], title: string, type: string, status: string, tone: StatusTone, owner: string, sourceNo: string, qty: number, skuCount: number, actions: string[]): WarehouseManagerDocument {
    return {
        id,
        category,
        title,
        type,
        status,
        statusTone: tone,
        owner,
        warehouse: '广州中心仓',
        sourceNo,
        qty,
        skuCount,
        operator: '仓库主管 mgr_gz01',
        date: '2026-06-10',
        actions
    };
}

function toneForStatus(status: TaskStatus): StatusTone {
    return statusTone(status);
}

function actionUpdatesQuantity(action: string) {
    return ['确认收货', '完成拣货', '装车', '确认出库', '完成配送', '确认上架', '填写库位数量', '回仓确认'].includes(action);
}

function doneStatus(status: TaskStatus) {
    return ['已收货', '已拣货', '已出库', '已完成', '已上架', '已关闭'].includes(status);
}

function cloneTasks() {
    return tasksBase.map((taskRow) => ({
        ...taskRow,
        items: taskRow.items.map((row) => ({ ...row, stationDemand: row.stationDemand ? row.stationDemand.map((stationRow) => ({ ...stationRow })) : undefined })),
        logs: [...taskRow.logs]
    }));
}

function taskState(): Record<string, Partial<WarehouseTask>> {
    const saved = uni.getStorageSync(TASK_STATE_KEY) as Record<string, Partial<WarehouseTask>>;
    return saved && typeof saved === 'object' ? saved : {};
}

function setTaskState(id: string, patch: Partial<WarehouseTask>) {
    const current = taskState();
    uni.setStorageSync(TASK_STATE_KEY, { ...current, [id]: { ...(current[id] || {}), ...patch } });
}

function mergeTaskState(taskRow: WarehouseTask, patch?: Partial<WarehouseTask>): WarehouseTask {
    if (!patch) return taskRow;
    const mergedItems = Array.isArray(patch.items) ? (patch.items as TaskItem[]).map((itemRow) => ({ ...itemRow })) : taskRow.items;
    const actualQty = patch.actualQty ?? mergedItems.reduce((sum, row) => sum + Number(row.actualQty || 0), 0);
    return {
        ...taskRow,
        ...patch,
        items: mergedItems,
        actualQty,
        skuCount: mergedItems.length,
        logs: Array.isArray(patch.logs) ? [...(patch.logs as string[])] : taskRow.logs,
        diffNote: patch.diffNote || (taskRow.plannedQty === actualQty ? '无差异' : `计划 ${taskRow.plannedQty}，实际 ${actualQty}，差异 ${Math.max(0, taskRow.plannedQty - actualQty)}`)
    };
}

function apiTaskCache(): WarehouseTask[] {
    const saved = uni.getStorageSync(API_TASK_CACHE_KEY) as WarehouseTask[];
    if (!Array.isArray(saved)) return [];
    return saved.map((taskRow) => ({
        ...taskRow,
        items: Array.isArray(taskRow.items) ? taskRow.items.map((row) => ({ ...row, stationDemand: row.stationDemand ? row.stationDemand.map((stationRow) => ({ ...stationRow })) : undefined })) : [],
        logs: Array.isArray(taskRow.logs) ? [...taskRow.logs] : []
    }));
}

function rememberApiTasks(tasks: WarehouseTask[]) {
    const existing = apiTaskCache().filter((taskRow) => !tasks.some((next) => next.id === taskRow.id));
    const next = [...tasks, ...existing].slice(0, 60);
    uni.setStorageSync(API_TASK_CACHE_KEY, next);
}

function apiDriverOrderCache(): WarehouseDriverOrder[] {
    const saved = uni.getStorageSync(API_DRIVER_CACHE_KEY) as WarehouseDriverOrder[];
    if (!Array.isArray(saved)) return [];
    return saved.map((order) => ({
        ...order,
        stations: Array.isArray(order.stations) ? order.stations.map((stationRow) => ({ ...stationRow })) : [],
        logs: Array.isArray(order.logs) ? [...order.logs] : []
    }));
}

function rememberApiDriverOrders(orders: WarehouseDriverOrder[]) {
    const existing = apiDriverOrderCache().filter((order) => !orders.some((next) => next.id === order.id));
    const next = [...orders, ...existing].slice(0, 30);
    uni.setStorageSync(API_DRIVER_CACHE_KEY, next);
}

function driverState(): {
    orders?: Record<string, Partial<WarehouseDriverOrder>>;
    stations?: Record<string, Partial<DriverStation>>;
} {
    const saved = uni.getStorageSync(DRIVER_STATE_KEY) as {
        orders?: Record<string, Partial<WarehouseDriverOrder>>;
        stations?: Record<string, Partial<DriverStation>>;
    };
    return saved && typeof saved === 'object' ? saved : {};
}

function clampQty(value: number, max: number) {
    const next = Math.round(Number(value || 0));
    return Math.max(0, Math.min(max, Number.isFinite(next) ? next : 0));
}
