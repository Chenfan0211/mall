import { request } from '@/utils/request';

export interface PageResult<T> {
    total: number;
    list: T[];
}

export interface InboundOrderDTO {
    id: number;
    inboundNo: string;
    sourceNo?: string;
    supplierId: number;
    warehouseId: number;
    status: number;
    receiveTime?: string;
    createTime?: string;
}

export interface InventoryDTO {
    id: number;
    warehouseId: number;
    zoneId: number;
    locationId: number;
    skuId: number;
    supplierId: number;
    batchNo: string;
    inStockQty: number;
    lockedQty: number;
    availableQty: number;
    frozenQty: number;
}

export interface WaveBatchDTO {
    id: number;
    waveNo: string;
    warehouseId: number;
    lineId: number;
    deliveryDate?: string;
    status: number;
}

export interface PickTaskDTO {
    id: number;
    pickNo: string;
    waveId?: number;
    warehouseId: number;
    lineId: number;
    assignAccountId?: number;
    requiredQty?: number;
    actualQty?: number;
    shortageQty?: number;
    status: number;
    completeTime?: string;
    createTime?: string;
}

export interface PickItemDTO {
    id: number;
    pickId: number;
    skuId: number;
    qty: number;
}

export interface LoadingOrderDTO {
    id: number;
    loadingNo: string;
    waveId?: number;
    warehouseId: number;
    lineId: number;
    driverAccountId?: number;
    deliveryDate?: string;
    requiredQty?: number;
    actualQty?: number;
    status: number;
    loadAccountId?: number;
    loadTime?: string;
    outboundAccountId?: number;
    outboundTime?: string;
    createTime?: string;
}

export interface LoadingItemDTO {
    id: number;
    loadingId: number;
    stationId: number;
    skuId: number;
    qty: number;
}

export interface OutboundOrderDTO {
    id: number;
    outboundNo: string;
    loadingId?: number;
    warehouseId: number;
    lineId: number;
    outboundQty: number;
    status: number;
}

export interface DeliveryOrderDTO {
    id: number;
    deliveryNo: string;
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
    status: number;
}

export interface DriverSignRecordDTO {
    id: number;
    deliveryId: number;
    driverAccountId: number;
    stationId: number;
    signType: number;
    resultStatus: number;
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
    driverAccountId?: number;
    supervisorAccountId?: number;
    status: number;
    confirmTime?: string;
    remark?: string;
    createTime?: string;
}

export interface InventoryLockDTO {
    id: number;
    lockNo: string;
    warehouseId: number;
    skuId: number;
    bizType: number;
    bizNo: string;
    lockQty: number;
    releaseQty: number;
    lockStatus: number;
}

export interface InventoryLogDTO {
    id: number;
    logNo: string;
    warehouseId: number;
    zoneId?: number;
    locationId?: number;
    skuId: number;
    batchNo?: string;
    changeType: number;
    beforeQty: number;
    changeQty: number;
    afterQty: number;
    bizType: string;
    bizNo: string;
    operatorAccountId?: number;
    remark?: string;
    createTime?: string;
}

export interface StocktakeOrderDTO {
    id: number;
    stocktakeNo: string;
    warehouseId: number;
    skuId: number;
    bookQty: number;
    actualQty: number;
    diffQty: number;
    status: number;
}

export interface WarehouseDTO {
    id: number;
    warehouseCode: string;
    warehouseName: string;
    cityId: number;
    warehouseType: number;
    managerAccountId: number;
    status: number;
}

export interface ZoneDTO {
    id: number;
    warehouseId: number;
    zoneCode: string;
    zoneName: string;
    zoneType: number;
    status: number;
}

export interface LocationDTO {
    id: number;
    warehouseId: number;
    zoneId: number;
    locationCode: string;
    locationName: string;
    capacityQty: number;
    status: number;
}

export interface LineDTO {
    id: number;
    warehouseId: number;
    lineCode: string;
    lineName: string;
    defaultDriverAccountId: number;
    status: number;
}

export interface DriverDTO {
    id: number;
    driverNo: string;
    accountId: number;
    driverName: string;
    driverMobile: string;
    vehicleNo: string;
    status: number;
}

export interface WmsOperationLogDTO {
    id: number;
    logNo: string;
    warehouseId: number;
    accountId: number;
    roleCode: string;
    moduleCode: string;
    actionCode: string;
    bizType: string;
    bizNo: string;
    resultStatus: number;
}

