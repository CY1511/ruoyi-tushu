import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页', showTabBar: true }
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('@/views/Category.vue'),
    meta: { title: '分类', showTabBar: true }
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('@/views/Publish.vue'),
    meta: { title: '发布', showTabBar: true, requireAuth: true }
  },
  {
    path: '/order',
    name: 'OrderList',
    component: () => import('@/views/OrderList.vue'),
    meta: { title: '订单', showTabBar: true, requireAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '我的', showTabBar: true }
  },
  {
    path: '/goods/:id',
    name: 'GoodsDetail',
    component: () => import('@/views/GoodsDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/my-goods',
    name: 'MyGoods',
    component: () => import('@/views/MyGoods.vue'),
    meta: { title: '我的商品', requireAuth: true }
  },
  {
    path: '/my-favorite',
    name: 'MyFavorite',
    component: () => import('@/views/MyFavorite.vue'),
    meta: { title: '我的收藏', requireAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = new VueRouter({
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta && to.meta.title) {
    document.title = to.meta.title + ' · 摊 · 二手书市'
  }
  if (to.meta && to.meta.requireAuth) {
    const token = store.state.token
    if (!token) {
      if (to.path !== '/login') {
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
      } else {
        next()
      }
    } else if (!store.state.userInfo) {
      store.dispatch('GetUserInfo').then(() => {
        next()
      }).catch(() => {
        store.commit('SET_TOKEN', '')
        store.commit('SET_USER_INFO', null)
        localStorage.removeItem('token')
        if (to.path !== '/login') {
          next({
            path: '/login',
            query: { redirect: to.fullPath }
          })
        } else {
          next()
        }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
