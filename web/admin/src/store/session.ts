import { defineStore } from 'pinia';

interface SessionState {
    token: string;
    username: string;
    portalCode: string;
}

const TOKEN_KEY = 'mall_admin_token';

export const useSessionStore = defineStore('session', {
    state: (): SessionState => ({
        token: localStorage.getItem(TOKEN_KEY) || '',
        username: '',
        portalCode: 'admin'
    }),
    actions: {
        setSession(token: string, username: string) {
            this.token = token;
            this.username = username;
            localStorage.setItem(TOKEN_KEY, token);
        },
        clearSession() {
            this.token = '';
            this.username = '';
            localStorage.removeItem(TOKEN_KEY);
        }
    }
});
