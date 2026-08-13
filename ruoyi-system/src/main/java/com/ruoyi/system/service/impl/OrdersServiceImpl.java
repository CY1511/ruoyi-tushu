package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Book;
import com.ruoyi.system.domain.OrderItem;
import com.ruoyi.system.domain.Orders;
import com.ruoyi.system.mapper.BookMapper;
import com.ruoyi.system.mapper.OrderItemMapper;
import com.ruoyi.system.mapper.OrdersMapper;
import com.ruoyi.system.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrdersServiceImpl implements IOrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<Orders> selectOrdersList(Orders orders) {
        return ordersMapper.selectOrdersList(orders);
    }

    @Override
    public Orders selectOrdersById(Long id) {
        return ordersMapper.selectOrdersById(id);
    }

    @Override
    public Orders selectOrdersByOrderNo(String orderNo) {
        return ordersMapper.selectOrdersByOrderNo(orderNo);
    }

    @Override
    @Transactional
    public int insertOrders(Orders orders) {
        orders.setCreateTime(DateUtils.getNowDate());
        orders.setUpdateTime(DateUtils.getNowDate());
        return ordersMapper.insertOrders(orders);
    }

    @Override
    @Transactional
    public int updateOrders(Orders orders) {
        orders.setUpdateTime(DateUtils.getNowDate());
        return ordersMapper.updateOrders(orders);
    }

    @Override
    @Transactional
    public int updateOrdersStatus(Long id, Integer status) {
        return ordersMapper.updateOrdersStatus(id, status);
    }

    @Override
    @Transactional
    public int deleteOrdersById(Long id) {
        orderItemMapper.deleteOrderItemByOrderId(id);
        return ordersMapper.deleteOrdersById(id);
    }

    @Override
    public String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + timestamp + random;
    }

    @Override
    @Transactional
    public Orders createOrder(Long buyerId, Long sellerId, List<Long> bookIds, Integer deliveryMethod,
                              Long addressId, String pickupPoint, String payMethod, String remark) {
        Orders orders = new Orders();
        orders.setOrderNo(generateOrderNo());
        orders.setBuyerId(buyerId);
        orders.setSellerId(sellerId);
        orders.setDeliveryMethod(deliveryMethod);
        orders.setPickupPoint(pickupPoint);
        orders.setPayMethod(payMethod);
        orders.setRemark(remark);
        orders.setStatus(0);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Long bookId : bookIds) {
            Book book = bookMapper.selectBookById(bookId);
            if (book == null || book.getStatus() != 1) {
                throw new ServiceException("商品不存在或已下架");
            }
            if (book.getStock() <= 0) {
                throw new ServiceException("商品库存不足");
            }
            totalAmount = totalAmount.add(book.getPrice());
        }

        orders.setTotalAmount(totalAmount);
        orders.setPayAmount(totalAmount);
        ordersMapper.insertOrders(orders);

        for (Long bookId : bookIds) {
            Book book = bookMapper.selectBookById(bookId);
            OrderItem item = new OrderItem();
            item.setOrderId(orders.getId());
            item.setBookId(bookId);
            item.setTitle(book.getTitle());
            item.setCoverImage(book.getCoverImage());
            item.setPrice(book.getPrice());
            item.setQuantity(1);
            item.setCondition(book.getCondition());
            orderItemMapper.insertOrderItem(item);

            book.setStock(book.getStock() - 1);
            book.setSales(book.getSales() + 1);
            bookMapper.updateBook(book);
        }

        return orders;
    }

    @Override
    @Transactional
    public int payOrder(Long orderId, String payMethod) {
        Orders orders = ordersMapper.selectOrdersById(orderId);
        if (orders == null) {
            throw new ServiceException("订单不存在");
        }
        if (orders.getStatus() != 0) {
            throw new ServiceException("订单状态不正确");
        }
        orders.setStatus(1);
        orders.setPayMethod(payMethod);
        orders.setPayTime(DateUtils.getNowDate());
        return ordersMapper.updateOrders(orders);
    }

    @Override
    @Transactional
    public int shipOrder(Long orderId, String logisticsNo) {
        Orders orders = ordersMapper.selectOrdersById(orderId);
        if (orders == null) {
            throw new ServiceException("订单不存在");
        }
        if (orders.getStatus() != 1) {
            throw new ServiceException("订单状态不正确");
        }
        orders.setStatus(2);
        orders.setLogisticsNo(logisticsNo);
        orders.setShipTime(DateUtils.getNowDate());
        return ordersMapper.updateOrders(orders);
    }

    @Override
    @Transactional
    public int completeOrder(Long orderId) {
        Orders orders = ordersMapper.selectOrdersById(orderId);
        if (orders == null) {
            throw new ServiceException("订单不存在");
        }
        if (orders.getStatus() != 2) {
            throw new ServiceException("订单状态不正确");
        }
        orders.setStatus(3);
        orders.setCompleteTime(DateUtils.getNowDate());
        return ordersMapper.updateOrders(orders);
    }

    @Override
    @Transactional
    public int closeOrder(Long orderId, String reason) {
        Orders orders = ordersMapper.selectOrdersById(orderId);
        if (orders == null) {
            throw new ServiceException("订单不存在");
        }
        orders.setStatus(-1);
        orders.setCloseTime(DateUtils.getNowDate());
        return ordersMapper.updateOrders(orders);
    }
}