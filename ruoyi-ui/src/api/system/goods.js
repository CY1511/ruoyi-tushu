import request from '@/utils/request'

export function getImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  return process.env.VUE_APP_BASE_API + url
}

export function listGoods(query) {
  return request({
    url: '/system/goods/list',
    method: 'get',
    params: query
  })
}

export function getGoods(goodsId) {
  return request({
    url: '/system/goods/' + goodsId,
    method: 'get'
  })
}

export function addGoods(data) {
  return request({
    url: '/system/goods',
    method: 'post',
    data: data
  })
}

export function updateGoods(data) {
  return request({
    url: '/system/goods',
    method: 'put',
    data: data
  })
}

export function auditGoods(data) {
  return request({
    url: '/system/goods/audit',
    method: 'put',
    data: data
  })
}

export function offlineGoods(goodsId) {
  return request({
    url: '/system/goods/offline/' + goodsId,
    method: 'put'
  })
}

export function onlineGoods(goodsId) {
  return request({
    url: '/system/goods/online/' + goodsId,
    method: 'put'
  })
}

export function delGoods(goodsId) {
  return request({
    url: '/system/goods/' + goodsId,
    method: 'delete'
  })
}

export function exportGoods(query) {
  return request({
    url: '/system/goods/export',
    method: 'post',
    data: query
  })
}
