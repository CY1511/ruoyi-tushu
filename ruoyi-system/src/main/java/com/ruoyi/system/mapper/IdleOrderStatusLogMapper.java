package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.IdleOrderStatusLog;

/**
 * 订单状态流转日志Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface IdleOrderStatusLogMapper
{
    /**
     * 查询某订单的流转记录（按时间正序，便于还原时间线）
     *
     * @param orderId 订单ID
     * @return 流转记录集合
     */
    public List<IdleOrderStatusLog> selectStatusLogByOrderId(Long orderId);

    /**
     * 新增流转记录
     *
     * @param log 流转记录
     * @return 结果
     */
    public int insertIdleOrderStatusLog(IdleOrderStatusLog log);

    /**
     * 按订单批量删除流转记录（订单删除时一并清理）
     *
     * @param orderIds 订单ID集合
     * @return 结果
     */
    public int deleteIdleOrderStatusLogByOrderIds(@Param("orderIds") Long[] orderIds);
}
