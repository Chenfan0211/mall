import { request } from '@/utils/request';

export interface PageResult<T> {
    total: number;
    list: T[];
}

export interface HomeSummary {
    userId: number;
    cityId?: number;
    stationId?: number;
    onlineProductCount: number;
    cartItemCount: number;
    waitPayOrderCount: number;
    waitPickupOrderCount: number;
    unreadMessageCount: number;
}

export interface UserStationDTO {
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

export interface UserCategoryDTO {
    id: number;
    categoryName: string;
    parentId?: number;
    level?: number;
    sortNo?: number;
    status: number;
}

export interface UserProductCardDTO {
    publishSkuId: number;
    periodId: number;
    productId: number;
    skuId: number;
    productName: string;
    skuName: string;
    mainImageUrl?: string;
    saleSpecText?: string;
    salePrice: string;
    limitQty: number;
    minBuyQty: number;
    plannedStockQty: number;
    soldQty: number;
    lockedQty: number;
    availableQty: number;
    deliveryDate?: string;
    saleEndTime?: string;
}

export interface UserProductSkuDTO {
    publishSkuId: number;
    periodId: number;
    skuId: number;
    skuName: string;
    saleSpecText?: string;
    baseUnit?: string;
    salePrice: string;
    limitQty: number;
    minBuyQty: number;
    availableQty: number;
    deliveryDate?: string;
}

export interface UserProductDetailDTO {
    product?: {
        id: number;
        productNo: string;
        productName: string;
        mainImageUrl?: string;
        auditStatus: number;
        saleStatus: number;
    };
    skus: UserProductSkuDTO[];
    favoriteFlag: number;
    commentCount: number;
}

export interface UserCommentDTO {
    id: number;
    commentNo: string;
    userId: number;
    productId: number;
    skuId: number;
    stationId: number;
    score: number;
    content: string;
    imageJson?: string;
    status: number;
    createTime?: string;
}

export interface UserCartItemDTO {
    id: number;
    userId: number;
    cityId: number;
    stationId: number;
    periodId: number;
    productId: number;
    skuId: number;
    productName: string;
    skuName: string;
    mainImageUrl?: string;
    salePrice: string;
    qty: number;
    selectedFlag: number;
    validStatus: number;
    updateTime?: string;
}

export interface UserOrderDTO {
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

export interface UserOrderItemDTO {
    id: number;
    orderId: number;
    orderNo: string;
    orderItemNo: string;
    productName: string;
    skuName: string;
    salePrice: string;
    qty: number;
    pickedQty: number;
    refundedQty: number;
    itemAmount: string;
    fulfillStatus: number;
    afterSaleStatus: number;
    expectedPickupDate?: string;
}

export interface UserTrackDTO {
    id: number;
    status: number;
    title: string;
    content: string;
    createTime?: string;
}

export interface UserPaymentTradeDTO {
    id: number;
    paymentNo: string;
    orderId: number;
    payAmount: string;
    payChannel: number;
    tradeStatus: number;
}

export interface UserMessageDTO {
    id: number;
    msgNo: string;
    title: string;
    bizType: string;
    bizNo: string;
    readStatus: number;
    sendStatus: number;
    createTime?: string;
}

export interface UserAfterSaleDTO {
    id: number;
    afterSaleNo: string;
    orderId: number;
    orderNo: string;
    userId: number;
    cityId: number;
    stationId: number;
    afterSaleType: number;
    status: number;
    applyReason?: string;
    refundAmount: string;
    responsibilityType: number;
    createTime?: string;
}

export interface UserAfterSaleItemDTO {
    id: number;
    afterSaleId: number;
    orderItemId: number;
    skuId: number;
    supplierId: number;
    applyQty: number;
    approvedQty: number;
    refundAmount: string;
}

export interface UserReturnRecordDTO {
    id: number;
    returnNo: string;
    afterSaleId: number;
    orderItemId: number;
    skuId: number;
    stationId: number;
    warehouseId: number;
    returnQty: number;
    returnStatus: number;
    proofFileJson?: string;
    createTime?: string;
}

type QueryValue = string | number | undefined;
type PayloadValue = string | number | number[] | undefined;

function buildSearch(query: Record<string, QueryValue>) {
    const search = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
            search.set(key, String(value));
        }
    });
    return search.toString();
}

export function getHomeSummary(query: Record<string, QueryValue>) {
    return request<HomeSummary>(`/user/home/summary?${buildSearch(query)}`);
}

export function pageStations(query: Record<string, QueryValue>) {
    return request<PageResult<UserStationDTO>>(`/user/stations?${buildSearch(query)}`);
}

export function pageCategories(query: Record<string, QueryValue>) {
    return request<PageResult<UserCategoryDTO>>(`/user/products/categories?${buildSearch(query)}`);
}

export function pageProducts(query: Record<string, QueryValue>) {
    return request<PageResult<UserProductCardDTO>>(`/user/products?${buildSearch(query)}`);
}

export function getProductDetail(id: number, query: Record<string, QueryValue>) {
    return request<UserProductDetailDTO>(`/user/products/${id}?${buildSearch(query)}`);
}

export function pageComments(query: Record<string, QueryValue>) {
    return request<PageResult<UserCommentDTO>>(`/user/products/comments?${buildSearch(query)}`);
}

export function pageCartItems(query: Record<string, QueryValue>) {
    return request<PageResult<UserCartItemDTO>>(`/user/cart/items?${buildSearch(query)}`);
}

export function addCartItem(payload: Record<string, PayloadValue>) {
    return request<UserCartItemDTO>('/user/cart/items', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}

export function updateCartItem(id: number, payload: Record<string, PayloadValue>) {
    return request<UserCartItemDTO>(`/user/cart/items/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
    });
}

export function pageOrders(query: Record<string, QueryValue>) {
    return request<PageResult<UserOrderDTO>>(`/user/orders?${buildSearch(query)}`);
}

export function submitOrder(payload: Record<string, PayloadValue>) {
    return request<UserOrderDTO>('/user/orders', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}

export function pageOrderItems(query: Record<string, QueryValue>) {
    return request<PageResult<UserOrderItemDTO>>(`/user/orders/items?${buildSearch(query)}`);
}

export function listFulfillmentTracks(id: number, query: Record<string, QueryValue>) {
    return request<UserTrackDTO[]>(`/user/orders/${id}/tracks?${buildSearch(query)}`);
}

export function createPaymentTrade(id: number, payload: Record<string, PayloadValue>) {
    return request<UserPaymentTradeDTO>(`/user/orders/${id}/payments`, {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}

export function pageAfterSales(query: Record<string, QueryValue>) {
    return request<PageResult<UserAfterSaleDTO>>(`/user/after-sales?${buildSearch(query)}`);
}

export function getAfterSale(id: number, query: Record<string, QueryValue>) {
    return request<UserAfterSaleDTO>(`/user/after-sales/${id}?${buildSearch(query)}`);
}

export function listAfterSaleItems(id: number, query: Record<string, QueryValue>) {
    return request<UserAfterSaleItemDTO[]>(`/user/after-sales/${id}/items?${buildSearch(query)}`);
}

export function listReturnRecords(id: number, query: Record<string, QueryValue>) {
    return request<UserReturnRecordDTO[]>(`/user/after-sales/${id}/returns?${buildSearch(query)}`);
}

export function pageMessages(query: Record<string, QueryValue>) {
    return request<PageResult<UserMessageDTO>>(`/user/messages?${buildSearch(query)}`);
}
