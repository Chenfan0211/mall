import { request, type WarehouseRequestOptions } from '@/utils/request';

export interface LoginPayload {
    username: string;
    password: string;
    portalCode: string;
}

export interface LoginResult {
    accessToken?: string;
    token?: string;
    account?: {
        id?: number;
        username?: string;
    };
    roles?: Array<{
        roleCode?: string;
        roleName?: string;
    }>;
    menus?: Array<{
        menuCode?: string;
        menuName?: string;
        routePath?: string;
    }>;
    authorities?: string[];
    dataScopes?: unknown[];
}

export function passwordLogin(payload: LoginPayload, options: WarehouseRequestOptions = {}) {
    return request<LoginResult>('/auth/password-login', {
        ...options,
        method: 'POST',
        data: payload
    });
}
