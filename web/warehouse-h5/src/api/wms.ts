import { request, type WarehouseRequestOptions } from '@/utils/request';

export interface PageResult<T> {
    total: number;
    list: T[];
}

export interface InboundOrderDTO {
    id: number;
    inboundNo: string;
    sourceType?: number;
    sourceNo?: string;
    purchaseId?: number;
    deliveryId?: number;
    supplierId: number;
    warehouseId: number;
    status: number;
    receiveAccountId?: number;
    receiveTime?: string;
    createTime?: string;
}

export interface InboundItemDTO {
    id: number;
    inboundId: number;
    skuId: number;
    supplierId: number;
    expectedQty: number;
    receivedQty: number;
    rejectedQty: number;
    damagedQty: number;
    batchNo?: string;
    productionDate?: string;
    shelfLifeDays?: number;
    diffReason?: string;
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

export interface PutawayTaskDTO {
    id: number;
    putawayNo: string;
    inboundId: number;
    skuId: number;
    warehouseId: number;
    putawayQty: number;
    actualQty?: number;
    status: number;
}

export interface PickTaskDTO {
    id: number;
    pickNo: string;
    warehouseId: number;
    lineId: number;
    requiredQty?: number;
    actualQty?: number;
    shortageQty?: number;
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

function buildSearch(query: Record<string, string | number | undefined>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return search.toString();
}

export function pageInboundOrders(query: Record<string, string | number | undefined>, options: WarehouseRequestOptions = {}) {
    return request<PageResult<InboundOrderDTO>>(`/wms/inbound/orders?${buildSearch(query)}`, options);
}

export function pageInboundItems(query: Record<string, string | number | undefined>, options: WarehouseRequestOptions = {}) {
    return request<PageResult<InboundItemDTO>>(`/wms/inbound/items?${buildSearch(query)}`, options);
}

export function pagePutawayTasks(query: Record<string, string | number | undefined>, options: WarehouseRequestOptions = {}) {
    return request<PageResult<PutawayTaskDTO>>(`/wms/inbound/putaway-tasks?${buildSearch(query)}`, options);
}

export function pageInventories(query: Record<string, string | number | undefined>, options: WarehouseRequestOptions = {}) {
    return request<PageResult<InventoryDTO>>(`/wms/inventory/stocks?${buildSearch(query)}`, options);
}

export function pagePickTasks(query: Record<string, string | number | undefined>, options: WarehouseRequestOptions = {}) {
    return request<PageResult<PickTaskDTO>>(`/wms/outbound/picks?${buildSearch(query)}`, options);
}

export function pageDeliveryOrders(query: Record<string, string | number | undefined>, options: WarehouseRequestOptions = {}) {
    return request<PageResult<DeliveryOrderDTO>>(`/wms/outbound/deliveries?${buildSearch(query)}`, options);
}
