import request from '@/utils/request'

// 商品列表（公开）
export function listGoods(query) {
  return request({
    url: '/app/goods/list',
    method: 'get',
    params: query
  })
}

// 商品详情（公开）
export function getGoods(id) {
  return request({
    url: '/app/goods/' + id,
    method: 'get'
  })
}

// 发布商品
export function addGoods(data) {
  return request({
    url: '/app/goods',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateGoods(data) {
  return request({
    url: '/app/goods',
    method: 'put',
    data: data
  })
}

// 删除商品
export function deleteGoods(id) {
  return request({
    url: '/app/goods/' + id,
    method: 'delete'
  })
}

// 下架商品（只关状态，不会把商品打回待审核）
export function offlineGoods(id) {
  return request({
    url: '/app/goods/offline/' + id,
    method: 'put'
  })
}

// 重新上架商品（仅已过审且处于已下架状态）
export function onlineGoods(id) {
  return request({
    url: '/app/goods/online/' + id,
    method: 'put'
  })
}
