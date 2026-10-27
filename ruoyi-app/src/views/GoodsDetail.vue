<template>
  <div class="detail-page">
    <div v-if="goods" class="detail-wrapper">
      <!-- 杂志封面区 -->
      <section class="cover-section">
        <div class="cover-frame">
          <el-carousel
            height="380px"
            indicator-position="outside"
            v-if="goods.imageList && goods.imageList.length > 0"
            :autoplay="false"
            arrow="always"
          >
            <el-carousel-item v-for="(img, index) in goods.imageList" :key="index">
              <img :src="img" class="carousel-img" @click="previewImage(index)" />
            </el-carousel-item>
          </el-carousel>
          <div class="cover-placeholder" v-else>
            <div class="placeholder-mark">{{ goods.goodsName ? goods.goodsName[0] : '摊' }}</div>
            <div class="placeholder-line"></div>
            <div class="placeholder-no">№ {{ formattedNo }}</div>
          </div>
        </div>

        <div class="status-stamp" v-if="goods.goodsStatus === '2'">
          <div class="stamp-content">已 预 订</div>
        </div>
        <div class="status-stamp sold" v-else-if="goods.goodsStatus === '3'">
          <div class="stamp-content">已 售 出</div>
        </div>
        <div class="status-stamp off" v-else-if="goods.goodsStatus === '4'">
          <div class="stamp-content">已 下 架</div>
        </div>
      </section>

      <!-- 标题区 -->
      <section class="title-section">
        <div class="title-meta">
          <span class="meta-no">№ {{ formattedNo }}</span>
          <span class="meta-date">{{ publishDate }}</span>
        </div>
        <h1 class="goods-title">{{ goods.goodsName }}</h1>
        <div class="title-tags">
          <span class="tag-cond" v-if="conditionText">{{ conditionText }}</span>
          <span class="tag-cat" v-if="goods.categoryName">{{ goods.categoryName }}</span>
          <span class="tag-views" v-if="goods.viewCount !== undefined">
            <i class="el-icon-view"></i> {{ goods.viewCount }}
          </span>
        </div>
      </section>

      <!-- 价格区 -->
      <section class="price-section">
        <div class="price-block">
          <div class="price-main">
            <span class="price-symbol">¥</span>
            <span class="price-int">{{ priceInt }}</span>
            <span v-if="priceDec" class="price-dec">.{{ priceDec }}</span>
          </div>
          <div class="price-original" v-if="goods.originalPrice">
            原价 ¥{{ goods.originalPrice }}
          </div>
        </div>
        <div class="discount-tag" v-if="discountPercent">
          省 {{ discountPercent }}%
        </div>
      </section>

      <!-- 出版信息 -->
      <section class="info-section">
        <div class="info-row" v-if="goods.courseName">
          <span class="info-label">课 程</span>
          <span class="info-value">{{ goods.courseName }}</span>
        </div>
        <div class="info-row" v-if="goods.isbn">
          <span class="info-label">ISBN</span>
          <span class="info-value mono">{{ goods.isbn }}</span>
        </div>
        <div class="info-row" v-if="goods.publisherName">
          <span class="info-label">摊 主</span>
          <span class="info-value">
            <i class="pub-mark">❍</i> {{ goods.publisherName }}
          </span>
        </div>
        <div class="info-row">
          <span class="info-label">入 摊</span>
          <span class="info-value mono">{{ goods.createTime || '—' }}</span>
        </div>
      </section>

      <!-- 描述 -->
      <section class="desc-section" v-if="goods.description">
        <div class="section-header">
          <div class="section-mark"></div>
          <span class="section-eyebrow">DESCRIPTION</span>
          <span class="section-cn">摊 主 笔 记</span>
        </div>
        <div class="desc-content">
          <div class="desc-quote-mark">「</div>
          <p class="desc-text">{{ goods.description }}</p>
        </div>
      </section>

      <!-- 二手书宣言 -->
      <section class="manifesto">
        <div class="manifesto-rule"></div>
        <p class="manifesto-text">「 物 尽 其 用 · 知 识 流 转 」</p>
        <p class="manifesto-sub">THIS BOOK WILL BE LOVED ONCE MORE.</p>
        <div class="manifesto-rule"></div>
      </section>

      <!-- 底部操作 -->
      <section class="action-section">
        <div class="action-row">
          <button class="action-btn secondary" @click="toggleFavorite">
            <i :class="isFavorited ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            <span>{{ isFavorited ? '已 收 藏' : '收 藏' }}</span>
          </button>
          <button
            class="action-btn primary"
            :disabled="goods.goodsStatus !== '1' || isMine"
            @click="handleBuy"
          >
            {{ buyButtonText }}
          </button>
        </div>
      </section>
    </div>

    <div class="loading-state" v-else-if="loadFailed">
      <i class="el-icon-warning-outline"></i>
      <p>商 品 不 存 在 或 已 下 架</p>
      <div class="back-link" @click="$router.back()">— 返 回 —</div>
    </div>

    <div class="loading-state" v-else>
      <i class="el-icon-loading"></i>
      <p>加 载 中</p>
    </div>

    <!-- 图片预览：用 el-dialog 承载（原来写的 <el-image-viewer> 不是 element-ui 对外注册的组件，
         渲染时报 Unknown custom element，点封面其实没有任何反应） -->
    <el-dialog :visible.sync="showViewer" width="92%" top="8vh">
      <img v-if="previewUrl" :src="previewUrl" class="viewer-img" alt="" />
    </el-dialog>
  </div>
