package com.ruoyi.web.controller.idle;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.service.IIdleOrderService;

/**
 * C端-订单接口
 *
 * <p>Controller 现在只做参数接收与结果封装，三件事都下沉了：</p>
 * <ul>
 *   <li>下单的并发控制与事务 → {@code IIdleOrderService#createOrder}</li>
 *   <li>状态流转的合法性校验 → {@code IdleOrderStatus} + {@code #changeStatus}</li>
 *   <li>买家/卖家身份校验 → {@code #selectMyOrderByOrderId / acceptOrder / confirmReceipt / cancelOrder}</li>
 * </ul>
 */
@RestController
@RequestMapping("/app/order")
public class AppOrderController extends BaseController
{
    @Autowired
    private IIdleOrderService idleOrderService;

    /** 创建订单（买家下单） */
    @Log(title = "C端订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody IdleOrder order)
    {
        // 状态、价格、买卖双方都由服务端决定，请求体里只认 goodsId
        return success(idleOrderService.createOrder(order.getGoodsId()));
    }

    /** 我买到的订单 */
    @GetMapping("/buyer/list")
    public TableDataInfo buyerList(IdleOrder order)
    {
        startPage();
        order.setBuyerId(SecurityUtils.getUserId());
        List<IdleOrder> list = idleOrderService.selectIdleOrderList(order);
        return getDataTable(list);
    }

    /** 我卖出的订单 */
    @GetMapping("/seller/list")
    public TableDataInfo sellerList(IdleOrder order)
    {
        startPage();
        order.setSellerId(SecurityUtils.getUserId());
        List<IdleOrder> list = idleOrderService.selectIdleOrderList(order);
        return getDataTable(list);
    }

    /** 订单详情（仅买卖双方可见） */
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(idleOrderService.selectMyOrderByOrderId(orderId));
    }

    /** 订单状态流转记录（时间线） */
    @GetMapping("/statusLog/{orderId}")
    public AjaxResult statusLog(@PathVariable Long orderId)
    {
        // 先过一遍可见性校验，避免把别人的订单时间线暴露出去
        idleOrderService.selectMyOrderByOrderId(orderId);
        return success(idleOrderService.selectStatusLogs(orderId));
    }

    /** 买家确认收货：交易中 → 已完成 */
    @Log(title = "C端订单", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{orderId}")
    public AjaxResult confirm(@PathVariable Long orderId)
    {
        return toAjax(idleOrderService.confirmReceipt(orderId));
    }

    /** 取消订单：待确认/交易中 → 已取消 */
    @Log(title = "C端订单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId,
                             @RequestParam(value = "reason", required = false) String reason)
    {
        return toAjax(idleOrderService.cancelOrder(orderId, reason));
    }

    /** 卖家确认接单：待确认 → 交易中 */
    @Log(title = "C端订单", businessType = BusinessType.UPDATE)
    @PutMapping("/accept/{orderId}")
    public AjaxResult accept(@PathVariable Long orderId)
    {
        return toAjax(idleOrderService.acceptOrder(orderId));
    }
}
