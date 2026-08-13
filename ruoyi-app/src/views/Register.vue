<template>
  <div class="register-page">
    <div class="top-deco">
      <div class="deco-line"></div>
      <div class="deco-text">TAN · 领票处</div>
      <div class="deco-line"></div>
    </div>

    <div class="masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 新票 —</div>
      <h1 class="masthead-title">领</h1>
      <div class="masthead-sub">填写以下信息 · 印鉴为凭</div>
    </div>

    <div class="form-card">
      <el-form ref="registerForm" :model="form" :rules="rules" class="register-form">
        <div class="form-row">
          <span class="form-label">USER</span>
          <el-input v-model="form.username" placeholder="账号 (2-20字符)" borderless class="form-input" />
        </div>
        <div class="form-row">
          <span class="form-label">NAME</span>
          <el-input v-model="form.nickName" placeholder="昵称" borderless class="form-input" />
        </div>
        <div class="form-row">
          <span class="form-label">PASS</span>
          <el-input v-model="form.password" type="password" placeholder="密码 (5-20字符)" show-password borderless class="form-input" />
        </div>
        <div class="form-row">
          <span class="form-label">AGAIN</span>
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" show-password borderless class="form-input" />
        </div>
        <div class="form-row" v-if="captchaEnabled">
          <span class="form-label">CODE</span>
          <div class="captcha-wrap">
            <el-input v-model="form.code" placeholder="验证码" borderless maxlength="6" class="form-input" />
            <div class="captcha-img" @click="getCaptcha">
              <img :src="captchaUrl" alt="验证码" />
            </div>
          </div>
        </div>
      </el-form>

      <div class="form-perforation"></div>

      <div class="form-footer">
        <div class="footer-eyebrow">— 印 鉴 为 凭 —</div>
        <el-button
          type="primary"
          class="submit-btn"
          :loading="loading"
          @click="handleRegister"
        >
          {{ loading ? '登记中…' : '登 记 领 票' }}
        </el-button>
        <div class="login-line">
          已有票？<router-link to="/login">直接入场 →</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { register, getCaptchaImage } from '@/api/user'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) callback(new Error('请确认密码'))
      else if (value !== this.form.password) callback(new Error('两次输入的密码不一致'))
      else callback()
    }
    return {
      form: {
        username: '',
        nickName: '',
        password: '',
        confirmPassword: '',
        code: '',
        uuid: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 5, max: 20, message: '长度在5到20个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
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
      }).catch(() => { this.captchaEnabled = false })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (!valid) return
        this.loading = true
        const registerData = {
          username: this.form.username,
          nickName: this.form.nickName,
          password: this.form.password,
          confirmPassword: this.form.confirmPassword
        }
        if (this.captchaEnabled) {
          registerData.code = this.form.code
          registerData.uuid = this.form.uuid
        }
        register(registerData).then(() => {
          this.$message.success('登记成功，请入场')
          this.$router.push('/login')
        }).catch(() => { this.getCaptcha() })
          .finally(() => { this.loading = false })
      })
    }
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: var(--paper);
  padding: 40px 24px;
}
.top-deco {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}
.deco-line {
  flex: 1; height: 1px; background: var(--ink); position: relative;
}
.deco-line::after {
  content: ''; position: absolute; inset: 2px 0 0 0;
  height: 1px; background: var(--ink); opacity: 0.3;
}
.deco-text {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.4em;
  color: var(--ink);
  font-weight: 500;
}
.masthead {
  text-align: center;
  margin-bottom: 24px;
}
.masthead-eyebrow {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-bottom: 6px;
}
.masthead-title {
  font-family: var(--font-serif);
  font-size: 64px;
  font-weight: 900;
  color: var(--ink);
  line-height: 1;
  letter-spacing: 0.05em;
}
.masthead-sub {
  font-family: var(--font-serif);
  font-size: 13px;
  color: var(--ink-soft);
  letter-spacing: 0.4em;
  margin-top: 6px;
  padding-left: 0.4em;
}

.form-card {
  background: var(--white-paper);
  border: 2px solid var(--ink);
  box-shadow: 6px 6px 0 var(--ink);
}
.form-row {
  display: flex;
  align-items: center;
  border-bottom: 1px dashed var(--line);
  padding: 14px 18px;
}
.form-row:last-of-type { border-bottom: none; }
.form-label {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  width: 50px;
  flex-shrink: 0;
  font-weight: 500;
}
.form-input { flex: 1; }
.form-input >>> .el-input__inner {
  padding-left: 0 !important;
  font-size: 15px !important;
  font-family: var(--font-serif) !important;
  color: var(--ink) !important;
}
.captcha-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}
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
.captcha-img img { width: 100%; height: 100%; object-fit: cover; }

.form-perforation {
  height: 12px;
  background-image: radial-gradient(circle, var(--paper) 4px, transparent 4px);
  background-size: 16px 12px;
  background-position: 0 6px;
  background-repeat: repeat-x;
  background-color: var(--ink);
  position: relative;
}
.form-perforation::before, .form-perforation::after {
  content: ''; position: absolute; top: 50%;
  width: 16px; height: 16px; background: var(--paper);
  border-radius: 50%; transform: translateY(-50%); border: 2px solid var(--ink);
}
.form-perforation::before { left: -8px; }
.form-perforation::after { right: -8px; }

.form-footer {
  padding: 18px 18px 22px;
  text-align: center;
  background: var(--paper-deep);
}
.footer-eyebrow {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-bottom: 14px;
}
.submit-btn {
  width: 100%;
  height: 46px;
  font-family: var(--font-serif) !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  letter-spacing: 0.4em !important;
  border-radius: 0 !important;
}
.login-line {
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink-mute);
  margin-top: 14px;
  letter-spacing: 0.1em;
}
.login-line a {
  color: var(--vermilion);
  text-decoration: none;
  font-weight: 500;
}
</style>
