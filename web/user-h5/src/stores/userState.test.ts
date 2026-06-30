import {
    addReceiver,
    cartTotal,
    createDefaultUserState,
    applyLocatedCity,
    buildCitiesFromStations,
    removeCartItemOrAsk,
    removeReceiver,
    resolveNearestOpenCity,
    resetStationsToFallback,
    selectedCartItems,
    setDefaultReceiver,
    switchCity,
    switchStation,
    syncStationsFromApi,
    toggleSelectAllCartItems,
    updateCartItemQty
} from './userState';

function assertEqual<T>(actual: T, expected: T, message: string) {
    if (actual !== expected) {
        throw new Error(`${message}: expected ${String(expected)}, got ${String(actual)}`);
    }
}

function assertJsonEqual(actual: unknown, expected: unknown, message: string) {
    const actualJson = JSON.stringify(actual);
    const expectedJson = JSON.stringify(expected);
    if (actualJson !== expectedJson) {
        throw new Error(`${message}: expected ${expectedJson}, got ${actualJson}`);
    }
}

export function runUserStateTests() {
    const defaultState = createDefaultUserState();
    assertEqual(defaultState.user.id, 740001, 'default user id');
    assertEqual(defaultState.city.id, 440100, 'default city id');
    assertEqual(defaultState.station.id, 720001, 'default station id');
    assertEqual(defaultState.cities.length >= 7, true, 'default nationwide cities');
    assertEqual(defaultState.stations.some((item) => item.cityId === 110100), true, 'default Beijing stations');
    assertEqual(defaultState.authenticated, false, 'default auth state');
    assertEqual(defaultState.mobileAuthorized, false, 'default mobile auth state');

    const apiState = createDefaultUserState();
    apiState.cartItems = [
        { id: 1, title: '后端接口同步前商品', price: '9.90', qty: 1, selected: true, valid: true }
    ];
    const syncResult = syncStationsFromApi(apiState, [
        {
            id: 910001,
            stationNo: 'ST-SZ-001',
            stationName: '深圳后端自提点',
            cityId: 440300,
            address: '深圳市南山区测试路 1 号',
            contactMobile: '13800009001',
            businessHours: '09:00-20:00',
            status: 1
        },
        {
            id: 910002,
            stationNo: 'ST-SH-001',
            stationName: '上海暂停自提点',
            cityId: 310100,
            address: '上海市浦东新区测试路 2 号',
            contactMobile: '13800009002',
            businessHours: '暂停服务',
            status: 2
        }
    ]);
    assertEqual(syncResult.synced, true, 'api station sync succeeds');
    assertEqual(apiState.cities.length, 2, 'api station sync derives cities');
    assertEqual(apiState.cities[0].name, '深圳', 'api station city name mapped');
    assertEqual(apiState.station.id, 910001, 'api station sync selects first available station');
    assertEqual(apiState.cartItems.length, 1, 'api station sync does not clear cart');

    const apiCities = buildCitiesFromStations(apiState.stations);
    assertJsonEqual(
        apiCities.map((item) => item.id),
        [440300, 310100],
        'build cities from station city ids'
    );

    const emptyApiState = createDefaultUserState();
    const emptySyncResult = syncStationsFromApi(emptyApiState, []);
    assertEqual(emptySyncResult.fallback, true, 'empty api station list requests fallback');
    const fallbackResult = resetStationsToFallback(emptyApiState);
    assertEqual(fallbackResult.fallback, true, 'reset stations to fallback');
    assertEqual(emptyApiState.city.id, 440100, 'fallback city id');

    const stationState = createDefaultUserState();
    stationState.cartItems = [
        { id: 1, title: '页面覆盖赣南鲜橙', price: '29.90', qty: 2, selected: true, valid: true },
        { id: 2, title: '失效商品', price: '9.90', qty: 1, selected: false, valid: false }
    ];
    const switchResult = switchStation(stationState, {
        id: 720002,
        name: '天河体育西店',
        cityId: 440100,
        address: '广州市天河区体育西路 52 号',
        mobile: '13900006402',
        businessHours: '09:00-20:00',
        status: 1
    });
    assertEqual(switchResult.clearedCart, true, 'station switch clears cart');
    assertEqual(stationState.station.id, 720002, 'station switched');
    assertJsonEqual(stationState.cartItems, [], 'cart cleared after station switch');

    const disabledStationResult = switchStation(stationState, {
        id: 720003,
        name: '王府花园休假点',
        cityId: 440100,
        address: '广州市越秀区王府花园 3 栋',
        mobile: '13700003185',
        businessHours: '暂停服务',
        status: 2,
        abnormalReason: '当前自提点休假或停用，暂不可选'
    });
    assertEqual(disabledStationResult.switched, false, 'disabled station blocked');
    assertEqual(stationState.station.id, 720002, 'disabled station keeps current station');

    const cityState = createDefaultUserState();
    cityState.cartItems = [
        { id: 1, title: '页面覆盖赣南鲜橙', price: '29.90', qty: 1, selected: true, valid: true }
    ];
    const cityResult = switchCity(cityState, { id: 440300, name: '深圳' });
    assertEqual(cityResult.switched, true, 'city switched');
    assertEqual(cityResult.clearedCart, true, 'city switch clears cart');
    assertEqual(cityState.city.name, '深圳', 'city name switched');
    assertEqual(cityState.station.id, 730001, 'city switch selects first available station');
    assertJsonEqual(cityState.cartItems, [], 'cart cleared after city switch');

    const locationCities = createDefaultUserState().cities;
    const locatedShenzhen = resolveNearestOpenCity(locationCities, { latitude: 22.5431, longitude: 114.0579 });
    assertEqual(locatedShenzhen.id, 440300, 'location resolves Shenzhen');
    const locatedShanghai = resolveNearestOpenCity(locationCities, { latitude: 31.2304, longitude: 121.4737 });
    assertEqual(locatedShanghai.id, 310100, 'location resolves Shanghai');
    const fallbackCity = resolveNearestOpenCity(locationCities, { latitude: 40.7128, longitude: -74.006 });
    assertEqual(fallbackCity.id, 440100, 'unsupported location falls back to first city');

    const backendLocatedState = createDefaultUserState();
    syncStationsFromApi(backendLocatedState, [
        {
            id: 910003,
            stationNo: 'ST-SZ-CLOSED',
            stationName: '深圳停用自提点',
            cityId: 440300,
            address: '深圳市南山区测试路 3 号',
            contactMobile: '13800009003',
            businessHours: '暂停服务',
            status: 3
        },
        {
            id: 910004,
            stationNo: 'ST-GZ-OPEN',
            stationName: '广州营业自提点',
            cityId: 440100,
            address: '广州市海珠区测试路 4 号',
            contactMobile: '13800009004',
            businessHours: '09:00-20:00',
            status: 1
        }
    ]);
    const locatedOpenCity = resolveNearestOpenCity(
        backendLocatedState.cities,
        { latitude: 22.5431, longitude: 114.0579 },
        backendLocatedState.stations
    );
    assertEqual(locatedOpenCity.id, 440100, 'location ignores cities without available stations');
    const backendLocatedResult = applyLocatedCity(backendLocatedState, { latitude: 22.5431, longitude: 114.0579 });
    assertEqual(backendLocatedResult.switched, false, 'located unavailable city keeps open fallback city');
    assertEqual(backendLocatedState.city.id, 440100, 'located unavailable city stays on open city');
    assertEqual(backendLocatedState.station.id, 910004, 'located unavailable city keeps open station');

    const locatedState = createDefaultUserState();
    locatedState.cartItems = [
        { id: 1, title: '页面覆盖赣南鲜橙', price: '29.90', qty: 1, selected: true, valid: true }
    ];
    const locatedResult = applyLocatedCity(locatedState, { latitude: 31.2304, longitude: 121.4737 });
    assertEqual(locatedResult.switched, true, 'located city switches state');
    assertEqual(locatedResult.clearedCart, true, 'located city switch clears cart');
    assertEqual(locatedState.city.id, 310100, 'located state city switched');
    assertEqual(locatedState.station.id, 740001, 'located city selects first available station');
    assertJsonEqual(locatedState.cartItems, [], 'cart cleared after located city switch');

    const cartState = createDefaultUserState();
    cartState.cartItems = [
        { id: 1, title: '页面覆盖赣南鲜橙', price: '29.90', qty: 2, selected: true, valid: true, stock: 3, limit: 2 },
        { id: 2, title: '页面覆盖待审核葡萄', price: '16.90', qty: 1, selected: false, valid: true, stock: 5, limit: 4 },
        { id: 3, title: '已失效商品', price: '99.00', qty: 1, selected: true, valid: false }
    ];
    updateCartItemQty(cartState, 1, 8);
    toggleSelectAllCartItems(cartState, true);
    assertEqual(cartState.cartItems[0].qty, 2, 'quantity clamps to limit');
    assertJsonEqual(selectedCartItems(cartState).map((item) => item.id), [1, 2], 'selected valid items only');
    assertEqual(cartTotal(cartState), '76.70', 'selected valid cart total');

    const removeState = createDefaultUserState();
    removeState.cartItems = [
        { id: 1, title: '页面覆盖赣南鲜橙', price: '29.90', qty: 1, selected: true, valid: true },
        { id: 2, title: '页面覆盖待审核葡萄', price: '16.90', qty: 2, selected: true, valid: true }
    ];
    const askDelete = removeCartItemOrAsk(removeState, 1, 0);
    const decrement = removeCartItemOrAsk(removeState, 2, 1);
    assertEqual(askDelete.requiresConfirm, true, 'qty zero asks confirmation');
    assertEqual(removeState.cartItems.length, 2, 'qty zero does not remove before confirmation');
    assertEqual(decrement.requiresConfirm, false, 'qty decrement updates directly');
    assertEqual(removeState.cartItems[1].qty, 1, 'qty decremented');

    const receiverState = createDefaultUserState();
    const newReceiver = addReceiver(receiverState, '新提货人', '13800009999');
    setDefaultReceiver(receiverState, newReceiver.id);
    removeReceiver(receiverState, 740101);
    assertEqual(receiverState.receivers.length, 2, 'receiver add and remove');
    assertEqual(receiverState.receivers[1].defaultFlag, true, 'default receiver changed');
}
