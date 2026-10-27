<template>
  <div class="mygoods-page">
    <!-- 报头 -->
    <div class="page-masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 我 摆 的 摊 —</div>
      <h1 class="masthead-title">摊 位</h1>
      <div class="masthead-sub">MY LISTINGS · 共 {{ total }} 件</div>
    </div>

    <!-- 标签栏 -->
    <div class="tab-rail">
      <div
        class="tab-cell"
        :class="{ active: activeTab === '' }"
        @click="switchTab('')"
      >
        <span class="tab-cn">全 部</span>
        <span class="tab-en">ALL</span>
      </div>
      <div
        class="tab-cell"
        :class="{ active: activeTab === '1' }"
        @click="switchTab('1')"
      >
        <span class="tab-cn">在 售</span>
        <span class="tab-en">SELL</span>
      </div>
      <div
        class="tab-cell"
        :class="{ active: activeTab === '2' }"
        @click="switchTab('2')"
      >
        <span class="tab-cn">已 订</span>
        <span class="tab-en">BOOK</span>
      </div>
      <div
        class="tab-cell"
        :class="{ active: activeTab === '3' }"
        @click="switchTab('3')"
      >
        <span class="tab-cn">已 售</span>
        <span class="tab-en">SOLD</span>
      </div>
      <div
        class="tab-cell"
        :class="{ active: activeTab === '4' }"
        @click="switchTab('4')"
      >
        <span class="tab-cn">已 下 架</span>
        <span class="tab-en">OFF</span>
      </div>
    </div>

    <!-- 列表 -->
    <div class="goods-list" v-if="goodsList.length > 0">
      <div
        v-for="(goods, idx) in goodsList"
        :key="goods.goodsId"
        :style="{ animationDelay: (idx * 0.06) + 's' }"
        class="goods-row"
      >
        <div class="row-cover">
          <img v-if="goods.coverImage" :src="coverSrc(goods)" alt="" />
          <div v-else class="cover-empty">摊</div>
          <div class="row-stamp" :class="stampClass(goods.goodsStatus)">{{ statusText(goods.goodsStatus) }}</div>
        </div>
        <div class="row-body">
          <div class="row-name">{{ goods.goodsName }}</div>
          <div class="row-price">
            <span class="price-symbol">¥</span>
            <span class="price-num">{{ Number(goods.price || 0).toFixed(0) }}</span>
          </div>
          <div class="row-meta">
            <span class="meta-no">№ {{ String(goods.goodsId).padStart(3, '0') }}</span>
            <span class="meta-date">{{ formatDate(goods.createTime) }}</span>
          </div>
        </div>
        <div class="row-actions">
          <button
            class="action-btn off-btn"
            v-if="goods.goodsStatus === '1'"
            @click="handleOffShelf(goods)"
          >下 架</button>
          <button
            class="action-btn back-btn"
            v-if="goods.goodsStatus === '4' && goods.auditStatus === '1'"
            @click="handleOnSale(goods)"
          >上 架</button>
          <button
            class="action-btn del-btn"
            v-if="goods.goodsStatus === '0' || goods.goodsStatus === '4'"
            @click="handleDelete(goods)"
          >删 除</button>
          <span class="action-tip" v-if="goods.goodsStatus === '0'">待审核</span>
          <span class="action-tip" v-if="goods.goodsStatus === '2'">交易中</span>
          <span class="action-tip" v-if="goods.goodsStatus === '3'">已售出</span>
        </div>
      </div>
    </div>

    <div class="load-more" v-if="goodsList.length > 0 && hasMore" @click="loadMore">
      {{ loadingMore ? '加 载 中' : '更 多 摊 位' }}
    </div>

    <div class="empty-state" v-if="!loading && goodsList.length === 0">
      <div class="empty-mark">摊</div>
      <p>暂 无 摊 位</p>
      <router-link to="/publish" class="empty-link">— 前 去 摆 摊 —</router-link>
    </div>

    <div class="floating-publish" @click="$router.push('/publish')">
      <span class="fp-mark">+</span>
      <span class="fp-text">摆摊</span>
    </div>
  </div>
</template>

