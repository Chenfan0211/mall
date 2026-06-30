import { request } from '@/utils/request';

export interface PageResult<T> {
    total: number;
    list: T[];
}

export interface StationWorkbenchDTO {
    stationId?: number;
    leaderId?: number;
    todayOrderCount?: number;
    waitPickupCount?: number;
    pickedCount?: number;
    arrivalCount?: number;
    exceptionCount?: number;
    unreadMessageCount?: number;
    availableAmount?: string;
    frozenAmount?: string;
    pendingCommissionAmount?: string;
}

export interface StationDTO {
    id?: number;
    stationNo?: string;
    stationName?: string;
    contactName?: string;
}

export interface LeaderDTO {
    id?: number;
    leaderNo?: string;
    leaderName?: string;
    leaderMobile?: string;
}

export interface StationOrderDTO {
    id: number;
    orderNo: string;
    stationId?: number;
    leaderId?: number;
    pickupName?: string;
    pickupMobile?: string;
    totalAmount?: string;
    payAmount?: string;
    tradeStatus?: number;
    payStatus?: number;
    fulfillStatus?: number;
    createTime?: string;
}

export interface StationOrderItemDTO {
    id: number;
    orderId?: number;
    orderNo?: string;
    orderItemNo?: string;
    productName?: string;
    skuName?: string;
    salePrice?: string;
    qty?: number;
    pickedQty?: number;
    itemAmount?: string;
    fulfillStatus?: number;
    afterSaleStatus?: number;
    expectedPickupDate?: string;
}

export interface DeliveryOrderDTO {
    id: number;
    deliveryNo: string;
    warehouseId?: number;
    lineId?: number;
    driverAccountId?: number;
    deliveryDate?: string;
    status?: number;
    startTime?: string;
    completeTime?: string;
    createTime?: string;
}

export interface DeliveryStationDTO {
    id: number;
    deliveryId?: number;
    stationId?: number;
    deliverySort?: number;
    status?: number;
    arriveTime?: string;
    completeTime?: string;
    remark?: string;
    createTime?: string;
}

export interface ReturnHandoverDTO {
    id: number;
    returnNo: string;
    deliveryId?: number;
    stationId?: number;
    skuId?: number;
    warehouseId?: number;
    returnQty?: number;
    reasonType?: number;
    status?: number;
    confirmTime?: string;
    remark?: string;
    createTime?: string;
}

export interface StationExceptionDTO {
    id: number;
    exceptionNo?: string;
    title?: string;
    priority?: number;
    sourceModule?: string;
    status?: number;
    description?: string;
    createTime?: string;
}

export interface StationFinanceAccountDTO {
    id: number;
    accountNo?: string;
    subjectType?: number;
    subjectId?: number;
    availableAmount?: string;
    frozenAmount?: string;
    depositAmount?: string;
    status?: number;
}

export interface StationFinanceFlowDTO {
    id: number;
    flowNo?: string;
    accountId?: number;
    amount?: string;
    flowType?: number;
    direction?: number;
    bizType?: string;
    bizNo?: string;
    createTime?: string;
}

export interface CommissionDetailDTO {
    id: number;
    commissionNo?: string;
    subjectType?: number;
    subjectId?: number;
    amount?: string;
    status?: number;
    createTime?: string;
}

export interface WithdrawApplyDTO {
    id: number;
    withdrawNo?: string;
    accountId?: number;
    applyAmount?: string;
    withdrawAmount?: string;
    status?: number;
    auditStatus?: number;
    payStatus?: number;
    auditTime?: string;
    auditRemark?: string;
    createTime?: string;
}

export interface StationMessageDTO {
    id: number;
    msgNo?: string;
    title?: string;
    content?: string;
    bizType?: string;
    bizNo?: string;
    readStatus?: number;
    sendStatus?: number;
    createTime?: string;
}

export interface StationLeaveDTO {
    id: number;
    leaveNo?: string;
    stationId?: number;
    leaderId?: number;
    startTime?: string;
    endTime?: string;
    reason?: string;
    auditStatus?: number;
    auditTime?: string;
    auditRemark?: string;
    status?: number;
    createTime?: string;
}

export interface SupplierWorkbenchDTO {
    supplierId?: number;
    waitAuditPurchaseCount?: number;
    waitDeliveryPurchaseCount?: number;
    deliveringOrderCount?: number;
    arrivedOrderCount?: number;
    todayDeliveryOrderCount?: number;
    purchaseQty?: number;
    receivedQty?: number;
    diffQty?: number;
}

export interface SupplierPurchaseDTO {
    id: number;
    purchaseNo?: string;
    supplierId?: number;
    warehouseId?: number;
    cityId?: number;
    deliveryDate?: string;
    purchaseStatus?: number;
    auditStatus?: number;
    createReason?: string;
    expectedArrivalTime?: string;
    createTime?: string;
}

export interface SupplierPurchaseItemDTO {
    id: number;
    purchaseId?: number;
    purchaseNo?: string;
    productId?: number;
    skuId?: number;
    supplierId?: number;
    warehouseId?: number;
    skuName?: string;
    productName?: string;
    applyQty?: number;
    purchaseQty?: number;
    receivedQty?: number;
    rejectedQty?: number;
    purchasePrice?: string;
    amount?: string;
    status?: number;
    createTime?: string;
}

