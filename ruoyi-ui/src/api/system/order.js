import request from '@/utils/request'

// 查询商品交易订单列表
export function listOrder(query) {
  return request({
    url: '/system/order/list',
    method: 'get',
    params: query
  })
}

// 查询商品交易订单详细
export function getOrder(orderId) {
  return request({
    url: '/system/order/' + orderId,
    method: 'get'
  })
}

// 新增商品交易订单
export function addOrder(data) {
  return request({
    url: '/system/order',
    method: 'post',
    data: data
  })
}

// 修改商品交易订单（不含订单状态，状态走 changeOrderStatus）
export function updateOrder(data) {
  return request({
    url: '/system/order',
    method: 'put',
    data: data
  })
}

// 删除商品交易订单
export function delOrder(orderId) {
  return request({
    url: '/system/order/' + orderId,
    method: 'delete'
  })
}

// 查询某订单当前可流转到的状态（合法流转表以后端为准，前端不硬编码）
export function listNextStatus(orderId) {
  return request({
    url: '/system/order/nextStatus/' + orderId,
    method: 'get'
  })
}

// 流转订单状态
export function changeOrderStatus(orderId, orderStatus, remark) {
  return request({
    url: '/system/order/changeStatus',
    method: 'put',
    params: { remark: remark },
    data: { orderId: orderId, orderStatus: orderStatus }
  })
}

// 查询订单状态流转记录
export function listStatusLog(orderId) {
  return request({
    url: '/system/order/statusLog/' + orderId,
    method: 'get'
  })
}
