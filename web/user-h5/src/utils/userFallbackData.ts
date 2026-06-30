import type { CartViewItem } from '@/stores/userState';
import type { UserProductCardDTO, UserProductDetailDTO } from '@/api/user';

export const shopImageAssets = {
    bannerFruit: '/static/user-home/banner-fruit.svg',
    promoGarden: '/static/user-home/promo-garden.svg',
    promoNew: '/static/user-home/promo-new.svg',
    promoBreakfast: '/static/user-home/promo-breakfast.svg',
    productFresh: '/static/user-home/product-fresh.svg',
    home: '/static/user-home/shop-home.jpg',
    list: '/static/user-home/shop-list.jpg',
    category: '/static/user-home/shop-category.jpg',
    cart: '/static/user-home/shop-cart.jpg',
    mine: '/static/user-home/shop-mine.jpg',
    order: '/static/user-home/shop-order.jpg',
    detail: '/static/user-home/shop-detail.jpg',
    detail2: '/static/user-home/shop-detail2.jpg',
    seafood: '/static/user-home/shop-seafood.jpg',
    cooked: '/static/user-home/shop-cooked.jpg',
    meat: '/static/user-home/shop-meat.jpg',
    bakery: '/static/user-home/shop-bakery.jpg',
    grain: '/static/user-home/shop-grain.jpg'
};

export const categoryIconAssets = {
    all: '/static/user-home/cat-all.svg',
    fruit: '/static/user-home/cat-fruit.svg',
    vegetable: '/static/user-home/cat-veg.svg',
    seafood: '/static/user-home/cat-seafood.svg',
    cooked: '/static/user-home/cat-cooked.svg',
    meat: '/static/user-home/cat-meat.svg',
    snack: '/static/user-home/cat-snack.svg',
    breakfast: '/static/user-home/cat-breakfast.svg',
    dairy: '/static/user-home/cat-dairy.svg',
    grain: '/static/user-home/cat-grain.svg'
};

export const fallbackCategories = [
    { id: 0, categoryName: '全部', label: '全部', imageUrl: categoryIconAssets.all },
    { id: 1, categoryName: '水果', label: '水果', imageUrl: categoryIconAssets.fruit },
    { id: 2, categoryName: '蔬菜百', label: '蔬菜百', imageUrl: categoryIconAssets.vegetable },
    { id: 3, categoryName: '海鲜', label: '海鲜', imageUrl: categoryIconAssets.seafood },
    { id: 4, categoryName: '熟食', label: '熟食', imageUrl: categoryIconAssets.cooked },
    { id: 5, categoryName: '肉禽蛋', label: '肉禽蛋', imageUrl: categoryIconAssets.meat },
    { id: 6, categoryName: '零食', label: '零食', imageUrl: categoryIconAssets.snack },
    { id: 7, categoryName: '早餐', label: '早餐', imageUrl: categoryIconAssets.breakfast },
    { id: 8, categoryName: '乳品', label: '乳品', imageUrl: categoryIconAssets.dairy },
    { id: 9, categoryName: '粮油', label: '粮油', imageUrl: categoryIconAssets.grain }
];

export function normalizeCategoryName(value?: string) {
    const text = String(value || '').trim();
    if (!text) {
        return '全部';
    }
    if (text.includes('水果')) {
        return '水果';
    }
    if (text.includes('蔬菜') || text.includes('豆制')) {
        return '蔬菜百';
    }
    if (text.includes('海鲜') || text.includes('水产')) {
        return '海鲜';
    }
    if (text.includes('熟食') || text.includes('卤味')) {
        return '熟食';
    }
    if (text.includes('肉') || text.includes('禽') || text.includes('蛋')) {
        return '肉禽蛋';
    }
    if (text.includes('零食') || text.includes('休闲')) {
        return '零食';
    }
    if (text.includes('早餐') || text.includes('烘焙')) {
        return '早餐';
    }
    if (text.includes('乳') || text.includes('奶')) {
        return '乳品';
    }
    if (text.includes('粮') || text.includes('油') || text.includes('米面')) {
        return '粮油';
    }
    return text.length > 4 ? text.slice(0, 4) : text;
}

