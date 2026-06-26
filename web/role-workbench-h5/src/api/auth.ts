import { request } from '@/utils/request';

export interface LoginPayload {
    username: string;
    password: string;
    portalCode: string;
}

export interface LoginResult {
    accessToken?: string;
    token?: string;
    username: string;
    roles: string[];
}

export function passwordLogin(payload: LoginPayload) {
    return request<LoginResult>('/auth/password-login', {
        method: 'POST',
        data: payload
    });
}
