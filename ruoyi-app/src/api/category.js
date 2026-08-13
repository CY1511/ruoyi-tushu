import request from '@/utils/request'

// 分类树
export function getCategoryTree() {
  return request({
    url: '/app/category/tree',
    method: 'get'
  })
}