export const fallbackProductImages = [
    shopImageAssets.detail,
    shopImageAssets.list,
    shopImageAssets.category,
    shopImageAssets.bakery,
    shopImageAssets.grain,
    shopImageAssets.mine,
    shopImageAssets.order,
    shopImageAssets.seafood,
    shopImageAssets.meat,
    shopImageAssets.cooked
];

export const fallbackProducts: UserProductCardDTO[] = [
    {
        publishSkuId: 910001,
        periodId: 810001,
        productId: 610001,
        skuId: 710001,
        productName: '四川爱媛果冻橙 5斤装',
        skuName: '5斤装',
        mainImageUrl: fallbackProductImages[0],
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
        mainImageUrl: fallbackProductImages[1],
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
        mainImageUrl: fallbackProductImages[2],
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
        mainImageUrl: fallbackProductImages[3],
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
        mainImageUrl: fallbackProductImages[4],
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
        mainImageUrl: fallbackProductImages[5],
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
        mainImageUrl: fallbackProductImages[6],
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
        mainImageUrl: fallbackProductImages[7],
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
    },
    {
        publishSkuId: 910009,
        periodId: 810001,
        productId: 610009,
        skuId: 710009,
        productName: '海南金煌芒 4斤装',
        skuName: '4斤装',
        mainImageUrl: fallbackProductImages[8],
        saleSpecText: '树上熟/果香浓郁/甜度高',
        salePrice: '26.90',
        limitQty: 2,
        minBuyQty: 1,
        plannedStockQty: 40,
        soldQty: 40,
        lockedQty: 0,
        availableQty: 18,
        deliveryDate: '明日 16:00-20:00',
        saleEndTime: '2026-06-29 18:00:00'
    },
    {
        publishSkuId: 910010,
        periodId: 810001,
        productId: 610010,
        skuId: 710010,
        productName: '五常大米 5kg',
        skuName: '5kg',
        mainImageUrl: fallbackProductImages[9],
        saleSpecText: '当季新米/香软弹牙',
        salePrice: '59.90',
        limitQty: 1,
        minBuyQty: 1,
        plannedStockQty: 45,
        soldQty: 27,
        lockedQty: 2,
        availableQty: 16,
        deliveryDate: '后日 16:00-20:00',
        saleEndTime: '2026-06-30 20:00:00'
    }
];

export const fallbackPublishSkuIds = new Set(fallbackProducts.map((item) => item.publishSkuId));

export function withProductImages(rows: UserProductCardDTO[]) {
    return rows.map((item, index) => ({
        ...item,
        mainImageUrl: item.mainImageUrl || fallbackProductImages[index % fallbackProductImages.length]
    }));
}

export function isSoldout(item: UserProductCardDTO) {
    return Number(item.availableQty || 0) <= 0;
}

export function productSpecText(item: UserProductCardDTO) {
    return item.skuName || item.saleSpecText || '默认规格';
}

