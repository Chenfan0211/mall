interface ApiResult<T> {
    code: string | number;
    message: string;
    data: T;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';
const TOKEN_KEY = 'mall_role_h5_token';
const SERVICE_UNAVAILABLE_MESSAGE = '服务暂时不可用，请稍后重试';
const DATA_UNAVAILABLE_MESSAGE = '数据暂不可用，请稍后重试';

function getResponseMessage(data: unknown) {
    if (!data) {
        return '';
    }
    if (typeof data === 'string') {
        return data.trim();
    }
    if (typeof data === 'object' && 'message' in data) {
        const message = (data as { message?: unknown }).message;
        return typeof message === 'string' ? message : '';
    }
    return '';
}

function sanitizeMessage(message: string, fallback = DATA_UNAVAILABLE_MESSAGE) {
    const text = message.trim();
    if (!text) return fallback;
    if (
        /HTTP\s*\d{3}/i.test(text)
        || /后端|网关|Whitelabel|Not Found|Internal Server Error|Cannot\s+\w+/i.test(text)
        || /ECONN|ENOTFOUND|ETIMEDOUT|Failed to fetch|NetworkError/i.test(text)
        || /^<!doctype/i.test(text)
        || /^<html/i.test(text)
        || /<html[\s>]/i.test(text)
    ) {
        return fallback;
    }
    return text;
}

export function friendlyErrorMessage(err: unknown, fallback = DATA_UNAVAILABLE_MESSAGE) {
    if (err instanceof Error) {
        return sanitizeMessage(err.message, fallback);
    }
    if (typeof err === 'string') {
        return sanitizeMessage(err, fallback);
    }
    return fallback;
}

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
                    const fallback = response.statusCode >= 500 ? SERVICE_UNAVAILABLE_MESSAGE : DATA_UNAVAILABLE_MESSAGE;
                    const message = sanitizeMessage(getResponseMessage(response.data), fallback);
                    uni.showToast({ title: message, icon: 'none' });
                    reject(new Error(message));
                    return;
                }
                if (result && result.code !== undefined && String(result.code) !== '0') {
                    const message = sanitizeMessage(result.message || '业务处理失败', '业务处理失败');
                    uni.showToast({ title: message, icon: 'none' });
                    reject(new Error(message));
                    return;
                }
                resolve(result && result.data !== undefined ? result.data : (response.data as T));
            },
            fail() {
                const message = '网络不可用，请检查后重试';
                uni.showToast({ title: message, icon: 'none' });
                reject(new Error(message));
            }
        });
    });
}