<script>
import { deleteGoods, offlineGoods, onlineGoods } from '@/api/goods'
import { getMyGoods } from '@/api/user'
import { withBase } from '@/utils/image'

export default {
  name: 'MyGoods',
  data() {
    return {
      activeTab: '',
      goodsList: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      loadingMore: false
    }
  },
  computed: {
    hasMore() {
      return this.goodsList.length < this.total
    }
  },
  created() {
    this.loadGoods()
  },
  methods: {
    coverSrc(goods) {
      return withBase(goods.coverImage ? String(goods.coverImage).split(',')[0] : '')
    },
    switchTab(tab) {
      this.activeTab = tab
      this.pageNum = 1
      this.loadGoods()
    },
    buildParams() {
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.activeTab) params.goodsStatus = this.activeTab
      return params
    },
    loadGoods() {
      this.loading = true
      getMyGoods(this.buildParams()).then(res => {
        this.goodsList = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    loadMore() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.pageNum++
      getMyGoods(this.buildParams()).then(res => {
        this.goodsList = [...this.goodsList, ...(res.rows || [])]
        this.total = res.total || 0
      }).catch(() => { this.pageNum-- })
        .finally(() => { this.loadingMore = false })
    },
    stampClass(s) {
      return {
        '1': 'sell', '2': 'book', '3': 'sold', '4': 'off'
      }[s] || ''
    },
    statusText(s) {
      return {
        '1': '在售', '2': '已订', '3': '已售', '4': '下架', '0': '待审'
      }[s] || ''
    },
    formatDate(t) {
      return t ? t.split(' ')[0] : '—'
    },
    handleOffShelf(goods) {
      this.$confirm('确认下架此商品吗？下架后不再展示给其他同学，随时可以重新上架。', '下 架', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'warning'
      }).then(() => {
        offlineGoods(goods.goodsId).then(() => {
          this.$message.success('已下架')
          this.loadGoods()
        }).catch(() => {})
      }).catch(() => {})
    },
    handleOnSale(goods) {
      this.$confirm('确认重新上架此商品吗？', '上 架', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'info'
      }).then(() => {
        onlineGoods(goods.goodsId).then(() => {
          this.$message.success('已上架')
          this.loadGoods()
        }).catch(() => {})
      }).catch(() => {})
    },
    handleDelete(goods) {
      this.$confirm('确认删除此商品？删除后不可恢复！', '删 除', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'warning'
      }).then(() => {
        deleteGoods(goods.goodsId).then(() => {
          this.$message.success('已删除')
          this.loadGoods()
        }).catch(() => {})
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mygoods-page {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: 80px;
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

.tab-rail {
  display: flex;
  background: var(--white-paper);
  border-bottom: 1px solid var(--ink);
  padding: 8px 8px 0;
  gap: 4px;
  overflow-x: auto;
}
.tab-rail::-webkit-scrollbar { display: none; }
.tab-cell {
  flex: 1;
  min-width: 60px;
  padding: 8px 4px 10px;
  text-align: center;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  transition: all 0.2s;
}
.tab-cell.active {
  border-bottom-color: var(--vermilion);
}
.tab-cn {
  font-family: var(--font-serif);
  font-size: 13px;
  color: var(--ink-soft);
  letter-spacing: 0.1em;
  font-weight: 500;
}
.tab-cell.active .tab-cn {
  color: var(--vermilion);
  font-weight: 700;
}
.tab-en {
  font-family: var(--font-mono);
  font-size: 8px;
  letter-spacing: 0.2em;
  color: var(--ink-fade);
}
.tab-cell.active .tab-en { color: var(--vermilion); }

.goods-list {
  padding: 12px 16px 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.goods-row {
  display: flex;
  gap: 12px;
  background: var(--white-paper);
  border: 1px solid var(--ink);
  padding: 10px;
  align-items: center;
  animation: fadeUp 0.4s var(--ease-ink) backwards;
  position: relative;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}
.goods-row::before {
  content: '';
  position: absolute;
  top: 4px;
  left: 4px;
  width: 8px;
  height: 8px;
  border-top: 1px solid var(--ink);
  border-left: 1px solid var(--ink);
  opacity: 0.4;
}
.goods-row::after {
  content: '';
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 8px;
  height: 8px;
  border-bottom: 1px solid var(--ink);
  border-right: 1px solid var(--ink);
  opacity: 0.4;
}
.row-cover {
  position: relative;
  width: 64px;
  height: 80px;
  background: var(--paper-deep);
  flex-shrink: 0;
  overflow: hidden;
  border: 1px solid var(--line);
}
.row-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: sepia(0.08) saturate(0.85);
}
.cover-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-serif);
  font-size: 32px;
  font-weight: 700;
  color: var(--ink-soft);
  background: #2D4A3E;
  color: #C9A875;
}
.row-stamp {
  position: absolute;
  top: 4px;
  right: 4px;
  font-family: var(--font-serif);
  font-size: 8px;
  font-weight: 700;
  padding: 1px 4px;
  letter-spacing: 0.05em;
  color: var(--paper);
  background: var(--moss);
}
.row-stamp.sell { background: var(--vermilion); }
.row-stamp.book { background: var(--gold); }
.row-stamp.sold { background: var(--ink-soft); }
.row-stamp.off { background: var(--ink-fade); }

.row-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.row-name {
  font-family: var(--font-serif);
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.05em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.3;
}
.row-price {
  font-family: var(--font-serif);
  color: var(--vermilion);
  display: flex;
  align-items: baseline;
}
.price-symbol { font-size: 11px; }
.price-num { font-size: 16px; font-weight: 700; }
.row-meta {
  display: flex;
  gap: 8px;
  font-family: var(--font-mono);
  font-size: 9px;
  color: var(--ink-fade);
  letter-spacing: 0.1em;
}
.row-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-end;
}
.action-btn {
  border: 1px solid var(--ink);
  background: var(--paper);
  font-family: var(--font-serif);
  font-size: 11px;
  padding: 4px 10px;
  letter-spacing: 0.15em;
  cursor: pointer;
  color: var(--ink);
}
.action-btn:active { background: var(--ink); color: var(--paper); }
.action-btn.off-btn { color: var(--gold); border-color: var(--gold); }
.action-btn.off-btn:active { background: var(--gold); color: var(--paper); }
.action-btn.back-btn { color: var(--moss); border-color: var(--moss); }
.action-btn.back-btn:active { background: var(--moss); color: var(--paper); }
.action-btn.del-btn { color: var(--vermilion); border-color: var(--vermilion); }
.action-btn.del-btn:active { background: var(--vermilion); color: var(--paper); }
.action-tip {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--ink-mute);
  letter-spacing: 0.1em;
  padding: 4px 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--ink-fade);
}
.load-more {
  text-align: center;
  padding: 16px 0 4px;
  font-family: var(--font-serif);
  font-size: 12px;
  letter-spacing: 0.3em;
  color: var(--ink-mute);
  cursor: pointer;
}
.empty-mark {
  font-family: var(--font-serif);
  font-size: 80px;
  font-weight: 900;
  color: var(--ink-fade);
  opacity: 0.3;
  margin-bottom: 12px;
  line-height: 1;
}
.empty-state p {
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.2em;
  margin-bottom: 16px;
}
.empty-link {
  display: inline-block;
  font-family: var(--font-serif);
  font-size: 13px;
  color: var(--vermilion);
  text-decoration: none;
  letter-spacing: 0.2em;
  border-bottom: 1px solid var(--vermilion);
  padding-bottom: 2px;
}

.floating-publish {
  position: fixed;
  bottom: 76px;
  right: 16px;
  width: 60px;
  height: 60px;
  background: var(--vermilion);
  color: var(--paper);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(181, 55, 30, 0.4);
  z-index: 40;
  border: 2px solid var(--paper);
  transition: transform 0.2s;
}
.floating-publish:active { transform: scale(0.95); }
.fp-mark {
  font-size: 20px;
  line-height: 1;
  font-weight: 300;
}
.fp-text {
  font-family: var(--font-serif);
  font-size: 10px;
  letter-spacing: 0.15em;
  margin-top: 2px;
}
</style>
