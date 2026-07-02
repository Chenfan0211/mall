import { fileURLToPath, URL } from 'node:url';
import type { IncomingMessage, ServerResponse } from 'node:http';
import uni from '@dcloudio/vite-plugin-uni';
import { defineConfig } from 'vite';

const uniPlugin = typeof uni === 'function' ? uni : uni.default;
const USER_H5_API_TARGET = process.env.VITE_USER_H5_PROXY_TARGET || 'http://192.168.28.242';

const devFallbackProducts = [
    {
        publishSkuId: 910001,
        periodId: 810001,
        productId: 610001,
        skuId: 710001,
        productName: '四川爱媛果冻橙 5斤装',
        skuName: '5斤装',
        mainImageUrl: '/static/user-home/shop-detail.jpg',
        saleSpecText: '基地现摘/皮薄多汁/家庭分享装',
        salePrice: '29.90',
        limitQty: 3,
        minBuyQty: 1,
        plannedStockQty: 120,
        soldQty: 86,
        lockedQty: 8,
        availableQty: 26,
        deliveryDate: '明日 16:00-20:00',
        saleEndTime: '2026-06-29 20:00:00'
    },
    {
        publishSkuId: 910002,
        periodId: 810001,
        productId: 610002,
        skuId: 710002,
        productName: '山东烟台红富士苹果 6枚',
        skuName: '6枚装',
        mainImageUrl: '/static/user-home/shop-list.jpg',
        saleSpecText: '脆甜多汁/精选大果/冷链到仓',
        salePrice: '18.80',
        limitQty: 4,
        minBuyQty: 1,
        plannedStockQty: 90,
        soldQty: 74,
        lockedQty: 6,
        availableQty: 10,
        deliveryDate: '明日 16:00-20:00',
        saleEndTime: '2026-06-29 20:00:00'
    },
    {
        publishSkuId: 910003,
        periodId: 810001,
        productId: 610003,
        skuId: 710003,
        productName: '有机蔬菜组合 3斤',
        skuName: '3斤装',
        mainImageUrl: '/static/user-home/shop-category.jpg',
        saleSpecText: '当日分拣/三口之家装',
        salePrice: '16.90',
        limitQty: 2,
        minBuyQty: 1,
        plannedStockQty: 80,
        soldQty: 51,
        lockedQty: 5,
        availableQty: 24,
        deliveryDate: '明日 17:00-20:00',
        saleEndTime: '2026-06-29 21:00:00'
    },
    {
        publishSkuId: 910004,
        periodId: 810001,
        productId: 610004,
        skuId: 710004,
        productName: '全麦吐司 1袋',
        skuName: '1袋',
        mainImageUrl: '/static/user-home/shop-bakery.jpg',
        saleSpecText: '低糖配方/早餐主食',
        salePrice: '9.90',
        limitQty: 2,
        minBuyQty: 1,
        plannedStockQty: 60,
        soldQty: 48,
        lockedQty: 4,
        availableQty: 8,
        deliveryDate: '明日 16:30-20:00',
        saleEndTime: '2026-06-29 19:30:00'
    },
    {
        publishSkuId: 910005,
        periodId: 810001,
        productId: 610005,
        skuId: 710005,
        productName: '即食燕麦片 1kg',
        skuName: '1kg',
        mainImageUrl: '/static/user-home/shop-grain.jpg',
        saleSpecText: '早餐冲泡/膳食纤维',
        salePrice: '18.90',
        limitQty: 5,
        minBuyQty: 1,
        plannedStockQty: 100,
        soldQty: 66,
        lockedQty: 10,
        availableQty: 24,
        deliveryDate: '明日 16:00-20:00',
        saleEndTime: '2026-06-29 20:00:00'
    },
    {
        publishSkuId: 910006,
        periodId: 810001,
        productId: 610006,
        skuId: 710006,
        productName: '鲜牛奶 950ml',
        skuName: '950ml',
        mainImageUrl: '/static/user-home/shop-mine.jpg',
        saleSpecText: '低温奶/次日达',
        salePrice: '12.80',
        limitQty: 6,
        minBuyQty: 1,
        plannedStockQty: 110,
        soldQty: 42,
        lockedQty: 5,
        availableQty: 63,
        deliveryDate: '明日 16:00-20:00',
        saleEndTime: '2026-06-29 20:30:00'
    },
    {
        publishSkuId: 910007,
        periodId: 810001,
        productId: 610007,
        skuId: 710007,
        productName: '散养鸡蛋 30枚',
        skuName: '30枚',
        mainImageUrl: '/static/user-home/shop-order.jpg',
        saleSpecText: '谷物喂养/当日分拣',
        salePrice: '29.90',
        limitQty: 2,
        minBuyQty: 1,
        plannedStockQty: 70,
        soldQty: 39,
        lockedQty: 3,
        availableQty: 28,
        deliveryDate: '后日 16:00-20:00',
        saleEndTime: '2026-06-30 20:00:00'
    },
    {
        publishSkuId: 910008,
        periodId: 810001,
        productId: 610008,
        skuId: 710008,
        productName: '云南蓝莓 125g*4盒',
        skuName: '125g*4盒',
        mainImageUrl: '/static/user-home/shop-list.jpg',
        saleSpecText: '售罄商品沉底/到货提醒',
        salePrice: '19.90',
        limitQty: 3,
        minBuyQty: 1,
        plannedStockQty: 75,
        soldQty: 58,
        lockedQty: 4,
        availableQty: 0,
        deliveryDate: '明日 17:00-20:00',
        saleEndTime: '2026-06-29 20:00:00'
    }
];