</template>

<script>
import { getGoods } from '@/api/goods'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { createOrder } from '@/api/order'
import { toImageList } from '@/utils/image'

export default {
  name: 'GoodsDetail',
  data() {
    return {
      goods: null,
      isFavorited: false,
      showViewer: false,
      previewIndex: 0,
      loading: false,
      loadFailed: false
    }
  },
  computed: {
    formattedNo() {
      return String(this.goods?.goodsId || 0).padStart(3, '0')
    },
    formattedPrice() {
      return Number(this.goods?.price || 0).toFixed(2)
    },
    priceInt() { return this.formattedPrice.split('.')[0] },
    priceDec() { return this.formattedPrice.split('.')[1] },
    discountPercent() {
      if (!this.goods?.originalPrice || !this.goods?.price) return 0
      const o = Number(this.goods.originalPrice)
      const p = Number(this.goods.price)
      if (o <= 0) return 0
      return Math.round((1 - p / o) * 100)
    },
    conditionText() {
      const map = { '1': '全新', '2': '九成新', '3': '八成新', '4': '七成新' }
      return map[this.goods?.qualityLevel] || ''
    },
    publishDate() {
      return this.goods?.createTime ? this.goods.createTime.split(' ')[0] : ''
    },
    previewUrl() {
      const list = (this.goods && this.goods.imageList) || []
      return list[this.previewIndex] || ''
    },
    isMine() {
      const uid = this.$store.state.userInfo && this.$store.state.userInfo.userId
      return !!uid && !!this.goods && String(uid) === String(this.goods.publisherId)
    },
    buyButtonText() {
      if (this.isMine) return '我 发 布 的'
      const s = this.goods?.goodsStatus
      if (s === '2') return '已 被 预 订'
      if (s === '3') return '已 售 出'
      if (s === '4') return '已 下 架'
      return '立 即 购 买'
    }
  },
  created() {
    // 详情接口是公开的，路由守卫不会为已登录用户拉一次用户信息；
    // 这里补一次，才能判断「这件商品是不是我发布的」并把购买按钮关掉。
    if (this.$store.state.token && !this.$store.state.userInfo) {
      this.$store.dispatch('GetUserInfo').catch(() => {})
    }
    this.loadGoodsDetail()
  },
  methods: {
    loadGoodsDetail() {
      const id = this.$route.params.id
      this.loading = true
      this.loadFailed = false
      getGoods(id).then(res => {
        this.goods = res.data
        // cover_image 存的是相对路径，渲染前统一拼接口前缀（见 utils/image.js）
        this.$set(this.goods, 'imageList', toImageList(this.goods.coverImage))
        if (this.$store.state.token) {
          this.checkFavStatus()
        }
      }).catch(() => {
        this.loadFailed = true
      }).finally(() => { this.loading = false })
    },
    checkFavStatus() {
      checkFavorite(this.goods.goodsId).then(res => {
        this.isFavorited = res.data === true
      }).catch(() => {})
    },
    previewImage(idx) {
      this.previewIndex = idx
      this.showViewer = true
    },
    toggleFavorite() {
      if (!this.$store.state.token) {
        this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
        return
      }
      toggleFavorite(this.goods.goodsId).then(() => {
        this.isFavorited = !this.isFavorited
        this.$message.success(this.isFavorited ? '收藏成功' : '已取消收藏')
      }).catch(() => {})
    },
    handleBuy() {
      if (this.goods.goodsStatus !== '1' || this.isMine) return
      if (!this.$store.state.token) {
        this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
        return
      }
      this.$confirm('确认购买此商品吗？购买后将与摊主联系面交。', '下 单', {
        confirmButtonText: '确 认 下 单',
        cancelButtonText: '再 考 虑',
        type: 'info'
      }).then(() => {
        createOrder({ goodsId: this.goods.goodsId }).then(() => {
          this.$message.success('下单成功！')
          this.$router.push('/order')
        }).catch(() => {})
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: 100px;
}
.detail-wrapper {
  max-width: 600px;
  margin: 0 auto;
}

/* —— 封面 —— */
.cover-section {
  position: relative;
  background: var(--paper-deep);
  padding: 0;
}
.cover-frame {
  position: relative;
  background: var(--ink);
  overflow: hidden;
}
.cover-frame >>> .el-carousel__container {
  background: var(--ink);
}
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: sepia(0.06) saturate(0.92);
}
.cover-placeholder {
  height: 380px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #2D4A3E;
  color: #C9A875;
  font-family: var(--font-serif);
  gap: 12px;
}
.placeholder-mark {
  font-size: 120px;
  font-weight: 900;
  line-height: 1;
  letter-spacing: 0.1em;
}
.placeholder-line {
  width: 60px;
  height: 1px;
  background: currentColor;
  opacity: 0.6;
}
.placeholder-no {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.3em;
  opacity: 0.7;
}

/* —— 状态印章 —— */
.status-stamp {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 90px;
  height: 90px;
  background: var(--vermilion);
  color: var(--paper);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 3px solid var(--paper);
  transform: rotate(-15deg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  animation: stamp-pulse 2s ease-in-out infinite;
}
.status-stamp.sold { background: #6B7C5A; }
.status-stamp.off { background: var(--ink-soft); }
.stamp-content {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-align: center;
  line-height: 1.2;
}
.stamp-content::before, .stamp-content::after {
  content: '';
  display: block;
  width: 50%;
  height: 1px;
  background: currentColor;
  margin: 4px auto;
  opacity: 0.5;
}
@keyframes stamp-pulse {
  0%, 100% { transform: rotate(-15deg) scale(1); }
  50% { transform: rotate(-12deg) scale(1.04); }
}

/* —— 标题区 —— */
.title-section {
  padding: 20px 18px 12px;
  border-bottom: 1px solid var(--line);
  background: var(--white-paper);
  position: relative;
}
.title-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.2em;
  color: var(--ink-mute);
  margin-bottom: 8px;
}
.meta-no { color: var(--vermilion); font-weight: 500; }
.goods-title {
  font-family: var(--font-serif);
  font-size: 22px;
  font-weight: 700;
  color: var(--ink);
  line-height: 1.3;
  letter-spacing: 0.05em;
  margin-bottom: 10px;
}
.title-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.tag-cond, .tag-cat {
  font-family: var(--font-serif);
  font-size: 11px;
  padding: 2px 8px;
  background: var(--paper-deep);
  color: var(--ink-soft);
  border: 1px solid var(--line);
  letter-spacing: 0.1em;
}
.tag-cond {
  background: var(--vermilion);
  color: var(--paper);
  border-color: var(--vermilion);
}
.tag-views {
  margin-left: auto;
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--ink-mute);
  letter-spacing: 0.1em;
}

/* —— 价格区 —— */
.price-section {
  background: var(--white-paper);
  padding: 16px 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--line);
  position: relative;
}
.price-block {
  display: flex;
  align-items: baseline;
  gap: 10px;
}
.price-main {
  font-family: var(--font-serif);
  color: var(--vermilion);
  display: flex;
  align-items: baseline;
  font-weight: 700;
}
.price-symbol { font-size: 16px; margin-right: 2px; }
.price-int { font-size: 30px; letter-spacing: -0.02em; }
.price-dec { font-size: 16px; }
.price-original {
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--ink-fade);
  text-decoration: line-through;
  letter-spacing: 0.1em;
}
.discount-tag {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--paper);
  background: var(--moss);
  padding: 3px 8px;
  letter-spacing: 0.1em;
  font-weight: 500;
}

