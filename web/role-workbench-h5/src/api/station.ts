import { request } from '@/utils/request';

export interface PageResult<T> {
    total: number;
    list: T[];
}

export interface StationWorkbenchDTO {
    stationId: number;
    leaderId: number;
    todayOrderCount: number;
    waitPickupCount: number;
    pickedCount: number;
    arrivalCount: number;
    exceptionCount: number;
    unreadMessageCount: number;
    availableAmount: string;
    frozenAmount: string;
    pendingCommissionAmount: string;
}

export interface StationOrderDTO {
    id: number;
    orderNo: string;
    stationId: number;
    tradeStatus: number;
    payStatus: number;
    fulfillStatus: number;
    pickupName?: string;
    createTime?: string;
}

export interface DeliveryOrderDTO {
    id: number;
    deliveryNo: string;
    outboundId?: number;
    loadingId?: number;
    warehouseId: number;
    lineId: number;
    driverAccountId?: number;
    deliveryDate?: string;
    status: number;
}

export interface DeliveryStationDTO {
    id: number;
    deliveryId: number;
    stationId: number;
    deliverySort?: number;
    status: number;
    arriveTime?: string;
    completeTime?: string;
    remark?: string;
}

export interface ReturnHandoverDTO {
    id: number;
    returnNo: string;
    deliveryId?: number;
    stationId: number;
    skuId?: number;
    warehouseId: number;
    returnQty?: number;
    reasonType?: number;
    status: number;
    remark?: string;
}

export interface StationExceptionDTO {
    id: number;
    exceptionNo: string;
    title: string;
    priority: number;
    sourceModule: string;
    status: number;
    description?: string;
    createTime?: string;
}

export interface StationFinanceAccountDTO {
    id: number;
    accountNo: string;
    subjectType: number;
    subjectId: number;
    availableAmount: string;
    frozenAmount: string;
    depositAmount: string;
    status: number;
}

export interface StationFinanceFlowDTO {
    id: number;
    flowNo: string;
    accountId: number;
    amount: string;
    flowType: number;
    bizType: string;
    bizNo: string;
    createTime?: string;
}

export interface CommissionDetailDTO {
    id: number;
    commissionNo: string;
    subjectType: number;
    subjectId: number;
    amount: string;
    status: number;
}

export interface WithdrawApplyDTO {
    id: number;
    withdrawNo: string;
    accountId: number;
    applyAmount: string;
    status: number;
}

export interface StationMessageDTO {
    id: number;
    msgNo: string;
    title: string;
    bizType: string;
    bizNo: string;
    readStatus: number;
    sendStatus: number;
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

export interface StationLeaderDTO {
    id: number;
    stationId: number;
    leaderId: number;
    bindStatus: number;
}

export interface StationLeaveDTO {
    id: number;
    leaveNo: string;
    stationId: number;
    leaderId: number;
    status: number;
}

export interface SupplierWorkbenchDTO {
    supplierId: number;
    waitAuditPurchaseCount: number;
    waitDeliveryPurchaseCount: number;
    deliveringOrderCount: number;
    arrivedOrderCount: number;
    todayDeliveryOrderCount: number;
    purchaseQty: number;
    receivedQty: number;
    diffQty: number;
}

function buildSearch(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return search.toString();
}

export function getWorkbenchSummary(query: Record<string, string | number | undefined>) {
    return request<StationWorkbenchDTO>(`/station/workbench/summary?${buildSearch(query)}`);
}

export function pageStationOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationOrderDTO>>(`/station/workbench/orders?${buildSearch(query)}`);
}

export function pageDeliveryOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryOrderDTO>>(`/station/workbench/deliveries?${buildSearch(query)}`);
}

export function pageDeliveryStations(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryStationDTO>>(`/station/workbench/delivery-stations?${buildSearch(query)}`);
}

export function pageReturnHandovers(query: Record<string, string | number | undefined>) {
    return request<PageResult<ReturnHandoverDTO>>(`/station/workbench/returns?${buildSearch(query)}`);
}

export function pageExceptions(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationExceptionDTO>>(`/station/workbench/exceptions?${buildSearch(query)}`);
}

export function pageFinanceAccounts(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationFinanceAccountDTO>>(`/station/finance/accounts?${buildSearch(query)}`);
}

