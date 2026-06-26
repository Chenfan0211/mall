import { ElMessage } from 'element-plus';
import { useSessionStore } from '@/store/session';

interface ApiResult<T> {
    code: string;
    message: string;
    data: T;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const sessionStore = useSessionStore();
    const headers = new Headers(options.headers);
    headers.set('Content-Type', 'application/json');
    if (sessionStore.token) {
        headers.set('Authorization', `Bearer ${sessionStore.token}`);
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers
    });

    if (!response.ok) {
        ElMessage.error(`请求失败：${response.status}`);
        throw new Error(`HTTP ${response.status}`);
    }

    const result = (await response.json()) as ApiResult<T>;
    if (result.code !== '0') {
        ElMessage.error(result.message || '业务处理失败');
        throw new Error(result.message || 'Business error');
    }

    return result.data;
}
