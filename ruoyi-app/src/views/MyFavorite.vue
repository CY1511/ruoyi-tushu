<template>
  <div class="my-favorite-page">
    <!-- 收藏列表 -->
    <div class="favorite-list" v-if="favoriteList.length > 0">
      <goods-card
        v-for="goods in favoriteList"
        :key="goods.goodsId"
        :goods="goods"
      />
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="!loading && favoriteList.length === 0">
      <i class="el-icon-star-off"></i>
      <p>暂无收藏</p>
    </div>

    <!-- 加载中 -->
    <div class="loading-wrapper" v-if="loading">
      <i class="el-icon-loading"></i>
      <p>加载中...</p>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="favoriteList.length > 0 && hasMore">
      <el-button type="text" :loading="loadingMore" @click="loadMore">
        {{ loadingMore ? '加载中...' : '加载更多' }}
      </el-button>
    </div>
  </div>
</template>

<script>
import { getFavoriteList } from '@/api/favorite'
import GoodsCard from '@/components/GoodsCard.vue'

export default {
  name: 'MyFavorite',
  components: { GoodsCard },
  data() {
    return {
      favoriteList: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      loadingMore: false
    }
  },
  computed: {
    hasMore() {
      return this.favoriteList.length < this.total
    }
  },
  created() {
    this.loadFavorites()
  },
  methods: {
    // 加载收藏列表
    loadFavorites() {
      this.loading = true
      getFavoriteList({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        this.favoriteList = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => {
        this.loading = false
      })
    },
    // 加载更多
    loadMore() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.pageNum++
      getFavoriteList({
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }).then(res => {
        this.favoriteList = [...this.favoriteList, ...(res.rows || [])]
        this.total = res.total || 0
      }).catch(() => {
        this.pageNum--
      }).finally(() => {
        this.loadingMore = false
      })
    }
  }
}
</script>

<style scoped>
.my-favorite-page {
  min-height: 100vh;
  background: var(--paper);
  padding: 12px;
}
.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.loading-wrapper {
  text-align: center;
  padding: 60px 0;
  color: #c0c4cc;
}
.load-more {
  text-align: center;
  padding: 16px 0;
}
</style>
