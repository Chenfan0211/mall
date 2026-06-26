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
    username: string;
    role: string;
    warehouseName: string;
}

export function passwordLogin(payload: LoginPayload) {
    return request<LoginResult>('/auth/password-login', {
        method: 'POST',
        data: payload
    });
}
