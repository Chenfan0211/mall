interface ApiResult<T> {
    code: string | number;
    message: string;
    data: T;
}

export type WarehouseRequestOptions = UniApp.RequestOptions & {
    silent?: boolean;
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://192.168.28.211/api';
const TOKEN_KEY = 'mall_warehouse_h5_token';

export function getToken() {
    return uni.getStorageSync(TOKEN_KEY) as string;
}

export function setToken(token: string) {
    uni.setStorageSync(TOKEN_KEY, token);
}

export function clearToken() {
    uni.removeStorageSync(TOKEN_KEY);
}

export function request<T>(url: string, options: WarehouseRequestOptions = {}): Promise<T> {
    const token = getToken();
    const { silent, ...requestOptions } = options;
    return new Promise((resolve, reject) => {
        uni.request({
            ...requestOptions,
            url: `${API_BASE_URL}${url}`,
            timeout: requestOptions.timeout ?? 10000,
            header: {
                'Content-Type': 'application/json',
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
                ...(requestOptions.header || {})
            },
            success(response) {
                const result = response.data as ApiResult<T>;
                if (response.statusCode < 200 || response.statusCode >= 300) {
                    if (!silent) uni.showToast({ title: '网络请求失败', icon: 'none' });
                    reject(new Error(`HTTP ${response.statusCode}`));
                    return;
                }
                if (String(result.code) !== '0') {
                    if (!silent) uni.showToast({ title: result.message || '业务处理失败', icon: 'none' });
                    reject(new Error(result.message || 'Business error'));
                    return;
                }
                resolve(result.data);
            },
            fail(error) {
                if (!silent) uni.showToast({ title: '网络不可用', icon: 'none' });
                reject(error);
            }
        });
    });
}
