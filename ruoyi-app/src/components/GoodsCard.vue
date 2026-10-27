<template>
  <div class="book-card" @click="handleClick">
    <!-- 状态印章 -->
    <div class="book-stamp" v-if="goods.goodsStatus === '2' || goods.goodsStatus === '3'">
      <div class="stamp-text">{{ statusText }}</div>
    </div>

    <!-- 封面 -->
    <div class="book-cover" :class="coverClass">
      <div class="cover-spine"></div>
      <div class="cover-frame">
        <img
          v-if="coverImage"
          class="cover-img"
          :src="coverImage"
          alt=""
          @error="handleImgError"
        />
        <div class="cover-placeholder" v-else>
          <div class="placeholder-mark">摊</div>
          <div class="placeholder-line"></div>
        </div>
      </div>
      <div class="cover-corner-top"></div>
      <div class="cover-corner-bot"></div>
      <div class="cover-num">{{ formattedNo }}</div>
    </div>

    <!-- 信息 -->
    <div class="book-info">
      <div class="book-meta-top">
        <span class="meta-cat" v-if="goods.categoryName">{{ goods.categoryName }}</span>
        <span class="meta-tag">{{ conditionText }}</span>
      </div>
      <h3 class="book-title">{{ goods.goodsName }}</h3>
      <div class="book-price-row">
        <div class="book-price">
          <span class="price-symbol">¥</span>
          <span class="price-int">{{ priceInt }}</span>
          <span v-if="priceDec" class="price-dec">.{{ priceDec }}</span>
        </div>
        <div class="book-original" v-if="goods.originalPrice">¥{{ goods.originalPrice }}</div>
      </div>
      <div class="book-publisher" v-if="goods.publisherName">
        <i class="pub-mark">❍</i>
        <span>{{ goods.publisherName }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { withBase } from '@/utils/image'

export default {
  name: 'GoodsCard',
  props: {
    goods: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      colorPalette: [
        { base: '#2D4A3E', accent: '#8B6F47', name: 'moss' },
        { base: '#5B3A29', accent: '#B89547', name: 'kraft' },
        { base: '#1F3A5F', accent: '#7B8FAB', name: 'navy' },
        { base: '#6B2048', accent: '#C49B5E', name: 'wine' },
        { base: '#3D3528', accent: '#A88E5B', name: 'earth' },
        { base: '#2F4858', accent: '#C9A875', name: 'ocean' }
      ]
    }
  },
  computed: {
    coverImage() {
      return withBase(this.goods.coverImage ? String(this.goods.coverImage).split(',')[0] : '')
    },
    coverClass() {
      const idx = (this.goods.goodsId || 0) % this.colorPalette.length
      return `cover-${this.colorPalette[idx].name}`
    },
    conditionText() {
      const map = { '1': '全新', '2': '九成新', '3': '八成新', '4': '七成新' }
      return map[this.goods.qualityLevel] || ''
    },
    statusText() {
      const map = { '2': '已预订', '3': '已售出' }
      return map[this.goods.goodsStatus] || ''
    },
    formattedPrice() {
      const p = Number(this.goods.price || 0)
      return p.toFixed(2)
    },
    priceInt() {
      return this.formattedPrice.split('.')[0]
    },
    priceDec() {
      return this.formattedPrice.split('.')[1]
    },
    formattedNo() {
      const n = this.goods.goodsId || 0
      return '№ ' + String(n).padStart(3, '0')
    }
  },
  methods: {
    handleClick() {
      this.$router.push('/goods/' + this.goods.goodsId)
    },
    handleImgError(e) {
      e.target.style.display = 'none'
    }
  }
}
</script>

<style scoped>
.book-card {
  background: var(--white-paper);
  position: relative;
  cursor: pointer;
  transition: transform 0.4s var(--ease-ink);
}
.book-card:active {
  transform: scale(0.98);
}

/* —— 封面 —— */
.book-cover {
  position: relative;
  aspect-ratio: 3 / 4;
  background: var(--paper-deep);
  overflow: hidden;
  box-shadow:
    0 1px 0 rgba(31, 27, 22, 0.2),
    0 4px 12px rgba(31, 27, 22, 0.15),
    inset 0 0 0 1px rgba(255, 255, 255, 0.06);
}