const devFallbackHomeCategories = [
    { categoryId: 750001, categoryName: '水果鲜食', imageUrl: '/static/user-home/shop-home.jpg', sortNo: 10 },
    { categoryId: 750002, categoryName: '蔬菜豆制', imageUrl: '/static/user-home/shop-category.jpg', sortNo: 20 },
    { categoryId: 810001, categoryName: '海鲜水产', imageUrl: '/static/user-home/shop-seafood.jpg', sortNo: 30 },
    { categoryId: 810002, categoryName: '熟食卤味', imageUrl: '/static/user-home/shop-cooked.jpg', sortNo: 40 },
    { categoryId: 810003, categoryName: '肉禽蛋品', imageUrl: '/static/user-home/shop-meat.jpg', sortNo: 50 },
    { categoryId: 810004, categoryName: '休闲零食', imageUrl: '/static/user-home/shop-list.jpg', sortNo: 60 },
    { categoryId: 810005, categoryName: '早餐烘焙', imageUrl: '/static/user-home/shop-bakery.jpg', sortNo: 70 },
    { categoryId: 810006, categoryName: '粮油调味', imageUrl: '/static/user-home/shop-grain.jpg', sortNo: 80 }
];

const devFallbackCategories = [
    {
        id: 750001,
        categoryName: '水果鲜食',
        categoryCode: 'FRONT_FRUIT',
        imageUrl: '/static/user-home/shop-home.jpg',
        parentId: 0,
        sortNo: 10,
        status: 1,
        children: [
            { id: 816001, categoryName: '柑橘橙柚', categoryCode: 'FRONT_FRUIT_CITRUS', parentId: 750001, sortNo: 101, status: 1 },
            { id: 816002, categoryName: '莓果葡萄', categoryCode: 'FRONT_FRUIT_BERRY_GRAPE', parentId: 750001, sortNo: 102, status: 1 },
            { id: 816003, categoryName: '苹果梨桃', categoryCode: 'FRONT_FRUIT_APPLE_PEAR', parentId: 750001, sortNo: 103, status: 1 }
        ]
    },
    {
        id: 750002,
        categoryName: '蔬菜豆制',
        categoryCode: 'FRONT_VEGETABLE',
        imageUrl: '/static/user-home/shop-category.jpg',
        parentId: 0,
        sortNo: 20,
        status: 1,
        children: [
            { id: 816011, categoryName: '叶菜菌菇', categoryCode: 'FRONT_VEG_LEAF_MUSHROOM', parentId: 750002, sortNo: 111, status: 1 },
            { id: 816012, categoryName: '茄果根茎', categoryCode: 'FRONT_VEG_ROOT_MELON', parentId: 750002, sortNo: 112, status: 1 },
            { id: 816013, categoryName: '豆制净菜', categoryCode: 'FRONT_VEG_TOFU_CLEAN', parentId: 750002, sortNo: 113, status: 1 }
        ]
    },
    {
        id: 810001,
        categoryName: '海鲜水产',
        categoryCode: 'FRONT_SEAFOOD',
        imageUrl: '/static/user-home/shop-seafood.jpg',
        parentId: 0,
        sortNo: 30,
        status: 1,
        children: [
            { id: 816021, categoryName: '鱼虾贝类', categoryCode: 'FRONT_SEAFOOD_FISH_SHRIMP', parentId: 810001, sortNo: 121, status: 1 },
            { id: 816022, categoryName: '海鲜半成品', categoryCode: 'FRONT_SEAFOOD_PREPARED', parentId: 810001, sortNo: 122, status: 1 },
            { id: 816023, categoryName: '即食海味', categoryCode: 'FRONT_SEAFOOD_READY', parentId: 810001, sortNo: 123, status: 1 }
        ]
    },
    {
        id: 810002,
        categoryName: '熟食卤味',
        categoryCode: 'FRONT_COOKED',
        imageUrl: '/static/user-home/shop-cooked.jpg',
        parentId: 0,
        sortNo: 40,
        status: 1,
        children: [
            { id: 816031, categoryName: '卤味熟食', categoryCode: 'FRONT_COOKED_BRAISED', parentId: 810002, sortNo: 131, status: 1 },
            { id: 816032, categoryName: '烤肠肉食', categoryCode: 'FRONT_COOKED_ROASTED', parentId: 810002, sortNo: 132, status: 1 },
            { id: 816033, categoryName: '豆干小菜', categoryCode: 'FRONT_COOKED_TOFU_SIDE', parentId: 810002, sortNo: 133, status: 1 }
        ]
    },
    {
        id: 810003,
        categoryName: '肉禽蛋品',
        categoryCode: 'FRONT_MEAT',
        imageUrl: '/static/user-home/shop-meat.jpg',
        parentId: 0,
        sortNo: 50,
        status: 1,
        children: [
            { id: 816041, categoryName: '猪牛羊肉', categoryCode: 'FRONT_MEAT_PORK_BEEF', parentId: 810003, sortNo: 141, status: 1 },
            { id: 816042, categoryName: '禽肉蛋品', categoryCode: 'FRONT_MEAT_POULTRY_EGG', parentId: 810003, sortNo: 142, status: 1 },
            { id: 816043, categoryName: '调理肉品', categoryCode: 'FRONT_MEAT_PREPARED', parentId: 810003, sortNo: 143, status: 1 }
        ]
    },
    {
        id: 810004,
        categoryName: '休闲零食',
        categoryCode: 'FRONT_SNACK',
        imageUrl: '/static/user-home/shop-list.jpg',
        parentId: 0,
        sortNo: 60,
        status: 1,
        children: [
            { id: 816051, categoryName: '坚果果干', categoryCode: 'FRONT_SNACK_NUT_FRUIT', parentId: 810004, sortNo: 151, status: 1 },
            { id: 816052, categoryName: '糕点饼干', categoryCode: 'FRONT_SNACK_BAKED', parentId: 810004, sortNo: 152, status: 1 },
            { id: 816053, categoryName: '膨化零食', categoryCode: 'FRONT_SNACK_CRISPY', parentId: 810004, sortNo: 153, status: 1 }
        ]
    },
    {
        id: 810005,
        categoryName: '早餐烘焙',
        categoryCode: 'FRONT_BREAKFAST',
        imageUrl: '/static/user-home/shop-bakery.jpg',
        parentId: 0,
        sortNo: 70,
        status: 1,
        children: [
            { id: 816061, categoryName: '面包烘焙', categoryCode: 'FRONT_BREAKFAST_BAKERY', parentId: 810005, sortNo: 161, status: 1 },
            { id: 816062, categoryName: '早餐主食', categoryCode: 'FRONT_BREAKFAST_STAPLE', parentId: 810005, sortNo: 162, status: 1 },
            { id: 816063, categoryName: '乳品饮品', categoryCode: 'FRONT_BREAKFAST_DAIRY', parentId: 810005, sortNo: 163, status: 1 }
        ]
    },
    {
        id: 810006,
        categoryName: '粮油调味',
        categoryCode: 'FRONT_GRAIN',
        imageUrl: '/static/user-home/shop-grain.jpg',
        parentId: 0,
        sortNo: 80,
        status: 1,
        children: [
            { id: 816071, categoryName: '米面杂粮', categoryCode: 'FRONT_GRAIN_RICE_FLOUR', parentId: 810006, sortNo: 171, status: 1 },
            { id: 816072, categoryName: '食用油品', categoryCode: 'FRONT_GRAIN_OIL', parentId: 810006, sortNo: 172, status: 1 },
            { id: 816073, categoryName: '调味酱料', categoryCode: 'FRONT_GRAIN_SEASONING', parentId: 810006, sortNo: 173, status: 1 }
        ]
    }
];

