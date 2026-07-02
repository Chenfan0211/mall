import { computed, reactive } from 'vue';

import { buildFallbackCartItems } from '@/utils/userFallbackData';

export const USER_MAIN_TABS = [
    { key: 'home', label: '首页', icon: '⌂', url: '/pages/home/index' },
    { key: 'category', label: '分类', icon: '▦', url: '/pages/category/index' },
    { key: 'cart', label: '购物车', icon: '车', url: '/pages/cart/index', auth: true },
    { key: 'mine', label: '我的', icon: '♙', url: '/pages/mine/index' }
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

export interface UserLocationPoint {
    latitude: number;
    longitude: number;
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

export interface UserStationDTO {
    id: number;
    stationNo?: string;
    stationName: string;
    cityId: number;
    address: string;
    contactName?: string;
    contactMobile?: string;
    businessHours?: string;
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
    availableQty: number;
    skuCount?: number;
    availableSkuCount?: number;
    minSalePrice?: string | number;
    maxSalePrice?: string | number;
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
    cities: CityState[];
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

const DEFAULT_CITIES: CityState[] = [
    { id: 440100, name: '广州' },
    { id: 440300, name: '深圳' },
    { id: 310100, name: '上海' },
    { id: 330100, name: '杭州' },
    { id: 510100, name: '成都' },
    { id: 420100, name: '武汉' },
    { id: 110100, name: '北京' }
];

const SERVICE_SCOPE_KEY = 'mall_user_h5_service_scope';

const CITY_LOCATION_POINTS: Record<number, UserLocationPoint> = {
    440100: { latitude: 23.1291, longitude: 113.2644 },
    440300: { latitude: 22.5431, longitude: 114.0579 },
    310100: { latitude: 31.2304, longitude: 121.4737 },
    330100: { latitude: 30.2741, longitude: 120.1551 },
    510100: { latitude: 30.5728, longitude: 104.0668 },
    420100: { latitude: 30.5928, longitude: 114.3055 },
    110100: { latitude: 39.9042, longitude: 116.4074 }
};

const CITY_MATCH_MAX_DISTANCE_KM = 80;

const CITY_NAME_MAP = DEFAULT_CITIES.reduce<Record<number, string>>((map, city) => {
    map[city.id] = city.name;
    return map;
}, {});

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
        id: 720004,
        name: '番禺万博社区店',
        cityId: 440100,
        address: '广州市番禺区汉溪大道 86 号',
        mobile: '13600007104',
        businessHours: '08:30-21:30',
        status: 1,
        distance: '6.1km',
        deliveryTime: '今日 20:00 前截单'
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
    },
    {
        id: 730001,
        name: '南山科技园自提点',
        cityId: 440300,
        address: '深圳市南山区科苑南路 15 号',
        mobile: '13800003001',
        businessHours: '09:00-21:00',
        status: 1,
        distance: '2.4km',
        deliveryTime: '明日 17:00-20:00'
    },
    {
        id: 730002,
        name: '福田香蜜湖店',
        cityId: 440300,
        address: '深圳市福田区香梅路 1068 号',
        mobile: '13800003002',
        businessHours: '08:30-20:30',
        status: 1,
        distance: '5.7km',
        deliveryTime: '今日 19:30 前截单'
    },
    {
        id: 730003,
        name: '宝安中心休整点',
        cityId: 440300,
        address: '深圳市宝安区新湖路 99 号',
        mobile: '13800003003',
        businessHours: '暂停服务',
        status: 2,
        distance: '8.9km',
        deliveryTime: '暂停提货',
        abnormalReason: '门店盘点休整，暂不可选'
    },
    {
        id: 740001,
        name: '浦东世纪公园店',
        cityId: 310100,
        address: '上海市浦东新区锦绣路 888 号',
        mobile: '13800004001',
        businessHours: '09:00-21:00',
        status: 1,
        distance: '1.6km',
        deliveryTime: '明日 16:30-20:30'
    },
    {
        id: 740002,
        name: '静安大宁社区点',
        cityId: 310100,
        address: '上海市静安区共和新路 1898 号',
        mobile: '13800004002',
        businessHours: '09:30-20:30',
        status: 1,
        distance: '4.1km',
        deliveryTime: '今日 19:00 前截单'
    },
    {
        id: 740003,
        name: '徐汇滨江休假点',
        cityId: 310100,
        address: '上海市徐汇区龙腾大道 2555 号',
        mobile: '13800004003',
        businessHours: '暂停服务',
        status: 2,
        distance: '7.2km',
        deliveryTime: '暂停提货',
        abnormalReason: '团长休假中，暂不可选'
    },
    {
        id: 750001,
        name: '西湖文三路店',
        cityId: 330100,
        address: '杭州市西湖区文三路 478 号',
        mobile: '13800005001',
        businessHours: '09:00-20:30',
        status: 1,
        distance: '2.0km',
        deliveryTime: '明日 16:00-19:30'
    },
    {
        id: 750002,
        name: '滨江星光社区点',
        cityId: 330100,
        address: '杭州市滨江区江南大道 228 号',
        mobile: '13800005002',
        businessHours: '08:30-21:00',
        status: 1,
        distance: '5.5km',
        deliveryTime: '今日 20:30 前截单'
    },
    {
        id: 760001,
        name: '高新天府三街店',
        cityId: 510100,
        address: '成都市高新区天府三街 218 号',
        mobile: '13800006001',
        businessHours: '09:00-21:00',
        status: 1,
        distance: '3.3km',
        deliveryTime: '明日 17:00-20:30'
    },
    {
        id: 760002,
        name: '锦江春熙社区点',
        cityId: 510100,
        address: '成都市锦江区红星路三段 99 号',
        mobile: '13800006002',
        businessHours: '09:30-20:30',
        status: 1,
        distance: '6.4km',
        deliveryTime: '今日 18:30 前截单'
    },
    {
        id: 760003,
        name: '青羊优品休整点',
        cityId: 510100,
        address: '成都市青羊区光华村街 66 号',
        mobile: '13800006003',
        businessHours: '暂停服务',
        status: 2,
        distance: '9.8km',
        deliveryTime: '暂停提货',
        abnormalReason: '冷柜维护中，暂不可选'
    },
    {
        id: 770001,
        name: '光谷软件园店',
        cityId: 420100,
        address: '武汉市洪山区关山大道 1 号',
        mobile: '13800007001',
        businessHours: '09:00-21:00',
        status: 1,
        distance: '2.7km',
        deliveryTime: '明日 16:00-20:00'
    },
    {
        id: 770002,
        name: '江汉路邻里点',
        cityId: 420100,
        address: '武汉市江汉区中山大道 818 号',
        mobile: '13800007002',
        businessHours: '09:00-20:00',
        status: 1,
        distance: '4.9km',
        deliveryTime: '今日 19:00 前截单'
    },
    {
        id: 780001,
        name: '朝阳望京自提点',
        cityId: 110100,
        address: '北京市朝阳区阜通东大街 12 号',
        mobile: '13800008001',
        businessHours: '09:00-21:00',
        status: 1,
        distance: '3.0km',
        deliveryTime: '明日 17:00-20:00'
    },
    {
        id: 780002,
        name: '海淀五道口店',
        cityId: 110100,
        address: '北京市海淀区成府路 28 号',
        mobile: '13800008002',
        businessHours: '09:30-20:30',
        status: 1,
        distance: '5.2km',
        deliveryTime: '今日 19:30 前截单'
    },
    {
        id: 780003,
        name: '丰台云岗休假点',
        cityId: 110100,
        address: '北京市丰台区云岗路 8 号',
        mobile: '13800008003',
        businessHours: '暂停服务',
        status: 2,
        distance: '10.6km',
        deliveryTime: '暂停提货',
        abnormalReason: '团长临时休假，暂不可选'
    }
];

function storageApi() {
    return (globalThis as unknown as {
        uni?: {
            getStorageSync(key: string): unknown;
            setStorageSync(key: string, value: unknown): void;
        };
    }).uni;
}

function readStoredServiceScope() {
    try {
        const stored = storageApi()?.getStorageSync(SERVICE_SCOPE_KEY) as
            | { cityId?: number; stationId?: number }
            | string
            | undefined;
        if (!stored) {
            return undefined;
        }
        if (typeof stored === 'string') {
            return JSON.parse(stored) as { cityId?: number; stationId?: number };
        }
        return stored;
    } catch {
        return undefined;
    }
}

function saveServiceScope(state: UserState) {
    try {
        storageApi()?.setStorageSync(SERVICE_SCOPE_KEY, {
            cityId: state.city.id,
            stationId: state.station.id
        });
    } catch {
        // Local test runners do not provide uni storage.
    }
}

function initialCityAndStation() {
    const stored = readStoredServiceScope();
    const storedStation = DEFAULT_STATIONS.find((item) => item.id === stored?.stationId && item.status === 1);
    if (storedStation) {
        const city = DEFAULT_CITIES.find((item) => item.id === storedStation.cityId) || DEFAULT_CITIES[0];
        return {
            city,
            station: storedStation
        };
    }
    const city = DEFAULT_CITIES.find((item) => item.id === stored?.cityId) || DEFAULT_CITIES[0];
    const station = DEFAULT_STATIONS.find((item) => item.cityId === city.id && item.status === 1) || DEFAULT_STATIONS[0];
    return {
        city,
        station
    };
}

export function createDefaultUserState(): UserState {
    const initialScope = initialCityAndStation();
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
        city: { ...initialScope.city },
        cities: DEFAULT_CITIES.map((item) => ({ ...item })),
        station: { ...initialScope.station },
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
            getLocation?(options: {
                type?: string;
                success?: (res: UserLocationPoint) => void;
                fail?: () => void;
            }): void;
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
        if (!navigateUserH5('/pages/login/index')) {
            uniApi()?.navigateTo({ url: '/pages/login/index' });
        }
        return false;
    }
    return true;
}

