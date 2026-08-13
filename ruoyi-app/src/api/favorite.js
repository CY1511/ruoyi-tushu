import request from '@/utils/request'

// 收藏/取消收藏（切换）
export function toggleFavorite(goodsId) {
  return request({
    url: '/app/favorite/' + goodsId,
    method: 'post'
  })
}

// 检查是否已收藏
export function checkFavorite(goodsId) {
  return request({
    url: '/app/favorite/check/' + goodsId,
    method: 'get'
  })
}

// 我的收藏列表
export function getFavoriteList(query) {
  return request({
    url: '/app/favorite/list',
    method: 'get',
    params: query
  })
}
