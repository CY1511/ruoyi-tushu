<template>
  <div class="login-page">
    <!-- 顶饰条 -->
    <div class="top-deco">
      <div class="deco-line"></div>
      <div class="deco-text">TAN · 二手书市</div>
      <div class="deco-line"></div>
    </div>

    <!-- 报刊头 -->
    <div class="masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 入场券 —</div>
      <h1 class="masthead-title">摊</h1>
      <div class="masthead-sub">校园二手书市 · 凭票入场</div>
      <div class="masthead-meta">
        <span>EST. 2026</span>
        <span class="dot">·</span>
        <span>IN CAMPUS</span>
      </div>
    </div>

    <!-- 票根 -->
    <div class="ticket">
      <div class="ticket-body">
        <div class="ticket-row">
          <span class="ticket-label">USER</span>
          <el-input
            v-model="form.username"
            placeholder="账号"
            borderless
            clearable
            class="ticket-input"
          ></el-input>
        </div>
        <div class="ticket-row">
          <span class="ticket-label">PASS</span>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            show-password
            borderless
            class="ticket-input"
            @keyup.enter.native="handleLogin"
          ></el-input>
        </div>
        <div class="ticket-row" v-if="captchaEnabled">
          <span class="ticket-label">CODE</span>
          <div class="captcha-wrap">
            <el-input
              v-model="form.code"
              placeholder="验证码"
              borderless
              maxlength="6"
              class="ticket-input captcha-input"
              @keyup.enter.native="handleLogin"
            ></el-input>
            <div class="captcha-img" @click="getCaptcha">
              <img :src="captchaUrl" alt="验证码" />
            </div>
          </div>
        </div>
      </div>

      <!-- 撕齿 -->
      <div class="ticket-perforation"></div>

      <!-- 票根脚 -->
      <div class="ticket-footer">
        <div class="footer-eyebrow">— 入 场 —</div>
        <el-button
          type="primary"
          class="ticket-btn"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '验票中…' : '揭 印 入 场' }}
        </el-button>
        <div class="register-line">
          尚未领票？<router-link to="/register">前往领票 →</router-link>
        </div>
      </div>

      <!-- 印章 -->
      <div class="stamp">
        <div class="stamp-ring">
          <span class="stamp-text">摊 · TAN</span>
        </div>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="bottom-deco">
      <div class="deco-ornament">❀</div>
      <div class="deco-quote">旧书不厌百回读 · 熟读深思子自知</div>
    </div>
  </div>
</template>

