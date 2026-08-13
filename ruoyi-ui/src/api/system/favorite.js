import request from '@/utils/request'

export function listFavorite(query) {
  return request({
    url: '/system/favorite/list',
    method: 'get',
    params: query
  })
}

export function getFavorite(favoriteId) {
  return request({
    url: '/system/favorite/' + favoriteId,
    method: 'get'
  })
}

export function addFavorite(data) {
  return request({
    url: '/system/favorite',
    method: 'post',
    data: data
  })
}

export function updateFavorite(data) {
  return request({
    url: '/system/favorite',
    method: 'put',
    data: data
  })
}

export function delFavorite(favoriteId) {
  return request({
    url: '/system/favorite/' + favoriteId,
    method: 'delete'
  })
}