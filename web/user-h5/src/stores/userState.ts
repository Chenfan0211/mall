import { computed, reactive } from 'vue';

export const USER_MAIN_TABS = [
    { key: 'home', label: '首页', icon: '⌂', url: '/pages/home/index' },
    { key: 'category', label: '分类', icon: '☷', url: '/pages/category/index' },
    { key: 'cart', label: '购物车', icon: '车', url: '/pages/cart/index', auth: true },
    { key: 'order', label: '订单', icon: '单', url: '/pages/order/index', auth: true },
    { key: 'mine', label: '我的', icon: '我', url: '/pages/mine/index', auth: true }
] as const;

export interface UserSession {
    id: number;
    nickname: string;
    mobile: string;
    avatarText: string;
}

export interface CityState {
    id: number;
    name: string;
}

export interface StationState {
    id: number;
    name: string;
    cityId: number;
    address: string;
    mobile: string;
    businessHours: string;
    status: number;
    distance?: string;
    deliveryTime?: string;
    abnormalReason?: string;
}

export interface CartViewItem {
    id: number;
    productId?: number;
    periodId?: number;
    skuId?: number;
    publishSkuId?: number;
    title: string;
    spec?: string;
    image?: string;
    price: string;
    qty: number;
    selected: boolean;
    valid: boolean;
    stock?: number;
    limit?: number;
    pickupDate?: string;
    invalidReason?: string;
    priceChanged?: boolean;
}

export interface ReceiverState {
    id: number;
    name: string;
    mobile: string;
    defaultFlag: boolean;
}

export interface LocalOrderItem {
    id: number;
    productId: number;
    productName: string;
    skuName: string;
    image?: string;
    salePrice: string;
    qty: number;
    refundableQty: number;
    afterSaleStatus: number;
    expectedPickupDate: string;
}

export interface LocalOrder {
    id: number;
    orderNo: string;
    stationId: number;
    stationName: string;
    pickupName: string;
    pickupMobile: string;
    tradeStatus: number;
    payStatus: number;
    fulfillStatus: number;
    totalAmount: string;
    payAmount: string;
    createTime: string;
    remark?: string;
    items: LocalOrderItem[];
    timeline: Array<{
        title: string;
        content: string;
        time: string;
    }>;
}

export interface LocalReview {
    id: number;
    orderNo: string;
    productId: number;
    productName: string;
    skuName: string;
    score: number;
    content: string;
    images: string[];
    status: number;
    createTime: string;
}

export interface LocalMessage {
    id: number;
    category: string;
    title: string;
    content: string;
    read: boolean;
    bizUrl?: string;
    createTime: string;
}

export interface UserCartItemDTO {
    id: number;
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
    availableQty: number;
    deliveryDate?: string;
}

export interface UserProductSkuDTO {
    publishSkuId: number;
    periodId: number;
    skuId: number;
    skuName: string;
    saleSpecText?: string;
    salePrice: string;
    limitQty: number;
    availableQty: number;
    deliveryDate?: string;
}

export interface UserState {
    authenticated: boolean;
    mobileAuthorized: boolean;
    agreementAccepted: boolean;
    afterLoginUrl: string;
    user: UserSession;
    city: CityState;
    station: StationState;
    stations: StationState[];
    receivers: ReceiverState[];
    cartItems: CartViewItem[];
    localOrders: LocalOrder[];
    localReviews: LocalReview[];
    localMessages: LocalMessage[];
    favorites: Set<number>;
    notices: Set<number>;
    searchHistory: string[];
    toast: {
        visible: boolean;
        text: string;
        type: 'success' | 'warn' | 'loading';
    };
}

const DEFAULT_STATIONS: StationState[] = [
    {
        id: 720001,
        name: '海珠测试自提点',
        cityId: 440100,
        address: '广州市海珠区测试路1号',
        mobile: '13800002001',
        businessHours: '09:00-20:00',
        status: 1,
        distance: '1.8km',
        deliveryTime: '明日 16:00-20:00'
    },
    {
        id: 720002,
        name: '天河体育西店',
        cityId: 440100,
        address: '广州市天河区体育西路 52 号',
        mobile: '13900006402',
        businessHours: '09:00-20:30',
        status: 1,
        distance: '3.2km',
        deliveryTime: '明日 18:00-21:00'
    },
    {
        id: 720003,
        name: '王府花园休假点',
        cityId: 440100,
        address: '广州市越秀区王府花园 3 栋',
        mobile: '13700003185',
        businessHours: '暂停服务',
        status: 2,
        distance: '4.6km',
        deliveryTime: '暂停提货',
        abnormalReason: '当前自提点休假或停用，暂不可选'
    }
];

