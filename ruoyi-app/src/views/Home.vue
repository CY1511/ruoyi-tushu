<template>
  <div class="home-page">
    <!-- 顶头条 -->
    <header class="masthead">
      <div class="masthead-top">
        <div class="masthead-date">
          <div class="date-num">002</div>
          <div class="date-text">期<br />JUL.2026</div>
        </div>
        <div class="masthead-title-block">
          <div class="masthead-eyebrow">CAMPUS · SECONDHAND · MARKET</div>
          <h1 class="masthead-title">摊</h1>
          <div class="masthead-sub">— 校园二手书市 —</div>
        </div>
        <div class="masthead-weather">
          <div class="weather-mark">{{ weatherMark }}</div>
          <div class="weather-text">{{ weatherText }}</div>
        </div>
      </div>
      <div class="masthead-rule"></div>
      <div class="masthead-strap">
        <span>VOL. 002</span>
        <span class="strap-dot">·</span>
        <span>本期新书 {{ total }} 册</span>
        <span class="strap-dot">·</span>
        <span>凭票入场</span>
      </div>
    </header>

    <!-- 搜索 -->
    <div class="search-block">
      <div class="search-frame">
        <i class="el-icon-search search-icon"></i>
        <input
          v-model="keyword"
          placeholder="寻书 · 书名 / 课程 / ISBN"
          class="search-input"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
          clearable
        />
        <span class="search-divider"></span>
        <button class="search-btn" @click="handleSearch">检 索</button>
      </div>
    </div>

    <!-- 分类书脊 -->
    <section class="spine-section">
      <div class="section-header">
        <div class="section-mark"></div>
        <span class="section-eyebrow">CHAPTER · 01</span>
        <span class="section-cn">分 类 索 引</span>
      </div>
      <div class="spine-rail">
        <div
          v-for="(cat, i) in categoryList"
          :key="cat.categoryId"
          class="spine-item"
          :style="{ '--spine-color': spineColors[i % spineColors.length] }"
          @click="goCategory(cat.categoryId)"
        >
          <div class="spine-no">№ {{ String(i + 1).padStart(2, '0') }}</div>
          <div class="spine-text">{{ cat.categoryName }}</div>
          <div class="spine-foot">
            <span class="spine-dot">●</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品区 -->
    <section class="goods-section">
      <div class="section-header">
        <div class="section-mark"></div>
        <span class="section-eyebrow">CHAPTER · 02</span>
        <span class="section-cn">本 期 新 刊</span>
      </div>

      <div class="refresh-tip" v-if="refreshing">
        <span class="refresh-line"></span>
        <i class="el-icon-loading"></i> 翻 页 中
        <span class="refresh-line"></span>
      </div>

      <!-- 双列瀑布流 -->
      <div class="goods-grid" v-if="goodsList.length > 0">
        <div class="goods-column">
          <div
            v-for="(goods, idx) in leftColumn"
            :key="goods.goodsId"
            :style="{ animationDelay: (idx * 0.08) + 's' }"
            class="goods-cell"
          >
            <goods-card :goods="goods" />
            <div class="cell-no">— {{ String(idx * 2 + 1).padStart(3, '0') }} —</div>
          </div>
        </div>
        <div class="goods-column">
          <div
            v-for="(goods, idx) in rightColumn"
            :key="goods.goodsId"
            :style="{ animationDelay: (idx * 0.08 + 0.04) + 's' }"
            class="goods-cell"
          >
            <goods-card :goods="goods" />
            <div class="cell-no">— {{ String(idx * 2 + 2).padStart(3, '0') }} —</div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-if="!loading && goodsList.length === 0">
        <div class="empty-mark">摊</div>
        <p>本 期 暂 无 新 刊</p>
        <p class="empty-tip">不妨先摆个摊 →</p>
      </div>

      <div class="load-more" v-if="goodsList.length > 0">
        <button
          v-if="hasMore"
          class="load-btn"
          :disabled="loadingMore"
          @click="loadMore"
        >
          <span v-if="!loadingMore">— 翻 下 一 页 —</span>
          <span v-else><i class="el-icon-loading"></i> 翻 页 中</span>
        </button>
        <div class="end-mark" v-else>
          <span class="end-line"></span>
          <span>本 期 完</span>
          <span class="end-line"></span>
        </div>
      </div>
    </section>

    <!-- 装订线 -->
    <div class="binding-edge">
      <span>· · ·</span>
    </div>
  </div>