export function pageFinanceFlows(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationFinanceFlowDTO>>(`/station/finance/account-flows?${buildSearch(query)}`);
}

export function pageCommissions(query: Record<string, string | number | undefined>) {
    return request<PageResult<CommissionDetailDTO>>(`/station/finance/commissions?${buildSearch(query)}`);
}

export function pageWithdraws(query: Record<string, string | number | undefined>) {
    return request<PageResult<WithdrawApplyDTO>>(`/station/finance/withdraws?${buildSearch(query)}`);
}

export function pageMessages(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationMessageDTO>>(`/station/finance/messages?${buildSearch(query)}`);
}

export function pageStations(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationDTO>>(`/station/base/stations?${buildSearch(query)}`);
}

export function pageLeaders(query: Record<string, string | number | undefined>) {
    return request<PageResult<LeaderDTO>>(`/station/base/leaders?${buildSearch(query)}`);
}

export function pageStationLeaders(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationLeaderDTO>>(`/station/base/station-leaders?${buildSearch(query)}`);
}

export function pageLeaves(query: Record<string, string | number | undefined>) {
    return request<PageResult<StationLeaveDTO>>(`/station/base/leaves?${buildSearch(query)}`);
}

export function getSupplierWorkbenchSummary(query: Record<string, string | number | undefined>) {
    return request<SupplierWorkbenchDTO>(`/supplier/workbench/summary?${buildSearch(query)}`);
}

export function getStationOrderDetail(orderNo: string) {
    return request<StationOrderDTO>(`/station/workbench/orders/${orderNo}`);
}

export function verifyPickup(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/workbench/orders/verify-pickup', {
        method: 'POST',
        data: payload
    });
}

export function reportStationException(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/workbench/exceptions', {
        method: 'POST',
        data: payload
    });
}

export function confirmStationArrival(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/workbench/delivery-stations/confirm-arrival', {
        method: 'POST',
        data: payload
    });
}

export function sendArrivalNotice(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/workbench/messages/arrival-notice', {
        method: 'POST',
        data: payload
    });
}

export function confirmReturnHandover(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/workbench/returns/confirm-handover', {
        method: 'POST',
        data: payload
    });
}

export function applyWithdraw(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/finance/withdraws', {
        method: 'POST',
        data: payload
    });
}

export function cancelWithdraw(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/finance/withdraws/cancel', {
        method: 'POST',
        data: payload
    });
}

export function applyLeave(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/base/leaves', {
        method: 'POST',
        data: payload
    });
}

export function cancelLeave(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/base/leaves/cancel', {
        method: 'POST',
        data: payload
    });
}

export function markMessageRead(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/messages/read', {
        method: 'POST',
        data: payload
    });
}

export function markAllMessagesRead(payload: Record<string, string | number | undefined>) {
    return request<void>('/station/messages/read-all', {
        method: 'POST',
        data: payload
    });
}

export function pageSupplierPurchases(query: Record<string, string | number | undefined>) {
    return request<PageResult<Record<string, unknown>>>(`/supplier/purchases?${buildSearch(query)}`);
}

export function getSupplierPurchaseDetail(purchaseNo: string) {
    return request<Record<string, unknown>>(`/supplier/purchases/${purchaseNo}`);
}

export function confirmSupplierPurchase(payload: Record<string, string | number | undefined>) {
    return request<void>('/supplier/purchases/confirm', {
        method: 'POST',
        data: payload
    });
}

export function submitSupplierArrival(payload: Record<string, string | number | undefined>) {
    return request<void>('/supplier/deliveries/arrival-proof', {
        method: 'POST',
        data: payload
    });
}

export function submitSupplierShortage(payload: Record<string, string | number | undefined>) {
    return request<void>('/supplier/deliveries/shortage', {
        method: 'POST',
        data: payload
    });
}

export function applySupplierSupplement(payload: Record<string, string | number | undefined>) {
    return request<void>('/supplier/purchases/supplement', {
        method: 'POST',
        data: payload
    });
}

export function pageSupplierSettlements(query: Record<string, string | number | undefined>) {
    return request<PageResult<Record<string, unknown>>>(`/supplier/settlements?${buildSearch(query)}`);
}