export interface PutawayTaskDTO {
    id: number;
    putawayNo: string;
    inboundId: number;
    skuId: number;
    warehouseId: number;
    fromZoneId?: number;
    targetZoneId?: number;
    targetLocationId?: number;
    putawayQty: number;
    actualQty?: number;
    status: number;
    assignAccountId?: number;
    completeTime?: string;
    createTime?: string;
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

export function pageInboundOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<InboundOrderDTO>>(`/wms/inbound/orders?${buildSearch(query)}`);
}

export function pagePutawayTasks(query: Record<string, string | number | undefined>) {
    return request<PageResult<PutawayTaskDTO>>(`/wms/inbound/putaway-tasks?${buildSearch(query)}`);
}

export function pageWarehouses(query: Record<string, string | number | undefined>) {
    return request<PageResult<WarehouseDTO>>(`/wms/base/warehouses?${buildSearch(query)}`);
}

export function pageZones(query: Record<string, string | number | undefined>) {
    return request<PageResult<ZoneDTO>>(`/wms/base/zones?${buildSearch(query)}`);
}

export function pageLocations(query: Record<string, string | number | undefined>) {
    return request<PageResult<LocationDTO>>(`/wms/base/locations?${buildSearch(query)}`);
}

export function pageLines(query: Record<string, string | number | undefined>) {
    return request<PageResult<LineDTO>>(`/wms/base/lines?${buildSearch(query)}`);
}

export function pageDrivers(query: Record<string, string | number | undefined>) {
    return request<PageResult<DriverDTO>>(`/wms/base/drivers?${buildSearch(query)}`);
}

export function pageInventories(query: Record<string, string | number | undefined>) {
    return request<PageResult<InventoryDTO>>(`/wms/inventory/stocks?${buildSearch(query)}`);
}

export function pageInventoryLocks(query: Record<string, string | number | undefined>) {
    return request<PageResult<InventoryLockDTO>>(`/wms/inventory/locks?${buildSearch(query)}`);
}

export function pageInventoryLogs(query: Record<string, string | number | undefined>) {
    return request<PageResult<InventoryLogDTO>>(`/wms/inventory/logs?${buildSearch(query)}`);
}

export function pageStocktakeOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<StocktakeOrderDTO>>(`/wms/inventory/stocktakes?${buildSearch(query)}`);
}

export function pageWaveBatches(query: Record<string, string | number | undefined>) {
    return request<PageResult<WaveBatchDTO>>(`/wms/outbound/waves?${buildSearch(query)}`);
}

export function pagePickTasks(query: Record<string, string | number | undefined>) {
    return request<PageResult<PickTaskDTO>>(`/wms/outbound/picks?${buildSearch(query)}`);
}

export function pagePickItems(query: Record<string, string | number | undefined>) {
    return request<PageResult<PickItemDTO>>(`/wms/outbound/pick-items?${buildSearch(query)}`);
}

export function pageLoadingOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<LoadingOrderDTO>>(`/wms/outbound/loading-orders?${buildSearch(query)}`);
}

export function pageLoadingItems(query: Record<string, string | number | undefined>) {
    return request<PageResult<LoadingItemDTO>>(`/wms/outbound/loading-items?${buildSearch(query)}`);
}

export function pageOutboundOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<OutboundOrderDTO>>(`/wms/outbound/orders?${buildSearch(query)}`);
}

export function pageDeliveryOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryOrderDTO>>(`/wms/outbound/deliveries?${buildSearch(query)}`);
}

export function pageDeliveryStations(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryStationDTO>>(`/wms/outbound/delivery-stations?${buildSearch(query)}`);
}

export function pageDriverSignRecords(query: Record<string, string | number | undefined>) {
    return request<PageResult<DriverSignRecordDTO>>(`/wms/outbound/driver-sign-records?${buildSearch(query)}`);
}

export function pageReturnHandovers(query: Record<string, string | number | undefined>) {
    return request<PageResult<ReturnHandoverDTO>>(`/wms/outbound/returns?${buildSearch(query)}`);
}

export function pageOperationLogs(query: Record<string, string | number | undefined>) {
    return request<PageResult<WmsOperationLogDTO>>(`/wms/outbound/operation-logs?${buildSearch(query)}`);
}
