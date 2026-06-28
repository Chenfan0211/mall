import { request } from '@/utils/request';
import type { RoleType } from '@/stores/role';

export interface LoginPayload {
    username: string;
    password: string;
    portalCode: string;
    roleType?: RoleType;
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

export interface ResetPasswordPayload {
    username: string;
    mobile: string;
    code: string;
    password: string;
    roleType: RoleType;
}

export function passwordLogin(payload: LoginPayload) {
    return request<LoginResult>('/auth/password-login', {
        method: 'POST',
        data: payload
    });
}

export function resetPassword(payload: ResetPasswordPayload) {
    return request<void>('/auth/reset-password', {
        method: 'POST',
        data: payload
    });
}
