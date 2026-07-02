import { fileURLToPath, URL } from 'node:url';
import uni from '@dcloudio/vite-plugin-uni';
import { defineConfig } from 'vite';

const uniPlugin = typeof uni === 'function' ? uni : uni.default;
const apiProxyTarget = process.env.VITE_API_PROXY_TARGET || 'http://192.168.28.242';

export default defineConfig({
    base: process.env.VITE_PUBLIC_BASE || '/',
    plugins: [uniPlugin()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        port: 5176,
        proxy: {
            '/api': {
                target: apiProxyTarget,
                changeOrigin: true
            }
        }
    }
});
