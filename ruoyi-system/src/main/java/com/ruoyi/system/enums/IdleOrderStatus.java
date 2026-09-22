package com.ruoyi.system.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.ruoyi.common.exception.ServiceException;

/**
 * 闲置订单状态与合法流转
 *
 * <p>状态取值与数据库 {@code idle_order.order_status} 一致（VARCHAR(2)）：</p>
 * <pre>
 *   0 待确认  买家下单后
 *   1 交易中  卖家确认接单后
 *   2 已完成  买家确认收货后（终态）
 *   3 已取消  买家或卖家取消后（终态）
 * </pre>
 *
 * <p>合法流转（这张表就是这个类的核心，业务代码只依赖它，不再散落 if 判断）：</p>
 * <pre>
 *   0 → 1   卖家确认接单
 *   0 → 3   买家/卖家取消
 *   1 → 2   买家确认收货
 *   1 → 3   买家/卖家取消
 * </pre>
 *
 * @author ruoyi
 */
public enum IdleOrderStatus
{
    /** 待确认 */
    PENDING_CONFIRM("0", "待确认"),
    /** 交易中 */
    TRADING("1", "交易中"),
    /** 已完成（终态） */
    FINISHED("2", "已完成"),
    /** 已取消（终态） */
    CANCELED("3", "已取消");

    private final String code;
    private final String label;

    IdleOrderStatus(String code, String label)
    {
        this.code = code;
        this.label = label;
    }

    public String getCode()
    {
        return code;
    }

    public String getLabel()
    {
        return label;
    }

    /** 合法流转表：当前状态 -> 允许迁移到的状态集合 */
    private static final Map<String, Set<IdleOrderStatus>> TRANSITIONS;

    static
    {
        Map<String, Set<IdleOrderStatus>> map = new HashMap<>(4);
        map.put(PENDING_CONFIRM.code, EnumSet.of(TRADING, CANCELED));
        map.put(TRADING.code, EnumSet.of(FINISHED, CANCELED));
        map.put(FINISHED.code, EnumSet.noneOf(IdleOrderStatus.class));
        map.put(CANCELED.code, EnumSet.noneOf(IdleOrderStatus.class));
        TRANSITIONS = Collections.unmodifiableMap(map);
    }

    /**
     * 按状态码取枚举，非法码直接抛异常（宁可报错也不要静默按未知状态处理）
     *
     * @param code 状态码
     * @return 状态枚举
     */
    public static IdleOrderStatus of(String code)
    {
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ServiceException("非法的订单状态：" + code));
    }

    /**
     * 判断能否从 fromCode 流转到 to
     *
     * @param fromCode 当前状态码
     * @param to       目标状态
     * @return 是否允许
     */
    public static boolean canTransfer(String fromCode, IdleOrderStatus to)
    {
        Set<IdleOrderStatus> allowed = TRANSITIONS.get(fromCode);
        return allowed != null && allowed.contains(to);
    }

    /**
     * 某状态下的下一步可选状态，用于给前端渲染"只能选合法状态"的下拉框
     *
     * @param fromCode 当前状态码
     * @return 可选状态集合（保持声明顺序）
     */
    public static Set<IdleOrderStatus> nextOf(String fromCode)
    {
        Set<IdleOrderStatus> allowed = TRANSITIONS.get(fromCode);
        if (allowed == null)
        {
            return Collections.emptySet();
        }
        Set<IdleOrderStatus> ordered = new LinkedHashSet<>();
        for (IdleOrderStatus status : values())
        {
            if (allowed.contains(status))
            {
                ordered.add(status);
            }
        }
        return ordered;
    }

    /** 是否终态（终态订单不能再被流转） */
    public boolean isFinal()
    {
        Set<IdleOrderStatus> allowed = TRANSITIONS.get(code);
        return allowed == null || allowed.isEmpty();
    }
}