/* 书脊效果：左侧暗带 */
.cover-spine {
  position: absolute;
  top: 0;
  left: 0;
  width: 8px;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.35), rgba(0, 0, 0, 0) 100%);
  z-index: 3;
}
.cover-spine::after {
  content: '';
  position: absolute;
  top: 6%;
  left: 2px;
  right: 60%;
  height: 88%;
  background: rgba(0, 0, 0, 0.2);
}

.cover-frame {
  position: absolute;
  inset: 8px 8px 8px 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: sepia(0.08) saturate(0.85) contrast(1.05);
}

/* 角标：右上 烫金小圆 */
.cover-corner-top {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 6px;
  height: 6px;
  background: var(--gold);
  border-radius: 50%;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.2);
  z-index: 4;
}
.cover-corner-bot {
  position: absolute;
  bottom: 10px;
  right: 12px;
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-bottom: 6px solid var(--gold);
  z-index: 4;
}

.cover-num {
  position: absolute;
  bottom: 6px;
  left: 14px;
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.15em;
  color: var(--paper);
  z-index: 4;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
}

/* 配色：每本书一个调色 */
.cover-moss .cover-frame { background: #2D4A3E; }
.cover-moss .cover-placeholder { color: #C9A875; }

.cover-kraft .cover-frame { background: #5B3A29; }
.cover-kraft .cover-placeholder { color: #E8C68A; }

.cover-navy .cover-frame { background: #1F3A5F; }
.cover-navy .cover-placeholder { color: #C5D1E0; }

.cover-wine .cover-frame { background: #6B2048; }
.cover-wine .cover-placeholder { color: #E0B57A; }

.cover-earth .cover-frame { background: #3D3528; }
.cover-earth .cover-placeholder { color: #B89968; }

.cover-ocean .cover-frame { background: #2F4858; }
.cover-ocean .cover-placeholder { color: #C9A875; }

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-family: var(--font-serif);
}
.placeholder-mark {
  font-size: 56px;
  font-weight: 900;
  line-height: 1;
  letter-spacing: 0.1em;
}
.placeholder-line {
  width: 28px;
  height: 1px;
  background: currentColor;
  opacity: 0.6;
}

/* —— 状态印章 —— */
.book-stamp {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 5;
  width: 52px;
  height: 52px;
  background: var(--vermilion);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 2px solid var(--paper);
  transform: rotate(-8deg);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  animation: stamp-stamp 0.6s var(--ease-ink);
}
.book-stamp::before {
  content: '';
  position: absolute;
  inset: 2px;
  border: 1px solid var(--paper);
  border-radius: 50%;
  opacity: 0.5;
}
.stamp-text {
  font-family: var(--font-serif);
  font-size: 11px;
  font-weight: 700;
  color: var(--paper);
  letter-spacing: 0.05em;
}
@keyframes stamp-stamp {
  0% { transform: rotate(-8deg) scale(2); opacity: 0; }
  60% { transform: rotate(-8deg) scale(0.9); opacity: 1; }
  100% { transform: rotate(-8deg) scale(1); opacity: 1; }
}

/* —— 信息区 —— */
.book-info {
  padding: 10px 4px 4px;
  position: relative;
}
.book-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: 4px;
  right: 4px;
  height: 1px;
  background: var(--ink);
  opacity: 0.15;
}

.book-meta-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  margin-bottom: 4px;
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.15em;
}
.meta-cat {
  color: var(--vermilion);
  font-weight: 500;
  text-transform: uppercase;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60%;
}
.meta-tag {
  color: var(--ink-mute);
  border: 1px solid var(--line);
  padding: 1px 5px;
  background: var(--paper);
  white-space: nowrap;
}

.book-title {
  font-family: var(--font-serif);
  font-size: 13px;
  font-weight: 500;
  color: var(--ink);
  line-height: 1.35;
  height: 36px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 6px;
  letter-spacing: 0.02em;
}

.book-price-row {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 4px;
}
.book-price {
  font-family: var(--font-serif);
  color: var(--vermilion);
  display: flex;
  align-items: baseline;
}
.price-symbol {
  font-size: 12px;
  font-weight: 500;
  margin-right: 1px;
}
.price-int {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.02em;
}
.price-dec {
  font-size: 11px;
  font-weight: 500;
}
.book-original {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--ink-fade);
  text-decoration: line-through;
}

.book-publisher {
  font-family: var(--font-serif);
  font-size: 11px;
  color: var(--ink-mute);
  display: flex;
  align-items: center;
  gap: 4px;
  letter-spacing: 0.05em;
}
.pub-mark {
  color: var(--vermilion);
  font-size: 8px;
}
</style>
