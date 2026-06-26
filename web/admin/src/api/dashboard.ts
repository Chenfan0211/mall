import { request } from '@/utils/request';

export interface OperationTodoCard {
    id: number;
    todoNo: string;
    title: string;
    priority: number;
    processRoleCode: string;
    status: number;
    dueTime?: string;
}

export interface OperationExceptionCard {
    id: number;
    exceptionNo: string;
    title: string;
    priority: number;
    sourceModule: string;
    status: number;
    description?: string;
}

export interface OperationDashboardSummary {
    pendingTodoCount: number;
    processingTodoCount: number;
    timeoutTodoCount: number;
    highPriorityTodoCount: number;
    pendingExceptionCount: number;
    processingExceptionCount: number;
    highPriorityExceptionCount: number;
    closedExceptionCount: number;
    urgentTodos: OperationTodoCard[];
    warningExceptions: OperationExceptionCard[];
}

export interface PageResult<T> {
    total: number;
    list: T[];
}

export interface OperationTodoQuery {
    pageNum?: number;
    pageSize?: number;
    todoType?: number;
    priority?: number;
    status?: number;
    sourceBizType?: string;
    processAccountId?: number;
    cityId?: number;
    supplierId?: number;
    warehouseId?: number;
}

export interface TodoDTO {
    id: number;
    todoNo: string;
    todoType?: number;
    sourceBizType?: string;
    sourceBizNo?: string;
    title: string;
    priority: number;
    processRoleCode: string;
    processAccountId?: number;
    cityId?: number;
    supplierId?: number;
    warehouseId?: number;
    status: number;
    dueTime?: string;
    createTime?: string;
}

export interface ProductDTO {
    id: number;
    productNo: string;
    productName: string;
    supplierId: number;
    auditStatus: number;
    saleStatus: number;
    cityId: number;
    mainImageUrl?: string;
}

export interface PublishPeriodDTO {
    id: number;
    periodNo: string;
    periodName: string;
    cityId: number;
    warehouseId: number;
    saleStartTime?: string;
    saleEndTime?: string;
    deliveryDate?: string;
    status: number;
}

export interface CategoryDTO {
    id: number;
    categoryCode: string;
    categoryName: string;
    categoryType: number;
    sortNo: number;
    status: number;
}

export interface OrderDTO {
    id: number;
    orderNo: string;
    stationId: number;
    pickupName?: string;
    tradeStatus: number;
    payStatus: number;
    fulfillStatus: number;
    totalAmount?: string;
    payAmount?: string;
    createTime?: string;
}

export interface FulfillmentTrackDTO {
    id: number;
    trackNo: string;
    orderId: number;
    orderItemId?: number;
    nodeType: number;
    nodeName: string;
    bizNo?: string;
    status: number;
    remark?: string;
    createTime?: string;
}

export interface AfterSaleDTO {
    id: number;
    afterSaleNo: string;
    orderId: number;
    orderNo: string;
    userId: number;
    stationId: number;
    afterSaleType: number;
    status: number;
    refundAmount: string;
    createTime?: string;
}

export interface ExceptionRecordDTO {
    id: number;
    exceptionNo: string;
    title: string;
    priority: number;
    sourceModule: string;
    status: number;
    description?: string;
    createTime?: string;
}

export interface StationDTO {
    id: number;
    stationNo: string;
    stationName: string;
    cityId: number;
    address: string;
    contactName: string;
    contactMobile: string;
    businessHours: string;
    status: number;
}

export interface LeaderDTO {
    id: number;
    leaderNo: string;
    leaderName: string;
    stationId?: number;
    status: number;
}

export interface StationLeaveDTO {
    id: number;
    leaveNo: string;
    stationId: number;
    leaderId: number;
    status: number;
}

export interface PurchaseOrderDTO {
    id: number;
    purchaseNo: string;
    supplierId: number;
    warehouseId: number;
    cityId: number;
    deliveryDate?: string;
    purchaseStatus: number;
    auditStatus: number;
    expectedArrivalTime?: string;
}

export interface DeliveryOrderDTO {
    id: number;
    deliveryNo: string;
    supplierId: number;
    purchaseId?: number;
    warehouseId: number;
    status: number;
}

export interface WithdrawApplyDTO {
    id: number;
    withdrawNo: string;
    accountId: number;
    subjectType: number;
    subjectId: number;
    withdrawAmount: string;
    receiveAccountName?: string;
    auditStatus: number;
    payStatus: number;
    auditRemark?: string;
}

export interface FinanceAccountFlowDTO {
    id: number;
    flowNo: string;
    accountId: number;
    amount: string;
    flowType: number;
    bizType: string;
    bizNo: string;
    createTime?: string;
}

export interface FinanceAccountDTO {
    id: number;
    accountNo: string;
    subjectType: number;
    subjectId: number;
    availableAmount: string;
    frozenAmount: string;
    depositAmount: string;
    status: number;
}

export function getOperationDashboardSummary() {
    return request<OperationDashboardSummary>('/operation/dashboard/summary');
}

export function pageOperationTodos(query: OperationTodoQuery) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<TodoDTO>>(`/operation/todos?${search.toString()}`);
}

export function pageOperationProducts(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<ProductDTO>>(`/product/products?${search.toString()}`);
}

export function pagePublishPeriods(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<PublishPeriodDTO>>(`/sale/periods?${search.toString()}`);
}

export function pageCategories(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<CategoryDTO[]>(`/product/categories?${search.toString()}`);
}

export function pageOperationOrders(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<OrderDTO>>(`/trade/orders?${search.toString()}`);
}

export function pageFulfillmentTracks(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<FulfillmentTrackDTO>>(`/trade/fulfillment-tracks?${search.toString()}`);
}

export function pageAfterSales(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<AfterSaleDTO>>(`/aftersale/after-sales?${search.toString()}`);
}

export function pageOperationExceptions(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<ExceptionRecordDTO>>(`/operation/exceptions?${search.toString()}`);
}

export function pageStations(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<StationDTO>>(`/station/base/stations?${search.toString()}`);
}

export function pageLeaders(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<LeaderDTO>>(`/station/base/leaders?${search.toString()}`);
}

export function pageStationLeaves(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<StationLeaveDTO>>(`/station/base/leaves?${search.toString()}`);
}

export function pagePurchases(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<PurchaseOrderDTO>>(`/supplier/purchases?${search.toString()}`);
}

export function pageSupplierDeliveries(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<DeliveryOrderDTO>>(`/supplier/deliveries?${search.toString()}`);
}

export function pageWithdraws(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<WithdrawApplyDTO>>(`/finance/withdraws?${search.toString()}`);
}

export function pageFinanceAccounts(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<FinanceAccountDTO>>(`/finance/accounts?${search.toString()}`);
}

export function pageFinanceFlows(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return request<PageResult<FinanceAccountFlowDTO>>(`/finance/accounts/flows?${search.toString()}`);
}
