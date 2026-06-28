import { request } from '@/utils/request';

export interface LoginPayload {
    username: string;
    password: string;
    portalCode: string;
    role: string;
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
}

export function passwordLogin(payload: LoginPayload) {
    return request<LoginResult>('/auth/password-login', {
        method: 'POST',
        data: payload
    });
}