export function createDefaultUserState(): UserState {
    return {
        authenticated: false,
        mobileAuthorized: false,
        agreementAccepted: false,
        afterLoginUrl: '/pages/mine/index',
        user: {
            id: 740001,
            nickname: '义炎',
            mobile: '13800004101',
            avatarText: '炎'
        },
        city: {
            id: 440100,
            name: '广州'
        },
        station: { ...DEFAULT_STATIONS[0] },
        stations: DEFAULT_STATIONS.map((item) => ({ ...item })),
        receivers: [
            { id: 740101, name: '默认提货人', mobile: '13800004101', defaultFlag: true },
            { id: 740102, name: '备用提货人', mobile: '13800004102', defaultFlag: false }
        ],
        cartItems: [],
        localOrders: [
            {
                id: 880001,
                orderNo: 'DD202606270001',
                stationId: 720001,
                stationName: '海珠测试自提点',
                pickupName: '默认提货人',
                pickupMobile: '13800004101',
                tradeStatus: 10,
                payStatus: 10,
                fulfillStatus: 10,
                totalAmount: '58.80',
                payAmount: '58.80',
                createTime: '2026-06-27 10:06:18',
                remark: '晚一点到店提货',
                items: [
                    {
                        id: 990001,
                        productId: 610001,
                        productName: '赣南鲜橙家庭装',
                        skuName: '5斤装',
                        salePrice: '29.90',
                        qty: 1,
                        refundableQty: 1,
                        afterSaleStatus: 0,
                        expectedPickupDate: '2026-06-28'
                    },
                    {
                        id: 990002,
                        productId: 610002,
                        productName: '云南蓝莓大果',
                        skuName: '2盒装',
                        salePrice: '28.90',
                        qty: 1,
                        refundableQty: 1,
                        afterSaleStatus: 0,
                        expectedPickupDate: '2026-06-28'
                    }
                ],
                timeline: [
                    { title: '提交订单', content: '订单已创建，等待微信支付', time: '2026-06-27 10:06:18' },
                    { title: '库存锁定', content: '已锁定当前自提点可售库存 15 分钟', time: '2026-06-27 10:06:18' }
                ]
            },
            {
                id: 880002,
                orderNo: 'DD202606270002',
                stationId: 720001,
                stationName: '海珠测试自提点',
                pickupName: '默认提货人',
                pickupMobile: '13800004101',
                tradeStatus: 20,
                payStatus: 30,
                fulfillStatus: 60,
                totalAmount: '36.90',
                payAmount: '36.90',
                createTime: '2026-06-27 09:22:10',
                items: [
                    {
                        id: 990003,
                        productId: 610003,
                        productName: '安心鲜鸡蛋',
                        skuName: '30枚',
                        salePrice: '36.90',
                        qty: 1,
                        refundableQty: 1,
                        afterSaleStatus: 0,
                        expectedPickupDate: '2026-06-28'
                    }
                ],
                timeline: [
                    { title: '支付成功', content: '微信支付已完成', time: '2026-06-27 09:23:02' },
                    { title: '商品到店', content: '商品已到自提点，等待提货', time: '2026-06-27 16:20:00' }
                ]
            },
            {
                id: 880003,
                orderNo: 'DD202606260018',
                stationId: 720002,
                stationName: '天河体育西店',
                pickupName: '备用提货人',
                pickupMobile: '13800004102',
                tradeStatus: 50,
                payStatus: 30,
                fulfillStatus: 70,
                totalAmount: '42.80',
                payAmount: '42.80',
                createTime: '2026-06-26 18:12:44',
                items: [
                    {
                        id: 990004,
                        productId: 610004,
                        productName: '早餐牛奶组合',
                        skuName: '牛奶+吐司',
                        salePrice: '42.80',
                        qty: 1,
                        refundableQty: 1,
                        afterSaleStatus: 0,
                        expectedPickupDate: '2026-06-27'
                    }
                ],
                timeline: [
                    { title: '订单完成', content: '用户已确认收货，可评价或申请售后', time: '2026-06-27 19:02:00' }
                ]
            },
            {
                id: 880004,
                orderNo: 'DD202606250009',
                stationId: 720001,
                stationName: '海珠测试自提点',
                pickupName: '默认提货人',
                pickupMobile: '13800004101',
                tradeStatus: 40,
                payStatus: 40,
                fulfillStatus: 70,
                totalAmount: '19.90',
                payAmount: '19.90',
                createTime: '2026-06-25 12:14:30',
                items: [
                    {
                        id: 990005,
                        productId: 610005,
                        productName: '小番茄盒装',
                        skuName: '500g',
                        salePrice: '19.90',
                        qty: 1,
                        refundableQty: 1,
                        afterSaleStatus: 10,
                        expectedPickupDate: '2026-06-26'
                    }
                ],
                timeline: [
                    { title: '提交售后', content: '商品破损，售后待审核', time: '2026-06-26 20:15:00' }
                ]
            }
        ],
        localReviews: [
            {
                id: 770001,
                orderNo: 'DD202606260018',
                productId: 610004,
                productName: '早餐牛奶组合',
                skuName: '牛奶+吐司',
                score: 5,
                content: '提货方便，吐司很新鲜。',
                images: [],
                status: 1,
                createTime: '2026-06-27 20:18:00'
            },
            {
                id: 770002,
                orderNo: 'DD202606240009',
                productId: 610006,
                productName: '脆甜香梨',
                skuName: '3斤装',
                score: 2,
                content: '有一颗磕碰，已申请售后。',
                images: [],
                status: 1,
                createTime: '2026-06-25 21:03:00'
            }
        ],
        localMessages: [
            {
                id: 660001,
                category: '订单',
                title: '订单待支付',
                content: 'DD202606270001 将在 15 分钟后关闭，请尽快完成支付。',
                read: false,
                bizUrl: '/pages/order/detail?id=880001',
                createTime: '2026-06-27 10:06:18'
            },
            {
                id: 660002,
                category: '提货',
                title: '商品已到自提点',
                content: 'DD202606270002 已到海珠测试自提点，可到店提货。',
                read: false,
                bizUrl: '/pages/order/detail?id=880002',
                createTime: '2026-06-27 16:20:00'
            },
            {
                id: 660003,
                category: '售后',
                title: '售后申请已提交',
                content: '小番茄盒装售后申请已进入审核。',
                read: true,
                bizUrl: '/pages/aftersale/detail?id=1',
                createTime: '2026-06-26 20:15:00'
            }
        ],
        favorites: new Set<number>(),
        notices: new Set<number>(),
        searchHistory: ['蓝莓', '苹果', '早餐组合'],
        toast: {
            visible: false,
            text: '',
            type: 'success'
        }
    };
}

