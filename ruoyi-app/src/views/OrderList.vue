<template>
  <div class="order-page">
    <div class="page-masthead">
      <div class="masthead-eyebrow">— 第 002 期 · 订 单 存 根 —</div>
      <h1 class="masthead-title">订 单</h1>
      <div class="masthead-sub">ORDERS · RECEIPTS</div>
    </div>

    <!-- Tab栏 -->
    <div class="tab-rail">
      <div
        class="tab-cell"
        :class="{ active: activeTab === 'buyer' }"
        @click="switchRole('buyer')"
      >
        <span class="tab-cn">我 买 的</span>
        <span class="tab-en">BUYER</span>
      </div>
      <div
        class="tab-cell"
        :class="{ active: activeTab === 'seller' }"
        @click="switchRole('seller')"
      >
        <span class="tab-cn">我 卖 的</span>
        <span class="tab-en">SELLER</span>
      </div>
    </div>

    <!-- 状态筛选 -->
    <div class="status-rail">
      <div
        class="status-cell"
        :class="{ active: activeStatus === null }"
        @click="switchStatus(null)"
      >全 部</div>
      <div
        class="status-cell"
        :class="{ active: activeStatus === '0' }"
        @click="switchStatus('0')"
      >待 确 认</div>
      <div
        class="status-cell"
        :class="{ active: activeStatus === '1' }"
        @click="switchStatus('1')"
      >交 易 中</div>
      <div
        class="status-cell"
        :class="{ active: activeStatus === '2' }"
        @click="switchStatus('2')"
      >已 完 成</div>
      <div
        class="status-cell"
        :class="{ active: activeStatus === '3' }"
        @click="switchStatus('3')"
      >已 取 消</div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-if="orderList.length > 0">
      <div
        v-for="(order, idx) in orderList"
        :key="order.orderId"
        :style="{ animationDelay: (idx * 0.06) + 's' }"
        class="order-stub"
      >
        <div class="stub-top">
          <div class="stub-info">
            <span class="stub-no">№ {{ String(order.orderId).padStart(4, '0') }}</span>
            <span class="stub-date mono">{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="stub-status" :class="statusClass(order.orderStatus)">
            {{ statusText(order.orderStatus) }}
          </div>
        </div>

        <div class="stub-body" @click="goDetail(order.goodsId)">
          <div class="stub-amount">
            <span class="amount-symbol">¥</span>
            <span class="amount-num">{{ Number(order.orderPrice || order.price || 0).toFixed(0) }}</span>
            <span class="amount-dec" v-if="order.orderPrice">.{{ (order.orderPrice + '').split('.')[1] || '00' }}</span>
          </div>
          <div class="stub-detail">
            <div class="detail-line">
              <span class="line-label">货 物</span>
              <span class="line-value">商品 № {{ order.goodsId }}</span>
            </div>
            <div class="detail-line">
              <span class="line-label">{{ activeTab === 'buyer' ? '摊 主' : '买 家' }}</span>
              <span class="line-value">{{ activeTab === 'buyer' ? (order.sellerName || '—') : (order.buyerName || '—') }}</span>
            </div>
          </div>
        </div>

        <div class="stub-foot">
          <span class="foot-mark">{{ activeTab === 'buyer' ? '买 家 存 根' : '摊 主 存 根' }}</span>
          <div class="foot-actions">
            <template v-if="activeTab === 'buyer'">
              <button
                v-if="order.orderStatus === '1'"
                class="action-btn primary"
                @click="handleConfirm(order)"
              >确认收货</button>
              <button
                v-if="order.orderStatus === '0'"
                class="action-btn ghost"
                @click="handleCancel(order)"
              >取消</button>
            </template>
            <template v-if="activeTab === 'seller'">
              <button
                v-if="order.orderStatus === '0'"
                class="action-btn primary"
                @click="handleAccept(order)"
              >确认接单</button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div class="load-more" v-if="orderList.length > 0 && hasMore" @click="loadMore">
      {{ loadingMore ? '加 载 中' : '更 多 订 单' }}
    </div>

    <div class="empty-state" v-if="!loading && orderList.length === 0">
      <div class="empty-mark">无</div>
      <p>尚 无 订 单</p>
    </div>
  </div>
</template>

<script>
import { getBuyerOrders, getSellerOrders, confirmOrder, cancelOrder, acceptOrder } from '@/api/order'

export default {
  name: 'OrderList',
  data() {
    return {
      activeTab: 'buyer',
      activeStatus: null,
      orderList: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      loadingMore: false
    }
  },
  computed: {
    hasMore() {
      return this.orderList.length < this.total
    }
  },
  created() {
    this.loadOrders()
  },
  methods: {
    switchRole(role) {
      this.activeTab = role
      this.pageNum = 1
      this.loadOrders()
    },
    switchStatus(s) {
      this.activeStatus = s
      this.pageNum = 1
      this.loadOrders()
    },
    loadOrders() {
      this.loading = true
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.activeStatus !== null) params.orderStatus = this.activeStatus
      const req = this.activeTab === 'buyer' ? getBuyerOrders : getSellerOrders
      req(params).then(res => {
        this.orderList = res.rows || res.data || []
        this.total = res.total || this.orderList.length
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    loadMore() {
      if (this.loadingMore) return
      this.loadingMore = true
      this.pageNum++
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.activeStatus !== null) params.orderStatus = this.activeStatus
      const req = this.activeTab === 'buyer' ? getBuyerOrders : getSellerOrders
      req(params).then(res => {
        this.orderList = [...this.orderList, ...(res.rows || res.data || [])]
        this.total = res.total || this.orderList.length
      }).catch(() => { this.pageNum-- })
        .finally(() => { this.loadingMore = false })
    },
    statusText(s) {
      return { '0': '待确认', '1': '交易中', '2': '已完成', '3': '已取消' }[s] || '未知'
    },
    statusClass(s) {
      return { '0': 'wait', '1': 'ing', '2': 'done', '3': 'cancel' }[s] || ''
    },
    formatDate(t) {
      return t ? t.split(' ')[0] : '—'
    },
    goDetail(id) {
      if (id) this.$router.push('/goods/' + id)
    },
    handleConfirm(order) {
      this.$confirm('确认已收到商品吗？', '确 认 收 货', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'info'
      }).then(() => {
        confirmOrder(order.orderId).then(() => {
          this.$message.success('确认收货成功')
          this.loadOrders()
        }).catch(() => {})
      }).catch(() => {})
    },
    handleCancel(order) {
      this.$confirm('确认取消此订单？', '取 消 订 单', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'warning'
      }).then(() => {
        cancelOrder(order.orderId).then(() => {
          this.$message.success('已取消')
          this.loadOrders()
        }).catch(() => {})
      }).catch(() => {})
    },
    handleAccept(order) {
      this.$confirm('确认接受此订单？', '接 单', {
        confirmButtonText: '确 认',
        cancelButtonText: '取 消',
        type: 'info'
      }).then(() => {
        acceptOrder(order.orderId).then(() => {
          this.$message.success('已接单')
          this.loadOrders()
        }).catch(() => {})
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.order-page {
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

.tab-rail {
  display: flex;
  background: var(--white-paper);
  border-bottom: 1px solid var(--ink);
  padding: 0 8px;
}
.tab-cell {
  flex: 1;
  padding: 12px 4px 10px;
  text-align: center;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.tab-cell.active {
  border-bottom-color: var(--vermilion);
}
.tab-cn {
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--ink-soft);
  font-weight: 500;
  letter-spacing: 0.2em;
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

.status-rail {
  display: flex;
  overflow-x: auto;
  padding: 10px 16px;
  gap: 6px;
  background: var(--paper-deep);
  border-bottom: 1px solid var(--line);
}
.status-rail::-webkit-scrollbar { display: none; }
.status-cell {
  flex-shrink: 0;
  padding: 4px 12px;
  font-family: var(--font-serif);
  font-size: 12px;
  color: var(--ink-mute);
  background: var(--paper);
  border: 1px solid var(--line);
  letter-spacing: 0.1em;
  cursor: pointer;
  white-space: nowrap;
}
.status-cell.active {
  background: var(--ink);
  color: var(--paper);
  border-color: var(--ink);
}

.order-list {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order-stub {
  background: var(--white-paper);
  border: 1.5px solid var(--ink);
  position: relative;
  animation: fadeUp 0.4s var(--ease-ink) backwards;
  box-shadow: 3px 3px 0 var(--ink);
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}
.order-stub::before {
  content: '';
  position: absolute;
  top: 50%;
  left: -8px;
  width: 12px;
  height: 12px;
  background: var(--paper);
  border: 1.5px solid var(--ink);
  border-radius: 50%;
  transform: translateY(-50%);
}
.order-stub::after {
  content: '';
  position: absolute;
  top: 50%;
  right: -8px;
  width: 12px;
  height: 12px;
  background: var(--paper);
  border: 1.5px solid var(--ink);
  border-radius: 50%;
  transform: translateY(-50%);
}
.stub-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: var(--paper-deep);
  border-bottom: 1px dashed var(--line);
}
.stub-info {
  display: flex;
  gap: 8px;
  align-items: baseline;
}
.stub-no {
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.15em;
}
.stub-date {
  font-size: 10px;
  color: var(--ink-mute);
  letter-spacing: 0.1em;
}
.stub-status {
  font-family: var(--font-serif);
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  letter-spacing: 0.15em;
  border: 1px solid;
}
.stub-status.wait { color: var(--gold); border-color: var(--gold); }
.stub-status.ing { color: var(--moss); border-color: var(--moss); }
.stub-status.done { color: var(--vermilion); border-color: var(--vermilion); }
.stub-status.cancel { color: var(--ink-fade); border-color: var(--ink-fade); }

.stub-body {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px dashed var(--line);
  cursor: pointer;
}
.stub-amount {
  font-family: var(--font-serif);
  color: var(--vermilion);
  display: flex;
  align-items: baseline;
  font-weight: 700;
  flex-shrink: 0;
  width: 80px;
}
.amount-symbol { font-size: 12px; }
.amount-num { font-size: 26px; letter-spacing: -0.02em; }
.amount-dec { font-size: 12px; }
.stub-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}
.detail-line {
  display: flex;
  font-size: 11px;
  letter-spacing: 0.05em;
}
.line-label {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.2em;
  color: var(--ink-mute);
  width: 40px;
  flex-shrink: 0;
}
.line-value {
  font-family: var(--font-serif);
  color: var(--ink-soft);
}

.stub-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
}
.foot-mark {
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--ink-fade);
}
.foot-actions {
  display: flex;
  gap: 6px;
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
.action-btn.primary {
  background: var(--vermilion);
  border-color: var(--vermilion);
  color: var(--paper);
}
.action-btn.primary:active { background: var(--vermilion-deep); }
.action-btn.ghost {
  background: transparent;
  color: var(--ink-soft);
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
  margin-bottom: 12px;
  line-height: 1;
}
.empty-state p {
  font-family: var(--font-serif);
  font-size: 14px;
  letter-spacing: 0.2em;
}
</style>
