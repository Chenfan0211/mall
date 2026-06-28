interface ApiResult<T> {
    code: string | number;
    message: string;
    data: T;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';
const TOKEN_KEY = 'mall_role_h5_token';

export function getToken() {
    return uni.getStorageSync(TOKEN_KEY) as string;
}

export function setToken(token: string) {
    uni.setStorageSync(TOKEN_KEY, token);
}

export function clearToken() {
    uni.removeStorageSync(TOKEN_KEY);
}

export function request<T>(url: string, options: UniApp.RequestOptions = {}): Promise<T> {
    const token = getToken();
    return new Promise((resolve, reject) => {
        uni.request({
            ...options,
            url: `${API_BASE_URL}${url}`,
            header: {
                'Content-Type': 'application/json',
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
                ...(options.header || {})
            },
            success(response) {
                const result = response.data as ApiResult<T>;
                if (response.statusCode < 200 || response.statusCode >= 300) {
                    uni.showToast({ title: '网络请求失败', icon: 'none' });
                    reject(new Error(`HTTP ${response.statusCode}`));
                    return;
                }
                if (result && result.code !== undefined && String(result.code) !== '0') {
                    uni.showToast({ title: result.message || '业务处理失败', icon: 'none' });
                    reject(new Error(result.message || 'Business error'));
                    return;
                }
                resolve(result && result.data !== undefined ? result.data : (response.data as T));
            },
            fail(error) {
                uni.showToast({ title: '网络不可用', icon: 'none' });
                reject(error);
            }
        });
    });
}
