<template>
  <div class="profile-page">
    <!-- 报头 -->
    <div class="page-masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 个 人 档 案 —</div>
      <h1 class="masthead-title">我 的</h1>
      <div class="masthead-sub">PROFILE · IDENTITY</div>
    </div>

    <!-- 档案卡 -->
    <div class="id-card">
      <div class="id-header">
        <div class="id-no">№ {{ userNo }}</div>
        <div class="id-stamp">摊 主</div>
      </div>
      <div class="id-body">
        <div class="id-avatar">
          <span class="avatar-mark">{{ avatarMark }}</span>
        </div>
        <div class="id-info">
          <div class="id-name">{{ userInfo.nickName || userInfo.userName || '访客' }}</div>
          <div class="id-username">@{{ userInfo.userName || 'guest' }}</div>
        </div>
      </div>
      <div class="id-footer">
        <div class="id-meta">
          <div class="meta-label">入 摊</div>
          <div class="meta-value mono">{{ joinDate }}</div>
        </div>
        <div class="id-meta">
          <div class="meta-label">身 份</div>
          <div class="meta-value">校 园 摊 主</div>
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-strip">
      <div class="stat-item" @click="$router.push('/my-goods')">
        <div class="stat-num">{{ stats.goodsCount }}</div>
        <div class="stat-label">摆 摊</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item" @click="$router.push('/order')">
        <div class="stat-num">{{ stats.orderCount }}</div>
        <div class="stat-label">订 单</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item" @click="$router.push('/my-favorite')">
        <div class="stat-num">{{ stats.favCount }}</div>
        <div class="stat-label">收 藏</div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="menu-list">
      <div class="menu-item" @click="$router.push('/my-goods')">
        <div class="menu-no">01</div>
        <i class="menu-icon el-icon-s-goods"></i>
        <span class="menu-text">我 摆 的 摊</span>
        <i class="menu-arrow">→</i>
      </div>
      <div class="menu-item" @click="$router.push('/order')">
        <div class="menu-no">02</div>
        <i class="menu-icon el-icon-s-order"></i>
        <span class="menu-text">我 的 订 单</span>
        <i class="menu-arrow">→</i>
      </div>
      <div class="menu-item" @click="$router.push('/my-favorite')">
        <div class="menu-no">03</div>
        <i class="menu-icon el-icon-star-on"></i>
        <span class="menu-text">我 的 收 藏</span>
        <i class="menu-arrow">→</i>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item" @click="showAbout = true">
        <div class="menu-no">04</div>
        <i class="menu-icon el-icon-info"></i>
        <span class="menu-text">关 于 书 市</span>
        <i class="menu-arrow">→</i>
      </div>
      <div class="menu-item logout" @click="handleLogout">
        <div class="menu-no">05</div>
        <i class="menu-icon el-icon-switch-button"></i>
        <span class="menu-text">退 场</span>
        <i class="menu-arrow">→</i>
      </div>
    </div>

    <!-- 关于弹窗 -->
    <el-dialog
      :visible.sync="showAbout"
      width="320px"
      :show-close="true"
      custom-class="about-dialog"
    >
      <div class="about-content">
        <div class="about-mark">摊</div>
        <div class="about-title">摊 · 校 园 二 手 书 市</div>
        <div class="about-rule"></div>
        <p class="about-quote">「 物 尽 其 用 · 知 识 流 转 」</p>
        <div class="about-version mono">VOL. 002 · JUL. 2026</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserStats } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    return {
      showAbout: false,
      stats: {
        goodsCount: 0,
        orderCount: 0,
        favCount: 0
      }
    }
  },
  created() {
    const token = this.$store.state.token
    if (token) {
      if (!this.$store.state.userInfo) {
        this.$store.dispatch('GetUserInfo').then(() => {
          this.loadStats()
        }).catch(() => {})
      } else {
        this.loadStats()
      }
    }
  },
  activated() {
    const token = this.$store.state.token
    if (token) {
      this.loadStats()
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.userInfo || {}
    },
    userNo() {
      const id = this.userInfo.userId || 0
      return String(id).padStart(4, '0')
    },
    avatarMark() {
      const n = this.userInfo.nickName || this.userInfo.userName || '?'
      return n.substring(0, 1).toUpperCase()
    },
    joinDate() {
      return this.userInfo.createTime ? this.userInfo.createTime.split(' ')[0] : '—'
    }
  },
  methods: {
    loadStats() {
      getUserStats().then(res => {
        if (res.code === 200) {
          this.stats = res.data || { goodsCount: 0, orderCount: 0, favCount: 0 }
        }
      }).catch(() => {})
    },
    handleLogout() {
      this.$confirm('确认退场？', '退 场', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('Logout').then(() => {
          this.$router.push('/login')
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: 40px;
}

.page-masthead {
  text-align: center;
  padding: 16px 20px 18px;
  border-bottom: 2px solid var(--ink);
  position: relative;
}
.page-masthead::after {
  content: '';
  position: absolute;
  inset: auto 0 -5px 0;
  height: 1px;
  background: var(--ink);
}
.masthead-eyebrow {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-bottom: 4px;
}
.masthead-title {
  font-family: var(--font-serif);
  font-size: 38px;
  font-weight: 900;
  color: var(--ink);
  line-height: 1;
  letter-spacing: 0.4em;
  padding-left: 0.4em;
}
.masthead-sub {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.4em;
  color: var(--ink-mute);
  margin-top: 6px;
}

/* —— 档案卡 —— */
.id-card {
  margin: 20px 16px 0;
  background: var(--white-paper);
  border: 1.5px solid var(--ink);
  box-shadow: 5px 5px 0 var(--ink);
  position: relative;
}
.id-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 14px;
  background: var(--paper-deep);
  border-bottom: 1px solid var(--ink);
}
.id-no {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.2em;
  color: var(--ink-soft);
}
.id-stamp {
  font-family: var(--font-serif);
  font-size: 10px;
  color: var(--vermilion);
  border: 1.5px solid var(--vermilion);
  padding: 1px 6px;
  letter-spacing: 0.2em;
  transform: rotate(2deg);
}
.id-body {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 14px;
}
.id-avatar {
  width: 56px;
  height: 56px;
  background: var(--ink);
  color: var(--paper);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}
.id-avatar::before {
  content: '';
  position: absolute;
  inset: 3px;
  border: 1px solid var(--paper);
  opacity: 0.3;
}
.avatar-mark {
  font-family: var(--font-serif);
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}
.id-info { flex: 1; min-width: 0; }
.id-name {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.05em;
  margin-bottom: 4px;
}
.id-username {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--ink-mute);
}
.id-footer {
  display: flex;
  border-top: 1px dashed var(--line);
}
.id-meta {
  flex: 1;
  padding: 10px 14px;
  border-right: 1px dashed var(--line);
  text-align: center;
}
.id-meta:last-child { border-right: none; }
.meta-label {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  margin-bottom: 4px;
}
.meta-value {
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink);
  letter-spacing: 0.1em;
  font-weight: 500;
}
.meta-value.mono {
  font-family: var(--font-mono);
  font-size: 11px;
}

/* —— 数据统计 —— */
.stats-strip {
  margin: 16px 16px 0;
  background: var(--white-paper);
  border: 1px solid var(--ink);
  display: flex;
  align-items: stretch;
}
.stat-item {
  flex: 1;
  padding: 14px 0 12px;
  text-align: center;
  cursor: pointer;
  transition: background 0.2s;
}
.stat-item:active { background: var(--paper-deep); }
.stat-num {
  font-family: var(--font-serif);
  font-size: 24px;
  font-weight: 700;
  color: var(--vermilion);
  line-height: 1;
  margin-bottom: 4px;
}
.stat-label {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--ink-soft);
  letter-spacing: 0.3em;
}
.stat-divider {
  width: 1px;
  background: var(--line);
  margin: 12px 0;
}