export interface SupplierDeliveryDTO {
    id: number;
    deliveryNo?: string;
    supplierId?: number;
    warehouseId?: number;
    deliveryDate?: string;
    status?: number;
    createTime?: string;
}

export interface SupplierSettlementDTO {
    id: number;
    settlementNo?: string;
    supplierId?: number;
    periodStartDate?: string;
    periodEndDate?: string;
    purchaseAmount?: string | number;
    diffAmount?: string | number;
    payableAmount?: string;
    status?: number;
    createTime?: string;
}

function buildSearch(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    const text = search.toString();
    return text ? `?${text}` : '';
}

export function getWorkbenchSummary(query: Record<string, string | number | undefined>) {
    return request<StationWorkbenchDTO>(`/station/workbench/summary${buildSearch(query)}`);
}

export function getStationDetail(id: number) {
    return request<StationDTO>(`/station/base/stations/${id}`);
}

export function getLeaderDetail(id: number) {
    return request<LeaderDTO>(`/station/base/leaders/${id}`);
}

export function pageStationOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationOrderDTO>>(`/station/workbench/orders${buildSearch(query)}`);
}

export function getStationOrderDetail(id: number) {
    return request<StationOrderDTO>(`/station/workbench/orders/${id}`);
}

export function pageStationOrderItems(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationOrderItemDTO>>(`/station/workbench/order-items${buildSearch(query)}`);
}

export function getStationOrderItem(id: number) {
    return request<StationOrderItemDTO>(`/station/workbench/order-items/${id}`);
}

export function verifyPickup(id: number, payload: Record<string, string | number | undefined>) {
    return request<StationOrderItemDTO>(`/station/workbench/order-items/${id}/pickup-confirm`, {
        method: 'POST',
        data: payload
    });
}

export function pageDeliveryOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryOrderDTO>>(`/station/workbench/deliveries${buildSearch(query)}`);
}

export function pageDeliveryStations(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryStationDTO>>(`/station/workbench/delivery-stations${buildSearch(query)}`);
}

export function confirmStationArrival(id: number, payload: Record<string, string | number | undefined>) {
    return request<DeliveryStationDTO>(`/station/workbench/delivery-stations/${id}/arrival-confirm`, {
        method: 'POST',
        data: payload
    });
}

export function pageReturnHandovers(query: Record<string, string | number | undefined>) {
    return request<PageResult<ReturnHandoverDTO>>(`/station/workbench/returns${buildSearch(query)}`);
}

export function pageExceptions(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationExceptionDTO>>(`/station/workbench/exceptions${buildSearch(query)}`);
}

export function pageFinanceAccounts(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationFinanceAccountDTO>>(`/station/finance/accounts${buildSearch(query)}`);
}

export function pageFinanceFlows(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationFinanceFlowDTO>>(`/station/finance/account-flows${buildSearch(query)}`);
}

export function pageCommissions(query: Record<string, string | number | undefined>) {
    return request<PageResult<CommissionDetailDTO>>(`/station/finance/commissions${buildSearch(query)}`);
}

export function pageWithdraws(query: Record<string, string | number | undefined>) {
    return request<PageResult<WithdrawApplyDTO>>(`/station/finance/withdraws${buildSearch(query)}`);
}

export function applyWithdraw(payload: Record<string, string | number | undefined>) {
    return request<WithdrawApplyDTO>('/station/finance/withdraws', {
        method: 'POST',
        data: payload
    });
}

export function cancelWithdraw(id: number) {
    return request<WithdrawApplyDTO>(`/station/finance/withdraws/${id}/cancel`, {
        method: 'POST'
    });
}

export function pageMessages(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationMessageDTO>>(`/station/finance/messages${buildSearch(query)}`);
}

export function markMessageRead(id: number) {
    return request<StationMessageDTO>(`/station/finance/messages/${id}/read`, {
        method: 'POST'
    });
}

export function pageLeaves(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationLeaveDTO>>(`/station/base/leaves${buildSearch(query)}`);
}

export function applyLeave(payload: Record<string, string | number | undefined>) {
    return request<StationLeaveDTO>('/station/base/leaves', {
        method: 'POST',
        data: payload
    });
}

export function cancelLeave(id: number) {
    return request<StationLeaveDTO>(`/station/base/leaves/${id}/cancel`, {
        method: 'POST'
    });
}

export function getSupplierWorkbenchSummary(query: Record<string, string | number | undefined>) {
    return request<SupplierWorkbenchDTO>(`/supplier/workbench/summary${buildSearch(query)}`);
}

export function pageSupplierPurchases(query: Record<string, string | number | undefined>) {
    return request<PageResult<SupplierPurchaseDTO>>(`/supplier/purchases${buildSearch(query)}`);
}

export function getSupplierPurchaseDetail(id: number) {
    return request<SupplierPurchaseDTO>(`/supplier/purchases/${id}`);
}

export function pageSupplierPurchaseItems(query: Record<string, string | number | undefined>) {
    return request<PageResult<SupplierPurchaseItemDTO>>(`/supplier/purchases/items${buildSearch(query)}`);
}

export function pageSupplierDeliveries(query: Record<string, string | number | undefined>) {
    return request<PageResult<SupplierDeliveryDTO>>(`/supplier/deliveries${buildSearch(query)}`);
}

export function pageSupplierSettlements(query: Record<string, string | number | undefined>) {
    return request<PageResult<SupplierSettlementDTO>>(`/finance/settlements/supplier${buildSearch(query)}`);
}