<script>
import { login, getCaptchaImage } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: '',
        code: '',
        uuid: ''
      },
      loading: false,
      captchaEnabled: true,
      captchaUrl: ''
    }
  },
  created() {
    this.getCaptcha()
  },
  methods: {
    getCaptcha() {
      getCaptchaImage().then(res => {
        this.captchaEnabled = res.captchaEnabled !== false
        if (this.captchaEnabled) {
          this.captchaUrl = 'data:image/gif;base64,' + res.img
          this.form.uuid = res.uuid
        }
      }).catch(() => {
        this.captchaEnabled = false
      })
    },
    handleLogin() {
      if (!this.form.username) { this.$message.warning('请填写账号'); return }
      if (!this.form.password) { this.$message.warning('请填写密码'); return }
      if (this.captchaEnabled && !this.form.code) { this.$message.warning('请填写验证码'); return }
      this.loading = true
      const loginData = {
        username: this.form.username,
        password: this.form.password
      }
      if (this.captchaEnabled) {
        loginData.code = this.form.code
        loginData.uuid = this.form.uuid
      }
      this.$store.dispatch('Login', loginData).then(() => {
        return this.$store.dispatch('GetUserInfo')
      }).then(() => {
        this.$message.success('入场成功')
        const redirect = this.$route.query.redirect || '/home'
        this.$router.push(redirect)
      }).catch(() => {
        this.loading = false
        this.getCaptcha()
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--paper);
  padding: 60px 24px 40px;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* —— 顶饰条 —— */
.top-deco {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}
.deco-line {
  flex: 1;
  height: 1px;
  background: var(--ink);
  position: relative;
}
.deco-line::after {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.deco-text {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.4em;
  color: var(--ink);
  font-weight: 500;
}

/* —— 报刊头 —— */
.masthead {
  text-align: center;
  margin-bottom: 28px;
}
.masthead-eyebrow {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-bottom: 8px;
}
.masthead-title {
  font-family: var(--font-serif);
  font-size: 84px;
  font-weight: 900;
  color: var(--ink);
  line-height: 0.9;
  letter-spacing: 0.05em;
  position: relative;
  display: inline-block;
}
.masthead-title::before,
.masthead-title::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 24px;
  height: 1px;
  background: var(--ink);
}
.masthead-title::before { right: 100%; margin-right: 12px; }
.masthead-title::after { left: 100%; margin-left: 12px; }
.masthead-sub {
  font-family: var(--font-serif);
  font-size: 15px;
  color: var(--ink-soft);
  letter-spacing: 0.5em;
  margin-top: 8px;
  padding-left: 0.5em;
}
.masthead-meta {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  margin-top: 10px;
}
.dot { margin: 0 8px; }

/* —— 票根 —— */
.ticket {
  position: relative;
  background: var(--white-paper);
  border: 2px solid var(--ink);
  box-shadow: 6px 6px 0 var(--ink);
  margin-bottom: 28px;
}
.ticket-body {
  padding: 20px 18px 16px;
}
.ticket-row {
  display: flex;
  align-items: center;
  border-bottom: 1px dashed var(--line);
  padding: 14px 4px;
  position: relative;
}
.ticket-row:last-child { border-bottom: none; }
.ticket-label {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  width: 50px;
  flex-shrink: 0;
  font-weight: 500;
}
.ticket-input {
  flex: 1;
}
.ticket-input >>> .el-input__inner {
  padding-left: 0 !important;
  font-size: 15px !important;
  font-family: var(--font-serif) !important;
  color: var(--ink) !important;
  letter-spacing: 0.05em;
}
.captcha-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}
.captcha-input { flex: 1; }
.captcha-img {
  width: 88px;
  height: 32px;
  cursor: pointer;
  flex-shrink: 0;
  background: var(--paper-deep);
  position: relative;
  overflow: hidden;
}
.captcha-img::before {
  content: '';
  position: absolute;
  inset: 0;
  border: 1px solid var(--line);
  pointer-events: none;
}
.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* —— 撕齿 —— */
.ticket-perforation {
  height: 12px;
  background-image: radial-gradient(circle, var(--paper) 4px, transparent 4px);
  background-size: 16px 12px;
  background-position: 0 6px;
  background-repeat: repeat-x;
  background-color: var(--ink);
  position: relative;
}
.ticket-perforation::before,
.ticket-perforation::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 16px;
  height: 16px;
  background: var(--paper);
  border-radius: 50%;
  transform: translateY(-50%);
  border: 2px solid var(--ink);
}
.ticket-perforation::before { left: -8px; }
.ticket-perforation::after { right: -8px; }

/* —— 票脚 —— */
.ticket-footer {
  padding: 20px 18px 22px;
  text-align: center;
  background: var(--paper-deep);
  position: relative;
}
.footer-eyebrow {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-bottom: 14px;
}
.ticket-btn {
  width: 100%;
  height: 46px;
  font-family: var(--font-serif) !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  letter-spacing: 0.4em !important;
  border-radius: 0 !important;
  position: relative;
}
.ticket-btn::before,
.ticket-btn::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 8px;
  height: 8px;
  border: 1px solid currentColor;
  transform: translateY(-50%) rotate(45deg);
}
.ticket-btn::before { left: 16px; }
.ticket-btn::after { right: 16px; }

.register-line {
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink-mute);
  margin-top: 14px;
  letter-spacing: 0.1em;
}
.register-line a {
  color: var(--vermilion);
  text-decoration: none;
  font-weight: 500;
}

/* —— 印章 —— */
.stamp {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 64px;
  height: 64px;
  pointer-events: none;
  animation: stamp-pulse 3s ease-in-out infinite;
}
.stamp-ring {
  width: 100%;
  height: 100%;
  border: 2px solid var(--vermilion);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transform: rotate(-12deg);
  background: rgba(181, 55, 30, 0.04);
}
.stamp-ring::before {
  content: '';
  position: absolute;
  inset: 4px;
  border: 1px solid var(--vermilion);
  border-radius: 50%;
}
.stamp-text {
  font-family: var(--font-serif);
  font-size: 12px;
  font-weight: 700;
  color: var(--vermilion);
  letter-spacing: 0.05em;
  opacity: 0.85;
}
@keyframes stamp-pulse {
  0%, 100% { transform: rotate(-12deg) scale(1); }
  50% { transform: rotate(-10deg) scale(1.04); }
}

/* —— 底饰 —— */
.bottom-deco {
  text-align: center;
  margin-top: auto;
  padding-top: 20px;
}
.deco-ornament {
  color: var(--vermilion);
  font-size: 18px;
  margin-bottom: 8px;
  opacity: 0.6;
}
.deco-quote {
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink-mute);
  letter-spacing: 0.2em;
}
</style>
