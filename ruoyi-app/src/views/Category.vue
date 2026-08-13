<template>
  <div class="category-page">
    <div class="page-masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 分 类 索 引 —</div>
      <h1 class="masthead-title">分 类</h1>
      <div class="masthead-sub">CATEGORY · BY CHAPTER</div>
    </div>

    <div class="category-content">
      <!-- 左侧主分类 -->
      <aside class="cat-aside">
        <div class="aside-header">
          <span class="aside-cn">主 目</span>
          <span class="aside-en">MAIN</span>
        </div>
        <div class="cat-list">
          <div
            v-for="(cat, i) in categoryList"
            :key="cat.categoryId"
            class="cat-item"
            :class="{ active: activeCategoryId === cat.categoryId }"
            @click="selectCategory(cat)"
          >
            <div class="cat-num">{{ String(i + 1).padStart(2, '0') }}</div>
            <div class="cat-name">{{ cat.categoryName }}</div>
            <div class="cat-active-dot" v-if="activeCategoryId === cat.categoryId">●</div>
          </div>
        </div>
      </aside>

      <!-- 右侧详情 -->
      <main class="cat-main">
        <div class="main-header">
          <span class="main-eyebrow">CHAPTER · {{ currentCatIndex }}</span>
          <span class="main-cn">{{ currentCategory ? currentCategory.categoryName : '' }}</span>
        </div>

        <!-- 子分类标签 -->
        <div class="sub-list" v-if="currentChildren.length > 0">
          <div
            class="sub-item"
            :class="{ active: activeSubId === sub.categoryId }"
            v-for="sub in currentChildren"
            :key="sub.categoryId"
            @click="selectSubCategory(sub)"
          >
            {{ sub.categoryName }}
          </div>
        </div>

        <!-- 商品流 -->
        <div class="goods-list" v-if="goodsList.length > 0">
          <div
            v-for="(goods, idx) in goodsList"
            :key="goods.goodsId"
            :style="{ animationDelay: (idx * 0.06) + 's' }"
            class="goods-cell"
          >
            <goods-card :goods="goods" />
            <div class="cell-no">— {{ String(idx + 1).padStart(3, '0') }} —</div>
          </div>
        </div>

        <div class="empty-state" v-if="!loading && goodsList.length === 0">
          <div class="empty-mark">空</div>
          <p>本 类 暂 无 书 刊</p>
        </div>

        <div class="load-more" v-if="goodsList.length > 0">
          <button v-if="hasMore" class="load-btn" :disabled="loadingMore" @click="loadMore">
            <span v-if="!loadingMore">— 翻 下 一 页 —</span>
            <span v-else><i class="el-icon-loading"></i> 翻 页 中</span>
          </button>
          <div class="end-mark" v-else>
            <span class="end-line"></span>
            <span>已 至 卷 末</span>
            <span class="end-line"></span>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { listGoods } from '@/api/goods'
import { getCategoryTree } from '@/api/category'
import GoodsCard from '@/components/GoodsCard.vue'

export default {
  name: 'Category',
  components: { GoodsCard },
  data() {
    return {
      categoryList: [],
      activeCategoryId: null,
      activeSubId: null,
      goodsList: [],
      pageNum: 1,
      pageSize: 12,
      total: 0,
      loading: false,
      loadingMore: false
    }
  },
  computed: {
    currentChildren() {
      const current = this.categoryList.find(c => c.categoryId === this.activeCategoryId)
      return current && current.children ? current.children : []
    },
    currentCategory() {
      return this.categoryList.find(c => c.categoryId === this.activeCategoryId) || {}
    },
    currentCatIndex() {
      const i = this.categoryList.findIndex(c => c.categoryId === this.activeCategoryId)
      return String(i + 1).padStart(2, '0')
    },
    hasMore() {
      return this.goodsList.length < this.total
    }
  },
  watch: {
    '$route.query.categoryId'(val) {
      if (val) {
        this.activeCategoryId = parseInt(val)
        this.activeSubId = null
        this.pageNum = 1
        this.goodsList = []
        this.loadGoods()
      }
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    loadCategories() {
      getCategoryTree().then(res => {
        this.categoryList = res.data || []
        const queryCatId = this.$route.query.categoryId
        if (queryCatId) {
          this.activeCategoryId = parseInt(queryCatId)
        } else if (this.categoryList.length > 0) {
          this.activeCategoryId = this.categoryList[0].categoryId
        }
        if (this.activeCategoryId) this.loadGoods()
      }).catch(() => {})
    },
    selectCategory(cat) {
      this.activeCategoryId = cat.categoryId
      this.activeSubId = null
      this.pageNum = 1
      this.goodsList = []
      this.loadGoods()
    },
    selectSubCategory(sub) {
      this.activeSubId = sub.categoryId
      this.pageNum = 1
      this.goodsList = []
      this.loadGoods()
    },
    loadGoods() {
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        categoryId: this.activeSubId || this.activeCategoryId
      }
      listGoods(params).then(res => {
        this.goodsList = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    loadMore() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.pageNum++
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        categoryId: this.activeSubId || this.activeCategoryId
      }
      listGoods(params).then(res => {
        this.goodsList = [...this.goodsList, ...(res.rows || [])]
        this.total = res.total || 0
      }).catch(() => { this.pageNum-- })
        .finally(() => { this.loadingMore = false })
    }
  }
}
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: 40px;
}

