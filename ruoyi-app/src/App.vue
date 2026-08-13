<template>
  <div id="app">
    <div class="app-container">
      <!-- 顶部导航栏：复古报刊头 -->
      <div class="top-nav" v-if="showNavBar">
        <div class="nav-inner">
          <div class="nav-back" v-if="canBack" @click="goBack">
            <span class="back-arrow">←</span>
          </div>
          <div class="nav-title-block">
            <div class="nav-eyebrow">TAN · 二手书市</div>
            <div class="nav-title">{{ pageTitle }}</div>
          </div>
          <div class="nav-right">
            <slot name="nav-right"></slot>
          </div>
        </div>
        <div class="nav-rule"></div>
      </div>

      <!-- 页面内容 -->
      <div class="page-content" :class="{ 'has-nav': showNavBar, 'has-tabbar': showTabBar }">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>

      <!-- 底部Tab栏：旧书摊档口 -->
      <div class="bottom-tabbar" v-if="showTabBar">
        <div class="tabbar-rule"></div>
        <div class="tabbar-inner">
          <div
            class="tab-item"
            :class="{ active: activeTab === tab.path }"
            v-for="(tab, i) in tabs"
            :key="tab.path"
            @click="switchTab(tab.path)"
          >
            <div class="tab-no">0{{ i + 1 }}</div>
            <i :class="tab.icon"></i>
            <span class="tab-text">{{ tab.name }}</span>
            <div class="tab-active-bar" v-if="activeTab === tab.path"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      tabs: [
        { name: '书市', path: '/home', icon: 'el-icon-s-home' },
        { name: '分类', path: '/category', icon: 'el-icon-menu' },
        { name: '摆摊', path: '/publish', icon: 'el-icon-circle-plus-outline' },
        { name: '订单', path: '/order', icon: 'el-icon-s-order' },
        { name: '我的', path: '/profile', icon: 'el-icon-user-solid' }
      ]
    }
  },
  computed: {
    showTabBar() {
      return this.$route.meta.showTabBar
    },
    showNavBar() {
      return this.$route.meta.title && !this.$route.meta.showTabBar
    },
    pageTitle() {
      return this.$route.meta.title || ''
    },
    activeTab() {
      return this.$route.path
    },
    canBack() {
      return window.history.length > 1
    }
  },
  methods: {
    switchTab(path) {
      if (this.$route.path !== path) {
        const requiresAuth = this.tabs.find(tab => tab.path === path)
        if (requiresAuth && (path === '/publish' || path === '/order')) {
          const token = this.$store.state.token
          if (!token) {
            this.$router.push({ path: '/login', query: { redirect: path } })
            return
          }
        }
        this.$router.push(path)
      }
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style>
:root {
  /* —— 色板：旧纸 + 墨 + 朱砂 —— */
  --paper: #F4EFE3;
  --paper-deep: #ECE4D0;
  --paper-edge: #D9CFB8;
  --ink: #1F1B16;
  --ink-soft: #4A413A;
  --ink-mute: #8A7E6E;
  --ink-fade: #B5A98F;
  --vermilion: #B5371E;       /* 朱砂 */
  --vermilion-deep: #8C2817;
  --moss: #4A5D3A;            /* 墨绿 */
  --kraft: #8B6F47;           /* 牛皮纸 */
  --gold: #B89547;            /* 烫金 */
  --line: #C8B998;
  --line-soft: #DDD2B3;
  --white-paper: #FBF7EC;
  --danger: #B5371E;
  --success: #4A5D3A;
  --warning: #B89547;
  --info: #8A7E6E;

  /* —— 字体 —— */
  --font-serif: 'Noto Serif SC', 'Songti SC', 'STSong', 'SimSun', serif;
  --font-sans: 'Noto Sans SC', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  --font-mono: 'DM Mono', 'SF Mono', Menlo, Consolas, monospace;
  --font-brush: 'ZCOOL XiaoWei', 'Noto Serif SC', serif;

  /* —— 阴影 —— */
  --shadow-paper: 0 1px 0 var(--line-soft), 0 2px 8px rgba(31, 27, 22, 0.06);
  --shadow-lift: 0 8px 24px rgba(31, 27, 22, 0.12);
  --shadow-stamp: 0 0 0 1px var(--vermilion) inset;

  /* —— 动效曲线 —— */
  --ease-ink: cubic-bezier(0.16, 1, 0.3, 1);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  -webkit-tap-highlight-color: transparent;
}

html, body {
  height: 100%;
  background: #1F1B16;
  font-family: var(--font-sans);
  color: var(--ink);
  font-size: 14px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100%;
}

.app-container {
  max-width: 768px;
  margin: 0 auto;
  min-height: 100vh;
  position: relative;
  background: var(--paper);
  box-shadow: 0 0 60px rgba(0, 0, 0, 0.4);
  /* 纸张纹理：双层叠加 */
  background-image:
    radial-gradient(at 20% 10%, rgba(184, 149, 71, 0.04) 0px, transparent 50%),
    radial-gradient(at 80% 80%, rgba(181, 55, 30, 0.03) 0px, transparent 50%),
    repeating-linear-gradient(0deg, transparent, transparent 24px, rgba(31, 27, 22, 0.015) 24px, rgba(31, 27, 22, 0.015) 25px);
}

/* 纸面颗粒：SVG噪点叠加 */
.app-container::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='200' height='200'><filter id='n'><feTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='2' seed='4'/><feColorMatrix values='0 0 0 0 0.12  0 0 0 0 0.10  0 0 0 0 0.08  0 0 0 0.05 0'/></filter><rect width='100%' height='100%' filter='url(%23n)'/></svg>");
  opacity: 0.5;
  mix-blend-mode: multiply;
  z-index: 0;
}

.app-container > * { position: relative; z-index: 1; }

/* —— 顶部导航 —— */
.top-nav {
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 768px;
  background: var(--paper);
  z-index: 100;
}
.nav-inner {
  height: 52px;
  display: flex;
  align-items: center;
  padding: 0 12px;
  position: relative;
}
.nav-back {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
}
.back-arrow {
  font-family: var(--font-serif);
  font-size: 22px;
  color: var(--ink);
  font-weight: 400;
  line-height: 1;
}
.nav-title-block {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.nav-eyebrow {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--vermilion);
  font-weight: 500;
}
.nav-title {
  font-family: var(--font-serif);
  font-size: 16px;
  color: var(--ink);
  font-weight: 600;
  letter-spacing: 0.1em;
}
.nav-right {
  width: 36px;
  flex-shrink: 0;
}
.nav-rule {
  height: 1px;
  background: var(--ink);
  position: relative;
}
.nav-rule::before {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}

/* —— 页面内容 —— */
.page-content { padding-bottom: env(safe-area-inset-bottom); }
.page-content.has-nav { padding-top: 53px; }
.page-content.has-tabbar { padding-bottom: calc(68px + env(safe-area-inset-bottom)); }

/* —— 底部 Tab —— */
.bottom-tabbar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 768px;
  z-index: 100;
  padding-bottom: env(safe-area-inset-bottom);
  background: var(--paper);
}
.tabbar-rule {
  height: 1px;
  background: var(--ink);
  position: relative;
}
.tabbar-rule::after {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.tabbar-inner {
  height: 56px;
  display: flex;
  background: var(--paper);
}
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--ink-mute);
  position: relative;
  padding-top: 6px;
  transition: color 0.3s var(--ease-ink);
}
.tab-no {
  position: absolute;
  top: 4px;
  right: 50%;
  transform: translateX(22px);
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.1em;
  color: var(--ink-fade);
  transition: color 0.3s;
}
.tab-item.active .tab-no { color: var(--vermilion); }
.tab-item i {
  font-size: 18px;
  transition: transform 0.3s var(--ease-ink);
}
.tab-text {
  font-family: var(--font-serif);
  font-size: 11px;
  margin-top: 2px;
  letter-spacing: 0.15em;
}
.tab-item.active {
  color: var(--ink);
}
.tab-item.active i {
  transform: translateY(-1px);
  color: var(--vermilion);
}
.tab-active-bar {
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 16px;
  height: 2px;
  background: var(--vermilion);
}