</template>

<script>
import { listGoods } from '@/api/goods'
import { getCategoryTree } from '@/api/category'
import GoodsCard from '@/components/GoodsCard.vue'

export default {
  name: 'Home',
  components: { GoodsCard },
  data() {
    return {
      keyword: '',
      goodsList: [],
      categoryList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      loadingMore: false,
      refreshing: false,
      categoryId: undefined,
      spineColors: ['#2D4A3E', '#5B3A29', '#1F3A5F', '#6B2048', '#3D3528', '#2F4858', '#4A5D3A']
    }
  },
  computed: {
    hasMore() {
      return this.goodsList.length < this.total
    },
    leftColumn() {
      return this.goodsList.filter((_, i) => i % 2 === 0)
    },
    rightColumn() {
      return this.goodsList.filter((_, i) => i % 2 === 1)
    },
    weatherMark() {
      const m = ['☀', '☁', '☂', '☃']
      return m[Math.floor(Math.random() * m.length)]
    },
    weatherText() {
      return '晴'
    }
  },
  created() {
    this.loadCategories()
    this.loadGoods()
  },
  methods: {
    loadCategories() {
      getCategoryTree().then(res => {
        this.categoryList = res.data || []
      }).catch(() => {})
    },
    loadGoods() {
      this.loading = true
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.keyword) params.goodsName = this.keyword
      if (this.categoryId) params.categoryId = this.categoryId
      listGoods(params).then(res => {
        this.goodsList = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    handleSearch() {
      this.pageNum = 1
      this.loadGoods()
    },
    loadMore() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.pageNum++
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.keyword) params.goodsName = this.keyword
      listGoods(params).then(res => {
        this.goodsList = [...this.goodsList, ...(res.rows || [])]
        this.total = res.total || 0
      }).catch(() => { this.pageNum-- })
        .finally(() => { this.loadingMore = false })
    },
    goCategory(categoryId) {
      this.$router.push({ path: '/category', query: { categoryId } })
    }
  }
}
</script>

<style scoped>
.home-page {
  background: var(--paper);
  min-height: 100vh;
  padding: 0 0 40px;
}

/* —— 顶头条 —— */
.masthead {
  padding: 18px 20px 16px;
  border-bottom: 1px solid var(--ink);
  position: relative;
}
.masthead-top {
  display: grid;
  grid-template-columns: 60px 1fr 60px;
  align-items: center;
  gap: 12px;
}
.masthead-date {
  border-right: 1px solid var(--ink);
  padding-right: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.date-num {
  font-family: var(--font-serif);
  font-size: 36px;
  font-weight: 900;
  line-height: 0.9;
  color: var(--vermilion);
  letter-spacing: -0.02em;
}
.date-text {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.2em;
  color: var(--ink-mute);
  line-height: 1.2;
}
.masthead-title-block {
  text-align: center;
}
.masthead-eyebrow {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  margin-bottom: 2px;
}
.masthead-title {
  font-family: var(--font-serif);
  font-size: 40px;
  font-weight: 900;
  color: var(--ink);
  line-height: 1;
  letter-spacing: 0.1em;
  display: inline-block;
  position: relative;
}
.masthead-sub {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--ink-soft);
  letter-spacing: 0.4em;
  margin-top: 2px;
  padding-left: 0.4em;
}
.masthead-weather {
  border-left: 1px solid var(--ink);
  padding-left: 8px;
  text-align: center;
}
.weather-mark {
  font-size: 20px;
  color: var(--vermilion);
  line-height: 1;
}
.weather-text {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.2em;
  color: var(--ink-mute);
  margin-top: 2px;
}
.masthead-rule {
  height: 1px;
  background: var(--ink);
  margin: 12px 0 8px;
  position: relative;
}
.masthead-rule::before {
  content: '';
  position: absolute;
  inset: 2px 0 0 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.masthead-strap {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.25em;
  color: var(--ink-mute);
}
.strap-dot { color: var(--vermilion); }

/* —— 搜索 —— */
.search-block {
  padding: 14px 16px;
  background: var(--paper-deep);
  border-bottom: 1px solid var(--line);
}
.search-frame {
  display: flex;
  align-items: center;
  background: var(--white-paper);
  border: 1.5px solid var(--ink);
  height: 40px;
  position: relative;
}
.search-icon {
  padding: 0 12px;
  color: var(--ink);
  font-size: 14px;
}
.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--ink);
  letter-spacing: 0.05em;
  height: 100%;
}
.search-input::placeholder {
  color: var(--ink-fade);
  font-family: var(--font-serif);
  letter-spacing: 0.1em;
}
.search-divider {
  width: 1px;
  height: 24px;
  background: var(--ink);
  opacity: 0.3;
}
.search-btn {
  background: var(--ink);
  color: var(--paper);
  border: none;
  height: 100%;
  padding: 0 18px;
  font-family: var(--font-serif);
  font-size: 13px;
  letter-spacing: 0.3em;
  cursor: pointer;
  transition: background 0.2s;
}
.search-btn:active { background: var(--vermilion); }

