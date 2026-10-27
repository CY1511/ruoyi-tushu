package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.IdleGoods;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.domain.IdleOrderStatusLog;
import com.ruoyi.system.enums.IdleOrderStatus;
import com.ruoyi.system.mapper.IdleOrderMapper;
import com.ruoyi.system.mapper.IdleOrderStatusLogMapper;
import com.ruoyi.system.service.IIdleGoodsService;
import com.ruoyi.system.service.IIdleOrderService;
import com.ruoyi.system.service.IIdleOrderStatusLogService;

/**
 * 商品交易订单Service业务层处理
 *
 * <p>状态机说明见 {@link IdleOrderStatus}：所有状态变更必须经过本类的
 * {@link #changeStatus(Long, IdleOrderStatus, String)}，通用更新接口
 * {@link #updateIdleOrder(IdleOrder)} 不再接受 orderStatus 字段。</p>
 *
 * @author ruoyi
 * @date 2026-07-13
 */
@Service
public class IdleOrderServiceImpl implements IIdleOrderService 
{
    @Autowired
    private IdleOrderMapper idleOrderMapper;

    @Autowired
    private IdleOrderStatusLogMapper idleOrderStatusLogMapper;

    @Autowired
    private IIdleOrderStatusLogService idleOrderStatusLogService;

    @Autowired
    private IIdleGoodsService idleGoodsService;

    /**
     * 查询商品交易订单
     * 
     * @param orderId 商品交易订单主键
     * @return 商品交易订单
     */
    @Override
    public IdleOrder selectIdleOrderByOrderId(Long orderId)
    {
        return idleOrderMapper.selectIdleOrderByOrderId(orderId);
    }

    /**
     * 查询商品交易订单列表
     * 
     * @param idleOrder 商品交易订单
     * @return 商品交易订单
     */
    @Override
    public List<IdleOrder> selectIdleOrderList(IdleOrder idleOrder)
    {
        return idleOrderMapper.selectIdleOrderList(idleOrder);
    }

    /**
     * 新增商品交易订单
     * 
     * @param idleOrder 商品交易订单
     * @return 结果
     */
    @Override
    public int insertIdleOrder(IdleOrder idleOrder)
    {
        if (StringUtils.isEmpty(idleOrder.getOrderNo()))
        {
            idleOrder.setOrderNo(IdUtils.fastSimpleUUID().substring(0, 20));
        }
        if (StringUtils.isEmpty(idleOrder.getOrderStatus()))
        {
            idleOrder.setOrderStatus(IdleOrderStatus.PENDING_CONFIRM.getCode());
        }
        idleOrder.setCreateTime(DateUtils.getNowDate());
        return idleOrderMapper.insertIdleOrder(idleOrder);
    }

    /**
     * 修改商品交易订单
     *
     * <p>订单状态不走这里：只要请求体带了 orderStatus 就直接拒绝，
     * 避免有人绕过状态机把订单改成任意状态。</p>
     * 
     * @param idleOrder 商品交易订单
     * @return 结果
     */
    @Override
    public int updateIdleOrder(IdleOrder idleOrder)
    {
        if (idleOrder.getOrderStatus() != null)
        {
            throw new ServiceException("订单状态不允许通过修改接口变更，请调用状态流转接口");
        }
        idleOrder.setUpdateTime(DateUtils.getNowDate());
        return idleOrderMapper.updateIdleOrder(idleOrder);
    }

