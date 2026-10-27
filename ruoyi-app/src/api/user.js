import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data: data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/register',
    method: 'post',
    data: data
  })
}

// 获取验证码
export function getCaptchaImage() {
  return request({
    url: '/captchaImage',
    method: 'get'
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 退出登录（服务端注销，让当前 token 立即失效）
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 我发布的商品
export function getMyGoods(query) {
  return request({
    url: '/app/user/goods/list',
    method: 'get',
    params: query
  })
}

// 获取用户统计数据
export function getUserStats() {
  return request({
    url: '/app/user/stats',
    method: 'get'
  })
}
