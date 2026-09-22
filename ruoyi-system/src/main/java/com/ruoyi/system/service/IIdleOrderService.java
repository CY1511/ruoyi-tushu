package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.domain.IdleOrderStatusLog;
import com.ruoyi.system.enums.IdleOrderStatus;

/**
 * 商品交易订单Service接口
 * 
 * @author ruoyi
 * @date 2026-07-13
 */
public interface IIdleOrderService 
{
    /**
     * 查询商品交易订单
     * 
     * @param orderId 商品交易订单主键
     * @return 商品交易订单
     */
    public IdleOrder selectIdleOrderByOrderId(Long orderId);

    /**
     * 查询商品交易订单列表
     * 
     * @param idleOrder 商品交易订单
     * @return 商品交易订单集合
     */
    public List<IdleOrder> selectIdleOrderList(IdleOrder idleOrder);

    /**
     * 新增商品交易订单
     * 
     * @param idleOrder 商品交易订单
     * @return 结果
     */
    public int insertIdleOrder(IdleOrder idleOrder);

    /**
     * 修改商品交易订单
     * <p><b>不接受 orderStatus</b>：订单状态只能通过状态流转接口修改，
     * 否则前端可以直接传值绕过流转校验。</p>
     * 
     * @param idleOrder 商品交易订单
     * @return 结果
     */
    public int updateIdleOrder(IdleOrder idleOrder);

    /**
     * 批量删除商品交易订单（同时清理状态流转日志）
     * 
     * @param orderIds 需要删除的商品交易订单主键集合
     * @return 结果
     */
    public int deleteIdleOrderByOrderIds(Long[] orderIds);

    /**
     * 删除商品交易订单信息
     * 
     * @param orderId 商品交易订单主键
     * @return 结果
     */
    public int deleteIdleOrderByOrderId(Long orderId);

    /**
     * 根据用户ID统计订单数量（买家+卖家）
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    public int countOrdersByUserId(Long userId);

    // =========================================================================
    // 订单状态机
    // 核心原则：状态不能靠前端传值直接改，服务端只放行"合法流转"，
    // 每一次成功流转都写一条 idle_order_status_log。
    // =========================================================================

    /**
     * 创建订单（C 端买家下单）
     * <p>整个方法一个事务：校验商品可售 → 条件更新锁定商品（防并发抢单）→ 落订单 → 记流转日志。
     * 原来这些步骤散在 Controller 里且没有事务，商品锁定成功但订单插入失败时会留下脏数据。</p>
     *
     * @param goodsId 商品ID
     * @return 新建的订单
     */
    public IdleOrder createOrder(Long goodsId);

    /**
     * 通用状态流转（校验合法性 + 记录日志 + 同步商品状态）
     *
     * @param orderId 订单ID
     * @param target  目标状态
     * @param remark  备注（如取消原因）
     * @return 结果
     */
    public int changeStatus(Long orderId, IdleOrderStatus target, String remark);

    /**
     * 卖家确认接单：待确认 → 交易中
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int acceptOrder(Long orderId);

    /**
     * 买家确认收货：交易中 → 已完成
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int confirmReceipt(Long orderId);

    /**
     * 取消订单：待确认/交易中 → 已取消（买家或卖家均可发起）
     *
     * @param orderId 订单ID
     * @param reason  取消原因
     * @return 结果
     */
    public int cancelOrder(Long orderId, String reason);

    /**
     * 管理端强制流转（不受买家/卖家身份限制，但仍必须是合法流转）
     *
     * @param orderId    订单ID
     * @param targetCode 目标状态码
     * @param remark     备注
     * @return 结果
     */
    public int changeStatusByAdmin(Long orderId, String targetCode, String remark);

    /**
     * 查询订单的状态流转记录
     *
     * @param orderId 订单ID
     * @return 流转记录集合
     */
    public List<IdleOrderStatusLog> selectStatusLogs(Long orderId);

    /**
     * 查询"我的订单"详情（C 端）
     * <p>只有买家或卖家本人可以查看，非相关方抛 {@code ServiceException}。</p>
     *
     * @param orderId 订单ID
     * @return 订单
     */
    public IdleOrder selectMyOrderByOrderId(Long orderId);
}
