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

export function pageInboundOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<InboundOrderDTO>>(`/wms/inbound/orders?${buildSearch(query)}`);
}

export function pagePutawayTasks(query: Record<string, string | number | undefined>) {
    return request<PageResult<PutawayTaskDTO>>(`/wms/inbound/putaway-tasks?${buildSearch(query)}`);
}

export function pageInventories(query: Record<string, string | number | undefined>) {
    return request<PageResult<InventoryDTO>>(`/wms/inventory/stocks?${buildSearch(query)}`);
}

export function pagePickTasks(query: Record<string, string | number | undefined>) {
    return request<PageResult<PickTaskDTO>>(`/wms/outbound/picks?${buildSearch(query)}`);
}

export function pageDeliveryOrders(query: Record<string, string | number | undefined>) {
    return request<PageResult<DeliveryOrderDTO>>(`/wms/outbound/deliveries?${buildSearch(query)}`);
}
