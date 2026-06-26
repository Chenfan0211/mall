import { request } from '@/utils/request';

export interface LoginPayload {
    username: string;
    password: string;
    portalCode: string;
}

export interface LoginResult {
    accessToken?: string;
    token?: string;
    accountId: string;
    username: string;
    roles: string[];
    permissions: string[];
}

export function passwordLogin(payload: LoginPayload) {
    return request<LoginResult>('/auth/password-login', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}