function writeDevFallback(req: IncomingMessage, res: ServerResponse) {
    if (res.headersSent || res.destroyed) {
        return;
    }
    const url = req.url || '';
    const ok = (data: unknown) => ({
        code: 0,
        message: 'OK',
        data
    });
    let body: unknown;
    if (url.includes('/api/user/home/summary') || url.includes('/user/home/summary')) {
        body = ok({
            userId: 740001,
            cityId: 440100,
            stationId: 720001,
            onlineProductCount: 10,
            cartItemCount: 0,
            waitPayOrderCount: 0,
            waitPickupOrderCount: 1,
            unreadMessageCount: 0
        });
    } else if (url.includes('/api/user/stations') || url.includes('/user/stations')) {
        body = ok({
            total: 12,
            list: [
                {
                    id: 720001,
                    stationNo: 'ST440100001',
                    stationName: '王府花园自提点',
                    cityId: 440100,
                    address: '广州市天河区体育西路王府花园 3 栋负一楼',
                    contactName: '王团长',
                    contactMobile: '13700003185',
                    businessHours: '09:00-21:00',
                    status: 1
                },
                {
                    id: 720002,
                    stationNo: 'ST440100002',
                    stationName: '珠江新城社区店',
                    cityId: 440100,
                    address: '广州市天河区花城大道 88 号社区服务点',
                    contactName: '李团长',
                    contactMobile: '13700003186',
                    businessHours: '10:00-20:30',
                    status: 1
                },
                {
                    id: 720101,
                    stationNo: 'ST440300001',
                    stationName: '南山科技园自提点',
                    cityId: 440300,
                    address: '深圳市南山区高新南一道社区生鲜点',
                    contactName: '陈团长',
                    contactMobile: '13700003201',
                    businessHours: '09:30-21:00',
                    status: 1
                },
                {
                    id: 720201,
                    stationNo: 'ST310100001',
                    stationName: '上海徐汇田林自提点',
                    cityId: 310100,
                    address: '上海市徐汇区田林路 140 号邻里中心',
                    contactName: '周团长',
                    contactMobile: '13700003301',
                    businessHours: '09:00-20:00',
                    status: 1
                },
                {
                    id: 720301,
                    stationNo: 'ST330100001',
                    stationName: '杭州滨江江南自提点',
                    cityId: 330100,
                    address: '杭州市滨江区江南大道 1888 号社区驿站',
                    contactName: '赵团长',
                    contactMobile: '13700003401',
                    businessHours: '09:00-20:30',
                    status: 1
                },
                {
                    id: 720401,
                    stationNo: 'ST510100001',
                    stationName: '成都高新天府自提点',
                    cityId: 510100,
                    address: '成都市高新区天府三街社区服务中心',
                    contactName: '刘团长',
                    contactMobile: '13700003501',
                    businessHours: '09:30-20:30',
                    status: 1
                },
                {
                    id: 720501,
                    stationNo: 'ST420100001',
                    stationName: '武汉光谷社区自提点',
                    cityId: 420100,
                    address: '武汉市洪山区光谷步行街社区驿站',
                    contactName: '黄团长',
                    contactMobile: '13700003601',
                    businessHours: '09:00-21:00',
                    status: 1
                },
                {
                    id: 720601,
                    stationNo: 'ST110100001',
                    stationName: '北京望京花园自提点',
                    cityId: 110100,
                    address: '北京市朝阳区望京花园西区便民服务点',
                    contactName: '孙团长',
                    contactMobile: '13700003701',
                    businessHours: '09:00-20:00',
                    status: 1
                },
                {
                    id: 720602,
                    stationNo: 'ST110100002',
                    stationName: '北京回龙观休假点',
                    cityId: 110100,
                    address: '北京市昌平区回龙观社区中心',
                    contactName: '吴团长',
                    contactMobile: '13700003702',
                    businessHours: '休假中',
                    status: 0
                }
            ]
        });
    } else if (url.includes('/api/user/home/assets') || url.includes('/user/home/assets')) {
        body = ok({
            categories: devFallbackHomeCategories,
            banners: [],
            promos: []
        });
    } else if (/\/(?:api\/user|user)\/activities\/[^/]+\/products/.test(url)) {
        body = ok({
            total: 0,
            list: []
        });
    } else if (url.includes('/api/user/products/categories') || url.includes('/user/products/categories')) {
        body = ok({
            total: devFallbackCategories.length,
            list: devFallbackCategories
        });
    } else if (url.includes('/api/user/products') || url.includes('/user/products')) {
        body = ok({
            total: devFallbackProducts.length,
            list: devFallbackProducts
        });
    } else {
        body = ok(null);
    }
    res.writeHead(200, {
        'Content-Type': 'application/json;charset=utf-8',
        'X-Mall-Dev-Fallback': 'gateway-unavailable'
    });
    res.end(JSON.stringify(body));
}