.page-masthead {
  text-align: center;
  padding: 16px 20px 14px;
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
  font-size: 36px;
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
  margin-top: 4px;
}

.category-content {
  display: flex;
  min-height: calc(100vh - 110px);
}

/* —— 左侧 —— */
.cat-aside {
  width: 100px;
  flex-shrink: 0;
  background: var(--paper-deep);
  border-right: 1px solid var(--ink);
  display: flex;
  flex-direction: column;
}
.aside-header {
  padding: 14px 10px 10px;
  text-align: center;
  border-bottom: 1px solid var(--ink);
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.aside-cn {
  font-family: var(--font-serif);
  font-size: 14px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.3em;
  padding-left: 0.3em;
}
.aside-en {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.3em;
  color: var(--vermilion);
  font-weight: 500;
}
.cat-list {
  flex: 1;
}
.cat-item {
  padding: 14px 8px 12px;
  border-bottom: 1px solid var(--line);
  cursor: pointer;
  position: relative;
  text-align: center;
  transition: background 0.2s;
}
.cat-item:active { background: var(--paper); }
.cat-item.active {
  background: var(--paper);
  border-left: 3px solid var(--vermilion);
  padding-left: 5px;
}
.cat-num {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.15em;
  color: var(--ink-fade);
  margin-bottom: 4px;
}
.cat-item.active .cat-num { color: var(--vermilion); }
.cat-name {
  font-family: var(--font-serif);
  font-size: 13px;
  color: var(--ink-soft);
  font-weight: 500;
  letter-spacing: 0.15em;
}
.cat-item.active .cat-name {
  color: var(--ink);
  font-weight: 700;
}
.cat-active-dot {
  position: absolute;
  top: 8px;
  right: 8px;
  color: var(--vermilion);
  font-size: 8px;
}

/* —— 右侧 —— */
.cat-main {
  flex: 1;
  padding: 14px;
  overflow: hidden;
}
.main-header {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--ink);
  margin-bottom: 12px;
  position: relative;
}
.main-header::after {
  content: '';
  position: absolute;
  inset: auto 0 -2px 0;
  height: 1px;
  background: var(--ink);
  opacity: 0.3;
}
.main-eyebrow {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--vermilion);
  font-weight: 500;
}
.main-cn {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.2em;
}

.sub-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 16px;
}
.sub-item {
  font-family: var(--font-serif);
  font-size: 11px;
  letter-spacing: 0.15em;
  color: var(--ink-soft);
  padding: 4px 10px;
  background: var(--paper);
  border: 1px solid var(--line);
  cursor: pointer;
  transition: all 0.2s;
}
.sub-item.active {
  background: var(--ink);
  color: var(--paper);
  border-color: var(--ink);
}
.sub-item:active { transform: scale(0.97); }

/* —— 商品流 —— */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.goods-cell {
  animation: fadeUp 0.5s var(--ease-ink) backwards;
  position: relative;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
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

/* —— 加载 —— */
.load-more {
  text-align: center;
  padding: 24px 0 0;
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
}
.load-btn:active { background: var(--ink); color: var(--paper); }
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

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--ink-fade);
}
.empty-mark {
  font-family: var(--font-serif);
  font-size: 64px;
  font-weight: 900;
  color: var(--ink-fade);
  opacity: 0.3;
  margin-bottom: 12px;
}
.empty-state p {
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.2em;
}
</style>
