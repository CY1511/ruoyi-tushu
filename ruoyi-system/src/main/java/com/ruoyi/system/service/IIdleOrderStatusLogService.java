package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.domain.IdleOrderStatusLog;

/**
 * 订单状态流转日志Service接口
 *
 * @author ruoyi
 */
public interface IIdleOrderStatusLogService
{
    /**
     * 查询某订单的流转记录
     *
     * @param orderId 订单ID
     * @return 流转记录集合
     */
    public List<IdleOrderStatusLog> selectLogsByOrderId(Long orderId);

    /**
     * 记录一次状态流转
     *
     * @param order      订单（取订单号）
     * @param fromStatus 变更前状态码，首次下单传 null
     * @param toStatus   变更后状态码
     * @param remark     备注（如取消原因）
     * @return 结果
     */
    public int record(IdleOrder order, String fromStatus, String toStatus, String remark);
}
