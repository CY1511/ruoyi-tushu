package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.OrderItem;

@Mapper
public interface OrderItemMapper {
    List<OrderItem> selectOrderItemByOrderId(Long orderId);
    int insertOrderItem(OrderItem item);
    int insertOrderItems(List<OrderItem> items);
    int deleteOrderItemByOrderId(Long orderId);
}