package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Orders;

@Mapper
public interface OrdersMapper {
    List<Orders> selectOrdersList(Orders orders);
    Orders selectOrdersById(Long id);
    Orders selectOrdersByOrderNo(String orderNo);
    int insertOrders(Orders orders);
    int updateOrders(Orders orders);
    int updateOrdersStatus(Long id, Integer status);
    int deleteOrdersById(Long id);
}