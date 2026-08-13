package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Orders;
import com.ruoyi.system.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    @GetMapping("/list")
    public R getOrdersList(Orders orders) {
        List<Orders> orderList = ordersService.selectOrdersList(orders);
        return R.ok(orderList);
    }

    @GetMapping("/{id}")
    public R getOrdersById(@PathVariable Long id) {
        Orders orders = ordersService.selectOrdersById(id);
        return R.ok(orders);
    }

    @GetMapping("/detail/{orderNo}")
    public R getOrdersByOrderNo(@PathVariable String orderNo) {
        Orders orders = ordersService.selectOrdersByOrderNo(orderNo);
        return R.ok(orders);
    }

    @PostMapping("/create")
    public R createOrder(@RequestBody Map<String, Object> params) {
        Long buyerId = Long.parseLong(params.get("buyerId").toString());
        Long sellerId = Long.parseLong(params.get("sellerId").toString());
        List<Long> bookIds = (List<Long>) params.get("bookIds");
        Integer deliveryMethod = Integer.parseInt(params.get("deliveryMethod").toString());
        String pickupPoint = params.get("pickupPoint") != null ? params.get("pickupPoint").toString() : null;
        String payMethod = params.get("payMethod").toString();
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;

        Orders orders = ordersService.createOrder(buyerId, sellerId, bookIds, deliveryMethod, null, pickupPoint, payMethod, remark);
        return R.ok(orders);
    }

    @PutMapping("/pay")
    public R payOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        String payMethod = params.get("payMethod").toString();
        ordersService.payOrder(orderId, payMethod);
        return R.ok("支付成功");
    }

    @PutMapping("/ship")
    public R shipOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        String logisticsNo = params.get("logisticsNo").toString();
        ordersService.shipOrder(orderId, logisticsNo);
        return R.ok("发货成功");
    }

    @PutMapping("/complete")
    public R completeOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        ordersService.completeOrder(orderId);
        return R.ok("订单完成");
    }

    @PutMapping("/close")
    public R closeOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        ordersService.closeOrder(orderId, reason);
        return R.ok("订单已关闭");
    }
}