function isHomeFallbackApi(url: string) {
    const path = (url || '').split('?')[0];
    return path === '/api/user/home/summary'
        || path === '/api/user/home/assets'
        || path === '/api/user/stations'
        || /^\/api\/user\/activities\/[^/]+\/products$/.test(path)
        || path === '/api/user/products/categories'
        || path === '/api/user/products'
        || path === '/user/home/summary'
        || path === '/user/home/assets'
        || path === '/user/stations'
        || /^\/user\/activities\/[^/]+\/products$/.test(path)
        || path === '/user/products/categories'
        || path === '/user/products';
}

function writeProxyFailure(res: ServerResponse) {
    if (res.headersSent || res.destroyed) {
        return;
    }
    res.writeHead(502, {
        'Content-Type': 'application/json;charset=utf-8'
    });
    res.end(JSON.stringify({
        code: 502,
        message: 'Gateway unavailable',
        data: null
    }));
}

function relayProxyResponse(proxyRes: IncomingMessage, req: IncomingMessage, res: ServerResponse) {
    const chunks: Buffer[] = [];
    proxyRes.on('data', (chunk: Buffer) => {
        chunks.push(Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk));
    });
    proxyRes.on('end', () => {
        const statusCode = proxyRes.statusCode || 500;
        if (isHomeFallbackApi(req.url || '') && statusCode >= 500) {
            writeDevFallback(req, res);
            return;
        }
        if (res.headersSent || res.destroyed) {
            return;
        }
        const headers = { ...proxyRes.headers };
        delete headers['transfer-encoding'];
        res.writeHead(statusCode, proxyRes.statusMessage, headers);
        res.end(Buffer.concat(chunks));
    });
    proxyRes.on('error', () => {
        if (isHomeFallbackApi(req.url || '')) {
            writeDevFallback(req, res);
            return;
        }
        writeProxyFailure(res);
    });
}

export default defineConfig({
    base: process.env.VITE_PUBLIC_BASE || '/',
    plugins: [uniPlugin()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        port: 5175,
        proxy: {
            '/api': {
                target: USER_H5_API_TARGET,
                changeOrigin: true,
                selfHandleResponse: true,
                configure(proxy) {
                    proxy.on('proxyRes', relayProxyResponse);
                    proxy.on('error', (_error, req, res) => {
                        if (isHomeFallbackApi(req.url || '')) {
                            writeDevFallback(req, res as ServerResponse);
                            return;
                        }
                        writeProxyFailure(res as ServerResponse);
                    });
                }
            },
            '/user': {
                target: USER_H5_API_TARGET,
                changeOrigin: true,
                selfHandleResponse: true,
                rewrite: (path) => path.replace(/^\/user(?=\/|$)/, '/api/user'),
                configure(proxy) {
                    proxy.on('proxyRes', relayProxyResponse);
                    proxy.on('error', (_error, req, res) => {
                        if (isHomeFallbackApi(req.url || '')) {
                            writeDevFallback(req, res as ServerResponse);
                            return;
                        }
                        writeProxyFailure(res as ServerResponse);
                    });
                }
            }
        }
    }
});
