package com.ruoyi.system.service;

import com.ruoyi.system.domain.Orders;

import java.util.List;

public interface IOrdersService {

    List<Orders> selectOrdersList(Orders orders);

    Orders selectOrdersById(Long id);

    Orders selectOrdersByOrderNo(String orderNo);

    int insertOrders(Orders orders);

    int updateOrders(Orders orders);

    int updateOrdersStatus(Long id, Integer status);

    int deleteOrdersById(Long id);

    String generateOrderNo();

    Orders createOrder(Long buyerId, Long sellerId, List<Long> bookIds, Integer deliveryMethod,
                       Long addressId, String pickupPoint, String payMethod, String remark);

    int payOrder(Long orderId, String payMethod);

    int shipOrder(Long orderId, String logisticsNo);

    int completeOrder(Long orderId);

    int closeOrder(Long orderId, String reason);
}