/* —— 通用 —— */
.page-wrapper {
  padding: 16px;
}
.section-eyebrow {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.4em;
  color: var(--vermilion);
  text-transform: uppercase;
  font-weight: 500;
}
.section-title {
  font-family: var(--font-serif);
  font-size: 22px;
  font-weight: 700;
  color: var(--ink);
  line-height: 1.2;
  margin: 6px 0 14px;
  letter-spacing: 0.05em;
}
.section-title::after {
  content: '';
  display: inline-block;
  width: 4px;
  height: 4px;
  background: var(--vermilion);
  border-radius: 50%;
  margin-left: 6px;
  vertical-align: middle;
}
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--ink-fade);
}
.empty-state i {
  font-size: 56px;
  margin-bottom: 16px;
  color: var(--ink-fade);
  opacity: 0.5;
}
.empty-state p {
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.2em;
}

/* —— 页面切换动画 —— */
.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.25s var(--ease-ink), transform 0.25s var(--ease-ink);
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(6px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* —— Element 主题覆盖 —— */
.el-button--primary {
  background: var(--vermilion) !important;
  border-color: var(--vermilion) !important;
  font-family: var(--font-serif) !important;
  letter-spacing: 0.1em;
}
.el-button--primary:hover {
  background: var(--vermilion-deep) !important;
  border-color: var(--vermilion-deep) !important;
  transform: translateY(-1px);
}
.el-input__inner {
  background: transparent !important;
  font-family: var(--font-sans) !important;
  border: none !important;
  border-bottom: 1px solid var(--line) !important;
  border-radius: 0 !important;
  color: var(--ink) !important;
  padding-left: 0 !important;
}
.el-input__inner:focus {
  border-bottom-color: var(--vermilion) !important;
}
.el-input__inner::placeholder {
  color: var(--ink-fade);
  font-family: var(--font-serif);
  letter-spacing: 0.05em;
}
.el-input__prefix {
  color: var(--ink-mute) !important;
  left: 0 !important;
}
.el-input__inner {
  padding-left: 28px !important;
}
.el-form-item__label {
  font-family: var(--font-serif) !important;
  color: var(--ink-soft) !important;
  letter-spacing: 0.1em;
  font-size: 13px !important;
}
.el-tag {
  border-radius: 0 !important;
  font-family: var(--font-serif) !important;
  letter-spacing: 0.1em;
  border: none !important;
}
.el-message-box {
  background: var(--paper) !important;
  border: 1px solid var(--ink) !important;
  border-radius: 0 !important;
}
.el-message-box__btns .el-button {
  border-radius: 0 !important;
}
.el-tabs__active-bar {
  background-color: var(--vermilion) !important;
}
.el-tabs__item.is-active {
  color: var(--ink) !important;
  font-family: var(--font-serif) !important;
  font-weight: 600 !important;
}
.el-tabs__item {
  font-family: var(--font-serif) !important;
  color: var(--ink-mute) !important;
  letter-spacing: 0.1em !important;
}
.el-loading-mask {
  background-color: rgba(244, 239, 227, 0.85) !important;
}
.el-radio__input.is-checked .el-radio__inner {
  background: var(--vermilion) !important;
  border-color: var(--vermilion) !important;
}
.el-radio__input.is-checked + .el-radio__label {
  color: var(--vermilion) !important;
}

/* —— 滚动条 —— */
::-webkit-scrollbar { width: 4px; height: 4px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: var(--ink-fade); border-radius: 0; }

/* 选区色 */
::selection {
  background: var(--vermilion);
  color: var(--paper);
}
</style>
