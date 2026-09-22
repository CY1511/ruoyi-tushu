package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单状态流转日志对象 idle_order_status_log
 *
 * <p>订单表只存"当前状态"，被改掉就没有痕迹。这张表回答的是
 * "谁、在什么时间、把订单从什么状态改成了什么状态"，
 * 每次合法流转落一条，与订单更新在同一个事务里。</p>
 *
 * @author ruoyi
 */
public class IdleOrderStatusLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 操作角色：买家 */
    public static final String ROLE_BUYER = "BUYER";
    /** 操作角色：卖家 */
    public static final String ROLE_SELLER = "SELLER";
    /** 操作角色：管理端 */
    public static final String ROLE_ADMIN = "ADMIN";
    /** 操作角色：系统（定时任务等） */
    public static final String ROLE_SYSTEM = "SYSTEM";

    /** 日志ID */
    private Long logId;

    /** 订单ID */
    private Long orderId;

    /** 订单编号（冗余，便于排查） */
    private String orderNo;

    /** 变更前状态 */
    private String fromStatus;

    /** 变更后状态 */
    private String toStatus;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人名称 */
    private String operatorName;

    /** 操作人角色 */
    private String operatorRole;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getFromStatus()
    {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus)
    {
        this.fromStatus = fromStatus;
    }

    public String getToStatus()
    {
        return toStatus;
    }

    public void setToStatus(String toStatus)
    {
        this.toStatus = toStatus;
    }

    public Long getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorId(Long operatorId)
    {
        this.operatorId = operatorId;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    public String getOperatorRole()
    {
        return operatorRole;
    }

    public void setOperatorRole(String operatorRole)
    {
        this.operatorRole = operatorRole;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("logId", getLogId())
                .append("orderId", getOrderId())
                .append("orderNo", getOrderNo())
                .append("fromStatus", getFromStatus())
                .append("toStatus", getToStatus())
                .append("operatorId", getOperatorId())
                .append("operatorName", getOperatorName())
                .append("operatorRole", getOperatorRole())
                .append("remark", getRemark())
                .append("createTime", getCreateTime())
                .toString();
    }
}