export function productPickupText(value?: string) {
    if (!value) {
        return '待定';
    }
    const matched = value.match(/(\d{4})-(\d{1,2})-(\d{1,2})/);
    if (matched) {
        return `${matched[2].padStart(2, '0')}-${matched[3].padStart(2, '0')}`;
    }
    const relativeDayMap: Record<string, number> = {
        今日: 0,
        今天: 0,
        明日: 1,
        明天: 1,
        后日: 2,
        后天: 2
    };
    const relativeDay = Object.keys(relativeDayMap).find((key) => value.includes(key));
    if (relativeDay) {
        const date = new Date();
        date.setDate(date.getDate() + relativeDayMap[relativeDay]);
        return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
    const shortMatched = value.match(/(\d{1,2})[./-](\d{1,2})/);
    if (shortMatched) {
        return `${shortMatched[1].padStart(2, '0')}-${shortMatched[2].padStart(2, '0')}`;
    }
    return value.replace(/\s.*$/, '').slice(0, 8);
}

export function productOldPrice(item: UserProductCardDTO) {
    if (!fallbackPublishSkuIds.has(item.publishSkuId)) {
        return '';
    }
    const value = Number(item.salePrice || 0);
    if (!Number.isFinite(value) || value <= 0) {
        return '';
    }
    return (value * 1.25).toFixed(2);
}

export function findFallbackProduct(productId: number) {
    return fallbackProducts.find((item) => item.productId === productId) || fallbackProducts[0];
}

export function buildFallbackDetail(productId: number): UserProductDetailDTO {
    const product = findFallbackProduct(productId);
    return {
        product: {
            id: product.productId,
            productNo: `DEMO${product.productId}`,
            productName: product.productName,
            mainImageUrl: product.mainImageUrl,
            auditStatus: 1,
            saleStatus: 1
        },
        skus: [
            {
                publishSkuId: product.publishSkuId,
                periodId: product.periodId,
                skuId: product.skuId,
                skuName: product.skuName,
                saleSpecText: product.saleSpecText,
                salePrice: product.salePrice,
                limitQty: product.limitQty,
                minBuyQty: product.minBuyQty,
                availableQty: product.availableQty,
                deliveryDate: product.deliveryDate
            },
            {
                publishSkuId: product.publishSkuId + 1000,
                periodId: product.periodId,
                skuId: product.skuId + 1000,
                skuName: `${product.skuName} 加量装`,
                saleSpecText: '家庭囤货规格',
                salePrice: (Number(product.salePrice || 0) * 1.8).toFixed(2),
                limitQty: Math.max(1, product.limitQty - 1),
                minBuyQty: 1,
                availableQty: Math.max(0, Math.floor(Number(product.availableQty || 0) / 2)),
                deliveryDate: product.deliveryDate
            }
        ],
        favoriteFlag: 0,
        commentCount: 128
    };
}

export function buildFallbackCartItems(): CartViewItem[] {
    const [apple, orange, vegetable, toast, oat] = [
        fallbackProducts[1],
        fallbackProducts[0],
        fallbackProducts[2],
        fallbackProducts[3],
        fallbackProducts[4]
    ];
    return [
        {
            id: 930001,
            productId: apple.productId,
            periodId: apple.periodId,
            skuId: apple.skuId,
            publishSkuId: apple.publishSkuId,
            title: apple.productName,
            spec: apple.saleSpecText || apple.skuName,
            image: apple.mainImageUrl,
            price: apple.salePrice,
            qty: 1,
            selected: true,
            valid: true,
            stock: apple.availableQty,
            limit: apple.limitQty,
            pickupDate: apple.deliveryDate,
            priceChanged: true
        },
        {
            id: 930002,
            productId: orange.productId,
            periodId: orange.periodId,
            skuId: orange.skuId,
            publishSkuId: orange.publishSkuId,
            title: orange.productName,
            spec: orange.saleSpecText || orange.skuName,
            image: orange.mainImageUrl,
            price: orange.salePrice,
            qty: 1,
            selected: true,
            valid: true,
            stock: orange.availableQty,
            limit: orange.limitQty,
            pickupDate: orange.deliveryDate
        },
        {
            id: 930003,
            productId: vegetable.productId,
            periodId: vegetable.periodId,
            skuId: vegetable.skuId,
            publishSkuId: vegetable.publishSkuId,
            title: vegetable.productName,
            spec: vegetable.saleSpecText || vegetable.skuName,
            image: vegetable.mainImageUrl,
            price: vegetable.salePrice,
            qty: 1,
            selected: true,
            valid: true,
            stock: vegetable.availableQty,
            limit: vegetable.limitQty,
            pickupDate: vegetable.deliveryDate
        },
        {
            id: 930004,
            productId: toast.productId,
            periodId: toast.periodId,
            skuId: toast.skuId,
            publishSkuId: toast.publishSkuId,
            title: toast.productName,
            spec: '销售期已结束',
            image: toast.mainImageUrl,
            price: toast.salePrice,
            qty: 1,
            selected: false,
            valid: false,
            stock: 0,
            limit: toast.limitQty,
            pickupDate: toast.deliveryDate,
            invalidReason: '销售期已结束'
        },
        {
            id: 930005,
            productId: oat.productId,
            periodId: oat.periodId,
            skuId: oat.skuId,
            publishSkuId: oat.publishSkuId,
            title: oat.productName,
            spec: oat.saleSpecText || oat.skuName,
            image: oat.mainImageUrl,
            price: oat.salePrice,
            qty: 1,
            selected: false,
            valid: false,
            stock: 0,
            limit: oat.limitQty,
            pickupDate: oat.deliveryDate,
            invalidReason: '当前不可购买'
        }
    ];
}
