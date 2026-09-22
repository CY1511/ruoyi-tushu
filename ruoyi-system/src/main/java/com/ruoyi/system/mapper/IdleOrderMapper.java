package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.IdleOrder;

/**
 * 商品交易订单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-13
 */
public interface IdleOrderMapper 
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
     * 
     * @param idleOrder 商品交易订单
     * @return 结果
     */
    public int updateIdleOrder(IdleOrder idleOrder);

    /**
     * 删除商品交易订单
     * 
     * @param orderId 商品交易订单主键
     * @return 结果
     */
    public int deleteIdleOrderByOrderId(Long orderId);

    /**
     * 批量删除商品交易订单
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIdleOrderByOrderIds(Long[] orderIds);

    /**
     * 根据用户ID统计订单数量（买家或卖家）
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    public int countOrdersByUserId(Long userId);
}
