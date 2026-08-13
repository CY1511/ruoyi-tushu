import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/app/order',
    method: 'post',
    data: data
  })
}

// 我买到的订单
export function getBuyerOrders(query) {
  return request({
    url: '/app/order/buyer/list',
    method: 'get',
    params: query
  })
}

// 我卖出的订单
export function getSellerOrders(query) {
  return request({
    url: '/app/order/seller/list',
    method: 'get',
    params: query
  })
}

// 订单详情
export function getOrder(id) {
  return request({
    url: '/app/order/' + id,
    method: 'get'
  })
}

// 买家确认收货
export function confirmOrder(id) {
  return request({
    url: '/app/order/confirm/' + id,
    method: 'put'
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: '/app/order/cancel/' + id,
    method: 'put'
  })
}

// 卖家确认订单
export function acceptOrder(id) {
  return request({
    url: '/app/order/accept/' + id,
    method: 'put'
  })
}