    /**
     * 批量删除商品交易订单
     * 
     * @param orderIds 需要删除的商品交易订单主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteIdleOrderByOrderIds(Long[] orderIds)
    {
        if (orderIds != null && orderIds.length > 0)
        {
            // 订单删了，它的流转日志没有存在意义，一并清掉避免留孤儿数据
            idleOrderStatusLogMapper.deleteIdleOrderStatusLogByOrderIds(orderIds);
        }
        return idleOrderMapper.deleteIdleOrderByOrderIds(orderIds);
    }

    /**
     * 删除商品交易订单信息
     * 
     * @param orderId 商品交易订单主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteIdleOrderByOrderId(Long orderId)
    {
        return deleteIdleOrderByOrderIds(new Long[] { orderId });
    }

    @Override
    public int countOrdersByUserId(Long userId)
    {
        return idleOrderMapper.countOrdersByUserId(userId);
    }

    // =========================================================================
    // 订单状态机
    // =========================================================================

    /**
     * 创建订单（C 端买家下单）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdleOrder createOrder(Long goodsId)
    {
        IdleGoods goods = idleGoodsService.selectIdleGoodsByGoodsId(goodsId);
        if (goods == null)
        {
            throw new ServiceException("商品不存在");
        }
        if (!IdleGoods.AUDIT_PASSED.equals(goods.getAuditStatus()))
        {
            throw new ServiceException("商品尚未通过审核，暂不可购买");
        }
        Long buyerId = SecurityUtils.getUserId();
        if (buyerId.equals(goods.getPublisherId()))
        {
            throw new ServiceException("不能购买自己发布的商品");
        }
        // 关键一步：条件更新锁定商品。并发下两个买家同时下单，
        // 只有一条 update 能命中 goods_status = '1'，另一条影响行数为 0 直接被拒。
        if (idleGoodsService.bookGoods(goodsId) == 0)
        {
            throw new ServiceException("手慢了，该商品已被预订或已下架");
        }

        IdleOrder order = new IdleOrder();
        order.setOrderNo(IdUtils.fastSimpleUUID().substring(0, 20));
        order.setGoodsId(goodsId);
        order.setSellerId(goods.getPublisherId());
        order.setBuyerId(buyerId);
        // 成交价以服务端商品价格为准，不接受客户端传值
        order.setOrderPrice(goods.getPrice());
        order.setOrderStatus(IdleOrderStatus.PENDING_CONFIRM.getCode());
        order.setCreateBy(SecurityUtils.getUsername());
        idleOrderMapper.insertIdleOrder(order);

        // 下单本身就是一次状态流转（null -> 待确认），落日志保证时间线完整
        idleOrderStatusLogService.record(order, null, IdleOrderStatus.PENDING_CONFIRM.getCode(), "买家下单");
        return order;
    }

    /**
     * 通用状态流转
     *
     * <p>注意：本方法是被同类其它流转入口以 {@code this.changeStatus(...)} 调用的，
     * Spring 代理不会拦截类内部调用，因此事务注解必须同时挂在各个对外入口方法上
     * （acceptOrder / confirmReceipt / cancelOrder / changeStatusByAdmin），
     * 否则"改订单 + 同步商品 + 写流转日志"三步会跑在事务外。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(Long orderId, IdleOrderStatus target, String remark)
    {
        IdleOrder order = requireOrder(orderId);
        IdleOrderStatus current = IdleOrderStatus.of(order.getOrderStatus());
        if (current == target)
        {
            throw new ServiceException("订单已经是「" + target.getLabel() + "」状态，无需重复操作");
        }
        if (!IdleOrderStatus.canTransfer(current.getCode(), target))
        {
            throw new ServiceException(String.format("订单不能从「%s」变更为「%s」",
                    current.getLabel(), target.getLabel()));
        }
        return doChangeStatus(order, current, target, remark);
    }

    /**
     * 卖家确认接单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int acceptOrder(Long orderId)
    {
        IdleOrder order = requireOrder(orderId);
        requireRole(order, true, "只有卖家可以确认接单");
        return changeStatus(orderId, IdleOrderStatus.TRADING, "卖家确认接单");
    }

    /**
     * 买家确认收货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmReceipt(Long orderId)
    {
        IdleOrder order = requireOrder(orderId);
        requireRole(order, false, "只有买家可以确认收货");
        return changeStatus(orderId, IdleOrderStatus.FINISHED, "买家确认收货");
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelOrder(Long orderId, String reason)
    {
        IdleOrder order = requireOrder(orderId);
        Long userId = SecurityUtils.getUserId();
        boolean isParty = userId.equals(order.getBuyerId()) || userId.equals(order.getSellerId());
        if (!isParty)
        {
            throw new ServiceException("无权操作该订单", 403);
        }
        return changeStatus(orderId, IdleOrderStatus.CANCELED,
                StringUtils.isEmpty(reason) ? "买家/卖家取消订单" : reason);
    }

    /**
     * 管理端强制流转
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatusByAdmin(Long orderId, String targetCode, String remark)
    {
        return changeStatus(orderId, IdleOrderStatus.of(targetCode), remark);
    }

    @Override
    public List<IdleOrderStatusLog> selectStatusLogs(Long orderId)
    {
        return idleOrderStatusLogService.selectLogsByOrderId(orderId);
    }

    /**
     * 查询"我的订单"详情（C 端）
     */
    @Override
    public IdleOrder selectMyOrderByOrderId(Long orderId)
    {
        IdleOrder order = requireOrder(orderId);
        Long userId = SecurityUtils.getUserId();
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId()))
        {
            throw new ServiceException("无权查看该订单", 403);
        }
        return order;
    }

    /**
     * 真正执行状态变更：更新订单 → 同步商品状态 → 写流转日志，三步同一事务
     */
    private int doChangeStatus(IdleOrder order, IdleOrderStatus from, IdleOrderStatus to, String remark)
    {
        IdleOrder update = new IdleOrder();
        update.setOrderId(order.getOrderId());
        update.setOrderStatus(to.getCode());
        update.setUpdateBy(currentUsername());
        if (to == IdleOrderStatus.FINISHED)
        {
            update.setFinishTime(DateUtils.getNowDate());
        }
        if (to == IdleOrderStatus.CANCELED && StringUtils.isNotEmpty(remark))
        {
            update.setCancelReason(remark);
        }
        int rows = idleOrderMapper.updateIdleOrder(update);

        syncGoodsStatus(order.getGoodsId(), to);

        idleOrderStatusLogService.record(order, from.getCode(), to.getCode(), remark);
        return rows;
    }

    /**
     * 订单状态联动商品状态：
     * 交易中 → 商品已预订；已完成 → 商品已售出；已取消 → 商品恢复上架
     */
    private void syncGoodsStatus(Long goodsId, IdleOrderStatus to)
    {
        if (goodsId == null)
        {
            return;
        }
        switch (to)
        {
            case TRADING:
                idleGoodsService.bookGoods(goodsId);
                break;
            case FINISHED:
                idleGoodsService.markGoodsSold(goodsId);
                break;
            case CANCELED:
                idleGoodsService.releaseGoods(goodsId);
                break;
            default:
                break;
        }
    }

    /**
     * 取订单，不存在直接报错
     */
    private IdleOrder requireOrder(Long orderId)
    {
        if (orderId == null)
        {
            throw new ServiceException("订单ID不能为空");
        }
        IdleOrder order = idleOrderMapper.selectIdleOrderByOrderId(orderId);
        if (order == null)
        {
            throw new ServiceException("订单不存在");
        }
        return order;
    }

    /**
     * 校验当前用户是不是订单的买家/卖家
     *
     * @param order     订单
     * @param asSeller  true 校验卖家，false 校验买家
     * @param message   不满足时的提示
     */
    private void requireRole(IdleOrder order, boolean asSeller, String message)
    {
        Long userId = SecurityUtils.getUserId();
        Long expect = asSeller ? order.getSellerId() : order.getBuyerId();
        if (!userId.equals(expect))
        {
            throw new ServiceException(message, 403);
        }
    }

    private String currentUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return IdleOrderStatusLog.ROLE_SYSTEM;
        }
    }
}
