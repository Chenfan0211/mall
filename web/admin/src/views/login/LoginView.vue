<template>
  <main class="login-page">
    <section class="login-card">
      <div class="login-copy">
        <span class="eyebrow">Operation console</span>
        <h1>社区团购运营后台</h1>
        <p>商品、团期、履约、售后、财务和权限在这里闭环处理。</p>
      </div>
      <el-form class="login-form" :model="form" label-position="top" @submit.prevent="submit">
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入运营账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" size="large" class="login-button" @click="submit">登录</el-button>
      </el-form>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { passwordLogin } from '@/api/auth';
import { useSessionStore } from '@/store/session';

const router = useRouter();
const sessionStore = useSessionStore();
const form = reactive({
    username: '',
    password: ''
});

async function submit() {
    const result = await passwordLogin({ ...form, portalCode: 'admin' });
    sessionStore.setSession(result.accessToken || result.token || '', result.username);
    router.push('/dashboard');
}
</script>

<style lang="scss" scoped>
.login-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 40px;
  background:
    radial-gradient(circle at top right, rgba(23, 105, 170, 0.14), transparent 30%),
    var(--mall-bg);
}

.login-card {
  display: grid;
  grid-template-columns: 1.1fr 420px;
  width: min(940px, 100%);
  overflow: hidden;
  background: #ffffff;
  border: 1px solid var(--mall-border);
  border-radius: 8px;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.18);
}

.login-copy {
  padding: 56px;
  color: #ffffff;
  background: var(--mall-nav);
}

.eyebrow {
  color: #cbd5e1;
  font-size: 12px;
  text-transform: uppercase;
}

h1 {
  margin: 20px 0 16px;
  font-size: 34px;
  line-height: 1.2;
}

p {
  width: 80%;
  margin: 0;
  color: #cbd5e1;
  line-height: 1.8;
}

.login-form {
  padding: 56px 40px;
}

.login-button {
  width: 100%;
}
</style>