function navigateUserH5(url: string) {
    if (typeof window === 'undefined') {
        return false;
    }
    const nextHash = `#${url}`;
    if (window.location.hash === nextHash) {
        return true;
    }
    window.location.hash = nextHash;
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
        if (navigateUserH5(url)) {
            return;
        }
        api.redirectTo({
            url: path,
            fail() {
                api.navigateTo({ url: path });
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
    if (userState.cartItems.length === 0) {
        userState.cartItems = buildFallbackCartItems();
    }
    showUserToast(authorizeMobile ? '已登录并授权手机号' : '已登录，手机号授权失败后可浏览但不能下单');
}

export function logoutUser() {
    userState.authenticated = false;
    userState.mobileAuthorized = false;
    showUserToast('已退出登录');
}

function cityNameOf(state: UserState, cityId: number) {
    return state.cities.find((item) => item.id === cityId)?.name || CITY_NAME_MAP[cityId] || state.city.name;
}

function cityNameFromId(cityId: number) {
    return CITY_NAME_MAP[cityId] || `城市${cityId}`;
}

export function mapStationFromApi(item: UserStationDTO): StationState {
    return {
        id: Number(item.id),
        name: item.stationName || item.stationNo || `自提点${item.id}`,
        cityId: Number(item.cityId),
        address: item.address || '地址待完善',
        mobile: item.contactMobile || '',
        businessHours: item.businessHours || '营业时间待定',
        status: Number(item.status),
        distance: '附近',
        deliveryTime: Number(item.status) === 1 ? '明日 16:00-20:00' : '暂停提货',
        abnormalReason: Number(item.status) === 1 ? undefined : '当前自提点休假或停用，暂不可选'
    };
}

export function buildCitiesFromStations(stations: StationState[]) {
    const cityMap = new Map<number, CityState>();
    stations.forEach((station) => {
        if (!cityMap.has(station.cityId)) {
            cityMap.set(station.cityId, {
                id: station.cityId,
                name: cityNameFromId(station.cityId)
            });
        }
    });
    return Array.from(cityMap.values());
}

export function syncStationsFromApi(state: UserState, stationDtos: UserStationDTO[]) {
    const stations = stationDtos.map(mapStationFromApi).filter((item) => item.id > 0 && item.cityId > 0);
    if (stations.length === 0) {
        return {
            synced: false,
            fallback: true,
            message: '接口暂无自提点数据，已展示本地兜底数据'
        };
    }

    const cities = buildCitiesFromStations(stations);
    const currentStation = stations.find((item) => item.id === state.station.id && item.status === 1);
    const currentCity = cities.find((item) => item.id === state.city.id);
    const stored = readStoredServiceScope();
    const storedStation = stations.find((item) => item.id === stored?.stationId && item.status === 1);
    const storedCity = cities.find((item) => item.id === stored?.cityId);
    const nextCity = currentCity || (storedStation && cities.find((item) => item.id === storedStation.cityId)) || storedCity || cities[0];
    const nextStation =
        currentStation ||
        storedStation ||
        stations.find((item) => item.cityId === nextCity?.id && item.status === 1) ||
        stations.find((item) => item.status === 1) ||
        stations[0];

    state.cities = cities;
    state.stations = stations;
    if (nextStation) {
        state.city = {
            id: nextStation.cityId,
            name: cityNameFromId(nextStation.cityId)
        };
        state.station = { ...nextStation };
        if (nextStation.status === 1) {
            saveServiceScope(state);
        }
    } else if (nextCity) {
        state.city = { ...nextCity };
    }

    return {
        synced: true,
        fallback: false,
        message: '已同步后端自提点'
    };
}

export function resetStationsToFallback(state: UserState) {
    const fallbackState = createDefaultUserState();
    state.cities = fallbackState.cities;
    state.stations = fallbackState.stations;
    state.city = fallbackState.city;
    state.station = fallbackState.station;
    return {
        synced: false,
        fallback: true,
        message: '接口不可用，已展示本地兜底数据'
    };
}

function distanceKm(from: UserLocationPoint, to: UserLocationPoint) {
    const radius = 6371;
    const toRadians = (value: number) => (value * Math.PI) / 180;
    const deltaLatitude = toRadians(to.latitude - from.latitude);
    const deltaLongitude = toRadians(to.longitude - from.longitude);
    const fromLatitude = toRadians(from.latitude);
    const toLatitude = toRadians(to.latitude);
    const factor =
        Math.sin(deltaLatitude / 2) ** 2 +
        Math.cos(fromLatitude) * Math.cos(toLatitude) * Math.sin(deltaLongitude / 2) ** 2;
    return radius * 2 * Math.atan2(Math.sqrt(factor), Math.sqrt(1 - factor));
}

export function resolveNearestOpenCity(cities: CityState[], location?: UserLocationPoint, stations: StationState[] = []) {
    const openCities = cities.filter((city) => stations.some((station) => station.cityId === city.id && station.status === 1));
    const candidates = openCities.length ? openCities : cities;
    if (!location) {
        return candidates[0];
    }
    const nearest = candidates
        .map((city) => {
            const point = CITY_LOCATION_POINTS[city.id];
            return point ? { city, distance: distanceKm(location, point) } : undefined;
        })
        .filter((item): item is { city: CityState; distance: number } => Boolean(item))
        .sort((left, right) => left.distance - right.distance)[0];
    return nearest && nearest.distance <= CITY_MATCH_MAX_DISTANCE_KM ? nearest.city : candidates[0];
}

export function applyLocatedCity(state: UserState, location?: UserLocationPoint) {
    const city = resolveNearestOpenCity(state.cities, location, state.stations);
    if (!city) {
        return {
            switched: false,
            clearedCart: false,
            message: '暂无可用城市'
        };
    }
    return switchCity(state, city);
}

export function locateCurrentCity() {
    const api = uniApi();
    let settled = false;
    let timer: ReturnType<typeof setTimeout> | undefined;
    const applyOnce = (location?: UserLocationPoint) => {
        if (settled) {
            return;
        }
        settled = true;
        if (timer) {
            clearTimeout(timer);
        }
        applyLocatedCity(userState, location);
    };
    if (!api?.getLocation) {
        return applyOnce();
    }
    timer = setTimeout(() => applyOnce(), 1500);
    api.getLocation({
        type: 'gcj02',
        success(res) {
            applyOnce(res);
        },
        fail() {
            applyOnce();
        }
    });
}

export function switchCity(state: UserState, next: CityState) {
    if (state.city.id === next.id && state.station.cityId === next.id) {
        return {
            switched: false,
            clearedCart: false,
            message: `当前已是 ${next.name}`
        };
    }
    const nextStation = state.stations.find((item) => item.cityId === next.id && item.status === 1);
    if (!nextStation) {
        const changed = state.city.id !== next.id;
        state.city = { ...next };
        return {
            switched: changed,
            clearedCart: false,
            message: `${next.name} 暂无可选自提点`
        };
    }
    const currentCityId = state.city.id;
    const currentStationId = state.station.id;
    const changed = currentCityId !== next.id || currentStationId !== nextStation.id;
    const clearedCart = changed && state.cartItems.some((item) => item.valid && item.qty > 0);
    state.city = { ...next };
    state.station = { ...nextStation };
    if (clearedCart) {
        state.cartItems = [];
    }
    saveServiceScope(state);
    return {
        switched: changed,
        clearedCart,
        message: clearedCart ? `已切换至 ${next.name}，购物车已清空` : `已切换至 ${next.name}`
    };
}

export function switchCurrentCity(next: CityState) {
    const result = switchCity(userState, next);
    showUserToast(result.message, result.switched ? 'success' : 'warn');
    return result;
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
    state.city = {
        id: next.cityId,
        name: cityNameOf(state, next.cityId)
    };
    if (clearedCart) {
        state.cartItems = [];
    }
    saveServiceScope(state);
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

export function updateReceiver(state: UserState, id: number, name: string, mobile: string) {
    const receiver = state.receivers.find((item) => item.id === id);
    if (!receiver) {
        return undefined;
    }
    receiver.name = name;
    receiver.mobile = mobile;
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
