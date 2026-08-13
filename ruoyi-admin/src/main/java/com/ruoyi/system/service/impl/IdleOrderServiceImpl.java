package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.IdleOrderMapper;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.service.IIdleOrderService;

/**
 * 商品交易订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-13
 */
@Service
public class IdleOrderServiceImpl implements IIdleOrderService 
{
    @Autowired
    private IdleOrderMapper idleOrderMapper;

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
        idleOrder.setCreateTime(DateUtils.getNowDate());
        return idleOrderMapper.insertIdleOrder(idleOrder);
    }

    /**
     * 修改商品交易订单
     * 
     * @param idleOrder 商品交易订单
     * @return 结果
     */
    @Override
    public int updateIdleOrder(IdleOrder idleOrder)
    {
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
    public int deleteIdleOrderByOrderIds(Long[] orderIds)
    {
        return idleOrderMapper.deleteIdleOrderByOrderIds(orderIds);
    }

    /**
     * 删除商品交易订单信息
     * 
     * @param orderId 商品交易订单主键
     * @return 结果
     */
    @Override
    public int deleteIdleOrderByOrderId(Long orderId)
    {
        return idleOrderMapper.deleteIdleOrderByOrderId(orderId);
    }

    @Override
    public int countOrdersByUserId(Long userId)
    {
        return idleOrderMapper.countOrdersByUserId(userId);
    }
}