/* —— 菜单列表 —— */
.menu-list {
  margin: 16px 16px 0;
  background: var(--white-paper);
  border: 1px solid var(--ink);
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 14px;
  border-bottom: 1px solid var(--line-soft);
  cursor: pointer;
  transition: background 0.2s;
}
.menu-item:last-child { border-bottom: none; }
.menu-item:active { background: var(--paper-deep); }
.menu-item.logout { color: var(--vermilion); }
.menu-item.logout:active { background: rgba(181, 55, 30, 0.06); }
.menu-no {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.1em;
  color: var(--ink-fade);
  width: 20px;
}
.menu-icon {
  font-size: 18px;
  color: var(--ink-soft);
  width: 24px;
  text-align: center;
}
.menu-item.logout .menu-icon { color: var(--vermilion); }
.menu-text {
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--ink);
  letter-spacing: 0.15em;
  flex: 1;
  font-weight: 500;
}
.menu-item.logout .menu-text { color: var(--vermilion); }
.menu-arrow {
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--ink-fade);
}

/* —— 关于 —— */
.about-content {
  text-align: center;
  padding: 20px 10px;
}
.about-mark {
  font-family: var(--font-serif);
  font-size: 80px;
  font-weight: 900;
  color: var(--ink);
  line-height: 1;
  margin-bottom: 12px;
}
.about-title {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.2em;
  margin-bottom: 14px;
}
.about-rule {
  height: 1px;
  background: var(--ink);
  position: relative;
  margin: 12px 40px;
}
.about-rule::after {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.about-quote {
  font-family: var(--font-serif);
  font-size: 13px;
  color: var(--ink-soft);
  letter-spacing: 0.3em;
  padding-left: 0.3em;
  margin: 14px 0;
}
.about-version {
  font-size: 10px;
  color: var(--ink-mute);
  letter-spacing: 0.3em;
}

.about-dialog {
  background: var(--white-paper) !important;
  border: 2px solid var(--ink);
  border-radius: 0 !important;
  box-shadow: 6px 6px 0 var(--ink) !important;
}
.about-dialog >>> .el-dialog__header { padding: 0; }
.about-dialog >>> .el-dialog__body { padding: 0; }
.about-dialog >>> .el-dialog__headerbtn {
  top: 8px;
  right: 8px;
}
.about-dialog >>> .el-dialog__close {
  color: var(--ink) !important;
  font-size: 20px;
  font-family: var(--font-serif);
}
</style>
