import { fileURLToPath, URL } from 'node:url';
import vue from '@vitejs/plugin-vue';
import { defineConfig } from 'vite';

export default defineConfig({
    base: process.env.VITE_PUBLIC_BASE || '/',
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        port: 5174,
        proxy: {
            '/api': {
                target: 'http://localhost:18000',
                changeOrigin: true
            }
        }
    }
});