const userState = reactive(createDefaultUserState());

function uniApi() {
    return (globalThis as unknown as {
        uni?: {
            navigateTo(options: { url: string; fail?: () => void }): void;
            redirectTo(options: { url: string; fail?: () => void }): void;
            switchTab(options: { url: string; fail?: () => void }): void;
            showToast(options: { title: string; icon: 'none'; duration?: number }): void;
        };
    }).uni;
}

export function useUserState() {
    return userState;
}

export const cartBadgeCount = computed(() =>
    userState.cartItems.filter((item) => item.valid).reduce((sum, item) => sum + item.qty, 0)
);

export function selectedCartItems(state: UserState = userState) {
    return state.cartItems.filter((item) => item.valid && item.selected);
}

export function invalidCartItems(state: UserState = userState) {
    return state.cartItems.filter((item) => !item.valid);
}

export function cartTotal(state: UserState = userState) {
    const amount = selectedCartItems(state).reduce((sum, item) => sum + Number(item.price || 0) * item.qty, 0);
    return amount.toFixed(2);
}

export function needsUserAuth(url: string) {
    return [
        '/pages/cart/index',
        '/pages/checkout/index',
        '/pages/payment/index',
        '/pages/order/index',
        '/pages/order/detail',
        '/pages/order/review',
        '/pages/aftersale/index',
        '/pages/aftersale/detail',
        '/pages/aftersale/apply',
        '/pages/message/index',
        '/pages/favorite/index',
        '/pages/history/index',
        '/pages/notice/index',
        '/pages/receivers/index',
        '/pages/reviews/mine',
        '/pages/station/apply',
        '/pages/settings/index'
    ].some((item) => url.startsWith(item));
}

export function guardUserPage(url: string) {
    if (needsUserAuth(url) && !userState.authenticated) {
        userState.afterLoginUrl = url;
        showUserToast('请先完成授权登录', 'warn');
        uniApi()?.navigateTo({ url: '/pages/login/index' });
        return false;
    }
    return true;
}

