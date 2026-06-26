import { fileURLToPath, URL } from 'node:url';
import uni from '@dcloudio/vite-plugin-uni';
import { defineConfig } from 'vite';

const uniPlugin = typeof uni === 'function' ? uni : uni.default;

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
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
});