/* —— 信息表 —— */
.info-section {
  background: var(--white-paper);
  padding: 4px 18px 14px;
  border-bottom: 1px solid var(--line);
}
.info-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px dashed var(--line-soft);
  align-items: center;
}
.info-row:last-child { border-bottom: none; }
.info-label {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  width: 60px;
  flex-shrink: 0;
  font-weight: 500;
}
.info-value {
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--ink);
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
  gap: 4px;
}
.info-value.mono {
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: 0.1em;
}
.pub-mark {
  color: var(--vermilion);
  font-size: 10px;
}

/* —— 节标题 —— */
.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 24px 18px 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--ink);
  position: relative;
}
.section-header::after {
  content: '';
  position: absolute;
  inset: auto 0 1px 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.section-mark {
  width: 4px;
  height: 14px;
  background: var(--vermilion);
}
.section-eyebrow {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--vermilion);
  font-weight: 500;
}
.section-cn {
  font-family: var(--font-serif);
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.2em;
}

/* —— 描述 —— */
.desc-section {
  background: var(--white-paper);
  padding: 0 0 24px;
  border-bottom: 1px solid var(--line);
}
.desc-content {
  padding: 8px 18px 0;
  position: relative;
}
.desc-quote-mark {
  font-family: var(--font-serif);
  font-size: 48px;
  color: var(--vermilion);
  line-height: 0.6;
  font-weight: 700;
  margin-bottom: 8px;
  opacity: 0.6;
}
.desc-text {
  font-family: var(--font-serif);
  font-size: 15px;
  line-height: 1.9;
  color: var(--ink-soft);
  letter-spacing: 0.05em;
  text-indent: 2em;
}