export function navigateUser(url: string, replace = false) {
    if (!guardUserPage(url)) {
        return;
    }
    const api = uniApi();
    if (!api) {
        return;
    }
    const path = url.split('?')[0];
    const tabTarget = USER_MAIN_TABS.some((item) => item.url === path);
    if (tabTarget) {
        api.switchTab({
            url: path,
            fail() {
                api.redirectTo({ url: path });
            }
        });
        return;
    }
    const navigate = replace ? api.redirectTo : api.navigateTo;
    navigate({
        url,
        fail() {
            api.navigateTo({ url });
        }
    });
}

export function goUserTab(key: string) {
    const tab = USER_MAIN_TABS.find((item) => item.key === key);
    if (!tab) {
        return;
    }
    navigateUser(tab.url, true);
}

export function loginUser(authorizeMobile = true) {
    userState.authenticated = true;
    userState.mobileAuthorized = authorizeMobile;
    showUserToast(authorizeMobile ? '已登录并授权手机号' : '已登录，手机号授权失败后可浏览但不能下单');
}

export function logoutUser() {
    userState.authenticated = false;
    userState.mobileAuthorized = false;
    showUserToast('已退出登录');
}

export function switchStation(state: UserState, next: StationState) {
    if (next.status !== 1) {
        return {
            switched: false,
            clearedCart: false,
            message: next.abnormalReason || '当前自提点休假或停用，暂不可选'
        };
    }
    const stationChanged = state.station.id !== next.id;
    const clearedCart = stationChanged && state.cartItems.some((item) => item.valid && item.qty > 0);
    state.station = { ...next };
    state.city.id = next.cityId;
    if (clearedCart) {
        state.cartItems = [];
    }
    return {
        switched: stationChanged,
        clearedCart,
        message: clearedCart ? `已切换至 ${next.name}，购物车已清空` : `已切换至 ${next.name}`
    };
}

export function switchCurrentStation(next: StationState) {
    const result = switchStation(userState, next);
    showUserToast(result.message, result.switched ? 'success' : 'warn');
    return result;
}

export function mapCartItem(item: UserCartItemDTO): CartViewItem {
    const valid = item.validStatus === 1;
    return {
        id: item.id,
        productId: item.productId,
        periodId: item.periodId,
        skuId: item.skuId,
        title: item.productName,
        spec: item.skuName,
        image: item.mainImageUrl,
        price: item.salePrice,
        qty: item.qty,
        selected: item.selectedFlag === 1 && valid,
        valid,
        pickupDate: item.updateTime,
        invalidReason: valid ? '' : '商品已售罄、下架或销售期结束'
    };
}

export function syncCartFromApi(items: UserCartItemDTO[]) {
    userState.cartItems = items.map(mapCartItem);
}

export function addProductToCart(product: UserProductCardDTO | UserProductSkuDTO, title?: string, productId?: number) {
    const publishSkuId = 'publishSkuId' in product ? product.publishSkuId : undefined;
    const skuId = 'skuId' in product ? product.skuId : undefined;
    const existing = userState.cartItems.find((item) => item.publishSkuId === publishSkuId && publishSkuId !== undefined);
    const stock = Number('availableQty' in product ? product.availableQty : 99);
    const limit = Number('limitQty' in product ? product.limitQty : 99);
    if (stock <= 0) {
        showUserToast('请先登录，登录后才能订阅到货提醒', 'warn');
        return undefined;
    }
    if (existing) {
        updateCartItemQty(userState, existing.id, existing.qty + 1);
        showUserToast('已更新购物车数量');
        return existing;
    }
    const localId = Date.now();
    const item: CartViewItem = {
        id: localId,
        productId,
        periodId: 'periodId' in product ? product.periodId : undefined,
        skuId,
        publishSkuId,
        title: title || ('productName' in product ? product.productName : '购物车商品'),
        spec: product.skuName || product.saleSpecText,
        image: 'mainImageUrl' in product ? product.mainImageUrl : undefined,
        price: product.salePrice,
        qty: 1,
        selected: true,
        valid: true,
        stock,
        limit,
        pickupDate: product.deliveryDate
    };
    userState.cartItems.unshift(item);
    showUserToast('成功加入购物车');
    return item;
}

export function toggleCartItem(state: UserState, id: number) {
    const item = state.cartItems.find((row) => row.id === id);
    if (!item || !item.valid) {
        return;
    }
    item.selected = !item.selected;
}

export function updateCartItemQty(state: UserState, id: number, qty: number) {
    const item = state.cartItems.find((row) => row.id === id);
    if (!item || !item.valid) {
        return;
    }
    const maxByStock = item.stock && item.stock > 0 ? item.stock : 99;
    const maxByLimit = item.limit && item.limit > 0 ? item.limit : 99;
    const nextQty = Math.max(1, Math.min(qty, maxByStock, maxByLimit));
    item.qty = nextQty;
}

