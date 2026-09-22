package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.IdleOrder;
import com.ruoyi.system.domain.IdleOrderStatusLog;
import com.ruoyi.system.mapper.IdleOrderStatusLogMapper;
import com.ruoyi.system.service.IIdleOrderStatusLogService;

/**
 * 订单状态流转日志Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class IdleOrderStatusLogServiceImpl implements IIdleOrderStatusLogService
{
    @Autowired
    private IdleOrderStatusLogMapper idleOrderStatusLogMapper;

    @Override
    public List<IdleOrderStatusLog> selectLogsByOrderId(Long orderId)
    {
        return idleOrderStatusLogMapper.selectStatusLogByOrderId(orderId);
    }

    @Override
    public int record(IdleOrder order, String fromStatus, String toStatus, String remark)
    {
        IdleOrderStatusLog log = new IdleOrderStatusLog();
        log.setOrderId(order.getOrderId());
        log.setOrderNo(order.getOrderNo());
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setRemark(remark);
        log.setCreateTime(DateUtils.getNowDate());
        // 操作人取当前上下文：C 端是买家/卖家，管理端是管理员，无登录态（定时任务）记 SYSTEM
        try
        {
            log.setOperatorId(SecurityUtils.getUserId());
            log.setOperatorName(SecurityUtils.getUsername());
        }
        catch (Exception e)
        {
            log.setOperatorName(IdleOrderStatusLog.ROLE_SYSTEM);
        }
        log.setOperatorRole(resolveRole(order));
        return idleOrderStatusLogMapper.insertIdleOrderStatusLog(log);
    }

    /**
     * 判定操作人角色：先看是不是买家/卖家，都不是则视为管理端操作
     */
    private String resolveRole(IdleOrder order)
    {
        Long userId;
        try
        {
            userId = SecurityUtils.getUserId();
        }
        catch (Exception e)
        {
            return IdleOrderStatusLog.ROLE_SYSTEM;
        }
        if (userId.equals(order.getBuyerId()))
        {
            return IdleOrderStatusLog.ROLE_BUYER;
        }
        if (userId.equals(order.getSellerId()))
        {
            return IdleOrderStatusLog.ROLE_SELLER;
        }
        return IdleOrderStatusLog.ROLE_ADMIN;
    }
}
