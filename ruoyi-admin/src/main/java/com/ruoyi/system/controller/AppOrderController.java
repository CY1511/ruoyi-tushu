package com.ruoyi.system.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.service.IIdleGoodsService;
import com.ruoyi.system.service.IIdleOrderService;

/**
 * C端-订单接口
 */
@RestController
@RequestMapping("/app/order")
public class AppOrderController extends BaseController
{
    @Autowired
    private IIdleOrderService idleOrderService;

    @Autowired
    private IIdleGoodsService idleGoodsService;

    /** 创建订单 */
    @PostMapping
    public AjaxResult create(@RequestBody IdleOrder order)
    {
        Long goodsId = order.getGoodsId();
        IdleGoods goods = idleGoodsService.selectIdleGoodsByGoodsId(goodsId);
        if (goods == null)
        {
            return error("商品不存在");
        }
        if (!"1".equals(goods.getGoodsStatus()))
        {
            return error("该商品暂不可购买");
        }
        // 不能买自己的商品
        if (goods.getPublisherId().equals(SecurityUtils.getUserId()))
        {
            return error("不能购买自己发布的商品");
        }
        // 生成订单编号
        order.setOrderNo(IdUtils.fastSimpleUUID().substring(0, 20));
        order.setSellerId(goods.getPublisherId());
        order.setBuyerId(SecurityUtils.getUserId());
        order.setOrderPrice(goods.getPrice());
        order.setOrderStatus("0");
        // 商品状态变为已预订
        IdleGoods updateGoods = new IdleGoods();
        updateGoods.setGoodsId(goodsId);
        updateGoods.setGoodsStatus("2");
        idleGoodsService.updateIdleGoods(updateGoods);
        return toAjax(idleOrderService.insertIdleOrder(order));
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

    /** 订单详情 */
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        IdleOrder order = idleOrderService.selectIdleOrderByOrderId(orderId);
        if (order == null)
        {
            return error("订单不存在");
        }
        // 只能查看自己的订单（买家或卖家）
        Long userId = SecurityUtils.getUserId();
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId))
        {
            return error("无权查看该订单");
        }
        return success(order);
    }

    /** 买家确认收货 */
    @PutMapping("/confirm/{orderId}")
    public AjaxResult confirm(@PathVariable Long orderId)
    {
        IdleOrder order = idleOrderService.selectIdleOrderByOrderId(orderId);
        if (order == null || !order.getBuyerId().equals(SecurityUtils.getUserId()))
        {
            return error("无权操作");
        }
        if (!"1".equals(order.getOrderStatus()))
        {
            return error("当前状态不可确认收货");
        }
        IdleOrder update = new IdleOrder();
        update.setOrderId(orderId);
        update.setOrderStatus("2");
        update.setFinishTime(DateUtils.getNowDate());
        // 商品状态变为已售出
        IdleGoods goods = new IdleGoods();
        goods.setGoodsId(order.getGoodsId());
        goods.setGoodsStatus("3");
        idleGoodsService.updateIdleGoods(goods);
        return toAjax(idleOrderService.updateIdleOrder(update));
    }

    /** 取消订单 */
    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        IdleOrder order = idleOrderService.selectIdleOrderByOrderId(orderId);
        if (order == null)
        {
            return error("订单不存在");
        }
        Long userId = SecurityUtils.getUserId();
        // 买家或卖家都可以取消待确认的订单
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId))
        {
            return error("无权操作");
        }
        if (!"0".equals(order.getOrderStatus()))
        {
            return error("当前状态不可取消");
        }
        IdleOrder update = new IdleOrder();
        update.setOrderId(orderId);
        update.setOrderStatus("3");
        // 商品恢复上架
        IdleGoods goods = new IdleGoods();
        goods.setGoodsId(order.getGoodsId());
        goods.setGoodsStatus("1");
        idleGoodsService.updateIdleGoods(goods);
        return toAjax(idleOrderService.updateIdleOrder(update));
    }

    /** 卖家确认订单 */
    @PutMapping("/accept/{orderId}")
    public AjaxResult accept(@PathVariable Long orderId)
    {
        IdleOrder order = idleOrderService.selectIdleOrderByOrderId(orderId);
        if (order == null || !order.getSellerId().equals(SecurityUtils.getUserId()))
        {
            return error("无权操作");
        }
        if (!"0".equals(order.getOrderStatus()))
        {
            return error("当前状态不可确认");
        }
        IdleOrder update = new IdleOrder();
        update.setOrderId(orderId);
        update.setOrderStatus("1");
        return toAjax(idleOrderService.updateIdleOrder(update));
    }
}