export function removeCartItemOrAsk(state: UserState, id: number, nextQty: number) {
    if (nextQty <= 0) {
        return {
            requiresConfirm: true,
            message: '数量减到 0 将删除该商品'
        };
    }
    updateCartItemQty(state, id, nextQty);
    return {
        requiresConfirm: false,
        message: '数量已更新'
    };
}

export function removeCartItem(state: UserState, id: number) {
    state.cartItems = state.cartItems.filter((item) => item.id !== id);
}

export function clearInvalidCartItems(state: UserState = userState) {
    const count = invalidCartItems(state).length;
    state.cartItems = state.cartItems.filter((item) => item.valid);
    return count;
}

export function toggleSelectAllCartItems(state: UserState, selected: boolean) {
    state.cartItems.forEach((item) => {
        if (item.valid) {
            item.selected = selected;
        }
    });
}

export function toggleFavorite(productId: number) {
    if (userState.favorites.has(productId)) {
        userState.favorites.delete(productId);
        showUserToast('已取消收藏');
        return false;
    }
    userState.favorites.add(productId);
    showUserToast('已加入我的收藏');
    return true;
}

export function toggleNotice(productId: number) {
    if (!userState.authenticated) {
        showUserToast('请先登录，登录后才能订阅到货提醒', 'warn');
        return false;
    }
    if (userState.notices.has(productId)) {
        userState.notices.delete(productId);
        showUserToast('已取消到货提醒');
        return false;
    }
    userState.notices.add(productId);
    showUserToast('到货提醒已开启');
    return true;
}

export function rememberSearchKeyword(keyword: string) {
    const value = keyword.trim();
    if (!value) {
        return;
    }
    userState.searchHistory = [value, ...userState.searchHistory.filter((item) => item !== value)].slice(0, 8);
}

export function defaultReceiver() {
    return userState.receivers.find((item) => item.defaultFlag) || userState.receivers[0];
}

export function addReceiver(state: UserState, name: string, mobile: string) {
    const receiver: ReceiverState = {
        id: Date.now(),
        name,
        mobile,
        defaultFlag: state.receivers.length === 0
    };
    state.receivers.push(receiver);
    return receiver;
}

export function removeReceiver(state: UserState, id: number) {
    const target = state.receivers.find((item) => item.id === id);
    state.receivers = state.receivers.filter((item) => item.id !== id);
    if (target?.defaultFlag && state.receivers.length > 0) {
        state.receivers[0].defaultFlag = true;
    }
}

export function setDefaultReceiver(state: UserState, id: number) {
    state.receivers.forEach((item) => {
        item.defaultFlag = item.id === id;
    });
}

export function findLocalOrder(id?: number, orderNo?: string) {
    return userState.localOrders.find((item) => item.id === id || item.orderNo === orderNo);
}

export function markMessageRead(id: number) {
    const item = userState.localMessages.find((row) => row.id === id);
    if (item) {
        item.read = true;
    }
}

export function markAllMessagesRead(category?: string) {
    userState.localMessages.forEach((item) => {
        if (!category || category === '全部' || item.category === category) {
            item.read = true;
        }
    });
}

export function showUserToast(text: string, type: 'success' | 'warn' | 'loading' = 'success') {
    userState.toast.text = text;
    userState.toast.type = type;
    userState.toast.visible = true;
    uniApi()?.showToast({ title: text, icon: 'none', duration: type === 'loading' ? 900 : 1400 });
    setTimeout(() => {
        userState.toast.visible = false;
    }, type === 'loading' ? 900 : 1400);
}

export function statusText(type: 'trade' | 'fulfill' | 'afterSale' | 'pay', status?: number) {
    const maps: Record<string, Record<number, string>> = {
        trade: {
            10: '待付款',
            20: '已付款',
            30: '已取消',
            40: '退款中',
            50: '已完成'
        },
        pay: {
            10: '待支付',
            20: '支付处理中',
            30: '已支付',
            40: '退款中',
            50: '部分退款',
            60: '已退款'
        },
        fulfill: {
            10: '待付款',
            20: '待采购',
            30: '备货中',
            40: '拣货中',
            50: '配送中',
            60: '待提货',
            70: '已完成'
        },
        afterSale: {
            10: '待审核',
            20: '待退货',
            30: '退款中',
            40: '退款完成',
            50: '退款失败',
            60: '已驳回',
            70: '已撤销'
        }
    };
    return status !== undefined ? maps[type][status] || `状态${status}` : '未知状态';
}