/* —— 宣言 —— */
.manifesto {
  margin: 32px 18px 24px;
  text-align: center;
}
.manifesto-rule {
  height: 1px;
  background: var(--ink);
  position: relative;
  margin: 8px 40px;
}
.manifesto-rule::after {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.manifesto-text {
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--ink);
  letter-spacing: 0.4em;
  font-weight: 500;
  padding-left: 0.4em;
  margin: 12px 0 6px;
}
.manifesto-sub {
  font-family: var(--font-mono);
  font-size: 9px;
  color: var(--ink-mute);
  letter-spacing: 0.4em;
  margin: 0;
}

/* —— 操作区 —— */
.action-section {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 768px;
  background: var(--paper);
  border-top: 1px solid var(--ink);
  padding: 10px 16px calc(10px + env(safe-area-inset-bottom));
  z-index: 50;
}
.action-row {
  display: flex;
  gap: 10px;
}
.action-btn {
  flex: 1;
  height: 44px;
  border: 1.5px solid var(--ink);
  background: var(--white-paper);
  color: var(--ink);
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.3em;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.2s;
  font-weight: 500;
}
.action-btn i { font-size: 16px; }
.action-btn.secondary:active {
  background: var(--paper-deep);
}
.action-btn.primary {
  background: var(--vermilion);
  border-color: var(--vermilion);
  color: var(--paper);
  font-weight: 600;
}
.action-btn.primary:active { background: var(--vermilion-deep); }
.action-btn.primary:disabled {
  background: var(--ink-mute);
  border-color: var(--ink-mute);
  color: var(--paper-deep);
  cursor: not-allowed;
}

.loading-state {
  text-align: center;
  padding: 120px 20px;
  color: var(--ink-fade);
}
.loading-state i {
  font-size: 32px;
  margin-bottom: 12px;
}
.loading-state p {
  font-family: var(--font-serif);
  letter-spacing: 0.3em;
}
.loading-state .back-link {
  margin-top: 16px;
  font-family: var(--font-serif);
  font-size: 12px;
  letter-spacing: 0.2em;
  color: var(--vermilion);
  cursor: pointer;
}
.viewer-img {
  display: block;
  width: 100%;
}
</style>
