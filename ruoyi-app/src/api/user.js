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

// 当前用户信息（C端）
export function getAppUserInfo() {
  return request({
    url: '/app/user/info',
    method: 'get'
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
