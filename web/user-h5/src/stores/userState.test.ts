import {
    addReceiver,
    cartTotal,
    createDefaultUserState,
    removeCartItemOrAsk,
    removeReceiver,
    selectedCartItems,
    setDefaultReceiver,
    switchStation,
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
    assertEqual(defaultState.authenticated, false, 'default auth state');
    assertEqual(defaultState.mobileAuthorized, false, 'default mobile auth state');

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
