import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/dev-api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 如果存在token，则在请求头中携带
    const token = store.state.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.log('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果返回的状态码不是200，说明接口有问题
    if (res.code !== undefined && res.code !== 200) {
      Message({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 3000
      })
      // 401: Token过期或未登录
      if (res.code === 401) {
        store.commit('SET_TOKEN', '')
        localStorage.removeItem('token')
        router.push({
          path: '/login',
          query: { redirect: router.currentRoute.fullPath }
        })
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    console.log('响应错误：', error)
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        Message.error('登录已过期，请重新登录')
        store.commit('SET_TOKEN', '')
        localStorage.removeItem('token')
        router.push({
          path: '/login',
          query: { redirect: router.currentRoute.fullPath }
        })
      } else {
        Message.error(error.response.data.msg || '网络请求失败')
      }
    } else {
      Message.error('网络连接失败，请检查网络')
    }
    return Promise.reject(error)
  }
)

export default service