/* —— 节标题 —— */
.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 24px 16px 14px;
  padding-bottom: 8px;
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
  font-size: 15px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.2em;
}

/* —— 分类书脊 —— */
.spine-rail {
  display: flex;
  overflow-x: auto;
  padding: 0 16px 4px;
  gap: 6px;
  -webkit-overflow-scrolling: touch;
}
.spine-rail::-webkit-scrollbar { display: none; }
.spine-item {
  flex-shrink: 0;
  width: 64px;
  height: 96px;
  background: var(--spine-color);
  color: var(--paper);
  position: relative;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 8px 4px;
  transition: transform 0.3s var(--ease-ink);
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.1),
    inset 4px 0 0 rgba(0, 0, 0, 0.3),
    0 2px 6px rgba(31, 27, 22, 0.25);
}
.spine-item::before {
  content: '';
  position: absolute;
  top: 4px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 1px;
  background: var(--gold);
  box-shadow: 0 16px 0 var(--gold);
}
.spine-item::after {
  content: '';
  position: absolute;
  bottom: 4px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 1px;
  background: var(--gold);
  box-shadow: 0 -16px 0 var(--gold);
}
.spine-item:active {
  transform: translateY(2px);
}
.spine-no {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.6);
}
.spine-text {
  writing-mode: vertical-rl;
  font-family: var(--font-serif);
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.3em;
  text-align: center;
  line-height: 1.2;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.spine-foot {
  color: var(--gold);
  font-size: 6px;
}

/* —— 翻页提示 —— */
.refresh-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--vermilion);
  letter-spacing: 0.2em;
  padding: 8px;
}
.refresh-line {
  flex: 1;
  height: 1px;
  background: var(--vermilion);
  opacity: 0.4;
  max-width: 60px;
}

/* —— 商品流 —— */
.goods-grid {
  display: flex;
  gap: 10px;
  padding: 0 16px;
}
.goods-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.goods-cell {
  animation: fadeUp 0.6s var(--ease-ink) backwards;
  position: relative;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
.cell-no {
  text-align: center;
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--ink-fade);
  margin-top: 6px;
}

/* —— 加载更多 —— */
.load-more {
  text-align: center;
  padding: 28px 16px 8px;
}
.load-btn {
  background: transparent;
  border: 1px solid var(--ink);
  color: var(--ink);
  padding: 10px 24px;
  font-family: var(--font-serif);
  font-size: 12px;
  letter-spacing: 0.3em;
  cursor: pointer;
  transition: all 0.2s;
}
.load-btn:hover { background: var(--ink); color: var(--paper); }
.load-btn:disabled { opacity: 0.5; }
.end-mark {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink-mute);
  letter-spacing: 0.3em;
}
.end-line {
  flex: 1;
  max-width: 80px;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}

/* —— 空状态 —— */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--ink-fade);
}
.empty-mark {
  font-family: var(--font-serif);
  font-size: 80px;
  font-weight: 900;
  color: var(--ink-fade);
  opacity: 0.3;
  line-height: 1;
  margin-bottom: 12px;
}
.empty-state p {
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.2em;
  margin-bottom: 4px;
}
.empty-tip {
  font-size: 12px !important;
  color: var(--vermilion) !important;
  opacity: 0.6;
}

.binding-edge {
  text-align: center;
  margin-top: 32px;
  color: var(--ink-fade);
  letter-spacing: 0.5em;
  font-size: 12px;
}
</style>
