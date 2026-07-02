interface ApiResult<T> {
    code: string | number;
    message: string;
    data: T;
}

const DEFAULT_API_BASE_URL = 'http://192.168.28.242/api/user';
const RAW_API_BASE_URL = String(import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL).replace(/\/+$/, '');
const API_BASE_URL = normalizeApiBaseUrl(RAW_API_BASE_URL);
const TOKEN_KEY = 'mall_user_h5_token';

interface MallRequestOptions extends Omit<UniApp.RequestOptions, 'url' | 'data'> {
    data?: UniApp.RequestOptions['data'];
    body?: UniApp.RequestOptions['data'];
    silent?: boolean;
}

export function getToken() {
    return uni.getStorageSync(TOKEN_KEY) as string;
}

export function setToken(token: string) {
    uni.setStorageSync(TOKEN_KEY, token);
}

function normalizeApiBaseUrl(baseUrl: string) {
    if (import.meta.env.DEV && typeof window !== 'undefined' && /^https?:\/\//i.test(baseUrl)) {
        try {
            const parsed = new URL(baseUrl);
            return parsed.pathname.replace(/\/+$/, '') || '/api';
        } catch {
            return '/api';
        }
    }
    return baseUrl || '/api';
}

function buildRequestUrlWithBase(baseUrl: string, url: string) {
    const path = url.startsWith('/') ? url : `/${url}`;
    const normalizedPath = /\/(?:api\/)?user$/i.test(baseUrl) && path.startsWith('/user/')
        ? path.slice('/user'.length)
        : path;
    return `${baseUrl}${normalizedPath}`;
}

function buildApiFallbackBaseUrl() {
    if (!/\/user$/i.test(API_BASE_URL)) {
        return '';
    }
    if (API_BASE_URL === '/user') {
        return '/api/user';
    }
    try {
        const parsed = new URL(RAW_API_BASE_URL);
        if (/\/user\/?$/i.test(parsed.pathname)) {
            return `${parsed.origin}/api/user`;
        }
    } catch {
        return '';
    }
    return '';
}

function buildRequestCandidates(url: string) {
    const urls = [buildRequestUrlWithBase(API_BASE_URL, url)];
    const fallbackBaseUrl = buildApiFallbackBaseUrl();
    if (fallbackBaseUrl) {
        const fallbackUrl = buildRequestUrlWithBase(fallbackBaseUrl, url);
        if (!urls.includes(fallbackUrl)) {
            urls.push(fallbackUrl);
        }
    }
    return urls;
}

export function request<T>(url: string, options: MallRequestOptions = {}): Promise<T> {
    const token = getToken();
    const { silent, body, ...requestOptions } = options;
    const requestUrls = buildRequestCandidates(url);
    return new Promise((resolve, reject) => {
        const attempt = (index: number) => {
            const requestUrl = requestUrls[index];
            const canRetry = index < requestUrls.length - 1;
            uni.request({
                ...requestOptions,
                url: requestUrl,
                data: requestOptions.data ?? body,
                header: {
                    'Content-Type': 'application/json',
                    ...(token ? { Authorization: `Bearer ${token}` } : {}),
                    ...(requestOptions.header || {})
                },
                success(response) {
                    const result = response.data as ApiResult<T>;
                    if (response.statusCode < 200 || response.statusCode >= 300) {
                        if (canRetry) {
                            attempt(index + 1);
                            return;
                        }
                        if (!silent) {
                            uni.showToast({ title: '网络请求失败', icon: 'none' });
                        }
                        reject(new Error(`HTTP ${response.statusCode}`));
                        return;
                    }
                    if (!result || typeof result !== 'object' || !('code' in result)) {
                        if (canRetry) {
                            attempt(index + 1);
                            return;
                        }
                        if (!silent) {
                            uni.showToast({ title: '接口返回格式错误', icon: 'none' });
                        }
                        reject(new Error(`Invalid API response from ${requestUrl}`));
                        return;
                    }
                    if (String(result.code) !== '0') {
                        if (!silent) {
                            uni.showToast({ title: result.message || '业务处理失败', icon: 'none' });
                        }
                        reject(new Error(result.message || 'Business error'));
                        return;
                    }
                    resolve(result.data);
                },
                fail(error) {
                    if (canRetry) {
                        attempt(index + 1);
                        return;
                    }
                    if (!silent) {
                        uni.showToast({ title: '网络不可用', icon: 'none' });
                    }
                    reject(error);
                }
            });
        };
        attempt(0);
    });
}
